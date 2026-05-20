import request from './index'

export const getSalesRecords = (params) => {
  return request.get('/sales-records', { params })
}

export const getSalesRecordById = (id) => {
  return request.get(`/sales-records/${id}`)
}

export const createSalesRecord = (data) => {
  return request.post('/sales-records', data)
}

export const updateSalesRecord = (id, data) => {
  return request.put(`/sales-records/${id}`, data)
}

export const deleteSalesRecord = (id) => {
  return request.delete(`/sales-records/${id}`)
}