<template>
  <div class="comp-root">
    <div class="comp-card">
      <div class="comp-header">
        <div class="header-left">
          <el-icon class="header-icon"><AlarmClock /></el-icon>
          <span class="header-title">智能提醒</span>
          <span class="header-tip">我设置的定时提醒</span>
        </div>
        <el-button class="add-btn" @click="handleAdd"><el-icon><Plus /></el-icon>新建提醒</el-button>
      </div>

      <div class="filter-bar">
        <el-select v-model="filterForm.reminderType" placeholder="全部类型" clearable size="small" class="filter-sel">
          <el-option label="投喂提醒" value="FEEDING" />
          <el-option label="水质监测" value="WATER_QUALITY" />
          <el-option label="库存检查" value="INVENTORY" />
          <el-option label="设备维护" value="MAINTENANCE" />
          <el-option label="自定义" value="CUSTOM" />
        </el-select>
        <el-select v-model="filterForm.isEnabled" placeholder="全部状态" clearable size="small" class="filter-sel">
          <el-option label="已启用" :value="true" />
          <el-option label="已停用" :value="false" />
        </el-select>
        <el-button size="small" class="filter-btn" @click="loadData">查询</el-button>
        <el-button size="small" class="filter-btn-reset" @click="resetFilter">重置</el-button>
      </div>

      <el-table :data="tableData" class="data-table" empty-text="还没有设置提醒，点击右上角新建一个吧" v-loading="loading">
        <el-table-column label="标题" min-width="150" show-overflow-tooltip>
          <template #default="{ row }">
            <span :style="{ textDecoration: !row.isEnabled ? 'line-through' : 'none' }">{{ row.title }}</span>
          </template>
        </el-table-column>
        <el-table-column label="类型" width="90">
          <template #default="{ row }">
            <span class="type-tag" :class="getTypeCls(row.reminderType)">{{ getTypeLabel(row.reminderType) }}</span>
          </template>
        </el-table-column>
        <el-table-column label="内容" min-width="200" show-overflow-tooltip>
          <template #default="{ row }">{{ row.content }}</template>
        </el-table-column>
        <el-table-column label="提醒时间" width="180">
          <template #default="{ row }">
            {{ row.reminderDate }} {{ row.reminderTime }}
          </template>
        </el-table-column>
        <el-table-column label="重复" width="70">
          <template #default="{ row }">{{ getRepeatLabel(row.repeatType) }}</template>
        </el-table-column>
        <el-table-column label="启用" width="60">
          <template #default="{ row }">
            <el-switch v-model="row.isEnabled" size="small" @change="toggleEnable(row)" />
          </template>
        </el-table-column>
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="{ row }">
            <el-button size="small" class="action-btn" @click="handleEdit(row)">编辑</el-button>
            <el-button size="small" class="action-btn-del" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <el-dialog v-model="dialogVisible" :title="isEditing ? '编辑提醒' : '新建提醒'" width="500px" class="add-dialog" :append-to-body="true" :close-on-click-modal="false">
      <el-form :model="form" label-width="80px">
        <el-form-item label="类型">
          <el-select v-model="form.reminderType" style="width:100%">
            <el-option label="投喂提醒" value="FEEDING" />
            <el-option label="水质监测" value="WATER_QUALITY" />
            <el-option label="库存检查" value="INVENTORY" />
            <el-option label="设备维护" value="MAINTENANCE" />
            <el-option label="自定义" value="CUSTOM" />
          </el-select>
        </el-form-item>
        <el-form-item label="标题"><el-input v-model="form.title" placeholder="提醒标题" /></el-form-item>
        <el-form-item label="内容">
          <el-input v-model="form.content" type="textarea" :rows="3" placeholder="提醒详细内容" />
        </el-form-item>
        <el-form-item label="提醒日期">
          <el-date-picker v-model="form.reminderDate" type="date" placeholder="选择日期" style="width:100%" value-format="YYYY-MM-DD" />
        </el-form-item>
        <el-form-item label="提醒时间">
          <el-time-picker v-model="form.reminderTime" placeholder="选择时间" style="width:100%" format="HH:mm:ss" value-format="HH:mm:ss" />
        </el-form-item>
        <el-form-item label="重复方式">
          <el-select v-model="form.repeatType" style="width:100%">
            <el-option label="仅一次" value="ONCE" />
            <el-option label="每天" value="DAILY" />
            <el-option label="每周" value="WEEKLY" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSave" :loading="saving">{{ isEditing ? '保存修改' : '创建提醒' }}</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, AlarmClock } from '@element-plus/icons-vue'
import { getReminders, createReminder, updateReminder, deleteReminder } from '@/api/reminder'

