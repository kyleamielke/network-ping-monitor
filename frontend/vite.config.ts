import { defineConfig } from 'vite';
import react from '@vitejs/plugin-react';
import { resolve } from 'path';

// https://vitejs.dev/config/
export default defineConfig({
  plugins: [react()],
  resolve: {
    alias: {
      '@': resolve(__dirname, './src'),
    },
  },
  server: {
    allowedHosts: ['stwvmcode01', 'localhost'],
    host: '0.0.0.0',
    port: 3000,
    open: true,
    proxy: {
      '/graphql': {
        target: 'http://localhost:8080',
        changeOrigin: true,
      },
      '/graphql-ws': {
        target: 'ws://localhost:8080',
        ws: true,
        changeOrigin: true,
      },
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true,
      },
    },
  },
  build: {
    outDir: 'build',
    sourcemap: true,
  },
});
