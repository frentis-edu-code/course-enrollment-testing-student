package com.example.enrollment.web;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "EnrollmentRequest", description = "신청 요청. 두 값 중 하나라도 없거나 공백뿐이면 400 MISSING_FIELD(R1).")
public record EnrollmentRequest(
        @Schema(description = "사원번호(교육용 가상 값 E1001~E1030)", example = "E1001",
                requiredMode = Schema.RequiredMode.REQUIRED)
        String employeeId,
        @Schema(description = "과정 ID", example = "C-101", requiredMode = Schema.RequiredMode.REQUIRED)
        String courseId) {
}
