<template>
  <div class="comp-root">
    <div class="comp-card">
      <div class="comp-header">
        <div class="header-left">
          <el-icon class="header-icon"><Download /></el-icon>
          <span class="header-title">数据备份</span>
        </div>
        <el-button class="backup-btn" @click="handleBackup" :loading="backing">立即备份</el-button>
      </div>

      <el-table :data="tableData" class="data-table" empty-text="暂无备份记录" v-loading="loading">
        <el-table-column prop="backupName" label="备份名称" min-width="200" />
        <el-table-column prop="backupSize" label="大小" min-width="100">
          <template #default="{ row }">{{ row.backupSize || '-' }}</template>
        </el-table-column>
        <el-table-column prop="createTime" label="备份时间" min-width="180" />
        <el-table-column label="状态" min-width="100">
          <template #default><el-tag type="success" size="small">正常</el-tag></template>
        </el-table-column>
        <el-table-column label="操作" min-width="160" fixed="right">
          <template #default="{ row }">
            <el-button size="small" class="action-btn-green" @click="handleRestore(row)">恢复</el-button>
            <el-button size="small" class="action-btn-del" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>
  </div>
</template>

<script>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Download } from '@element-plus/icons-vue'
import { getBackups, createBackup, deleteBackup, restoreBackup } from '@/api/backup'

export default {
  name: 'BackupManage',
  components: { Download },
  setup() {
    const tableData = ref([])
    const loading = ref(false)
    const backing = ref(false)

    const loadData = async () => {
      loading.value = true
      try { const res = await getBackups(); tableData.value = Array.isArray(res.data.backups) ? res.data.backups : [] }
      catch (e) { console.error(e); tableData.value = [] }
      finally { loading.value = false }
    }

    const handleBackup = async () => {
      backing.value = true
      try {
        const res = await createBackup()
        ElMessage.success(res.data ? (res.data.message || '备份成功') : '备份成功')
        loadData()
      } catch (e) { ElMessage.error('备份失败') }
      finally { backing.value = false }
    }

    const handleRestore = async (row) => {
      try {
        await ElMessageBox.confirm('恢复备份将覆盖当前数据，确定继续？', '警告', { type: 'warning' })
        await restoreBackup(row.backupName)
        ElMessage.success('恢复成功')
      } catch { /* ignore */ }
    }

    const handleDelete = async (row) => {
      try {
        await ElMessageBox.confirm('确定删除？', '提示', { type: 'warning' })
        await deleteBackup(row.backupName)
        ElMessage.success('已删除')
        loadData()
      } catch { /* ignore */ }
    }

    onMounted(() => { loadData() })
    return { tableData, loading, backing, loadData, handleBackup, handleRestore, handleDelete }
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
.backup-btn { background: rgba(0,255,136,0.12) !important; border: 1px solid rgba(0,255,136,0.25) !important; color: #00ff88 !important; border-radius: 10px; }
.backup-btn:hover { background: rgba(0,255,136,0.22) !important; }

.data-table { border-radius: 10px; overflow: hidden; }
.data-table :deep(.el-table) { background: #0c1628 !important; }
.data-table :deep(.el-table::before) { display: none; }
.data-table :deep(.el-table__header th) { background: #0f1f35 !important; color: #8cb8e0 !important; font-weight: 600; font-size: 13px; border-bottom: 1px solid rgba(80,150,220,0.15) !important; }
.data-table :deep(.el-table__cell) { background: #0c1628 !important; color: #d6e8f8 !important; border-color: rgba(80,150,220,0.08) !important; font-size: 14px; }
.data-table :deep(.el-table__row:hover > td) { background: #132742 !important; }
.data-table :deep(.el-table__empty-block) { background: #0c1628 !important; }
.data-table :deep(.el-table__empty-text) { color: rgba(255,255,255,0.4); }

.action-btn-green { background: rgba(0,255,136,0.1) !important; border: 1px solid rgba(0,255,136,0.15) !important; color: #00ff88 !important; border-radius: 6px; }
.action-btn-green:hover { background: rgba(0,255,136,0.2) !important; }
.action-btn-del { background: rgba(255,68,68,0.1) !important; border: 1px solid rgba(255,68,68,0.15) !important; color: #ff6b6b !important; border-radius: 6px; }
.action-btn-del:hover { background: rgba(255,68,68,0.2) !important; }
</style>