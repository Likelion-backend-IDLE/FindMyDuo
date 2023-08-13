package com.idle.fmd.domain.user.service;


import com.idle.fmd.domain.user.dto.*;
import com.idle.fmd.domain.user.entity.UserEntity;
import com.idle.fmd.domain.user.repo.UserRepository;
import com.idle.fmd.global.auth.jwt.JwtTokenUtils;
import com.idle.fmd.global.error.exception.BusinessException;
import com.idle.fmd.global.error.exception.BusinessExceptionCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.idle.fmd.domain.user.entity.CustomUserDetails;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final CustomUserDetailsManager manager;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenUtils jwtTokenUtils;
    private final UserRepository repository;

    // 회원가입 메서드
    public void signup(SignupDto dto){
        String password = dto.getPassword();
        String passwordCheck = dto.getPasswordCheck();

        // 비밀번호와 비밀번호 확인 데이터가 다르면 예외 발생
        if(!password.equals(passwordCheck))
            throw new BusinessException(BusinessExceptionCode.PASSWORD_CHECK_ERROR);

        // CustomUserDetailsManager 의 createUser 메서드를 호출해서 유저를 등록 ( UserDetails 객체 전달 필요 )
        manager.createUser(
                CustomUserDetails.builder()
                        .accountId(dto.getAccountId())
                        .email(dto.getEmail())
                        .nickname(dto.getNickname())
                        .password(passwordEncoder.encode(dto.getPassword()))
                        .build()
        );
    }

    public UserLoginResponseDto loginUser(UserLoginRequestDto dto) {

        log.info("로그인 : " + manager.userExists(dto.getAccountId()));

        if (!manager.userExists(dto.getAccountId())) {
            throw new BusinessException(BusinessExceptionCode.NOT_EXIST_USER_ERROR);
        }

        UserDetails userDetails = manager.loadUserByUsername(dto.getAccountId());

        if (!passwordEncoder.matches(dto.getPassword(), userDetails.getPassword())) {
            throw new BusinessException(BusinessExceptionCode.LOGIN_PASSWORD_CHECK_ERROR);
        }

        return new UserLoginResponseDto(jwtTokenUtils.generateToken(userDetails));
    }

    // 유저 조회 메소드
    public UserMyPageResponseDto profile(String accountId) {
        Optional<UserEntity> entity = repository.findByAccountId(accountId);
        if(entity.isPresent()) {
            return UserMyPageResponseDto.fromEntity(entity.get());
        } else throw new BusinessException(BusinessExceptionCode.NOT_EXIST_USER_ERROR);
    }

    // 유저 정보 수정 메소드
    public UserMyPageRequestDto update(UserMyPageRequestDto dto) {
        String password = dto.getPassword();
        String passwordCheck = dto.getPasswordCheck();

        // 비밀번호와 비밀번호 확인 데이터가 다르면 예외 발생 (회원가입에 사용한 에러 사용)
        if(!password.equals(passwordCheck))
            throw new BusinessException(BusinessExceptionCode.PASSWORD_CHECK_ERROR);

        CustomUserDetails updateUserDetails =
                CustomUserDetails.builder()
                        .email(dto.getEmail())
                        .nickname(dto.getNickname())
                        .password(passwordEncoder.encode(dto.getPassword()))
                        .build();

        // CustomUserDetailsManager 의 updateUser 메서드를 호출해서 유저를 등록 (UserDetails 객체 전달 필요)
        manager.updateUser(updateUserDetails, dto.getAccountId());

        return dto;
    }

    // User 삭제 메서드
    public void delete(String accountId) {
        if(!manager.userExists(accountId)) {
            throw new BusinessException(BusinessExceptionCode.NOT_EXIST_USER_ERROR);
        }

        // UserDetailsManager의 deleteUser 메소드를 호출하여 유저 정보 삭제
        manager.deleteUser(accountId);
    }
}
