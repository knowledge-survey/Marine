<template>
  <div class="trace-root">
    <el-card class="main-card" shadow="never">
      <template #header>
        <div class="card-header">
          <span class="title">
            <el-icon><CircleCheck /></el-icon>溯源管理
          </span>
        </div>
      </template>

      <el-tabs v-model="activeTab" class="trace-tabs">
        <el-tab-pane label="生成溯源码" name="generate" lazy>
          <div class="section-body">
            <el-form label-width="100px" class="gen-form">
              <el-form-item label="选择批次">
                <el-select v-model="selectedBatch" placeholder="请选择苗种批次" class="full-width" @change="handleBatchChange" :loading="batchLoading" filterable>
                  <el-option v-for="batch in batchList" :key="batch.batchNo" :label="`${batch.species} - ${batch.batchNo}`" :value="batch.batchNo" />
                </el-select>
              </el-form-item>
              <el-form-item v-if="batchDetail" label="批次详情">
                <el-descriptions :column="2" size="small" class="batch-desc">
                  <el-descriptions-item label="品种">{{ batchDetail.species }}</el-descriptions-item>
                  <el-descriptions-item label="数量">{{ batchDetail.quantity }}</el-descriptions-item>
                  <el-descriptions-item label="来源">{{ batchDetail.source }}</el-descriptions-item>
                  <el-descriptions-item label="投放日期">{{ batchDetail.releaseDate }}</el-descriptions-item>
                </el-descriptions>
              </el-form-item>
              <el-form-item>
                <el-button type="primary" @click="handleGenerate" class="add-btn" :disabled="!selectedBatch || generating" :loading="generating">
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
                  <el-descriptions :column="1" size="small" class="gen-result-desc">
                    <el-descriptions-item label="溯源码">{{ generatedCode.code }}</el-descriptions-item>
                    <el-descriptions-item label="描述">{{ generatedCode.description }}</el-descriptions-item>
                    <el-descriptions-item label="批次号">{{ generatedCode.batchNo }}</el-descriptions-item>
                    <el-descriptions-item label="创建时间">{{ generatedCode.createTime }}</el-descriptions-item>
                    <el-descriptions-item label="溯源链接">
                      <el-link :href="traceLink" target="_blank" type="primary">{{ traceLink }}</el-link>
                    </el-descriptions-item>
                  </el-descriptions>
                </el-col>
              </el-row>
            </div>
          </div>
        </el-tab-pane>

        <el-tab-pane label="溯源查询" name="query" lazy>
          <div class="query-bar">
            <el-input v-model="queryCode" placeholder="请输入溯源码" class="query-input" @keyup.enter="handleQuery" />
            <el-button type="primary" @click="handleQuery" class="add-btn">查询</el-button>
          </div>
          <div v-if="traceResult" class="trace-result">
            <el-card class="info-card" shadow="never">
              <template #header><span class="section-label">溯源信息</span></template>
              <el-descriptions :column="2" size="small" class="trace-desc">
                <el-descriptions-item label="溯源码">{{ traceResult.traceabilityCode?.code }}</el-descriptions-item>
                <el-descriptions-item label="描述">{{ traceResult.traceabilityCode?.description }}</el-descriptions-item>
                <el-descriptions-item label="批次号">{{ traceResult.traceabilityCode?.batchNo || '--' }}</el-descriptions-item>
                <el-descriptions-item label="创建时间">{{ traceResult.traceabilityCode?.createTime }}</el-descriptions-item>
                <el-descriptions-item label="溯源链接" :span="2">
                  <el-link :href="getTraceLink(traceResult.traceabilityCode?.code)" target="_blank" type="primary">{{ getTraceLink(traceResult.traceabilityCode?.code) }}</el-link>
                </el-descriptions-item>
              </el-descriptions>
            </el-card>
          </div>
        </el-tab-pane>

        <el-tab-pane label="溯源码列表" name="list" lazy>
          <el-table :data="traceCodeList" class="data-table" v-loading="listLoading" empty-text="暂无数据">
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
import { getAllBatches, getSeedlingsByBatch, generateTraceabilityForBatch, getQrCodeUrl, getTraceabilityInfo, getTraceabilityCodes } from '@/api/record'
import { ElMessage } from 'element-plus'
import { CircleCheck } from '@element-plus/icons-vue'

