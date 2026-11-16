package com.example.enrollment.config;

import com.example.enrollment.domain.Course;
import com.example.enrollment.domain.CourseRepository;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * 기준 데이터(시드). 메모리 DB이므로 서버를 시작할 때마다 이 4개 과정만 있는 상태로 돌아간다.
 * 기본 시드에는 신청이 하나도 없다(모든 과정 잔여석 20).
 * 테스트에 필요한 신청 상태는 각 테스트가 따로 준비한다.
 */
@Component
public class SeedData implements ApplicationRunner {

    /** 시각은 모두 Asia/Seoul 기준이다. 취소 기한은 시작 일시 − 24시간으로 계산한다. */
    public static final List<SeedCourse> COURSES = List.of(
            new SeedCourse("C-101", "테스트 설계 기초", 20,
                    LocalDateTime.of(2026, 12, 1, 18, 0, 0), LocalDateTime.of(2026, 12, 3, 9, 0, 0)),
            new SeedCourse("C-102", "API 테스트 실무", 20,
                    LocalDateTime.of(2026, 12, 8, 18, 0, 0), LocalDateTime.of(2026, 12, 10, 9, 0, 0)),
            new SeedCourse("C-103", "화면 테스트 입문", 20,
                    LocalDateTime.of(2026, 12, 15, 18, 0, 0), LocalDateTime.of(2026, 12, 17, 9, 0, 0)),
            new SeedCourse("C-104", "회귀 테스트 운영", 20,
                    LocalDateTime.of(2026, 12, 22, 18, 0, 0), LocalDateTime.of(2026, 12, 24, 9, 0, 0)));

    public record SeedCourse(String courseId, String title, int capacity,
                             LocalDateTime enrollmentDeadline, LocalDateTime startAt) {
        Course toEntity() {
            return new Course(courseId, title, capacity, enrollmentDeadline, startAt);
        }
    }

    private final CourseRepository courseRepository;

    public SeedData(CourseRepository courseRepository) {
        this.courseRepository = courseRepository;
    }

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        if (courseRepository.count() == 0) {
            courseRepository.saveAll(COURSES.stream().map(SeedCourse::toEntity).toList());
        }
    }
}
