<template>
  <div class="disease-container">
    <el-card class="main-card">
      <template #header>
        <div class="card-header">
          <span class="title">病害防控管理</span>
        </div>
      </template>

      <div class="action-bar">
        <el-button type="primary" @click="showDialog" class="add-btn">
          记录病害
        </el-button>
      </div>

      <el-table :data="diseaseList" border class="data-table">
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
            <el-button type="success" size="small" @click="handleRecover(row)" v-if="row.status === 'ACTIVE'">
              治愈
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="dialogVisible" title="记录病害" width="500px" custom-class="disease-add-dialog">
      <el-form :model="form" label-width="100px" class="add-form">
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
import { getDiseasesByZone, createDisease, recoverDisease } from '@/api/record'
import { ElMessage } from 'element-plus'

export default {
  name: 'DiseaseControl',
  props: ['zoneId'],
  setup(props) {
    const diseaseList = ref([])
    const dialogVisible = ref(false)
    const form = ref({})

    const loadData = async () => {
      if (!props.zoneId) return
      
      try {
        const res = await getDiseasesByZone(props.zoneId)
        diseaseList.value = res.data
      } catch (error) {
        console.error(error)
      }
    }

    const showDialog = () => {
      form.value = { diseaseName: '', occurrenceDate: '', symptoms: '', treatment: '' }
      dialogVisible.value = true
    }

    const handleAdd = async () => {
      try {
        await createDisease({ ...form.value, zone: { id: props.zoneId } })
        dialogVisible.value = false
        ElMessage.success('添加成功')
        loadData()
      } catch (error) {
        ElMessage.error('添加失败')
      }
    }

    const handleRecover = async (row) => {
      try {
        await recoverDisease(row.id)
        ElMessage.success('状态已更新')
        loadData()
      } catch (error) {
        ElMessage.error('操作失败')
      }
    }

    watch(() => props.zoneId, () => {
      loadData()
    })

    onMounted(() => {
      loadData()
    })

    return {
      diseaseList,
      dialogVisible,
      form,
      showDialog,
      handleAdd,
      handleRecover
    }
  }
}
</script>

<style scoped>
.disease-container {
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

.action-bar {
  margin-bottom: 16px;
}

.add-btn {
  background: linear-gradient(135deg, #00d4ff 0%, #0077ff 100%);
  border: none;
  box-shadow: 0 4px 15px rgba(0, 212, 255, 0.4);
}

.data-table {
  border-radius: 8px;
  overflow: hidden;
}

.data-table :deep(.el-table) {
  background-color: rgba(10, 25, 40, 0.8) !important;
}

.data-table :deep(.el-table::before) {
  background-color: transparent !important;
}

.data-table :deep(.el-table__header-wrapper) {
  background-color: rgba(10, 25, 40, 0.9) !important;
}

.data-table :deep(.el-table__header) {
  background-color: rgba(10, 25, 40, 0.9) !important;
}

.data-table :deep(.el-table__header-wrapper th) {
  background: linear-gradient(135deg, rgba(0, 212, 255, 0.15) 0%, rgba(0, 119, 255, 0.1) 100%) !important;
  color: #00d4ff !important;
  font-weight: 600;
  font-size: 14px;
  letter-spacing: 0.5px;
  border-bottom: 2px solid rgba(0, 212, 255, 0.3) !important;
  padding: 12px 0;
}

.data-table :deep(.el-table__body-wrapper) {
  background-color: rgba(10, 25, 40, 0.8) !important;
}

.data-table :deep(.el-table__body) {
  background-color: rgba(10, 25, 40, 0.8) !important;
}

.data-table :deep(.el-table__row) {
  background-color: rgba(10, 25, 40, 0.8) !important;
}

.data-table :deep(.el-table__cell) {
  background-color: rgba(10, 25, 40, 0.8) !important;
  color: rgba(255, 255, 255, 0.9) !important;
  border-color: rgba(0, 212, 255, 0.08) !important;
  padding: 10px 0;
  font-size: 13px;
}

.data-table :deep(.el-table__row:hover > .el-table__cell) {
  background-color: rgba(0, 212, 255, 0.08) !important;
  color: #00d4ff !important;
}

.data-table :deep(.el-table__empty-block) {
  background-color: rgba(10, 25, 40, 0.5) !important;
}

.data-table :deep(.el-table__empty-text) {
  color: rgba(255, 255, 255, 0.5) !important;
}

.status-tag {
  font-weight: 600;
  border-radius: 12px;
  padding: 4px 12px;
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

.add-form :deep(.el-textarea__inner) {
  background-color: rgba(10, 25, 40, 0.8) !important;
  border: 1px solid rgba(0, 212, 255, 0.2) !important;
  border-radius: 8px !important;
  color: rgba(255, 255, 255, 0.9) !important;
  box-shadow: none !important;
}

.add-form :deep(.el-textarea__inner:hover) {
  border-color: rgba(0, 212, 255, 0.4) !important;
}

.add-form :deep(.el-textarea__inner:focus) {
  border-color: #00d4ff !important;
  box-shadow: 0 0 15px rgba(0, 212, 255, 0.2) !important;
}

.add-form :deep(.el-textarea__inner::placeholder) {
  color: rgba(255, 255, 255, 0.4) !important;
}

.add-form :deep(.el-date-editor.el-input__wrapper) {
  background-color: rgba(10, 25, 40, 0.8) !important;
  border: 1px solid rgba(0, 212, 255, 0.2) !important;
  border-radius: 8px !important;
  box-shadow: none !important;
}

.add-form :deep(.el-date-editor.el-input__wrapper:hover) {
  border-color: rgba(0, 212, 255, 0.4) !important;
}

.add-form :deep(.el-date-editor .el-input__inner) {
  color: rgba(255, 255, 255, 0.9) !important;
}

.add-form :deep(.el-input__icon) {
  color: rgba(0, 212, 255, 0.6) !important;
}

.add-form :deep(.el-picker-panel) {
  background-color: rgba(20, 40, 60, 0.95) !important;
  border: 1px solid rgba(0, 212, 255, 0.3) !important;
  color: #fff !important;
}

.add-form :deep(.el-picker-panel__icon-btn) {
  color: rgba(255, 255, 255, 0.7) !important;
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

.full-width {
  width: 100%;
}
</style>
