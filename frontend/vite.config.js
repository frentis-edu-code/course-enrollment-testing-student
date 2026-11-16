import { defineConfig } from 'vite';
import react from '@vitejs/plugin-react';

// 개발 서버는 127.0.0.1에서만 열고, API·문서 요청은 Spring Boot 서버로 넘긴다(같은 출처라 CORS 설정이 필요 없다).
const apiTarget = process.env.API_TARGET ?? 'http://127.0.0.1:8080';
const proxied = { target: apiTarget, changeOrigin: false };

export default defineConfig({
  plugins: [react()],
  server: {
    host: '127.0.0.1',
    port: Number(process.env.VITE_PORT ?? 5173),
    strictPort: false,
    proxy: {
      '/api': proxied,
      '/v3': proxied,
      '/swagger-ui': proxied,
    },
  },
  preview: {
    host: '127.0.0.1',
  },
});
