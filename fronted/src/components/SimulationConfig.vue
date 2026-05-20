<template>
  <div class="simulation-root">
    <el-card class="main-card" shadow="never">
      <template #header>
        <div class="card-header">
          <span class="title">
            <el-icon><TrendCharts /></el-icon>
            水质模拟生成配置
          </span>
          <span class="subtitle">此配置用于控制每30秒自动生成的水质数据范围，与警告阈值独立</span>
        </div>
      </template>

      <el-table :data="zoneList" row-key="id" class="data-table" empty-text="暂无数据" stripe>
        <el-table-column prop="name" label="区域名称" min-width="160" />
        <el-table-column prop="location" label="位置" min-width="120" />
        <el-table-column prop="species" label="养殖品种" width="120" />
        <el-table-column label="模拟配置状态" width="140">
          <template #default="{ row }">
            <el-tag :type="getZoneSimulation(row.id) ? 'success' : 'info'" size="small">
              {{ getZoneSimulation(row.id) ? '已配置' : '默认范围' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="180" fixed="right">
          <template #default="{ row }">
            <el-button size="small" class="op-btn edit" @click="handleEdit(row)">配置模拟</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 配置弹窗 -->
    <el-dialog v-model="dialogVisible" :title="`配置模拟 - ${currentZoneName}`" width="540px" class="config-dialog">
      <el-form :model="form" label-width="110px" class="config-form">
        <div class="config-section">
          <div class="section-title">
            <el-icon><Sunny /></el-icon>
            <span>水温范围 (°C)</span>
          </div>
          <div class="config-row">
            <el-form-item label="最小值">
              <el-input-number v-model="form.tempMin" :min="0" :max="50" :precision="1" class="config-input" />
            </el-form-item>
            <el-form-item label="最大值">
              <el-input-number v-model="form.tempMax" :min="0" :max="50" :precision="1" class="config-input" />
            </el-form-item>
          </div>
        </div>

        <div class="config-section">
          <div class="section-title">
            <el-icon><Drizzling /></el-icon>
            <span>溶解氧范围 (mg/L)</span>
          </div>
          <div class="config-row">
            <el-form-item label="最小值">
              <el-input-number v-model="form.doMin" :min="0" :max="20" :precision="1" class="config-input" />
            </el-form-item>
            <el-form-item label="最大值">
              <el-input-number v-model="form.doMax" :min="0" :max="20" :precision="1" class="config-input" />
            </el-form-item>
          </div>
        </div>

        <div class="config-section">
          <div class="section-title">
            <el-icon><DataLine /></el-icon>
            <span>pH值范围</span>
          </div>
          <div class="config-row">
            <el-form-item label="最小值">
              <el-input-number v-model="form.phMin" :min="0" :max="14" :precision="2" class="config-input" />
            </el-form-item>
            <el-form-item label="最大值">
              <el-input-number v-model="form.phMax" :min="0" :max="14" :precision="2" class="config-input" />
            </el-form-item>
          </div>
        </div>

        <div class="config-section">
          <div class="section-title">
            <el-icon><DataAnalysis /></el-icon>
            <span>盐度范围 (‰)</span>
          </div>
          <div class="config-row">
            <el-form-item label="最小值">
              <el-input-number v-model="form.salinityMin" :min="0" :max="50" :precision="1" class="config-input" />
            </el-form-item>
            <el-form-item label="最大值">
              <el-input-number v-model="form.salinityMax" :min="0" :max="50" :precision="1" class="config-input" />
            </el-form-item>
          </div>
        </div>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSave">保存配置</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import { ref, onMounted } from 'vue'
import { getZones } from '@/api/zone'
import { getAllSimulations, getSimulationByZoneId, saveSimulation } from '@/api/simulation'
import { ElMessage } from 'element-plus'
import { TrendCharts, Sunny, Drizzling, DataLine, DataAnalysis } from '@element-plus/icons-vue'

export default {
  name: 'SimulationConfig',
  components: { TrendCharts, Sunny, Drizzling, DataLine, DataAnalysis },
  setup() {
    const zoneList = ref([])
    const simulationList = ref([])
    const dialogVisible = ref(false)
    const currentZoneId = ref(null)
    const currentZoneName = ref('')
    const form = ref({
      tempMin: 15, tempMax: 30,
      doMin: 5, doMax: 12,
      phMin: 7.0, phMax: 8.5,
      salinityMin: 20, salinityMax: 30
    })

    const loadData = async () => {
      try {
        const [zonesRes, simRes] = await Promise.all([
          getZones(),
          getAllSimulations()
        ])
        zoneList.value = zonesRes.data || []
        simulationList.value = simRes.data || []
      } catch (e) {
        console.error(e)
      }
    }

    const getZoneSimulation = (zoneId) => {
      return simulationList.value.find(s => s.zoneId === zoneId)
    }

    const handleEdit = async (row) => {
      currentZoneId.value = row.id
      currentZoneName.value = row.name

      try {
        const res = await getSimulationByZoneId(row.id)
        if (res.data) {
          form.value = {
            tempMin: res.data.tempMin || 15,
            tempMax: res.data.tempMax || 30,
            doMin: res.data.doMin || 5,
            doMax: res.data.doMax || 12,
            phMin: res.data.phMin || 7.0,
            phMax: res.data.phMax || 8.5,
            salinityMin: res.data.salinityMin || 20,
            salinityMax: res.data.salinityMax || 30
          }
        } else {
          form.value = {
            tempMin: 15, tempMax: 30,
            doMin: 5, doMax: 12,
            phMin: 7.0, phMax: 8.5,
            salinityMin: 20, salinityMax: 30
          }
        }
      } catch (e) {
        form.value = {
          tempMin: 15, tempMax: 30,
          doMin: 5, doMax: 12,
          phMin: 7.0, phMax: 8.5,
          salinityMin: 20, salinityMax: 30
        }
      }

      dialogVisible.value = true
    }

    const handleSave = async () => {
      try {
        await saveSimulation(currentZoneId.value, form.value)
        dialogVisible.value = false
        ElMessage.success('配置保存成功')
        loadData()
      } catch (e) {
        ElMessage.error('保存失败')
      }
    }

    onMounted(() => {
      loadData()
    })

    return {
      zoneList,
      dialogVisible,
      currentZoneName,
      form,
      getZoneSimulation,
      handleEdit,
      handleSave
    }
  }
}
</script>

<style scoped>
.simulation-root { padding: 20px; }
.main-card {
  background: rgba(12, 22, 40, 0.5) !important;
  border: 1px solid rgba(80, 150, 220, 0.08) !important;
  border-radius: 16px;
  box-shadow: none;
}
.main-card :deep(.el-card__header) {
  border-bottom: 1px solid rgba(80, 150, 220, 0.06);
  padding: 16px 20px;
}
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  flex-wrap: wrap;
  gap: 12px;
}
.title {
  display: flex;
  align-items: center;
  gap: 8px;
  color: #8cb8e0;
  font-size: 16px;
  font-weight: 600;
}
.title .el-icon { font-size: 18px; color: #5b9bd5; }
.subtitle {
  font-size: 13px;
  color: rgba(255, 255, 255, 0.45);
}

.data-table { border-radius: 10px; overflow: hidden; }
.data-table :deep(.el-table) { background: #0c1628 !important; }
.data-table :deep(.el-table::before) { display: none; }
.data-table :deep(.el-table__header th) {
  background: #0f1f35 !important;
  color: #8cb8e0 !important;
  font-weight: 600;
  font-size: 13px;
  border-bottom: 1px solid rgba(80, 150, 220, 0.15) !important;
  padding: 14px 0;
}
.data-table :deep(.el-table__cell) {
  background: #0c1628 !important;
  color: #d6e8f8 !important;
  border-color: rgba(80, 150, 220, 0.08) !important;
  padding: 12px 0;
  font-size: 14px;
}
.data-table :deep(.el-table__cell .cell) { color: #d6e8f8 !important; }
.data-table :deep(.el-table__row:hover > td) { background: #132742 !important; }

.op-btn { border-radius: 6px !important; padding: 5px 12px !important; font-size: 12px !important; }
.op-btn.edit { background: rgba(91, 155, 213, 0.12) !important; border: 1px solid rgba(91, 155, 213, 0.2) !important; color: #8cb8e0 !important; }
.op-btn.edit:hover { background: rgba(91, 155, 213, 0.22) !important; }

.config-form { padding: 8px 0; }
.config-section { margin-bottom: 20px; }
.config-section:last-child { margin-bottom: 0; }
.section-title {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 14px;
  font-weight: 600;
  color: #8cb8e0;
  margin-bottom: 12px;
  padding-bottom: 8px;
  border-bottom: 1px solid rgba(80, 150, 220, 0.08);
}
.section-title .el-icon { font-size: 16px; color: #5b9bd5; }
.config-row {
  display: flex;
  gap: 16px;
}
.config-row :deep(.el-form-item) { flex: 1; margin-bottom: 0; }
.config-input { width: 100%; }
</style>


