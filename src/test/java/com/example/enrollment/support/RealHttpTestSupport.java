package com.example.enrollment.support;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;

/**
 * 실제 HTTP 테스트 공통 준비.
 *
 * <p>MockMvc(ApiTestSupport)는 Spring 안에서 요청을 흉내 내므로 네트워크·Tomcat을 지나지 않는다.
 * 이 클래스는 빈 포트(RANDOM_PORT)에 실제 내장 서버를 띄우고 JDK HttpClient로 요청한다.
 * 그래서 실제 직렬화·문자 인코딩·컨테이너 오류 처리까지 함께 지나간다.
 * 서버는 같은 JVM에서 뜨므로 시계(MutableClock)와 데이터(TestData)는 테스트가 그대로 정할 수 있다.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import({TestClockConfig.class, TestData.class})
public abstract class RealHttpTestSupport {

    private static final HttpClient CLIENT = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(5))
            .build();

    @LocalServerPort
    protected int port;

    @Autowired
    protected MutableClock clock;

    @Autowired
    protected TestData data;

    @Autowired
    protected ObjectMapper objectMapper;

    @BeforeEach
    void resetRealHttpState() {
        data.clearEnrollments();
        clock.setTo(TestClockConfig.LAB_NOW);
    }

    /** 실제로 받은 응답. body는 JSON이 아니면 null이다. */
    public record Response(int status, String contentType, String raw, JsonNode body) {
        public String code() {
            return body == null ? null : body.path("code").asText(null);
        }

        public String message() {
            return body == null ? null : body.path("message").asText(null);
        }
    }

    protected Response get(String pathAndQuery) throws IOException, InterruptedException {
        return send(HttpRequest.newBuilder(uri(pathAndQuery)).GET());
    }

    protected Response postJson(String path, String json) throws IOException, InterruptedException {
        return post(path, "application/json", json);
    }

    protected Response post(String path, String contentType, String body) throws IOException, InterruptedException {
        HttpRequest.Builder builder = HttpRequest.newBuilder(uri(path))
                .POST(HttpRequest.BodyPublishers.ofString(body, StandardCharsets.UTF_8));
        if (contentType != null) {
            builder.header("Content-Type", contentType);
        }
        return send(builder);
    }

    protected Response enroll(String employeeId, String courseId) throws IOException, InterruptedException {
        String json = objectMapper.createObjectNode().put("employeeId", employeeId).put("courseId", courseId).toString();
        return postJson("/api/enrollments", json);
    }

    protected Response cancel(Object enrollmentId) throws IOException, InterruptedException {
        return post("/api/enrollments/" + enrollmentId + "/cancel", null, "");
    }

    protected Response myEnrollments(String employeeId) throws IOException, InterruptedException {
        return get("/api/enrollments?employeeId=" + URLEncoder.encode(employeeId, StandardCharsets.UTF_8));
    }

    /** 과정 목록 응답에서 그 과정의 잔여석. */
    protected long remainingSeats(String courseId) throws IOException, InterruptedException {
        for (JsonNode course : get("/api/courses").body()) {
            if (course.path("courseId").asText().equals(courseId)) {
                return course.path("remainingSeats").asLong();
            }
        }
        throw new AssertionError("과정 없음: " + courseId);
    }

    protected URI uri(String pathAndQuery) {
        return URI.create("http://127.0.0.1:" + port + pathAndQuery);
    }

    private Response send(HttpRequest.Builder builder) throws IOException, InterruptedException {
        HttpResponse<String> response = CLIENT.send(builder.timeout(Duration.ofSeconds(10)).build(),
                HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
        String contentType = response.headers().firstValue("Content-Type").orElse("");
        JsonNode body = null;
        if (contentType.startsWith("application/json") && !response.body().isEmpty()) {
            body = objectMapper.readTree(response.body());
        }
        return new Response(response.statusCode(), contentType, response.body(), body);
    }
}
