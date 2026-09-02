import request from '../utils/request'

// 公开作品列表
export function listProjects() {
  return request.get('/api/project/list')
}

// 新增作品（需登录）
export function createProject(data) {
  return request.post('/api/project', data)
}

// 更新作品（需登录）
export function updateProject(id, data) {
  return request.put(`/api/project/${id}`, data)
}

// 删除作品（需登录）
export function deleteProject(id) {
  return request.delete(`/api/project/${id}`)
}
