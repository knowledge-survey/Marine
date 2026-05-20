import request from './index'

export const getSystemSettings = () => {
  return request.get('/system-settings')
}

export const getSystemSettingByKey = (key) => {
  return request.get(`/system-settings/${key}`)
}

export const saveSystemSettings = (data) => {
  return request.put('/system-settings', data)
}

export const updateSystemSetting = (key, value) => {
  return request.put(`/system-settings/${key}`, { configValue: value })
}