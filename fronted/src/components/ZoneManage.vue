<template>
  <div class="zone-root">
    <el-card class="main-card" shadow="never">
      <template #header>
        <div class="card-header">
          <div class="title-wrap">
            <span class="title">
              <el-icon><Grid /></el-icon>区域管理
            </span>
            <span class="subtitle">此处配置水质警告阈值，模拟数据生成范围请去「水质模拟配置」页面</span>
          </div>
          <el-button type="primary" @click="showAddDialog" class="add-btn">
            <el-icon><Plus /></el-icon>添加区域
          </el-button>
        </div>
      </template>

      <el-table :data="zoneList" row-key="id" class="data-table" empty-text="暂无数据" stripe>
        <el-table-column prop="name" label="区域名称" min-width="140" />
        <el-table-column prop="location" label="位置" min-width="120" />
        <el-table-column prop="area" label="面积(m²)" width="100" />
        <el-table-column prop="species" label="养殖品种" width="120" />
        <el-table-column label="操作" width="240" fixed="right">
          <template #default="{ row }">
            <el-button size="small" class="op-btn edit" @click="handleEdit(row)">编辑</el-button>
            <el-button size="small" class="op-btn threshold" @click="handleThreshold(row)">警告阈值</el-button>
            <el-button size="small" class="op-btn delete" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 添加/编辑区域弹窗 -->
    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑区域' : '添加区域'" width="500px" class="add-dialog">
      <el-form :model="form" label-width="100px" class="add-form">
        <el-form-item label="区域名称">
          <el-input v-model="form.name" class="full-width" placeholder="请输入区域名称" />
        </el-form-item>
        <el-form-item label="位置">
          <el-input v-model="form.location" class="full-width" placeholder="请输入位置" />
        </el-form-item>
        <el-form-item label="面积(m²)">
          <el-input-number v-model="form.area" class="full-width" :min="0" :precision="2" />
        </el-form-item>
        <el-form-item label="养殖品种">
          <el-input v-model="form.species" class="full-width" placeholder="请输入养殖品种" />
        </el-form-item>
        <el-form-item label="描述">
          <el-input v-model="form.description" type="textarea" :rows="3" class="full-width" placeholder="请输入描述" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>

    <!-- 阈值配置弹窗 -->
    <el-dialog v-model="thresholdVisible" :title="`警告阈值 - ${currentZoneName}`" width="520px" class="threshold-dialog">
      <el-form :model="thresholdForm" label-width="110px" class="threshold-form">
        <div class="threshold-section">
          <div class="section-title">
            <el-icon><Sunny /></el-icon>
            <span>水温阈值 (°C)</span>
          </div>
          <div class="threshold-row">
            <el-form-item label="最小值">
              <el-input-number v-model="thresholdForm.tempMin" :min="0" :max="50" :precision="1" class="threshold-input" />
            </el-form-item>
            <el-form-item label="最大值">
              <el-input-number v-model="thresholdForm.tempMax" :min="0" :max="50" :precision="1" class="threshold-input" />
            </el-form-item>
          </div>
        </div>
        <div class="threshold-section">
          <div class="section-title">
            <el-icon><Drizzling /></el-icon>
            <span>溶解氧阈值 (mg/L)</span>
          </div>
          <div class="threshold-row">
            <el-form-item label="最小值">
              <el-input-number v-model="thresholdForm.doMin" :min="0" :max="20" :precision="1" class="threshold-input" />
            </el-form-item>
            <el-form-item label="最大值">
              <el-input-number v-model="thresholdForm.doMax" :min="0" :max="20" :precision="1" class="threshold-input" />
            </el-form-item>
          </div>
        </div>
        <div class="threshold-section">
          <div class="section-title">
            <el-icon><DataLine /></el-icon>
            <span>pH阈值</span>
          </div>
          <div class="threshold-row">
            <el-form-item label="最小值">
              <el-input-number v-model="thresholdForm.phMin" :min="0" :max="14" :precision="2" class="threshold-input" />
            </el-form-item>
            <el-form-item label="最大值">
              <el-input-number v-model="thresholdForm.phMax" :min="0" :max="14" :precision="2" class="threshold-input" />
            </el-form-item>
          </div>
        </div>
        <div class="threshold-section">
          <div class="section-title">
            <el-icon><DataAnalysis /></el-icon>
            <span>盐度阈值 (‰)</span>
          </div>
          <div class="threshold-row">
            <el-form-item label="最小值">
              <el-input-number v-model="thresholdForm.salinityMin" :min="0" :max="50" :precision="1" class="threshold-input" />
            </el-form-item>
            <el-form-item label="最大值">
              <el-input-number v-model="thresholdForm.salinityMax" :min="0" :max="50" :precision="1" class="threshold-input" />
            </el-form-item>
          </div>
        </div>
      </el-form>
      <template #footer>
        <el-button @click="thresholdVisible = false">取消</el-button>
        <el-button type="primary" @click="handleThresholdSubmit">保存阈值</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import { ref, onMounted } from 'vue'
import { getZones, createZone, updateZone, deleteZone, getZoneThreshold, saveZoneThreshold } from '@/api/zone'
import { ElMessage } from 'element-plus'
import { Grid, Plus, Sunny, Drizzling, DataLine, DataAnalysis } from '@element-plus/icons-vue'

