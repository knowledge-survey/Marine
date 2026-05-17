<template>
  <div class="public-trace-root">
    <div v-if="loading" class="status-box">
      <el-icon class="is-loading" :size="40"><Loading /></el-icon>
      <div>加载中...</div>
    </div>

    <div v-else-if="error" class="status-box error-box">
      <el-icon :size="60"><Warning /></el-icon>
      <h2>未找到溯源信息</h2>
      <p>溯源码: {{ route.params.code }}</p>
      <p>请确认溯源码是否正确</p>
    </div>

    <div v-else-if="traceResult" class="trace-content">
      <div class="trace-header">
        <h1>水产养殖溯源系统</h1>
        <div class="trace-code-box">
          <span class="code-label">溯源码:</span>
          <span class="code-val">{{ traceResult.traceabilityCode?.code }}</span>
        </div>
      </div>

      <div class="trace-body">
        <el-timeline class="main-timeline">
          <el-timeline-item :timestamp="traceResult.seedlings?.[0]?.releaseDate || '--'" placement="top" type="success" hollow size="large">
            <el-card class="section-card" shadow="never">
              <template #header>
                <div class="section-hd"><el-icon><Document /></el-icon><span>苗种投放</span></div>
              </template>
              <div v-if="traceResult.seedlings && traceResult.seedlings.length" class="record-list">
                <div v-for="item in traceResult.seedlings" :key="item.id" class="record-item">
                  <div class="rec-info"><span class="rec-label">品种:</span><span class="rec-value">{{ item.species }}</span></div>
                  <div class="rec-info"><span class="rec-label">批次号:</span><span class="rec-value">{{ item.batchNo || '--' }}</span></div>
                  <div class="rec-info"><span class="rec-label">数量:</span><span class="rec-value">{{ item.quantity }}</span></div>
                  <div class="rec-info"><span class="rec-label">来源:</span><span class="rec-value">{{ item.source || '--' }}</span></div>
                  <div v-if="item.remarks" class="rec-info"><span class="rec-label">备注:</span><span class="rec-value">{{ item.remarks }}</span></div>
                </div>
              </div>
              <div v-else class="no-data">暂无苗种投放记录</div>
            </el-card>
          </el-timeline-item>

          <el-timeline-item :timestamp="traceResult.zone ? '养殖区域' : '--'" placement="top" type="primary" hollow size="large">
            <el-card class="section-card" shadow="never">
              <template #header>
                <div class="section-hd"><el-icon><Location /></el-icon><span>养殖区域</span></div>
              </template>
              <div v-if="traceResult.zone" class="zone-info">
                <div class="rec-info"><span class="rec-label">区域名称:</span><span class="rec-value">{{ traceResult.zone.name }}</span></div>
                <div class="rec-info"><span class="rec-label">位置:</span><span class="rec-value">{{ traceResult.zone.location || '--' }}</span></div>
                <div class="rec-info"><span class="rec-label">面积:</span><span class="rec-value">{{ traceResult.zone.area }} m²</span></div>
                <div class="rec-info"><span class="rec-label">养殖品种:</span><span class="rec-value">{{ traceResult.zone.species || '--' }}</span></div>
              </div>
              <div v-else class="no-data">暂无区域信息</div>
            </el-card>
          </el-timeline-item>

          <el-timeline-item :timestamp="traceResult.feedings?.length ? '饲料投喂记录' : '--'" placement="top" type="warning" hollow size="large">
            <el-card class="section-card" shadow="never">
              <template #header>
                <div class="section-hd"><el-icon><Food /></el-icon><span>饲料投喂</span></div>
              </template>
              <div v-if="traceResult.feedings && traceResult.feedings.length" class="record-list">
                <div v-for="item in traceResult.feedings" :key="item.id" class="record-item">
                  <div class="rec-info"><span class="rec-label">投喂时间:</span><span class="rec-value">{{ item.feedingTime }}</span></div>
                  <div class="rec-info"><span class="rec-label">饲料类型:</span><span class="rec-value">{{ item.feedType }}</span></div>
                  <div class="rec-info"><span class="rec-label">投喂量:</span><span class="rec-value">{{ item.quantity }}</span></div>
                  <div v-if="item.remarks" class="rec-info"><span class="rec-label">备注:</span><span class="rec-value">{{ item.remarks }}</span></div>
                </div>
              </div>
              <div v-else class="no-data">暂无饲料投喂记录</div>
            </el-card>
          </el-timeline-item>

          <el-timeline-item :timestamp="traceResult.diseases?.length ? '疾病防治记录' : '--'" placement="top" type="danger" hollow size="large">
            <el-card class="section-card" shadow="never">
              <template #header>
                <div class="section-hd"><el-icon><FirstAidKit /></el-icon><span>疾病防治</span></div>
              </template>
              <div v-if="traceResult.diseases && traceResult.diseases.length" class="record-list">
                <div v-for="item in traceResult.diseases" :key="item.id" class="record-item">
                  <div class="rec-info"><span class="rec-label">发生日期:</span><span class="rec-value">{{ item.occurrenceDate }}</span></div>
                  <div class="rec-info"><span class="rec-label">疾病名称:</span><span class="rec-value">{{ item.diseaseName }}</span></div>
                  <div class="rec-info"><span class="rec-label">症状:</span><span class="rec-value">{{ item.symptoms || '--' }}</span></div>
                  <div class="rec-info"><span class="rec-label">治疗方式:</span><span class="rec-value">{{ item.treatment || '--' }}</span></div>
                  <div class="rec-info"><span class="rec-label">状态:</span>
                    <el-tag :type="item.status === 'ACTIVE' ? 'danger' : 'success'" size="small">{{ item.status === 'ACTIVE' ? '治疗中' : '已康复' }}</el-tag>
                  </div>
                </div>
              </div>
              <div v-else class="no-data">暂无疾病防治记录</div>
            </el-card>
          </el-timeline-item>

          <el-timeline-item :timestamp="traceResult.waterQualities?.length ? '水质监测' : '--'" placement="top" type="info" hollow size="large">
            <el-card class="section-card" shadow="never">
              <template #header>
                <div class="section-hd"><el-icon><Grid /></el-icon><span>水质监测 (最近7条)</span></div>
              </template>
              <div v-if="traceResult.waterQualities && traceResult.waterQualities.length" class="record-list">
                <el-table :data="traceResult.waterQualities.slice(0, 7)" size="small" class="water-table">
                  <el-table-column prop="recordTime" label="时间" width="170" />
                  <el-table-column prop="temperature" label="水温(°C)" />
                  <el-table-column prop="dissolvedOxygen" label="溶解氧(mg/L)" />
                  <el-table-column prop="ph" label="pH" />
                  <el-table-column prop="salinity" label="盐度" />
                </el-table>
              </div>
              <div v-else class="no-data">暂无水质数据</div>
            </el-card>
          </el-timeline-item>

          <el-timeline-item :timestamp="traceResult.harvest ? traceResult.harvest.harvestDate : '--'" placement="top" type="success" hollow size="large">
            <el-card class="section-card" shadow="never">
              <template #header>
                <div class="section-hd"><el-icon><Coin /></el-icon><span>捕捞信息</span></div>
              </template>
              <div v-if="traceResult.harvest" class="record-list">
                <div class="record-item">
                  <div class="rec-info"><span class="rec-label">品种:</span><span class="rec-value">{{ traceResult.harvest.species }}</span></div>
                  <div class="rec-info"><span class="rec-label">数量:</span><span class="rec-value">{{ traceResult.harvest.quantity }}</span></div>
                  <div class="rec-info"><span class="rec-label">平均重量:</span><span class="rec-value">{{ traceResult.harvest.avgWeight }}</span></div>
                  <div v-if="traceResult.harvest.remarks" class="rec-info"><span class="rec-label">备注:</span><span class="rec-value">{{ traceResult.harvest.remarks }}</span></div>
                </div>
              </div>
              <div v-else class="no-data">暂无捕捞记录</div>
            </el-card>
          </el-timeline-item>
        </el-timeline>
      </div>

      <div class="trace-footer">
        <p>数据来源: 水产养殖管理系统</p>
        <p>生成时间: {{ traceResult.traceabilityCode?.createTime }}</p>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { getPublicTraceabilityInfo } from '@/api/record'
