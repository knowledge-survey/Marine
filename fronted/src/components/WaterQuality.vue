<template>
  <div class="water-quality-app">
    <div class="app-wrapper">
      <div class="top-bar">
        <div class="top-bar-left">
          <div class="brand">
            <el-icon class="brand-icon"><Drizzling /></el-icon>
            <span class="brand-text">水质监测中心</span>
          </div>
          <div class="source-strip">
            <span class="src-tag live">
              <i class="src-dot"></i>实时数据
            </span>
          </div>
        </div>
        <div class="top-bar-right">
          <el-button class="top-btn primary" @click="showAddDialog" size="small">
            <el-icon><Plus /></el-icon>
            <span class="top-btn-text">添加记录</span>
          </el-button>
          <el-button class="top-btn" @click="loadData" :loading="loading" size="small">
            <el-icon><Refresh /></el-icon>
            <span class="top-btn-text">刷新</span>
          </el-button>
        </div>
      </div>

      <div v-if="latest" class="dashboard">
        <div class="hero-section">
          <div class="hero-grid">
            <div class="hero-left">
              <div class="hero-location">
                <el-icon class="loc-icon"><LocationFilled /></el-icon>
                <span class="loc-name">当前水域</span>
                <span class="loc-date">{{ updateTime }}</span>
              </div>
              <div class="hero-temp">
                <span class="temp-num">{{ latest.temperature || '--' }}</span>
                <span class="temp-deg">°C</span>
              </div>
              <div class="hero-range">
                <span class="range-low">
                  <el-icon><ArrowDown /></el-icon>{{ minTemp }}°
                </span>
                <span class="range-sep">~</span>
                <span class="range-high">
                  <el-icon><ArrowUp /></el-icon>{{ maxTemp }}°
                </span>
                <span class="range-feel">水温适宜度: {{ tempStatus }}</span>
              </div>
            </div>
            <div class="hero-center">
              <div class="water-visual">
                <el-icon class="water-main-icon" :size="80">
                  <Drizzling />
                </el-icon>
                <span class="water-label">{{ waterQualityStatus }}</span>
              </div>
            </div>
            <div class="hero-right">
              <div class="hero-metrics">
                <div class="hmetric">
                  <div class="hm-icon do">
                    <el-icon><Drizzling /></el-icon>
                  </div>
                  <div class="hm-info">
                    <span class="hm-val">{{ latest.dissolvedOxygen || '--' }}</span>
                    <span class="hm-lbl">溶解氧</span>
                    <span class="hm-unit">mg/L</span>
                  </div>
                </div>
                <div class="hmetric">
                  <div class="hm-icon ph">
                    <el-icon><Document /></el-icon>
                  </div>
                  <div class="hm-info">
                    <span class="hm-val">{{ latest.ph || '--' }}</span>
                    <span class="hm-lbl">pH值</span>
                  </div>
                </div>
                <div class="hmetric">
                  <div class="hm-icon salinity">
                    <el-icon><TrendCharts /></el-icon>
                  </div>
                  <div class="hm-info">
                    <span class="hm-val">{{ latest.salinity || '--' }}</span>
                    <span class="hm-lbl">盐度</span>
                  </div>
                </div>
                <div class="hmetric">
                  <div class="hm-icon status">
                    <el-icon><CircleCheck /></el-icon>
                  </div>
                  <div class="hm-info">
                    <span class="hm-val" :style="{ color: latest.isWarning ? '#ff4444' : '#00ff88' }">
                      {{ latest.isWarning ? '异常' : '正常' }}
                    </span>
                    <span class="hm-lbl">状态</span>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>

        <el-tabs v-model="activeTab" class="main-tabs">
          <el-tab-pane name="trends">
            <template #label>
              <span class="tab-label">
                <el-icon><TrendCharts /></el-icon>
                <span>趋势分析</span>
              </span>
            </template>
            <div class="tab-body">
              <div class="section">
                <div class="section-bar">
                  <el-icon><Calendar /></el-icon>
                  <span>近7日水质变化</span>
                </div>
                <div ref="trendChartRef" class="chart-container-large"></div>
              </div>

              <div class="section">
                <div class="section-bar">
                  <el-icon><Grid /></el-icon>
                  <span>详细指标</span>
                </div>
                <div class="metrics-grid">
                  <div class="metric-item">
                    <div class="mt-icon" style="background: rgba(255, 107, 53, 0.18); color: #ff6b35;">
                      <el-icon :size="18"><Sunny /></el-icon>
                    </div>
                    <div class="mt-body">
                      <span class="mt-label">水温</span>
                      <span class="mt-value">{{ latest?.temperature || '--' }}°C</span>
                    </div>
                  </div>
                  <div class="metric-item">
                    <div class="mt-icon" style="background: rgba(0, 212, 255, 0.18); color: #00d4ff;">
                      <el-icon :size="18"><Drizzling /></el-icon>
                    </div>
                    <div class="mt-body">
                      <span class="mt-label">溶解氧</span>
                      <span class="mt-value">{{ latest?.dissolvedOxygen || '--' }} mg/L</span>
                    </div>
                  </div>
                  <div class="metric-item">
                    <div class="mt-icon" style="background: rgba(0, 255, 136, 0.18); color: #00ff88;">
                      <el-icon :size="18"><Document /></el-icon>
                    </div>
                    <div class="mt-body">
                      <span class="mt-label">pH值</span>
                      <span class="mt-value">{{ latest?.ph || '--' }}</span>
                    </div>
                  </div>
                  <div class="metric-item">
                    <div class="mt-icon" style="background: rgba(255, 204, 0, 0.18); color: #ffcc00;">
                      <el-icon :size="18"><TrendCharts /></el-icon>
                    </div>
                    <div class="mt-body">
                      <span class="mt-label">盐度</span>
                      <span class="mt-value">{{ latest?.salinity || '--' }}</span>
                    </div>
                  </div>
                </div>
              </div>

              <div class="section">
                <div class="section-bar">
                  <el-icon><DataLine /></el-icon>
                  <span>各指标对比</span>
                </div>
                <div class="double-charts">
                  <div ref="tempChartRef" class="chart-container-half"></div>
                  <div ref="doChartRef" class="chart-container-half"></div>
                </div>
              </div>
            </div>
          </el-tab-pane>

          <el-tab-pane name="analysis">
            <template #label>
              <span class="tab-label">
                <el-icon><DataAnalysis /></el-icon>
                <span>数据分析</span>
              </span>
            </template>
            <div class="tab-body">
              <div class="analysis-grid">
                <div class="analysis-left">
                  <div class="section-bar">
                    <span class="bar-icon">📊</span>
                    <span>指标分布</span>
                  </div>
                  <div ref="radarChartRef" class="chart-container-radar"></div>
                </div>
                <div class="analysis-right">
                  <div class="section-bar">
                    <span class="bar-icon">📋</span>
                    <span>指标详情</span>
                  </div>
                  <div class="indicator-cards">
                    <div class="indicator-card" style="--accent: #ff6b35">
                      <div class="ic-color-dot"></div>
                      <div class="ic-body">
                        <span class="ic-label">水温</span>
                        <span class="ic-value">{{ avgTemp }} <small>°C</small></span>
                      </div>
                      <div class="ic-bar-bg">
                        <div class="ic-bar-fill" :style="{ width: calcPercent(avgTemp) }"></div>
                      </div>
                    </div>
                    <div class="indicator-card" style="--accent: #00d4ff">
                      <div class="ic-color-dot"></div>
                      <div class="ic-body">
                        <span class="ic-label">溶解氧</span>
                        <span class="ic-value">{{ avgDo }} <small>mg/L</small></span>
                      </div>
                      <div class="ic-bar-bg">
                        <div class="ic-bar-fill" :style="{ width: calcPercent(avgDo) }"></div>
                      </div>
                    </div>
                    <div class="indicator-card" style="--accent: #00ff88">
                      <div class="ic-color-dot"></div>
                      <div class="ic-body">
                        <span class="ic-label">pH值</span>
                        <span class="ic-value">{{ avgPh }}</span>
                      </div>
                      <div class="ic-bar-bg">
                        <div class="ic-bar-fill" :style="{ width: calcPercent(avgPh) }"></div>
                      </div>
                    </div>
                    <div class="indicator-card" style="--accent: #ffcc00">
                      <div class="ic-color-dot"></div>
                      <div class="ic-body">
                        <span class="ic-label">盐度</span>
                        <span class="ic-value">{{ avgSalinity }}</span>
                      </div>
                      <div class="ic-bar-bg">
                        <div class="ic-bar-fill" :style="{ width: calcPercent(avgSalinity) }"></div>
                      </div>
                    </div>
                  </div>
                </div>
              </div>

              <div class="section">
                <div class="section-bar">
                  <span class="bar-icon">📈</span>
                  <span>近7日统计</span>
                </div>
                <div class="stats-cards">
                  <div class="stat-card-box">
                    <div class="stat-label-box">平均水温</div>
                    <div class="stat-value-box">{{ avgTemp }}°C</div>
                    <div class="stat-trend" :style="{ color: tempTrendColor }">
                      <el-icon><TrendCharts /></el-icon>
                      {{ tempTrend }}
                    </div>
                  </div>
                  <div class="stat-card-box">
                    <div class="stat-label-box">平均溶解氧</div>
                    <div class="stat-value-box">{{ avgDo }} mg/L</div>
                    <div class="stat-trend" :style="{ color: doTrendColor }">
                      <el-icon><TrendCharts /></el-icon>
                      {{ doTrend }}
                    </div>
                  </div>
                  <div class="stat-card-box">
                    <div class="stat-label-box">记录总数</div>
                    <div class="stat-value-box">{{ waterQualityList.length }}</div>
                    <div class="stat-trend" style="color: #5b9bd5;">
                      <el-icon><Document /></el-icon>
                      条数据
                    </div>
                  </div>
                  <div class="stat-card-box">
                    <div class="stat-label-box">异常次数</div>
                    <div class="stat-value-box" :style="{ color: warningCount > 0 ? '#ff4444' : '#00ff88' }">
                      {{ warningCount }}
                    </div>
                    <div class="stat-trend" :style="{ color: warningCount > 0 ? '#ff4444' : '#00ff88' }">
                      <el-icon><Warning /></el-icon>
                      {{ warningCount > 0 ? '需关注' : '良好' }}
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </el-tab-pane>

          <el-tab-pane name="history">
            <template #label>
              <span class="tab-label">
                <el-icon><Clock /></el-icon>
                <span>历史记录</span>
              </span>
            </template>
            <div class="tab-body">
              <div class="section">
                <div class="section-bar">
                  <el-icon><Document /></el-icon>
                  <span>历史数据</span>
                </div>
                <div v-if="waterQualityList.length > 0" class="history-list">
                  <div v-for="(group, dateKey) in groupedHistory" :key="dateKey" class="history-day-card">
                    <div class="day-header">
                      <div class="day-left">
                        <span class="day-date">{{ group.date }}</span>
                        <el-tag :type="group.hasWarning ? 'danger' : 'success'" size="small" effect="dark">
                          {{ group.hasWarning ? '有异常' : '正常' }}
                        </el-tag>
                      </div>
                      <div class="day-right">
                        <span class="day-count">{{ group.records.length }} 条记录</span>
                        <span class="day-avg-temp">平均: {{ group.avgTemp }}°C</span>
                        <span class="day-range">{{ group.minTemp }}° ~ {{ group.maxTemp }}°</span>
                      </div>
                    </div>
                    <div class="day-records">
                      <div v-for="record in group.records" :key="record.id" class="record-item">
                        <div class="record-time">
                          <el-icon><Clock /></el-icon>
                          {{ formatTime(record.recordTime) }}
                        </div>
                        <div class="record-values">
                          <span class="val-item temp">
                            <span class="val-label">水温</span>
                            <span class="val-num">{{ record.temperature }}°C</span>
                          </span>
                          <span class="val-item do">
                            <span class="val-label">溶解氧</span>
                            <span class="val-num">{{ record.dissolvedOxygen }} mg/L</span>
                          </span>
                          <span class="val-item ph">
                            <span class="val-label">pH</span>
                            <span class="val-num">{{ record.ph }}</span>
                          </span>
                          <span class="val-item salinity">
                            <span class="val-label">盐度</span>
                            <span class="val-num">{{ record.salinity }}</span>
                          </span>
                        </div>
                        <div class="record-status">
                          <el-tag :type="record.isWarning ? 'danger' : 'success'" size="small">
                            {{ record.isWarning ? '异常' : '正常' }}
                          </el-tag>
                          <span v-if="record.isWarning" class="warning-text">{{ record.warningInfo }}</span>
                        </div>
                      </div>
                    </div>
                  </div>
                </div>
                <div v-else class="empty-state">
                  <el-icon :size="36"><Document /></el-icon>
                  <span>暂无历史数据</span>
                </div>
              </div>
            </div>
          </el-tab-pane>
        </el-tabs>
      </div>

      <div v-else class="welcome">
        <div class="welcome-visual">
          <div class="welcome-ring">
            <el-icon :size="64"><Drizzling /></el-icon>
          </div>
        </div>
        <h2 class="welcome-h2">水质监测系统</h2>
        <p class="welcome-p">请选择水域或添加水质记录开始监测</p>
        <el-button class="welcome-btn" type="primary" size="large" round @click="showAddDialog">
          <el-icon><Plus /></el-icon>
          添加记录
        </el-button>
      </div>
    </div>

    <el-dialog v-model="dialogVisible" title="添加水质记录" width="500px" class="add-dialog">
      <el-form :model="form" label-width="100px" class="add-form">
        <el-form-item label="水温">
          <el-input-number v-model="form.temperature" :step="0.1" :min="0" :max="40" class="full-width" />
        </el-form-item>
        <el-form-item label="溶解氧">
          <el-input-number v-model="form.dissolvedOxygen" :step="0.1" :min="0" :max="20" class="full-width" />
        </el-form-item>
        <el-form-item label="pH值">
          <el-input-number v-model="form.ph" :step="0.1" :min="0" :max="14" class="full-width" />
        </el-form-item>
        <el-form-item label="盐度">
          <el-input-number v-model="form.salinity" :step="0.1" :min="0" :max="50" class="full-width" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleAdd">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import { ref, watch, onMounted, nextTick, computed } from 'vue'
