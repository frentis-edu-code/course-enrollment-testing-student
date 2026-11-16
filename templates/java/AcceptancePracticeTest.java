package com.example.enrollment.practice;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

import com.example.enrollment.support.RealHttpTestSupport;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * 실습 4(인수): 인수조건 하나를 업무 시나리오(Given/When/Then)로 실제 HTTP에서 확인한다.
 * RealHttpTestSupport는 빈 포트에 실제 서버를 띄운다. 도구: enroll(..), cancel(..), myEnrollments(..),
 * remainingSeats(..), clock.setTo(..). 응답 객체: status(), code(), message(), body().
 * 결과는 사용자가 보는 값(안내 메시지, 잔여석, 내 신청 목록)으로 확인한다.
 * 이 테스트가 통과해도 사용자 인수 승인은 아니다.
 */
@Disabled("실습 4에서 이 줄을 지우고 작성합니다")
@Tag("acceptance")
@DisplayName("실습 4: 인수 시나리오")
class AcceptancePracticeTest extends RealHttpTestSupport {

    @Test
    @DisplayName("(인수조건 ID): 시나리오 이름")
    void scenario() throws Exception {
        // Given
        // When
        // Then
        fail("아직 작성하지 않았습니다");
    }
}
