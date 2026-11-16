# 교육 신청·취소 앱 이해와 설계 구조 안내

이 문서는 **준비된 앱의 현재 소스·요구 문서·API 계약을 읽어 이번 소개를 위해 새로 작성한 안내**입니다. 개발 전에 작성된 원 설계서가 아닙니다. 예상 결과의 기준은 [요구와 합의 규칙](requirements.md)이며, 현재 구현과 실행 관찰은 그 기준과 비교할 대상입니다. 이 안내에는 완성 답안 테스트를 넣지 않았습니다.

## 1. 앱의 목적과 사용자

사원은 원하는 사내 교육 과정에 신청해 자리를 확보하고, 참석할 수 없으면 신청을 취소해 다른 사람에게 자리를 돌려줍니다. 교육 담당자의 요구 메모가 업무 배경이며, 별도의 담당자 관리 화면은 없습니다. 학생은 이미 준비된 Java/Spring Boot + React 앱을 사용하고 테스트합니다. 빈 프로젝트부터 앱 전체를 개발하는 실습이 아닙니다.

- 과정 목록: 이름·정원·잔여석·신청 마감·시작 시각을 보고 신청합니다.
- 내 신청 목록: 사원번호로 조회하고, 신청 ID·과정명·신청됨/취소됨 상태를 봅니다. 신청됨만 취소 버튼을 보여 줍니다.
- 결과 안내: 신청·취소의 성공 안내 또는 서버 오류 메시지를 화면 위쪽에서 확인합니다.
- 실습 정보: 고정 시계와 초기화 방법, API 문서 링크를 제공합니다.

사원번호 E1001 등은 교육용 값입니다. 로그인·인증·권한·사원 존재 확인은 없습니다. 실제 개인정보나 업무 서비스를 연결하지 않습니다.

## 2. 실제 화면으로 따라 읽는 정상 흐름

아래는 학생 공개판을 별도 사본에서 빌드하고 실행한 실제 화면입니다. 캡처는 빈 loopback 포트 54775를 사용했습니다. 본인 앱의 기본 주소는 `http://127.0.0.1:8080/`입니다. 화면 안에는 개인 폴더 경로나 계정 정보가 없습니다. 확대 이미지는 같은 원본의 해당 영역을 잘라 낸 것입니다.

### ① 사원번호 조회와 과정 목록

E1001로 조회합니다. 새 서버는 C-101~C-104 네 과정, 각 정원·잔여석 20, 신청 0건입니다. C-101은 “테스트 설계 기초”입니다.

![새 앱의 과정 목록과 빈 내 신청 목록](images/01-course-list-focus.png)

[전체 화면](images/01-course-list.png)에서 실습 기준 시각과 교육용 안내도 확인할 수 있습니다.

### ② 과정 신청과 내 신청 확인

C-101 행의 `신청` 버튼을 누릅니다. 실제 관찰은 성공 안내, C-101의 잔여석 **20 → 19**, 내 신청의 **신청됨(APPLIED)**입니다. API에서도 C-101 신청 인원 1, 잔여석 19, E1001 신청됨 한 건을 확인했습니다. 다른 과정의 잔여석은 20입니다.

![신청 성공 안내, 잔여석 19와 내 신청됨 기록](images/02-applied-focus.png)

[전체 화면](images/02-applied.png). 화면의 신청 ID 501은 이번 실행에서 얻은 값입니다. 테스트에서 특정 ID에 기대지 말고 응답이 반환한 ID를 사용합니다.

### ③ 취소 확인과 좌석 반환

내 신청의 `취소`를 누르면 실제 브라우저 확인 창에 **“정말 취소하시겠습니까?”**가 표시됩니다. 확인해야 취소 요청이 나갑니다. 창을 닫거나 취소하면 요청하지 않습니다. 이번 검증에서는 창에서 한 번 취소해 신청됨이 유지됨을 확인하고, 다시 눌러 확인한 뒤 아래 상태를 관찰했습니다. 확인 창 자체의 이미지 캡처는 브라우저 제한으로 확보하지 못했으며, 문구와 확인·취소 행동은 실제 도구 응답으로 확인했습니다.

