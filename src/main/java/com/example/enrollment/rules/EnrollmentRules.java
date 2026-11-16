package com.example.enrollment.rules;

import com.example.enrollment.domain.EnrollmentStatus;
import java.time.LocalDateTime;
import java.util.Optional;

/**
 * 신청·취소 규칙을 판단하는 순수 객체. 저장소·시계·Spring에 의존하지 않는다.
 * 결과는 "처음 위반한 규칙의 오류 코드" 하나이며, 통과하면 빈 값이다.
 *
 * <p>존재 확인(R2 과정 존재, C1 신청 존재)은 저장소 조회 결과 자체이므로 서비스가 맡는다.
 */
public class EnrollmentRules {

    /** R5: 동시에 보유할 수 있는 신청됨 신청의 최대 개수. */
    public static final int MAX_ACTIVE_ENROLLMENTS_PER_EMPLOYEE = 3;

    /** C3: 취소 기한은 과정 시작 일시에서 이 시간만큼 앞이다. */
    public static final long CANCEL_HOURS_BEFORE_START = 24;

    /** R1 필수 입력: null, 빈 문자열, 공백만 있는 문자열은 모두 누락이다. */
    public Optional<ErrorCode> checkRequiredFields(String employeeId, String courseId) {
        if (isMissing(employeeId) || isMissing(courseId)) {
            return Optional.of(ErrorCode.MISSING_FIELD);
        }
        return Optional.empty();
    }

    /** R3 → R4 → R5 → R6 순서로 확인한다(R7). R1·R2는 이미 통과했다고 본다. */
    public Optional<ErrorCode> checkEnrollment(EnrollmentCheck check) {
        // R3 신청 마감: 요청 시각이 마감 일시와 같거나 이전이면 통과(정각은 접수)
        if (check.requestTime().isAfter(check.enrollmentDeadline())) {
            return Optional.of(ErrorCode.ENROLLMENT_CLOSED);
        }
        // R4 중복 금지: 신청됨 상태만 중복으로 본다
        if (check.alreadyApplied()) {
            return Optional.of(ErrorCode.DUPLICATE_ENROLLMENT);
        }
        // R5 개인 한도: 보유 신청 수가 3 미만이어야 한다
        if (check.employeeActiveCount() >= MAX_ACTIVE_ENROLLMENTS_PER_EMPLOYEE) {
            return Optional.of(ErrorCode.ENROLLMENT_LIMIT);
        }
        // R6 정원: 신청 인원이 정원보다 작아야 한다
        if (check.courseActiveCount() >= check.capacity()) {
            return Optional.of(ErrorCode.COURSE_FULL);
        }
        return Optional.empty();
    }

    /** C2 → C3 순서로 확인한다(C4). C1은 이미 통과했다고 본다. */
    public Optional<ErrorCode> checkCancellation(EnrollmentStatus status, LocalDateTime courseStartAt,
                                                 LocalDateTime requestTime) {
        // C2 상태: 신청됨이어야 한다
        if (status != EnrollmentStatus.APPLIED) {
            return Optional.of(ErrorCode.ALREADY_CANCELLED);
        }
        // C3 취소 기한: 요청 시각이 (시작 일시 − 24시간)과 같거나 이전이면 통과
        if (requestTime.isAfter(cancelDeadline(courseStartAt))) {
            return Optional.of(ErrorCode.CANCEL_CLOSED);
        }
        return Optional.empty();
    }

    public static LocalDateTime cancelDeadline(LocalDateTime courseStartAt) {
        return courseStartAt.minusHours(CANCEL_HOURS_BEFORE_START);
    }

    private static boolean isMissing(String value) {
        return value == null || value.isBlank();
    }
}
