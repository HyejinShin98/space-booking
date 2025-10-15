package com.hyejin.space_booking.service;

import com.hyejin.space_booking.api.request.SignupBasicRequest;
import com.hyejin.space_booking.entity.KakaoProfile;
import com.hyejin.space_booking.entity.KakaoToken;
import com.hyejin.space_booking.entity.User;
import com.hyejin.space_booking.repository.UserRepository;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
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
public class KakaoService {

    @Value("${kakao.client_id}")
    private String clientId;

    @Value("${kakao.client_secret:}")     // 없을 수도 있으니 기본값 빈 문자열
    private String clientSecret;

    @Value("${kakao.redirect_uri}")
    private String redirectUri;

    private final RestClient client = RestClient.create();

    /** 1) 카카오로 리다이렉트 URL 만들어 리다이렉트 */
    public void kakaoLogin(HttpServletResponse res, HttpSession session) throws IOException {
        String state = UUID.randomUUID().toString();
        session.setAttribute("OAUTH_STATE", state);

        String scope = URLEncoder.encode("account_email profile_nickname profile_image", StandardCharsets.UTF_8);
        String url = "https://kauth.kakao.com/oauth/authorize"
                + "?response_type=code"
                + "&client_id=" + clientId
                + "&redirect_uri=" + URLEncoder.encode(redirectUri, StandardCharsets.UTF_8)
                + "&state=" + state
                + "&scope=" + scope;

        res.sendRedirect(url);
    }

    /** 2) 인가코드로 토큰 교환 */
    public KakaoToken exchangeCode(String code) {
        try {
            var form = new LinkedMultiValueMap<String, String>();
            form.add("grant_type", "authorization_code");
            form.add("client_id", clientId);
            if (clientSecret != null && !clientSecret.isBlank()) {
                form.add("client_secret", clientSecret);
            }
            form.add("redirect_uri", redirectUri);
            form.add("code", code);

            Map<?, ?> body = client.post()
                    .uri("https://kauth.kakao.com/oauth/token")
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .body(form)
                    .retrieve()
                    .onStatus(HttpStatusCode::isError, (req, res) -> {
                        throw new ResponseStatusException(res.getStatusCode(), "kakao token error");
                    })
                    .body(Map.class);

            String accessToken  = asString(body.get("access_token"));
            String refreshToken = asString(body.get("refresh_token"));
            String tokenType    = asString(body.get("token_type"));
            Long   expiresIn    = asLong(body.get("expires_in"));
            String scope        = asString(body.get("scope"));

            if (accessToken == null) throw new ResponseStatusException(org.springframework.http.HttpStatus.BAD_GATEWAY, "no access_token");

            return new KakaoToken(accessToken, refreshToken, tokenType, expiresIn, scope);

        } catch (RestClientException e) {
            throw new ResponseStatusException(org.springframework.http.HttpStatus.BAD_GATEWAY, "kakao token request failed");
        }
    }

    /** 3) 액세스 토큰으로 카카오 프로필 조회 */
    public KakaoProfile getProfile(String accessToken) {
        try {
            Map<?, ?> me = client.get()
                    .uri("https://kapi.kakao.com/v2/user/me")
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                    .retrieve()
                    .onStatus(HttpStatusCode::isError, (req, res) -> {
                        throw new ResponseStatusException(res.getStatusCode(), "kakao userinfo error");
                    })
                    .body(Map.class);

            Long id = asLong(me.get("id"));

            Map<?, ?> properties    = asMap(me.get("properties"));
            Map<?, ?> kakaoAccount  = asMap(me.get("kakao_account"));

            String nickname         = asString(properties.get("nickname"));
            String profileImageUrl  = asString(properties.get("profile_image"));
            String email            = asString(kakaoAccount.get("email"));
            Boolean emailVerified   = asBoolean(kakaoAccount.get("is_email_verified"));

            return new KakaoProfile(
                    id != null ? String.valueOf(id) : null,
                    nickname,
                    profileImageUrl,
                    email,
                    emailVerified != null ? emailVerified : false
            );

        } catch (RestClientException e) {
            throw new ResponseStatusException(org.springframework.http.HttpStatus.BAD_GATEWAY, "kakao userinfo request failed");
        }
    }

    // --------- helpers ---------
    @SuppressWarnings("unchecked")
    private Map<String, Object> asMap(Object o) {
        return (o instanceof Map<?,?> m) ? (Map<String, Object>) m : Map.of();
    }
    private String asString(Object o) { return o == null ? null : String.valueOf(o); }
    private Long asLong(Object o) {
        if (o == null) return null;
        if (o instanceof Number n) return n.longValue();
        try { return Long.parseLong(o.toString()); } catch (Exception e) { return null; }
    }
    private Boolean asBoolean(Object o) {
        if (o == null) return null;
        if (o instanceof Boolean b) return b;
        return Boolean.parseBoolean(o.toString());
    }

}
