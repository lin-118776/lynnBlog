<template>
  <div class="login-page">
    <div class="blob blob-1"></div>
    <div class="blob blob-2"></div>
    <el-card class="login-card" shadow="never">
      <div class="login-logo" aria-hidden="true"></div>
      <h2 class="login-title">{{ isRegister ? '注册' : '登录' }} · Lynn's Blog</h2>
      <p class="login-sub">{{ isRegister ? '创建账号，开启你的数字空间' : '欢迎回来，继续你的记录与成长' }}</p>
      <el-form :model="form" @keyup.enter="onSubmit">
        <el-form-item>
          <el-input
            v-model="form.username"
            placeholder="用户名"
            clearable
            autocomplete="username"
          >
            <template #prefix><AppIcon name="user" :size="15" class="input-icon" /></template>
          </el-input>
        </el-form-item>
        <el-form-item>
          <el-input
            v-model="form.password"
            type="password"
            placeholder="密码"
            show-password
            autocomplete="current-password"
          >
            <template #prefix><AppIcon name="lock" :size="15" class="input-icon" /></template>
          </el-input>
        </el-form-item>
        <el-form-item v-if="isRegister">
          <el-input
            v-model="form.nickname"
            placeholder="昵称（必填，将作为你的名字展示）"
            clearable
            autocomplete="nickname"
          >
            <template #prefix><AppIcon name="sparkles" :size="15" class="input-icon" /></template>
          </el-input>
        </el-form-item>
        <el-button type="primary" size="large" class="submit-btn" :loading="loading" @click="onSubmit">
          {{ isRegister ? '注 册' : '登 录' }}
        </el-button>
      </el-form>
      <div class="login-switch">
        <el-link type="primary" @click="toggleMode">
          {{ isRegister ? '已有账号？去登录' : '没有账号？去注册' }}
        </el-link>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { login, register } from '../api/auth'
import AppIcon from '../components/AppIcon.vue'
import { useAuth } from '../composables/useAuth'

const router = useRouter()
const route = useRoute()
const { setAuth } = useAuth()

const isRegister = ref(false)
const loading = ref(false)
const form = reactive({ username: '', password: '', nickname: '' })

function toggleMode() {
  isRegister.value = !isRegister.value
}