import { Loading, Warning, Location, Food, FirstAidKit, Coin, Grid, Document } from '@element-plus/icons-vue'

export default {
  name: 'TraceabilityPublic',
  components: { Loading, Warning, Location, Food, FirstAidKit, Coin, Grid, Document },
  setup() {
    const route = useRoute()
    const traceResult = ref(null)
    const loading = ref(true)
    const error = ref(false)

    const loadTraceData = async () => {
      const code = route.params.code
      if (!code) { error.value = true; loading.value = false; return }
      try {
        const res = await getPublicTraceabilityInfo(code)
        if (res.data) { traceResult.value = res.data } else { error.value = true }
      } catch (err) { error.value = true } finally { loading.value = false }
    }

    onMounted(() => { loadTraceData() })
    return { route, traceResult, loading, error }
  }
}
</script>

<style scoped>
.public-trace-root {
  min-height: 100vh;
  background: linear-gradient(170deg, #060d1a 0%, #0d1b2a 25%, #132742 50%, #0f1f35 75%, #080f1e 100%);
}

.status-box {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  min-height: 100vh;
  color: rgba(255,255,255,0.7);
  gap: 16px;
}
.error-box { color: #ff8a65; }
.error-box h2 { margin: 0; }
.error-box p { margin: 4px 0; color: rgba(255,255,255,0.5); }

.trace-content { max-width: 800px; margin: 0 auto; padding: 20px 16px; }
.trace-header { text-align: center; padding: 24px 0; border-bottom: 1px solid rgba(80,150,220,0.1); margin-bottom: 24px; }
.trace-header h1 { color: #5b9bd5; margin: 0 0 16px; font-size: 24px; }
.trace-code-box { display: inline-flex; align-items: center; gap: 8px; background: rgba(80,150,220,0.08); padding: 8px 16px; border-radius: 20px; border: 1px solid rgba(80,150,220,0.15); }
.code-label { color: rgba(255,255,255,0.5); font-size: 14px; }
.code-val { color: #5b9bd5; font-weight: 600; font-size: 14px; word-break: break-all; }
.trace-body { padding-bottom: 40px; }
.main-timeline { padding-left: 8px; }

.section-card {
  background: rgba(12,22,40,0.6) !important;
  border: 1px solid rgba(80,150,220,0.08) !important;
  border-radius: 12px !important;
}
.section-card :deep(.el-card__header) { border-bottom: 1px solid rgba(80,150,220,0.06); padding: 12px 16px; }
.section-card :deep(.el-card__body) { padding: 16px; }
.section-hd { display: flex; align-items: center; gap: 8px; color: #8cb8e0; font-weight: 600; font-size: 15px; }

.record-list { display: flex; flex-direction: column; gap: 10px; }
.record-item { padding: 12px; background: rgba(0,0,0,0.15); border-radius: 8px; border: 1px solid rgba(80,150,220,0.05); }
.rec-info { display: flex; align-items: center; gap: 8px; margin-bottom: 5px; flex-wrap: wrap; }
.rec-info:last-child { margin-bottom: 0; }
.rec-label { color: rgba(255,255,255,0.4); font-size: 13px; min-width: 70px; }
.rec-value { color: rgba(255,255,255,0.8); font-size: 14px; }
.no-data { text-align: center; color: rgba(255,255,255,0.2); padding: 16px 0; font-size: 14px; }

.water-table :deep(.el-table) { background: #0c1628 !important; }
.water-table :deep(.el-table::before) { display: none; }
.water-table :deep(.el-table__header th) { background: #0f1f35 !important; color: #8cb8e0 !important; font-weight: 600; font-size: 12px; border-color: rgba(80,150,220,0.1) !important; }
.water-table :deep(.el-table__cell) { background: #0c1628 !important; color: #d6e8f8 !important; border-color: rgba(80,150,220,0.06) !important; font-size: 12px; }
.water-table :deep(.el-table__cell .cell) { color: #d6e8f8 !important; }
.water-table :deep(.el-table__row:hover > td) { background: #132742 !important; }

.trace-footer { text-align: center; padding: 24px 0; border-top: 1px solid rgba(80,150,220,0.08); color: rgba(255,255,255,0.3); font-size: 12px; }
.trace-footer p { margin: 4px 0; }

:deep(.el-timeline-item__node) { border-width: 2px; }
:deep(.el-timeline-item__timestamp) { color: rgba(255,255,255,0.5); font-size: 13px; }
</style>