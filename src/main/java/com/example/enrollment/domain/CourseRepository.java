package com.example.enrollment.domain;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CourseRepository extends JpaRepository<Course, String> {

    List<Course> findAllByOrderByCourseIdAsc();
}
