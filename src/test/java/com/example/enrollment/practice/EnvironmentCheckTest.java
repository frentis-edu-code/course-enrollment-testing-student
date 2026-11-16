package com.example.enrollment.practice;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.enrollment.support.ApiTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * 환경 확인. 실습을 시작하기 전에 빌드·테스트 도구·앱 설정이 준비됐는지만 본다.
 * 이 테스트가 통과했다고 앱의 품질을 검증한 것은 아니다. 규칙을 확인하는 테스트는 실습에서 직접 작성한다.
 */
@Tag("smoke")
@DisplayName("환경 확인(품질 검증 아님)")
class EnvironmentCheckTest extends ApiTestSupport {

    @Test
    @DisplayName("앱이 테스트 설정(고정 시계·메모리 DB·기준 과정 4개)으로 뜬다")
    void appStartsWithLabSettings() throws Exception {
        mvc.perform(get("/api/lab-info")).andExpect(status().isOk());
        assertThat(json(courses())).hasSize(4);
        assertThat(data.totalEnrollments()).isZero();
    }
}
