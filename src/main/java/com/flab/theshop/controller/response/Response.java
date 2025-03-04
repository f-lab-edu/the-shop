package com.flab.theshop.controller.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.flab.theshop.exception.ErrorCode;
import com.flab.theshop.exception.FieldError;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.*;
import static org.springframework.http.HttpStatus.*;

@AllArgsConstructor
@Getter
public class Response<T> {

    private int code;

    private String message;

    @JsonInclude(NON_NULL) //null이면 응답에서 제외
    private T result;

    @JsonInclude(NON_NULL)
    private List<FieldError> errors;

    public static Response<Void> success() {
        return new Response<>(OK.value(), "SUCCESS", null, null);
    }

    public static Response<Void> success(String message) {
        return new Response<>(OK.value(), message, null, null);
    }

    public static <T> Response<T> success(T result) {
        return new Response<>(OK.value(), "SUCCESS", result, null);
    }

    public static <T> Response<T> success(String message, T result) {
        return new Response<>(OK.value(), message, result, null);
    }

    public static Response<Void> error(int code, String message) {
        return new Response<>(code, message, null, null);
    }

    public static Response<Void> error(ErrorCode errorCode) {
        return new Response<>(errorCode.getStatus().value(), errorCode.getMessage(), null, null);
    }

    public static Response<Void> error(ErrorCode errorCode, List<FieldError> errors) {
        return new Response<>(errorCode.getStatus().value(), errorCode.getMessage(), null, errors);
    }
}
