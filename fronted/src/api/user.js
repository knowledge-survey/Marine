import request from './index'

export const login = (data) => {
  return request.post('/users/login', data)
}

export const getUsers = () => {
  return request.get('/users')
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
