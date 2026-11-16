package com.example.enrollment.web;

import com.example.enrollment.domain.Enrollment;
import com.example.enrollment.domain.EnrollmentStatus;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "CancelResponse", description = "취소 성공 응답")
public record CancelResponse(
        @Schema(description = "취소한 신청 ID", example = "501", requiredMode = Schema.RequiredMode.REQUIRED)
        Long enrollmentId,
        @Schema(description = "신청 상태", example = "CANCELLED", requiredMode = Schema.RequiredMode.REQUIRED)
        EnrollmentStatus status) {

    static CancelResponse from(Enrollment e) {
        return new CancelResponse(e.getId(), e.getStatus());
    }
}
