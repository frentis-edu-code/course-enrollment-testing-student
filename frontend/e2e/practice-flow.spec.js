import { test, expect } from '@playwright/test';
import { useEmployee, courseRow } from './helpers.js';

// 실습 4(화면): 신청 취소 흐름을 화면에서 확인한다.
// 시작할 때 test.fixme를 test로 바꾼다. 실행: npm run e2e (먼저 jar를 빌드해 둔다. README 참고)
// 요소는 사용자가 보는 역할과 이름으로 찾는다. 예) page.getByRole('button', { name: '신청' })
test.fixme('실습 4: 신청한 과정을 취소하면 취소됨으로 바뀌고 잔여석이 늘어난다', async ({ page }) => {
  // 도구: useEmployee(page, 'E1001'), courseRow(page, 'C-101')
  // 1. 앱을 연다: await page.goto('/')
  // 2. 사원번호를 입력하고 신청한다
  // 3. 내 신청 목록에서 취소 → 확인 창에서 확인
  // 4. 보이는 결과를 확인한다(메시지, 상태, 잔여석)
  expect(true).toBe(false);
});
