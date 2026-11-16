package com.example.enrollment.web;

import com.example.enrollment.rules.EnrollmentRules;
import com.example.enrollment.service.EnrollmentService.CourseSummary;
import com.example.enrollment.service.LabTime;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.OffsetDateTime;

@Schema(name = "CourseResponse", description = "과정과 현재 잔여석. 시각은 +09:00(Asia/Seoul) 오프셋으로 표기한다.")
public record CourseResponse(
        @Schema(example = "C-101", requiredMode = Schema.RequiredMode.REQUIRED) String courseId,
        @Schema(example = "테스트 설계 기초", requiredMode = Schema.RequiredMode.REQUIRED) String title,
        @Schema(description = "정원", example = "20", requiredMode = Schema.RequiredMode.REQUIRED) int capacity,
        @Schema(description = "신청 인원(신청됨 상태만)", example = "0", requiredMode = Schema.RequiredMode.REQUIRED)
        long appliedCount,
        @Schema(description = "잔여석 = 정원 − 신청 인원", example = "20", requiredMode = Schema.RequiredMode.REQUIRED)
        long remainingSeats,
        @Schema(description = "신청 마감 일시(이 시각까지 접수)", example = "2026-12-01T18:00:00+09:00",
                requiredMode = Schema.RequiredMode.REQUIRED)
        OffsetDateTime enrollmentDeadline,
        @Schema(description = "과정 시작 일시", example = "2026-12-03T09:00:00+09:00",
                requiredMode = Schema.RequiredMode.REQUIRED)
        OffsetDateTime startAt,
        @Schema(description = "취소 기한 = 시작 일시 − 24시간(이 시각까지 취소 가능)",
                example = "2026-12-02T09:00:00+09:00", requiredMode = Schema.RequiredMode.REQUIRED)
        OffsetDateTime cancelDeadline) {

    static CourseResponse from(CourseSummary s) {
        var c = s.course();
        return new CourseResponse(c.getCourseId(), c.getTitle(), c.getCapacity(), s.appliedCount(),
                s.remainingSeats(),
                LabTime.toOffset(c.getEnrollmentDeadline()),
                LabTime.toOffset(c.getStartAt()),
                LabTime.toOffset(EnrollmentRules.cancelDeadline(c.getStartAt())));
    }
}
