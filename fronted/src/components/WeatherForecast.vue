<template>
  <div class="weather-app">
    <div class="app-wrapper">
      <div class="top-bar">
        <div class="top-bar-left">
          <div class="brand">
            <el-icon class="brand-icon"><Ship /></el-icon>
            <span class="brand-text">海洋气象</span>
          </div>
          <div class="source-strip">
            <span class="src-tag" :class="dataSources.weather">
              <i class="src-dot"></i>天气
            </span>
            <span class="src-tag" :class="dataSources.marine">
              <i class="src-dot"></i>海洋
            </span>
            <span class="src-tag" :class="dataSources.tide">
              <i class="src-dot"></i>潮汐
            </span>
            <span class="src-tag" :class="dataSources.news">
              <i class="src-dot"></i>资讯
            </span>
          </div>
        </div>
        <div class="top-bar-right">
          <el-button class="top-btn" @click="getLocationWeather" :loading="locating" size="small">
            <el-icon><Location /></el-icon>
            <span class="top-btn-text">定位</span>
          </el-button>
          <el-button class="top-btn primary" @click="refreshWeather" :loading="loading" size="small">
            <el-icon><Refresh /></el-icon>
            <span class="top-btn-text">刷新</span>
          </el-button>
        </div>
      </div>

      <div class="search-section">
        <div class="search-box">
          <el-icon class="search-icon"><Search /></el-icon>
          <el-autocomplete
            v-model="city"
            :fetch-suggestions="querySearch"
            placeholder="搜索城市名称..."
            :popper-class="'weather-popper'"
            clearable
            @select="handleSelect"
            @keyup.enter="searchAndLoad"
            class="search-input"
          >
            <template #default="{ item }">
              <div class="popper-option">
                <span class="popper-name">{{ item.name }}</span>
                <span class="popper-region">{{ item.adm1 }}, {{ item.country }}</span>
              </div>
            </template>
          </el-autocomplete>
        </div>
      </div>

      <div v-if="error" class="error-bar">
        <el-alert :description="error" type="error" show-icon :closable="false" class="error-alert" />
      </div>

      <div v-else-if="weatherData" class="dashboard" v-loading="loading">
        <div class="hero-section">
          <div class="hero-grid">
            <div class="hero-left">
              <div class="hero-location">
                <el-icon class="loc-icon"><LocationFilled /></el-icon>
                <span class="loc-name">{{ weatherData.city }}</span>
                <span class="loc-date">{{ weatherData.updateTime }}</span>
              </div>
              <div class="hero-temp">
                <span class="temp-num">{{ Math.round(weatherData.temp) }}</span>
                <span class="temp-deg">°C</span>
              </div>
              <div class="hero-range">
                <span class="range-low">
                  <el-icon><ArrowDown /></el-icon>{{ Math.round(weatherData.tempLow) }}°
                </span>
                <span class="range-sep">~</span>
                <span class="range-high">
                  <el-icon><ArrowUp /></el-icon>{{ Math.round(weatherData.tempHigh) }}°
                </span>
                <span class="range-feel">体感 {{ Math.round(weatherData.feelsLike) }}°</span>
              </div>
            </div>
            <div class="hero-center">
              <div class="weather-visual">
                <el-icon class="weather-main-icon" :size="80">
                  <component :is="getWeatherIcon(weatherData.weather)" />
                </el-icon>
                <span class="weather-label">{{ weatherData.weather }}</span>
              </div>
            </div>
            <div class="hero-right">
              <div class="hero-metrics">
                <div class="hmetric">
                  <div class="hm-icon humidity"><el-icon><Drizzling /></el-icon></div>
                  <div class="hm-info">
                    <span class="hm-val">{{ weatherData.humidity }}%</span>
                    <span class="hm-lbl">湿度</span>
                  </div>
                </div>
                <div class="hmetric">
                  <div class="hm-icon wind"><el-icon><WindPower /></el-icon></div>
                  <div class="hm-info">
                    <span class="hm-val">{{ weatherData.wind }}</span>
                    <span class="hm-lbl">风力风向</span>
                  </div>
                </div>
                <div class="hmetric">
                  <div class="hm-icon visibility"><el-icon><View /></el-icon></div>
                  <div class="hm-info">
                    <span class="hm-val">{{ weatherData.visibility }}km</span>
                    <span class="hm-lbl">能见度</span>
                  </div>
                </div>
                <div class="hmetric">
                  <div class="hm-icon uv"><el-icon><Sunny /></el-icon></div>
                  <div class="hm-info">
                    <span class="hm-val">{{ weatherData.uv }}</span>
                    <span class="hm-lbl">紫外线</span>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>

        <el-tabs v-model="activeTab" class="main-tabs">
          <el-tab-pane name="weather">
            <template #label>
              <span class="tab-label">
                <el-icon><PartlyCloudy /></el-icon>
                <span>天气详情</span>
              </span>
            </template>
            <div class="tab-body">
              <div class="section">
                <div class="section-bar">
                  <el-icon><Calendar /></el-icon>
                  <span>7日预报</span>
                </div>
                <div class="forecast-grid">
                  <div
                    v-for="(item, index) in weatherData.forecast"
                    :key="index"
                    class="fc-card"
                    :class="{ today: index === 0 }"
                  >
                    <span class="fc-day">{{ index === 0 ? '今天' : item.date }}</span>
                    <span class="fc-week">{{ item.week }}</span>
                    <el-icon class="fc-weather" :size="30">
                      <component :is="getWeatherIcon(item.weatherDay)" />
                    </el-icon>
                    <div class="fc-range-bar">
                      <div class="fc-range-fill" :style="{ left: tempRangeLeft(item.tempLow), width: tempRangeWidth(item.tempLow, item.tempHigh) }"></div>
                    </div>
                    <div class="fc-temps">
                      <span class="fc-lo">{{ Math.round(item.tempLow) }}°</span>
                      <span class="fc-hi">{{ Math.round(item.tempHigh) }}°</span>
                    </div>
                    <span v-if="item.precipitation" class="fc-rain">
                      <el-icon><Umbrella /></el-icon>{{ item.precipitation }}mm
                    </span>
                  </div>
                </div>
              </div>

              <div class="section">
                <div class="section-bar">
                  <el-icon><Clock /></el-icon>
                  <span>逐时预报</span>
                </div>
                <div class="hourly-wrap">
                  <div class="hourly-scroll">
                    <div
                      v-for="(item, index) in weatherData.hourly"
                      :key="index"
                      class="hr-cell"
                      :class="{ current: index === 0 }"
                    >
                      <span class="hr-time">{{ index === 0 ? '现在' : item.time }}</span>
                      <el-icon class="hr-icon" :size="20"><component :is="getWeatherIcon(item.weather)" /></el-icon>
                      <span class="hr-temp">{{ Math.round(item.temp) }}°</span>
                      <span v-if="item.precip" class="hr-rain">{{ item.precip }}%</span>
                    </div>
                  </div>
                  <div class="scroll-shadow left"></div>
                  <div class="scroll-shadow right"></div>
                </div>
              </div>

              <div class="section">
                <div class="section-bar">
                  <el-icon><Grid /></el-icon>
                  <span>详细指标</span>
                </div>
                <div class="metrics-grid">
                  <div v-for="(detail, index) in weatherDetails" :key="index" class="metric-item">
                    <div class="mt-icon" :style="{ background: detail.color + '18', color: detail.color }">
                      <el-icon :size="18"><component :is="detail.icon" /></el-icon>
                    </div>
                    <div class="mt-body">
                      <span class="mt-label">{{ detail.label }}</span>
                      <span class="mt-value">{{ detail.value }}</span>
                    </div>
                  </div>
                </div>
              </div>

              <div class="section sun-section">
                <div class="section-bar">
                  <el-icon><Sunrise /></el-icon>
                  <span>日出日落</span>
                </div>
                <div class="sun-track">
                  <div class="sun-event-block rise">
                    <el-icon class="sun-big"><Sunrise /></el-icon>
                    <span class="sun-label">日出</span>
                    <span class="sun-time">{{ weatherData.sunrise }}</span>
                  </div>
                  <div class="sun-arc-area">
                    <svg viewBox="0 0 200 80" class="sun-arc-svg">
                      <defs>
                        <linearGradient id="sunGrad" x1="0" y1="0" x2="1" y2="0">
                          <stop offset="0%" stop-color="#ff9800" stop-opacity="0.6"/>
                          <stop offset="50%" stop-color="#ffc107" stop-opacity="0.8"/>
                          <stop offset="100%" stop-color="#ef5350" stop-opacity="0.6"/>
                        </linearGradient>
                      </defs>
                      <path d="M 20 70 Q 100 5 180 70" fill="none" stroke="url(#sunGrad)" stroke-width="2" stroke-dasharray="4,3"/>
                      <circle cx="100" cy="32" r="10" fill="#ffc107" opacity="0.9">
                        <animate attributeName="opacity" values="0.7;1;0.7" dur="3s" repeatCount="indefinite"/>
                      </circle>
                    </svg>
                    <span class="sun-duration">日照约 {{ daylightHours }}h</span>
                  </div>
                  <div class="sun-event-block set">
                    <el-icon class="sun-big"><Sunset /></el-icon>
                    <span class="sun-label">日落</span>
                    <span class="sun-time">{{ weatherData.sunset }}</span>
                  </div>
                </div>
              </div>
            </div>
          </el-tab-pane>

          <el-tab-pane name="marine">
            <template #label>
              <span class="tab-label">
                <el-icon><Ship /></el-icon>
                <span>海洋气象</span>
              </span>
            </template>
            <div class="tab-body">
              <div v-if="marineWeather" class="marine-panel">
                <div class="section">
                  <div class="section-bar">
                    <span class="bar-icon">🌊</span>
                    <span>实时海况</span>
                  </div>
                  <div class="marine-cards">
                    <div class="mc-card wave">
                      <div class="mc-head">
                        <span class="mc-title">有效浪高</span>
                        <el-tag size="small" effect="dark" type="info">当前</el-tag>
                      </div>
                      <div class="mc-value-row">
                        <span class="mc-num">{{ marineWeather.waveHeight.toFixed(1) }}</span>
                        <span class="mc-unit">m</span>
                      </div>
                      <div class="mc-foot">
                        <span>浪向 {{ marineWeather.waveDirection }}</span>
                        <span>周期 {{ marineWeather.wavePeriod.toFixed(1) }}s</span>
                      </div>
                    </div>
                    <div class="mc-card temp">
                      <div class="mc-head">
                        <span class="mc-title">海水温度</span>
                        <el-tag size="small" effect="dark" type="danger">当前</el-tag>
                      </div>
                      <div class="mc-value-row">
                        <span class="mc-num">{{ marineWeather.seaTemp ? marineWeather.seaTemp.toFixed(1) : '--' }}</span>
                        <span class="mc-unit">°C</span>
                      </div>
                      <div class="mc-foot">
                        <span>{{ marineWeather.seaTemp ? (marineWeather.seaTemp > 25 ? '适宜养殖' : '水温偏低') : '--' }}</span>
                      </div>
                    </div>
                    <div class="mc-card current">
                      <div class="mc-head">
                        <span class="mc-title">海流速度</span>
                        <el-tag size="small" effect="dark" type="primary">当前</el-tag>
                      </div>
                      <div class="mc-value-row">
                        <span class="mc-num">{{ marineWeather.currentVelocity.toFixed(2) }}</span>
                        <span class="mc-unit">m/s</span>
                      </div>
                      <div class="mc-foot">
                        <span>流向 {{ marineWeather.currentDirection }}</span>
                      </div>
                    </div>
                  </div>

                  <div class="marine-sub-grid">
                    <div class="msg-card">
                      <span class="msg-lbl">浪向</span>
                      <span class="msg-val">{{ marineWeather.waveDirection }}</span>
                    </div>
                    <div class="msg-card">
                      <span class="msg-lbl">浪周期</span>
                      <span class="msg-val">{{ marineWeather.wavePeriod.toFixed(1) }}s</span>
                    </div>
                    <div class="msg-card">
                      <span class="msg-lbl">海流方向</span>
                      <span class="msg-val">{{ marineWeather.currentDirection }}</span>
                    </div>
                    <div class="msg-card">
                      <span class="msg-lbl">数据来源</span>
                      <span class="msg-val src">{{ dataSources.marine === 'live' ? '实时观测' : dataSources.marine === 'mock' ? '推算数据' : '计算数据' }}</span>
                    </div>
                  </div>
                </div>

                <div v-if="marineWeather.forecast && marineWeather.forecast.length" class="section">
                  <div class="section-bar">
                    <span class="bar-icon">📈</span>
                    <span>海浪趋势预报</span>
                  </div>
                  <div class="wave-trend">
                    <div v-for="(item, idx) in marineWeather.forecast" :key="idx" class="wt-card">
                      <span class="wt-date">{{ item.date }}</span>
                      <div class="wt-main">
                        <span class="wt-wave-icon">🌊</span>
                        <span class="wt-height">{{ item.waveHeight.toFixed(1) }}<small>m</small></span>
                      </div>
                      <div class="wt-meta">
                        <span>{{ item.waveDirection }}</span>
                        <span class="wt-dot">·</span>
                        <span>{{ item.wavePeriod.toFixed(1) }}s</span>
                      </div>
                    </div>
                  </div>
                </div>

                <div v-if="tideData && tideData.length" class="section">
                  <div class="section-bar">
                    <span class="bar-icon">🌙</span>
                    <span>潮汐预报</span>
                    <el-tag size="small" effect="plain" class="tide-tag">
                      {{ dataSources.tide === 'computed' ? '天文推算' : '实时数据' }}
                    </el-tag>
                  </div>
                  <div class="tide-grid">
                    <div v-for="(day, idx) in tideData" :key="idx" class="tide-day-card">
                      <div class="td-header">{{ day.date }}</div>
                      <div class="td-records">
                        <div
                          v-for="(rec, ridx) in day.records"
                          :key="ridx"
                          class="td-rec"
                          :class="rec.type === '高潮' ? 'high' : 'low'"
                        >
                          <div class="td-wave-icon" :class="rec.type === '高潮' ? 'high' : 'low'">
                            <svg viewBox="0 0 24 24" width="24" height="24">
                              <path d="M2 12 Q6 6 10 12 Q14 18 18 12 Q22 6 24 12" fill="none" :stroke="rec.type === '高潮' ? '#ff9800' : '#00bcd4'" stroke-width="2"/>
                            </svg>
                          </div>
                          <div class="td-info">
                            <span class="td-time">{{ rec.time }}</span>
                            <span class="td-type" :class="rec.type === '高潮' ? 'high' : 'low'">{{ rec.type }}</span>
                          </div>
                          <span class="td-height">{{ rec.height }}</span>
                        </div>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
              <div v-else class="empty-state">
                <el-icon class="is-loading" :size="36"><Loading /></el-icon>
                <span>海洋数据加载中...</span>
              </div>
            </div>
          </el-tab-pane>

          <el-tab-pane name="news">
            <template #label>
              <span class="tab-label">
                <el-icon><Reading /></el-icon>
                <span>渔业资讯</span>
              </span>
            </template>
            <div class="tab-body">
              <div v-if="fisheryNews && fisheryNews.length" class="news-panel">
                <div class="section-bar">
                  <span class="bar-icon">📰</span>
                  <span>渔业养殖资讯</span>
                  <el-tag size="small" effect="plain" :type="dataSources.news === 'live' ? 'success' : 'warning'" class="news-tag">
                    {{ dataSources.news === 'live' ? '实时抓取' : '备用数据' }}
                  </el-tag>
                </div>
                <div class="news-grid">
                  <div v-for="(news, idx) in fisheryNews.slice(0, 8)" :key="idx" class="news-item">
                    <div class="ni-top">
                      <el-tag size="small" :type="getNewsTagType(news.category)" effect="dark">
                        {{ news.category }}
                      </el-tag>
                      <span class="ni-date">{{ news.pubDate }}</span>
                    </div>
                    <a
                      v-if="news.url && news.url !== '#'"
                      :href="news.url"
                      target="_blank"
                      class="ni-title"
                    >{{ news.title }}</a>
                    <span v-else class="ni-title static">{{ news.title }}</span>
                    <p class="ni-desc">{{ news.summary }}</p>
                    <div class="ni-foot">
                      <span class="ni-source">{{ news.source }}</span>
                      <a v-if="news.url && news.url !== '#'" :href="news.url" target="_blank" class="ni-link">
                        阅读原文 <el-icon><ArrowRight /></el-icon>
                      </a>
                    </div>
                  </div>
                </div>
              </div>
              <div v-else class="empty-state">
                <el-icon class="is-loading" :size="36"><Loading /></el-icon>
                <span>资讯加载中...</span>
              </div>
            </div>
          </el-tab-pane>
        </el-tabs>
      </div>

      <div v-else class="welcome">
        <div class="welcome-visual">
          <div class="welcome-ring">
            <el-icon :size="64"><PartlyCloudy /></el-icon>
          </div>
        </div>
        <h2 class="welcome-h2">海洋气象预报服务</h2>
        <p class="welcome-p">输入城市名称或点击定位，获取天气预报、海况数据及渔业资讯</p>
        <el-button class="welcome-btn" type="primary" size="large" round @click="getLocationWeather">
          <el-icon><Location /></el-icon>
          自动定位
        </el-button>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, computed, onMounted } from 'vue'
