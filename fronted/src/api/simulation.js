import request from './index'

export const getAllSimulations = () => {
  return request.get('/simulation')
}

export const getSimulationByZoneId = (zoneId) => {
  return request.get(`/simulation/zone/${zoneId}`)
}

export const saveSimulation = (zoneId, data) => {
  return request.post(`/simulation/zone/${zoneId}`, data)
}

export const deleteSimulation = (zoneId) => {
  return request.delete(`/simulation/zone/${zoneId}`)
}
