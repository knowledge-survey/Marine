<template>
  <div class="trace-container">
    <el-card class="main-card">
      <template #header>
        <div class="card-header">
          <span class="title">溯源管理</span>
        </div>
      </template>

      <el-tabs v-model="activeTab" class="trace-tabs">
        <el-tab-pane label="生成溯源码" name="generate">
          <div class="generate-section">
            <el-form label-width="100px" class="generate-form">
              <el-form-item label="选择批次">
                <el-select
                  v-model="selectedBatch"
                  placeholder="请选择苗种批次"
                  class="full-width"
                  @change="handleBatchChange"
                  :loading="batchLoading"
                >
                  <el-option
                    v-for="batch in batchList"
                    :key="batch.batchNo"
                    :label="`${batch.species} - ${batch.batchNo} (投放: ${batch.releaseDate})`"
                    :value="batch.batchNo"
                  />
                </el-select>
              </el-form-item>

              <el-form-item v-if="batchDetail" label="批次详情">
                <el-descriptions :column="2" border size="small" class="batch-desc">
                  <el-descriptions-item label="品种">{{ batchDetail.species }}</el-descriptions-item>
                  <el-descriptions-item label="数量">{{ batchDetail.quantity }}</el-descriptions-item>
                  <el-descriptions-item label="来源">{{ batchDetail.source }}</el-descriptions-item>
                  <el-descriptions-item label="投放日期">{{ batchDetail.releaseDate }}</el-descriptions-item>
                </el-descriptions>
              </el-form-item>

              <el-form-item>
                <el-button
                  type="primary"
                  @click="handleGenerate"
                  class="generate-btn"
                  :disabled="!selectedBatch || generating"
                  :loading="generating"
                >
                  {{ generating ? '生成中...' : '生成溯源码' }}
                </el-button>
              </el-form-item>
            </el-form>

            <div v-if="generatedCode" class="result-section">
              <el-divider>生成结果</el-divider>
              <el-row :gutter="20">
                <el-col :span="8">
                  <div class="qr-code-box">
                    <img :src="qrCodeUrl" alt="溯源二维码" class="qr-code-img" />
                    <div class="qr-code-label">溯源二维码</div>
                  </div>
                </el-col>
                <el-col :span="16">
                  <el-descriptions :column="1" border class="trace-result-desc">
                    <el-descriptions-item label="溯源码">{{ generatedCode.code }}</el-descriptions-item>
                    <el-descriptions-item label="描述">{{ generatedCode.description }}</el-descriptions-item>
                    <el-descriptions-item label="批次号">{{ generatedCode.batchNo }}</el-descriptions-item>
                    <el-descriptions-item label="创建时间">{{ generatedCode.createTime }}</el-descriptions-item>
                  </el-descriptions>
                  <div class="trace-link">
                    溯源链接:
                    <el-link :href="traceLink" target="_blank" type="primary">
                      {{ traceLink }}
                    </el-link>
                  </div>
                </el-col>
              </el-row>
            </div>
          </div>
        </el-tab-pane>

        <el-tab-pane label="溯源查询" name="query">
          <div class="query-bar">
            <el-input
              v-model="queryCode"
              placeholder="请输入溯源码"
              class="query-input"
              @keyup.enter="handleQuery"
            />
            <el-button type="primary" @click="handleQuery" class="query-btn">
              查询
            </el-button>
          </div>

          <div v-if="traceResult" class="trace-result">
            <el-card class="trace-info-card">
              <template #header>
                <span class="trace-title">溯源信息</span>
              </template>
              <el-descriptions :column="2" border class="trace-desc">
                <el-descriptions-item label="溯源码">{{ traceResult.traceabilityCode?.code }}</el-descriptions-item>
                <el-descriptions-item label="描述">{{ traceResult.traceabilityCode?.description }}</el-descriptions-item>
                <el-descriptions-item label="批次号">{{ traceResult.traceabilityCode?.batchNo || '--' }}</el-descriptions-item>
                <el-descriptions-item label="创建时间">{{ traceResult.traceabilityCode?.createTime }}</el-descriptions-item>
              </el-descriptions>

              <el-divider>养殖区域</el-divider>
              <el-descriptions v-if="traceResult.zone" :column="2" border class="trace-desc">
                <el-descriptions-item label="区域名称">{{ traceResult.zone.name }}</el-descriptions-item>
                <el-descriptions-item label="位置">{{ traceResult.zone.location }}</el-descriptions-item>
                <el-descriptions-item label="面积">{{ traceResult.zone.area }} m²</el-descriptions-item>
                <el-descriptions-item label="养殖品种">{{ traceResult.zone.species }}</el-descriptions-item>
              </el-descriptions>
              <div v-else class="empty-info">暂无区域信息</div>

              <el-divider>苗种投放记录</el-divider>
              <el-timeline v-if="traceResult.seedlings && traceResult.seedlings.length" class="trace-timeline">
                <el-timeline-item
                  v-for="item in traceResult.seedlings"
                  :key="item.id"
                  :timestamp="item.releaseDate"
                  placement="top"
                  type="success"
                >
                  <el-card shadow="hover" class="timeline-card">
                    <div><strong>品种:</strong> {{ item.species }}</div>
                    <div><strong>批次号:</strong> {{ item.batchNo }}</div>
                    <div><strong>数量:</strong> {{ item.quantity }}</div>
                    <div><strong>来源:</strong> {{ item.source }}</div>
                    <div v-if="item.remarks"><strong>备注:</strong> {{ item.remarks }}</div>
                  </el-card>
                </el-timeline-item>
              </el-timeline>
              <div v-else class="empty-info">暂无苗种投放记录</div>

              <el-divider>饲料投喂记录</el-divider>
              <el-timeline v-if="traceResult.feedings && traceResult.feedings.length" class="trace-timeline">
                <el-timeline-item
                  v-for="item in traceResult.feedings"
                  :key="item.id"
                  :timestamp="item.feedingTime"
                  placement="top"
                  type="primary"
                >
                  <el-card shadow="hover" class="timeline-card">
                    <div><strong>饲料类型:</strong> {{ item.feedType }}</div>
                    <div><strong>投喂量:</strong> {{ item.quantity }}</div>
                    <div v-if="item.remarks"><strong>备注:</strong> {{ item.remarks }}</div>
                  </el-card>
                </el-timeline-item>
              </el-timeline>
              <div v-else class="empty-info">暂无饲料投喂记录</div>

              <el-divider>疾病防治记录</el-divider>
              <el-timeline v-if="traceResult.diseases && traceResult.diseases.length" class="trace-timeline">
                <el-timeline-item
                  v-for="item in traceResult.diseases"
                  :key="item.id"
                  :timestamp="item.occurrenceDate"
                  placement="top"
                  type="danger"
                >
                  <el-card shadow="hover" class="timeline-card">
                    <div><strong>疾病名称:</strong> {{ item.diseaseName }}</div>
                    <div><strong>症状:</strong> {{ item.symptoms }}</div>
                    <div><strong>治疗方式:</strong> {{ item.treatment }}</div>
                    <div><strong>状态:</strong> <el-tag :type="item.status === 'ACTIVE' ? 'danger' : 'success'">{{ item.status === 'ACTIVE' ? '治疗中' : '已康复' }}</el-tag></div>
                  </el-card>
                </el-timeline-item>
              </el-timeline>
              <div v-else class="empty-info">暂无疾病防治记录</div>

              <el-divider>水质数据 (最近7条)</el-divider>
              <el-table v-if="traceResult.waterQualities && traceResult.waterQualities.length" :data="traceResult.waterQualities.slice(0, 7)" border size="small" class="data-table">
                <el-table-column prop="recordTime" label="时间" width="180" />
                <el-table-column prop="temperature" label="水温">
                  <template #default="{ row }">
                    <span class="temp-value">{{ row.temperature }}°C</span>
                  </template>
                </el-table-column>
                <el-table-column prop="dissolvedOxygen" label="溶解氧">
                  <template #default="{ row }">
                    <span class="do-value">{{ row.dissolvedOxygen }} mg/L</span>
                  </template>
                </el-table-column>
                <el-table-column prop="ph" label="pH" />
                <el-table-column prop="salinity" label="盐度" />
              </el-table>
              <div v-else class="empty-info">暂无水质数据</div>

              <el-divider>捕捞记录</el-divider>
              <el-timeline v-if="traceResult.harvest" class="trace-timeline">
                <el-timeline-item
                  :timestamp="traceResult.harvest.harvestDate"
                  placement="top"
                  type="warning"
                >
                  <el-card shadow="hover" class="timeline-card">
                    <div><strong>品种:</strong> {{ traceResult.harvest.species }}</div>
                    <div><strong>数量:</strong> {{ traceResult.harvest.quantity }}</div>
                    <div><strong>平均重量:</strong> {{ traceResult.harvest.avgWeight }}</div>
                    <div v-if="traceResult.harvest.remarks"><strong>备注:</strong> {{ traceResult.harvest.remarks }}</div>
                  </el-card>
                </el-timeline-item>
              </el-timeline>
              <div v-else class="empty-info">暂无捕捞记录</div>
            </el-card>
          </div>
        </el-tab-pane>

        <el-tab-pane label="溯源码列表" name="list">
          <el-table :data="traceCodeList" border class="data-table" v-loading="listLoading">
            <el-table-column prop="code" label="溯源码" width="280" />
            <el-table-column prop="batchNo" label="批次号" width="160" />
            <el-table-column prop="description" label="描述" />
            <el-table-column prop="createTime" label="创建时间" width="180" />
            <el-table-column prop="scanCount" label="扫描次数" width="100" />
            <el-table-column label="操作" width="200">
              <template #default="{ row }">
                <el-button size="small" @click="viewTraceCode(row)">查看</el-button>
                <el-button size="small" type="primary" @click="showQrCode(row)">二维码</el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-tab-pane>
      </el-tabs>
    </el-card>

    <el-dialog v-model="qrDialogVisible" title="溯源二维码" width="400px" class="qr-dialog">
      <div class="qr-dialog-content">
        <img :src="dialogQrUrl" alt="溯源二维码" class="dialog-qr-img" />
        <div class="dialog-code">{{ dialogCode }}</div>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { ref, onMounted } from 'vue'