import * as echarts from 'echarts'
import {
  Plus, Refresh, LocationFilled, ArrowDown, ArrowUp,
  Drizzling, Document, TrendCharts, CircleCheck, Calendar,
  Grid, DataLine, DataAnalysis, Clock, Sunny, Warning
} from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import { getWaterQualityByZone, getLatestWaterQuality, getWaterQualityByRange, createWaterQuality } from '@/api/waterQuality'

export default {
  name: 'WaterQuality',
  components: {
    Plus, Refresh, LocationFilled, ArrowDown, ArrowUp,
    Drizzling, Document, TrendCharts, CircleCheck, Calendar,
    Grid, DataLine, DataAnalysis, Clock, Sunny, Warning
  },
  props: ['zoneId'],
  setup(props) {
    const trendChartRef = ref(null)
    const radarChartRef = ref(null)
    const tempChartRef = ref(null)
    const doChartRef = ref(null)

    let trendChart = null
    let radarChart = null
    let tempChart = null
    let doChart = null

    const waterQualityList = ref([])
    const waterQualityListForCharts = ref([])
    const latest = ref(null)
    const loading = ref(false)
    const dialogVisible = ref(false)
    const activeTab = ref('trends')
    const form = ref({
      temperature: null,
      dissolvedOxygen: null,
      ph: null,
      salinity: null
    })

    const updateTime = computed(() => {
      if (!latest.value) return ''
      const date = new Date(latest.value.recordTime)
      return date.toLocaleString('zh-CN')
    })

    const tempStatus = computed(() => {
      if (!latest.value) return '--'
      const temp = parseFloat(latest.value.temperature)
      if (temp >= 15 && temp <= 28) return '适宜'
      if (temp >= 10 && temp <= 30) return '一般'
      return '需关注'
    })

    const waterQualityStatus = computed(() => {
      if (!latest.value) return '--'
      return latest.value.isWarning ? '水质异常' : '水质良好'
    })

    const minTemp = computed(() => {
      if (waterQualityListForCharts.value.length === 0) return '--'
      return Math.min(...waterQualityListForCharts.value.map(item => parseFloat(item.temperature) || 0)).toFixed(1)
    })

    const maxTemp = computed(() => {
      if (waterQualityListForCharts.value.length === 0) return '--'
      return Math.max(...waterQualityListForCharts.value.map(item => parseFloat(item.temperature) || 0)).toFixed(1)
    })

    const avgTemp = computed(() => {
      if (waterQualityListForCharts.value.length === 0) return '--'
      const sum = waterQualityListForCharts.value.reduce((sum, item) => sum + (parseFloat(item.temperature) || 0), 0)
      return (sum / waterQualityListForCharts.value.length).toFixed(1)
    })

    const avgDo = computed(() => {
      if (waterQualityListForCharts.value.length === 0) return '--'
      const sum = waterQualityListForCharts.value.reduce((sum, item) => sum + (parseFloat(item.dissolvedOxygen) || 0), 0)
      return (sum / waterQualityListForCharts.value.length).toFixed(1)
    })

    const avgPh = computed(() => {
      if (waterQualityListForCharts.value.length === 0) return '--'
      const sum = waterQualityListForCharts.value.reduce((sum, item) => sum + (parseFloat(item.ph) || 0), 0)
      return (sum / waterQualityListForCharts.value.length).toFixed(1)
    })

    const avgSalinity = computed(() => {
      if (waterQualityListForCharts.value.length === 0) return '--'
      const sum = waterQualityListForCharts.value.reduce((sum, item) => sum + (parseFloat(item.salinity) || 0), 0)
      return (sum / waterQualityListForCharts.value.length).toFixed(1)
    })

    const warningCount = computed(() => {
      return waterQualityList.value.filter(item => item.isWarning).length
    })

    const tempTrend = computed(() => '平稳')
    const tempTrendColor = computed(() => '#00ff88')
    const doTrend = computed(() => '平稳')
    const doTrendColor = computed(() => '#00ff88')

    const calcPercent = (val) => {
      if (waterQualityListForCharts.value.length === 0) return '0%'
      const dailyData = getDailyAverageData(waterQualityListForCharts.value)
      const allVals = [
        parseFloat(dailyData.reduce((s, i) => s + (parseFloat(i.temp) || 0), 0) / dailyData.length),
        parseFloat(dailyData.reduce((s, i) => s + (parseFloat(i.do) || 0), 0) / dailyData.length),
        parseFloat(dailyData.reduce((s, i) => s + (parseFloat(i.ph) || 0), 0) / dailyData.length),
        parseFloat(dailyData.reduce((s, i) => s + (parseFloat(i.salinity) || 0), 0) / dailyData.length)
      ]
      const maxVal = Math.max(...allVals, 1)
      const numVal = parseFloat(val) || 0
      return `${(numVal / maxVal * 100).toFixed(0)}%`
    }

    const groupedHistory = computed(() => {
      const groups = {}
      waterQualityList.value.forEach(item => {
        const date = new Date(item.recordTime)
        const dateKey = `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')}`
        if (!groups[dateKey]) {
          groups[dateKey] = {
            date: dateKey,
            records: [],
            avgTemp: 0,
            minTemp: Infinity,
            maxTemp: -Infinity,
            hasWarning: false
          }
        }
        groups[dateKey].records.push(item)
        const temp = parseFloat(item.temperature) || 0
        groups[dateKey].avgTemp += temp
        if (temp < groups[dateKey].minTemp) groups[dateKey].minTemp = temp
        if (temp > groups[dateKey].maxTemp) groups[dateKey].maxTemp = temp
        if (item.isWarning) groups[dateKey].hasWarning = true
      })
      Object.values(groups).forEach(g => {
        g.avgTemp = (g.avgTemp / g.records.length).toFixed(2)
        g.minTemp = g.minTemp === Infinity ? 0 : g.minTemp.toFixed(2)
        g.maxTemp = g.maxTemp === -Infinity ? 0 : g.maxTemp.toFixed(2)
      })
      return groups
    })

    const formatTime = (timeStr) => {
      const date = new Date(timeStr)
      return date.toLocaleTimeString('zh-CN', { hour: '2-digit', minute: '2-digit' })
    }

    const loadData = async () => {
      if (!props.zoneId) return

      loading.value = true
      try {
        const now = new Date()
        const endTime = new Date(now.getFullYear(), now.getMonth(), now.getDate(), 23, 59, 59).toISOString()
        const startTime = new Date(now.getTime() - 6 * 24 * 60 * 60 * 1000)
        startTime.setHours(0, 0, 0, 0)

        const [listRes, latestRes, rangeRes] = await Promise.all([
          getWaterQualityByZone(props.zoneId),
          getLatestWaterQuality(props.zoneId),
          getWaterQualityByRange(props.zoneId, startTime.toISOString(), endTime)
        ])
        waterQualityList.value = listRes.data.reverse()
        latest.value = latestRes.data
        waterQualityListForCharts.value = rangeRes.data
        nextTick(() => {
          initCharts()
          updateCharts()
        })
      } catch (error) {
        console.error(error)
      } finally {
        loading.value = false
      }
    }

    const initCharts = () => {
      if (trendChartRef.value) {
        trendChart = echarts.init(trendChartRef.value)
      }
      if (radarChartRef.value) {
        radarChart = echarts.init(radarChartRef.value)
      }
      if (tempChartRef.value) {
        tempChart = echarts.init(tempChartRef.value)
      }
      if (doChartRef.value) {
        doChart = echarts.init(doChartRef.value)
      }

      window.addEventListener('resize', () => {
        trendChart?.resize()
        radarChart?.resize()
        tempChart?.resize()
        doChart?.resize()
      })
    }

    const getDailyAverageData = (dataList) => {
      const dailyMap = {}
      dataList.forEach(item => {
        const date = new Date(item.recordTime)
        const dateStr = `${date.getMonth() + 1}/${date.getDate()}`

        if (!dailyMap[dateStr]) {
          dailyMap[dateStr] = {
            date: dateStr,
            temps: [],
            dos: [],
            phs: [],
            salinities: []
          }
        }
        dailyMap[dateStr].temps.push(parseFloat(item.temperature) || 0)
        dailyMap[dateStr].dos.push(parseFloat(item.dissolvedOxygen) || 0)
        dailyMap[dateStr].phs.push(parseFloat(item.ph) || 0)
        dailyMap[dateStr].salinities.push(parseFloat(item.salinity) || 0)
      })

      return Object.values(dailyMap).map(d => ({
        date: d.date,
        temp: d.temps.length ? (d.temps.reduce((a, b) => a + b, 0) / d.temps.length).toFixed(2) : null,
        do: d.dos.length ? (d.dos.reduce((a, b) => a + b, 0) / d.dos.length).toFixed(2) : null,
        ph: d.phs.length ? (d.phs.reduce((a, b) => a + b, 0) / d.phs.length).toFixed(2) : null,
        salinity: d.salinities.length ? (d.salinities.reduce((a, b) => a + b, 0) / d.salinities.length).toFixed(2) : null
      }))
    }

    const updateCharts = () => {
      updateTrendChart()
      updateRadarChart()
      updateTempChart()
      updateDoChart()
    }

    const updateTrendChart = () => {
      if (!trendChart) return

      const dailyData = getDailyAverageData(waterQualityListForCharts.value)

      const now = new Date()
      const full7Days = []
      for (let i = 6; i >= 0; i--) {
        const date = new Date(now.getTime() - i * 24 * 60 * 60 * 1000)
        const dateStr = `${date.getMonth() + 1}/${date.getDate()}`
        const existingData = dailyData.find(d => d.date === dateStr)
        if (existingData) {
          full7Days.push(existingData)
        } else {
          full7Days.push({ date: dateStr, temp: '-', do: '-', ph: '-', salinity: '-' })
        }
      }

      trendChart.setOption({
        backgroundColor: 'transparent',
        tooltip: {
          trigger: 'axis',
          backgroundColor: 'rgba(20, 40, 60, 0.9)',
          borderColor: '#5b9bd5',
          textStyle: { color: '#fff' }
        },
        legend: {
          data: ['水温', '溶解氧', 'pH', '盐度'],
          textStyle: { color: 'rgba(255,255,255,0.7)' },
          top: 0
        },
        grid: {
          left: '3%',
          right: '4%',
          bottom: '3%',
          containLabel: true
        },
        xAxis: {
          type: 'category',
          boundaryGap: false,
          data: full7Days.map(item => item.date),
          axisLine: { lineStyle: { color: 'rgba(80, 150, 220, 0.3)' } },
          axisLabel: { color: 'rgba(255,255,255,0.6)' }
        },
        yAxis: {
          type: 'value',
          axisLine: { lineStyle: { color: 'rgba(80, 150, 220, 0.3)' } },
          axisLabel: { color: 'rgba(255,255,255,0.6)' },
          splitLine: { lineStyle: { color: 'rgba(80, 150, 220, 0.1)' } }
        },
        series: [
          {
            name: '水温',
            type: 'line',
            smooth: true,
            data: full7Days.map(item => item.temp),
            lineStyle: { color: '#ff6b35' },
            connectNulls: true,
            areaStyle: {
              color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
                { offset: 0, color: 'rgba(255, 107, 53, 0.3)' },
                { offset: 1, color: 'rgba(255, 107, 53, 0.05)' }
              ])
            }
          },
          {
            name: '溶解氧',
            type: 'line',
            smooth: true,
            data: full7Days.map(item => item.do),
            lineStyle: { color: '#00d4ff' },
            connectNulls: true,
            areaStyle: {
              color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
                { offset: 0, color: 'rgba(0, 212, 255, 0.3)' },
                { offset: 1, color: 'rgba(0, 212, 255, 0.05)' }
              ])
            }
          },
          {
            name: 'pH',
            type: 'line',
            smooth: true,
            data: full7Days.map(item => item.ph),
            lineStyle: { color: '#00ff88' },
            connectNulls: true,
            areaStyle: {
              color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
                { offset: 0, color: 'rgba(0, 255, 136, 0.3)' },
                { offset: 1, color: 'rgba(0, 255, 136, 0.05)' }
              ])
            }
          },
          {
            name: '盐度',
            type: 'line',
            smooth: true,
            data: full7Days.map(item => item.salinity),
            lineStyle: { color: '#ffcc00' },
            connectNulls: true,
            areaStyle: {
              color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
                { offset: 0, color: 'rgba(255, 204, 0, 0.3)' },
                { offset: 1, color: 'rgba(255, 204, 0, 0.05)' }
              ])
            }
          }
        ]
      })
    }

    const updateRadarChart = () => {
      if (!radarChart) return

      const dailyData = getDailyAverageData(waterQualityListForCharts.value)
      if (dailyData.length === 0) return

      const avgTemp = dailyData.reduce((sum, item) => sum + (parseFloat(item.temp) || 0), 0) / dailyData.length
      const avgDo = dailyData.reduce((sum, item) => sum + (parseFloat(item.do) || 0), 0) / dailyData.length
      const avgPh = dailyData.reduce((sum, item) => sum + (parseFloat(item.ph) || 0), 0) / dailyData.length
      const avgSalinity = dailyData.reduce((sum, item) => sum + (parseFloat(item.salinity) || 0), 0) / dailyData.length

      const maxTemp = Math.max(...dailyData.map(item => parseFloat(item.temp) || 0), avgTemp, 1)
      const maxDo = Math.max(...dailyData.map(item => parseFloat(item.do) || 0), avgDo, 1)
      const maxPh = Math.max(...dailyData.map(item => parseFloat(item.ph) || 0), avgPh, 1)
      const maxSalinity = Math.max(...dailyData.map(item => parseFloat(item.salinity) || 0), avgSalinity, 1)

      radarChart.setOption({
        backgroundColor: 'transparent',
        tooltip: {
          trigger: 'item',
          backgroundColor: 'rgba(20, 40, 60, 0.95)',
          borderColor: '#5b9bd5',
          borderWidth: 1,
          padding: [10, 14],
          textStyle: { color: '#fff', fontSize: 13 }
        },
        legend: {
          data: ['平均值'],
          textStyle: { color: 'rgba(255,255,255,0.7)' },
          bottom: 0
        },
        radar: {
          indicator: [
            { name: '水温', max: Math.ceil(maxTemp * 1.2) },
            { name: '溶解氧', max: Math.ceil(maxDo * 1.2) },
            { name: 'pH值', max: Math.ceil(maxPh * 1.2) },
            { name: '盐度', max: Math.ceil(maxSalinity * 1.2) }
          ],
          center: ['50%', '50%'],
          radius: '65%',
          shape: 'polygon',
          splitNumber: 4,
          axisName: {
            color: 'rgba(255,255,255,0.85)',
            fontSize: 14,
            fontWeight: 600
          },
          splitLine: {
            lineStyle: {
              color: 'rgba(80, 150, 220, 0.2)'
            }
          },
          splitArea: {
            show: true,
            areaStyle: {
              color: [
                'rgba(80, 150, 220, 0.05)',
                'rgba(80, 150, 220, 0.02)',
                'rgba(80, 150, 220, 0.05)',
                'rgba(80, 150, 220, 0.02)'
              ]
            }
          },
          axisLine: {
            lineStyle: {
              color: 'rgba(80, 150, 220, 0.25)'
            }
          }
        },
        series: [
          {
            name: '水质指标',
            type: 'radar',
            data: [
              {
                value: [
                  parseFloat(avgTemp.toFixed(2)),
                  parseFloat(avgDo.toFixed(2)),
                  parseFloat(avgPh.toFixed(2)),
                  parseFloat(avgSalinity.toFixed(2))
                ],
                name: '平均值',
                symbol: 'circle',
                symbolSize: 8,
                lineStyle: {
                  color: '#5b9bd5',
                  width: 2
                },
                itemStyle: {
                  color: '#5b9bd5',
                  borderColor: '#fff',
                  borderWidth: 2
                },
                areaStyle: {
                  color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
                    { offset: 0, color: 'rgba(91, 155, 213, 0.4)' },
                    { offset: 1, color: 'rgba(91, 155, 213, 0.1)' }
                  ])
                }
              }
            ]
          }
        ]
      })
    }

    const updateTempChart = () => {
      if (!tempChart) return

      const dailyData = getDailyAverageData(waterQualityListForCharts.value)

      const now = new Date()
      const full7Days = []
      for (let i = 6; i >= 0; i--) {
        const date = new Date(now.getTime() - i * 24 * 60 * 60 * 1000)
        const dateStr = `${date.getMonth() + 1}/${date.getDate()}`
        const existingData = dailyData.find(d => d.date === dateStr)
        if (existingData) {
          full7Days.push(existingData)
        } else {
          full7Days.push({ date: dateStr, temp: null })
        }
      }

      tempChart.setOption({
        backgroundColor: 'transparent',
        title: {
          text: '水温变化',
          left: 'center',
          textStyle: { color: 'rgba(255,255,255,0.7)', fontSize: 14 }
        },
        tooltip: {
          trigger: 'axis',
          backgroundColor: 'rgba(20, 40, 60, 0.9)',
          borderColor: '#5b9bd5',
          textStyle: { color: '#fff' }
        },
        grid: {
          left: '10%',
          right: '10%',
          bottom: '15%',
          top: '20%',
          containLabel: true
        },
        xAxis: {
          type: 'category',
          data: full7Days.map(item => item.date),
          axisLine: { lineStyle: { color: 'rgba(80, 150, 220, 0.3)' } },
          axisLabel: { color: 'rgba(255,255,255,0.6)' }
        },
        yAxis: {
          type: 'value',
          name: '°C',
          axisLine: { lineStyle: { color: 'rgba(80, 150, 220, 0.3)' } },
          axisLabel: { color: 'rgba(255,255,255,0.6)' },
          splitLine: { lineStyle: { color: 'rgba(80, 150, 220, 0.1)' } }
        },
        series: [
          {
            name: '水温',
            type: 'bar',
            data: full7Days.map(item => item.temp),
            itemStyle: {
              color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
                { offset: 0, color: '#ff6b35' },
                { offset: 1, color: '#ff9f43' }
              ]),
              borderRadius: [4, 4, 0, 0]
            }
          }
        ]
      })
    }

    const updateDoChart = () => {
      if (!doChart) return

      const dailyData = getDailyAverageData(waterQualityListForCharts.value)

      const now = new Date()
      const full7Days = []
      for (let i = 6; i >= 0; i--) {
        const date = new Date(now.getTime() - i * 24 * 60 * 60 * 1000)
        const dateStr = `${date.getMonth() + 1}/${date.getDate()}$`
        const existingData = dailyData.find(d => d.date === dateStr.replace('$', ''))
        if (existingData) {
          full7Days.push(existingData)
        } else {
          full7Days.push({ date: dateStr.replace('$', ''), do: null })
        }
      }

      doChart.setOption({
        backgroundColor: 'transparent',
        title: {
          text: '溶解氧变化',
          left: 'center',
          textStyle: { color: 'rgba(255,255,255,0.7)', fontSize: 14 }
        },
        tooltip: {
          trigger: 'axis',
          backgroundColor: 'rgba(20, 40, 60, 0.9)',
          borderColor: '#5b9bd5',
          textStyle: { color: '#fff' }
        },
        grid: {
          left: '10%',
          right: '10%',
          bottom: '15%',
          top: '20%',
          containLabel: true
        },
        xAxis: {
          type: 'category',
          data: full7Days.map(item => item.date),
          axisLine: { lineStyle: { color: 'rgba(80, 150, 220, 0.3)' } },
          axisLabel: { color: 'rgba(255,255,255,0.6)' }
        },
        yAxis: {
          type: 'value',
          name: 'mg/L',
          axisLine: { lineStyle: { color: 'rgba(80, 150, 220, 0.3)' } },
          axisLabel: { color: 'rgba(255,255,255,0.6)' },
          splitLine: { lineStyle: { color: 'rgba(80, 150, 220, 0.1)' } }
        },
        series: [
          {
            name: '溶解氧',
            type: 'bar',
            data: full7Days.map(item => item.do),
            itemStyle: {
              color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
                { offset: 0, color: '#00d4ff' },
                { offset: 1, color: '#0099ff' }
              ]),
              borderRadius: [4, 4, 0, 0]
            }
          }
        ]
      })
    }

    const showAddDialog = () => {
      form.value = { temperature: null, dissolvedOxygen: null, ph: null, salinity: null }
      dialogVisible.value = true
    }

    const handleAdd = async () => {
      try {
        await createWaterQuality({
          ...form.value,
          zoneId: props.zoneId
        })
        dialogVisible.value = false
        ElMessage.success('添加成功')
        loadData()
      } catch (error) {
        console.error('添加失败:', error)
        ElMessage.error('添加失败: ' + (error.response?.data?.message || error.message))
      }
    }

    watch(() => props.zoneId, () => {
      loadData()
    })

    onMounted(() => {
      loadData()
    })

    return {
      trendChartRef,
      radarChartRef,
      tempChartRef,
      doChartRef,
      waterQualityList,
      latest,
      loading,
      dialogVisible,
      activeTab,
      form,
      updateTime,
      tempStatus,
      waterQualityStatus,
      minTemp,
      maxTemp,
      avgTemp,
      avgDo,
      avgPh: avgPh,
      avgSalinity: avgSalinity,
      warningCount,
      tempTrend,
      tempTrendColor,
      doTrend,
      doTrendColor,
      groupedHistory,
      formatTime,
      calcPercent,
      loadData,
      showAddDialog,
      handleAdd
    }
  }
}
</script>

