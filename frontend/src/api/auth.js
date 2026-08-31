import request from '../utils/request'

// 登录
export function login(data) {
  return request.post('/api/auth/login', data)
}

// 注册
export function register(data) {
  return request.post('/api/auth/register', data)
}