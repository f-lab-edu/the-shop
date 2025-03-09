package com.flab.theshop.exception.member;

import lombok.Getter;

@Getter
public class MemberTaskException extends RuntimeException {

    private final int code;
    private final String message;

    public MemberTaskException(int code, String message) {
        super(message);
        this.code = code;
        this.message = message;
    }

}
