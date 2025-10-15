package com.hyejin.space_booking.api.controller;

import com.hyejin.space_booking.api.ApiResponse;
import com.hyejin.space_booking.api.request.SignupBasicRequest;
import com.hyejin.space_booking.entity.User;
import com.hyejin.space_booking.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.catalina.connector.Request;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;

    /**
     * 일반 회원가입
     */
    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<User>> signup(@Valid @RequestBody SignupBasicRequest req) {
        User user = userService.signup(req);
        return ResponseEntity.ok(ApiResponse.success("회원가입 성공", user));
    }

    /**
     * 카카오로그인
     * 1)카카오로 리다이렉트
     */
    @GetMapping("/kakaoLogin")
    public ResponseEntity<ApiResponse<User>> kakaoLogin(HttpServletResponse response, HttpSession session) throws IOException {
        userService.kakaoLogin(response, session);
        return ResponseEntity.ok(ApiResponse.success("카카오 로그인 리다이렉트"));
    }

    /**
     * 카카오로그인
     * 2)응답값
     */
    @GetMapping("/kakaoLoginCallback")
    public ResponseEntity<?> kakaoLoginCallback(
                @RequestParam String code,
                @RequestParam(required = false) String state,
                HttpServletRequest request) {
        return userService.handleKakaoCallback(code, state, request);
    }

}
