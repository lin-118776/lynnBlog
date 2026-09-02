import request from '../utils/request'

// 留言板：公开读取 + 游客留言（无需登录）

export function listGuestbook(page = 1, size = 10) {
  return request.get('/api/guestbook/list', { params: { page, size } })
}

export function addGuestbook(nickname, content) {
  return request.post('/api/guestbook', { nickname, content })
}
