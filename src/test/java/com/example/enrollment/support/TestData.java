package com.example.enrollment.support;

import com.example.enrollment.domain.Enrollment;
import com.example.enrollment.domain.EnrollmentRepository;
import com.example.enrollment.domain.EnrollmentStatus;
import java.util.ArrayList;
import java.util.List;

/**
 * 테스트 사전 조건을 저장소에 직접 만든다(규칙을 거치지 않음).
 * @Import(TestData.class)로 테스트에 넣어 쓴다.
 */
public class TestData {

    private final EnrollmentRepository enrollments;

    public TestData(EnrollmentRepository enrollments) {
        this.enrollments = enrollments;
    }

    /** 신청 전부 삭제. 과정 시드는 그대로 둔다. */
    public void clearEnrollments() {
        enrollments.deleteAll();
    }

    public Enrollment applied(String employeeId, String courseId) {
        return enrollments.save(Enrollment.apply(employeeId, courseId));
    }

    public Enrollment cancelled(String employeeId, String courseId) {
        Enrollment e = Enrollment.apply(employeeId, courseId);
        e.cancel();
        return enrollments.save(e);
    }

    /** E{first}부터 count명이 과정에 신청됨 상태로 들어간 상태를 만든다. 예: fill("C-101", 1001, 20) → E1001~E1020 */
    public List<Enrollment> fill(String courseId, int firstEmployeeNumber, int count) {
        List<Enrollment> created = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            created.add(applied("E" + (firstEmployeeNumber + i), courseId));
        }
        return created;
    }

    public long appliedCountOf(String courseId) {
        return enrollments.countByCourseIdAndStatus(courseId, EnrollmentStatus.APPLIED);
    }

    public long activeCountOfEmployee(String employeeId) {
        return enrollments.countByEmployeeIdAndStatus(employeeId, EnrollmentStatus.APPLIED);
    }

    public long totalEnrollments() {
        return enrollments.count();
    }

    public List<Enrollment> all() {
        return enrollments.findAll();
    }
}
