package com.example.enrollment.web;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.OffsetDateTime;

@Schema(name = "LabInfoResponse", description = "실습 환경 정보")
public record LabInfoResponse(
        @Schema(example = "course-enrollment-testing", requiredMode = Schema.RequiredMode.REQUIRED) String appId,
        @Schema(example = "교육용 학습 예제입니다. 인증·권한 기능이 없고 실제 운영용이 아닙니다.",
                requiredMode = Schema.RequiredMode.REQUIRED) String notice,
        @Schema(description = "FIXED: 실습 기준 시각에 멈춘 시계, SYSTEM: 실제 현재 시각", example = "FIXED",
                requiredMode = Schema.RequiredMode.REQUIRED) String clockMode,
        @Schema(description = "서버가 규칙 판단에 쓰는 현재 요청 시각(초 단위)", example = "2026-11-20T10:00:00+09:00",
                requiredMode = Schema.RequiredMode.REQUIRED) OffsetDateTime labNow,
        @Schema(example = "Asia/Seoul", requiredMode = Schema.RequiredMode.REQUIRED) String timeZone,
        @Schema(example = "서버를 다시 시작하면 메모리 DB가 초기화되어 기본 과정 4개, 신청 0건 상태로 돌아갑니다.",
                requiredMode = Schema.RequiredMode.REQUIRED) String resetMethod) {
}
