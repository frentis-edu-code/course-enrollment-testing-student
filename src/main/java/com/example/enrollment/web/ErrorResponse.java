package com.example.enrollment.web;

import com.example.enrollment.rules.ErrorCode;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "ErrorResponse", description = "오류 응답. 처음 위반한 규칙 하나만 담는다. 내부 예외 정보는 담지 않는다.")
public record ErrorResponse(
        @Schema(description = "오류 코드", example = "COURSE_FULL",
                requiredMode = Schema.RequiredMode.REQUIRED,
                allowableValues = {"MISSING_FIELD", "COURSE_NOT_FOUND", "ENROLLMENT_CLOSED", "DUPLICATE_ENROLLMENT",
                        "ENROLLMENT_LIMIT", "COURSE_FULL", "ENROLLMENT_NOT_FOUND", "ALREADY_CANCELLED",
                        "CANCEL_CLOSED", "INVALID_REQUEST", "NOT_FOUND", "METHOD_NOT_ALLOWED",
                        "UNSUPPORTED_MEDIA_TYPE", "INTERNAL_ERROR"})
        String code,
        @Schema(description = "화면에 그대로 보여 줄 한국어 메시지", example = "정원이 마감되었습니다.",
                requiredMode = Schema.RequiredMode.REQUIRED)
        String message) {

    public static ErrorResponse of(ErrorCode code) {
        return new ErrorResponse(code.name(), code.message());
    }
}
