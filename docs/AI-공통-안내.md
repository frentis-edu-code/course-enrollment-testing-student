# AI 공통 안내

AI 도구가 실습을 시작할 때 먼저 읽는 짧은 프로젝트 안내입니다. [AI 요청문](AI-요청문.md)의 **공통 준비 요청**이 이 파일을 읽게 합니다. 사람이 읽어도 됩니다.

## 이 프로젝트

- 사원이 교육 과정을 신청하고 취소하는 앱입니다. 백엔드는 Java 21·Spring Boot 3.5, 화면은 React입니다. 앱은 이미 완성되어 있고, 실습에서는 요구에 맞는 테스트를 만들고 실행합니다.
- 테스트의 예상 결과는 현재 코드의 동작이 아니라 `docs/requirements.md`의 규칙·합의·인수조건에서 정합니다. 문서로 정할 수 없으면 추측하지 말고 질문으로 남깁니다.

## 테스트를 만드는 위치

Java 테스트의 위치는 `src/test/java/com/example/enrollment/practice/`입니다. 처음에는 환경 확인 `EnvironmentCheckTest.java`만 있습니다.

| 만들 테스트 | 위치와 형식 | 실행(프로젝트 루트) |
| --- | --- | --- |
| 규칙 단위 테스트 | practice의 새 `*Test.java`, `@Tag("unit")`. Spring·DB 없이 `new EnrollmentRules()`를 직접 호출하고 시각은 고정값을 넣는다 | `./mvnw test -Dgroups=unit` |
| TDD 연습 | practice의 새 연습 판단 클래스와 새 `*Test.java`, `@Tag("unit")`. 기존 앱이나 규칙 객체를 고치지 않고 연습 클래스를 직접 호출한다 | `./mvnw test -Dgroups=unit` |
| API 테스트 | practice의 새 `*Test.java`, `@Tag("api")`, `support/ApiTestSupport` 상속. MockMvc로 웹 계층부터 DB까지 연결하며 실제 HTTP 서버는 띄우지 않는다 | `./mvnw test -Dgroups=api` |
| 인수 시나리오(실제 HTTP) | practice의 새 `*Test.java`, `@Tag("acceptance")`, `support/RealHttpTestSupport` 상속 | `./mvnw test -Dgroups=acceptance` |
| 화면 E2E | `frontend/e2e/`의 새 `*.spec.js`. `helpers.js`의 도구를 재사용한다 | `frontend`에서 `npm run e2e` |

- 이 안내에서는 Maven의 기본 테스트 파일명 형식 중 `src/test/java` 아래 `*Test.java`를, Playwright의 기본 파일명 형식 중 `frontend/e2e` 아래 `*.spec.js`를 사용합니다. 위 예시 위치·이름과 태그를 함께 맞추고, 실행 결과에 새 테스트 이름과 실행 수가 나타나는지 확인합니다.
- TDD 연습의 판단 클래스는 앱 코드(`src/main`)가 아니라 practice의 새 연습 클래스로 둡니다. 실패 테스트 단계에서는 컴파일은 되지만 요구 동작이 아직 없어 검증이 실패하게 만듭니다.
- 신청 ID처럼 실행할 때마다 달라질 수 있는 값은 응답에서 얻어 쓰고 예상값으로 고정하지 않습니다.
- 화면 E2E는 빌드된 jar와 Chromium이 준비되어 있어야 합니다. `frontend/e2e`에 `*.spec.js`가 하나도 없으면 실행할 테스트가 없다는 뜻이며 앱의 실패가 아닙니다.

## 바꾸지 않는 것

- `src/main/`, `src/test/java/com/example/enrollment/support/`, `frontend/src/`, `frontend/e2e/helpers.js`
- `docs/requirements.md`, `docs/requirements-memo.md`, `docs/openapi.json`, `pom.xml`, `frontend/package.json`·`package-lock.json`, `frontend/playwright.config.js` 등 설정
- `templates/` 아래 파일
- 새 설치, 의존성 추가, 전역 설정 변경은 하지 않습니다. 실행 준비가 없으면 멈추고 알립니다. 기존 테스트를 끄거나 실패를 숨기지 않습니다.
- 단원 5의 `lab5-change.patch` 적용과 조회 파일 수정은 사람이 지시한 작업 사본에서만 합니다.

## templates/ 의 빈 틀

`templates/java/`의 `RulesPracticeTest.java`·`ApiPracticeTest.java`·`AcceptancePracticeTest.java`와 `templates/e2e/practice-flow.spec.js`는 예전 시작판의 미작성 틀을 원본 그대로 보관한 **읽기 참고**입니다. 실행 탐색 위치 밖에 있어 실행되지 않습니다. 안에 `@Disabled`·`test.fixme`와 미작성 `fail`이 있으므로 활성 폴더로 복사하지 않습니다. 이번에 완성할 테스트만 활성 폴더에 새로 만듭니다.

## 결과를 알릴 때

- 만들거나 바꾼 파일 목록과, 각 테스트가 무엇을 확인하는지 짧게 설명합니다.
- 실행하기 전에는 통과했다고 말하지 않습니다. 실행했다면 실제 출력의 테스트 이름, 실행 수, 실패·오류·건너뜀 수를 그대로 전합니다. 실행 수 0은 테스트가 실행되지 않은 것입니다.
- 실패하면 원인이 테스트 작성, 앱 결함, 환경·데이터, 요구 해석 중 어디에 있는지 근거와 함께 구분합니다. TDD에서 기능이 아직 없어 생긴 의도한 실패와 컴파일 오류·미작성 `fail`·환경 오류는 다른 실패입니다.
- Windows에서는 `./mvnw` 대신 `.\mvnw.cmd`를 씁니다.
