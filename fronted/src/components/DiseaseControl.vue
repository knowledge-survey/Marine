<template>
  <div class="disease-root">
    <el-card class="main-card" shadow="never">
      <template #header>
        <div class="card-header">
          <span class="title">
            <el-icon><Warning /></el-icon>病害防控管理
          </span>
        </div>
      </template>

      <div class="action-bar">
        <el-button type="primary" @click="showDialog" class="add-btn">
          <el-icon><Plus /></el-icon>记录病害
        </el-button>
      </div>

      <el-table :data="diseaseList" class="data-table" empty-text="暂无数据">
        <el-table-column prop="species" label="品种" width="120" />
        <el-table-column prop="batchNo" label="批次号" width="160" />
        <el-table-column prop="diseaseName" label="病害名称" />
        <el-table-column prop="occurrenceDate" label="发生日期" />
        <el-table-column prop="symptoms" label="症状" />
        <el-table-column prop="treatment" label="处理措施" />
        <el-table-column prop="status" label="状态" width="120">
          <template #default="{ row }">
            <el-tag :type="row.status === 'ACTIVE' ? 'warning' : 'success'" class="status-tag">
              {{ row.status === 'ACTIVE' ? '进行中' : '已治愈' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="120" v-if="diseaseList.some(d => d.status === 'ACTIVE')">
          <template #default="{ row }">
            <el-button type="success" size="small" @click="handleRecover(row)" v-if="row.status === 'ACTIVE'">治愈</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="dialogVisible" title="记录病害" width="500px" class="add-dialog">
      <el-form :model="form" label-width="100px" class="add-form">
        <el-form-item label="批次号">
          <el-select v-model="form.batchNo" class="full-width" placeholder="请选择批次" @change="handleBatchChange" filterable>
            <el-option v-for="seedling in seedlingList" :key="seedling.batchNo" :label="seedling.batchNo" :value="seedling.batchNo" />
          </el-select>
        </el-form-item>
        <el-form-item label="品种">
          <el-input v-model="form.species" class="full-width" disabled />
        </el-form-item>
        <el-form-item label="病害名称">
          <el-input v-model="form.diseaseName" class="full-width" />
        </el-form-item>
        <el-form-item label="发生日期">
          <el-date-picker v-model="form.occurrenceDate" type="date" value-format="YYYY-MM-DD" class="full-width" />
        </el-form-item>
        <el-form-item label="症状">
          <el-input v-model="form.symptoms" type="textarea" :rows="3" class="full-width" />
        </el-form-item>
        <el-form-item label="处理措施">
          <el-input v-model="form.treatment" type="textarea" :rows="3" class="full-width" />
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
import { ref, watch, onMounted } from 'vue'
import { getDiseasesByZone, createDisease, recoverDisease, getSeedlingsByZone } from '@/api/record'
import { ElMessage } from 'element-plus'
import { Warning, Plus } from '@element-plus/icons-vue'

export default {
  name: 'DiseaseControl',
  components: { Warning, Plus },
  props: ['zoneId'],
  setup(props) {
    const diseaseList = ref([])
    const seedlingList = ref([])
    const dialogVisible = ref(false)
    const form = ref({})

    const loadData = async () => {
      if (!props.zoneId) return
      try { const res = await getDiseasesByZone(props.zoneId); diseaseList.value = res.data } catch (e) { console.error(e) }
    }

    const loadSeedlings = async () => {
      if (!props.zoneId) return
      try { const res = await getSeedlingsByZone(props.zoneId); seedlingList.value = res.data || [] } catch (e) { console.error(e) }
    }

    const handleBatchChange = (batchNo) => { const s = seedlingList.value.find(s => s.batchNo === batchNo); if (s) form.value.species = s.species }
    const showDialog = () => { form.value = { species: '', batchNo: '', diseaseName: '', occurrenceDate: '', symptoms: '', treatment: '' }; dialogVisible.value = true }

    const handleAdd = async () => {
      if (!form.value.batchNo) { ElMessage.warning('请选择批次号'); return }
      if (!form.value.diseaseName) { ElMessage.warning('请输入病害名称'); return }
      if (!form.value.occurrenceDate) { ElMessage.warning('请选择发生日期'); return }
      if (!form.value.symptoms) { ElMessage.warning('请输入症状'); return }
      if (!form.value.treatment) { ElMessage.warning('请输入处理措施'); return }
      try { await createDisease({ ...form.value, zoneId: props.zoneId }); dialogVisible.value = false; ElMessage.success('添加成功'); loadData() } catch (e) { ElMessage.error('添加失败: ' + (e.response?.data?.message || e.message)) }
    }

    const handleRecover = async (row) => { try { await recoverDisease(row.id); ElMessage.success('状态已更新'); loadData() } catch (e) { ElMessage.error('操作失败') } }

    watch(() => props.zoneId, () => { loadData(); loadSeedlings() })
    onMounted(() => { loadData(); loadSeedlings() })

    return { diseaseList, seedlingList, dialogVisible, form, showDialog, handleAdd, handleRecover, handleBatchChange }
  }
}
</script>

<style scoped>
.disease-root { padding: 20px; }
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

.action-bar { margin-bottom: 16px; }
.add-btn { background: rgba(33, 150, 243, 0.15) !important; border: 1px solid rgba(33, 150, 243, 0.25) !important; color: #64b5f6 !important; border-radius: 10px; transition: all 0.3s; }
.add-btn:hover { background: rgba(33, 150, 243, 0.25) !important; }

.data-table { border-radius: 10px; overflow: hidden; }
.data-table :deep(.el-table) { background: #0c1628 !important; }
.data-table :deep(.el-table::before) { display: none; }
.data-table :deep(.el-table__header th) { background: #0f1f35 !important; color: #8cb8e0 !important; font-weight: 600; font-size: 13px; border-bottom: 1px solid rgba(80, 150, 220, 0.15) !important; padding: 14px 0; }
.data-table :deep(.el-table__cell) { background: #0c1628 !important; color: #d6e8f8 !important; border-color: rgba(80, 150, 220, 0.08) !important; padding: 12px 0; font-size: 14px; }
.data-table :deep(.el-table__cell .cell) { color: #d6e8f8 !important; }
.data-table :deep(.el-table__row:hover > td) { background: #132742 !important; }
.data-table :deep(.el-table__empty-block) { background: #0c1628 !important; }
.data-table :deep(.el-table__empty-text) { color: rgba(255,255,255,0.4); }

.status-tag { font-weight: 500; border-radius: 10px; padding: 2px 10px; }
.full-width { width: 100%; }
</style>

<style>
.add-dialog { --el-dialog-bg-color: #0f1c2e; }
.add-dialog .el-dialog { background: #0f1c2e; border: 1px solid rgba(80, 150, 220, 0.15); border-radius: 16px; box-shadow: 0 20px 60px rgba(0, 0, 0, 0.6); }
.add-dialog .el-dialog__header { padding: 20px 24px 0; border-bottom: none; }
.add-dialog .el-dialog__title { color: #d6e8f8; font-size: 18px; font-weight: 600; }
.add-dialog .el-dialog__close { color: rgba(255, 255, 255, 0.5); }
.add-dialog .el-dialog__close:hover { color: #5b9bd5; }
.add-dialog .el-dialog__body { padding: 20px 24px; }
.add-dialog .el-dialog__footer { padding: 0 24px 20px; border-top: none; }
.add-dialog .el-form-item__label { color: #a0c8e8 !important; font-weight: 500; font-size: 14px; }
.add-dialog .el-input__wrapper { background: rgba(255, 255, 255, 0.06) !important; box-shadow: none !important; border: 1px solid rgba(80, 150, 220, 0.15); border-radius: 8px; }
.add-dialog .el-input__wrapper:hover { border-color: rgba(80, 150, 220, 0.3); }
.add-dialog .el-input__wrapper.is-focus { border-color: #5b9bd5 !important; box-shadow: 0 0 0 2px rgba(91, 155, 213, 0.15) !important; }
.add-dialog .el-input__inner { color: #d6e8f8 !important; font-size: 14px; }
.add-dialog .el-input__inner::placeholder { color: rgba(255, 255, 255, 0.3) !important; }
.add-dialog .el-input.is-disabled .el-input__wrapper { background: rgba(255, 255, 255, 0.03) !important; opacity: 0.6; }
.add-dialog .el-select .el-input__wrapper { background: rgba(255, 255, 255, 0.06) !important; box-shadow: none !important; border: 1px solid rgba(80, 150, 220, 0.15); }
.add-dialog .el-select .el-input__inner { color: #d6e8f8 !important; }
.add-dialog .el-textarea__inner { background: rgba(255, 255, 255, 0.06) !important; border: 1px solid rgba(80, 150, 220, 0.15); color: #d6e8f8 !important; border-radius: 8px; }
.add-dialog .el-textarea__inner:focus { border-color: #5b9bd5 !important; box-shadow: 0 0 0 2px rgba(91, 155, 213, 0.15) !important; }
.add-dialog .el-textarea__inner::placeholder { color: rgba(255, 255, 255, 0.3) !important; }
.add-dialog .el-date-editor .el-input__wrapper { background: rgba(255, 255, 255, 0.06) !important; box-shadow: none !important; border: 1px solid rgba(80, 150, 220, 0.15); }
.add-dialog .el-button { border-radius: 8px; }
.add-dialog .el-button--default { background: rgba(255, 255, 255, 0.06) !important; border: 1px solid rgba(255, 255, 255, 0.1) !important; color: #a0c8e0 !important; }
.add-dialog .el-button--default:hover { background: rgba(255, 255, 255, 0.1) !important; border-color: rgba(255, 255, 255, 0.2) !important; color: #d6e8f8 !important; }
.add-dialog .el-button--primary { background: linear-gradient(135deg, #5b9bd5, #2196f3); border: none; box-shadow: 0 4px 15px rgba(33, 150, 243, 0.25); }
.add-dialog .el-select-dropdown { background: #0f1c2e !important; border: 1px solid rgba(80, 150, 220, 0.15) !important; }
.add-dialog .el-select-dropdown__item { color: #d6e8f8 !important; }
.add-dialog .el-select-dropdown__item.hover,
.add-dialog .el-select-dropdown__item:hover { background: rgba(80, 150, 220, 0.1) !important; }
.add-dialog .el-select-dropdown__item.selected { color: #5b9bd5 !important; font-weight: 600; }
.add-dialog .el-popper.is-light { background: #0f1c2e !important; border: 1px solid rgba(80, 150, 220, 0.15) !important; color: #d6e8f8 !important; }
.add-dialog .el-picker-panel { background: #0f1c2e !important; border: 1px solid rgba(80, 150, 220, 0.15) !important; color: #d6e8f8 !important; }
</style>