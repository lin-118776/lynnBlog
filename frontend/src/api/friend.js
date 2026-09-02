import request from '../utils/request'

// 公开友链列表
export function listFriends() {
  return request.get('/api/friend/list')
}

// 管理端全部列表（含待审核，需登录）
export function listAllFriends() {
  return request.get('/api/friend/all')
}

// 访客提交友链申请（无需登录）
export function applyFriend(data) {
  return request.post('/api/friend/apply', data)
}

// 新增友链（需登录）
export function createFriend(data) {
  return request.post('/api/friend', data)
}

// 更新友链（需登录）
export function updateFriend(id, data) {
  return request.put(`/api/friend/${id}`, data)
}

// 删除友链（需登录）
export function deleteFriend(id) {
  return request.delete(`/api/friend/${id}`)
}
