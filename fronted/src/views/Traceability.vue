<template>
  <div class="traceability-root">
    <div class="app-wrapper">
      <div class="top-bar">
        <div class="brand">
          <el-icon class="brand-icon"><CircleCheck /></el-icon>
          <span class="brand-text">产品溯源查询</span>
        </div>
      </div>

      <div class="query-section">
        <div class="query-card">
          <div class="query-icon">
            <el-icon :size="48"><Search /></el-icon>
          </div>
          <h3 class="query-title">请输入溯源码查询产品来源</h3>
          <div class="query-input-row">
            <el-input v-model="code" placeholder="请输入溯源码或扫描二维码" size="large" class="query-input" @keyup.enter="handleQuery" />
            <el-button type="primary" size="large" @click="handleQuery" class="query-btn">
              <el-icon><Search /></el-icon>查询
            </el-button>
          </div>
        </div>

        <div v-if="traceData" class="result-card">
          <el-divider><span class="divider-text">溯源信息</span></el-divider>
          <el-descriptions :column="2" size="small" class="result-desc">
            <el-descriptions-item label="溯源码">{{ traceData.code }}</el-descriptions-item>
            <el-descriptions-item label="描述">{{ traceData.description }}</el-descriptions-item>
            <el-descriptions-item label="创建时间">{{ traceData.createTime }}</el-descriptions-item>
            <el-descriptions-item label="扫描次数">{{ traceData.scanCount }}</el-descriptions-item>
          </el-descriptions>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { ref } from 'vue'
import { getTraceabilityByCode } from '@/api/record'
import { CircleCheck, Search } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'

export default {
  name: 'Traceability',
  components: { CircleCheck, Search },
  setup() {
    const code = ref('')
    const traceData = ref(null)
    const handleQuery = async () => {
      if (!code.value) { ElMessage.warning('请输入溯源码'); return }
      try { const res = await getTraceabilityByCode(code.value); traceData.value = res.data } catch (e) { ElMessage.error('未找到溯源信息'); traceData.value = null }
    }
    return { code, traceData, handleQuery }
  }
}
</script>

<style scoped>
.traceability-root {
  min-height: 100vh;
  padding: 20px;
  background: linear-gradient(170deg, #060d1a 0%, #0d1b2a 25%, #132742 50%, #0f1f35 75%, #080f1e 100%);
  display: flex;
  justify-content: center;
}

.app-wrapper {
  width: 100%;
  max-width: 720px;
  background: rgba(12, 22, 40, 0.88);
  backdrop-filter: blur(24px);
  -webkit-backdrop-filter: blur(24px);
  border: 1px solid rgba(80, 150, 220, 0.12);
  border-radius: 24px;
  box-shadow: 0 30px 80px rgba(0, 0, 0, 0.55), 0 0 0 1px rgba(255,255,255,0.03) inset;
  overflow: hidden;
}

.top-bar {
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 14px 28px;
  background: rgba(8, 18, 32, 0.6);
  border-bottom: 1px solid rgba(80, 150, 220, 0.08);
}

.brand { display: flex; align-items: center; gap: 10px; }
.brand-icon { font-size: 22px; color: #5b9bd5; filter: drop-shadow(0 0 8px rgba(91,155,213,0.5)); }
.brand-text { font-size: 18px; font-weight: 700; color: #d6e8f8; letter-spacing: 1.5px; }

.query-section { padding: 40px 28px; }
.query-card { text-align: center; margin-bottom: 32px; }
.query-icon { color: rgba(91, 155, 213, 0.4); margin-bottom: 16px; }
.query-title { color: rgba(255,255,255,0.6); font-size: 16px; font-weight: 500; margin: 0 0 24px; }

.query-input-row { display: flex; gap: 12px; max-width: 500px; margin: 0 auto; }
.query-input { flex: 1; }
.query-btn { background: linear-gradient(135deg, #5b9bd5, #2196f3); border: none; border-radius: 10px; box-shadow: 0 4px 15px rgba(33,150,243,0.3); }

.result-card { padding-top: 8px; }
.divider-text { color: #8cb8e0; font-size: 14px; font-weight: 600; }

.result-desc :deep(.el-descriptions__label) { background: rgba(33,120,220,0.06) !important; color: #8cb8e0 !important; }
.result-desc :deep(.el-descriptions__body) { color: rgba(255,255,255,0.75) !important; }

:deep(.el-divider__text) { background: rgba(12,22,40,0.88); }
:deep(.el-divider) { border-color: rgba(80,150,220,0.08) !important; }
</style>