import {
  getAllBatches,
  getSeedlingsByBatch,
  generateTraceabilityForBatch,
  getQrCodeUrl,
  getTraceabilityInfo,
  getTraceabilityCodes
} from '@/api/record'
import { ElMessage } from 'element-plus'

export default {
  name: 'TraceManage',
  props: ['zoneId'],
  setup() {
    const activeTab = ref('generate')
    const batchList = ref([])
    const batchLoading = ref(false)
    const selectedBatch = ref('')
    const batchDetail = ref(null)
    const generating = ref(false)
    const generatedCode = ref(null)
    const qrCodeUrl = ref('')
    const traceLink = ref('')

    const queryCode = ref('')
    const traceResult = ref(null)

    const traceCodeList = ref([])
    const listLoading = ref(false)

    const qrDialogVisible = ref(false)
    const dialogQrUrl = ref('')
    const dialogCode = ref('')

    const loadBatches = async () => {
      batchLoading.value = true
      try {
        const res = await getAllBatches()
        batchList.value = res.data || []
      } catch (error) {
        console.error('加载批次列表失败:', error)
      } finally {
        batchLoading.value = false
      }
    }

    const handleBatchChange = async (batchNo) => {
      try {
        const res = await getSeedlingsByBatch(batchNo)
        if (res.data && res.data.length > 0) {
          batchDetail.value = res.data[0]
        } else {
          batchDetail.value = null
        }
      } catch (error) {
        batchDetail.value = null
      }
    }

    const handleGenerate = async () => {
      if (!selectedBatch.value) {
        ElMessage.warning('请选择苗种批次')
        return
      }
      generating.value = true
      try {
        const res = await generateTraceabilityForBatch(selectedBatch.value)
        generatedCode.value = res.data
        qrCodeUrl.value = getQrCodeUrl(res.data.code)
        traceLink.value = `http://localhost:5173/traceability/${res.data.code}`
        ElMessage.success('溯源码生成成功')
      } catch (error) {
        console.error('生成失败:', error)
        ElMessage.error(error.response?.data?.message || '生成失败: ' + (error.message || '未知错误'))
      } finally {
        generating.value = false
      }
    }

    const handleQuery = async () => {
      if (!queryCode.value) {
        ElMessage.warning('请输入溯源码')
        return
      }
      try {
        const res = await getTraceabilityInfo(queryCode.value)
        traceResult.value = res.data
        if (!traceResult.value) {
          ElMessage.warning('未找到溯源信息')
        }
      } catch (error) {
        traceResult.value = null
        ElMessage.error('未找到溯源信息')
      }
    }

    const viewTraceCode = (row) => {
      queryCode.value = row.code
      activeTab.value = 'query'
      handleQuery()
    }

    const showQrCode = (row) => {
      dialogCode.value = row.code
      dialogQrUrl.value = getQrCodeUrl(row.code)
      qrDialogVisible.value = true
    }

    const loadTraceCodes = async () => {
      listLoading.value = true
      try {
        const res = await getTraceabilityCodes()
        traceCodeList.value = res.data || []
      } catch (error) {
        console.error('加载溯源码列表失败:', error)
      } finally {
        listLoading.value = false
      }
    }

    onMounted(() => {
      loadBatches()
      loadTraceCodes()
    })

    return {
      activeTab,
      batchList,
      batchLoading,
      selectedBatch,
      batchDetail,
      generating,
      generatedCode,
      qrCodeUrl,
      traceLink,
      queryCode,
      traceResult,
      traceCodeList,
      listLoading,
      qrDialogVisible,
      dialogQrUrl,
      dialogCode,
      handleBatchChange,
      handleGenerate,
      handleQuery,
      viewTraceCode,
      showQrCode
    }
  }
}
</script>

