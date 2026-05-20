import request from './index'

export const listNotification = (params) => {
  return request.get('/notification', { params })
}

export const listAllNotification = (params) => {
  return request.get('/notification/all', { params })
}

export const getUnreadNotifications = (params) => {
  return request.get('/notification/unread', { params })
}

export const getUnreadCount = (params) => {
  return request.get('/notification/unread-count', { params })
}

export const getNotification = (id) => {
  return request.get(`/notification/${id}`)
}

export const saveNotification = (data) => {
  return request.post('/notification', data)
}

export const updateNotification = (data) => {
  return request.put('/notification', data)
}

export const deleteNotification = (id) => {
  return request.delete(`/notification/${id}`)
}

export const markAsRead = (id, userId) => {
  return request.put(`/notification/${id}/read`, null, { params: { userId } })
}

export const markAllAsRead = (params) => {
  return request.put('/notification/mark-all-read', null, { params })
}