export default {
  name: 'TraceManage',
  components: { CircleCheck },
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

    const getTraceLink = (code) => code ? `http://localhost:8080/traceability/${code}` : '--'

    const loadBatches = async () => { batchLoading.value = true; try { batchList.value = (await getAllBatches()).data || [] } catch (e) { console.error(e) } finally { batchLoading.value = false } }
    const handleBatchChange = async (batchNo) => { try { const res = await getSeedlingsByBatch(batchNo); batchDetail.value = res.data?.[0] || null } catch (e) { batchDetail.value = null } }
    const handleGenerate = async () => {
      if (!selectedBatch.value) { ElMessage.warning('请选择苗种批次'); return }
      generating.value = true; try { const res = await generateTraceabilityForBatch(selectedBatch.value); generatedCode.value = res.data; qrCodeUrl.value = getQrCodeUrl(res.data.code); traceLink.value = getTraceLink(res.data.code); ElMessage.success('溯源码生成成功') } catch (e) { ElMessage.error(e.response?.data?.message || '生成失败') } finally { generating.value = false }
    }
    const handleQuery = async () => { if (!queryCode.value) { ElMessage.warning('请输入溯源码'); return } try { traceResult.value = (await getTraceabilityInfo(queryCode.value)).data; if (!traceResult.value) ElMessage.warning('未找到溯源信息') } catch (e) { traceResult.value = null; ElMessage.error('未找到溯源信息') } }
    const viewTraceCode = (row) => { queryCode.value = row.code; activeTab.value = 'query'; handleQuery() }
    const showQrCode = (row) => { dialogCode.value = row.code; dialogQrUrl.value = getQrCodeUrl(row.code); qrDialogVisible.value = true }
    const loadTraceCodes = async () => { listLoading.value = true; try { traceCodeList.value = (await getTraceabilityCodes()).data || [] } catch (e) { ElMessage.error('加载失败') } finally { listLoading.value = false } }

    onMounted(() => { loadBatches(); loadTraceCodes() })
    return { activeTab, batchList, batchLoading, selectedBatch, batchDetail, generating, generatedCode, qrCodeUrl, traceLink, queryCode, traceResult, traceCodeList, listLoading, qrDialogVisible, dialogQrUrl, dialogCode, getTraceLink, handleBatchChange, handleGenerate, handleQuery, viewTraceCode, showQrCode }
  }
}
</script>

