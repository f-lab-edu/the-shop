package com.flab.theshop.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.*;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    /**
     * 400 Bad Request (잘못된 요청)
     */
    E400_INVALID(BAD_REQUEST, "잘못된 요청입니다."),
    E400_INVALID_ENCODING_ID(BAD_REQUEST, "잘못된 ID가 입력되었습니다"),


    /**
     * 401 UnAuthorized (인증 실패)
     */
    E401_UNAUTHORIZED(UNAUTHORIZED, "세션이 만료되었습니다. 다시 로그인 해주세요"),
    E401_INVALID_PASSWORD(UNAUTHORIZED, "비밀번호가 일치하지 않습니다."),

    /**
     * 403 Forbidden (권한 등의 이유로 허용하지 않는 요청)
     */
    E403_FORBIDDEN(FORBIDDEN, "허용하지 않는 요청입니다"),

    /**
     * 404 Not Found (존재하지 않는 리소스)
     */
    E404_NOT_EXISTS(NOT_FOUND, "존재하지 않습니다"),
    E404_MEMBER_NOT_EXISTS(NOT_FOUND, "해당 회원을 찾을 수 없습니다."),

    /**
     * 405 Method Not Allowed
     */
    E405_METHOD_NOT_ALLOWED(METHOD_NOT_ALLOWED, "허용되지 않은 HTTP 메소드입니다"),

    /**
     * 406 Not Acceptable
     */
    E406_NOT_ACCEPTABLE(NOT_ACCEPTABLE, "허용되지 않은 Content-Type 입니다"),

    /**
     * 409 Conflict (중복되는 리소스)
     */
    E409_DUPLICATE(CONFLICT, "처리할 수 없는 요청입니다."),
    E409_DUPLICATE_USERID(CONFLICT, "이미 사용중인 아이디입니다."),

    /**
     * 415 Unsupported Media Type
     */
    E415_UNSUPPORTED_MEDIA_TYPE(UNSUPPORTED_MEDIA_TYPE, "지원 하지 않는 MediaType 입니다/"),

    /**
     * 429 Too Many Requests (RateLimit)
     */
    E429_TOO_MANY_REQUESTS(TOO_MANY_REQUESTS, "일시적으로 많은 요청이 들어왔습니다\n잠시후 다시 이용해주세요"),

    /**
     * 500 Internal Server Exception (서버 내부 에러)
     */
    E500_INTERNAL_SERVER(INTERNAL_SERVER_ERROR, "예상치 못한 에러가 발생하였습니다\n잠시 후 다시 시도해주세요!"),

    /**
     * 501 Not Implemented (현재 지원하지 않는 요청)
     */
    E501_NOT_SUPPORTED(NOT_IMPLEMENTED, "지원하지 않는 요청입니다"),

    /**
     * 502 Bad Gateway (외부 시스템의 Bad Gateway)
     */
    E502_BAD_GATEWAY(BAD_GATEWAY, "일시적인 에러가 발생하였습니다\n잠시 후 다시 시도해주세요!"),

    /**
     * 503 Service UnAvailable
     */
    E503_SERVICE_UNAVAILABLE(SERVICE_UNAVAILABLE, "해당 기능은 현재 사용할 수 없습니다"),
    ;

    private final HttpStatus status;
    private final String message;
}
