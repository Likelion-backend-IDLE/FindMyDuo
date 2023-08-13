package com.idle.fmd.domain.user.controller;

import com.idle.fmd.domain.user.dto.*;
import com.idle.fmd.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    @PostMapping("/signup")
    public void signup(@Valid @RequestBody SignupDto dto){
        userService.signup(dto);
    }

    // 로그인 유저 토큰 발급
    @PostMapping("/login")
    public UserLoginResponseDto login(@RequestBody UserLoginRequestDto dto) {
        return userService.loginUser(dto);
    }

    // OAuth 로그인 유저 토큰 발급
    @GetMapping("/oauth")
    public UserLoginResponseDto oauthLogin(@RequestParam("token") String token) {
        return new UserLoginResponseDto(token);

    }

    // 유저 정보 조회
    @GetMapping("/mypage")
    public UserMyPageResponseDto myPage(Authentication authentication) {
        String accountId = authentication.getName();
        UserMyPageResponseDto user = userService.profile(accountId);
        return user;
    }

    // 유저 정보 수정
    @PutMapping("/mypage")
    public UserMyPageRequestDto updateMyPage(
            @RequestBody UserMyPageRequestDto dto) {
        return userService.update(dto);
    }

    // 회원 탈퇴 (유저 정보 삭제)
    @DeleteMapping("/mypage")
    public void UserDelete(Authentication authentication) {
        String accountId = authentication.getName();
        userService.delete(accountId);
    }
}
