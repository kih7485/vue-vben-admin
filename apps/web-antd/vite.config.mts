import { defineConfig } from '@vben/vite-config';

export default defineConfig(async () => {
  return {
    application: {},
    vite: {
      server: {
        proxy: {
          '/api': {
            changeOrigin: true,
            // 실제 백엔드 서버로 프록시 (express-backend)
            target: 'http://localhost:8080/api',
            rewrite: (path) => path.replace(/^\/api/, ''),
            ws: true,
          },
        },
      },
    },
  };
});
