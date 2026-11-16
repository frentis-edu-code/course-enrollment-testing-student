package com.example.enrollment.support;

import java.time.Clock;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneId;

/** 테스트에서 "요청 시각"을 바꿔 가며 쓰는 시계. */
public class MutableClock extends Clock {

    private volatile Instant instant;
    private final ZoneId zone;

    public MutableClock(Instant instant, ZoneId zone) {
        this.instant = instant;
        this.zone = zone;
    }

    /** 예: setTo("2026-12-01T18:00:01+09:00") */
    public void setTo(String isoOffsetDateTime) {
        this.instant = OffsetDateTime.parse(isoOffsetDateTime).toInstant();
    }

    @Override
    public ZoneId getZone() {
        return zone;
    }

    @Override
    public Clock withZone(ZoneId zone) {
        return new MutableClock(instant, zone);
    }

    @Override
    public Instant instant() {
        return instant;
    }
}
