import { defineConfig, devices } from '@playwright/test';
import net from 'node:net';
import path from 'node:path';
import { fileURLToPath } from 'node:url';

// 화면 테스트(E2E). 기본으로는 빌드된 jar를 127.0.0.1의 빈 포트에 새로 띄워 깨끗한 데이터로 시험한다.
// 미리 띄운 서버를 쓰려면 E2E_BASE_URL을 준다(이 경우 데이터가 초기 상태여야 한다).

const here = path.dirname(fileURLToPath(import.meta.url));
const jar = path.resolve(here, '..', 'target', 'course-enrollment-testing-0.1.0.jar');
const javaBin = process.env.JAVA_HOME ? path.join(process.env.JAVA_HOME, 'bin', 'java') : 'java';

function freePort() {
  return new Promise((resolve, reject) => {
    const server = net.createServer();
    server.unref();
    server.on('error', reject);
    server.listen(0, '127.0.0.1', () => {
      const { port } = server.address();
      server.close(() => resolve(port));
    });
  });
}

// 워커 프로세스도 같은 포트를 보도록 환경 변수에 남긴다.
if (!process.env.E2E_BASE_URL && !process.env.E2E_PORT) {
  process.env.E2E_PORT = String(await freePort());
}
const baseURL = process.env.E2E_BASE_URL ?? `http://127.0.0.1:${process.env.E2E_PORT}`;

export default defineConfig({
  testDir: './e2e',
  fullyParallel: false,
  workers: 1,
  retries: 0,
  reporter: [['list']],
  timeout: 30_000,
  use: {
    baseURL,
    trace: 'retain-on-failure',
    locale: 'ko-KR',
  },
  projects: [{ name: 'chromium', use: { ...devices['Desktop Chrome'] } }],
  webServer: process.env.E2E_BASE_URL
    ? undefined
    : {
        command: `"${javaBin}" -jar "${jar}" --server.address=127.0.0.1 --server.port=${process.env.E2E_PORT}`,
        url: `${baseURL}/api/lab-info`,
        reuseExistingServer: false,
        timeout: 60_000,
        stdout: 'ignore',
        stderr: 'pipe',
      },
});
