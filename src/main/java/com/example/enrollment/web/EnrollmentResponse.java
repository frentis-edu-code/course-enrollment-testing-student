package com.example.enrollment.web;

import com.example.enrollment.domain.Enrollment;
import com.example.enrollment.domain.EnrollmentStatus;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "EnrollmentResponse", description = "신청 성공 응답")
public record EnrollmentResponse(
        @Schema(description = "새로 만든 신청 ID", example = "501", requiredMode = Schema.RequiredMode.REQUIRED)
        Long enrollmentId,
        @Schema(description = "과정 ID", example = "C-101", requiredMode = Schema.RequiredMode.REQUIRED)
        String courseId,
        @Schema(description = "신청 상태", example = "APPLIED", requiredMode = Schema.RequiredMode.REQUIRED)
        EnrollmentStatus status) {

    static EnrollmentResponse from(Enrollment e) {
        return new EnrollmentResponse(e.getId(), e.getCourseId(), e.getStatus());
    }
}
