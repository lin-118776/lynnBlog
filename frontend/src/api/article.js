import request from '../utils/request'

export function listArticles(page = 1, size = 10, params = {}) {
  return request.get('/api/article/list', { params: { page, size, ...params } })
}

export function getArticleDetail(id) {
  return request.get(`/api/article/${id}`)
}

export function createArticle(data) {
  return request.post('/api/article', data)
}

export function updateArticle(id, data) {
  return request.put(`/api/article/${id}`, data)
}

export function deleteArticle(id) {
  return request.delete(`/api/article/${id}`)
}

export function likeArticle(id) {
  return request.post(`/api/article/${id}/like`)
}

export function getArticleNeighbors(id) {
  return request.get(`/api/article/neighbors/${id}`)
}

export { listCategories } from './category'

export function listArticleComments(articleId, page = 1, size = 10) {
  return request.get(`/api/comment/article/${articleId}`, { params: { page, size } })
}

export function addArticleComment(articleId, content) {
  return request.post(`/api/comment/article/${articleId}`, { content })
}