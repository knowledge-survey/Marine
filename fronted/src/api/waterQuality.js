import request from './index'

export const getWaterQualityByZone = (zoneId) => {
  return request.get(`/water-quality/zone/${zoneId}`)
}

export const getWaterQualityByRange = (zoneId, startTime, endTime) => {
  return request.get(`/water-quality/zone/${zoneId}/range`, {
    params: { startTime, endTime }
  })
}

export const getLatestWaterQuality = (zoneId) => {
  return request.get(`/water-quality/zone/${zoneId}/latest`)
}

export const getWarnings = () => {
  return request.get('/water-quality/warnings')
}

export const getThreshold = (zoneId) => {
  return request.get(`/water-quality/threshold/${zoneId}`)
}

export const createWaterQuality = (data) => {
  return request.post('/water-quality', data)
}

export const createThreshold = (data) => {
  return request.post('/water-quality/threshold', data)
}

export const updateWaterQuality = (id, data) => {
  return request.put(`/water-quality/${id}`, data)
}

export const updateThreshold = (id, data) => {
  return request.put(`/water-quality/threshold/${id}`, data)
}

export const deleteWaterQuality = (id) => {
  return request.delete(`/water-quality/${id}`)
}
