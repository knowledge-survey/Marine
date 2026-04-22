import request from './index'

export const getSeedlingsByZone = (zoneId) => {
  return request.get(`/seedlings/zone/${zoneId}`)
}

export const getSeedlingsByBatch = (batchNo) => {
  return request.get(`/seedlings/batch/${batchNo}`)
}

export const getAllBatches = () => {
  return request.get('/seedlings/batches')
}

export const createSeedling = (data) => {
  return request.post('/seedlings', data)
}

export const getFeedingByZone = (zoneId) => {
  return request.get(`/feeding/zone/${zoneId}`)
}

export const createFeeding = (data) => {
  return request.post('/feeding', data)
}

export const getHarvestByZone = (zoneId) => {
  return request.get(`/harvest/zone/${zoneId}`)
}

export const createHarvest = (data) => {
  return request.post('/harvest', data)
}

export const getDiseasesByZone = (zoneId) => {
  return request.get(`/diseases/zone/${zoneId}`)
}

export const getActiveDiseases = () => {
  return request.get('/diseases/active')
}

export const createDisease = (data) => {
  return request.post('/diseases', data)
}

export const recoverDisease = (id) => {
  return request.patch(`/diseases/${id}/recover`)
}

export const getTraceabilityCodes = () => {
  return request.get('/traceability')
}

export const getTraceabilityByCode = (code) => {
  return request.get(`/traceability/code/${code}`)
}

export const getTraceabilityInfo = (code) => {
  return request.get(`/traceability/info/${code}`)
}

export const getPublicTraceabilityInfo = (code) => {
  return request.get(`/traceability/public/${code}`)
}

export const generateTraceability = (data) => {
  return request.post('/traceability', data)
}

export const generateTraceabilityForHarvest = (harvestId) => {
  return request.post(`/traceability/harvest/${harvestId}`)
}

export const generateTraceabilityForBatch = (batchNo) => {
  return request.post(`/traceability/batch/${batchNo}`)
}

export const getQrCodeUrl = (code) => {
  return `http://localhost:8080/api/traceability/qrcode/${code}`
}
