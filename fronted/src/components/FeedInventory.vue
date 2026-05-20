<template>
  <div class="inv-container">
    <div class="filter-bar">
      <el-input v-model="filterKeyword" placeholder="搜索饲料名称/供应商" clearable size="small" class="filter-input" />
      <el-input v-model="filterForm.zoneName" placeholder="区域筛选" clearable size="small" class="filter-sel" />
      <el-button size="small" class="filter-btn" @click="loadData">查询</el-button>
      <el-button size="small" class="filter-btn-reset" @click="resetFilter">重置</el-button>
      <el-button size="small" class="add-btn" @click="handleAdd">新增</el-button>
    </div>

    <el-table :data="tableData" class="data-table" empty-text="暂无数据" v-loading="loading">
      <el-table-column prop="feedName" label="饲料名称" min-width="180" />
      <el-table-column prop="brand" label="品牌" min-width="120" />
      <el-table-column prop="supplier" label="供应商" min-width="140" />
      <el-table-column prop="zoneName" label="存储区域" min-width="130" />
      <el-table-column label="当前库存" min-width="100">
        <template #default="{ row }">
          <span :class="{low: row.currentStock <= row.warningThreshold}">{{ row.currentStock }} {{ row.unit }}</span>
        </template>
      </el-table-column>
      <el-table-column label="预警阈值" min-width="90">
        <template #default="{ row }">{{ row.warningThreshold }} {{ row.unit }}</template>
      </el-table-column>
      <el-table-column prop="purchaseDate" label="采购日期" min-width="110" />
      <el-table-column prop="expireDate" label="保质期至" min-width="110" />
      <el-table-column label="成本(元/kg)" min-width="100">
        <template #default="{ row }">¥{{ row.costPerKg }}</template>
      </el-table-column>
      <el-table-column prop="remarks" label="备注" min-width="140" show-overflow-tooltip />
      <el-table-column label="操作" min-width="140" fixed="right">
        <template #default="{ row }">
          <el-button size="small" class="action-btn" @click="handleEdit(row)">编辑</el-button>
          <el-button size="small" class="action-btn-del" @click="handleDelete(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog v-model="dialogVisible" :title="editing ? '编辑饲料' : '新增饲料'" width="520px" class="add-dialog" :append-to-body="true" :close-on-click-modal="false">
      <el-form :model="form" label-width="90px">
        <el-form-item label="饲料名称"><el-input v-model="form.feedName" /></el-form-item>
        <el-form-item label="品牌"><el-input v-model="form.brand" /></el-form-item>
        <el-form-item label="供应商"><el-input v-model="form.supplier" /></el-form-item>
        <el-form-item label="区域">
          <el-input v-model="form.zoneName" placeholder="如：A区仓库-3号架" />
        </el-form-item>
        <el-form-item label="当前库存">
          <el-input-number v-model="form.currentStock" :min="0" :precision="2" style="width:100%" />
        </el-form-item>
        <el-form-item label="预警阈值">
          <el-input-number v-model="form.warningThreshold" :min="0" :precision="2" style="width:100%" />
        </el-form-item>
        <el-form-item label="单位">
          <el-select v-model="form.unit" style="width:100%">
            <el-option label="公斤" value="kg" />
            <el-option label="吨" value="ton" />
            <el-option label="包" value="bag" />
          </el-select>
        </el-form-item>
        <el-form-item label="成本(元/kg)"><el-input-number v-model="form.costPerKg" :min="0" :precision="2" style="width:100%" /></el-form-item>
        <el-form-item label="采购日期"><el-date-picker v-model="form.purchaseDate" type="date" style="width:100%" value-format="YYYY-MM-DD" /></el-form-item>
        <el-form-item label="保质期至"><el-date-picker v-model="form.expireDate" type="date" style="width:100%" value-format="YYYY-MM-DD" /></el-form-item>
        <el-form-item label="备注"><el-input v-model="form.remarks" type="textarea" :rows="2" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSave" :loading="saving">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { listFeedInventory, saveFeedInventory, updateFeedInventory, deleteFeedInventory } from '@/api/feedInventory'

