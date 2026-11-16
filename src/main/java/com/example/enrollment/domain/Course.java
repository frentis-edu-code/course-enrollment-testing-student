package com.example.enrollment.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

/** 교육 과정. 날짜·시각은 Asia/Seoul 기준의 현지 시각으로 저장한다. */
@Entity
@Table(name = "course")
public class Course {

    @Id
    @Column(name = "course_id", length = 20)
    private String courseId;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private int capacity;

    @Column(name = "enrollment_deadline", nullable = false)
    private LocalDateTime enrollmentDeadline;

    @Column(name = "start_at", nullable = false)
    private LocalDateTime startAt;

    protected Course() {
        // JPA
    }

    public Course(String courseId, String title, int capacity,
                  LocalDateTime enrollmentDeadline, LocalDateTime startAt) {
        this.courseId = courseId;
        this.title = title;
        this.capacity = capacity;
        this.enrollmentDeadline = enrollmentDeadline;
        this.startAt = startAt;
    }

    public String getCourseId() {
        return courseId;
    }

    public String getTitle() {
        return title;
    }

    public int getCapacity() {
        return capacity;
    }

    public LocalDateTime getEnrollmentDeadline() {
        return enrollmentDeadline;
    }

    public LocalDateTime getStartAt() {
        return startAt;
    }
}
