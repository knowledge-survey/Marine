<template>
  <div class="comp-root">
    <div class="comp-card">
      <div class="comp-header">
        <div class="header-left">
          <el-icon class="header-icon"><Calendar /></el-icon>
          <span class="header-title">生产计划</span>
        </div>
        <el-button class="add-btn" @click="handleAdd"><el-icon><Plus /></el-icon>新增计划</el-button>
      </div>

      <el-row :gutter="16" class="stats-row">
        <el-col :span="6" v-for="s in statsItems" :key="s.key">
          <div class="stat-box" :class="s.cls">
            <div class="stat-inner">
              <el-icon class="stat-icon" :size="28"><component :is="s.icon" /></el-icon>
              <div class="stat-body">
                <span class="stat-val">{{ stats[s.key] }}</span>
                <span class="stat-lbl">{{ s.label }}</span>
              </div>
            </div>
          </div>
        </el-col>
      </el-row>

      <div class="filter-bar">
        <el-select v-model="filterForm.status" placeholder="全部状态" clearable size="small" class="filter-sel">
          <el-option label="待开始" value="待开始" /><el-option label="进行中" value="进行中" /><el-option label="已完成" value="已完成" /><el-option label="已取消" value="已取消" />
        </el-select>
        <el-select v-model="filterForm.zoneId" placeholder="全部区域" clearable size="small" class="filter-sel">
          <el-option v-for="z in zones" :key="z.id" :label="z.name" :value="z.id" />
        </el-select>
        <el-button size="small" class="filter-btn" @click="loadData">查询</el-button>
        <el-button size="small" class="filter-btn-reset" @click="resetFilter">重置</el-button>
      </div>

      <el-table :data="tableData" class="data-table" empty-text="暂无数据" v-loading="loading">
        <el-table-column prop="planName" label="计划名称" min-width="160" />
        <el-table-column prop="species" label="养殖品种" min-width="110" />
        <el-table-column prop="zoneName" label="区域" min-width="140" />
        <el-table-column prop="batchNo" label="批次号" min-width="130" />
        <el-table-column label="目标产量" min-width="110">
          <template #default="{ row }">{{ row.targetQuantity }} {{ row.unit }}</template>
        </el-table-column>
        <el-table-column prop="targetSize" label="目标规格" min-width="110" />
        <el-table-column prop="startDate" label="开始日期" min-width="110" />
        <el-table-column prop="endDate" label="结束日期" min-width="110" />
        <el-table-column label="状态" min-width="100">
          <template #default="{ row }">
            <el-tag :class="'plan-status ' + getStatusCls(row.status)" size="small">{{ row.status }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="description" label="备注" min-width="160" show-overflow-tooltip />
        <el-table-column label="操作" min-width="220" fixed="right">
          <template #default="{ row }">
            <el-button size="small" class="action-btn" @click="handleEdit(row)">编辑</el-button>
            <el-button v-if="row.status==='进行中'||row.status==='待开始'" size="small" class="action-btn-done" @click="handleComplete(row)">完成</el-button>
            <el-button v-if="row.status==='进行中'||row.status==='待开始'" size="small" class="action-btn-cancel" @click="handleCancel(row)">取消</el-button>
            <el-button size="small" class="action-btn-del" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <el-dialog v-model="dialogVisible" :title="editingPlan ? '编辑计划' : '新增计划'" width="620px" class="add-dialog" :append-to-body="true" :close-on-click-modal="false">
      <el-form :model="form" label-width="90px">
        <el-form-item label="计划名称"><el-input v-model="form.planName" /></el-form-item>
        <el-form-item label="养殖品种">
          <el-select v-model="form.species" style="width:100%">
            <el-option v-for="s in speciesOptions" :key="s" :label="s" :value="s" />
          </el-select>
        </el-form-item>
        <el-form-item label="所属区域">
          <el-select v-model="form.zoneId" style="width:100%">
            <el-option v-for="z in zones" :key="z.id" :label="z.name" :value="z.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="批次号"><el-input v-model="form.batchNo" /></el-form-item>
        <el-form-item label="目标产量">
          <el-input-number v-model="form.targetQuantity" :min="0" style="width:160px" />
          <el-select v-model="form.unit" style="width:100px;margin-left:8px">
            <el-option label="斤" value="斤" /><el-option label="尾" value="尾" /><el-option label="只" value="只" />
          </el-select>
        </el-form-item>
        <el-form-item label="目标规格"><el-input v-model="form.targetSize" /></el-form-item>
        <el-form-item label="开始日期"><el-date-picker v-model="form.startDate" type="date" style="width:100%" value-format="YYYY-MM-DD" /></el-form-item>
        <el-form-item label="结束日期"><el-date-picker v-model="form.endDate" type="date" style="width:100%" value-format="YYYY-MM-DD" /></el-form-item>
        <el-form-item label="计划状态">
          <el-select v-model="form.status" style="width:100%">
            <el-option label="待开始" value="待开始" /><el-option label="进行中" value="进行中" /><el-option label="已完成" value="已完成" /><el-option label="已取消" value="已取消" />
          </el-select>
        </el-form-item>
        <el-form-item label="备注"><el-input v-model="form.description" type="textarea" :rows="2" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSave" :loading="saving">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import { ref, computed, onMounted, reactive } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Calendar, Document, Clock, CircleCheck, CircleClose } from '@element-plus/icons-vue'
import { getProductionPlans, createProductionPlan, updateProductionPlan, deleteProductionPlan } from '@/api/production'
import { getZones } from '@/api/zone'

export default {
  name: 'ProductionManage',
  components: { Plus, Calendar, Document, Clock, CircleCheck, CircleClose },
  setup() {
    const tableData = ref([])
    const zones = ref([])
    const loading = ref(false)
    const saving = ref(false)
    const dialogVisible = ref(false)
    const editingPlan = ref(null)
    const filterForm = reactive({ status: '', zoneId: null })
    const form = reactive({
      planName: '', species: '', zoneId: null, batchNo: '',
      targetQuantity: 0, unit: '斤', targetSize: '',
      startDate: '', endDate: '', status: '待开始', description: ''
    })
    const speciesOptions = ['南美白对虾', '石斑鱼', '海鲈鱼', '鲍鱼', '大闸蟹']

    const stats = computed(() => {
      const data = tableData.value || []
      return {
        total: data.length,
        pending: data.filter(i => i.status === '进行中').length,
        done: data.filter(i => i.status === '已完成').length,
        cancelled: data.filter(i => i.status === '已取消').length
      }
    })
    const statsItems = [
      { key: 'total', label: '总计划数', icon: 'Document', cls: 's-total' },
      { key: 'pending', label: '进行中', icon: 'Clock', cls: 's-pending' },
      { key: 'done', label: '已完成', icon: 'CircleCheck', cls: 's-done' },
      { key: 'cancelled', label: '已取消', icon: 'CircleClose', cls: 's-cancel' }
    ]
    const getStatusCls = (status) => ({ '待开始': 's-pending', '进行中': 's-active', '已完成': 's-done', '已取消': 's-cancel' }[status] || '')
    const resetForm = () => {
      form.planName = ''; form.species = ''; form.zoneId = null; form.batchNo = ''
      form.targetQuantity = 0; form.unit = '斤'; form.targetSize = ''
      form.startDate = ''; form.endDate = ''; form.status = '待开始'; form.description = ''
    }

    const loadData = async () => {
      loading.value = true
      try {
        const params = {}
        if (filterForm.status) params.status = filterForm.status
        if (filterForm.zoneId) params.zoneId = filterForm.zoneId
        const res = await getProductionPlans(params)
        tableData.value = Array.isArray(res.data) ? res.data : []
      } catch (e) { console.error(e); tableData.value = [] }
      finally { loading.value = false }
    }

    const loadZones = async () => {
      try { const res = await getZones(); zones.value = Array.isArray(res.data) ? res.data : [] }
      catch { /* ignore */ }
    }

    const handleAdd = () => { editingPlan.value = null; resetForm(); dialogVisible.value = true }
    const handleEdit = (row) => { editingPlan.value = row; Object.assign(form, row);
      form.zoneId = row.zoneId || (row.zone ? row.zone.id : null); dialogVisible.value = true }

    const handleSave = async () => {
      saving.value = true
      try {
        const data = { ...form }
        const zone = zones.value.find(z => z.id === form.zoneId)
        if (zone) data.zoneName = zone.name
        if (editingPlan.value) { await updateProductionPlan(editingPlan.value.id, data); ElMessage.success('编辑成功') }
        else { await createProductionPlan(data); ElMessage.success('添加成功') }
        dialogVisible.value = false; loadData()
      } catch (e) { ElMessage.error('操作失败') }
      finally { saving.value = false }
    }

    const handleComplete = async (row) => {
      try { await ElMessageBox.confirm('确定标记为已完成？', '提示', { type: 'success' }); await updateProductionPlan(row.id, { ...row, status: '已完成' }); ElMessage.success('已标记'); loadData() }
      catch { /* ignore */ }
    }

    const handleCancel = async (row) => {
      try { await ElMessageBox.confirm('确定取消此计划？', '提示', { type: 'warning' }); await updateProductionPlan(row.id, { ...row, status: '已取消' }); ElMessage.success('已取消'); loadData() }
      catch { /* ignore */ }
    }

    const handleDelete = async (row) => {
      try { await ElMessageBox.confirm('确定删除？', '提示', { type: 'warning' }); await deleteProductionPlan(row.id); ElMessage.success('删除成功'); loadData() }
      catch { /* ignore */ }
    }

    const resetFilter = () => { filterForm.status = ''; filterForm.zoneId = null; loadData() }
    onMounted(() => { loadZones(); loadData() })
    return { tableData, zones, loading, saving, dialogVisible, editingPlan, filterForm, form,
      speciesOptions, stats, statsItems, getStatusCls,
      loadData, handleAdd, handleEdit, handleSave, handleComplete, handleCancel, handleDelete, resetFilter }
  }
}
</script>

<style scoped>
.comp-root { padding: 0; }
.comp-card { background: rgba(12,22,40,0.5); border: 1px solid rgba(80,150,220,0.08); border-radius: 16px; padding: 20px; }
.comp-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 20px; }
.header-left { display: flex; align-items: center; gap: 10px; }
.header-icon { font-size: 22px; color: #5b9bd5; }
.header-title { font-size: 17px; font-weight: 700; color: #d6e8f8; }
.add-btn { background: rgba(33,150,243,0.15) !important; border: 1px solid rgba(33,150,243,0.25) !important; color: #64b5f6 !important; border-radius: 10px; }
.add-btn:hover { background: rgba(33,150,243,0.25) !important; }

.stats-row { margin-bottom: 16px; }
.stat-box { background: rgba(255,255,255,0.03); border: 1px solid rgba(80,150,220,0.08); border-radius: 14px; padding: 14px 16px; }
.stat-box.s-total { border-left: 3px solid #5b9bd5; }
.stat-box.s-pending { border-left: 3px solid #ffcc00; }
.stat-box.s-done { border-left: 3px solid #00ff88; }
.stat-box.s-cancel { border-left: 3px solid #ff6b35; }
.stat-inner { display: flex; align-items: center; gap: 14px; }
.s-total .stat-icon { color: #5b9bd5; }
.s-pending .stat-icon { color: #ffcc00; }
.s-done .stat-icon { color: #00ff88; }
.s-cancel .stat-icon { color: #ff6b35; }
.stat-body { display: flex; flex-direction: column; gap: 2px; }
.stat-val { font-size: 22px; font-weight: 700; color: #e8f2fc; }
.stat-lbl { font-size: 12px; color: rgba(255,255,255,0.4); }

.filter-bar { display: flex; gap: 10px; margin-bottom: 16px; align-items: center; }
.filter-sel { width: 150px; }
.filter-btn { background: rgba(33,150,243,0.12) !important; border: 1px solid rgba(33,150,243,0.2) !important; color: #64b5f6 !important; border-radius: 8px; }
.filter-btn-reset { background: rgba(255,255,255,0.04) !important; border: 1px solid rgba(255,255,255,0.08) !important; color: rgba(255,255,255,0.4) !important; border-radius: 8px; }

.data-table { border-radius: 10px; overflow: hidden; }
.data-table :deep(.el-table) { background: #0c1628 !important; }
.data-table :deep(.el-table::before) { display: none; }
.data-table :deep(.el-table__header th) { background: #0f1f35 !important; color: #8cb8e0 !important; font-weight: 600; font-size: 13px; border-bottom: 1px solid rgba(80,150,220,0.15) !important; }
.data-table :deep(.el-table__cell) { background: #0c1628 !important; color: #d6e8f8 !important; border-color: rgba(80,150,220,0.08) !important; font-size: 14px; }
.data-table :deep(.el-table__row:hover > td) { background: #132742 !important; }
.data-table :deep(.el-table__empty-block) { background: #0c1628 !important; }
.data-table :deep(.el-table__empty-text) { color: rgba(255,255,255,0.4); }

.action-btn { background: rgba(80,150,220,0.1) !important; border: 1px solid rgba(80,150,220,0.15) !important; color: #8cb8e0 !important; border-radius: 6px; }
.action-btn:hover { background: rgba(80,150,220,0.2) !important; }
.action-btn-done { background: rgba(0,255,136,0.1) !important; border: 1px solid rgba(0,255,136,0.15) !important; color: #00ff88 !important; border-radius: 6px; }
.action-btn-done:hover { background: rgba(0,255,136,0.2) !important; }
.action-btn-cancel { background: rgba(255,107,53,0.1) !important; border: 1px solid rgba(255,107,53,0.15) !important; color: #ff8a65 !important; border-radius: 6px; }
.action-btn-cancel:hover { background: rgba(255,107,53,0.2) !important; }
.action-btn-del { background: rgba(255,68,68,0.1) !important; border: 1px solid rgba(255,68,68,0.15) !important; color: #ff6b6b !important; border-radius: 6px; }
.action-btn-del:hover { background: rgba(255,68,68,0.2) !important; }

.plan-status { border-radius: 10px; padding: 2px 10px; font-weight: 500; border: none; }
.plan-status.s-pending { background: rgba(255,204,0,0.12); color: #ffcc00; }
.plan-status.s-active { background: rgba(0,212,255,0.12); color: #00d4ff; }
.plan-status.s-done { background: rgba(0,255,136,0.1); color: #00ff88; }
.plan-status.s-cancel { background: rgba(255,107,53,0.12); color: #ff8a65; }
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
  background: rgba(255, 255, 255, 0.06) !important;
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
  background: rgba(255, 255, 255, 0.03) !important;
  opacity: 0.6;
}
.add-dialog .el-input-number .el-input__wrapper {
  width: 100%;
}
.add-dialog .el-select .el-input__wrapper {
  background: rgba(255,255,255,0.06) !important;
  box-shadow: none !important;
  border: 1px solid rgba(80,150,220,0.15);
}
.add-dialog .el-select .el-input__inner {
  color: #d6e8f8 !important;
}
.add-dialog .el-textarea__inner {
  background: rgba(255,255,255,0.06) !important;
  border: 1px solid rgba(80,150,220,0.15);
  color: #d6e8f8 !important;
  border-radius: 8px;
}
.add-dialog .el-textarea__inner:focus {
  border-color: #5b9bd5 !important;
  box-shadow: 0 0 0 2px rgba(91,155,213,0.15) !important;
}
.add-dialog .el-textarea__inner::placeholder {
  color: rgba(255,255,255,0.3) !important;
}
.add-dialog .el-date-editor .el-input__wrapper {
  background: rgba(255,255,255,0.06) !important;
  box-shadow: none !important;
  border: 1px solid rgba(80,150,220,0.15);
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
</style>