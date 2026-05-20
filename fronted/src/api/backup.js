import request from './index'

export const getBackups = () => {
  return request.get('/backup/list')
}

export const createBackup = () => {
  return request.post('/backup/create')
}

export const deleteBackup = (backupName) => {
  return request.delete(`/backup/${backupName}`)
}

export const restoreBackup = (backupName) => {
  return request.post(`/backup/restore/${backupName}`)
}