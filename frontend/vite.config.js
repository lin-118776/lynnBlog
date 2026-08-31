import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'

// Vite 配置：开发端口 5173，代理转发后端接口与上传文件
export default defineConfig({
  plugins: [vue()],
  server: {
    port: 5173,
    proxy: {
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true
      },
      '/uploads': {
        target: 'http://localhost:8080',
        changeOrigin: true
      }
    }
  }
})