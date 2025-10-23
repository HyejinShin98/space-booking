package com.hyejin.space_booking.service;

import com.hyejin.space_booking.common.ApiException;
import com.hyejin.space_booking.common.ErrorCode;
import com.hyejin.space_booking.entity.JwtPair;
import com.hyejin.space_booking.entity.User;
import com.hyejin.space_booking.exception.UnauthorizedException;
import com.hyejin.space_booking.repository.UserRepository;
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
    private final Clock clock;

    private final JwtParser jwtParser;     // 재사용
    private final UserRepository userRepository;

    public JwtService(
            @Value("${jwt.secret}") String secretBase64,
            @Value("${jwt.access-ttl}") long accessTokenTtlSec,
            @Value("${jwt.refresh-ttl}") long refreshTokenTtlSec,
            Clock clock,
            UserRepository userRepository
    ) {
        byte[] keyBytes = Decoders.BASE64.decode(secretBase64); // 최소 32 bytes 이상
        this.key = Keys.hmacShaKeyFor(keyBytes);
        this.ACCESS_TOKEN_TTL = accessTokenTtlSec;
        this.REFRESH_TOKEN_TTL = refreshTokenTtlSec;
        this.clock = clock;
        this.userRepository = userRepository;

        this.jwtParser = Jwts.parserBuilder()
                .setSigningKey(this.key)
                .setAllowedClockSkewSeconds(120) // 2분 오차 허용
                .build();
    }

    /** JWT 발급 (AT/RT) */
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

    /** Authorization 헤더 파싱 + JWT 검증 + User 조회 + 요청 범위 캐싱 */
    public User extractUser(HttpServletRequest request) {
        // 요청 범위 캐시
        Object cached = request.getAttribute("AUTH_USER");
        if (cached instanceof User u) return u;

        String token = resolveBearerToken(request);
        Claims claims = parseClaims(token);
        Long userKey = Long.parseLong(claims.getSubject());

        User user = userRepository.findUserByKey(userKey)
                .orElseThrow(() -> new ApiException(ErrorCode.USER_NOT_FOUND));

        request.setAttribute("AUTH_USER", user);
        return user;
    }

    /** 기존 시그니처가 필요하면 유지: userKey만 추출 */
    public Long extractUserKey(HttpServletRequest request) {
        String token = resolveBearerToken(request);
        Claims claims = parseClaims(token);
        return Long.parseLong(claims.getSubject());
    }

    /** AT 만료시간(초) 노출: 프론트 갱신 타이밍 계산용 */
    public long getAccessTtlSeconds() {
        return ACCESS_TOKEN_TTL;
    }

    // =============== 내부 유틸 ===============

    private String resolveBearerToken(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (header == null) throw new ApiException(ErrorCode.LOGIN_DATA_NOT_FOUND); // 401

        String[] parts = header.trim().split("\\s+");
        if (parts.length != 2 || !parts[0].equalsIgnoreCase("Bearer")) {
            throw new UnauthorizedException("Authorization 스킴 오류");
        }
        return parts[1];
    }

    private Claims parseClaims(String token) {
        try {
            return jwtParser.parseClaimsJws(token).getBody();
        } catch (ExpiredJwtException e) {
            throw new UnauthorizedException("토큰 만료", e);
        } catch (SecurityException e) {
            throw new UnauthorizedException("서명 검증 실패", e);
        } catch (MalformedJwtException | UnsupportedJwtException | IllegalArgumentException e) {
            throw new UnauthorizedException("토큰 형식 오류", e);
        }
    }
}