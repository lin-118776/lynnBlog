import { createApp } from 'vue'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import './styles/main.css'
import App from './App.vue'
import router from './router'
import { initTheme } from './utils/theme'

// 挂载前应用主题（避免换装闪烁）
initTheme()

// 创建应用并挂载路由与 Element Plus
const app = createApp(App)
app.use(ElementPlus)
app.use(router)
app.mount('#app')