package com.example.enrollment.web;

import com.example.enrollment.service.EnrollmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/enrollments")
@Tag(name = "신청", description = "교육 과정 신청·취소와 내 신청 목록")
public class EnrollmentController {

    static final String EX_MISSING_FIELD = "{\"code\":\"MISSING_FIELD\",\"message\":\"필수 정보를 입력해 주세요.\"}";
    static final String EX_INVALID_REQUEST = "{\"code\":\"INVALID_REQUEST\",\"message\":\"요청 형식이 올바르지 않습니다.\"}";
    static final String EX_COURSE_NOT_FOUND = "{\"code\":\"COURSE_NOT_FOUND\",\"message\":\"존재하지 않는 과정입니다.\"}";
    static final String EX_CLOSED = "{\"code\":\"ENROLLMENT_CLOSED\",\"message\":\"신청 기간이 지났습니다.\"}";
    static final String EX_DUPLICATE = "{\"code\":\"DUPLICATE_ENROLLMENT\",\"message\":\"이미 신청한 과정입니다.\"}";
    static final String EX_LIMIT = "{\"code\":\"ENROLLMENT_LIMIT\",\"message\":\"동시에 신청할 수 있는 과정은 최대 3개입니다.\"}";
    static final String EX_FULL = "{\"code\":\"COURSE_FULL\",\"message\":\"정원이 마감되었습니다.\"}";
    static final String EX_ENROLLMENT_NOT_FOUND = "{\"code\":\"ENROLLMENT_NOT_FOUND\",\"message\":\"신청 내역을 찾을 수 없습니다.\"}";
    static final String EX_ALREADY_CANCELLED = "{\"code\":\"ALREADY_CANCELLED\",\"message\":\"이미 취소된 신청입니다.\"}";
    static final String EX_CANCEL_CLOSED = "{\"code\":\"CANCEL_CLOSED\",\"message\":\"취소 가능 기간이 지났습니다.\"}";
    static final String EX_UNSUPPORTED = "{\"code\":\"UNSUPPORTED_MEDIA_TYPE\",\"message\":\"요청 본문은 application/json 형식이어야 합니다.\"}";

    private final EnrollmentService enrollmentService;