async function onSubmit() {
  if (!form.username || !form.password) {
    ElMessage.warning('请输入用户名和密码')
    return
  }
  if (isRegister.value && !form.nickname.trim()) {
    ElMessage.warning('请填写昵称')
    return
  }
  loading.value = true
  try {
    if (isRegister.value) {
      await register({
        username: form.username,
        password: form.password,
        nickname: form.nickname.trim()
      })
      ElMessage.success('注册成功，请登录')
      isRegister.value = false
      return
    }
    const { data } = await login({ username: form.username, password: form.password })
    // 保存 Token 与用户信息（统一走 useAuth）
    setAuth(data.token, data.userInfo)
    ElMessage.success('登录成功')
    router.push(route.query.redirect || '/dashboard')
  } catch (e) {
    // 错误提示已由 request 响应拦截器统一处理
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.login-page {
  position: relative;
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  overflow: hidden;
  padding: 24px;
  background:
    linear-gradient(135deg, rgba(244, 114, 182, 0.18), rgba(255, 255, 255, 0.06)),
    url('../images/login_pink.png') center / cover no-repeat;
  background-color: #fdf2f8;
}

.login-page::before {
  content: "";
  position: absolute;
  inset: 0;
  pointer-events: none;
  background: linear-gradient(180deg, rgba(255, 255, 255, 0.22), rgba(255, 255, 255, 0.02) 45%, rgba(255, 255, 255, 0.12));
}

.blob {
  position: absolute;
  border-radius: 50%;
  filter: blur(60px);
  opacity: 0.42;
}

.blob-1 {
  width: 320px;
  height: 320px;
  background: #f9a8d4;
  top: -80px;
  left: -60px;
}

.blob-2 {
  width: 280px;
  height: 280px;
  background: #fbcfe8;
  bottom: -70px;
  right: -50px;
}

.login-card {
  position: relative;
  width: min(400px, 100%);
  border-radius: 22px !important;
  border: 1px solid rgba(255, 255, 255, 0.55) !important;
  background: rgba(255, 255, 255, 0.38) !important;
  backdrop-filter: blur(22px) saturate(150%);
  -webkit-backdrop-filter: blur(22px) saturate(150%);
  box-shadow:
    0 18px 45px rgba(190, 24, 93, 0.18),
    0 3px 10px rgba(190, 24, 93, 0.08) !important;
  padding: 10px 8px;
  overflow: hidden;
  transform: translateY(0);
  transition:
    transform 0.28s ease,
    box-shadow 0.28s ease,
    border-color 0.28s ease,
    background 0.28s ease;
  animation: card-in 0.55s cubic-bezier(0.22, 0.9, 0.34, 1) backwards;
}

.login-card::before {
  content: "";
  position: absolute;
  inset: 0;
  pointer-events: none;
  background: linear-gradient(120deg, rgba(255, 255, 255, 0.38), transparent 42%, transparent 60%, rgba(255, 255, 255, 0.24));
}

.login-card:hover {
  transform: translateY(-5px) scale(1.004);
  border-color: rgba(255, 255, 255, 0.75) !important;
  background: rgba(255, 255, 255, 0.46) !important;
  box-shadow:
    0 24px 60px rgba(190, 24, 93, 0.24),
    0 6px 18px rgba(190, 24, 93, 0.12) !important;
}

@keyframes card-in {
  0% {
    opacity: 0;
    transform: translateY(28px) scale(0.96);
    filter: blur(6px);
  }
  100% {
    opacity: 1;
    transform: translateY(0) scale(1);
    filter: blur(0);
  }
}

.login-logo {
  width: 52px;
  height: 52px;
  margin: 8px auto 14px;
  border-radius: 16px;
  background: linear-gradient(135deg, #ec4899, #f472b6);
  box-shadow: 0 8px 20px rgba(236, 72, 153, 0.32);
  position: relative;
}

.login-logo::after {
  content: "";
  position: absolute;
  left: 15px;
  top: 15px;
  width: 22px;
  height: 22px;
  border-radius: 7px;
  background: rgba(255, 255, 255, 0.92);
}

.login-title {
  text-align: center;
  margin: 0;
  font-size: 19px;
  font-weight: 700;
  letter-spacing: 0.5px;
}

.login-sub {
  text-align: center;
  margin: 6px 0 22px;
  font-size: 13px;
  color: var(--text-sub);
}

.input-icon {
  color: var(--text-sub);
}

.login-card :deep(.el-input__wrapper) {
  border-radius: 12px;
  padding: 2px 12px;
  background: rgba(255, 255, 255, 0.55);
  box-shadow: 0 0 0 1px rgba(236, 72, 153, 0.12) inset;
  transition:
    background 0.2s ease,
    box-shadow 0.2s ease,
    transform 0.2s ease;
}

.login-card :deep(.el-input__wrapper:hover) {
  background: rgba(255, 255, 255, 0.76);
  box-shadow: 0 0 0 1px rgba(236, 72, 153, 0.28) inset;
}

.login-card :deep(.el-input__wrapper.is-focus) {
  background: rgba(255, 255, 255, 0.92);
  box-shadow: 0 0 0 1.5px rgba(236, 72, 153, 0.55) inset;
}

.submit-btn {
  width: 100%;
  border-radius: 12px;
  letter-spacing: 4px;
  border-color: transparent !important;
  background: linear-gradient(135deg, #ec4899, #f472b6) !important;
  box-shadow: 0 12px 24px rgba(236, 72, 153, 0.24);
  transition:
    transform 0.22s ease,
    box-shadow 0.22s ease,
    filter 0.22s ease;
}

.submit-btn:hover {
  transform: translateY(-2px);
  filter: brightness(1.04);
  box-shadow: 0 16px 30px rgba(236, 72, 153, 0.32);
}

.submit-btn:focus-visible {
  outline: 2px solid rgba(236, 72, 153, 0.55);
  outline-offset: 2px;
}

.login-switch {
  margin-top: 14px;
  text-align: center;
}

@media (prefers-reduced-motion: reduce) {
  .login-card,
  .login-card :deep(.el-input__wrapper),
  .submit-btn {
    animation: none;
    transition: none;
  }
}
</style>