![취소 성공 안내, 잔여석 20과 취소됨으로 남은 기록](images/04-cancelled-focus.png)

실제 관찰은 C-101 신청 인원 **1 → 0**, 잔여석 **19 → 20**, 같은 신청 ID의 **취소됨(CANCELLED)**입니다. 기록을 삭제하지 않습니다. [전체 화면](images/04-cancelled.png)도 제공합니다. 재신청 시 새 ID를 만드는 것은 요구·소스에서 확인한 규칙이며, 이 캡처 흐름에서 재신청까지 실행한 것은 아닙니다.

## 3. 전체 요구사항을 읽는 지도

소개에서 요약한 범위는 아래 전체 묶음입니다. 대표 실습만 골랐다고 나머지 요구가 제외되는 것은 아닙니다. **정확한 조건·응답 코드·메시지·값은 [requirements.md 전체 1~9절](requirements.md)을 읽습니다.** [openapi.json](openapi.json)은 실제 요청·응답의 필드와 형식을 확인할 계약 파일입니다.

| 원문 절 | 전체 범위와 이 안내에서의 연결 |
| --- | --- |
| 1 입력 메모와 합의 | 온라인 신청·취소, 정원, 중복, 마감·취소 기한, 개인 한도. Q1 정각 포함, Q2 취소 후 재신청, Q3 취소분 집계 제외, Q4 시작 24시간 전, Q5 신청됨 3개, Q6 첫 위반 순서, Q7 입력·과정 존재. 4절에서 규칙을 풉니다. |
| 2 용어 | 과정·신청, 과정별 신청 인원·사원별 보유 신청 수, 잔여석, 서버 요청 시각과 초 단위. 5절의 데이터와 연결합니다. |
| 3 신청 규칙 | R1~R7, 201 새 신청, 거절 시 데이터 불변. 4절에서 전체 조건과 순서를 풉니다. |
| 4 취소 규칙 | C1~C4, 200 상태 변경·즉시 좌석 반환·보유 수 감소, 재신청은 새 ID·신청 규칙 재적용. 4~6절과 연결합니다. |
| 5 API 계약 | 과정 조회, 신청, 취소, 내 신청 목록(취소됨 포함, 신청 ID 순), 실습 정보. 6절의 요청 흐름, 7절의 실제 파일과 연결합니다. |
| 6 화면 | 정원 마감 표시·버튼 비활성과 서버 재확인, 결과 안내, 내 신청·취소 확인 창, 기준 시각·인증 없음 안내. 2절의 실제 화면과 연결합니다. |
| 7 기준 데이터 | C-101~C-104의 이름·정원·신청 마감·시작·취소 기한 전체 표, 시작 신청 0건, 예제 사원 E1001~E1030. 원문의 네 과정 표를 함께 봅니다. |
| 8 추가 결정 | 아래 별도 목록의 입력·오류·사원번호·시계·ID·동시 처리 전 항목. |
| 9 유저스토리와 인수조건 | US-1 신청: AC1-1 정상 접수·좌석 감소, AC1-2 정원, AC1-3 중복·재신청, AC1-4 마감, AC1-5 한도, AC1-6 이유·순서. US-2 취소: AC2-1 기한·정각, AC2-2 좌석 반환·다른 사원 접수, AC2-3 이미 취소됨, AC2-4 기한 초과 안내. 자동 테스트 통과와 업무 담당자의 인수 승인은 다릅니다. |

### 요구 문서 8절의 추가 결정 전체

