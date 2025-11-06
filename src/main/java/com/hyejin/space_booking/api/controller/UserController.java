package com.hyejin.space_booking.api.controller;

import com.hyejin.space_booking.api.ApiResponse;
import com.hyejin.space_booking.api.request.BasicLoginRequest;
import com.hyejin.space_booking.api.request.SignupBasicRequest;
import com.hyejin.space_booking.api.response.LoginResponse;
import com.hyejin.space_booking.api.response.UserInfoResponse;
import com.hyejin.space_booking.entity.User;
import com.hyejin.space_booking.entity.UserSns;
import com.hyejin.space_booking.service.JwtService;
import com.hyejin.space_booking.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;
    private final JwtService jwtService;

    /**
     * 일반 회원가입
     */
    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<User>> signup(@Valid @RequestBody SignupBasicRequest req) {
        User user = userService.signup(req);
        return ResponseEntity.ok(ApiResponse.success(user));
    }

    /**
     * 일반 로그인
     */
    @PostMapping("/basic-login")
    public ResponseEntity<ApiResponse<LoginResponse>> basicLogin(@Valid @RequestBody BasicLoginRequest req) {
        LoginResponse user = userService.basicLogin(req);
        return ResponseEntity.ok(ApiResponse.success(user));
    }

    /**
     * 카카오로그인
     * 1)카카오로 리다이렉트
     */
    @GetMapping("/kakao-login")
    public ResponseEntity<ApiResponse<Void>> kakaoLogin(HttpServletResponse response, HttpSession session) throws IOException {
        userService.kakaoLogin(response, session);
        return ResponseEntity.ok(ApiResponse.success());
    }

    /**
     * 카카오로그인
     * 2)응답값
     */
    @GetMapping("/kakao-login-callback")
    public ResponseEntity<?> kakaoLoginCallback(
                @RequestParam String code,
                @RequestParam(required = false) String state,
                HttpServletRequest request) {
        return userService.handleKakaoCallback(code, state, request);
    }

    /**
     * 내정보 조회
     */
    @PostMapping("/get-myinfo")
    public ResponseEntity<ApiResponse<UserInfoResponse>> getMyInfo(HttpServletRequest request) {
        Long userKey = jwtService.extractUserKey(request); // JWT에서 유저 식별
        UserInfoResponse resp = userService.getUserInfo(userKey);
        return ResponseEntity.ok(ApiResponse.success(resp));
    }

}
