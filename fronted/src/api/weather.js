import axios from 'axios'

const GEOCODING_API = 'https://geocoding-api.open-meteo.com/v1'
const WEATHER_API = 'https://api.open-meteo.com/v1'

export const searchCity = async (location) => {
  try {
    console.log('搜索城市:', location)
    const res = await axios.get(`${GEOCODING_API}/search`, {
      params: {
        name: location,
        count: 10,
        language: 'zh',
        format: 'json'
      }
    })
    console.log('城市搜索响应:', res.data)
    return {
      data: {
        code: '200',
        location: (res.data.results || []).map(item => ({
          name: item.name,
          id: `${item.latitude},${item.longitude}`,
          adm1: item.admin1 || '',
          country: item.country || '',
          lon: item.longitude,
          lat: item.latitude
        }))
      }
    }
  } catch (error) {
    console.error('城市搜索失败:', error)
    throw error
  }
}

const weatherCodeMap = {
  0: '晴朗',
  1: '大部晴朗', 2: '局部多云', 3: '多云',
  45: '雾', 48: '雾凇',
  51: '小毛毛雨', 53: '毛毛雨', 55: '大毛毛雨',
  56: '冻毛毛雨', 57: '大冻毛毛雨',
  61: '小雨', 63: '雨', 65: '大雨',
  66: '冻雨', 67: '大冻雨',
  71: '小雪', 73: '雪', 75: '大雪',
  77: '雪粒',
  80: '小阵雨', 81: '阵雨', 82: '大阵雨',
  85: '小阵雪', 86: '大阵雪',
  95: '雷暴',
  96: '雷暴伴小冰雹', 99: '雷暴伴大冰雹'
}

export const getWeather = async (lat, lon, cityName = '未知城市') => {
  try {
    console.log('获取天气:', lat, lon)
    const res = await axios.get(`${WEATHER_API}/forecast`, {
      params: {
        latitude: lat,
        longitude: lon,
        current: ['temperature_2m', 'relative_humidity_2m', 'apparent_temperature', 'precipitation', 'weather_code', 'wind_speed_10m', 'wind_direction_10m', 'uv_index', 'visibility', 'surface_pressure', 'cloud_cover', 'dew_point_2m'],
        hourly: ['temperature_2m', 'weather_code', 'precipitation_probability'],
        daily: ['weather_code', 'temperature_2m_max', 'temperature_2m_min', 'precipitation_sum', 'uv_index_max', 'sunrise', 'sunset'],
        timezone: 'auto',
        forecast_days: 7
      }
    })
    console.log('天气API响应:', res.data)
    
    const data = res.data
    const current = data.current
    const daily = data.daily
    const hourly = data.hourly
    
    const now = new Date()
    const hourlyData = []
    if (hourly) {
      for (let i = 0; i < Math.min(24, hourly.time.length); i++) {
        const timeStr = hourly.time[i]
        if (timeStr) {
          const timeDate = new Date(timeStr)
          hourlyData.push({
            time: `${String(timeDate.getHours()).padStart(2, '0')}:00`,
            temp: hourly.temperature_2m[i],
            weather: weatherCodeMap[hourly.weather_code[i]] || '未知',
            precip: hourly.precipitation_probability[i] || 0
          })
        }
      }
    }
    
    return {
      city: cityName,
      source: 'live',
      updateTime: now.toLocaleString('zh-CN'),
      currentDate: `${now.getFullYear()}年${now.getMonth() + 1}月${now.getDate()}日 ${getWeekDay(now.toISOString())}`,
      temp: current.temperature_2m,
      tempLow: daily.temperature_2m_min[0],
      tempHigh: daily.temperature_2m_max[0],
      feelsLike: current.apparent_temperature,
      weather: weatherCodeMap[current.weather_code] || '未知',
      wind: getWindDirection(current.wind_direction_10m) + ' ' + current.wind_speed_10m + ' km/h',
      humidity: current.relative_humidity_2m,
      uv: getUVLevel(current.uv_index),
      visibility: Math.round((current.visibility || 10000) / 1000),
      cloudCover: current.cloud_cover ?? 0,
      precipitation: current.precipitation ?? daily.precipitation_sum[0] ?? 0,
      dewPoint: current.dew_point_2m ?? current.temperature_2m - 5,
      pressure: current.surface_pressure ?? 1013,
      sunrise: daily.sunrise ? daily.sunrise[0].split('T')[1] : '06:30',
      sunset: daily.sunset ? daily.sunset[0].split('T')[1] : '18:30',
      hourly: hourlyData,
      forecast: daily.time.map((date, index) => ({
        date: index === 0 ? '今天' : index === 1 ? '明天' : index === 2 ? '后天' : formatDate(date),
        week: getWeekDay(date),
        weatherDay: weatherCodeMap[daily.weather_code[index]] || '未知',
        weatherNight: weatherCodeMap[daily.weather_code[index]] || '未知',
        tempLow: daily.temperature_2m_min[index],
        tempHigh: daily.temperature_2m_max[index],
        precipitation: daily.precipitation_sum ? daily.precipitation_sum[index] : 0
      }))
    }
  } catch (error) {
    console.error('获取天气失败，使用备用数据:', error.message)
    const mock = getDynamicMockWeather(cityName)
    mock.source = 'mock'
    return mock
  }
}