- 깨진 JSON·본문 없음·필드 타입 불일치: 400 `INVALID_REQUEST`. 빈 객체·필드 누락은 R1의 400 `MISSING_FIELD`와 구분합니다.
- 숫자가 아니거나 범위를 넘는 신청 ID: 400 `INVALID_REQUEST`.
- JSON이 아닌 Content-Type: 415 `UNSUPPORTED_MEDIA_TYPE`.
- 없는 주소·허용하지 않는 메서드·예상하지 못한 서버 오류: 각각 404 `NOT_FOUND`, 405 `METHOD_NOT_ALLOWED`, 500 `INTERNAL_ERROR`.
- 오류 응답은 `{code, message}`만 반환하고 내부 정보를 넣지 않습니다.
- 사원 존재는 확인하지 않습니다. 비어 있지 않은 사원번호를 받습니다. 별도의 업무상 길이 규칙도 없고 저장 열은 H2 VARCHAR 최대 길이(1,000,000,000자)로 매핑되어 51자 이상도 받습니다.
- 사원번호·과정 ID는 앞뒤 공백을 잘라 같은 값으로 봅니다. 내 신청 조회에 사원번호가 없으면 400 `MISSING_FIELD`입니다.
- 요청 시각은 초 미만을 버립니다. `18:00:00.900`은 `18:00:00`으로 비교합니다.
- 신청 ID는 501부터 증가하지만 업무 의미가 없으며 테스트의 고정 예상값으로 쓰지 않습니다.
- 같은 서버 안에서 신청·취소를 한 번에 하나씩 처리합니다. 여러 서버의 동시 처리까지 보장하지 않습니다.

추가 결정도 요구의 일부이며 원문의 정확한 메시지와 계약을 기준으로 확인합니다. 현재 화면에서 직접 관찰하지 못한 오류 조건까지 통과했다고 판단하지 않습니다.

## 4. 신청·취소의 명확한 규칙

### 신청: 모두 통과해야 새 신청을 만든다

1. **R1 필수 입력**: 사원번호와 과정 ID가 모두 있어야 합니다. null·빈 문자열·공백만 있으면 누락입니다.
2. **R2 과정 존재**: 과정 ID로 찾은 과정이 있어야 합니다.
3. **R3 신청 마감**: 서버 요청 시각이 신청 마감 일시와 같거나 이전이어야 합니다.
4. **R4 중복 금지**: 같은 사원·같은 과정의 신청됨이 없어야 합니다. 취소됨은 중복이 아닙니다.
5. **R5 개인 한도**: 그 사원의 모든 과정 신청됨을 합해 3개 미만이어야 합니다.
6. **R6 정원**: 이번 과정의 신청됨 수가 정원보다 작아야 합니다.
7. **R7 확인 순서**: R1 → R2 → R3 → R4 → R5 → R6. 여러 조건을 어겨도 첫 번째 위반 하나만 알려 줍니다.

성공은 201과 새 신청됨 기록, 신청 인원 +1·잔여석 −1입니다. 거절되면 신청이 생기지 않고 데이터가 그대로입니다. 필수 입력은 400, 과정 없음은 404, 나머지 업무 충돌은 409입니다. 정확한 오류 코드와 한국어 메시지는 원문 3절에 있습니다.

### 취소: 존재 → 상태 → 기한

- **C1**: 신청 ID가 가리키는 기록이 있어야 합니다(없으면 404).
- **C2**: 신청됨이어야 합니다. 이미 취소됨이면 409입니다.
- **C3**: 요청 시각 ≤ 시작 일시 − 24시간이어야 합니다. 정각도 포함하며 넘으면 409입니다.
- **C4**: C1 → C2 → C3 순서로 확인합니다.

성공은 200, 같은 기록의 상태가 취소됨으로 바뀌며 좌석이 즉시 돌아옵니다. 과정 신청 인원과 사원 보유 수가 각각 1 줄어듭니다. 거절되면 상태·인원은 그대로입니다. 다시 신청하면 **새 신청 ID**를 만들고 R1~R7을 처음부터 적용합니다.

### 두 기한과 서버 시계

C-101의 신청 마감은 `2026-12-01 18:00:00`, 시작은 `2026-12-03 09:00:00`, 취소 기한은 `2026-12-02 09:00:00`입니다. 모두 Asia/Seoul 기준이며 신청 마감과 취소 기한은 다른 시각입니다. 기본 서버의 요청 시각은 `2026-11-20 10:00:00`으로 고정되어 있습니다. 실제 오늘 날짜·교육 일정이 아닙니다. 테스트에서는 시계를 주입해 경계를 만듭니다.

## 5. 데이터와 설계 구조