export default {
  name: 'FeedInventory',
  setup() {
    const tableData = ref([])
    const loading = ref(false)
    const saving = ref(false)
    const dialogVisible = ref(false)
    const editing = ref(null)
    const filterKeyword = ref('')
    const filterForm = reactive({ zoneName: null })
    const form = reactive({
      feedName: '', brand: '', supplier: '', zoneName: '',
      currentStock: 0, warningThreshold: 50, unit: 'kg',
      costPerKg: 0, purchaseDate: '', expireDate: '', remarks: ''
    })

    const resetForm = () => {
      form.feedName = ''; form.brand = ''; form.supplier = ''; form.zoneName = '';
      form.currentStock = 0; form.warningThreshold = 50; form.unit = 'kg';
      form.costPerKg = 0; form.purchaseDate = ''; form.expireDate = ''; form.remarks = ''
    }

    const loadData = async () => {
      loading.value = true
      try {
        const params = {}
        if (filterForm.zoneName) params.zoneName = filterForm.zoneName
        if (filterKeyword.value) params.keyword = filterKeyword.value
        const res = await listFeedInventory(params)
        tableData.value = Array.isArray(res.data) ? res.data : []
      } catch (e) { console.error(e); tableData.value = [] }
      finally { loading.value = false }
    }

    const handleAdd = () => { editing.value = null; resetForm(); dialogVisible.value = true }
    const handleEdit = (row) => { editing.value = row; Object.assign(form, row);
      dialogVisible.value = true }

    const handleSave = async () => {
      saving.value = true
      try {
        const data = { ...form }
        if (editing.value) { await updateFeedInventory(editing.value.id, data); ElMessage.success('编辑成功') }
        else { await saveFeedInventory(data); ElMessage.success('添加成功') }
        dialogVisible.value = false; loadData()
      } catch (e) { ElMessage.error('操作失败') }
      finally { saving.value = false }
    }

    const handleDelete = async (row) => {
      try { await ElMessageBox.confirm('确定删除？', '提示', { type: 'warning' }); await deleteFeedInventory(row.id); ElMessage.success('已删除'); loadData() } catch { /* ignore */ }
    }

    const resetFilter = () => { filterKeyword.value = ''; filterForm.zoneName = null; loadData() }

    onMounted(() => { loadData() })
    return { tableData, loading, saving, dialogVisible, editing, filterKeyword, filterForm, form,
      loadData, handleAdd, handleEdit, handleSave, handleDelete, resetFilter }
  }
}
</script>

