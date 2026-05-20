import request from './index'

export const getCosts = (params) => {
  return request.get('/cost-records', { params })
}

export const createCost = (data) => {
  return request.post('/cost-records', data)
}

export const updateCost = (id, data) => {
  return request.put(`/cost-records/${id}`, data)
}

export const deleteCost = (id) => {
  return request.delete(`/cost-records/${id}`)
}

export const getRevenues = (params) => {
  return request.get('/revenue-records', { params })
}

export const createRevenue = (data) => {
  return request.post('/revenue-records', data)
}

export const updateRevenue = (id, data) => {
  return request.put(`/revenue-records/${id}`, data)
}

export const deleteRevenue = (id) => {
  return request.delete(`/revenue-records/${id}`)
}