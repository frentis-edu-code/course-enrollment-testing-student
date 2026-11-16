# 교육 과정 신청·취소 — AI 테스트 실습 시작판

사원이 교육 과정을 조회·신청하고 내 신청 목록에서 취소하는 **Java/Spring Boot + React 웹 앱**입니다. 취소하면 좌석이 돌아오고 다른 사원이 신청할 수 있습니다.

앱은 준비되어 있습니다. 학생은 요구를 읽어 **테스트케이스와 테스트를 AI로 만들고 검토·실행**합니다. 완성 답안 테스트와 강사 저장소의 이력은 포함하지 않습니다. 처음 받은 저장소의 실행 테스트는 환경 확인 하나뿐이고, 업무 규칙을 확인하는 테스트는 실습에서 새로 만듭니다. 예전 시작판의 미작성 틀은 `templates/`에 읽기 참고로만 둡니다.

> 인증·권한이 없는 교육용 예제입니다. 실제 업무 데이터나 서비스를 연결하지 마세요. 사원번호 E1001 등은 예제 값이며 외부 API 키는 사용하지 않습니다.

## 앱 전체 소개 먼저 읽기

[앱 이해와 설계 구조 안내](docs/design-guide.md)에서 실제 실행 화면 → 실행 방법 → 전체 요구사항 → 화면·API·서비스·규칙·DB → 테스트 위치를 읽습니다. 이 안내는 현재 소스를 대조해 새로 작성했으며 원래의 사전 설계서가 아닙니다. 전체 판단 기준은 [요구와 합의 규칙](docs/requirements.md)입니다. 메모의 질문과 합의, 신청·취소 규칙, API 계약, 화면, 기준 데이터, 추가 결정, 유저스토리와 인수조건이 한 문서에 있습니다.

## 전체 자료 받기

학생 저장소: https://github.com/frentis-edu-code/course-enrollment-testing-student

```bash
git clone https://github.com/frentis-edu-code/course-enrollment-testing-student.git
cd course-enrollment-testing-student
```

Git 없이 받으려면 **Code → Download ZIP**을 누르고 압축을 풉니다. 풀린 프로젝트 폴더에서 시작합니다. [실습 진행 안내](docs/실습-진행-안내.md)에서 현재 단원을 찾고 [AI 요청문](docs/AI-요청문.md)을 복사합니다. 새 AI 대화를 열 때는 먼저 공통 준비 요청으로 AI가 [AI 공통 안내](docs/AI-공통-안내.md)를 읽게 합니다. `docs/worksheets/`는 현재 실습의 선택 참고 양식이며 먼저 채울 필요가 없습니다. API 이후의 [Cucumber 강사 시연](docs/Cucumber-강사시연.md)은 문장과 실행 코드를 연결하는 짧은 관찰 활동입니다. 학생용 의존성이나 필수 실습을 추가하지 않습니다.

## 준비 환경과 앱 실행

- JDK **21**: `java -version`으로 확인하고 JAVA_HOME 또는 PATH를 설정합니다.
- Node.js **22.12.0 이상**: npm으로 React 빌드와 화면 테스트를 준비합니다.
- Maven은 포함된 Wrapper(`mvnw` / `mvnw.cmd`)를 씁니다. 처음에는 Maven/npm 의존성을 받으므로 인터넷이 필요합니다.
- 화면 E2E에는 Playwright용 Chromium, 단원 5 패치에는 `patch` 도구가 필요합니다. 실습 중 준비가 없으면 임의 설치·설정 변경 대신 수업 담당자에게 알립니다.

**macOS / Linux — 프로젝트 루트에서:**

```bash
cd frontend
npm ci
npm run build
cd ..
./mvnw clean package
java -jar target/course-enrollment-testing-0.1.0.jar
```

**Windows PowerShell — 프로젝트 루트에서:**

```powershell
cd frontend
npm ci
npm run build
cd ..
.\mvnw.cmd clean package
java -jar target\course-enrollment-testing-0.1.0.jar
```

- 화면: http://127.0.0.1:8080/
- Swagger UI: http://127.0.0.1:8080/swagger-ui/index.html
- 실행 앱의 API 문서: http://127.0.0.1:8080/v3/api-docs
- 파일로 읽는 API 계약: [docs/openapi.json](docs/openapi.json)

