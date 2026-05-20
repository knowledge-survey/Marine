<template>
  <div class="comp-root">
    <div class="comp-card">
      <div class="comp-header">
        <div class="header-left">
          <el-icon class="header-icon"><Bell /></el-icon>
          <span class="header-title">通知告警</span>
          <span class="header-badge" v-if="unreadCount > 0">{{ unreadCount }}条未读</span>
        </div>
        <div style="display: flex; gap: 10px;">
          <el-button v-if="unreadCount > 0" size="small" class="action-btn" @click="handleMarkAllRead">全部已读</el-button>
          <el-button v-if="isAdmin" class="add-btn" @click="handleAdd"><el-icon><Plus /></el-icon>发布通知</el-button>
        </div>
      </div>

      <div class="filter-bar">
        <el-select v-model="filterForm.type" placeholder="全部类型" clearable size="small" class="filter-sel">
          <el-option label="水质告警" value="WARNING" />
          <el-option label="信息通知" value="INFO" />
          <el-option label="提醒" value="REMINDER" />
        </el-select>
        <el-select v-model="filterForm.status" placeholder="全部状态" clearable size="small" class="filter-sel">
          <el-option label="未读" value="UNREAD" />
          <el-option label="已读" value="READ" />
        </el-select>
        <el-button size="small" class="filter-btn" @click="loadData">查询</el-button>
        <el-button size="small" class="filter-btn-reset" @click="resetFilter">重置</el-button>
      </div>

      <el-table :data="tableData" class="data-table" empty-text="暂无通知" v-loading="loading">
        <el-table-column label="" width="40">
          <template #default="{ row }">
            <el-icon v-if="!row.isRead" color="#ff8a65" size="16"><WarningFilled /></el-icon>
          </template>
        </el-table-column>
        <el-table-column label="标题" min-width="180" show-overflow-tooltip>
          <template #default="{ row }">
            <span @click="handleRead(row)" :class="{unread: !row.isRead}" style="cursor:pointer">{{ row.title }}</span>
          </template>
        </el-table-column>
        <el-table-column label="类型" width="90">
          <template #default="{ row }">
            <span class="type-tag" :class="getTypeCls(row.type)">{{ getTypeLabel(row.type) }}</span>
          </template>
        </el-table-column>
        <el-table-column label="内容" min-width="220" show-overflow-tooltip>
          <template #default="{ row }">{{ row.content }}</template>
        </el-table-column>
        <el-table-column label="发布人" width="100">
          <template #default="{ row }">{{ row.senderName || '系统' }}</template>
        </el-table-column>
        <el-table-column label="发送对象" width="100">
          <template #default="{ row }">{{ getTargetLabel(row) }}</template>
        </el-table-column>
        <el-table-column label="时间" width="150">
          <template #default="{ row }">{{ row.createTime }}</template>
        </el-table-column>
        <el-table-column label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="row.isRead ? 'info' : 'danger'" size="small">{{ row.isRead ? '已读' : '未读' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="160" fixed="right">
          <template #default="{ row }">
            <el-button size="small" class="action-btn" @click="handleRead(row)" v-if="!row.isRead">标为已读</el-button>
            <el-button size="small" class="action-btn-del" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <el-dialog v-model="dialogVisible" title="发布通知" width="520px" class="add-dialog" :append-to-body="true" :close-on-click-modal="false">
      <el-form :model="form" label-width="80px">
        <el-form-item label="标题"><el-input v-model="form.title" placeholder="请输入通知标题" /></el-form-item>
        <el-form-item label="类型">
          <el-select v-model="form.type" style="width:100%">
            <el-option label="信息通知" value="INFO" />
            <el-option label="水质告警" value="WARNING" />
            <el-option label="提醒" value="REMINDER" />
          </el-select>
        </el-form-item>
        <el-form-item label="优先级">
          <el-select v-model="form.priority" style="width:100%">
            <el-option label="高" value="HIGH" />
            <el-option label="中" value="MEDIUM" />
            <el-option label="低" value="LOW" />
          </el-select>
        </el-form-item>
        <el-form-item label="发送对象">
          <el-select v-model="form.targetType" style="width:100%" @change="onTargetChange">
            <el-option label="全部人员" value="ALL" />
            <el-option label="指定角色" value="ROLE" />
            <el-option label="指定人员" value="USER" />
          </el-select>
        </el-form-item>
        <el-form-item label="目标角色" v-if="form.targetType === 'ROLE'">
          <el-select v-model="form.targetRole" style="width:100%">
            <el-option label="超级管理员" value="SUPER_ADMIN" />
            <el-option label="运维人员" value="OPERATOR" />
            <el-option label="管理人员" value="MANAGER" />
            <el-option label="养殖人员" value="BREEDER" />
          </el-select>
        </el-form-item>
        <el-form-item label="指定人员" v-if="form.targetType === 'USER'">
          <el-select v-model="form.targetUserId" style="width:100%" filterable placeholder="搜索选择人员">
            <el-option v-for="u in userOptions" :key="u.id" :label="u.realName" :value="u.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="内容">
          <el-input v-model="form.content" type="textarea" :rows="4" placeholder="请输入通知内容" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSave" :loading="saving">发布通知</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import { ref, reactive, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Bell, WarningFilled } from '@element-plus/icons-vue'
import { listNotification, saveNotification, deleteNotification, markAsRead, markAllAsRead, getUnreadCount } from '@/api/notification'
import { getUsers } from '@/api/user'

export default {
  name: 'NotificationManage',
  components: { Plus, Bell, WarningFilled },
  setup() {
    const tableData = ref([])
    const loading = ref(false)
    const saving = ref(false)
    const dialogVisible = ref(false)
    const unreadCount = ref(0)
    const userOptions = ref([])

    const currentUser = JSON.parse(localStorage.getItem('user') || '{}')
    const isAdmin = computed(() => ['SUPER_ADMIN', 'MANAGER'].includes(currentUser.role))

    const filterForm = reactive({ type: '', status: '' })
    const form = reactive({ title: '', type: 'INFO', priority: 'MEDIUM', content: '', targetType: 'ALL', targetRole: '', targetUserId: null })

    const getTypeCls = (t) => ({ 'WARNING': 't-water', 'INFO': 't-sys', 'REMINDER': 't-dis' })[t] || ''
    const getTypeLabel = (t) => ({ 'WARNING': '告警', 'INFO': '通知', 'REMINDER': '提醒' })[t] || t

    const getTargetLabel = (row) => {
      if (row.targetType === 'ALL') return '全员'
      if (row.targetType === 'ROLE') {
        const map = { SUPER_ADMIN: '超级管理员', OPERATOR: '运维', MANAGER: '管理', BREEDER: '养殖' }
        return map[row.targetRole] || row.targetRole || '角色'
      }
      if (row.targetType === 'USER') return '个人'
      return '全员'
    }

    const resetForm = () => {
      form.title = ''; form.type = 'INFO'; form.priority = 'MEDIUM'; form.content = ''
      form.targetType = 'ALL'; form.targetRole = ''; form.targetUserId = null
    }

    const onTargetChange = () => {
      form.targetRole = ''; form.targetUserId = null
    }

    const loadUsers = async () => {
      try {
        const res = await getUsers()
        userOptions.value = Array.isArray(res.data) ? res.data : []
      } catch (e) { console.error(e) }
    }

    const loadData = async () => {
      loading.value = true
      try {
        const params = { userId: currentUser.id }
        if (filterForm.type) params.type = filterForm.type
        if (filterForm.status) params.status = filterForm.status
        const res = await listNotification(params)
        if (res.data?.records) {
          tableData.value = res.data.records
        } else {
          tableData.value = Array.isArray(res.data) ? res.data : []
        }
      } catch (e) { console.error(e); tableData.value = [] }
      finally { loading.value = false }
    }

    const loadUnreadCount = async () => {
      try {
        const res = await getUnreadCount({ userId: currentUser.id })
        unreadCount.value = res.data?.count || 0
      } catch (e) { /* ignore */ }
    }

    const handleAdd = () => { resetForm(); loadUsers(); dialogVisible.value = true }

    const handleSave = async () => {
      if (!form.title || !form.content) { ElMessage.warning('标题和内容不能为空'); return }
      saving.value = true
      try {
        await saveNotification({
          ...form,
          senderId: currentUser.id,
          senderName: currentUser.realName || currentUser.username
        })
        dialogVisible.value = false
        ElMessage.success('通知发布成功')
        loadData(); loadUnreadCount()
      } catch (e) { ElMessage.error('发布失败') }
      finally { saving.value = false }
    }

    const handleRead = async (row) => {
      if (row.isRead) return
      try { 
        await markAsRead(row.id, currentUser.id); 
        loadData(); 
        loadUnreadCount() 
      }
      catch (e) { ElMessage.error('操作失败') }
    }

    const handleMarkAllRead = async () => {
      try { 
        await markAllAsRead({ userId: currentUser.id }); 
        ElMessage.success('全部已读');
        loadData(); 
        loadUnreadCount();
      }
      catch (e) { ElMessage.error('操作失败') }
    }

    const handleDelete = async (row) => {
      try { await ElMessageBox.confirm('确定删除？', '提示', { type: 'warning' }); await deleteNotification(row.id); ElMessage.success('已删除'); loadData(); loadUnreadCount() }
      catch { /* canceled */ }
    }

    const resetFilter = () => { filterForm.type = ''; filterForm.status = ''; loadData() }

    onMounted(() => { loadData(); loadUnreadCount() })

    return {
      tableData, loading, saving, dialogVisible, unreadCount, userOptions, isAdmin,
      filterForm, form,
      getTypeCls, getTypeLabel, getTargetLabel,
      loadData, handleAdd, handleSave, handleRead, handleMarkAllRead, handleDelete, resetFilter, onTargetChange
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
.header-badge { font-size: 12px; color: #ff8a65; background: rgba(255,107,53,0.12); padding: 2px 10px; border-radius: 10px; }
.add-btn { background: rgba(33,150,243,0.15) !important; border: 1px solid rgba(33,150,243,0.25) !important; color: #64b5f6 !important; border-radius: 10px; }
.add-btn:hover { background: rgba(33,150,243,0.25) !important; }

.filter-bar { display: flex; gap: 10px; margin-bottom: 16px; align-items: center; }
.filter-sel { width: 140px; }
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
.data-table td span.unread { font-weight: 700; color: #ff8a65 !important; }

.type-tag { padding: 2px 8px; border-radius: 6px; font-size: 12px; }
.t-water { background: rgba(0,212,255,0.12); color: #00d4ff; }
.t-inv { background: rgba(255,204,0,0.12); color: #ffcc00; }
.t-dis { background: rgba(255,107,53,0.12); color: #ff8a65; }
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