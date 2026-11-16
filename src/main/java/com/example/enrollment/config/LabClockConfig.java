package com.example.enrollment.config;

import com.example.enrollment.rules.EnrollmentRules;
import java.time.Clock;
import java.time.ZoneId;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 시계 주입. 서비스는 Clock 빈에서 "요청 시각"을 얻는다.
 * 기본값은 실습 기준 시각(2026-11-20T10:00:00+09:00)에 멈춘 고정 시계다.
 * 테스트는 이 빈을 바꿔 끼워 원하는 시각을 만든다.
 */
@Configuration
public class LabClockConfig {

    public static final ZoneId SEOUL = ZoneId.of("Asia/Seoul");

    @Bean
    public Clock clock(LabProperties properties) {
        if (properties.clockMode() == LabProperties.ClockMode.SYSTEM) {
            return Clock.system(SEOUL);
        }
        return Clock.fixed(properties.fixedAt().toInstant(), SEOUL);
    }

    @Bean
    public EnrollmentRules enrollmentRules() {
        return new EnrollmentRules();
    }
}