    public EnrollmentController(EnrollmentService enrollmentService) {
        this.enrollmentService = enrollmentService;
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "과정 신청",
            description = """
                    사원이 과정을 신청한다. R1 필수 입력 → R2 과정 존재 → R3 신청 마감 → R4 중복 금지 → \
                    R5 개인 한도(신청됨 3개 미만) → R6 정원 순서로 확인하고, 처음 위반한 규칙 하나만 응답한다(R7).
                    거절되면 데이터가 바뀌지 않는다. 성공하면 언제나 새 신청 ID를 만든다(취소 후 재신청 포함).
                    요청 시각은 서버 시계 기준이며 초 단위로 비교한다(마감 정각은 접수).""")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(required = true, content = @Content(
            schema = @Schema(implementation = EnrollmentRequest.class),
            examples = {
                    @ExampleObject(name = "정상", value = "{\"employeeId\":\"E1001\",\"courseId\":\"C-101\"}"),
                    @ExampleObject(name = "R1 공백 사원번호", value = "{\"employeeId\":\"   \",\"courseId\":\"C-101\"}"),
                    @ExampleObject(name = "R2 없는 과정", value = "{\"employeeId\":\"E1001\",\"courseId\":\"C-999\"}")
            }))
    @ApiResponse(responseCode = "201", description = "신청 완료(신청됨 상태의 새 신청)",
            content = @Content(schema = @Schema(implementation = EnrollmentResponse.class),
                    examples = @ExampleObject(name = "APPLIED",
                            value = "{\"enrollmentId\":501,\"courseId\":\"C-101\",\"status\":\"APPLIED\"}")))
    @ApiResponse(responseCode = "400", description = "R1 필수 입력 누락, 또는 JSON 형식 오류",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class), examples = {
                    @ExampleObject(name = "MISSING_FIELD", value = EX_MISSING_FIELD),
                    @ExampleObject(name = "INVALID_REQUEST", value = EX_INVALID_REQUEST)}))
    @ApiResponse(responseCode = "404", description = "R2 과정 없음",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class), examples = {
                    @ExampleObject(name = "COURSE_NOT_FOUND", value = EX_COURSE_NOT_FOUND)}))
    @ApiResponse(responseCode = "409", description = "R3 마감, R4 중복, R5 개인 한도, R6 정원",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class), examples = {
                    @ExampleObject(name = "ENROLLMENT_CLOSED", value = EX_CLOSED),
                    @ExampleObject(name = "DUPLICATE_ENROLLMENT", value = EX_DUPLICATE),
                    @ExampleObject(name = "ENROLLMENT_LIMIT", value = EX_LIMIT),
                    @ExampleObject(name = "COURSE_FULL", value = EX_FULL)}))
    @ApiResponse(responseCode = "415", description = "JSON이 아닌 본문",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class), examples = {
                    @ExampleObject(name = "UNSUPPORTED_MEDIA_TYPE", value = EX_UNSUPPORTED)}))
    public EnrollmentResponse enroll(@RequestBody EnrollmentRequest request) {
        return EnrollmentResponse.from(enrollmentService.enroll(request.employeeId(), request.courseId()));
    }

    @PostMapping("/{enrollmentId}/cancel")
    @Operation(summary = "신청 취소",
            description = """
                    신청을 취소한다. C1 신청 존재 → C2 상태(신청됨) → C3 취소 기한 순서로 확인한다(C4).
                    취소 기한은 과정 시작 일시 − 24시간이며 그 시각까지 취소할 수 있다.
                    성공하면 좌석이 즉시 반환되고 사원의 보유 신청 수도 1 줄어든다. 요청 본문은 없다.""")
    @ApiResponse(responseCode = "200", description = "취소 완료",
            content = @Content(schema = @Schema(implementation = CancelResponse.class),
                    examples = @ExampleObject(name = "CANCELLED",
                            value = "{\"enrollmentId\":501,\"status\":\"CANCELLED\"}")))
    @ApiResponse(responseCode = "400", description = "신청 ID가 숫자가 아님",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class), examples = {
                    @ExampleObject(name = "INVALID_REQUEST", value = EX_INVALID_REQUEST)}))
    @ApiResponse(responseCode = "404", description = "C1 신청 없음",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class), examples = {
                    @ExampleObject(name = "ENROLLMENT_NOT_FOUND", value = EX_ENROLLMENT_NOT_FOUND)}))
    @ApiResponse(responseCode = "409", description = "C2 이미 취소됨, C3 취소 기한 지남",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class), examples = {
                    @ExampleObject(name = "ALREADY_CANCELLED", value = EX_ALREADY_CANCELLED),
                    @ExampleObject(name = "CANCEL_CLOSED", value = EX_CANCEL_CLOSED)}))
    public CancelResponse cancel(
            @Parameter(description = "신청 ID", example = "501") @PathVariable("enrollmentId") long enrollmentId) {
        return CancelResponse.from(enrollmentService.cancel(enrollmentId));
    }

    @GetMapping
    @Operation(summary = "내 신청 목록 조회",
            description = "사원번호의 신청을 신청 ID 순으로 돌려준다. 취소됨 신청도 포함한다. "
                    + "사원번호가 없거나 공백뿐이면 400 MISSING_FIELD. 사원 존재 여부는 확인하지 않는다(없으면 빈 목록).")
    @ApiResponse(responseCode = "200", description = "내 신청 목록",
            content = @Content(array = @ArraySchema(schema = @Schema(implementation = MyEnrollmentResponse.class))))
    @ApiResponse(responseCode = "400", description = "사원번호 누락",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class), examples = {
                    @ExampleObject(name = "MISSING_FIELD", value = EX_MISSING_FIELD)}))
    public List<MyEnrollmentResponse> myEnrollments(
            @Parameter(description = "사원번호", example = "E1001")
            @RequestParam(name = "employeeId", required = false) String employeeId) {
        return enrollmentService.findMyEnrollments(employeeId).stream().map(MyEnrollmentResponse::from).toList();
    }
}
