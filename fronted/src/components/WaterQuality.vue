<template>
  <div class="water-quality-container">
    <el-card class="main-card">
      <template #header>
        <div class="card-header">
          <span class="title">水质监测中心</span>
          <el-button type="primary" @click="showAddDialog" class="add-btn">添加记录</el-button>
        </div>
      </template>

      <el-row :gutter="20" class="stats-row">
        <el-col :span="6">
          <div class="stat-card temp-card">
            <div class="stat-icon">
              <el-icon :size="40"><Sunny /></el-icon>
            </div>
            <div class="stat-content">
              <div class="stat-label">水温</div>
              <div class="stat-value">{{ latest?.temperature || '--' }} <span class="unit">°C</span></div>
            </div>
          </div>
        </el-col>
        <el-col :span="6">
          <div class="stat-card do-card">
            <div class="stat-icon">
              <el-icon :size="40"><Sunny /></el-icon>
            </div>
            <div class="stat-content">
              <div class="stat-label">溶解氧</div>
              <div class="stat-value">{{ latest?.dissolvedOxygen || '--' }} <span class="unit">mg/L</span></div>
            </div>
          </div>
        </el-col>
        <el-col :span="6">
          <div class="stat-card ph-card">
            <div class="stat-icon">
              <el-icon :size="40"><Document /></el-icon>
            </div>
            <div class="stat-content">
              <div class="stat-label">pH值</div>
              <div class="stat-value">{{ latest?.ph || '--' }}</div>
            </div>
          </div>
        </el-col>
        <el-col :span="6">
          <div class="stat-card salinity-card">
            <div class="stat-icon">
              <el-icon :size="40"><TrendCharts /></el-icon>
            </div>
            <div class="stat-content">
              <div class="stat-label">盐度</div>
              <div class="stat-value">{{ latest?.salinity || '--' }}</div>
            </div>
          </div>
        </el-col>
      </el-row>

      <el-row :gutter="20" class="charts-row">
        <el-col :span="16">
          <el-card class="chart-card">
            <template #header>
              <div class="chart-title">近7日水质变化趋势</div>
            </template>
            <div ref="trendChartRef" class="chart-container"></div>
          </el-card>
        </el-col>
        <el-col :span="8">
          <el-card class="chart-card">
            <template #header>
              <div class="chart-title">水质指标占比</div>
            </template>
            <div ref="pieChartRef" class="chart-container"></div>
          </el-card>
        </el-col>
      </el-row>

      <el-row :gutter="20" class="charts-row">
        <el-col :span="12">
          <el-card class="chart-card">
            <template #header>
              <div class="chart-title">水温变化柱状图</div>
            </template>
            <div ref="tempChartRef" class="chart-container"></div>
          </el-card>
        </el-col>
        <el-col :span="12">
          <el-card class="chart-card">
            <template #header>
              <div class="chart-title">溶解氧变化柱状图</div>
            </template>
            <div ref="doChartRef" class="chart-container"></div>
          </el-card>
        </el-col>
      </el-row>

      <el-card class="table-card">
        <template #header>
          <div class="table-title">历史数据记录</div>
        </template>
        <el-collapse v-model="activeCollapse" class="history-collapse">
          <el-collapse-item v-for="(group, dateKey) in groupedHistory" :key="dateKey" :name="dateKey">
            <template #title>
              <div class="collapse-title">
                <span class="date">{{ group.date }}</span>
                <span class="count">{{ group.records.length }} 条记录</span>
                <span class="avg-temp">平均水温: {{ group.avgTemp }}°C</span>
                <span class="temp-range">{{ group.minTemp }}°C ~ {{ group.maxTemp }}°C</span>
                <el-tag :type="group.hasWarning ? 'danger' : 'success'" size="small" class="status-tag">
                  {{ group.hasWarning ? '有异常' : '正常' }}
                </el-tag>
              </div>
            </template>
            <el-table :data="group.records" border stripe style="width: 100%" class="data-table" size="small">
              <el-table-column prop="recordTime" label="时间" width="180" />
              <el-table-column prop="temperature" label="水温" width="100">
                <template #default="{ row }">
                  <span class="temp-value">{{ row.temperature }}°C</span>
                </template>
              </el-table-column>
              <el-table-column prop="dissolvedOxygen" label="溶解氧" width="100">
                <template #default="{ row }">
                  <span class="do-value">{{ row.dissolvedOxygen }} mg/L</span>
                </template>
              </el-table-column>
              <el-table-column prop="ph" label="pH" width="100">
                <template #default="{ row }">
                  <span class="ph-value">{{ row.ph }}</span>
                </template>
              </el-table-column>
              <el-table-column prop="salinity" label="盐度" width="100">
                <template #default="{ row }">
                  <span class="salinity-value">{{ row.salinity }}</span>
                </template>
              </el-table-column>
              <el-table-column prop="isWarning" label="状态" width="100">
                <template #default="{ row }">
                  <el-tag :type="row.isWarning ? 'danger' : 'success'" class="status-tag">
                    {{ row.isWarning ? '异常' : '正常' }}
                  </el-tag>
                </template>
              </el-table-column>
              <el-table-column prop="warningInfo" label="告警信息" />
            </el-table>
          </el-collapse-item>
        </el-collapse>
        <div v-if="waterQualityList.length === 0" class="empty-tip">暂无历史数据</div>
      </el-card>
    </el-card>

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
import { getWaterQualityByZone, getLatestWaterQuality, createWaterQuality } from '@/api/waterQuality'
import { Sunny, Document, TrendCharts } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'

