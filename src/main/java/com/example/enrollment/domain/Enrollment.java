package com.example.enrollment.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

/**
 * 신청. 취소해도 행을 지우지 않고 상태만 CANCELLED로 바꾼다.
 * 취소 뒤 다시 신청하면 이 행을 되살리지 않고 새 신청(새 ID)을 만든다.
 */
@Entity
@Table(name = "enrollment", indexes = {
        @Index(name = "ix_enrollment_employee", columnList = "employee_id, status"),
        @Index(name = "ix_enrollment_course", columnList = "course_id, status")
})
public class Enrollment {

    /** 신청 ID는 501부터 1씩 늘어난다(문서 예시와 맞추기 위한 값일 뿐 의미는 없다). */
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "enrollment_seq")
    @SequenceGenerator(name = "enrollment_seq", sequenceName = "enrollment_seq",
            initialValue = 501, allocationSize = 1)
    @Column(name = "enrollment_id")
    private Long id;

    /**
     * 사원번호에는 길이 규칙이 없다(R1은 "비어 있지 않음"만 요구한다).
     * 그래서 H2 VARCHAR의 최대 길이(1,000,000,000자)로 매핑해 사실상 제한을 두지 않는다.
     * length만 크게 주면 Hibernate가 CLOB으로 바꾸고, H2는 CLOB 열에 인덱스를 만들지 못한다.
     * 같음 비교(R4·R5 조회)와 인덱스를 그대로 쓰려고 열 정의를 VARCHAR로 직접 적었다(H2 전용 예제).
     */
    @Column(name = "employee_id", nullable = false, columnDefinition = "varchar(1000000000)")
    private String employeeId;

    @Column(name = "course_id", nullable = false, length = 20)
    private String courseId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EnrollmentStatus status;

    protected Enrollment() {
        // JPA
    }

    private Enrollment(String employeeId, String courseId, EnrollmentStatus status) {
        this.employeeId = employeeId;
        this.courseId = courseId;
        this.status = status;
    }

    /** 신청됨 상태의 새 신청을 만든다. */
    public static Enrollment apply(String employeeId, String courseId) {
        return new Enrollment(employeeId, courseId, EnrollmentStatus.APPLIED);
    }

    public void cancel() {
        this.status = EnrollmentStatus.CANCELLED;
    }

    public Long getId() {
        return id;
    }

    public String getEmployeeId() {
        return employeeId;
    }

    public String getCourseId() {
        return courseId;
    }

    public EnrollmentStatus getStatus() {
        return status;
    }
}