<style scoped>
.inv-container { padding-top: 8px; }
.filter-bar { display: flex; gap: 10px; margin-bottom: 12px; align-items: center; }
.filter-input { width: 200px; }
.filter-sel { width: 150px; }
.filter-btn { background: rgba(33,150,243,0.12) !important; border: 1px solid rgba(33,150,243,0.2) !important; color: #64b5f6 !important; border-radius: 8px; }
.filter-btn:hover { background: rgba(33,150,243,0.22) !important; }
.filter-btn-reset { background: rgba(255,255,255,0.04) !important; border: 1px solid rgba(255,255,255,0.08) !important; color: rgba(255,255,255,0.4) !important; border-radius: 8px; }
.add-btn { background: rgba(0,255,136,0.1) !important; border: 1px solid rgba(0,255,136,0.2) !important; color: #00ff88 !important; border-radius: 8px; }

.data-table { border-radius: 10px; overflow: hidden; }
.data-table :deep(.el-table) { background: #0c1628 !important; }
.data-table :deep(.el-table::before) { display: none; }
.data-table :deep(.el-table__header th) { background: #0f1f35 !important; color: #8cb8e0 !important; font-weight: 600; font-size: 13px; border-bottom: 1px solid rgba(80,150,220,0.15) !important; }
.data-table :deep(.el-table__cell) { background: #0c1628 !important; color: #d6e8f8 !important; border-color: rgba(80,150,220,0.08) !important; font-size: 14px; }
.data-table :deep(.el-table__row:hover > td) { background: #132742 !important; }
.data-table :deep(.el-table__empty-block) { background: #0c1628 !important; }
.data-table :deep(.el-table__empty-text) { color: rgba(255,255,255,0.4); }
.data-table td span.low { color: #ff6b35; font-weight: 600; }

.action-btn { background: rgba(80,150,220,0.1) !important; border: 1px solid rgba(80,150,220,0.15) !important; color: #8cb8e0 !important; border-radius: 6px; }
.action-btn:hover { background: rgba(80,150,220,0.2) !important; }
.action-btn-del { background: rgba(255,68,68,0.1) !important; border: 1px solid rgba(255,68,68,0.15) !important; color: #ff6b6b !important; border-radius: 6px; }
.action-btn-del:hover { background: rgba(255,68,68,0.2) !important; }
</style>

<style>
.add-dialog {
  --el-dialog-bg-color: #0f1c2e;
}
.add-dialog .el-dialog {
  background: #0f1c2e;
  border: 1px solid rgba(80,150,220,0.15);
  border-radius: 16px;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.6);
}
.add-dialog .el-dialog__header {
  padding: 20px 24px 0;
  border-bottom: none;
}
.add-dialog .el-dialog__title {
  color: #d6e8f8;
  font-size: 18px;
  font-weight: 600;
}
.add-dialog .el-dialog__close {
  color: rgba(255, 255, 255, 0.5);
}
.add-dialog .el-dialog__close:hover {
  color: #5b9bd5;
}
.add-dialog .el-dialog__body {
  padding: 20px 24px;
}
.add-dialog .el-dialog__footer {
  padding: 0 24px 20px;
  border-top: none;
}
.add-dialog .el-form-item__label {
  color: #a0c8e8 !important;
  font-weight: 500;
  font-size: 14px;
}
.add-dialog .el-input__wrapper {
  background: rgba(255,255,255,0.06) !important;
  box-shadow: none !important;
  border: 1px solid rgba(80,150,220,0.15);
  border-radius: 8px;
}
.add-dialog .el-input__wrapper:hover {
  border-color: rgba(80,150,220,0.3);
}
.add-dialog .el-input__wrapper.is-focus {
  border-color: #5b9bd5 !important;
  box-shadow: 0 0 0 2px rgba(91,155,213,0.15) !important;
}
.add-dialog .el-input__inner {
  color: #d6e8f8 !important;
  font-size: 14px;
}
.add-dialog .el-input__inner::placeholder {
  color: rgba(255,255,255,0.3) !important;
}
.add-dialog .el-input.is-disabled .el-input__wrapper {
  background: rgba(255,255,255,0.03) !important;
  opacity: 0.6;
}
.add-dialog .el-select .el-input__wrapper {
  background: rgba(255,255,255,0.06) !important;
  box-shadow: none !important;
  border: 1px solid rgba(80,150,220,0.15);
}
.add-dialog .el-select-dropdown {
  background: #0f1c2e !important;
  border: 1px solid rgba(80,150,220,0.15) !important;
}
.add-dialog .el-select-dropdown__item {
  color: #d6e8f8 !important;
}
.add-dialog .el-select-dropdown__item.hover,
.add-dialog .el-select-dropdown__item:hover {
  background: rgba(80,150,220,0.1) !important;
}
.add-dialog .el-select-dropdown__item.selected {
  color: #5b9bd5 !important;
  font-weight: 600;
}
.add-dialog .el-popper.is-light {
  background: #0f1c2e !important;
  border: 1px solid rgba(80,150,220,0.15) !important;
  color: #d6e8f8 !important;
}
.add-dialog .el-picker-panel {
  background: #0f1c2e !important;
  border: 1px solid rgba(80,150,220,0.15) !important;
  color: #d6e8f8 !important;
}
.add-dialog .el-button {
  border-radius: 8px;
}
.add-dialog .el-button--default {
  background: rgba(255,255,255,0.06) !important;
  border: 1px solid rgba(255,255,255,0.1) !important;
  color: #a0c8e0 !important;
}
.add-dialog .el-button--default:hover {
  background: rgba(255,255,255,0.1) !important;
  border-color: rgba(255,255,255,0.2) !important;
  color: #d6e8f8 !important;
}
.add-dialog .el-button--primary {
  background: linear-gradient(135deg, #5b9bd5, #2196f3);
  border: none;
  box-shadow: 0 4px 15px rgba(33,150,243,0.25);
}
.add-dialog .el-input-number .el-input__wrapper {
  width: 100%;
}
</style>