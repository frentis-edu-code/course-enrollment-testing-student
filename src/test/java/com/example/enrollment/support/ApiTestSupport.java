package com.example.enrollment.support;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

/**
 * API 통합 테스트 공통 준비.
 * 각 테스트 전에 신청을 모두 지우고 시계를 실습 기준 시각으로 되돌린다(테스트끼리 상태를 나누지 않음).
 */
@SpringBootTest
@AutoConfigureMockMvc
@Import({TestClockConfig.class, TestData.class})
public abstract class ApiTestSupport {

    @Autowired
    protected MockMvc mvc;

    @Autowired
    protected MutableClock clock;

    @Autowired
    protected TestData data;

    @Autowired
    protected ObjectMapper objectMapper;

    @BeforeEach
    void resetState() {
        data.clearEnrollments();
        clock.setTo(TestClockConfig.LAB_NOW);
    }

    protected ResultActions enroll(String employeeId, String courseId) throws Exception {
        String body = objectMapper.writeValueAsString(objectMapper.createObjectNode()
                .put("employeeId", employeeId)
                .put("courseId", courseId));
        return enrollRaw(body);
    }

    protected ResultActions enrollRaw(String json) throws Exception {
        return mvc.perform(post("/api/enrollments").contentType(MediaType.APPLICATION_JSON).content(json));
    }

    protected ResultActions cancel(Object enrollmentId) throws Exception {
        return mvc.perform(post("/api/enrollments/{id}/cancel", enrollmentId));
    }

    protected ResultActions courses() throws Exception {
        return mvc.perform(get("/api/courses"));
    }

    protected JsonNode json(ResultActions result) throws Exception {
        return objectMapper.readTree(result.andReturn().getResponse().getContentAsString());
    }

    protected long remainingSeatsOf(String courseId) throws Exception {
        for (JsonNode c : json(courses())) {
            if (c.get("courseId").asText().equals(courseId)) {
                return c.get("remainingSeats").asLong();
            }
        }
        throw new AssertionError("과정 없음: " + courseId);
    }
}
