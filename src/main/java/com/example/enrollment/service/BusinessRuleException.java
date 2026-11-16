package com.example.enrollment.service;

import com.example.enrollment.rules.ErrorCode;

/** 규칙 위반으로 요청을 거절할 때 던진다. 컨트롤러 조언이 {code, message} 응답으로 바꾼다. */
public class BusinessRuleException extends RuntimeException {

    private final ErrorCode errorCode;

    public BusinessRuleException(ErrorCode errorCode) {
        super(errorCode.name());
        this.errorCode = errorCode;
    }

    /** Optional.ifPresent(BusinessRuleException::throwFor) 형태로 쓰기 위한 도우미. */
    public static void throwFor(ErrorCode errorCode) {
        throw new BusinessRuleException(errorCode);
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }
}
