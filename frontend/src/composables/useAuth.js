import { ref, computed } from 'vue'

// 统一的登录态管理：模块级单例状态，所有调用方共享同一份 token / userInfo
const TOKEN_KEY = 'token'
const USER_KEY = 'userInfo'

function readUserInfo() {
  try {
    return JSON.parse(localStorage.getItem(USER_KEY) || 'null')
  } catch {
    return null
  }
}

// 模块加载时从 localStorage 恢复，刷新页面不丢登录态
const token = ref(localStorage.getItem(TOKEN_KEY) || '')
const userInfo = ref(readUserInfo())

/**
 * 登录态组合式函数
 * 用法：const { isLoggedIn, setAuth, clearAuth } = useAuth()
 * 登录成功 → setAuth(token, userInfo)；登出/Token 失效 → clearAuth()
 */
export function useAuth() {
  const isLoggedIn = computed(() => Boolean(token.value))

  function setAuth(newToken, info) {
    token.value = newToken || ''
    userInfo.value = info || null
    localStorage.setItem(TOKEN_KEY, token.value)
    localStorage.setItem(USER_KEY, JSON.stringify(userInfo.value))
  }

  function clearAuth() {
    token.value = ''
    userInfo.value = null
    localStorage.removeItem(TOKEN_KEY)
    localStorage.removeItem(USER_KEY)
  }

  return { token, userInfo, isLoggedIn, setAuth, clearAuth }
}
