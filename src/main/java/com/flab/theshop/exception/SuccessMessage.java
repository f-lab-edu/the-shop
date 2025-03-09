package com.flab.theshop.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum SuccessMessage {

    AVAILABLE_USERID("사용 가능한 사용자 아이디입니다."),
    SIGNUP_OK("회원가입 성공"),
    SIGNIN_OK("로그인 성공"),
    SIGNOUT_OK("로그아웃 성공"),
    ;

    private final String message;
}
