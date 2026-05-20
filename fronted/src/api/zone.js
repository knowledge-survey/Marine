import request from './index'

export const getZones = () => {
  return request.get('/zones')
}

export const getRootZones = () => {
  return request.get('/zones/roots')
}

export const getZoneById = (id) => {
  return request.get(`/zones/${id}`)
}

export const searchZones = (name) => {
  return request.get('/zones/search', { params: { name } })
}

export const createZone = (data) => {
  return request.post('/zones', data)
}

export const updateZone = (id, data) => {
  return request.put(`/zones/${id}`, data)
}

export const deleteZone = (id) => {
  return request.delete(`/zones/${id}`)
}

export const getZoneThreshold = (id) => {
  return request.get(`/zones/${id}/threshold`)
}

export const saveZoneThreshold = (id, data) => {
  return request.post(`/zones/${id}/threshold`, data)
}
