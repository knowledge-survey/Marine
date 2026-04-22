<template>
  <div class="zone-manage">
    <el-card class="main-card">
      <template #header>
        <div class="card-header">
          <span class="title">区域管理</span>
          <el-button type="primary" @click="showDialog" class="add-btn">
            添加区域
          </el-button>
        </div>
      </template>

      <div class="table-wrapper">
        <el-table :data="zoneList" border row-key="id" class="data-table">
          <el-table-column prop="name" label="区域名称" width="150" />
          <el-table-column prop="location" label="位置" width="160" />
          <el-table-column prop="area" label="面积" width="120">
            <template #default="{ row }">
              {{ row.area }} m²
            </template>
          </el-table-column>
          <el-table-column prop="species" label="养殖品种" width="140" />
          <el-table-column prop="description" label="描述" show-overflow-tooltip />
          <el-table-column label="操作" width="180">
            <template #default="{ row }">
              <el-button size="small" class="action-btn" @click="handleEdit(row)">
                编辑
              </el-button>
              <el-button size="small" type="danger" class="action-btn" @click="handleDelete(row)">
                删除
              </el-button>
            </template>
          </el-table-column>
        </el-table>
      </div>
    </el-card>

    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑区域' : '添加区域'" class="add-dialog">
      <el-form :model="form" label-width="100px" class="add-form">
        <el-form-item label="区域名称">
          <el-input v-model="form.name" class="full-width" />
        </el-form-item>
        <el-form-item label="位置">
          <el-input v-model="form.location" class="full-width" />
        </el-form-item>
        <el-form-item label="面积">
          <el-input-number v-model="form.area" class="full-width" />
        </el-form-item>
        <el-form-item label="养殖品种">
          <el-input v-model="form.species" class="full-width" />
        </el-form-item>
        <el-form-item label="描述">
          <el-input v-model="form.description" type="textarea" :rows="3" class="full-width" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false" class="cancel-btn">取消</el-button>
        <el-button type="primary" @click="handleSubmit" class="confirm-btn">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import { ref, onMounted } from 'vue'
import { getZones, createZone, updateZone, deleteZone } from '@/api/zone'
import { ElMessage } from 'element-plus'

export default {
  name: 'ZoneManage',
  setup() {
    const zoneList = ref([])
    const dialogVisible = ref(false)
    const isEdit = ref(false)
    const form = ref({})

    const loadZones = async () => {
      try {
        const res = await getZones()
        zoneList.value = res.data
      } catch (error) {
        console.error(error)
      }
    }

    const showDialog = () => {
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
        if (isEdit.value) {
          await updateZone(form.value.id, form.value)
        } else {
          await createZone(form.value)
        }
        dialogVisible.value = false
        ElMessage.success('操作成功')
        loadZones()
      } catch (error) {
        ElMessage.error('操作失败')
      }
    }

    const handleDelete = async (row) => {
      try {
        if (confirm('确定删除该区域吗？')) {
          await deleteZone(row.id)
          ElMessage.success('删除成功')
          loadZones()
        }
      } catch (error) {
        ElMessage.error('删除失败')
      }
    }

    onMounted(() => {
      loadZones()
    })

    return {
      zoneList,
      dialogVisible,
      isEdit,
      form,
      showDialog,
      handleEdit,
      handleSubmit,
      handleDelete
    }
  }
}
</script>

<style scoped>
.zone-manage {
  width: 100%;
}

.main-card {
  background: rgba(20, 40, 60, 0.9) !important;
  border: 1px solid rgba(0, 212, 255, 0.3) !important;
  border-radius: 12px;
}

.main-card :deep(.el-card__header) {
  border-bottom: 1px solid rgba(0, 212, 255, 0.2) !important;
  background: transparent !important;
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

.table-wrapper {
  border-radius: 8px;
  overflow: hidden;
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

.action-btn {
  border-radius: 6px;
}

.add-dialog :deep(.el-dialog) {
  background: rgba(20, 40, 60, 0.95) !important;
  border: 1px solid rgba(0, 212, 255, 0.3) !important;
  border-radius: 12px;
}

.add-dialog :deep(.el-dialog__header) {
  border-bottom: 1px solid rgba(0, 212, 255, 0.2) !important;
}

.add-dialog :deep(.el-dialog__title) {
  color: #00d4ff !important;
}

.add-form :deep(.el-form-item__label) {
  color: rgba(255, 255, 255, 0.9) !important;
}

.add-form :deep(.el-input__wrapper) {
  background-color: rgba(10, 25, 40, 0.8) !important;
  border: 1px solid rgba(0, 212, 255, 0.2) !important;
}

.add-form :deep(.el-input__wrapper.is-focus) {
  border-color: #00d4ff !important;
}

.add-form :deep(.el-input__inner) {
  color: #fff !important;
}

.add-form :deep(.el-textarea__inner) {
  background-color: rgba(10, 25, 40, 0.8) !important;
  border: 1px solid rgba(0, 212, 255, 0.2) !important;
  color: #fff !important;
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

.full-width {
  width: 100%;
}

.cancel-btn {
  background: rgba(255, 255, 255, 0.1);
  border: 1px solid rgba(255, 255, 255, 0.2);
  color: rgba(255, 255, 255, 0.8);
}

.cancel-btn:hover {
  background: rgba(255, 255, 255, 0.15);
  color: #fff;
}

.confirm-btn {
  background: linear-gradient(135deg, #00d4ff 0%, #0077ff 100%);
  border: none;
  box-shadow: 0 4px 15px rgba(0, 212, 255, 0.4);
}
</style>
