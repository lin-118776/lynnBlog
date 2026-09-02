import request from '../utils/request'

// 歌曲列表（公开）
export function listMusic() {
  return request.get('/api/music/list')
}

// 新增歌曲（需登录）
export function addMusic(data) {
  return request.post('/api/music', data)
}

// 删除歌曲（需登录）
export function deleteMusic(id) {
  return request.delete(`/api/music/${id}`)
}