<style scoped>
.water-quality-app {
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
  filter: drop-shadow(0 0 8px rgba(80, 150, 220, 0.5));
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

.src-tag.live { color: #00ff88; background: rgba(0, 255, 136, 0.1); }

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

.water-visual {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 10px;
}

.water-main-icon {
  color: #00d4ff;
  filter: drop-shadow(0 0 24px rgba(0, 212, 255, 0.35));
  animation: waterPulse 3s ease-in-out infinite;
}

@keyframes waterPulse {
  0%, 100% { transform: scale(1); }
  50% { transform: scale(1.06); }
}

.water-label {
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

.hm-icon.do { background: rgba(0, 212, 255, 0.15); color: #00d4ff; }
.hm-icon.ph { background: rgba(0, 255, 136, 0.15); color: #00ff88; }
.hm-icon.salinity { background: rgba(255, 204, 0, 0.15); color: #ffcc00; }
.hm-icon.status { background: rgba(80, 150, 220, 0.15); color: #5b9bd5; }

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

.hm-unit {
  font-size: 11px;
  color: rgba(255, 255, 255, 0.4);
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

.chart-container-large {
  height: 300px;
  width: 100%;
}

.chart-container-radar {
  height: 420px;
  width: 100%;
}

.analysis-grid {
  display: grid;
  grid-template-columns: 1.8fr 1fr;
  gap: 12px;
  margin-bottom: 32px;
  align-items: start;
}

.analysis-left,
.analysis-right {
  min-width: 0;
}

.indicator-cards {
  display: flex;
  flex-direction: column;
  gap: 14px;
}

.indicator-card {
  position: relative;
  display: flex;
  flex-direction: column;
  gap: 10px;
  padding: 18px 20px;
  background: rgba(255, 255, 255, 0.03);
  border: 1px solid rgba(255, 255, 255, 0.06);
  border-left: 3px solid var(--accent);
  border-radius: 12px;
  transition: all 0.3s;
}

.indicator-card:hover {
  background: rgba(255, 255, 255, 0.05);
  border-color: var(--accent);
  transform: translateX(4px);
}

.ic-color-dot {
  width: 10px;
  height: 10px;
  border-radius: 50%;
  background: var(--accent);
  position: absolute;
  top: 18px;
  right: 20px;
  box-shadow: 0 0 10px var(--accent);
}

.ic-body {
  display: flex;
  justify-content: space-between;
  align-items: baseline;
  padding-right: 24px;
}

.ic-label {
  font-size: 13px;
  color: rgba(255, 255, 255, 0.5);
  font-weight: 500;
}

.ic-value {
  font-size: 22px;
  font-weight: 700;
  color: var(--accent);
}

.ic-value small {
  font-size: 12px;
  font-weight: 400;
  color: rgba(255, 255, 255, 0.35);
  margin-left: 2px;
}

.ic-bar-bg {
  width: 100%;
  height: 5px;
  background: rgba(255, 255, 255, 0.06);
  border-radius: 3px;
  overflow: hidden;
}

.ic-bar-fill {
  height: 100%;
  background: var(--accent);
  border-radius: 3px;
  transition: width 0.8s cubic-bezier(0.25, 0.46, 0.45, 0.94);
}

.double-charts {
  display: flex;
  gap: 20px;
}

.chart-container-half {
  flex: 1;
  height: 250px;
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

.stats-cards {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 14px;
}

.stat-card-box {
  padding: 20px;
  background: rgba(255, 255, 255, 0.025);
  border: 1px solid rgba(255, 255, 255, 0.05);
  border-radius: 16px;
  text-align: center;
  transition: all 0.3s;
}

.stat-card-box:hover {
  transform: translateY(-2px);
  background: rgba(80, 150, 220, 0.06);
}

.stat-label-box {
  font-size: 12px;
  color: rgba(255, 255, 255, 0.5);
  margin-bottom: 8px;
}

.stat-value-box {
  font-size: 32px;
  font-weight: 700;
  color: #e8f2fc;
  margin-bottom: 8px;
}

.stat-trend {
  font-size: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 4px;
}

.history-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.history-day-card {
  background: rgba(255, 255, 255, 0.025);
  border: 1px solid rgba(255, 255, 255, 0.05);
  border-radius: 16px;
  overflow: hidden;
}

.day-header {
  padding: 14px 20px;
  background: rgba(33, 120, 220, 0.06);
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.day-left {
  display: flex;
  align-items: center;
  gap: 12px;
}

.day-date {
  font-size: 15px;
  font-weight: 600;
  color: #5b9bd5;
}

.day-right {
  display: flex;
  align-items: center;
  gap: 16px;
}

.day-count {
  font-size: 13px;
  color: rgba(255, 255, 255, 0.6);
}

.day-avg-temp {
  font-size: 13px;
  color: #ff6b35;
}

.day-range {
  font-size: 13px;
  color: rgba(255, 255, 255, 0.5);
}

.day-records {
  padding: 10px;
}

.record-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 16px;
  background: rgba(255, 255, 255, 0.02);
  border-radius: 12px;
  margin-bottom: 8px;
}

.record-item:last-child {
  margin-bottom: 0;
}

.record-time {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 13px;
  color: rgba(255, 255, 255, 0.6);
  min-width: 80px;
}

.record-values {
  display: flex;
  gap: 24px;
}

.val-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 2px;
}

.val-label {
  font-size: 11px;
  color: rgba(255, 255, 255, 0.4);
}

.val-num {
  font-size: 14px;
  font-weight: 600;
}

.val-item.temp .val-num { color: #ff6b35; }
.val-item.do .val-num { color: #00d4ff; }
.val-item.ph .val-num { color: #00ff88; }
.val-item.salinity .val-num { color: #ffcc00; }

.record-status {
  display: flex;
  align-items: center;
  gap: 8px;
}

.warning-text {
  font-size: 12px;
  color: #ff4444;
}

.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 60px 20px;
  gap: 14px;
  color: rgba(255, 255, 255, 0.35);
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

.full-width {
  width: 100%;
}

@media (max-width: 900px) {
  .water-quality-app { padding: 10px; }
  .hero-grid {
    grid-template-columns: 1fr;
    gap: 20px;
  }
  .hero-center { order: -1; }
  .water-main-icon { font-size: 60px !important; }
  .temp-num { font-size: 64px; }
  .hero-metrics { grid-template-columns: repeat(4, 1fr); }
  .metrics-grid { grid-template-columns: repeat(2, 1fr); }
  .stats-cards { grid-template-columns: repeat(2, 1fr); }
  .double-charts { flex-direction: column; }
  .analysis-grid { grid-template-columns: 1fr; }
  .chart-container-radar { height: 320px; }
  .top-btn-text { display: none; }
  .source-strip { display: none; }
  .top-bar { padding: 10px 16px; }
  .hero-section { padding: 20px 16px; }
  .main-tabs { padding: 0 12px; }
  .main-tabs :deep(.el-tabs__item) { padding: 0 16px; font-size: 13px; }
  .record-values { gap: 12px; }
  .day-right { flex-direction: column; align-items: flex-end; gap: 4px; }
}

@media (max-width: 480px) {
  .hero-metrics { grid-template-columns: repeat(2, 1fr); }
  .metrics-grid { grid-template-columns: 1fr; }
  .stats-cards { grid-template-columns: 1fr; }
  .record-item { flex-direction: column; gap: 12px; align-items: flex-start; }
  .record-values { width: 100%; justify-content: space-between; gap: 8px; }
}
</style>
