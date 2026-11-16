package com.example.enrollment.web;

import com.example.enrollment.service.EnrollmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/courses")
@Tag(name = "과정", description = "교육 과정 목록과 잔여석")
public class CourseController {

    private final EnrollmentService enrollmentService;

    public CourseController(EnrollmentService enrollmentService) {
        this.enrollmentService = enrollmentService;
    }

    @GetMapping
    @Operation(summary = "과정 목록 조회",
            description = "모든 과정의 정원, 신청 인원, 잔여석, 신청 마감·시작·취소 기한을 과정 ID 순으로 돌려준다. "
                    + "잔여석은 신청됨 상태만 세어 계산한다.")
    @ApiResponse(responseCode = "200", description = "과정 목록",
            content = @Content(array = @ArraySchema(schema = @Schema(implementation = CourseResponse.class))))
    public List<CourseResponse> list() {
        return enrollmentService.findCourses().stream().map(CourseResponse::from).toList();
    }
}
