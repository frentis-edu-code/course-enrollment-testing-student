package com.example.enrollment.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.context.annotation.Configuration;

/**
 * OpenAPI 문서 정보.
 * 서버 주소를 "/"(같은 출처)로 두어, 개발 프록시(Vite)를 거쳐 Swagger UI를 열어도
 * Try it out 요청이 다른 출처로 나가지 않게 한다.
 */
@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "교육 과정 신청 API (교육용 예제)",
                version = "0.1.0",
                description = """
                        사내 교육 과정 신청·취소를 다루는 **학습용 예제 API**입니다.
                        인증·권한이 없고 실제 운영용이 아닙니다.
                        규칙(R1~R7, C1~C4)과 상태코드는 교육용으로 합의한 계약입니다.
                        서버의 "요청 시각"은 기본적으로 실습 기준 시각 2026-11-20T10:00:00+09:00에 고정되어 있습니다.
                        """),
        servers = @Server(url = "/", description = "이 앱과 같은 출처"))
public class OpenApiConfig {
}
