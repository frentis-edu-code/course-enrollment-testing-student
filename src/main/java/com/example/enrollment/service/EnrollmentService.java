package com.example.enrollment.service;

import com.example.enrollment.domain.Course;
import com.example.enrollment.domain.CourseRepository;
import com.example.enrollment.domain.Enrollment;
import com.example.enrollment.domain.EnrollmentRepository;
import com.example.enrollment.domain.EnrollmentStatus;
import com.example.enrollment.rules.EnrollmentCheck;
import com.example.enrollment.rules.EnrollmentRules;
import com.example.enrollment.rules.ErrorCode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

/**
 * 신청·취소 유스케이스.
 *
 * <p><b>쓰기 직렬화</b>: 신청과 취소는 하나의 공정(fair) 잠금 안에서 트랜잭션을 시작하고 커밋까지 마친다.
 * 그래서 같은 JVM 안에서는 "확인 → 저장"이 한 번에 하나씩만 일어나고, 동시에 들어온 요청도
 * 정원(R6)·중복(R4)·개인 한도(R5)를 넘기지 못한다. 서버를 여러 대 띄우는 분산 환경은 보장하지 않는다.
 * 읽기(목록 조회)는 잠그지 않으며 커밋된 값만 본다.
 */
@Service
public class EnrollmentService {

    private final CourseRepository courseRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final EnrollmentRules rules;
    private final LabTime labTime;
    private final TransactionTemplate tx;
    private final ReentrantLock writeLock = new ReentrantLock(true);

    public EnrollmentService(CourseRepository courseRepository,
                             EnrollmentRepository enrollmentRepository,
                             EnrollmentRules rules,
                             LabTime labTime,
                             PlatformTransactionManager transactionManager) {
        this.courseRepository = courseRepository;
        this.enrollmentRepository = enrollmentRepository;
        this.rules = rules;
        this.labTime = labTime;
        this.tx = new TransactionTemplate(transactionManager);
    }

    /** 신청. R1 → R2 → R3 → R4 → R5 → R6 순서로 확인하고 처음 위반한 규칙 하나로 거절한다. */
    public Enrollment enroll(String rawEmployeeId, String rawCourseId) {
        // R1 필수 입력
        rules.checkRequiredFields(rawEmployeeId, rawCourseId).ifPresent(BusinessRuleException::throwFor);
        // 앞뒤 공백은 같은 값으로 본다(" E1001 " == "E1001")
        String employeeId = rawEmployeeId.strip();
        String courseId = rawCourseId.strip();

        return writeSerialized(() -> {
            // R2 과정 존재
            Course course = courseRepository.findById(courseId)
                    .orElseThrow(() -> new BusinessRuleException(ErrorCode.COURSE_NOT_FOUND));

            LocalDateTime now = labTime.now();
            // R4·R5·R6에 필요한 값은 각각 따로 조회한다.
            EnrollmentCheck check = new EnrollmentCheck(
                    now,
                    course.getEnrollmentDeadline(),
                    enrollmentRepository.existsByEmployeeIdAndCourseIdAndStatus(
                            employeeId, courseId, EnrollmentStatus.APPLIED),
                    enrollmentRepository.countByEmployeeIdAndStatus(employeeId, EnrollmentStatus.APPLIED),
                    enrollmentRepository.countByCourseIdAndStatus(courseId, EnrollmentStatus.APPLIED),
                    course.getCapacity());
            // R3 → R6
            rules.checkEnrollment(check).ifPresent(BusinessRuleException::throwFor);

            // 성공: 언제나 새 신청을 만든다(취소된 신청을 되살리지 않는다)
            return enrollmentRepository.save(Enrollment.apply(employeeId, courseId));
        });
    }

    /** 취소. C1 → C2 → C3 순서로 확인한다. 성공하면 좌석이 즉시 반환된다. */
    public Enrollment cancel(long enrollmentId) {
        return writeSerialized(() -> {
            // C1 신청 존재
            Enrollment enrollment = enrollmentRepository.findById(enrollmentId)
                    .orElseThrow(() -> new BusinessRuleException(ErrorCode.ENROLLMENT_NOT_FOUND));
            Course course = courseRepository.findById(enrollment.getCourseId())
                    .orElseThrow(() -> new IllegalStateException("신청이 가리키는 과정이 없습니다: " + enrollment.getCourseId()));

            // C2 → C3
            rules.checkCancellation(enrollment.getStatus(), course.getStartAt(), labTime.now())
                    .ifPresent(BusinessRuleException::throwFor);

            enrollment.cancel();
            return enrollmentRepository.save(enrollment);
        });
    }

    /** 내 신청 목록(취소됨 포함). 사원번호가 비어 있으면 MISSING_FIELD. */
    public List<MyEnrollment> findMyEnrollments(String rawEmployeeId) {
        if (rawEmployeeId == null || rawEmployeeId.isBlank()) {
            throw new BusinessRuleException(ErrorCode.MISSING_FIELD);
        }
        String employeeId = rawEmployeeId.strip();
        return readOnly(() -> {
            Map<String, String> titles = courseRepository.findAll().stream()
                    .collect(Collectors.toMap(Course::getCourseId, Course::getTitle));
            return enrollmentRepository.findByEmployeeIdOrderByIdAsc(employeeId).stream()
                    .map(e -> new MyEnrollment(e.getId(), e.getCourseId(),
                            titles.getOrDefault(e.getCourseId(), e.getCourseId()), e.getStatus()))
                    .toList();
        });
    }

    /** 과정 목록과 잔여석. */
    public List<CourseSummary> findCourses() {
        return readOnly(() -> {
            Map<String, Long> applied = enrollmentRepository.countAppliedByCourse().stream()
                    .collect(Collectors.toMap(EnrollmentRepository.CourseAppliedCount::getCourseId,
                            EnrollmentRepository.CourseAppliedCount::getAppliedCount));
            return courseRepository.findAllByOrderByCourseIdAsc().stream()
                    .map(c -> CourseSummary.of(c, applied.getOrDefault(c.getCourseId(), 0L)))
                    .toList();
        });
    }

    private <T> T writeSerialized(Supplier<T> work) {
        writeLock.lock();
        try {
            // 잠금을 쥔 채로 트랜잭션을 시작하고 커밋까지 끝낸다.
            return tx.execute(status -> work.get());
        } finally {
            writeLock.unlock();
        }
    }

    private <T> T readOnly(Supplier<T> work) {
        TransactionTemplate readTx = new TransactionTemplate(tx.getTransactionManager());
        readTx.setReadOnly(true);
        return readTx.execute(status -> work.get());
    }

    /** 내 신청 목록의 한 줄. */
    public record MyEnrollment(Long enrollmentId, String courseId, String courseTitle, EnrollmentStatus status) {
    }

    /** 과정 목록의 한 줄. */
    public record CourseSummary(Course course, long appliedCount) {
        static CourseSummary of(Course course, long appliedCount) {
            return new CourseSummary(course, appliedCount);
        }

        public long remainingSeats() {
            return course.getCapacity() - appliedCount;
        }
    }
}
