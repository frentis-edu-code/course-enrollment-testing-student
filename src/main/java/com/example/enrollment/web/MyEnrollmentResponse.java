package com.example.enrollment.web;

import com.example.enrollment.domain.EnrollmentStatus;
import com.example.enrollment.service.EnrollmentService.MyEnrollment;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "MyEnrollmentResponse", description = "내 신청 목록의 한 줄(취소됨 포함)")
public record MyEnrollmentResponse(
        @Schema(example = "501", requiredMode = Schema.RequiredMode.REQUIRED) Long enrollmentId,
        @Schema(example = "C-101", requiredMode = Schema.RequiredMode.REQUIRED) String courseId,
        @Schema(example = "테스트 설계 기초", requiredMode = Schema.RequiredMode.REQUIRED) String courseTitle,
        @Schema(example = "APPLIED", requiredMode = Schema.RequiredMode.REQUIRED) EnrollmentStatus status) {

    static MyEnrollmentResponse from(MyEnrollment e) {
        return new MyEnrollmentResponse(e.enrollmentId(), e.courseId(), e.courseTitle(), e.status());
    }
}
