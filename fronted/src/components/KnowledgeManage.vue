<template>
  <div class="comp-root">
    <div class="comp-card">
      <div class="comp-header">
        <div class="header-left">
          <el-icon class="header-icon"><Collection /></el-icon>
          <span class="header-title">知识库</span>
        </div>
        <el-button class="add-btn" @click="handleAdd"><el-icon><Plus /></el-icon>添加知识</el-button>
      </div>

      <div class="filter-bar">
        <el-input v-model="filterKeyword" placeholder="搜索知识标题/分类" clearable size="small" class="filter-input" />
        <el-select v-model="filterCategory" placeholder="全部分类" clearable size="small" class="filter-sel">
          <el-option v-for="c in categories" :key="c" :label="c" :value="c" />
        </el-select>
        <el-button size="small" class="filter-btn" @click="loadData">查询</el-button>
        <el-button size="small" class="filter-btn-reset" @click="resetFilter">重置</el-button>
      </div>

      <el-table :data="tableData" class="data-table" empty-text="暂无数据" v-loading="loading">
        <el-table-column prop="title" label="标题" min-width="200" />
        <el-table-column prop="category" label="分类" min-width="100">
          <template #default="{ row }"><span class="cat-tag">{{ row.category }}</span></template>
        </el-table-column>
        <el-table-column prop="summary" label="摘要" min-width="300" show-overflow-tooltip />
        <el-table-column prop="createTime" label="创建时间" min-width="150" />
        <el-table-column label="操作" min-width="160" fixed="right">
          <template #default="{ row }">
            <el-button size="small" class="action-btn" @click="handleView(row)">查看</el-button>
            <el-button size="small" class="action-btn" @click="handleEdit(row)">编辑</el-button>
            <el-button size="small" class="action-btn-del" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <el-dialog v-model="viewDlg" title="知识详情" width="600px" class="add-dialog" :append-to-body="true" :close-on-click-modal="false">
      <div class="view-content">{{ viewContent }}</div>
    </el-dialog>

    <el-dialog v-model="dialogVisible" :title="editing ? '编辑知识' : '添加知识'" width="600px" class="add-dialog" :append-to-body="true" :close-on-click-modal="false">
      <el-form :model="form" label-width="80px">
        <el-form-item label="标题"><el-input v-model="form.title" /></el-form-item>
        <el-form-item label="分类">
          <el-select v-model="form.category" style="width:100%">
            <el-option v-for="c in categories" :key="c" :label="c" :value="c" />
          </el-select>
        </el-form-item>
        <el-form-item label="摘要"><el-input v-model="form.summary" type="textarea" :rows="2" /></el-form-item>
        <el-form-item label="详细内容"><el-input v-model="form.content" type="textarea" :rows="6" /></el-form-item>
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
import { Plus, Collection } from '@element-plus/icons-vue'
import { getKnowledgeList, createKnowledge, updateKnowledge, deleteKnowledge, getKnowledgeById } from '@/api/knowledge'

export default {
  name: 'KnowledgeManage',
  components: { Plus, Collection },
  setup() {
    const tableData = ref([])
    const loading = ref(false)
    const saving = ref(false)
    const dialogVisible = ref(false)
    const viewDlg = ref(false)
    const viewContent = ref('')
    const editing = ref(null)
    const filterKeyword = ref('')
    const filterCategory = ref('')
    const categories = ['养殖技术', '病害防治', '饲料管理', '水质管理', '政策法规', '产业动态']
    const form = reactive({ title: '', category: '', summary: '', content: '' })

    const resetForm = () => { form.title = ''; form.category = ''; form.summary = ''; form.content = '' }

    const loadData = async () => {
      loading.value = true
      try {
        const params = {}
        if (filterCategory.value) params.category = filterCategory.value
        if (filterKeyword.value) params.keyword = filterKeyword.value
        const res = await getKnowledgeList(params)
        tableData.value = Array.isArray(res.data) ? res.data : []
      } catch (e) { console.error(e); tableData.value = [] }
      finally { loading.value = false }
    }

    const handleAdd = () => { editing.value = null; resetForm(); dialogVisible.value = true }
    const handleEdit = (row) => { editing.value = row; Object.assign(form, row); dialogVisible.value = true }

    const handleView = async (row) => {
      try { const res = await getKnowledgeById(row.id); viewContent.value = res.data.content || res.data.summary || ''; viewDlg.value = true }
      catch { viewContent.value = row.content || row.summary || ''; viewDlg.value = true }
    }

    const handleSave = async () => {
      saving.value = true
      try {
        if (editing.value) { await updateKnowledge(editing.value.id, form); ElMessage.success('编辑成功') }
        else { await createKnowledge(form); ElMessage.success('添加成功') }
        dialogVisible.value = false; loadData()
      } catch (e) { ElMessage.error('操作失败') }
      finally { saving.value = false }
    }

    const handleDelete = async (row) => {
      try {
        await ElMessageBox.confirm('确定删除？', '提示', { type: 'warning' })
        await deleteKnowledge(row.id)
        ElMessage.success('已删除')
        loadData()
      } catch { /* ignore */ }
    }

    const resetFilter = () => { filterKeyword.value = ''; filterCategory.value = ''; loadData() }

    onMounted(() => { loadData() })
    return { tableData, loading, saving, dialogVisible, viewDlg, viewContent, editing,
      filterKeyword, filterCategory, categories, form,
      loadData, handleAdd, handleEdit, handleView, handleSave, handleDelete, resetFilter }
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

.filter-bar { display: flex; gap: 10px; margin-bottom: 16px; }
.filter-input { width: 220px; }
.filter-sel { width: 150px; }
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

.cat-tag { background: rgba(80,150,220,0.1); color: #8cb8e0; padding: 2px 8px; border-radius: 6px; font-size: 12px; }
.view-content { color: #d6e8f8; white-space: pre-wrap; line-height: 1.8; font-size: 14px; max-height: 400px; overflow-y: auto; }

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
.add-dialog .el-textarea__inner {
  background: rgba(255,255,255,0.06) !important;
  border: 1px solid rgba(80,150,220,0.15);
  border-radius: 8px;
  color: #d6e8f8 !important;
  font-size: 14px;
}
.add-dialog .el-textarea__inner:hover {
  border-color: rgba(80,150,220,0.3);
}
.add-dialog .el-textarea__inner:focus {
  border-color: #5b9bd5 !important;
  box-shadow: 0 0 0 2px rgba(91,155,213,0.15) !important;
}
.add-dialog .el-textarea__inner::placeholder {
  color: rgba(255,255,255,0.3) !important;
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
</style>