8080이 사용 중이면 본인 앱에 `--server.port=8081`을 붙일 수 있습니다. 다른 사람의 서버는 종료하지 않습니다.

화면에서 E1001 조회 → C-101 신청 → 내 신청의 취소 확인 창 수락을 해 봅니다. 새 앱의 잔여석은 **20 → 19 → 20**으로 바뀌어야 합니다. 기존 데이터가 있으면 현재 값의 증감을 봅니다.

요청 시각은 **2026-11-20 10:00:00(한국 시간)**으로 고정됩니다. 교육용 기준 데이터이지 수업 일정이 아닙니다. 서버 재시작 시 신청 데이터는 사라지고 과정 4개만 남습니다.

## 파일 지도

| 위치 | 역할 |
| --- | --- |
| [docs/requirements-memo.md](docs/requirements-memo.md) | 다듬기 전 업무 메모 |
| [docs/requirements.md](docs/requirements.md) | 합의 규칙·API 계약·인수조건. 예상값의 근거 |
| [docs/design-guide.md](docs/design-guide.md) | 실제 실행 화면·전체 요구 지도·현재 소스의 설계 구조·실행·테스트 위치 |
| [docs/AI-공통-안내.md](docs/AI-공통-안내.md) | AI가 먼저 읽는 짧은 작업 안내. 테스트 위치·이름·태그, 바꾸지 않는 파일, 결과 보고 방법 |
| [docs/AI-요청문.md](docs/AI-요청문.md) | 공통 준비 요청과 실습별 복사용 요청, 만들 파일 예시와 사람이 볼 것 |
| `src/main/java/com/example/enrollment/web/` | HTTP 요청·응답 |
| `src/main/java/com/example/enrollment/service/` | 신청·취소 처리 |
| `src/main/java/com/example/enrollment/rules/` | EnrollmentRules·입력 자료형 EnrollmentCheck |
| `src/main/java/com/example/enrollment/domain/` | 과정·신청·저장소 |
| [frontend/src/App.jsx](frontend/src/App.jsx), [frontend/src/api.js](frontend/src/api.js) | React 화면·API 호출 |
| `src/test/java/com/example/enrollment/practice/` | Java 테스트를 새로 만들 자리. 처음에는 환경 확인 `EnvironmentCheckTest.java`만 있음 |
| `src/test/java/com/example/enrollment/support/` | API·데이터·시계 준비 도구. 실습에서 변경하지 않음 |
| `frontend/e2e/` | Playwright 화면 테스트를 새로 만들 자리. 처음에는 준비 도구 `helpers.js`만 있음 |
| `templates/java/`, `templates/e2e/` | 예전 시작판의 미작성 틀을 원본 그대로 둔 읽기 참고. 실행 탐색 위치 밖이며 활성 폴더로 복사하지 않음 |
| [lab5-change.patch](lab5-change.patch) | 단원 5에서 먼저 분석할 변경 입력 |

## 다섯 단원에서 할 일