<style scoped>
.trace-root { padding: 20px; }
.main-card {
  background: rgba(12, 22, 40, 0.5) !important;
  border: 1px solid rgba(80, 150, 220, 0.08) !important;
  border-radius: 16px;
  box-shadow: none;
}
.main-card :deep(.el-card__header) { border-bottom: 1px solid rgba(80, 150, 220, 0.06); padding: 16px 20px; }
.card-header { display: flex; justify-content: space-between; align-items: center; }
.title { display: flex; align-items: center; gap: 8px; color: #8cb8e0; font-size: 16px; font-weight: 600; }
.title .el-icon { font-size: 18px; color: #5b9bd5; }

.trace-tabs :deep(.el-tabs__header) { border-bottom-color: rgba(80, 150, 220, 0.06); margin-bottom: 16px; }
.trace-tabs :deep(.el-tabs__item) { color: rgba(255,255,255,0.4); transition: all 0.3s; }
.trace-tabs :deep(.el-tabs__item.is-active) { color: #5b9bd5; font-weight: 600; }
.trace-tabs :deep(.el-tabs__active-bar) { background: linear-gradient(90deg, #2196f3, #5b9bd5); }
.trace-tabs :deep(.el-tabs__nav-wrap::after) { display: none; }

.section-body { padding: 0; }
.add-btn { background: rgba(33, 150, 243, 0.15) !important; border: 1px solid rgba(33, 150, 243, 0.25) !important; color: #64b5f6 !important; border-radius: 10px; transition: all 0.3s; }
.add-btn:hover { background: rgba(33, 150, 243, 0.25) !important; }
.add-btn:disabled { opacity: 0.4; }

.full-width { width: 100%; }

.result-section { margin-top: 24px; }
.qr-code-box { text-align: center; padding: 16px; background: rgba(255,255,255,0.04); border-radius: 12px; border: 1px solid rgba(80,150,220,0.08); }
.qr-code-img { width: 160px; height: 160px; background: #fff; border-radius: 8px; padding: 8px; }
.qr-code-label { color: #8cb8e0; margin-top: 8px; font-size: 13px; }

.query-bar { display: flex; gap: 12px; margin-bottom: 20px; }
.query-input { flex: 1; max-width: 400px; }

.info-card { background: #0c1628 !important; border: 1px solid rgba(80,150,220,0.08) !important; border-radius: 12px; }
.info-card :deep(.el-card__header) { border-bottom: 1px solid rgba(80,150,220,0.06); padding: 12px 16px; }
.section-label { color: #8cb8e0; font-weight: 600; font-size: 14px; }

.trace-desc :deep(.el-descriptions__label) { background: #0f1f35 !important; color: #8cb8e0 !important; }
.trace-desc :deep(.el-descriptions__content) { background: #0c1628 !important; color: #d6e8f8 !important; }
.trace-desc :deep(.el-descriptions__body) { background: #0c1628 !important; }
.trace-desc :deep(.el-descriptions-row) { border-color: rgba(80,150,220,0.08) !important; }
.batch-desc :deep(.el-descriptions__label) { background: #0f1f35 !important; color: #8cb8e0 !important; }
.batch-desc :deep(.el-descriptions__content) { background: #0c1628 !important; color: #d6e8f8 !important; }
.batch-desc :deep(.el-descriptions__body) { background: #0c1628 !important; }
.batch-desc :deep(.el-descriptions-row) { border-color: rgba(80,150,220,0.08) !important; }
.gen-result-desc :deep(.el-descriptions__label) { background: #0f1f35 !important; color: #8cb8e0 !important; }
.gen-result-desc :deep(.el-descriptions__content) { background: #0c1628 !important; color: #d6e8f8 !important; }
.gen-result-desc :deep(.el-descriptions__body) { background: #0c1628 !important; }
.gen-result-desc :deep(.el-descriptions-row) { border-color: rgba(80,150,220,0.08) !important; }

.data-table { border-radius: 10px; overflow: hidden; }
.data-table :deep(.el-table) { background: #0c1628 !important; }
.data-table :deep(.el-table::before) { display: none; }
.data-table :deep(.el-table__header th) { background: #0f1f35 !important; color: #8cb8e0 !important; font-weight: 600; font-size: 13px; border-bottom: 1px solid rgba(80,150,220,0.15) !important; padding: 14px 0; }
.data-table :deep(.el-table__cell) { background: #0c1628 !important; color: #d6e8f8 !important; border-color: rgba(80,150,220,0.08) !important; padding: 12px 0; font-size: 14px; }
.data-table :deep(.el-table__cell .cell) { color: #d6e8f8 !important; }
.data-table :deep(.el-table__row:hover > td) { background: #132742 !important; }
.data-table :deep(.el-table__empty-block) { background: #0c1628 !important; }
.data-table :deep(.el-table__empty-text) { color: rgba(255,255,255,0.4); }
.data-table :deep(.el-loading-mask) { background: rgba(12, 22, 40, 0.8) !important; }

:deep(.el-divider__text) { background: rgba(12,22,40,0.5); color: #8cb8e0 !important; }
:deep(.el-divider) { border-color: rgba(80,150,220,0.08) !important; }

.qr-dialog-content { text-align: center; }
.dialog-qr-img { width: 220px; height: 220px; background: #fff; border-radius: 8px; padding: 10px; }
.dialog-code { margin-top: 12px; color: #8cb8e0; font-size: 13px; word-break: break-all; }
</style>

<style>
.qr-dialog { --el-dialog-bg-color: #0f1c2e; }
.qr-dialog .el-dialog { background: #0f1c2e; border: 1px solid rgba(80, 150, 220, 0.15); border-radius: 16px; box-shadow: 0 20px 60px rgba(0, 0, 0, 0.6); }
.qr-dialog .el-dialog__header { padding: 20px 24px 0; border-bottom: none; }
.qr-dialog .el-dialog__title { color: #d6e8f8; font-size: 18px; font-weight: 600; }
.qr-dialog .el-dialog__close { color: rgba(255, 255, 255, 0.5); }
.qr-dialog .el-dialog__close:hover { color: #5b9bd5; }
.qr-dialog .el-dialog__body { padding: 20px 24px; }
</style>