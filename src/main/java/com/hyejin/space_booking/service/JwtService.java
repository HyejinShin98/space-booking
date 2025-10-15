package com.hyejin.space_booking.service;

import com.hyejin.space_booking.entity.JwtPair;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.time.Instant;
import java.util.Date;

@Service
public class JwtService {
    private final Key key;
    private final long ACCESS_TOKEN_TTL;
    private final long REFRESH_TOKEN_TTL;

    public JwtService(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.access-ttl}") long accessTokenTtl,
            @Value("${jwt.refresh-ttl}") long refreshTokenTtl
    ) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes());
        this.ACCESS_TOKEN_TTL = accessTokenTtl;
        this.REFRESH_TOKEN_TTL = refreshTokenTtl;
    }

    /**
     * JWT access/refresh 토큰 발급
     * @param userKey 유저의 PK
     * @param provider 로그인 제공자 (e.g. "KAKAO")
     * @param providerUserId 카카오 사용자 id
     */
    public JwtPair issue(Long userKey, String provider, String providerUserId) {
        Instant now = Instant.now();

        // 1) Access Token
        String accessToken = Jwts.builder()
                .setSubject(String.valueOf(userKey))
                .claim("provider", provider)
                .claim("providerUserId", providerUserId)
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(now.plusSeconds(ACCESS_TOKEN_TTL)))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();

        // 2) Refresh Token
        String refreshToken = Jwts.builder()
                .setSubject(String.valueOf(userKey))
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(now.plusSeconds(REFRESH_TOKEN_TTL)))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();

        return new JwtPair(accessToken, refreshToken, ACCESS_TOKEN_TTL, REFRESH_TOKEN_TTL);
    }
}