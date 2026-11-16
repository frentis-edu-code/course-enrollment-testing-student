package com.example.enrollment.web;

import com.example.enrollment.config.LabClockConfig;
import com.example.enrollment.config.LabProperties;
import com.example.enrollment.service.LabTime;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "실습 정보", description = "실습 환경 확인용")
public class LabInfoController {

    private final LabProperties properties;
    private final LabTime labTime;

    public LabInfoController(LabProperties properties, LabTime labTime) {
        this.properties = properties;
        this.labTime = labTime;
    }

    @GetMapping("/api/lab-info")
    @Operation(summary = "실습 환경 정보",
            description = "앱 식별자, 서버가 규칙 판단에 쓰는 요청 시각(시계 모드), 데이터 초기화 방법을 돌려준다.")
    public LabInfoResponse labInfo() {
        return new LabInfoResponse(
                "course-enrollment-testing",
                "교육용 학습 예제입니다. 인증·권한 기능이 없고 실제 운영용이 아닙니다.",
                properties.clockMode().name(),
                labTime.nowWithOffset(),
                LabClockConfig.SEOUL.getId(),
                "서버를 다시 시작하면 메모리 DB가 초기화되어 기본 과정 4개, 신청 0건 상태로 돌아갑니다.");
    }
}