import { 
  WindPower, Drizzling, Sunny, View, Location, Refresh, 
  LocationFilled, ArrowDown, ArrowUp, Calendar, Clock,
  Sunrise, Sunset, Umbrella, Cloudy, MostlyCloudy,
  PartlyCloudy, Pouring, Lightning, Ship, Reading,
  Search, Grid, TrendCharts, Loading, ArrowRight
} from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import { searchCity, getWeather, getCurrentPosition } from '@/api/weather'
import { fetchFisheryNews, fetchTideData, fetchMarineWeather } from '@/api/fisheryNews'

export default {
  name: 'WeatherForecast',
  components: { 
    WindPower, Drizzling, Sunny, View, Location, Refresh,
    LocationFilled, ArrowDown, ArrowUp, Calendar, Clock,
    Sunrise, Sunset, Umbrella, Cloudy, MostlyCloudy,
    PartlyCloudy, Pouring, Lightning, Ship, Reading,
    Search, Grid, TrendCharts, Loading, ArrowRight
  },
  setup() {
    const city = ref('')
    const currentLocation = ref(null)
    const weatherData = ref(null)
    const loading = ref(false)
    const locating = ref(false)
    const error = ref('')
    const selectedCityName = ref('')
    const fisheryNews = ref([])
    const tideData = ref([])
    const marineWeather = ref(null)
    const activeTab = ref('weather')
    const dataSources = ref({
      weather: '--',
      marine: '--',
      tide: '--',
      news: '--'
    })

    const weatherDetails = computed(() => {
      if (!weatherData.value) return []
      return [
        { icon: WindPower, label: '风向风力', value: weatherData.value.wind, color: '#00d4ff' },
        { icon: Drizzling, label: '湿度', value: `${weatherData.value.humidity}%`, color: '#4facfe' },
        { icon: Sunny, label: '紫外线', value: weatherData.value.uv, color: '#ffc107' },
        { icon: View, label: '能见度', value: `${weatherData.value.visibility} km`, color: '#00ff88' },
        { icon: Cloudy, label: '云量', value: `${weatherData.value.cloudCover}%`, color: '#90a4ae' },
        { icon: Umbrella, label: '降水量', value: `${weatherData.value.precipitation} mm`, color: '#00bcd4' },
        { icon: Drizzling, label: '露点温度', value: `${Math.round(weatherData.value.dewPoint)}°C`, color: '#4dd0e1' },
        { icon: WindPower, label: '气压', value: `${Math.round(weatherData.value.pressure)} hPa`, color: '#8d6e63' }
      ]
    })

    const daylightHours = computed(() => {
      if (!weatherData.value) return '--'
      try {
        const [sh, sm] = weatherData.value.sunrise.split(':').map(Number)
        const [eh, em] = weatherData.value.sunset.split(':').map(Number)
        const diff = (eh * 60 + em - sh * 60 - sm) / 60
        return diff > 0 ? diff.toFixed(1) : '--'
      } catch { return '--' }
    })

    const forecastTempRange = computed(() => {
      if (!weatherData.value || !weatherData.value.forecast.length) return { min: 0, max: 40, range: 40 }
      const temps = weatherData.value.forecast.flatMap(f => [f.tempLow, f.tempHigh])
      return {
        min: Math.min(...temps) - 2,
        max: Math.max(...temps) + 2,
        range: Math.max(...temps) - Math.min(...temps) + 4
      }
    })

    const tempRangeLeft = (low) => {
      const { min, range } = forecastTempRange.value
      return `${((low - min) / range) * 100}%`
    }

    const tempRangeWidth = (low, high) => {
      const { range } = forecastTempRange.value
      const w = ((high - low) / range) * 100
      return `${Math.max(w, 10)}%`
    }

    const getWeatherIcon = (weather) => {
      const w = weather || ''
      if (w.includes('雷')) return Lightning
      if (w.includes('雪')) return MostlyCloudy
      if (w.includes('雨')) return Pouring
      if (w.includes('雾')) return PartlyCloudy
      if (w.includes('云') || w.includes('阴')) return Cloudy
      if (w.includes('毛毛')) return Drizzling
      return Sunny
    }

    const getNewsTagType = (category) => {
      const map = {
        '国际渔业': 'danger',
        '国内养殖': 'success',
        '政策法规': 'warning',
        '水产技术': 'info'
      }
      return map[category] || ''
    }

    const loadAncillaryData = async (lat, lon) => {
      fisheryNews.value = []
      tideData.value = []
      marineWeather.value = null
      dataSources.value.marine = '--'
      dataSources.value.tide = '--'
      dataSources.value.news = '--'
      
      try {
        const [newsResult, tideResult, marineResult] = await Promise.allSettled([
          fetchFisheryNews(),
          fetchTideData(lat),
          fetchMarineWeather(lat, lon)
        ])
        
        if (newsResult.status === 'fulfilled' && newsResult.value) {
          const { items, source } = newsResult.value
          if (items && items.length > 0) {
            fisheryNews.value = items
            dataSources.value.news = source
          }
        }
        if (tideResult.status === 'fulfilled' && tideResult.value) {
          const { items, source } = tideResult.value
          if (items && items.length > 0) {
            tideData.value = items
            dataSources.value.tide = source
          }
        }
        if (marineResult.status === 'fulfilled' && marineResult.value) {
          marineWeather.value = marineResult.value
          dataSources.value.marine = marineResult.value.source || 'live'
        }
      } catch (e) {
        console.warn('加载辅助数据失败:', e.message)
      }
    }

    const querySearch = async (queryString, cb) => {
      if (!queryString) { cb([]); return }
      try {
        const res = await searchCity(queryString)
        if (res.data.code === '200') {
          cb(res.data.location)
        } else { cb([]) }
      } catch (e) { cb([]) }
    }

    const handleSelect = (item) => {
      currentLocation.value = { lat: item.lat, lon: item.lon }
      selectedCityName.value = item.name
      city.value = item.name
      loadWeather()
    }

    const searchAndLoad = async () => {
      if (!city.value.trim()) { ElMessage.warning('请输入城市名称'); return }
      try {
        const res = await searchCity(city.value)
        if (res.data.code === '200' && res.data.location.length > 0) {
          const firstCity = res.data.location[0]
          currentLocation.value = { lat: firstCity.lat, lon: firstCity.lon }
          selectedCityName.value = firstCity.name
          loadWeather()
        } else {
          ElMessage.warning('未找到该城市，请尝试其他名称')
        }
      } catch (e) {
        ElMessage.error('搜索城市失败')
      }
    }

    const loadWeather = async () => {
      if (!currentLocation.value) { ElMessage.warning('请先选择或定位城市'); return }
      loading.value = true
      error.value = ''
      try {
        const lat = currentLocation.value.lat
        const lon = currentLocation.value.lon
        const data = await getWeather(lat, lon, selectedCityName.value || '当前城市')
        weatherData.value = data
        dataSources.value.weather = data.source || 'live'
        ElMessage.success('天气信息加载成功')
        loadAncillaryData(lat, lon)
      } catch (e) {
        error.value = `获取天气失败: ${e.message}`
      } finally {
        loading.value = false
      }
    }

    const refreshWeather = () => {
      if (weatherData.value) { loadWeather() }
      else { getLocationWeather() }
    }

    const getLocationWeather = async () => {
      locating.value = true
      error.value = ''
      try {
        const pos = await getCurrentPosition()
        currentLocation.value = { lat: pos.lat, lon: pos.lon }
        selectedCityName.value = '当前位置'
        city.value = '当前位置'
        ElMessage.success('定位成功')
        await loadWeather()
      } catch (e) {
        ElMessage.warning('定位失败，请手动输入城市')
      } finally {
        locating.value = false
      }
    }

    onMounted(() => {
      getLocationWeather().catch(() => {
        city.value = '北京'
        currentLocation.value = { lat: 39.9042, lon: 116.4074 }
        selectedCityName.value = '北京'
        loadWeather()
      })
    })

    return {
      city, weatherData, loading, locating, error, weatherDetails,
      fisheryNews, tideData, marineWeather, dataSources, activeTab,
      daylightHours,
      loadWeather, refreshWeather, getLocationWeather,
      querySearch, handleSelect, searchAndLoad,
      getWeatherIcon, getNewsTagType,
      tempRangeLeft, tempRangeWidth
    }
  }
}
</script>

