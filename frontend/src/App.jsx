import { useCallback, useEffect, useRef, useState } from 'react';
import { api } from './api.js';

const DEFAULT_EMPLOYEE = 'E1001';
const STATUS_LABEL = { APPLIED: '신청됨', CANCELLED: '취소됨' };

// "2026-12-01T18:00:00+09:00" → "2026-12-01 18:00:00" (서버는 언제나 +09:00으로 보낸다)
const formatTime = (iso) => (iso ? iso.slice(0, 19).replace('T', ' ') : '');

export default function App() {
  const [labInfo, setLabInfo] = useState(null);
  const [courses, setCourses] = useState([]);
  const [coursesError, setCoursesError] = useState('');
  const [employeeInput, setEmployeeInput] = useState(DEFAULT_EMPLOYEE);
  const [employeeId, setEmployeeId] = useState(DEFAULT_EMPLOYEE);
  const [mine, setMine] = useState({ employeeId: null, items: [], loading: false, error: '' });
  const [notice, setNotice] = useState(null);
  const [busy, setBusy] = useState(false);

  // 이중 제출 방지: 상태 갱신 전에 연달아 눌러도 막히도록 ref로도 잠근다.
  const busyRef = useRef(false);
  // 늦게 도착한 응답이 다른 사원의 목록을 덮지 않도록 현재 사원과 요청 순번을 기억한다.
  const employeeRef = useRef(employeeId);
  const mineSeq = useRef(0);
  const mineAbort = useRef(null);
  const coursesSeq = useRef(0);

  const loadCourses = useCallback(async () => {
    const seq = ++coursesSeq.current;
    try {
      const data = await api.courses();
      if (seq === coursesSeq.current) {
        setCourses(data);
        setCoursesError('');
      }
    } catch (error) {
      if (seq === coursesSeq.current) setCoursesError(error.message);
    }
  }, []);

  const loadMine = useCallback(async (targetEmployee) => {
    mineAbort.current?.abort();
    const controller = new AbortController();
    mineAbort.current = controller;
    const seq = ++mineSeq.current;
    setMine((prev) => ({
      employeeId: targetEmployee,
      items: prev.employeeId === targetEmployee ? prev.items : [],
      loading: true,
      error: '',
    }));
    try {
      const items = await api.myEnrollments(targetEmployee, controller.signal);
      if (seq !== mineSeq.current || employeeRef.current !== targetEmployee) return; // 늦은 응답은 버린다
      setMine({ employeeId: targetEmployee, items, loading: false, error: '' });
    } catch (error) {
      if (error.name === 'AbortError') return;
      if (seq !== mineSeq.current || employeeRef.current !== targetEmployee) return;
      setMine({ employeeId: targetEmployee, items: [], loading: false, error: error.message });
    }
  }, []);

  useEffect(() => {
    api.labInfo().then(setLabInfo).catch((error) => setNotice({ type: 'error', text: error.message }));
    loadCourses();
  }, [loadCourses]);

  useEffect(() => {
    employeeRef.current = employeeId;
    loadMine(employeeId);
  }, [employeeId, loadMine]);

  const applyEmployee = (event) => {
    event.preventDefault();
    if (busyRef.current) return; // 신청·취소 중에는 사원을 바꾸지 않는다
    const next = employeeInput.trim();
    if (!next) {
      setNotice({ type: 'error', text: '필수 정보를 입력해 주세요.' });
      return;
    }
    setNotice(null);
    if (next === employeeId) {
      loadMine(next);
    } else {
      setEmployeeId(next);
    }
  };

  // 신청·취소 공통: 한 번에 하나만 보내고, 끝나면 과정 목록과 내 신청 목록을 다시 읽는다.
  const runMutation = async (action, successText) => {
    if (busyRef.current) return;
    busyRef.current = true;
    setBusy(true);
    const actingEmployee = employeeId;
    try {
      await action();
      setNotice({ type: 'success', text: successText });
    } catch (error) {
      setNotice({ type: 'error', text: error.message });
    } finally {
      await Promise.all([loadCourses(), loadMine(actingEmployee)]);
      busyRef.current = false;
      setBusy(false);
    }
  };

  const enroll = (course) => runMutation(() => api.enroll(employeeId, course.courseId), '신청이 완료되었습니다.');

  const cancel = (enrollment) => {
    if (busyRef.current) return;
    if (!window.confirm('정말 취소하시겠습니까?')) return;
    runMutation(() => api.cancel(enrollment.enrollmentId), '취소가 완료되었습니다.');
  };

  return (
    <div className="page">
      <header className="header">
        <h1>교육 과정 신청</h1>
        <p className="lab-banner" data-testid="lab-banner">
          교육용 학습 예제입니다. 인증·권한 기능이 없고 실제 운영용이 아닙니다.
        </p>
        <dl className="lab-info">
          <div>
            <dt>실습 기준 시각</dt>
            <dd data-testid="lab-now">
              {labInfo ? `${formatTime(labInfo.labNow)} (${labInfo.timeZone}, ${labInfo.clockMode === 'FIXED' ? '고정 시계' : '실제 시계'})` : '불러오는 중…'}
            </dd>
          </div>
          <div>
            <dt>초기화</dt>
            <dd>{labInfo?.resetMethod ?? ''}</dd>
          </div>
          <div>
            <dt>API 문서</dt>
            <dd>
              <a href="/swagger-ui/index.html" target="_blank" rel="noreferrer">Swagger UI</a>
              {' · '}
              <a href="/v3/api-docs" target="_blank" rel="noreferrer">OpenAPI JSON</a>
            </dd>
          </div>
        </dl>
      </header>

      <section className="card">
        <form className="employee-form" onSubmit={applyEmployee}>
          <label htmlFor="employee-id">사원번호</label>
          <input
            id="employee-id"
            value={employeeInput}
            onChange={(e) => setEmployeeInput(e.target.value)}
            placeholder="예: E1001"
            autoComplete="off"
          />
          <button type="submit" disabled={busy}>조회</button>
          <span className="current-employee">
            현재 사원번호: <strong data-testid="current-employee">{employeeId}</strong>
          </span>
        </form>
      </section>

      <div className={`notice ${notice ? `notice-${notice.type}` : ''}`} role="status" aria-live="polite" data-testid="notice">
        {notice?.text ?? ''}
      </div>

      <section className="card">
        <h2>과정 목록</h2>
        {coursesError && <p className="error-text">{coursesError}</p>}
        <table className="table" data-testid="course-table">
          <thead>
            <tr>
              <th>과정 ID</th>
              <th>과정명</th>
              <th>정원</th>
              <th>잔여석</th>
              <th>신청 마감</th>
              <th>시작</th>
              <th></th>
            </tr>
          </thead>
          <tbody>
            {courses.map((course) => {
              const full = course.remainingSeats <= 0;
              return (
                <tr key={course.courseId} data-testid={`course-${course.courseId}`}>
                  <td data-label="과정 ID">{course.courseId}</td>
                  <td data-label="과정명">{course.title}</td>
                  <td data-label="정원">{course.capacity}</td>
                  <td data-label="잔여석" data-testid="remaining">{course.remainingSeats}</td>
                  <td data-label="신청 마감">{formatTime(course.enrollmentDeadline)}</td>
                  <td data-label="시작">{formatTime(course.startAt)}</td>
                  <td className="action">
                    {full && <span className="badge">정원 마감</span>}
                    <button type="button" onClick={() => enroll(course)} disabled={busy || full}>
                      신청
                    </button>
                  </td>
                </tr>
              );
            })}
          </tbody>
        </table>
      </section>

      <section className="card">
        <h2>
          내 신청 목록 <small>({mine.employeeId ?? employeeId})</small>
        </h2>
        {mine.error && <p className="error-text">{mine.error}</p>}
        {mine.loading && mine.items.length === 0 ? (
          <p className="muted">불러오는 중…</p>
        ) : mine.items.length === 0 ? (
          <p className="muted" data-testid="my-empty">신청 내역이 없습니다.</p>
        ) : (
          <table className="table" data-testid="my-table">
            <thead>
              <tr>
                <th>신청 ID</th>
                <th>과정명</th>
                <th>상태</th>
                <th></th>
              </tr>
            </thead>
            <tbody>
              {mine.items.map((item) => (
                <tr key={item.enrollmentId} data-testid={`enrollment-${item.enrollmentId}`}>
                  <td data-label="신청 ID">{item.enrollmentId}</td>
                  <td data-label="과정명">{item.courseTitle}</td>
                  <td data-label="상태">
                    <span className={`status status-${item.status.toLowerCase()}`}>{STATUS_LABEL[item.status] ?? item.status}</span>
                  </td>
                  <td className="action">
                    {item.status === 'APPLIED' && (
                      <button type="button" onClick={() => cancel(item)} disabled={busy}>
                        취소
                      </button>
                    )}
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        )}
      </section>
    </div>
  );
}
