import request from './index'

export const login = (data) => {
  return request.post('/users/login', data)
}

export const getUsers = () => {
  return request.get('/users')
}

export const getUserById = (id) => {
  return request.get(`/users/${id}`)
}

export const createUser = (data) => {
  return request.post('/users', data)
}

export const updateUser = (id, data) => {
  return request.put(`/users/${id}`, data)
}

export const deleteUser = (id) => {
  return request.delete(`/users/${id}`)
}

export const toggleUser = (id) => {
  return request.patch(`/users/${id}/toggle`)
}

export const uploadAvatar = (id, file) => {
  const formData = new FormData()
  formData.append('file', file)
  return request.post(`/users/${id}/avatar`, formData, {
    headers: { 'Content-Type': 'multipart/form-data' }
  })
}

export const deleteAvatar = (id) => {
  return request.delete(`/users/${id}/avatar`)
}