export default {
  name: 'WaterQuality',
  components: { Sunny, Document, TrendCharts },
  props: ['zoneId'],
  setup(props) {
    const trendChartRef = ref(null)
    const pieChartRef = ref(null)
    const tempChartRef = ref(null)
    const doChartRef = ref(null)
    
    let trendChart = null
    let pieChart = null
    let tempChart = null
    let doChart = null

    const waterQualityList = ref([])
    const latest = ref(null)
    const dialogVisible = ref(false)
    const activeCollapse = ref([])
    const form = ref({
      temperature: null,
      dissolvedOxygen: null,
      ph: null,
      salinity: null
    })

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

    const loadData = async () => {
      if (!props.zoneId) return
      
      try {
        const [listRes, latestRes] = await Promise.all([
          getWaterQualityByZone(props.zoneId),
          getLatestWaterQuality(props.zoneId)
        ])
        waterQualityList.value = listRes.data.reverse()
        latest.value = latestRes.data
        nextTick(() => {
          initCharts()
          updateCharts()
        })
      } catch (error) {
        console.error(error)
      }
    }

    const initCharts = () => {
      if (trendChartRef.value) {
        trendChart = echarts.init(trendChartRef.value)
      }
      if (pieChartRef.value) {
        pieChart = echarts.init(pieChartRef.value)
      }
      if (tempChartRef.value) {
        tempChart = echarts.init(tempChartRef.value)
      }
      if (doChartRef.value) {
        doChart = echarts.init(doChartRef.value)
      }

      window.addEventListener('resize', () => {
        trendChart?.resize()
        pieChart?.resize()
        tempChart?.resize()
        doChart?.resize()
      })
    }

    const updateCharts = () => {
      updateTrendChart()
      updatePieChart()
      updateTempChart()
      updateDoChart()
    }

    const updateTrendChart = () => {
      if (!trendChart || !waterQualityList.value.length) return
      
      const data = waterQualityList.value.slice(-7)
      const xData = data.map(item => {
        const date = new Date(item.recordTime)
        return `${date.getMonth() + 1}/${date.getDate()}`
      })

      trendChart.setOption({
        backgroundColor: 'transparent',
        tooltip: {
          trigger: 'axis',
          backgroundColor: 'rgba(20, 40, 60, 0.9)',
          borderColor: '#00d4ff',
          textStyle: { color: '#fff' }
        },
        legend: {
          data: ['水温', '溶解氧', 'pH', '盐度'],
          textStyle: { color: '#fff' },
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
          data: xData,
          axisLine: { lineStyle: { color: '#00d4ff' } },
          axisLabel: { color: '#fff' }
        },
        yAxis: {
          type: 'value',
          axisLine: { lineStyle: { color: '#00d4ff' } },
          axisLabel: { color: '#fff' },
          splitLine: { lineStyle: { color: 'rgba(0, 212, 255, 0.1)' } }
        },
        series: [
          {
            name: '水温',
            type: 'line',
            smooth: true,
            data: data.map(item => item.temperature),
            lineStyle: { color: '#ff6b35' },
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
            data: data.map(item => item.dissolvedOxygen),
            lineStyle: { color: '#00d4ff' },
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
            data: data.map(item => item.ph),
            lineStyle: { color: '#00ff88' },
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
            data: data.map(item => item.salinity),
            lineStyle: { color: '#ffcc00' },
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

    const updatePieChart = () => {
      if (!pieChart || !waterQualityList.value.length) return
      
      const recent = waterQualityList.value.slice(-7)
      const avgTemp = recent.reduce((sum, item) => sum + (item.temperature || 0), 0) / recent.length
      const avgDo = recent.reduce((sum, item) => sum + (item.dissolvedOxygen || 0), 0) / recent.length
      const avgPh = recent.reduce((sum, item) => sum + (item.ph || 0), 0) / recent.length
      const avgSalinity = recent.reduce((sum, item) => sum + (item.salinity || 0), 0) / recent.length

      pieChart.setOption({
        backgroundColor: 'transparent',
        tooltip: {
          trigger: 'item',
          backgroundColor: 'rgba(20, 40, 60, 0.9)',
          borderColor: '#00d4ff',
          textStyle: { color: '#fff' }
        },
        legend: {
          orient: 'vertical',
          right: '5%',
          top: 'center',
          textStyle: { color: '#fff' }
        },
        series: [
          {
            name: '水质指标',
            type: 'pie',
            radius: ['40%', '70%'],
            center: ['35%', '50%'],
            avoidLabelOverlap: false,
            itemStyle: {
              borderRadius: 10,
              borderColor: '#0c1929',
              borderWidth: 2
            },
            label: {
              show: false
            },
            emphasis: {
              label: {
                show: true,
                fontSize: '16',
                fontWeight: 'bold',
                color: '#fff'
              }
            },
            data: [
              { value: avgTemp.toFixed(2), name: '水温', itemStyle: { color: '#ff6b35' } },
              { value: avgDo.toFixed(2), name: '溶解氧', itemStyle: { color: '#00d4ff' } },
              { value: avgPh.toFixed(2), name: 'pH值', itemStyle: { color: '#00ff88' } },
              { value: avgSalinity.toFixed(2), name: '盐度', itemStyle: { color: '#ffcc00' } }
            ]
          }
        ]
      })
    }

    const updateTempChart = () => {
      if (!tempChart || !waterQualityList.value.length) return
      
      const data = waterQualityList.value.slice(-7)
      const xData = data.map(item => {
        const date = new Date(item.recordTime)
        return `${date.getMonth() + 1}/${date.getDate()}`
      })

      tempChart.setOption({
        backgroundColor: 'transparent',
        tooltip: {
          trigger: 'axis',
          backgroundColor: 'rgba(20, 40, 60, 0.9)',
          borderColor: '#00d4ff',
          textStyle: { color: '#fff' }
        },
        grid: {
          left: '3%',
          right: '4%',
          bottom: '3%',
          containLabel: true
        },
        xAxis: {
          type: 'category',
          data: xData,
          axisLine: { lineStyle: { color: '#00d4ff' } },
          axisLabel: { color: '#fff' }
        },
        yAxis: {
          type: 'value',
          name: '°C',
          axisLine: { lineStyle: { color: '#00d4ff' } },
          axisLabel: { color: '#fff' },
          splitLine: { lineStyle: { color: 'rgba(0, 212, 255, 0.1)' } }
        },
        series: [
          {
            name: '水温',
            type: 'bar',
            data: data.map(item => item.temperature),
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
      if (!doChart || !waterQualityList.value.length) return
      
      const data = waterQualityList.value.slice(-7)
      const xData = data.map(item => {
        const date = new Date(item.recordTime)
        return `${date.getMonth() + 1}/${date.getDate()}`
      })

      doChart.setOption({
        backgroundColor: 'transparent',
        tooltip: {
          trigger: 'axis',
          backgroundColor: 'rgba(20, 40, 60, 0.9)',
          borderColor: '#00d4ff',
          textStyle: { color: '#fff' }
        },
        grid: {
          left: '3%',
          right: '4%',
          bottom: '3%',
          containLabel: true
        },
        xAxis: {
          type: 'category',
          data: xData,
          axisLine: { lineStyle: { color: '#00d4ff' } },
          axisLabel: { color: '#fff' }
        },
        yAxis: {
          type: 'value',
          name: 'mg/L',
          axisLine: { lineStyle: { color: '#00d4ff' } },
          axisLabel: { color: '#fff' },
          splitLine: { lineStyle: { color: 'rgba(0, 212, 255, 0.1)' } }
        },
        series: [
          {
            name: '溶解氧',
            type: 'bar',
            data: data.map(item => item.dissolvedOxygen),
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
          zone: { id: props.zoneId }
        })
        dialogVisible.value = false
        ElMessage.success('添加成功')
        loadData()
      } catch (error) {
        ElMessage.error('添加失败')
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
      pieChartRef,
      tempChartRef,
      doChartRef,
      waterQualityList,
      latest,
      dialogVisible,
      activeCollapse,
      groupedHistory,
      form,
      showAddDialog,
      handleAdd
    }
  }
}
</script>

<style scoped>
.water-quality-container {
  background: #0c1929;
  min-height: 100vh;
  padding: 20px;
}

.main-card {
  background: rgba(20, 40, 60, 0.9) !important;
  border: 1px solid rgba(0, 212, 255, 0.3) !important;
}

.main-card :deep(.el-card__header) {
  border-bottom: 1px solid rgba(0, 212, 255, 0.2) !important;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.title {
  color: #00d4ff;
  font-size: 20px;
  font-weight: 600;
  text-shadow: 0 0 10px rgba(0, 212, 255, 0.5);
}

.add-btn {
  background: linear-gradient(135deg, #00d4ff 0%, #0077ff 100%);
  border: none;
  box-shadow: 0 4px 15px rgba(0, 212, 255, 0.4);
}

.stats-row {
  margin-bottom: 24px;
}

.stat-card {
  display: flex;
  align-items: center;
  padding: 20px;
  border-radius: 12px;
  border: 1px solid rgba(0, 212, 255, 0.2);
  background: rgba(0, 0, 0, 0.2);
  transition: all 0.3s;
}

.stat-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 25px rgba(0, 212, 255, 0.2);
}

.temp-card { border-color: rgba(255, 107, 53, 0.5); }
.do-card { border-color: rgba(0, 212, 255, 0.5); }
.ph-card { border-color: rgba(0, 255, 136, 0.5); }
.salinity-card { border-color: rgba(255, 204, 0, 0.5); }

.temp-card .stat-icon { color: #ff6b35; }
.do-card .stat-icon { color: #00d4ff; }
.ph-card .stat-icon { color: #00ff88; }
.salinity-card .stat-icon { color: #ffcc00; }

.stat-icon {
  margin-right: 16px;
}

.stat-content {
  flex: 1;
}

.stat-label {
  color: rgba(255, 255, 255, 0.7);
  font-size: 14px;
  margin-bottom: 4px;
}

.stat-value {
  color: #fff;
  font-size: 32px;
  font-weight: 700;
}

.unit {
  font-size: 16px;
  font-weight: 400;
  color: rgba(255, 255, 255, 0.7);
}

.charts-row {
  margin-bottom: 24px;
}

.chart-card {
  background: rgba(0, 0, 0, 0.2) !important;
  border: 1px solid rgba(0, 212, 255, 0.2) !important;
}

.chart-card :deep(.el-card__header) {
  border-bottom: 1px solid rgba(0, 212, 255, 0.1) !important;
}

.chart-title {
  color: #00d4ff;
  font-size: 16px;
  font-weight: 600;
}

.chart-container {
  height: 300px;
  width: 100%;
}

.table-card {
  background: rgba(0, 0, 0, 0.2) !important;
  border: 1px solid rgba(0, 212, 255, 0.2) !important;
}

.table-card :deep(.el-card__header) {
  border-bottom: 1px solid rgba(0, 212, 255, 0.1) !important;
}

.table-title {
  color: #00d4ff;
  font-size: 16px;
  font-weight: 600;
}

.data-table :deep(.el-table) {
  background-color: transparent !important;
}

.data-table :deep(.el-table__header-wrapper th) {
  background-color: rgba(0, 212, 255, 0.1) !important;
  color: #00d4ff !important;
  font-weight: 600;
}

.data-table :deep(.el-table__cell) {
  background-color: transparent !important;
  color: #fff !important;
  border-color: rgba(0, 212, 255, 0.1) !important;
}

.data-table :deep(.el-table__row:hover) {
  background-color: rgba(0, 212, 255, 0.05) !important;
}

.temp-value { color: #ff6b35; }
.do-value { color: #00d4ff; }
.ph-value { color: #00ff88; }
.salinity-value { color: #ffcc00; }

.status-tag {
  font-weight: 600;
}

.history-collapse {
  background: transparent;
}

.history-collapse :deep(.el-collapse-item__header) {
  background: rgba(0, 212, 255, 0.05) !important;
  color: #fff !important;
  border: 1px solid rgba(0, 212, 255, 0.1) !important;
  border-radius: 8px;
  margin-bottom: 8px;
  padding: 0 16px;
}

.history-collapse :deep(.el-collapse-item__wrap) {
  background: transparent;
  border: none;
}

.history-collapse :deep(.el-collapse-item__content) {
  padding-bottom: 16px;
}

.collapse-title {
  display: flex;
  align-items: center;
  gap: 16px;
  width: 100%;
  flex: 1;
}

.collapse-title .date {
  color: #00d4ff;
  font-weight: 600;
  min-width: 100px;
}

.collapse-title .count {
  color: rgba(255, 255, 255, 0.7);
}

.collapse-title .avg-temp {
  color: #ff6b35;
}

.collapse-title .temp-range {
  color: rgba(255, 255, 255, 0.6);
  font-size: 13px;
}

.empty-tip {
  text-align: center;
  color: rgba(255, 255, 255, 0.5);
  padding: 40px 0;
}

.add-dialog :deep(.el-dialog) {
  background: rgba(20, 40, 60, 0.95) !important;
  border: 1px solid rgba(0, 212, 255, 0.3) !important;
  border-radius: 12px !important;
}

.add-dialog :deep(.el-dialog__header) {
  border-bottom: 1px solid rgba(0, 212, 255, 0.2) !important;
  background: transparent !important;
}

.add-dialog :deep(.el-dialog__title) {
  color: #00d4ff !important;
}

.add-dialog :deep(.el-dialog__close) {
  color: rgba(255, 255, 255, 0.6) !important;
}

.add-dialog :deep(.el-dialog__close:hover) {
  color: #00d4ff !important;
}

.add-dialog :deep(.el-dialog__body) {
  background: transparent !important;
}

.add-dialog :deep(.el-dialog__footer) {
  border-top: 1px solid rgba(0, 212, 255, 0.15) !important;
  background: transparent !important;
}

.add-form :deep(.el-form-item__label) {
  color: rgba(255, 255, 255, 0.9) !important;
  font-size: 14px;
  font-weight: 500;
  line-height: 40px;
}

.add-form :deep(.el-form-item) {
  margin-bottom: 20px;
}

.add-form :deep(.el-form-item__content) {
  justify-content: flex-start;
}

.add-form :deep(.el-input__wrapper) {
  background-color: rgba(10, 25, 40, 0.8) !important;
  border: 1px solid rgba(0, 212, 255, 0.2) !important;
  border-radius: 8px !important;
  box-shadow: none !important;
}

.add-form :deep(.el-input__wrapper:hover) {
  border-color: rgba(0, 212, 255, 0.4) !important;
}

.add-form :deep(.el-input__wrapper.is-focus) {
  border-color: #00d4ff !important;
  box-shadow: 0 0 15px rgba(0, 212, 255, 0.2) !important;
}

.add-form :deep(.el-input__inner) {
  color: rgba(255, 255, 255, 0.9) !important;
}

.add-form :deep(.el-input__inner::placeholder) {
  color: rgba(255, 255, 255, 0.4) !important;
}

.add-form :deep(.el-input-number__decrease),
.add-form :deep(.el-input-number__increase) {
  background-color: rgba(0, 212, 255, 0.1) !important;
  color: #00d4ff !important;
  border-color: rgba(0, 212, 255, 0.2) !important;
}

.add-form :deep(.el-input-number .el-input__wrapper) {
  background-color: rgba(10, 25, 40, 0.8) !important;
  border: 1px solid rgba(0, 212, 255, 0.2) !important;
}

.add-dialog :deep(.el-button) {
  border-radius: 8px !important;
}

.add-dialog :deep(.el-button--default) {
  background: rgba(255, 255, 255, 0.1) !important;
  border: 1px solid rgba(255, 255, 255, 0.2) !important;
  color: rgba(255, 255, 255, 0.8) !important;
}

.add-dialog :deep(.el-button--default:hover) {
  background: rgba(255, 255, 255, 0.15) !important;
  color: #fff !important;
}

.add-dialog :deep(.el-button--primary) {
  background: linear-gradient(135deg, #00d4ff 0%, #0077ff 100%) !important;
  border: none !important;
  color: #fff !important;
  box-shadow: 0 4px 15px rgba(0, 212, 255, 0.4) !important;
}

.add-dialog :deep(.el-button--primary:hover) {
  box-shadow: 0 6px 20px rgba(0, 212, 255, 0.6) !important;
}
</style>