<style scoped>
.trace-container {
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

.trace-tabs :deep(.el-tabs__item) {
  color: rgba(255, 255, 255, 0.7);
}

.trace-tabs :deep(.el-tabs__item.is-active) {
  color: #00d4ff;
}

.trace-tabs :deep(.el-tabs__active-bar) {
  background-color: #00d4ff;
}

.generate-form :deep(.el-form-item__label) {
  color: #fff !important;
}

.generate-btn {
  background: linear-gradient(135deg, #00d4ff 0%, #0077ff 100%);
  border: none;
  box-shadow: 0 4px 15px rgba(0, 212, 255, 0.4);
}

.full-width {
  width: 100%;
}

.result-section {
  margin-top: 24px;
  padding-top: 16px;
}

.qr-code-box {
  text-align: center;
  padding: 16px;
  background: rgba(255, 255, 255, 0.05);
  border-radius: 8px;
  border: 1px solid rgba(0, 212, 255, 0.2);
}

.qr-code-img {
  width: 200px;
  height: 200px;
  background: #fff;
  border-radius: 8px;
  padding: 8px;
}

.qr-code-label {
  color: #00d4ff;
  margin-top: 12px;
  font-size: 14px;
}

.trace-result-desc :deep(.el-descriptions__label) {
  color: #00d4ff !important;
  background: rgba(0, 212, 255, 0.1) !important;
}

.trace-result-desc :deep(.el-descriptions__body) {
  color: #fff !important;
}

.trace-link {
  margin-top: 16px;
  color: rgba(255, 255, 255, 0.7);
  font-size: 13px;
}

.query-bar {
  display: flex;
  gap: 12px;
  margin-bottom: 20px;
}

.query-input {
  flex: 1;
  max-width: 400px;
}

.query-btn {
  background: linear-gradient(135deg, #00d4ff 0%, #0077ff 100%);
  border: none;
  box-shadow: 0 4px 15px rgba(0, 212, 255, 0.4);
}

.trace-info-card {
  background: rgba(0, 212, 255, 0.05) !important;
  border: 1px solid rgba(0, 212, 255, 0.2) !important;
}

.trace-title {
  color: #00d4ff;
  font-weight: 600;
}

.trace-desc :deep(.el-descriptions__label) {
  background-color: rgba(0, 212, 255, 0.1) !important;
  color: #00d4ff !important;
  font-weight: 600;
}

.trace-desc :deep(.el-descriptions__body) {
  color: #fff !important;
}

.batch-desc :deep(.el-descriptions__label) {
  color: #00d4ff !important;
}

.batch-desc :deep(.el-descriptions__body) {
  color: #fff !important;
}

.empty-info {
  text-align: center;
  color: rgba(255, 255, 255, 0.4);
  padding: 20px 0;
}

.trace-timeline {
  padding: 10px 0;
}

.timeline-card {
  background: rgba(0, 0, 0, 0.2) !important;
  border: 1px solid rgba(0, 212, 255, 0.1) !important;
  color: #fff !important;
}

.timeline-card :deep(.el-card__body) {
  padding: 12px;
}

.timeline-card div {
  margin-bottom: 4px;
}

.timeline-card div:last-child {
  margin-bottom: 0;
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

.temp-value { color: #ff6b35; }
.do-value { color: #00d4ff; }

.qr-dialog :deep(.el-dialog) {
  background: rgba(20, 40, 60, 0.95) !important;
  border: 1px solid rgba(0, 212, 255, 0.3) !important;
}

.qr-dialog-content {
  text-align: center;
}

.dialog-qr-img {
  width: 250px;
  height: 250px;
  background: #fff;
  border-radius: 8px;
  padding: 12px;
}

.dialog-code {
  margin-top: 12px;
  color: #00d4ff;
  font-size: 14px;
  word-break: break-all;
}

:deep(.el-divider__text) {
  background: rgba(20, 40, 60, 0.9) !important;
  color: #00d4ff !important;
}

:deep(.el-divider) {
  border-color: rgba(0, 212, 255, 0.2) !important;
}
</style>
