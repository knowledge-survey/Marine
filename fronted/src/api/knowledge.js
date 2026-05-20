import request from './index'

export const getKnowledgeList = (params) => {
  return request.get('/knowledge-base', { params })
}

export const getKnowledgeById = (id) => {
  return request.get(`/knowledge-base/${id}`)
}

export const createKnowledge = (data) => {
  return request.post('/knowledge-base', data)
}

export const updateKnowledge = (id, data) => {
  return request.put(`/knowledge-base/${id}`, data)
}

export const deleteKnowledge = (id) => {
  return request.delete(`/knowledge-base/${id}`)
}