import { expect } from '@playwright/test';

/** 화면에서 사원번호를 바꾸고 조회한다. */
export async function useEmployee(page, employeeId) {
  await page.getByLabel('사원번호').fill(employeeId);
  await page.getByRole('button', { name: '조회' }).click();
  await expect(page.getByTestId('current-employee')).toHaveText(employeeId);
}

export function courseRow(page, courseId) {
  return page.getByTestId(`course-${courseId}`);
}

/** API로 직접 신청한다(화면 밖에서 다른 사람이 신청한 상황 만들기). */
export async function enrollByApi(request, employeeId, courseId) {
  const response = await request.post('/api/enrollments', { data: { employeeId, courseId } });
  expect(response.status(), `${employeeId} → ${courseId}`).toBe(201);
  return response.json();
}
