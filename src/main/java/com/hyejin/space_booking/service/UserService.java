package com.hyejin.space_booking.service;

import com.hyejin.space_booking.api.request.SignupBasicRequest;
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
        if (userRepository.existsByUserId(userId)) {
            throw new ApiException(ErrorCode.DUPLICATE_USER_ID);
        }

        // 2) 이메일 소셜 연동 존재 (활성만 볼 건지 비활성 포함할 건지는 정책대로)
        userRepository.findActiveWithSnsByEmail(email).ifPresent(user -> {
            throw new ApiException(ErrorCode.SOCIAL_ACCOUNT_EXISTS);
        });

        // 3) 로컬 계정 이메일 중복
        if (userRepository.existsByEmail(email)) {
            throw new ApiException(ErrorCode.DUPLICATE_EMAIL);
        }

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

        // 4) JWT 발급
        JwtPair jwt = jwtService.issue(user.getUserKey(), "KAKAO", profile.providerUserId());

        // 5) 응답
        return ResponseEntity.ok()
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwt.accessToken())
                .body(Map.of(
                        "message", "로그인 성공",
                        "userKey", user.getUserKey(),
                        "userId", user.getUserId(),
                        "provider", profile.providerUserId(),
                        "nickname", profile.nickname(),
                        "expiresIn", jwt.accessTokenTtlSeconds()
                ));
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

}


