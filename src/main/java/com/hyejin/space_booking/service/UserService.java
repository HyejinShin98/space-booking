package com.hyejin.space_booking.service;

import com.hyejin.space_booking.api.ApiResponse;
import com.hyejin.space_booking.api.request.BasicLoginRequest;
import com.hyejin.space_booking.api.request.SignupBasicRequest;
import com.hyejin.space_booking.api.response.LoginResponse;
import com.hyejin.space_booking.api.response.UserInfoResponse;
import com.hyejin.space_booking.common.ApiException;
import com.hyejin.space_booking.common.ErrorCode;
import com.hyejin.space_booking.entity.*;
import com.hyejin.space_booking.repository.UserRepository;
import com.hyejin.space_booking.repository.UserSnsRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.*;

import static com.hyejin.space_booking.util.StringUtils.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    private final UserRepository userRepository;
    private final UserSnsRepository userSnsRepository;
    private final PasswordEncoder passwordEncoder;
    private final KakaoService kakaoService;
    private final JwtService jwtService;

    /**
     * 일반 회원가입
     */
    @Transactional
    public User signup(SignupBasicRequest req) {
        final String userId = req.userId().trim();
        final String email  = req.email() == null ? null : req.email().trim().toLowerCase();

        // 1) 아이디 중복체크
        if (userRepository.existsByUserId(userId))
            throw new ApiException(ErrorCode.DUPLICATE_USER_ID);

        // 2) 이메일 소셜 연동 존재 (활성만 볼 건지 비활성 포함할 건지는 정책대로)
        userRepository.findActiveWithSnsByEmail(email).ifPresent(user -> {
            throw new ApiException(ErrorCode.SOCIAL_ACCOUNT_EXISTS);
        });

        // 3) 로컬 계정 이메일 중복
        if (userRepository.existsByEmail(email))
            throw new ApiException(ErrorCode.DUPLICATE_EMAIL);

        // 4) 생성
        User user = new User();
        user.setUserId(userId);
        user.setUserPw(passwordEncoder.encode(req.userPw()));
        user.setEmail(email);
        user.setName(req.name());
        user.setBirthDate(req.birthDate());
        user.setPhoneNum(req.phoneNum());
        user.setUseYn("Y");
        // user.setEmailVerified(false);
        return userRepository.save(user);
    }

    /**
     * 일반 로그인
     *
     */
    public LoginResponse basicLogin(BasicLoginRequest req) {
        // 1) 유저 조회(활성만)
        User user = userRepository.findUser(req.userId(), "Y")
                .orElseThrow(() -> new ApiException(ErrorCode.USER_NOT_FOUND));

        // 2) 유저 소셜연동여부 조회
        if (user.getUserSns() != null) {
            throw new ApiException(ErrorCode.SOCIAL_ACCOUNT_LOGIN_REQ);
        }
        
        // 2) 비밀번호 검증 (raw vs encoded)
        if (!passwordEncoder.matches(req.userPw(), user.getUserPw())) {
            throw new ApiException(ErrorCode.PW_INVALID_CREDENTIAL);
        }

        // 3) JWT 발급
        return issueLoginResponse(user);
    }

    /**
     * 1) 카카오 로그인
     * 카카오로 리다이렉트
     */
    public void kakaoLogin(HttpServletResponse res, HttpSession session) throws IOException {
        kakaoService.kakaoLogin(res, session);
    }

    /**
     * 카카오 로그인 콜백처리 + 응답값 반환 (JSON)
     */
    public ResponseEntity<?> handleKakaoCallback(String code, String state, HttpServletRequest request) {
        var session = request.getSession(false);
        if (session == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error","no_session"));

        String saved = (String) session.getAttribute("OAUTH_STATE");
        if (saved == null || !saved.equals(state)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error","invalid_state"));
        }
        // 1회성 소거 + 세션 아이디 교체(세션 고정 방지) 필수
        session.removeAttribute("OAUTH_STATE");
        request.changeSessionId();

        // 2) 토큰/프로필
        KakaoToken token = kakaoService.exchangeCode(code);
        KakaoProfile profile = kakaoService.getProfile(token.accessToken());

        // 3) 유저정보 업서트
        User user = upsertFromKakao(profile, token);

        // 4) 공통 JWT 발급 + 응답 DTO 조립
        LoginResponse body = issueLoginResponse(user);

        // 5) 헤더에 access 담고, 바디는 표준 ApiResponse로
        return ResponseEntity.ok()
                .header(HttpHeaders.AUTHORIZATION, body.tokenType() + " " + body.accessToken())
                .body(ApiResponse.success("로그인 성공", body));
    }

    /**
     * 카카오 프로필/토큰 기반 업서트
     * 1) provider + providerUserId 매칭
     * 2) 없으면 email로 기존 유저 매칭 후 소셜 연동
     * 3) 그것도 없으면 신규 생성
     */
    @Transactional
    public User upsertFromKakao(KakaoProfile profile, KakaoToken token) {
        final String provider = "KAKAO";
        final String providerUid = profile.providerUserId();
        final String email = safeNull(profile.email());
        final LocalDateTime now = LocalDateTime.now();

        // 1) provider + providerUserId로 기존 연동 조회
        Optional<UserSns> optSns = userSnsRepository.findByProviderAndProviderUserId(provider, providerUid);

        if (optSns.isPresent()) {
            // 기존 연동 있음 -> 업데이트
            UserSns sns = optSns.get();
            User user = sns.getUser();

            // 토큰/프로필 최신화
            sns.setAccessToken(coalesce(token.accessToken(), sns.getAccessToken()));
            sns.setRefreshToken(coalesce(token.refreshToken(), sns.getRefreshToken()));
            sns.setNickname(coalesce(profile.nickname(), sns.getNickname()));
            sns.setProfileImageUrl(coalesce(profile.profileImageUrl(), sns.getProfileImageUrl()));

            if (isNotBlank(email)) user.setEmail(email);
            user.setLastLoginDate(now);

            userSnsRepository.save(sns);
            return userRepository.save(user);
        }

        // 2) 연동 없음: 이메일로 기존 유저 매칭
        User user = (isNotBlank(email)) ? userRepository.findByEmail(email).orElse(null) : null;

        if (user == null) {
            // 3) 신규유저
            user = new User();
            user.setUserId("KAKAO_" + providerUid);
            user.setEmail(email);
            user.setFirstLoginDate(now);
            user.setLastLoginDate(now);
            user.setUseYn("Y");
            user = userRepository.save(user);
        } else {
            user.setLastLoginDate(now);
            user = userRepository.save(user);
        }

        // 4) UserSns 신규 생성 후 1:1 연결
        UserSns sns = new UserSns();
        sns.setUser(user);
        sns.setProvider(provider);
        sns.setProviderUserId(providerUid);
        sns.setNickname(fallbackNickname(profile.nickname(), providerUid));
        sns.setProfileImageUrl(profile.profileImageUrl());
        sns.setAccessToken(token.accessToken());
        sns.setRefreshToken(token.refreshToken());
        sns = userSnsRepository.save(sns);

        user.setUserSns(sns);
        return user;
    }

    // 공통: JWT 발급 + 응답
    private LoginResponse issueLoginResponse(User user) {
        UserSns sns = user.getUserSns();

        String provider = sns != null && sns.getProvider() != null ? sns.getProvider() : "LOCAL";
        String providerUserId = sns != null ? sns.getProviderUserId() : null;

        JwtPair jwt = jwtService.issue(
                user.getUserKey(),
                provider,
                providerUserId
        );

        return LoginResponse.of(
                jwt.accessToken(),
                jwt.refreshToken(),
                "Bearer",
                jwt.accessTokenTtlSeconds(),
                new UserInfoResponse(user, sns));
    }


    /**
     * 내 정보 조회
     */
    public UserInfoResponse getUserInfo(Long userKey) {
        if (userKey == null) throw new ApiException(ErrorCode.USER_NOT_FOUND);

        User user = userRepository.findUserByKey(userKey)
                .orElseThrow(() -> new ApiException(ErrorCode.USER_NOT_FOUND));

        return new UserInfoResponse(user, user.getUserSns());
    }
}


