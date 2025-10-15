package com.hyejin.space_booking.service;

import com.hyejin.space_booking.api.request.SignupBasicRequest;
import com.hyejin.space_booking.entity.JwtPair;
import com.hyejin.space_booking.entity.KakaoProfile;
import com.hyejin.space_booking.entity.KakaoToken;
import com.hyejin.space_booking.entity.User;
import com.hyejin.space_booking.repository.UserRepository;
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
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final KakaoService kakaoService;
    private final JwtService jwtService;

    /**
     * 일반 회원가입
     */
    @Transactional
    public User signup(SignupBasicRequest req) {
        // 아이디 중복체크
        Optional<User> existsUser = userRepository.findUser(req.userId(), null);
        if (existsUser.isPresent()) {
            throw new IllegalArgumentException("이미 존재하는 아이디입니다.");
        }

        User user = new User();
        user.setUserId(req.userId());
        user.setUserPw(passwordEncoder.encode(req.userPw())); // 비밀번호 암호화
        user.setEmail(req.email());
        user.setName(req.name());
        user.setBirthDate(req.birthDate());
        user.setPhoneNum(req.phoneNum());
        user.setUseYn("Y");
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

        // 3) TODO: 업서트
//        User user = upsertFromKakao(profile, token);

        // 4) JWT 발급
//        JwtPair jwt = jwtService.issue(user.getId(), "KAKAO", profile.providerUserId());
        JwtPair jwt = jwtService.issue(1L, "KAKAO", profile.providerUserId());

        // 5) 응답
        return ResponseEntity.ok()
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwt.accessToken())
                .body(Map.of(
                        "message", "로그인 성공",
//                        "userId", user.getId(),
//                        "nickname", user.getRemarks(),
                        "userId", profile.providerUserId(),
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

    /*
    @Transactional
    public User upsertFromKakao(KakaoProfile profile, KakaoToken token) {
        final String provider = "KAKAO";
        final String providerUid = profile.providerUserId();
        final String email = safeNull(profile.email());

        // 1) provider + providerUserId로 조회
        User user = userRepository.findByProviderAndProviderUserId(provider, providerUid)
                .orElse(null);

        if (user == null && email != null && !email.isBlank()) {
            // 2) 이메일로 기존 계정 찾기(이전에 로컬 가입했거나 다른 소셜)
            user = userRepository.findByEmail(email).orElse(null);
        }

        LocalDateTime now = LocalDateTime.now();

        if (user == null) {
            // 3) 신규 생성
            user = new User();
            user.setProvider(provider);
            user.setProviderUserId(providerUid);

            // 로컬용 userId는 소셜 신규에선 비워도 되지만, 운영 로그/식별 편의로 임시값 생성 가능
            user.setUserId("kakao_" + providerUid);

            user.setEmail(email);
            user.setEmailVerified(profile.emailVerified());

            user.setNickname(fallbackNickname(profile.nickname(), providerUid));
            user.setProfileImageUrl(profile.profileImageUrl());

            user.setFirstLoginAt(now);
            user.setLastLoginAt(now);
            return userRepository.save(user);
        }

        // 기존 유저 업데이트(프로필/이메일 최신화)
        if (user.getProvider() == null) user.setProvider(provider);
        if (user.getProviderUserId() == null) user.setProviderUserId(providerUid);

        // 최신 값 덮어쓰기. 닉네임/이미지 비어있을 때만 갱신하고 싶으면 조건 걸면 됨.
        user.setNickname(coalesce(profile.nickname(), user.getNickname()));
        user.setProfileImageUrl(coalesce(profile.profileImageUrl(), user.getProfileImageUrl()));

        if (email != null && !email.isBlank()) {
            user.setEmail(email);
            user.setEmailVerified(Boolean.TRUE.equals(profile.emailVerified()));
        }

        user.setLastLoginAt(now);
        return userRepository.save(user);
    }
     */

    private String fallbackNickname(String nickname, String providerUid) {
        if (nickname != null && !nickname.isBlank()) return nickname;
        return "kakao_user_" + providerUid.substring(0, Math.min(8, providerUid.length()));
    }
    private String coalesce(String a, String b) { return (a != null && !a.isBlank()) ? a : b; }
    private String safeNull(String s) { return s == null ? null : s.trim(); }
}


