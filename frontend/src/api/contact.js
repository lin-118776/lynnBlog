import request from '../utils/request'

// 查询全部联系方式（公开）
export function listContacts() {
  return request.get('/api/contact/list')
}

// 更新某个联系方式（需登录）
export function updateContact(key, value) {
  return request.put(`/api/contact/${key}`, { value })
}
