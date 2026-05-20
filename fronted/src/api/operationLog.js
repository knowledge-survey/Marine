import request from './index'

export const getOperationLogs = (params) => {
  return request.get('/operation-logs', { params })
}

export const deleteOperationLog = (id) => {
  return request.delete(`/operation-logs/${id}`)
}

export const clearOperationLogs = () => {
  return request.delete('/operation-logs')
}