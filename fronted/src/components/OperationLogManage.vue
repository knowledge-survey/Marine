<template>
  <div class="comp-root">
    <div class="comp-card">
      <div class="comp-header">
        <div class="header-left">
          <el-icon class="header-icon"><List /></el-icon>
          <span class="header-title">操作日志</span>
        </div>
        <el-button class="clear-btn" @click="handleClear">清空日志</el-button>
      </div>

      <div class="filter-bar">
        <el-input v-model="filterKeyword" placeholder="搜索操作/用户" clearable size="small" class="filter-input" />
        <el-select v-model="filterType" placeholder="全部操作类型" clearable size="small" class="filter-sel">
          <el-option v-for="t in types" :key="t" :label="t" :value="t" />
        </el-select>
        <el-date-picker v-model="filterDate" type="date" placeholder="日期" size="small" value-format="YYYY-MM-DD" class="filter-date" />
        <el-button size="small" class="filter-btn" @click="loadData">查询</el-button>
        <el-button size="small" class="filter-btn-reset" @click="resetFilter">重置</el-button>
      </div>

      <el-table :data="tableData" class="data-table" empty-text="暂无数据" v-loading="loading">
        <el-table-column prop="operator" label="操作用户" min-width="130" />
        <el-table-column prop="operationType" label="操作类型" min-width="130">
          <template #default="{ row }"><span class="op-tag">{{ row.operationType }}</span></template>
        </el-table-column>
        <el-table-column prop="description" label="操作详情" min-width="400" show-overflow-tooltip />
        <el-table-column prop="ipAddress" label="IP地址" min-width="140" />
        <el-table-column prop="createTime" label="时间" min-width="160" />
        <el-table-column label="操作" min-width="80" fixed="right">
          <template #default="{ row }">
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
import { List } from '@element-plus/icons-vue'
import { getOperationLogs, deleteOperationLog, clearOperationLogs } from '@/api/operationLog'

export default {
  name: 'OperationLogManage',
  components: { List },
  setup() {
    const tableData = ref([])
    const loading = ref(false)
    const filterKeyword = ref('')
    const filterType = ref('')
    const filterDate = ref('')
    const types = ['登录', '新增', '编辑', '删除', '导出', '备份', '系统']

    const loadData = async () => {
      loading.value = true
      try {
        const params = {}
        if (filterType.value) params.operationType = filterType.value
        if (filterKeyword.value) params.keyword = filterKeyword.value
        if (filterDate.value) params.date = filterDate.value
        const res = await getOperationLogs(params)
        tableData.value = Array.isArray(res.data) ? res.data : []
      } catch (e) { console.error(e); tableData.value = [] }
      finally { loading.value = false }
    }

    const handleDelete = async (row) => {
      try {
        await ElMessageBox.confirm('确定删除？', '提示', { type: 'warning' })
        await deleteOperationLog(row.id)
        ElMessage.success('已删除')
        loadData()
      } catch { /* ignore */ }
    }

    const handleClear = async () => {
      try {
        await ElMessageBox.confirm('确定清空所有日志？', '警告', { type: 'error' })
        await clearOperationLogs()
        ElMessage.success('已清空')
        loadData()
      } catch { /* ignore */ }
    }

    const resetFilter = () => { filterKeyword.value = ''; filterType.value = ''; filterDate.value = ''; loadData() }

    onMounted(() => { loadData() })
    return { tableData, loading, filterKeyword, filterType, filterDate, types,
      loadData, handleDelete, handleClear, resetFilter }
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
.clear-btn { background: rgba(255,68,68,0.1) !important; border: 1px solid rgba(255,68,68,0.2) !important; color: #ff6b6b !important; border-radius: 10px; }
.clear-btn:hover { background: rgba(255,68,68,0.2) !important; }

.filter-bar { display: flex; gap: 10px; margin-bottom: 16px; align-items: center; }
.filter-input { width: 180px; }
.filter-sel { width: 140px; }
.filter-date { width: 150px; }
.filter-btn { background: rgba(33,150,243,0.12) !important; border: 1px solid rgba(33,150,243,0.2) !important; color: #64b5f6 !important; border-radius: 8px; }
.filter-btn:hover { background: rgba(33,150,243,0.22) !important; }
.filter-btn-reset { background: rgba(255,255,255,0.04) !important; border: 1px solid rgba(255,255,255,0.08) !important; color: rgba(255,255,255,0.4) !important; border-radius: 8px; }

.data-table { border-radius: 10px; overflow: hidden; }
.data-table :deep(.el-table) { background: #0c1628 !important; }
.data-table :deep(.el-table::before) { display: none; }
.data-table :deep(.el-table__header th) { background: #0f1f35 !important; color: #8cb8e0 !important; font-weight: 600; font-size: 13px; border-bottom: 1px solid rgba(80,150,220,0.15) !important; }
.data-table :deep(.el-table__cell) { background: #0c1628 !important; color: #d6e8f8 !important; border-color: rgba(80,150,220,0.08) !important; font-size: 14px; }
.data-table :deep(.el-table__row:hover > td) { background: #132742 !important; }
.data-table :deep(.el-table__empty-block) { background: #0c1628 !important; }
.data-table :deep(.el-table__empty-text) { color: rgba(255,255,255,0.4); }
.op-tag { background: rgba(80,150,220,0.1); color: #8cb8e0; padding: 2px 8px; border-radius: 6px; font-size: 12px; }

.action-btn-del { background: rgba(255,68,68,0.1) !important; border: 1px solid rgba(255,68,68,0.15) !important; color: #ff6b6b !important; border-radius: 6px; }
.action-btn-del:hover { background: rgba(255,68,68,0.2) !important; }
</style>