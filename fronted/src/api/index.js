import axios from 'axios'

const request = axios.create({
  baseURL: 'http://localhost:8081/api',
  timeout: 10000
})

// Request interceptor
request.interceptors.request.use(
  (config) => {
    console.log('Request URL:', config.url)
    console.log('Request data:', config.data)
    return config
  },
  (error) => {
    console.error('Request error:', error)
    return Promise.reject(error)
  }
)

// Response interceptor
request.interceptors.response.use(
  (response) => {
    console.log('Response:', response)
    return response
  },
  (error) => {
    console.error('Response error:', error)
    if (error.response) {
      console.error('Error response data:', error.response.data)
      console.error('Error status:', error.response.status)
    }
    return Promise.reject(error)
  }
)

export default request
