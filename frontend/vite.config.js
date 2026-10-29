import { defineConfig } from 'vite';
import react from '@vitejs/plugin-react';
// Proxies /api to Spring Boot so no CORS setup is needed in development
export default defineConfig({ plugins: [react()], server: { port: 5173, proxy: { '/api': 'http://localhost:8080' } } });
