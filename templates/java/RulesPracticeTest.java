package com.example.enrollment.practice;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

import com.example.enrollment.rules.EnrollmentCheck;
import com.example.enrollment.rules.EnrollmentRules;
import com.example.enrollment.rules.ErrorCode;
import java.time.LocalDateTime;
import java.util.Optional;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * 실습 3: 규칙 객체(EnrollmentRules) 단위 테스트를 직접 작성한다.
 * 시작할 때 클래스의 @Disabled를 지운다. 예상값의 근거는 docs/requirements.md의 규칙 번호로 적는다.
 * EnrollmentCheck(요청 시각, 신청 마감 일시, 이미 신청함, 사원 보유 신청 수, 과정 신청 인원, 정원)
 */
@Disabled("실습 3에서 이 줄을 지우고 작성합니다")
@Tag("unit")
@DisplayName("실습 3: 규칙 단위 테스트")
class RulesPracticeTest {

    static final LocalDateTime LAB_NOW = LocalDateTime.of(2026, 11, 20, 10, 0, 0);
    static final LocalDateTime C101_DEADLINE = LocalDateTime.of(2026, 12, 1, 18, 0, 0);

    final EnrollmentRules rules = new EnrollmentRules();

    @Test
    @DisplayName("기본: (케이스 ID와 확인할 내용을 적는다)")
    void basic() {
        // 준비(Arrange): 이 케이스의 사전 조건을 값으로 만든다
        EnrollmentCheck check = new EnrollmentCheck(LAB_NOW, C101_DEADLINE, false, 0, 0, 20);

        // 실행(Act)
        Optional<ErrorCode> result = rules.checkEnrollment(check);

        // 확인(Assert): 근거 규칙 = (예: R6)
        fail("아직 작성하지 않았습니다. 예상 결과를 정하고 assertThat(result)...로 바꾸세요. 현재 결과: " + result);
    }

    @Test
    @DisplayName("보강: 경계·예외·조합 중 무엇을 왜 더하는지 적는다")
    void reinforced() {
        fail("아직 작성하지 않았습니다");
    }
}