const getWindDirection = (degree) => {
  const directions = ['北风', '东北风', '东风', '东南风', '南风', '西南风', '西风', '西北风']
  return directions[Math.round(degree / 45) % 8]
}

const getUVLevel = (uv) => {
  if (uv <= 2) return '弱'
  if (uv <= 5) return '中等'
  if (uv <= 7) return '强'
  if (uv <= 10) return '很强'
  return '极强'
}

const formatDate = (dateStr) => {
  const date = new Date(dateStr)
  return `${date.getMonth() + 1}月${date.getDate()}日`
}

const getWeekDay = (dateStr) => {
  const weekDays = ['周日', '周一', '周二', '周三', '周四', '周五', '周六']
  return weekDays[new Date(dateStr).getDay()]
}

export const getCurrentPosition = () => {
  return new Promise((resolve, reject) => {
    if (!navigator.geolocation) {
      reject(new Error('浏览器不支持地理定位'))
      return
    }
    navigator.geolocation.getCurrentPosition(
      (position) => {
        resolve({
          lon: position.coords.longitude,
          lat: position.coords.latitude
        })
      },
      (error) => {
        reject(error)
      }
    )
  })
}

function getDynamicMockWeather(cityName = '未知城市') {
  const now = new Date()
  const baseTemp = 18 + Math.floor(Math.random() * 10)
  const hourlyData = []
  
  for (let i = 0; i < 24; i++) {
    const h = (now.getHours() + i) % 24
    hourlyData.push({
      time: `${String(h).padStart(2, '0')}:00`,
      temp: baseTemp + Math.round(Math.sin((i - 6) * Math.PI / 12) * 4),
      weather: ['晴朗', '局部多云', '多云', '大部晴朗'][Math.floor(Math.random() * 4)],
      precip: Math.floor(Math.random() * 30)
    })
  }
  
  const weatherOptions = ['晴朗', '大部晴朗', '局部多云', '多云', '阴']
  const weekDays = ['周日', '周一', '周二', '周三', '周四', '周五', '周六']
  
  const forecast = []
  for (let i = 0; i < 7; i++) {
    const date = new Date(now.getTime() + i * 86400000)
    const low = baseTemp - 2 - Math.floor(Math.random() * 4)
    const high = baseTemp + 2 + Math.floor(Math.random() * 5)
    forecast.push({
      date: i === 0 ? '今天' : i === 1 ? '明天' : i === 2 ? '后天' : `${date.getMonth() + 1}月${date.getDate()}日`,
      week: weekDays[date.getDay()],
      weatherDay: weatherOptions[Math.floor(Math.random() * weatherOptions.length)],
      weatherNight: weatherOptions[Math.floor(Math.random() * weatherOptions.length)],
      tempLow: low,
      tempHigh: high,
      precipitation: Math.random() < 0.3 ? Math.round(Math.random() * 15 * 10) / 10 : 0
    })
  }

  return {
    city: cityName,
    updateTime: now.toLocaleString('zh-CN'),
    currentDate: `${now.getFullYear()}年${now.getMonth() + 1}月${now.getDate()}日 ${weekDays[now.getDay()]}`,
    temp: baseTemp,
    tempLow: forecast[0].tempLow,
    tempHigh: forecast[0].tempHigh,
    feelsLike: baseTemp - 1,
    weather: forecast[0].weatherDay,
    wind: ['东风', '南风', '西风', '北风', '东南风', '东北风'][Math.floor(Math.random() * 6)] + ' ' + (2 + Math.floor(Math.random() * 4)) + ' km/h',
    humidity: 50 + Math.floor(Math.random() * 40),
    uv: ['弱', '中等', '强'][Math.floor(Math.random() * 3)],
    visibility: 8 + Math.floor(Math.random() * 7),
    cloudCover: Math.floor(Math.random() * 100),
    precipitation: forecast[0].precipitation,
    dewPoint: baseTemp - 5 - Math.floor(Math.random() * 3),
    pressure: 1005 + Math.floor(Math.random() * 25),
    sunrise: `0${5 + Math.floor(Math.random() * 2)}:${String(Math.floor(Math.random() * 60)).padStart(2, '0')}`,
    sunset: `1${7 + Math.floor(Math.random() * 2)}:${String(Math.floor(Math.random() * 60)).padStart(2, '0')}`,
    hourly: hourlyData,
    forecast
  }
}

export { getDynamicMockWeather }
