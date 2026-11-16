package com.example.enrollment.service;

import com.example.enrollment.config.LabClockConfig;
import java.time.Clock;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import org.springframework.stereotype.Component;

/** 주입된 시계에서 "요청 시각"을 초 단위(Asia/Seoul)로 얻는다. */
@Component
public class LabTime {

    private final Clock clock;

    public LabTime(Clock clock) {
        this.clock = clock;
    }

    /** 규칙 비교용 현지 시각. 초 미만은 버린다(18:00:00.900은 18:00:00으로 본다). */
    public LocalDateTime now() {
        return LocalDateTime.ofInstant(clock.instant(), LabClockConfig.SEOUL).truncatedTo(ChronoUnit.SECONDS);
    }

    public OffsetDateTime nowWithOffset() {
        return toOffset(now());
    }

    public static OffsetDateTime toOffset(LocalDateTime seoulLocal) {
        return seoulLocal.atZone(LabClockConfig.SEOUL).toOffsetDateTime();
    }
}
