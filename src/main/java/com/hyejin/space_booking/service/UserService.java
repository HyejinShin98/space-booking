package com.hyejin.space_booking.service;

import com.hyejin.space_booking.api.request.SignupBasicRequest;
import com.hyejin.space_booking.entity.User;
import com.hyejin.space_booking.repository.UserRepository;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.RestClient;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    // 카카오 로그인 연동시 사용
    @Value("${kakao.client_id}")
    private String KAKAO_LOGIN_CLIENT_ID;
    @Value("${kakao.redirect_uri}")
    private String KAKAO_LOGIN_REDIRECT_URI;
    private final RestClient client = RestClient.create();

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
     * 카카오 로그인
     * 1) 카카오로 리다이렉트
     */
    public void kakaoLogin(HttpServletResponse res, HttpSession session) throws IOException {
        String state = UUID.randomUUID().toString();
        session.setAttribute("OAUTH_STATE", state); // CSRF 방지

        String scope = URLEncoder.encode("account_email profile_nickname profile_image", StandardCharsets.UTF_8);
        String url = "https://kauth.kakao.com/oauth/authorize"
                + "?response_type=code"
                + "&client_id=" + KAKAO_LOGIN_CLIENT_ID
                + "&redirect_uri=" + KAKAO_LOGIN_REDIRECT_URI
                + "&state=" + state
                + "&scope=" + scope;
        res.sendRedirect(url);
    }

    /**
     * 카카오 로그인 - 응답값
     */
    public Map<String, Object> kakaoLoginCallback(String code, String state, HttpSession session) {

        if (!Objects.equals(state, session.getAttribute("OAUTH_STATE"))) {
            throw new IllegalArgumentException("Invalid state");
        }

        // 토큰 교환
        var form = new LinkedMultiValueMap<String, String>();
        form.add("grant_type", "authorization_code");
        form.add("client_id", KAKAO_LOGIN_CLIENT_ID);
        form.add("redirect_uri", KAKAO_LOGIN_REDIRECT_URI);
        form.add("code", code);

        //
        Map<String, Object> token = client.post()
                .uri("https://kauth.kakao.com/oauth/token")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(form)
                .retrieve()
                .body(Map.class);

        String accessToken = (String) token.get("access_token");

        // 유저 정보 조회
        Map<String, Object> me = client.get()
                .uri("https://kapi.kakao.com/v2/user/me")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                .retrieve()
                .body(Map.class);

        // DB upsert 혹은 로그인 처리 로직 (임시로 세션에 저장)
        session.setAttribute("LOGIN_USER_ID", String.valueOf(me.get("id")));

        // 응답용 Map 생성
        Map<String, Object> response = new HashMap<>();
        response.put("message", "로그인 성공");
        response.put("kakaoId", me.get("id"));
        response.put("nickname", ((Map) me.get("properties")).get("nickname"));
        response.put("email", ((Map) me.get("kakao_account")).get("email"));
        return response;
    }

}
