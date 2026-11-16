# Cucumber 강사 시연 안내

API 테스트를 본 뒤, 테스트 설계에서 사용한 **상황 → 행동 → 기대결과**를 실행 가능한 업무 문장에 연결하는 강사 시연 한 건을 봅니다. 학생 필수 실습이나 설치는 추가하지 않습니다.

## 무엇을 구분하나요?

| 이름 | 역할 |
| --- | --- |
| BDD | 구체적인 행동 예로 업무·개발·테스트가 기대를 함께 맞추는 방법 |
| Gherkin | `.feature`에 상황(Given)·행동(When)·기대결과(Then)를 적는 표기 |
| Step Definition | 문장을 실제 API 호출과 검증 코드에 연결하는 부분 |
| Cucumber | 문장에 맞는 Step Definition을 찾아 실행하고 결과를 보여 주는 도구 |

## 짧은 첫 요청 예시

```text
요구사항을 보고 정원이 찬 과정에 다른 사원이 신청하면 거절되는 Cucumber 시나리오를 만들어 줘. 기존 앱은 바꾸지 말고 강사 시연 폴더에만 만들어 줘.
```

이 요청은 수업에서 사용할 예시입니다. 교재의 실행 기록은 같은 정원 요구로 별도 준비한 강사 예제를 실행한 결과이며, 이 짧은 요청을 그대로 넣어 얻은 출력으로 소급하지 않습니다.

## 생성할 파일과 관찰할 결과

| 생성할 파일 예시(강사 시연용) | 역할 | 확인할 것 |
| --- | --- | --- |
| `src/test/resources/features/enrollment/course-full.feature` | 정원이 찬 과정에 다른 사원이 신청하는 업무 시나리오 한 건 | 상황·행동·기대결과가 [요구 문서](requirements.md)의 정원 규칙·거절 시 데이터 불변과 같은가 |
| `src/test/java/demo/cucumber/CourseFullSteps.java` | 업무 문장을 실제 앱 API 호출·검증, 앱 기동·종료에 연결 | 거절 응답, 과정 목록의 신청 인원·잔여석, 거절된 사원의 신청 내역을 확인하는가 |
| Cucumber 실행 보고서 | 단계별 결과와 시나리오 결과 | 실제 실행·실패·건너뜀, 종료 코드를 구분하는가 |

완성 `.feature`·Java 답안과 Cucumber 의존성은 학생 저장소에 넣지 않습니다. 강사가 별도 격리 폴더에서 실제 실행을 보여 줍니다. 준비가 되지 않으면 저장한 실제 실행 기록을 읽되, 방금 실행한 결과라고 말하지 않습니다.

짧게 생성 요청 → 파일 내용 검토 → 실행 요청 → 실제 결과 확인의 흐름은 기존 실습과 같습니다. 결과가 다르면 관찰한 차이에 따라 다음 대화를 이어 갑니다.

참고: [Cucumber 소개](https://cucumber.io/docs/), [Gherkin Reference](https://cucumber.io/docs/gherkin/reference/), [Cucumber Reference](https://cucumber.io/docs/cucumber/api/).