<style scoped>
.weather-app {
  min-height: 100vh;
  padding: 20px;
  background: linear-gradient(170deg, #060d1a 0%, #0d1b2a 25%, #132742 50%, #0f1f35 75%, #080f1e 100%);
  display: flex;
  justify-content: center;
}

.app-wrapper {
  width: 100%;
  max-width: 1120px;
  background: rgba(12, 22, 40, 0.88);
  backdrop-filter: blur(24px);
  -webkit-backdrop-filter: blur(24px);
  border: 1px solid rgba(80, 150, 220, 0.12);
  border-radius: 24px;
  box-shadow: 
    0 30px 80px rgba(0, 0, 0, 0.55),
    0 0 0 1px rgba(255, 255, 255, 0.03) inset,
    0 1px 0 rgba(255, 255, 255, 0.04) inset;
  overflow: hidden;
}

.top-bar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 14px 28px;
  background: rgba(8, 18, 32, 0.6);
  border-bottom: 1px solid rgba(80, 150, 220, 0.08);
}

.top-bar-left {
  display: flex;
  align-items: center;
  gap: 24px;
}

.brand {
  display: flex;
  align-items: center;
  gap: 10px;
}

.brand-icon {
  font-size: 22px;
  color: #5b9bd5;
  filter: drop-shadow(0 0 8px rgba(91, 155, 213, 0.5));
}

