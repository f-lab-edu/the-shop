package com.flab.theshop.exception;

import com.flab.theshop.controller.response.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import static com.flab.theshop.exception.ErrorCode.*;

@Slf4j
@RestControllerAdvice
public class GlobalControllerAdvice {

    @ExceptionHandler(MemberTaskException.class)
    public ResponseEntity<?> handleMemberTaskException(MemberTaskException e) {
        log.error("오류 발생: ", e);
        return ResponseEntity.status(e.getErrorCode().getStatus())
                .body(Response.error(e.getErrorCode()));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<?> handleIllegalArgumentException(IllegalArgumentException e) {
        log.error("잘못된 요청입니다: " + e);
        return ResponseEntity.status(E400_INVALID.getStatus())
                .body(Response.error(E400_INVALID));
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<?> handleIllegalStateException(IllegalStateException e) {
        log.error("처리할 수 없는 요청입니다: " + e);
        return ResponseEntity.status(E409_DUPLICATE.getStatus())
                .body(Response.error(E409_DUPLICATE));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleException(Exception e) {
        log.error("서버 오류가 발생했습니다: " + e);
        return ResponseEntity.status(E500_INTERNAL_SERVER.getStatus())
                .body(Response.error(E500_INTERNAL_SERVER));
    }
}