```text
Course(과정)                         Enrollment(신청 한 건)
courseId ── 같은 과정 ID로 연결 ──< courseId
 title                              id(신청 ID), employeeId
 capacity                           status: APPLIED / CANCELLED
 enrollmentDeadline, startAt
```

위 그림은 업무상 연결을 설명합니다. 현재 JPA 구현은 `Enrollment.courseId` 문자열을 보관하며 `@ManyToOne` 객체 관계로 매핑하지 않습니다. 사원 테이블도 없습니다.

- `Course`는 과정 정보, `Enrollment`는 사원·과정·상태를 가진 신청 기록입니다.
- 과정의 신청 인원 = 그 과정의 `APPLIED` 수. 잔여석 = 정원 − 그 수입니다.
- 사원의 보유 신청 수 = 그 사원의 모든 과정 `APPLIED` 수입니다.
- 취소한 기록은 남지만 정원·개인 한도 집계에서 빠집니다. 잔여석을 따로 저장해 증감시키는 대신 조회 결과로 계산합니다.
- H2 메모리 DB는 서버 재시작 시 초기화됩니다. `SeedData`가 네 과정을 넣고 기본 신청은 없습니다.

역할은 다음과 같이 나뉩니다. 이것은 현재 구현을 읽어 정리한 구조이며 테스트가 모두 통과했다는 주장이 아닙니다.

```text
React App.jsx → api.js → HTTP 요청
                         ↓
web/Controller → service/EnrollmentService → rules/EnrollmentRules
                         ↓                    (주어진 값의 규칙 판단)
                 domain/Repository → H2
                         ↓
                 응답 → 화면 목록 다시 조회
```

서비스는 존재 확인·시간·집계 값 준비·저장을 조율합니다. `EnrollmentRules`는 Spring·DB·시계를 직접 사용하지 않고 입력값으로 규칙을 판단합니다. 같은 서버의 쓰기는 `ReentrantLock` 안에서 트랜잭션 시작부터 커밋까지 처리합니다. 읽기는 잠그지 않고 커밋된 값을 읽습니다. 여러 서버의 보장은 범위 밖입니다.

## 6. 화면에서 API·저장까지의 신청·취소 흐름

### 신청 한 건

1. `App.jsx`의 신청 버튼이 현재 사원번호와 과정 ID로 `api.enroll`을 부릅니다.
2. `api.js`는 `POST /api/enrollments`에 `{employeeId, courseId}` JSON을 보냅니다.
3. `EnrollmentController.enroll` → `EnrollmentService.enroll`로 전달합니다.
4. 서비스는 R1 확인·공백 정리, 과정 조회, 요청 시각과 중복·한도·정원 값을 준비합니다. `EnrollmentRules.checkEnrollment`가 R3~R6을 판단합니다.
5. 통과하면 `Enrollment.apply`로 새 신청을 저장하고 201 `{enrollmentId, courseId, status}`를 반환합니다.
6. 실패는 `ApiExceptionHandler`가 `{code, message}` 응답으로 바꿉니다. 화면은 오류 메시지를 표시합니다.
7. 화면은 성공·실패 후 과정 목록과 내 신청 목록을 다시 조회합니다. 성공 메시지는 화면에 정해진 문구이고, 오류 메시지는 서버 메시지를 사용합니다.

### 취소 한 건

1. `App.jsx`에서 취소 확인 창을 띄웁니다. 확인해야 `api.cancel`이 실행됩니다.
2. `POST /api/enrollments/{enrollmentId}/cancel`을 본문 없이 보냅니다.
3. 서비스가 신청 존재(C1)를 찾고 규칙 객체가 상태·기한(C2·C3)을 확인합니다.
4. 같은 신청의 상태를 `CANCELLED`로 저장한 뒤 200 `{enrollmentId, status}`를 반환합니다.
5. 화면에서 목록을 다시 조회하면 집계에서 취소분이 빠져 잔여석이 늘고, 취소됨 기록은 남습니다.

### 조회와 문서

