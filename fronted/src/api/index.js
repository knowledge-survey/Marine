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
    
    // 添加用户信息到请求头
    const userStr = localStorage.getItem('user')
    if (userStr) {
      try {
        const user = JSON.parse(userStr)
        config.headers['X-User-Id'] = user.id
        config.headers['X-Username'] = user.username
      } catch (e) {
        console.error('解析用户信息失败:', e)
      }
    }
    
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
    const data = response.data
    if (data && typeof data === 'object' && Array.isArray(data.records)) {
      response.data = data.records
    }
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
