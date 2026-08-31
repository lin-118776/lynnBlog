import request from '../utils/request'

// 上传文件（需登录）：返回 { url, originalName, size }，url 为 /uploads/... 可直接访问
export function uploadFile(file, bizType = 'article') {
  const formData = new FormData()
  formData.append('file', file)
  if (bizType) formData.append('bizType', bizType)
  return request.post('/api/file/upload', formData, {
    headers: { 'Content-Type': 'multipart/form-data' }
  })
}
