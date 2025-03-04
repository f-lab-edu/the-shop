package com.flab.theshop.controller.response;

import com.flab.theshop.exception.ErrorCode;
import lombok.AllArgsConstructor;

import static org.springframework.http.HttpStatus.*;

@AllArgsConstructor
public class Response<T> {

    private int code;
    private String message;
    private T result;

    public static <T> Response<T> success() {
        return new Response<T>(OK.value(), "SUCCESS", null);
    }

    public static <T> Response<T> success(T result) {
        return new Response<T>(OK.value(), "SUCCESS", result);
    }

    public static Response<Void> error(ErrorCode errorCode) {
        return new Response<Void>(errorCode.getStatus().value(), errorCode.getMessage(), null);
    }
}
