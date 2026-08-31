import request from '../utils/request'

// 分类列表（公开，含文章数统计）
export function listCategories() {
  return request.get('/api/category/list')
}

// 新建分类（需登录）
export function createCategory(name) {
  return request.post('/api/category', { name })
}

// 重命名分类（需登录）
export function renameCategory(id, name) {
  return request.put(`/api/category/${id}`, { name })
}

// 删除分类（需登录；分类下有文章时后端会拒绝）
export function deleteCategory(id) {
  return request.delete(`/api/category/${id}`)
}