export default {
  name: 'ZoneManage',
  components: { Grid, Plus, Sunny, Drizzling, DataLine, DataAnalysis },
  setup() {
    const zoneList = ref([])
    const dialogVisible = ref(false)
    const isEdit = ref(false)
    const form = ref({})

    const thresholdVisible = ref(false)
    const currentZoneId = ref(null)
    const currentZoneName = ref('')
    const thresholdForm = ref({
      tempMin: 15, tempMax: 30,
      doMin: 5, doMax: 12,
      phMin: 7.0, phMax: 8.5,
      salinityMin: 20, salinityMax: 30
    })

    const loadZones = async () => {
      try { const res = await getZones(); zoneList.value = Array.isArray(res.data) ? res.data : [] } catch (e) { console.error(e); zoneList.value = [] }
    }

    const showAddDialog = () => {
      isEdit.value = false
      form.value = { name: '', location: '', area: null, species: '', description: '' }
      dialogVisible.value = true
    }

    const handleEdit = (row) => {
      isEdit.value = true
      form.value = { ...row }
      dialogVisible.value = true
    }

    const handleSubmit = async () => {
      try {
        if (isEdit.value) { await updateZone(form.value.id, form.value) } else { await createZone(form.value) }
        dialogVisible.value = false
        ElMessage.success('操作成功')
        loadZones()
      } catch (e) { ElMessage.error('操作失败') }
    }

    const handleDelete = async (row) => {
      try {
        if (confirm('确定删除该区域吗？')) {
          await deleteZone(row.id)
          ElMessage.success('删除成功')
          loadZones()
        }
      } catch (e) { ElMessage.error('删除失败') }
    }

    const handleThreshold = async (row) => {
      currentZoneId.value = row.id
      currentZoneName.value = row.name
      try {
        const res = await getZoneThreshold(row.id)
        if (res.data) {
          thresholdForm.value = {
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
          thresholdForm.value = {
            tempMin: 15, tempMax: 30,
            doMin: 5, doMax: 12,
            phMin: 7.0, phMax: 8.5,
            salinityMin: 20, salinityMax: 30
          }
        }
      } catch (e) {
        thresholdForm.value = {
          tempMin: 15, tempMax: 30,
          doMin: 5, doMax: 12,
          phMin: 7.0, phMax: 8.5,
          salinityMin: 20, salinityMax: 30
        }
      }
      thresholdVisible.value = true
    }

    const handleThresholdSubmit = async () => {
      try {
        await saveZoneThreshold(currentZoneId.value, thresholdForm.value)
        thresholdVisible.value = false
        ElMessage.success('阈值配置已保存')
      } catch (e) { ElMessage.error('保存失败') }
    }

    onMounted(() => { loadZones() })

    return {
      zoneList, dialogVisible, isEdit, form,
      thresholdVisible, currentZoneName, thresholdForm,
      showAddDialog, handleEdit, handleSubmit, handleDelete,
      handleThreshold, handleThresholdSubmit
    }
  }
}
</script>

<style scoped>
.zone-root { padding: 20px; }
.main-card {
  background: rgba(12, 22, 40, 0.5) !important;
  border: 1px solid rgba(80, 150, 220, 0.08) !important;
  border-radius: 16px;
  box-shadow: none;
}
.main-card :deep(.el-card__header) { border-bottom: 1px solid rgba(80, 150, 220, 0.06); padding: 16px 20px; }
.card-header { display: flex; justify-content: space-between; align-items: center; flex-wrap: wrap; gap: 12px; }
.title-wrap { display: flex; flex-direction: column; gap: 4px; }
.title { display: flex; align-items: center; gap: 8px; color: #8cb8e0; font-size: 16px; font-weight: 600; }
.title .el-icon { font-size: 18px; color: #5b9bd5; }
.subtitle { font-size: 13px; color: rgba(255, 255, 255, 0.45); }

.add-btn { background: rgba(33, 150, 243, 0.15) !important; border: 1px solid rgba(33, 150, 243, 0.25) !important; color: #64b5f6 !important; border-radius: 10px; transition: all 0.3s; }
.add-btn:hover { background: rgba(33, 150, 243, 0.25) !important; }

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
.data-table :deep(.el-table__empty-block) { background: #0c1628 !important; }
.data-table :deep(.el-table__empty-text) { color: rgba(255,255,255,0.4); }

.op-btn { border-radius: 6px !important; padding: 5px 12px !important; font-size: 12px !important; }
.op-btn.edit { background: rgba(91, 155, 213, 0.12) !important; border: 1px solid rgba(91, 155, 213, 0.2) !important; color: #8cb8e0 !important; }
.op-btn.edit:hover { background: rgba(91, 155, 213, 0.22) !important; }
.op-btn.threshold { background: rgba(0, 255, 136, 0.1) !important; border: 1px solid rgba(0, 255, 136, 0.2) !important; color: #00ff88 !important; }
.op-btn.threshold:hover { background: rgba(0, 255, 136, 0.18) !important; }
.op-btn.delete { background: rgba(255, 107, 53, 0.1) !important; border: 1px solid rgba(255, 107, 53, 0.2) !important; color: #ff8a65 !important; }
.op-btn.delete:hover { background: rgba(255, 107, 53, 0.18) !important; }

.full-width { width: 100%; }

.threshold-form { padding: 8px 0; }
.threshold-section { margin-bottom: 20px; }
.threshold-section:last-child { margin-bottom: 0; }
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
.threshold-row {
  display: flex;
  gap: 16px;
}
.threshold-row :deep(.el-form-item) { flex: 1; margin-bottom: 0; }
.threshold-input { width: 100%; }
</style>