| 단원 | 읽을 실제 파일 | AI에 요청할 일 | 만들 파일 예시(아직 없음) | 결과에서 확인할 것 |
| --- | --- | --- | --- | --- |
| 1. 테스트 기본 개념과 AI 활용 | 실제 화면과 `docs/design-guide.md`, `docs/requirements.md`의 신청·취소 규칙 | 신청·취소에서 확인할 상황과 근거 | 없음(대화로 받음) | 한 항목의 근거와 먼저 볼 이유. 정상 신청·취소의 상태·잔여석 |
| 2. AI 테스트케이스 설계 | `docs/requirements.md`의 마감 정각 합의·신청 마감 규칙·C-101 마감 시각·마감 인수조건 | 마감 정각과 1초 뒤의 케이스 표 | 없음(대화로 받음) | 예상 결과와 신청 인원 변화가 규칙과 맞는가. 따라 하기는 취소 기한 규칙과 C-101 시작 시각 |
| 3. 단위 테스트와 TDD | 요구 문서의 정원·취소 기한 규칙, `rules/EnrollmentRules.java`, `rules/EnrollmentCheck.java` | 정원이 한 자리 남았을 때와 찼을 때의 단위 테스트, 이어 취소 기한 연습 기능의 실패 테스트 | practice의 `RulesCapacityTest.java`, `CancellationDeadline.java`·`CancellationDeadlineTest.java` | Spring·DB 없는 검사, 정원 규칙에서 온 예상값과 실제 실행. TDD 빨강→초록→정리 후 재통과 |
| 4. API·통합·React 화면 테스트 | 요구 문서의 취소 규칙·화면 설명, `support/ApiTestSupport.java`, `support/TestData.java`, `frontend/e2e/helpers.js` | 취소한 좌석의 다른 사원 접수 API 테스트, 신청·취소 화면 E2E | practice의 `EnrollmentApiTest.java`, `frontend/e2e/enrollment-flow.spec.js` | API 응답·인원·잔여석. 화면 확인 창·목록 대기·안내·상태 |
| 5. 회귀와 결과 정리 | `lab5-change.patch`, 요구 문서의 중복 신청·개인 한도 규칙과 재신청, 지금 있는 테스트와 실제 실행 기록 | 먼저 영향 분석만. 범위 확인 후 재검증과 근거표·보고 | 새 파일 대신 단원 4의 API 테스트에 재신청 테스트 보강 | 변경 전 기준선→패치 뒤→수정 뒤 결과. 미실행과 남은 위험 |

`practice`와 `support`는 `src/test/java/com/example/enrollment/` 아래입니다. 만들 파일 이름은 예시이며, AI가 다른 이름으로 만들었으면 실제 이름으로 읽습니다. 복사 요청은 [AI 요청문](docs/AI-요청문.md), 안전 조건·누적 파일 지도·복구는 [진행 안내](docs/실습-진행-안내.md)를 따릅니다.

## 테스트 실행과 결과 읽기

생성된 코드를 읽고 허용 파일만 바뀌었는지 확인한 뒤 프로젝트 루트에서 실행합니다.

```bash
./mvnw test -Dgroups=unit
./mvnw test -Dgroups=api
./mvnw test -Dgroups=unit,api,acceptance
./mvnw test
```

Windows는 `./mvnw` 대신 `.\mvnw.cmd`를 씁니다. 콘솔과 `target/surefire-reports/`에서 신규 이름·실행 수·실패·오류·건너뜀을 확인합니다. 아직 해당 태그의 테스트를 만들지 않았으면 실행 수가 0일 수 있으며 이는 통과가 아닙니다. 환경 확인 통과만으로 앱을 검증했다고 하지 않습니다.

MockMvc API 검사는 Spring 컨텍스트·DB를 연결하지만 실제 HTTP 서버는 띄우지 않습니다. 규칙 단위 검사와 React 화면 E2E와 구별합니다.

화면 E2E는 새 jar와 브라우저를 준비한 뒤 별도로 실행합니다. 사전 준비 단계에서:

```bash
cd frontend
npx playwright install chromium
npm run e2e
```

`frontend/e2e`에 새 `*.spec.js`를 만들기 전에는 실행할 화면 테스트가 없습니다. Maven 전체 실행은 Playwright를 실행하지 않습니다. E2E 기본 설정은 빈 포트에 jar를 새로 띄웁니다. `E2E_BASE_URL`로 기존 서버를 쓰거나 여러 테스트를 실행하면 서버 데이터의 준비·정리를 별도로 확인해야 합니다.

## 사용 범위와 검증 상태

- 학습 예제의 동시 처리는 서버 하나 안에서만 다룹니다. 실운영용 인증·권한·여러 서버 보장은 없습니다.
- 현재 요청문으로 새 AI 코드를 생성하고 Maven/Playwright 시험을 통과시켰다는 기록은 제공하지 않습니다. 실제 결과는 본인의 실행에서 확인합니다.
- 별도의 프로젝트 라이선스를 임의로 추가하지 않았습니다. 저장소 공개를 새 라이선스 부여로 해석하지 않으며 Maven Wrapper의 기존 라이선스 고지는 유지합니다.
