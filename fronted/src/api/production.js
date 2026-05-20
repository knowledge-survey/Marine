import request from './index'

export const getProductionPlans = (params) => {
  return request.get('/production-plans', { params })
}

export const getProductionPlanById = (id) => {
  return request.get(`/production-plans/${id}`)
}

export const createProductionPlan = (data) => {
  return request.post('/production-plans', data)
}

export const updateProductionPlan = (id, data) => {
  return request.put(`/production-plans/${id}`, data)
}

export const deleteProductionPlan = (id) => {
  return request.delete(`/production-plans/${id}`)
}