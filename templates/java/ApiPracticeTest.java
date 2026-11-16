package com.example.enrollment.practice;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.enrollment.support.ApiTestSupport;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * 실습 4: API 테스트(MockMvc, Spring 안에서 웹 계층부터 DB까지 연결).
 * ApiTestSupport가 테스트마다 신청을 지우고 시계를 2026-11-20 10:00으로 되돌린다.
 * 도구: enroll(사원, 과정), cancel(신청ID), courses(), remainingSeatsOf(과정),
 *       data.fill("C-101", 1001, 19), data.applied(..), data.cancelled(..), data.appliedCountOf(..),
 *       clock.setTo("2026-12-01T18:00:01+09:00")
 * 응답(상태코드·code·message)과 함께 데이터 변화(신청 인원, 거절이면 그대로)도 확인한다.
 */
@Disabled("실습 4에서 이 줄을 지우고 작성합니다")
@Tag("api")
@DisplayName("실습 4: API 테스트")
class ApiPracticeTest extends ApiTestSupport {

    @Test
    @DisplayName("(케이스 ID): 사전 조건 → 요청 → 예상 응답과 데이터")
    void firstCase() throws Exception {
        // 준비: 예) data.fill("C-101", 1001, 19);
        // 실행과 응답 확인: 예) enroll("E1020", "C-101").andExpect(status().isCreated());
        // 데이터 확인: 예) assertThat(data.appliedCountOf("C-101")).isEqualTo(...);
        fail("아직 작성하지 않았습니다");
    }
}