.brand-text {
  font-size: 18px;
  font-weight: 700;
  color: #d6e8f8;
  letter-spacing: 1.5px;
}

.source-strip {
  display: flex;
  gap: 4px;
}

.src-tag {
  display: inline-flex;
  align-items: center;
  gap: 5px;
  font-size: 11px;
  padding: 4px 10px;
  border-radius: 14px;
  color: rgba(255, 255, 255, 0.3);
  background: rgba(255, 255, 255, 0.04);
  transition: all 0.3s;
}

.src-tag .src-dot {
  display: inline-block;
  width: 5px;
  height: 5px;
  border-radius: 50%;
  background: currentColor;
}

.src-tag.live { color: #4caf50; background: rgba(76, 175, 80, 0.1); }
.src-tag.computed { color: #42a5f5; background: rgba(66, 165, 245, 0.1); }
.src-tag.mock { color: #ff9800; background: rgba(255, 152, 0, 0.1); }

.top-bar-right {
  display: flex;
  gap: 10px;
}

.top-btn {
  background: rgba(255, 255, 255, 0.05) !important;
  border: 1px solid rgba(255, 255, 255, 0.08) !important;
  color: #a0c4e0 !important;
  border-radius: 10px !important;
  padding: 6px 14px !important;
  transition: all 0.3s;
}

.top-btn:hover {
  background: rgba(80, 150, 220, 0.15) !important;
  border-color: rgba(80, 150, 220, 0.25) !important;
  color: #7ab0e0 !important;
}

.top-btn.primary {
  background: rgba(33, 150, 243, 0.18) !important;
  border-color: rgba(33, 150, 243, 0.25) !important;
  color: #64b5f6 !important;
}

.top-btn.primary:hover {
  background: rgba(33, 150, 243, 0.32) !important;
}

.top-btn-text { margin-left: 5px; }

.search-section {
  padding: 16px 28px;
  background: rgba(8, 18, 32, 0.25);
  border-bottom: 1px solid rgba(80, 150, 220, 0.06);
}

.search-box {
  display: flex;
  align-items: center;
  background: rgba(255, 255, 255, 0.04);
  border: 1px solid rgba(80, 150, 220, 0.12);
  border-radius: 14px;
  padding: 0 16px;
  transition: all 0.3s;
}

.search-box:focus-within {
  border-color: rgba(80, 150, 220, 0.35);
  background: rgba(255, 255, 255, 0.06);
  box-shadow: 0 0 0 3px rgba(80, 150, 220, 0.08);
}

.search-icon {
  color: rgba(255, 255, 255, 0.35);
  font-size: 18px;
  flex-shrink: 0;
}

.search-input {
  flex: 1;
}

.search-input :deep(.el-input__wrapper) {
  background: transparent !important;
  border: none !important;
  box-shadow: none !important;
  padding: 10px 0 !important;
}

.search-input :deep(.el-input__inner) {
  color: #d6e8f8 !important;
  font-size: 14px;
  height: 32px;
}

.search-input :deep(.el-input__inner::placeholder) {
  color: rgba(255, 255, 255, 0.25);
}

.popper-option {
  display: flex;
  justify-content: space-between;
  width: 100%;
  padding: 4px 0;
}

.popper-name { color: #d6e8f8; font-weight: 500; }
.popper-region { color: rgba(255, 255, 255, 0.35); font-size: 12px; }

.error-bar { padding: 16px 28px; }

.error-alert {
  background: rgba(244, 67, 54, 0.1) !important;
  border: 1px solid rgba(244, 67, 54, 0.2) !important;
}

.dashboard { position: relative; }

.hero-section {
  padding: 32px 28px 28px;
  background: linear-gradient(180deg, rgba(33, 120, 220, 0.06) 0%, transparent 100%);
  border-bottom: 1px solid rgba(80, 150, 220, 0.06);
}

.hero-grid {
  display: grid;
  grid-template-columns: 1fr auto 1fr;
  align-items: center;
  gap: 24px;
}

.hero-location {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 14px;
}

.loc-icon {
  font-size: 20px;
  color: #5b9bd5;
}

.loc-name {
  font-size: 20px;
  font-weight: 700;
  color: #e8f2fc;
}

.loc-date {
  font-size: 12px;
  color: rgba(255, 255, 255, 0.3);
  margin-left: auto;
}

.hero-temp {
  display: flex;
  align-items: flex-start;
  line-height: 1;
  margin-bottom: 10px;
}

.temp-num {
  font-size: 96px;
  font-weight: 800;
  color: #e8f2fc;
  letter-spacing: -3px;
  text-shadow: 0 0 60px rgba(80, 150, 220, 0.25);
}

.temp-deg {
  font-size: 34px;
  font-weight: 500;
  color: rgba(255, 255, 255, 0.45);
  margin-top: 12px;
}

.hero-range {
  display: flex;
  align-items: center;
  gap: 10px;
  font-size: 14px;
}

.range-low {
  display: flex;
  align-items: center;
  gap: 3px;
  color: #5b9bd5;
}

.range-high {
  display: flex;
  align-items: center;
  gap: 3px;
  color: #ff8a65;
}

.range-sep {
  color: rgba(255, 255, 255, 0.2);
}

.range-feel {
  color: rgba(255, 255, 255, 0.35);
  margin-left: 8px;
  font-size: 13px;
}

.hero-center {
  display: flex;
  justify-content: center;
}

.weather-visual {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 10px;
}

.weather-main-icon {
  color: #ffc107;
  filter: drop-shadow(0 0 24px rgba(255, 193, 7, 0.35));
  animation: weatherPulse 3s ease-in-out infinite;
}

@keyframes weatherPulse {
  0%, 100% { transform: scale(1); }
  50% { transform: scale(1.06); }
}

.weather-label {
  font-size: 16px;
  color: rgba(255, 255, 255, 0.7);
  font-weight: 500;
}

.hero-metrics {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 10px;
}

.hmetric {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 14px;
  background: rgba(255, 255, 255, 0.025);
  border: 1px solid rgba(255, 255, 255, 0.05);
  border-radius: 14px;
  transition: all 0.3s;
}

.hmetric:hover {
  background: rgba(80, 150, 220, 0.06);
  border-color: rgba(80, 150, 220, 0.15);
  transform: translateY(-1px);
}

.hm-icon {
  width: 40px;
  height: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 11px;
  font-size: 18px;
  flex-shrink: 0;
}

.hm-icon.humidity { background: rgba(79, 172, 254, 0.15); color: #4facfe; }
.hm-icon.wind { background: rgba(0, 212, 255, 0.15); color: #00d4ff; }
.hm-icon.visibility { background: rgba(0, 255, 136, 0.12); color: #00ff88; }
.hm-icon.uv { background: rgba(255, 193, 7, 0.15); color: #ffc107; }

.hm-info {
  display: flex;
  flex-direction: column;
  gap: 2px;
  min-width: 0;
}

.hm-val {
  font-size: 14px;
  font-weight: 700;
  color: #e8f2fc;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.hm-lbl {
  font-size: 11px;
  color: rgba(255, 255, 255, 0.35);
}

.main-tabs {
  padding: 0 28px;
}

.main-tabs :deep(.el-tabs__header) {
  margin: 0;
  border-bottom: 1px solid rgba(80, 150, 220, 0.08);
}

.main-tabs :deep(.el-tabs__nav-wrap::after) {
  display: none;
}

.main-tabs :deep(.el-tabs__item) {
  color: rgba(255, 255, 255, 0.4) !important;
  font-size: 14px;
  font-weight: 500;
  height: 50px;
  line-height: 50px;
  padding: 0 28px;
  transition: all 0.3s;
}

.main-tabs :deep(.el-tabs__item:hover) {
  color: rgba(255, 255, 255, 0.7) !important;
}

.main-tabs :deep(.el-tabs__item.is-active) {
  color: #5b9bd5 !important;
  font-weight: 700;
}

.main-tabs :deep(.el-tabs__active-bar) {
  background: linear-gradient(90deg, #2196f3, #5b9bd5);
  height: 3px;
  border-radius: 2px;
}

.tab-label {
  display: flex;
  align-items: center;
  gap: 7px;
}

.tab-label .el-icon { font-size: 16px; }

.tab-body {
  padding: 24px 0 8px;
}

.section {
  margin-bottom: 32px;
}

.section-bar {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 16px;
  font-size: 15px;
  font-weight: 600;
  color: #8cb8e0;
}

.section-bar .el-icon { font-size: 17px; }

.bar-icon { font-size: 18px; }

.forecast-grid {
  display: grid;
  grid-template-columns: repeat(7, 1fr);
  gap: 8px;
}

.fc-card {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 18px 8px 14px;
  background: rgba(255, 255, 255, 0.025);
  border: 1px solid rgba(255, 255, 255, 0.05);
  border-radius: 16px;
  gap: 8px;
  transition: all 0.3s;
}

.fc-card:hover {
  background: rgba(80, 150, 220, 0.07);
  border-color: rgba(80, 150, 220, 0.18);
  transform: translateY(-3px);
}

.fc-card.today {
  background: rgba(33, 120, 220, 0.1);
  border-color: rgba(33, 120, 220, 0.22);
}

.fc-day {
  font-size: 13px;
  font-weight: 600;
  color: #8cb8e0;
}

.fc-week {
  font-size: 11px;
  color: rgba(255, 255, 255, 0.3);
}

.fc-weather {
  color: #ffc107;
  margin: 2px 0;
}

.fc-range-bar {
  width: 100%;
  height: 5px;
  background: rgba(255, 255, 255, 0.06);
  border-radius: 3px;
  position: relative;
  overflow: hidden;
}

.fc-range-fill {
  position: absolute;
  height: 100%;
  background: linear-gradient(90deg, #4facfe, #5b9bd5, #ff8a65);
  border-radius: 3px;
  transition: all 0.5s ease;
}

.fc-temps {
  display: flex;
  gap: 8px;
  font-size: 13px;
  font-weight: 600;
}

.fc-lo { color: #4facfe; }
.fc-hi { color: #ff8a65; }

.fc-rain {
  display: flex;
  align-items: center;
  gap: 3px;
  font-size: 11px;
  color: rgba(0, 188, 212, 0.65);
}

.hourly-wrap {
  position: relative;
}

.hourly-scroll {
  display: flex;
  gap: 8px;
  overflow-x: auto;
  padding-bottom: 10px;
  scrollbar-width: thin;
  scrollbar-color: rgba(255, 255, 255, 0.08) transparent;
}

.hourly-scroll::-webkit-scrollbar { height: 4px; }
.hourly-scroll::-webkit-scrollbar-thumb {
  background: rgba(255, 255, 255, 0.1);
  border-radius: 4px;
}

.scroll-shadow {
  position: absolute;
  top: 0;
  bottom: 0;
  width: 40px;
  pointer-events: none;
  z-index: 1;
}

.scroll-shadow.left {
  left: 0;
  background: linear-gradient(90deg, rgba(12, 22, 40, 0.9) 0%, transparent 100%);
}

.scroll-shadow.right {
  right: 0;
  background: linear-gradient(270deg, rgba(12, 22, 40, 0.9) 0%, transparent 100%);
}

.hr-cell {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 14px 12px;
  background: rgba(255, 255, 255, 0.025);
  border: 1px solid rgba(255, 255, 255, 0.05);
  border-radius: 13px;
  gap: 7px;
  min-width: 68px;
  flex-shrink: 0;
  transition: all 0.25s;
}

.hr-cell:hover {
  background: rgba(80, 150, 220, 0.06);
}

.hr-cell.current {
  background: rgba(33, 120, 220, 0.12);
  border-color: rgba(33, 120, 220, 0.2);
}

.hr-time {
  font-size: 12px;
  color: rgba(255, 255, 255, 0.4);
  font-weight: 500;
}

.hr-icon { color: #ffc107; }

.hr-temp {
  font-size: 16px;
  font-weight: 700;
  color: #e8f2fc;
}

.hr-rain {
  font-size: 11px;
  color: rgba(0, 188, 212, 0.6);
}

.metrics-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 10px;
}

.metric-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 14px 16px;
  background: rgba(255, 255, 255, 0.025);
  border: 1px solid rgba(255, 255, 255, 0.05);
  border-radius: 14px;
  transition: all 0.3s;
}

.metric-item:hover {
  background: rgba(255, 255, 255, 0.05);
  border-color: rgba(255, 255, 255, 0.1);
  transform: translateY(-2px);
}

.mt-icon {
  width: 38px;
  height: 38px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 10px;
  flex-shrink: 0;
}

.mt-body {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.mt-label {
  font-size: 11px;
  color: rgba(255, 255, 255, 0.35);
}

.mt-value {
  font-size: 15px;
  font-weight: 600;
  color: #e8f2fc;
}

.sun-section {
  padding: 24px 28px;
  background: linear-gradient(135deg, rgba(255, 152, 0, 0.05) 0%, rgba(244, 67, 54, 0.04) 100%);
  border: 1px solid rgba(255, 255, 255, 0.05);
  border-radius: 18px;
}

.sun-track {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.sun-event-block {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
  min-width: 80px;
}

.sun-big {
  font-size: 40px;
}

.sun-event-block.rise .sun-big { color: #ff9800; }
.sun-event-block.set .sun-big { color: #ef5350; }

.sun-label {
  font-size: 12px;
  color: rgba(255, 255, 255, 0.4);
}

.sun-time {
  font-size: 20px;
  font-weight: 700;
  color: #e8f2fc;
}

.sun-arc-area {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 4px;
  flex: 1;
}

.sun-arc-svg {
  width: 200px;
  height: 80px;
}

.sun-duration {
  font-size: 12px;
  color: rgba(255, 255, 255, 0.3);
}

.marine-cards {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 14px;
  margin-bottom: 16px;
}

.mc-card {
  padding: 20px;
  border-radius: 16px;
  border: 1px solid rgba(255, 255, 255, 0.05);
  transition: all 0.3s;
}

.mc-card:hover {
  transform: translateY(-2px);
}

.mc-card.wave {
  background: rgba(0, 188, 212, 0.06);
  border-color: rgba(0, 188, 212, 0.15);
}

.mc-card.temp {
  background: rgba(255, 87, 34, 0.06);
  border-color: rgba(255, 87, 34, 0.15);
}

.mc-card.current {
  background: rgba(33, 150, 243, 0.06);
  border-color: rgba(33, 150, 243, 0.15);
}

.mc-head {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 14px;
}

.mc-title {
  font-size: 13px;
  color: rgba(255, 255, 255, 0.45);
}

.mc-value-row {
  display: flex;
  align-items: baseline;
  gap: 6px;
  margin-bottom: 12px;
}

.mc-num {
  font-size: 36px;
  font-weight: 800;
  color: #e8f2fc;
  line-height: 1;
}

.mc-unit {
  font-size: 16px;
  color: rgba(255, 255, 255, 0.35);
}

.mc-foot {
  display: flex;
  gap: 14px;
  font-size: 12px;
  color: rgba(255, 255, 255, 0.35);
}

.marine-sub-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 10px;
}

.msg-card {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 16px;
  background: rgba(255, 255, 255, 0.025);
  border: 1px solid rgba(255, 255, 255, 0.05);
  border-radius: 13px;
  gap: 6px;
  transition: all 0.3s;
}

.msg-card:hover {
  background: rgba(255, 255, 255, 0.05);
}

.msg-lbl {
  font-size: 11px;
  color: rgba(255, 255, 255, 0.35);
}

.msg-val {
  font-size: 18px;
  font-weight: 600;
  color: #00ff88;
}

.msg-val.src {
  font-size: 13px;
  color: #5b9bd5;
}

.wave-trend {
  display: flex;
  gap: 12px;
}

.wt-card {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 20px 14px;
  background: rgba(0, 188, 212, 0.05);
  border: 1px solid rgba(0, 188, 212, 0.12);
  border-radius: 16px;
  gap: 12px;
  transition: all 0.3s;
}

.wt-card:hover {
  background: rgba(0, 188, 212, 0.1);
  transform: translateY(-2px);
}

.wt-date {
  font-size: 14px;
  font-weight: 600;
  color: #00d4ff;
}

.wt-main {
  display: flex;
  align-items: center;
  gap: 8px;
}

.wt-wave-icon { font-size: 28px; }

.wt-height {
  font-size: 32px;
  font-weight: 800;
  color: #e8f2fc;
}

.wt-height small {
  font-size: 14px;
  font-weight: 400;
  color: rgba(255, 255, 255, 0.35);
}

.wt-meta {
  display: flex;
  gap: 8px;
  font-size: 12px;
  color: rgba(255, 255, 255, 0.35);
}

.wt-dot { color: rgba(255, 255, 255, 0.15); }

.tide-tag {
  background: rgba(33, 150, 243, 0.12) !important;
  border-color: rgba(33, 150, 243, 0.2) !important;
  color: #5b9bd5 !important;
}

.tide-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 14px;
}

.tide-day-card {
  background: rgba(255, 255, 255, 0.025);
  border: 1px solid rgba(255, 255, 255, 0.05);
  border-radius: 15px;
  overflow: hidden;
}

.td-header {
  padding: 13px;
  background: rgba(33, 120, 220, 0.08);
  text-align: center;
  font-size: 14px;
  font-weight: 600;
  color: #5b9bd5;
}

.td-records {
  padding: 10px;
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.td-rec {
  display: flex;
  align-items: center;
  padding: 12px;
  border-radius: 11px;
  gap: 10px;
}

.td-rec.high {
  background: rgba(255, 152, 0, 0.07);
  border: 1px solid rgba(255, 152, 0, 0.12);
}

.td-rec.low {
  background: rgba(0, 188, 212, 0.07);
  border: 1px solid rgba(0, 188, 212, 0.12);
}

.td-wave-icon { flex-shrink: 0; }

.td-info {
  display: flex;
  flex-direction: column;
  gap: 2px;
  flex: 1;
}

.td-time {
  font-size: 16px;
  font-weight: 600;
  color: #e8f2fc;
}

.td-type {
  font-size: 12px;
}

.td-type.high { color: #ff9800; }
.td-type.low { color: #00bcd4; }

.td-height {
  font-size: 18px;
  font-weight: 700;
  color: #e8f2fc;
}

.news-tag { margin-left: auto; }

.news-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 12px;
}

.news-item {
  padding: 18px;
  background: rgba(255, 255, 255, 0.025);
  border: 1px solid rgba(255, 255, 255, 0.05);
  border-radius: 15px;
  transition: all 0.3s;
  display: flex;
  flex-direction: column;
}

.news-item:hover {
  background: rgba(80, 150, 220, 0.06);
  border-color: rgba(80, 150, 220, 0.15);
  transform: translateY(-2px);
}

.ni-top {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
}

.ni-date {
  font-size: 11px;
  color: rgba(255, 255, 255, 0.25);
}

.ni-title {
  font-size: 15px;
  font-weight: 600;
  color: #8cb8e0;
  text-decoration: none;
  line-height: 1.45;
  margin-bottom: 8px;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  transition: color 0.3s;
}

a.ni-title:hover {
  color: #5b9bd5;
}

.ni-title.static { cursor: default; }

.ni-desc {
  font-size: 13px;
  color: rgba(255, 255, 255, 0.4);
  line-height: 1.55;
  margin: 0 0 12px;
  flex: 1;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.ni-foot {
  display: flex;
  justify-content: space-between;
  align-items: center;
  border-top: 1px solid rgba(255, 255, 255, 0.05);
  padding-top: 12px;
}

.ni-source {
  font-size: 11px;
  color: rgba(255, 255, 255, 0.25);
}

.ni-link {
  font-size: 12px;
  color: #5b9bd5;
  text-decoration: none;
  display: flex;
  align-items: center;
  gap: 4px;
  transition: color 0.3s;
}

.ni-link:hover { color: #7ab0e0; }

.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 60px 20px;
  gap: 14px;
  color: rgba(255, 255, 255, 0.25);
  font-size: 14px;
}

.welcome {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 80px 20px;
  gap: 20px;
}

.welcome-visual { margin-bottom: 8px; }

.welcome-ring {
  width: 120px;
  height: 120px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 50%;
  background: rgba(80, 150, 220, 0.06);
  border: 2px solid rgba(80, 150, 220, 0.1);
  color: rgba(80, 150, 220, 0.3);
}

.welcome-h2 {
  margin: 0;
  font-size: 24px;
  font-weight: 600;
  color: rgba(255, 255, 255, 0.5);
}

.welcome-p {
  margin: 0;
  font-size: 14px;
  color: rgba(255, 255, 255, 0.3);
}

.welcome-btn {
  margin-top: 8px;
  padding: 12px 32px !important;
  font-size: 15px !important;
}

@media (max-width: 900px) {
  .weather-app { padding: 10px; }
  .hero-grid {
    grid-template-columns: 1fr;
    gap: 20px;
  }
  .hero-center { order: -1; }
  .weather-main-icon { font-size: 60px !important; }
  .temp-num { font-size: 64px; }
  .hero-metrics { grid-template-columns: repeat(4, 1fr); }
  .forecast-grid { grid-template-columns: repeat(4, 1fr); }
  .metrics-grid { grid-template-columns: repeat(2, 1fr); }
  .marine-cards { grid-template-columns: 1fr; }
  .marine-sub-grid { grid-template-columns: repeat(2, 1fr); }
  .wave-trend { flex-direction: column; }
  .tide-grid { grid-template-columns: 1fr; }
  .news-grid { grid-template-columns: 1fr; }
  .top-btn-text { display: none; }
  .source-strip { display: none; }
  .sun-arc-svg { width: 120px; }
  .top-bar { padding: 10px 16px; }
  .search-section { padding: 10px 16px; }
  .hero-section { padding: 20px 16px; }
  .main-tabs { padding: 0 12px; }
  .main-tabs :deep(.el-tabs__item) { padding: 0 16px; font-size: 13px; }
}

@media (max-width: 480px) {
  .hero-metrics { grid-template-columns: repeat(2, 1fr); }
  .forecast-grid { grid-template-columns: repeat(2, 1fr); }
}
</style>