export default {
  name: 'ReminderManage',
  components: { Plus, AlarmClock },
  setup() {
    const tableData = ref([])
    const loading = ref(false)
    const saving = ref(false)
    const dialogVisible = ref(false)
    const isEditing = ref(false)
    const editingId = ref(null)

    const currentUser = JSON.parse(localStorage.getItem('user') || '{}')

    const filterForm = reactive({ reminderType: '', isEnabled: null })
    const form = reactive({
      reminderType: 'FEEDING', title: '', content: '',
      reminderDate: '', reminderTime: '', repeatType: 'ONCE'
    })

    const getTypeCls = (t) => ({
      'FEEDING': 't-feed', 'WATER_QUALITY': 't-water',
      'INVENTORY': 't-inv', 'MAINTENANCE': 't-main', 'CUSTOM': 't-sys'
    })[t] || ''

    const getTypeLabel = (t) => ({
      'FEEDING': '投喂', 'WATER_QUALITY': '水质', 'INVENTORY': '库存', 'MAINTENANCE': '设备', 'CUSTOM': '自定义'
    })[t] || t

    const getRepeatLabel = (t) => ({ 'ONCE': '一次', 'DAILY': '每天', 'WEEKLY': '每周' })[t] || t

    const resetForm = () => {
      form.reminderType = 'FEEDING'; form.title = ''; form.content = '';
      form.reminderDate = ''; form.reminderTime = ''; form.repeatType = 'ONCE';
      isEditing.value = false; editingId.value = null;
    }

    const loadData = async () => {
      loading.value = true
      try {
        const params = { userId: currentUser.id }
        if (filterForm.reminderType) params.reminderType = filterForm.reminderType
        if (filterForm.isEnabled !== null && filterForm.isEnabled !== '') params.isEnabled = filterForm.isEnabled
        const res = await getReminders(params)
        tableData.value = Array.isArray(res.data) ? res.data : []
      } catch (e) { console.error(e); tableData.value = [] }
      finally { loading.value = false }
    }

    const handleAdd = () => { resetForm(); dialogVisible.value = true }

    const handleEdit = (row) => {
      Object.assign(form, {
        reminderType: row.reminderType, title: row.title, content: row.content,
        reminderDate: row.reminderDate, reminderTime: row.reminderTime, repeatType: row.repeatType
      })
      isEditing.value = true; editingId.value = row.id
      dialogVisible.value = true
    }

    const handleSave = async () => {
      if (!form.title || !form.reminderDate) { ElMessage.warning('标题和日期不能为空'); return }
      saving.value = true
      try {
        const data = { ...form, userId: currentUser.id, isEnabled: true }
        if (isEditing.value) {
          data.id = editingId.value
          await updateReminder(data)
          ElMessage.success('修改成功')
        } else {
          await createReminder(data)
          ElMessage.success('提醒已创建，届时将在页面弹出提示')
        }
        dialogVisible.value = false
        loadData()
      } catch (e) { ElMessage.error('操作失败') }
      finally { saving.value = false }
    }

    const toggleEnable = async (row) => {
      try {
        await updateReminder({ id: row.id, isEnabled: row.isEnabled, updateTime: new Date().toISOString() })
      } catch (e) { ElMessage.error('操作失败'); loadData() }
    }

    const handleDelete = async (row) => {
      try { await ElMessageBox.confirm('确定删除？', '提示', { type: 'warning' }); await deleteReminder(row.id); ElMessage.success('已删除'); loadData() }
      catch { /* canceled */ }
    }

    const resetFilter = () => { filterForm.reminderType = ''; filterForm.isEnabled = null; loadData() }

    onMounted(() => { loadData() })

    return {
      tableData, loading, saving, dialogVisible, isEditing, filterForm, form,
      getTypeCls, getTypeLabel, getRepeatLabel,
      loadData, handleAdd, handleEdit, handleSave, toggleEnable, handleDelete, resetFilter
    }
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
.header-tip { font-size: 12px; color: rgba(255,255,255,0.4); }
.add-btn { background: rgba(33,150,243,0.15) !important; border: 1px solid rgba(33,150,243,0.25) !important; color: #64b5f6 !important; border-radius: 10px; }
.add-btn:hover { background: rgba(33,150,243,0.25) !important; }

.filter-bar { display: flex; gap: 10px; margin-bottom: 16px; align-items: center; }
.filter-sel { width: 130px; }
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

.type-tag { padding: 2px 8px; border-radius: 6px; font-size: 12px; }
.t-feed { background: rgba(76,175,80,0.12); color: #81c784; }
.t-water { background: rgba(0,212,255,0.12); color: #00d4ff; }
.t-inv { background: rgba(255,204,0,0.12); color: #ffcc00; }
.t-main { background: rgba(156,39,176,0.12); color: #ce93d8; }
.t-sys { background: rgba(80,150,220,0.12); color: #8cb8e0; }

.action-btn { background: rgba(80,150,220,0.1) !important; border: 1px solid rgba(80,150,220,0.15) !important; color: #8cb8e0 !important; border-radius: 6px; }
.action-btn:hover { background: rgba(80,150,220,0.2) !important; }
.action-btn-del { background: rgba(255,68,68,0.1) !important; border: 1px solid rgba(255,68,68,0.15) !important; color: #ff6b6b !important; border-radius: 6px; }
.action-btn-del:hover { background: rgba(255,68,68,0.2) !important; }
</style>

<style>
.add-dialog { --el-dialog-bg-color: #0f1c2e; }
.add-dialog .el-dialog { background: #0f1c2e; border: 1px solid rgba(80,150,220,0.15); border-radius: 16px; }
.add-dialog .el-dialog__title { color: #d6e8f8; }
.add-dialog .el-form-item__label { color: #a0c8e8 !important; }
.add-dialog .el-input__wrapper { background: rgba(255,255,255,0.06) !important; box-shadow: none !important; border: 1px solid rgba(80,150,220,0.15); border-radius: 8px; }
.add-dialog .el-input__inner { color: #d6e8f8 !important; }
.add-dialog .el-textarea__inner { background: rgba(255,255,255,0.06) !important; border: 1px solid rgba(80,150,220,0.15); border-radius: 8px; color: #d6e8f8 !important; }
.add-dialog .el-select .el-input__wrapper { background: rgba(255,255,255,0.06) !important; }
.add-dialog .el-button--primary { background: linear-gradient(135deg, #5b9bd5, #2196f3); border: none; }
</style>