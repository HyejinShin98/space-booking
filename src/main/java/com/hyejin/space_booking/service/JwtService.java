package com.hyejin.space_booking.service;

import com.hyejin.space_booking.common.ApiException;
import com.hyejin.space_booking.common.ErrorCode;
import com.hyejin.space_booking.entity.JwtPair;
import com.hyejin.space_booking.exception.UnauthorizedException;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.time.Clock;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;

@Service
public class JwtService {
    private final Key key;
    private final long ACCESS_TOKEN_TTL;   // seconds
    private final long REFRESH_TOKEN_TTL;  // seconds
    private final Clock clock;             // 주입해서 테스트 가능하게

    public JwtService(
            @Value("${jwt.secret}") String secretBase64,
            @Value("${jwt.access-ttl}") long accessTokenTtlSeconds,
            @Value("${jwt.refresh-ttl}") long refreshTokenTtlSeconds,
            Clock clock // @Bean 으로 등록하거나 Clock.systemUTC()
    ) {
        byte[] keyBytes = Decoders.BASE64.decode(secretBase64); // 최소 32 bytes
        this.key = Keys.hmacShaKeyFor(keyBytes);
        this.ACCESS_TOKEN_TTL = accessTokenTtlSeconds;
        this.REFRESH_TOKEN_TTL = refreshTokenTtlSeconds;
        this.clock = clock;
    }

    public JwtPair issue(Long userKey, String provider, String providerUserId) {
        Instant now = clock.instant();

        String accessToken = Jwts.builder()
                .setIssuer("space-booking")                // iss
                .setSubject(String.valueOf(userKey))       // sub
                .setAudience("space-booking-client")       // aud (선택)
                .setId(UUID.randomUUID().toString())       // jti
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(now.plusSeconds(ACCESS_TOKEN_TTL)))
                .claim("provider", provider)
                .claim("providerUserId", providerUserId)   // 필요 최소화 고민
                .signWith(key)                             // HS256 자동 선택
                .compact();

        String refreshToken = Jwts.builder()
                .setIssuer("space-booking")
                .setSubject(String.valueOf(userKey))
                .setAudience("space-booking-client")
                .setId(UUID.randomUUID().toString())       // RT는 DB에 저장해 로테이션/블랙리스트
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(now.plusSeconds(REFRESH_TOKEN_TTL)))
                .signWith(key)
                .compact();

        return new JwtPair(accessToken, refreshToken, ACCESS_TOKEN_TTL, REFRESH_TOKEN_TTL);
    }

    public Long extractUserKey(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
//        if (header == null) throw new UnauthorizedException("Authorization 헤더 없음");
        if (header == null) throw new ApiException(ErrorCode.LOGIN_DATA_NOT_FOUND);

        String[] parts = header.trim().split("\\s+");
        if (parts.length != 2 || !parts[0].equalsIgnoreCase("Bearer"))
            throw new UnauthorizedException("Authorization 스킴 오류");

        String token = parts[1];

        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .setAllowedClockSkewSeconds(120)   // 시계 오차 허용
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            return Long.parseLong(claims.getSubject());
        } catch (ExpiredJwtException e) {
            throw new UnauthorizedException("토큰 만료", e);
        } catch (SecurityException e) { // 서명 검증 실패 포함
            throw new UnauthorizedException("서명 검증 실패", e);
        } catch (MalformedJwtException | UnsupportedJwtException | IllegalArgumentException e) {
            throw new UnauthorizedException("토큰 형식 오류", e);
        }
    }
}