package com.flab.theshop.exception.member;

import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.*;

public enum MemberException {

    INVALID_PASSWORD(UNAUTHORIZED, "비밀번호가 일치하지 않습니다."),
    MEMBER_NOT_EXISTS(NOT_FOUND, "해당 회원을 찾을 수 없습니다."),
    DUPLICATE_USERID(CONFLICT, "이미 사용중인 아이디입니다."),
    ;

    private final MemberTaskException memberTaskException;

    MemberException(HttpStatus status, String message) {
        this.memberTaskException = new MemberTaskException(status.value(), message);
    }

    public MemberTaskException get() {
        return memberTaskException;
    }
}
