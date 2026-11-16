package com.example.enrollment.domain;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {

    /** R4 중복 확인: 같은 사원·같은 과정에 이 상태의 신청이 있는가. */
    boolean existsByEmployeeIdAndCourseIdAndStatus(String employeeId, String courseId, EnrollmentStatus status);

    /** R5 개인 한도: 사원의 보유 신청 수(과정 무관). */
    long countByEmployeeIdAndStatus(String employeeId, EnrollmentStatus status);

    /** R6 정원: 과정의 신청 인원. */
    long countByCourseIdAndStatus(String courseId, EnrollmentStatus status);

    /** 내 신청 목록(취소됨 포함), 신청 ID 순. */
    List<Enrollment> findByEmployeeIdOrderByIdAsc(String employeeId);

    /** 과정 목록의 잔여석 계산용: 과정별 신청됨 인원. */
    @Query("select e.courseId as courseId, count(e) as appliedCount from Enrollment e "
            + "where e.status = com.example.enrollment.domain.EnrollmentStatus.APPLIED group by e.courseId")
    List<CourseAppliedCount> countAppliedByCourse();

    interface CourseAppliedCount {
        String getCourseId();

        long getAppliedCount();
    }
}
