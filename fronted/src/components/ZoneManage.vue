<template>
  <div class="zone-root">
    <el-card class="main-card" shadow="never">
      <template #header>
        <div class="card-header">
          <span class="title">
            <el-icon><Grid /></el-icon>区域管理
          </span>
          <el-button type="primary" @click="showDialog" class="add-btn">
            <el-icon><Plus /></el-icon>添加区域
          </el-button>
        </div>
      </template>

      <el-table :data="zoneList" row-key="id" class="data-table" empty-text="暂无数据">
        <el-table-column prop="name" label="区域名称" width="150" />
        <el-table-column prop="location" label="位置" width="160" />
        <el-table-column prop="area" label="面积" width="120">
          <template #default="{ row }">{{ row.area }} m²</template>
        </el-table-column>
        <el-table-column prop="species" label="养殖品种" width="140" />
        <el-table-column prop="description" label="描述" show-overflow-tooltip />
        <el-table-column label="操作" width="180">
          <template #default="{ row }">
            <el-button size="small" @click="handleEdit(row)">编辑</el-button>
            <el-button size="small" type="danger" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑区域' : '添加区域'" width="500px" class="add-dialog">
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
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import { ref, onMounted } from 'vue'
import { getZones, createZone, updateZone, deleteZone } from '@/api/zone'
import { ElMessage } from 'element-plus'
import { Grid, Plus } from '@element-plus/icons-vue'

export default {
  name: 'ZoneManage',
  components: { Grid, Plus },
  setup() {
    const zoneList = ref([])
    const dialogVisible = ref(false)
    const isEdit = ref(false)
    const form = ref({})

    const loadZones = async () => { try { const res = await getZones(); zoneList.value = res.data } catch (e) { console.error(e) } }
    const showDialog = () => { isEdit.value = false; form.value = { name: '', location: '', area: null, species: '', description: '' }; dialogVisible.value = true }
    const handleEdit = (row) => { isEdit.value = true; form.value = { ...row }; dialogVisible.value = true }

    const handleSubmit = async () => {
      try {
        if (isEdit.value) { await updateZone(form.value.id, form.value) } else { await createZone(form.value) }
        dialogVisible.value = false; ElMessage.success('操作成功'); loadZones()
      } catch (e) { ElMessage.error('操作失败') }
    }

    const handleDelete = async (row) => {
      try { if (confirm('确定删除该区域吗？')) { await deleteZone(row.id); ElMessage.success('删除成功'); loadZones() } } catch (e) { ElMessage.error('删除失败') }
    }

    onMounted(() => { loadZones() })
    return { zoneList, dialogVisible, isEdit, form, showDialog, handleEdit, handleSubmit, handleDelete }
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
.card-header { display: flex; justify-content: space-between; align-items: center; }
.title { display: flex; align-items: center; gap: 8px; color: #8cb8e0; font-size: 16px; font-weight: 600; }
.title .el-icon { font-size: 18px; color: #5b9bd5; }

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
.add-dialog .el-input-number .el-input__wrapper { width: 100%; }
.add-dialog .el-textarea__inner { background: rgba(255, 255, 255, 0.06) !important; border: 1px solid rgba(80, 150, 220, 0.15); color: #d6e8f8 !important; border-radius: 8px; }
.add-dialog .el-textarea__inner:focus { border-color: #5b9bd5 !important; box-shadow: 0 0 0 2px rgba(91, 155, 213, 0.15) !important; }
.add-dialog .el-textarea__inner::placeholder { color: rgba(255, 255, 255, 0.3) !important; }
.add-dialog .el-button { border-radius: 8px; }
.add-dialog .el-button--default { background: rgba(255, 255, 255, 0.06) !important; border: 1px solid rgba(255, 255, 255, 0.1) !important; color: #a0c8e0 !important; }
.add-dialog .el-button--default:hover { background: rgba(255, 255, 255, 0.1) !important; border-color: rgba(255, 255, 255, 0.2) !important; color: #d6e8f8 !important; }
.add-dialog .el-button--primary { background: linear-gradient(135deg, #5b9bd5, #2196f3); border: none; box-shadow: 0 4px 15px rgba(33, 150, 243, 0.25); }
</style>