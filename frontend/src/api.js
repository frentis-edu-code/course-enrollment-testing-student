// 실제 Spring Boot API를 호출한다. 개발 중에는 Vite 프록시를, 빌드 뒤에는 같은 서버를 통해 같은 출처로 요청한다.

export class ApiError extends Error {
  constructor(message, { status, code } = {}) {
    super(message);
    this.status = status;
    this.code = code;
  }
}

const NETWORK_MESSAGE = '서버에 연결할 수 없습니다. 서버가 실행 중인지 확인해 주세요.';

async function request(path, { method = 'GET', body, signal } = {}) {
  let response;
  try {
    response = await fetch(path, {
      method,
      signal,
      headers: body === undefined ? { Accept: 'application/json' } : { Accept: 'application/json', 'Content-Type': 'application/json' },
      body: body === undefined ? undefined : JSON.stringify(body),
    });
  } catch (error) {
    if (error.name === 'AbortError') throw error;
    throw new ApiError(NETWORK_MESSAGE);
  }

  const isJson = (response.headers.get('content-type') ?? '').includes('application/json');
  const data = isJson ? await response.json().catch(() => null) : null;

  if (!response.ok) {
    // 서버가 준 한국어 메시지를 그대로 쓴다.
    const message = data?.message ?? `요청을 처리하지 못했습니다. (HTTP ${response.status})`;
    throw new ApiError(message, { status: response.status, code: data?.code });
  }
  return data;
}

export const api = {
  labInfo: (signal) => request('/api/lab-info', { signal }),
  courses: (signal) => request('/api/courses', { signal }),
  myEnrollments: (employeeId, signal) =>
    request(`/api/enrollments?employeeId=${encodeURIComponent(employeeId)}`, { signal }),
  enroll: (employeeId, courseId) => request('/api/enrollments', { method: 'POST', body: { employeeId, courseId } }),
  cancel: (enrollmentId) => request(`/api/enrollments/${encodeURIComponent(enrollmentId)}/cancel`, { method: 'POST' }),
};
