package com.example.enrollment;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

/**
 * 교육용 예제 애플리케이션: 사내 교육 과정 신청·취소.
 * 인증·권한이 없고 실제 운영용이 아니다.
 */
@SpringBootApplication
@ConfigurationPropertiesScan
public class CourseEnrollmentApplication {

    public static void main(String[] args) {
        SpringApplication.run(CourseEnrollmentApplication.class, args);
    }
}