- `GET /api/courses`: 정원·신청 인원·잔여석·신청 마감·시작·취소 기한. 과정 ID 순입니다.
- `GET /api/enrollments?employeeId=E1001`: 취소됨 포함, 신청 ID 순입니다. 신청이 없으면 빈 목록입니다.
- `GET /api/lab-info`: 앱 식별자, 시계 모드·시각·시간대·초기화 방법입니다.
- 실행 앱의 `/swagger-ui/index.html`과 `/v3/api-docs`에서 문서를 읽습니다. 오프라인 입력은 [openapi.json](openapi.json)입니다.

## 7. 실제 파일 지도

Java 경로의 공통 앞부분은 `src/main/java/com/example/enrollment/`입니다.

| 이해할 내용 | 실제 파일·위치 |
| --- | --- |
| 원래 짧은 메모 / 합의 규칙 / API 계약 | `docs/requirements-memo.md` / `docs/requirements.md` / `docs/openapi.json` |
| React 진입 / 화면 / HTTP 호출 / 스타일 | `frontend/src/main.jsx` / `App.jsx` / `api.js` / `styles.css` |
| 프론트 빌드·개발 프록시 / E2E 실행 | `frontend/package.json`, `vite.config.js`, `playwright.config.js` |
| Java 앱 시작 | `CourseEnrollmentApplication.java` |
| 과정·신청·실습 정보 API 입구 | `web/CourseController.java`, `EnrollmentController.java`, `LabInfoController.java` |
| 입력·응답·오류 형태 | `web/EnrollmentRequest.java`, `CourseResponse.java`, `MyEnrollmentResponse.java`, `EnrollmentResponse.java`, `CancelResponse.java`, `LabInfoResponse.java`, `ErrorResponse.java` |
| 오류 응답 변환 | `web/ApiExceptionHandler.java`, `service/BusinessRuleException.java`, `rules/ErrorCode.java` |
| 신청·취소 처리·집계 준비·저장·동시 처리 | `service/EnrollmentService.java` |
| 주어진 조건의 규칙 판단과 입력 자료형 | `rules/EnrollmentRules.java`, `EnrollmentCheck.java` |
| 과정·신청·상태와 조회 | `domain/Course.java`, `Enrollment.java`, `EnrollmentStatus.java`, `CourseRepository.java`, `EnrollmentRepository.java` |
| 시계와 초 단위 변환 | `config/LabClockConfig.java`, `LabProperties.java`, `service/LabTime.java` |
| 기준 데이터·API 문서 설정 | `config/SeedData.java`, `OpenApiConfig.java` |
| 메모리 DB·loopback 주소·기본 포트·시계 설정 | `src/main/resources/application.properties` |
| Java 의존성과 화면을 jar에 넣는 빌드 | `pom.xml`, `mvnw` / `mvnw.cmd` |

## 8. 실행과 테스트 위치

### 기본 실행 순서

