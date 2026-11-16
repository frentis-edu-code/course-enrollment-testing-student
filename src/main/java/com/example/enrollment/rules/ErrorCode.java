package com.example.enrollment.rules;

/**
 * 오류 코드와 상태코드·화면 메시지(교육용 API 계약).
 * 규칙 위반 코드(R1~R6, C1~C3)와, 계약 밖 입력 형식 오류용 공통 코드로 나뉜다.
 */
public enum ErrorCode {

    // 신청 규칙
    MISSING_FIELD(400, "필수 정보를 입력해 주세요."),                     // R1
    COURSE_NOT_FOUND(404, "존재하지 않는 과정입니다."),                    // R2
    ENROLLMENT_CLOSED(409, "신청 기간이 지났습니다."),                     // R3
    DUPLICATE_ENROLLMENT(409, "이미 신청한 과정입니다."),                  // R4
    ENROLLMENT_LIMIT(409, "동시에 신청할 수 있는 과정은 최대 3개입니다."), // R5
    COURSE_FULL(409, "정원이 마감되었습니다."),                            // R6

    // 취소 규칙
    ENROLLMENT_NOT_FOUND(404, "신청 내역을 찾을 수 없습니다."),            // C1
    ALREADY_CANCELLED(409, "이미 취소된 신청입니다."),                     // C2
    CANCEL_CLOSED(409, "취소 가능 기간이 지났습니다."),                    // C3

    // 공통(정본 규칙 밖, 이 예제에서 정한 값)
    INVALID_REQUEST(400, "요청 형식이 올바르지 않습니다."),
    NOT_FOUND(404, "요청한 주소를 찾을 수 없습니다."),
    METHOD_NOT_ALLOWED(405, "지원하지 않는 요청 방식입니다."),
    UNSUPPORTED_MEDIA_TYPE(415, "요청 본문은 application/json 형식이어야 합니다."),
    INTERNAL_ERROR(500, "일시적인 오류가 발생했습니다. 잠시 뒤 다시 시도해 주세요.");

    private final int httpStatus;
    private final String message;

    ErrorCode(int httpStatus, String message) {
        this.httpStatus = httpStatus;
        this.message = message;
    }

    public int httpStatus() {
        return httpStatus;
    }

    public String message() {
        return message;
    }
}
