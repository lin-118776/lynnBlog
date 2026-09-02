import request from '../utils/request'

// 穿搭墙列表（匿名仅公开；支持 category/status/keyword 筛选）
export function listLolita(page = 1, size = 12, params = {}) {
  return request.get('/api/lolita/list', { params: { page, size, ...params } })
}

// 穿搭详情（公开可直接访问）
export function getLolitaDetail(id) {
  return request.get(`/api/lolita/${id}`)
}

// 新增穿搭（需登录）
export function createLolita(data) {
  return request.post('/api/lolita', data)
}

// 更新穿搭（需登录）
export function updateLolita(id, data) {
  return request.put(`/api/lolita/${id}`, data)
}

// 删除穿搭（需登录）
export function deleteLolita(id) {
  return request.delete(`/api/lolita/${id}`)
}

// 穿着次数 +1（需登录），返回最新次数
export function wearLolita(id) {
  return request.put(`/api/lolita/wear/${id}`)
}
