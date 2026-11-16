package com.example.enrollment.support;

import com.example.enrollment.config.LabClockConfig;
import java.time.OffsetDateTime;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

/** 애플리케이션의 고정 시계 대신 테스트가 바꿀 수 있는 시계를 넣는다. */
@TestConfiguration
public class TestClockConfig {

    public static final String LAB_NOW = "2026-11-20T10:00:00+09:00";

    @Bean
    @Primary
    public MutableClock testClock() {
        return new MutableClock(OffsetDateTime.parse(LAB_NOW).toInstant(), LabClockConfig.SEOUL);
    }
}
