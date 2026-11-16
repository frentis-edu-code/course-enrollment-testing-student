package com.example.enrollment.config;

import java.time.OffsetDateTime;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;

/**
 * 실습 환경 설정.
 *
 * @param clockMode FIXED면 fixedAt 시각에 멈춘 시계를, SYSTEM이면 실제 현재 시각을 쓴다.
 * @param fixedAt   FIXED 모드에서 쓰는 실습 기준 시각
 */
@ConfigurationProperties(prefix = "lab")
public record LabProperties(
        @DefaultValue("FIXED") ClockMode clockMode,
        @DefaultValue("2026-11-20T10:00:00+09:00") OffsetDateTime fixedAt) {

    public enum ClockMode {
        FIXED,
        SYSTEM
    }
}
