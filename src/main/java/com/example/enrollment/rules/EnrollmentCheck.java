package com.example.enrollment.rules;

import java.time.LocalDateTime;

/**
 * R3~R6 판단에 필요한 사실. 서비스가 저장소와 시계에서 모아 규칙 객체에 넘긴다.
 *
 * @param requestTime         요청 시각(초 단위로 자른 Asia/Seoul 현지 시각)
 * @param enrollmentDeadline  과정의 신청 마감 일시
 * @param alreadyApplied      같은 사원이 같은 과정에 신청됨 상태 신청을 갖고 있는가
 * @param employeeActiveCount 사원의 보유 신청 수(신청됨만, 과정 무관)
 * @param courseActiveCount   과정의 신청 인원(신청됨만)
 * @param capacity            과정 정원
 */
public record EnrollmentCheck(
        LocalDateTime requestTime,
        LocalDateTime enrollmentDeadline,
        boolean alreadyApplied,
        long employeeActiveCount,
        long courseActiveCount,
        int capacity) {
}
