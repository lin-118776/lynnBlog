import axios from 'axios'
import { ElMessage } from 'element-plus'
import router from '../router'
import { useAuth } from '../composables/useAuth'

const { token, clearAuth } = useAuth()

// Axios 实例：统一请求封装
const request = axios.create({
  baseURL: '',
  timeout: 15000
})

// 请求拦截器：读取统一登录态并写入 Authorization
request.interceptors.request.use(
  (config) => {
    if (token.value) {
      config.headers.Authorization = `Bearer ${token.value}`
    }
    return config
  },
  (error) => Promise.reject(error)
)

// 响应拦截器：统一处理 Result 结构
request.interceptors.response.use(
  (response) => {
    const res = response.data
    // 非标准 Result 结构（如二进制流）直接返回
    if (res === null || typeof res !== 'object' || !('code' in res)) {
      return res
    }
    if (res.code === 200) {
      return res
    }
    if (res.code === 401) {
      // Token 失效：清除统一登录态并跳转登录
      clearAuth()
      ElMessage.error(res.msg || '登录已过期，请重新登录')
      router.push('/login')
      return Promise.reject(new Error(res.msg || '未登录'))
    }
    // 其他业务错误：提示 msg
    ElMessage.error(res.msg || '请求失败')
    return Promise.reject(new Error(res.msg || '请求失败'))
  },
  (error) => {
    // HTTP 层错误（网络异常 / 5xx 等）
    if (error.response && error.response.status === 401) {
      clearAuth()
      router.push('/login')
    }
    const msg = error.response?.data?.msg || error.message || '网络错误'
    ElMessage.error(msg)
    return Promise.reject(error)
  }
)

export default request