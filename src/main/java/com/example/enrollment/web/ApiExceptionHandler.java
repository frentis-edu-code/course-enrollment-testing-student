package com.example.enrollment.web;

import com.example.enrollment.rules.ErrorCode;
import com.example.enrollment.service.BusinessRuleException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

/**
 * 모든 오류를 {code, message} 형태로 바꾼다. 스택 트레이스나 내부 예외 메시지는 응답에 넣지 않는다.
 */
@RestControllerAdvice
public class ApiExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(ApiExceptionHandler.class);

    @ExceptionHandler(BusinessRuleException.class)
    ResponseEntity<ErrorResponse> businessRule(BusinessRuleException e) {
        return respond(e.getErrorCode());
    }

    /** 본문이 없거나 JSON 문법·타입이 맞지 않음. */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    ResponseEntity<ErrorResponse> unreadable(HttpMessageNotReadableException e) {
        return respond(ErrorCode.INVALID_REQUEST);
    }

    /** 경로·쿼리 값의 타입이 맞지 않음(예: /api/enrollments/abc/cancel). */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    ResponseEntity<ErrorResponse> typeMismatch(MethodArgumentTypeMismatchException e) {
        return respond(ErrorCode.INVALID_REQUEST);
    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    ResponseEntity<ErrorResponse> unsupportedMediaType(HttpMediaTypeNotSupportedException e) {
        return respond(ErrorCode.UNSUPPORTED_MEDIA_TYPE);
    }

    @ExceptionHandler(HttpMediaTypeNotAcceptableException.class)
    ResponseEntity<ErrorResponse> notAcceptable(HttpMediaTypeNotAcceptableException e) {
        return respond(ErrorCode.INVALID_REQUEST);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    ResponseEntity<ErrorResponse> methodNotAllowed(HttpRequestMethodNotSupportedException e) {
        return respond(ErrorCode.METHOD_NOT_ALLOWED);
    }

    @ExceptionHandler(NoResourceFoundException.class)
    ResponseEntity<ErrorResponse> notFound(NoResourceFoundException e) {
        return respond(ErrorCode.NOT_FOUND);
    }

    @ExceptionHandler(Exception.class)
    ResponseEntity<ErrorResponse> unexpected(Exception e) {
        log.error("처리하지 못한 오류", e);
        return respond(ErrorCode.INTERNAL_ERROR);
    }

    private static ResponseEntity<ErrorResponse> respond(ErrorCode code) {
        return ResponseEntity.status(code.httpStatus()).body(ErrorResponse.of(code));
    }
}
