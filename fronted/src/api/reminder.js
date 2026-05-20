import request from './index'

export const getReminders = (params) => {
  return request.get('/smart-reminders', { params })
}

export const listAllReminders = (params) => {
  return request.get('/smart-reminders/all', { params })
}

export const getDueReminders = (params) => {
  return request.get('/smart-reminders/due', { params })
}

export const getReminderById = (id) => {
  return request.get(`/smart-reminders/${id}`)
}

export const createReminder = (data) => {
  return request.post('/smart-reminders', data)
}

export const updateReminder = (data) => {
  return request.put('/smart-reminders', data)
}

export const deleteReminder = (id) => {
  return request.delete(`/smart-reminders/${id}`)
}

export const triggerReminder = (id) => {
  return request.put(`/smart-reminders/${id}/trigger`)
}

export const dismissReminder = (id) => {
  return request.put(`/smart-reminders/${id}/dismiss`)
}