[README의 준비 환경과 앱 실행](../README.md#준비-환경과-앱-실행)을 기준으로 합니다. `pom.xml`, `mvnw`, `frontend`가 있는 곳이 프로젝트 루트입니다.

```bash
# 프로젝트 루트에서
java -version
node --version
cd frontend
npm ci
npm run build
cd ..
./mvnw clean package
java -jar target/course-enrollment-testing-0.1.0.jar
```

Windows PowerShell에서는 Maven 명령을 `.\mvnw.cmd clean package`로 바꿉니다. JDK 21, Node.js 22.12.0 이상을 준비합니다. 화면 빌드는 `frontend/dist`, jar는 `target/course-enrollment-testing-0.1.0.jar`에 생깁니다. `pom.xml`이 `frontend/dist`를 jar의 정적 자원으로 넣으므로 기본 실행에는 별도 Vite 서버가 필요하지 않습니다. `npm run dev`는 개발용 별도 방식이며 앞 명령의 필수 단계가 아닙니다.

터미널에 앱 시작 로그가 나온 뒤 `http://127.0.0.1:8080/`에서 제목·기준 시각·네 과정이 보이고 조회가 되면 화면이 준비된 것입니다. 그 터미널은 켜 둡니다. 8080이 사용 중이면 **본인 앱에만** `--server.port=8081`을 붙이고 접속 주소도 8081로 바꿉니다. 다른 사람의 서버를 종료하지 않습니다. 종료는 본인이 실행한 터미널의 Ctrl+C를 사용합니다. 재시작하면 신청 데이터가 사라집니다.

### 학생이 테스트를 만들 위치

Java 공통 앞부분: `src/test/java/com/example/enrollment/`.

| 단원 | 입력·생성 위치 | 실행·읽을 결과 |
| --- | --- | --- |
| 1 | 앱 화면·메모, 이 안내. 확인하고 싶은 상황과 이유 | 정상 흐름의 안내·상태·잔여석을 직접 관찰 |
| 2 | `docs/requirements.md`의 해당 절 → AI가 케이스 표 생성. `docs/worksheets/02-테스트케이스-설계.md`는 선택 참고 | 마감 시각·예상 응답·데이터 변화를 요구에 대조 |
| 3 | `practice/RulesPracticeTest.java`: Spring·DB 없이 규칙 객체. TDD는 교재의 새 연습 기능 | `./mvnw test -Dgroups=unit` → 콘솔·`target/surefire-reports/` |
| 4 | `practice/ApiPracticeTest.java`: Spring·DB를 잇는 MockMvc, `practice/AcceptancePracticeTest.java`: 실제 HTTP 준비 틀 | `./mvnw test -Dgroups=api` 등 교재의 해당 명령 |
| 4 화면 | `frontend/e2e/practice-flow.spec.js`: Playwright 브라우저 흐름. `helpers.js`는 준비 도구 | jar·Chromium 준비 후 `frontend`에서 `npm run e2e` |
| 5 | `lab5-change.patch` 분석, 기존 생성 테스트와 실행 기록 | 변경 전 기준선 → 변경 뒤 → 필요한 수정 뒤 재실행. 미실행은 그대로 보고 |

`practice/EnvironmentCheckTest.java`는 환경 확인이고 나머지 실습 틀은 비활성 출발점입니다. `support/`에는 테스트 시계·데이터·API 호출 도구가 있습니다. Maven 전체 실행은 Playwright를 실행하지 않습니다. `@Disabled`·`test.fixme`의 건너뜀을 기능 검증 통과로 세지 않습니다. 현재 첫 요청과 안전 조건은 [AI 요청문](AI-요청문.md)과 [실습 진행 안내](실습-진행-안내.md)를 따릅니다.

## 9. 이번 확인의 범위

학생 공개판의 별도 실행 사본에서 React 빌드, Maven 패키징, jar 실행, 브라우저 조회→신청→취소 확인→취소, API의 상태·집계 값을 확인했습니다. 기본 Maven 실행에서 환경 확인 1건 통과·빈 실습 4건 건너뜀을 확인했으며 이것을 업무 전체 테스트로 세지 않습니다. 실제 화면 캡처와 로그를 근거로 한 정상 흐름 확인입니다.

이번 작업은 AI로 새 테스트를 생성하는 전체 실습, 모든 오류·경계·동시 조건의 실행, Playwright 학생 테스트 전체 실행, TDD·변경 패치의 종단 실습, 학생 PC 재현, 강사의 낭독·시간 리허설을 수행한 것이 아닙니다. 화면 확인과 자동 테스트 생성 실습 검증은 구분합니다.


## 10. 요구·화면·소스·이후 테스트의 대응

단원 1에서 앱 목적·실행·실제 화면과 전체 레이어를 먼저 보고, 다음의 큰 요구별 책임을 이해한 뒤 테스트 이론으로 들어갑니다. 번호는 암기 목록이 아니라 원문 위치를 찾는 표식입니다. Q는 질문과 합의, R은 신청 규칙, C는 취소 규칙, US는 유저스토리, AC는 인수조건입니다. 교재의 인수조건 문장은 requirements.md 9절에서 그대로 가져옵니다.

| 큰 요구·원문 | 화면에서 보는 것 | 실제 소스의 책임 | 이후 테스트와 연결 |
| --- | --- | --- | --- |
| 과정 조회: 5·6절, 잔여석 정의 2절 | 과정·정원·잔여석·신청 마감·시작 | `frontend/src/App.jsx` → `api.js` → `web/CourseController.java` → `service/EnrollmentService.java` → `domain/CourseRepository.java`와 `EnrollmentRepository.java`. 신청됨 수를 조회해 잔여석 계산 | 단원 4 API·화면의 관찰값, 단원 5 조회 변경 영향 |
| 내 신청 조회: 5·6절 | 사원번호 조회, 취소됨 포함 목록 | `App.jsx` → `api.js` → `web/EnrollmentController.java` → `service/EnrollmentService.java` → `domain/EnrollmentRepository.java`. 사원번호 누락 확인·공백 정리, ID 순 목록 | 단원 4 신청·취소 후 기록 확인, 단원 5 상태 조건 회귀 |
| 실습 정보: 5·6절 | 기준 시각·고정 시계·초기화 안내 | `api.js` → `web/LabInfoController.java` → `service/LabTime.java`와 `config/LabProperties.java`. **신청 서비스·규칙 객체·DB를 거치지 않음** | 경계 실습의 시계 조건 확인. 업무 규칙 전체 검증으로 세지 않음 |
| 신청: R1~R7·AC1-1~6 | 접수·거절 안내, 새 신청됨, 잔여석 감소 | `App.jsx` → `api.js` → `web/EnrollmentController.java` → `service/EnrollmentService.java`. 서비스가 입력·존재·집계·시각을 준비하고 `rules/EnrollmentRules.java`가 조건 판단. 통과 시 `domain/Enrollment.java`·`EnrollmentRepository.java`로 새 기록 | 단원 2 마감 케이스, 단원 3 규칙 단위 검사, 단원 4 API·화면, 단원 5 중복·한도 관련 조회 회귀 |
| 취소: C1~C4·AC2-1~4 | 확인 창·취소됨 기록·좌석 반환 | `App.jsx`가 확인 창 수락 후 `api.js` 호출 → Controller → Service가 신청 존재 조회 → Rules가 상태·기한 판단 → 기존 Enrollment 상태 변경·Repository 저장. 재조회 집계에서 빠짐 | 단원 2 취소 기한 따라 하기, 단원 3 별도 TDD 연습, 단원 4 좌석 반환 API·화면, 단원 5 재신청 관련 회귀 |
| 잘못된 요청·오류 안내: 8절 | 오류 메시지, 내부 정보 없는 응답 | HTTP 형식·상태 처리는 Spring 웹 계층과 `web/ApiExceptionHandler.java`, 업무 충돌은 서비스·규칙의 첫 위반. 화면 `api.js`가 서버 오류 메시지를 전달 | 각 테스트 범위에서 입력·응답 계약 확인. 모든 오류가 업무 규칙 객체를 거친다고 가정하지 않음 |

### 질문 번호와 규칙의 연결

- Q1 마감 정각 → R3 → AC1-4. Q2 취소 후 재신청 → R4 → AC1-3.
- Q3 취소분 집계 제외 → R6, 취소 성공 → AC1-2·AC2-2.
- Q4 “하루 전”은 24시간 → C3 → AC2-1·AC2-4.
- Q5 신청됨 세 개 → R5 → AC1-5. Q6 첫 위반 순서 → R7 → AC1-6.
- Q7 입력과 없는 과정 → R1·R2. 취소의 존재·상태·기한과 순서는 C1~C4에 따로 있습니다.

### 큰 레이어와 다른 경로

```text
React 화면·api.js → web/Controller → service/EnrollmentService ↔ domain/Repository ↔ H2
                                               ↕
                                    rules/EnrollmentRules
                           (서비스가 준비한 값으로 판단, DB·시계 직접 조회 없음)

별도 실습 정보: api.js → web/LabInfoController → LabTime·LabProperties
```

규칙 객체가 저장소를 통과해 DB를 읽는 구조가 아닙니다. 조회 요청은 데이터를 읽고 계산하며, 신청·취소 요청은 필요한 상태를 읽어 판단한 뒤 저장합니다. 전체 구조도를 먼저 이해하고 조회·신청·취소의 대표 경로를 나누어 읽습니다. 함수별 문법이나 클래스의 모든 필드를 익히는 수업으로 범위를 넓히지 않습니다.
