<template>
  <div class="user-root">
    <el-card class="main-card" shadow="never">
      <template #header>
        <div class="card-header">
          <span class="title">
            <el-icon><User /></el-icon>用户管理
          </span>
          <el-button type="primary" @click="showDialog" class="add-btn">
            <el-icon><Plus /></el-icon>添加用户
          </el-button>
        </div>
      </template>

      <el-table :data="userList" class="data-table" empty-text="暂无数据">
        <el-table-column prop="username" label="用户名" min-width="150" />
        <el-table-column prop="realName" label="姓名" min-width="120" />
        <el-table-column prop="phone" label="电话" min-width="160" />
        <el-table-column prop="role" label="角色" min-width="140">
          <template #default="{ row }">
            <el-tag class="role-tag" :type="getRoleType(row.role)">{{ getRoleText(row.role) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="enabled" label="状态" min-width="100">
          <template #default="{ row }">
            <el-tag :type="row.enabled ? 'success' : 'danger'" class="status-tag">{{ row.enabled ? '启用' : '禁用' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" min-width="180">
          <template #default="{ row }">
            <el-button size="small" @click="handleToggle(row)">{{ row.enabled ? '禁用' : '启用' }}</el-button>
            <el-button size="small" type="danger" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑用户' : '添加用户'" width="500px" class="add-dialog">
      <el-form :model="form" label-width="100px" class="add-form">
        <el-form-item label="用户名">
          <el-input v-model="form.username" :disabled="isEdit" class="full-width" />
        </el-form-item>
        <el-form-item label="密码" v-if="!isEdit">
          <el-input v-model="form.password" type="password" class="full-width" />
        </el-form-item>
        <el-form-item label="姓名">
          <el-input v-model="form.realName" class="full-width" />
        </el-form-item>
        <el-form-item label="电话">
          <el-input v-model="form.phone" class="full-width" />
        </el-form-item>
        <el-form-item label="角色">
          <el-select v-model="form.role" class="full-width">
            <el-option label="养殖人员" value="FARMER" />
            <el-option label="管理员" value="ADMIN" />
            <el-option label="超级管理员" value="SUPER_ADMIN" />
          </el-select>
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
import { getUsers, createUser, updateUser, deleteUser, toggleUser } from '@/api/user'
import { ElMessage } from 'element-plus'
import { User, Plus } from '@element-plus/icons-vue'

export default {
  name: 'UserManage',
  components: { User, Plus },
  setup() {
    const userList = ref([])
    const dialogVisible = ref(false)
    const isEdit = ref(false)
    const form = ref({})

    const getRoleType = (role) => { switch (role) { case 'SUPER_ADMIN': return 'danger'; case 'ADMIN': return 'warning'; default: return '' } }
    const getRoleText = (role) => { switch (role) { case 'SUPER_ADMIN': return '超级管理员'; case 'ADMIN': return '管理员'; default: return '养殖人员' } }

    const loadUsers = async () => { try { const res = await getUsers(); userList.value = res.data } catch (e) { console.error(e) } }
    const showDialog = () => { isEdit.value = false; form.value = { username: '', password: '', realName: '', phone: '', role: 'FARMER' }; dialogVisible.value = true }

    const handleSubmit = async () => {
      try {
        if (isEdit.value) { await updateUser(form.value.id, form.value) } else { await createUser(form.value) }
        dialogVisible.value = false; ElMessage.success('操作成功'); loadUsers()
      } catch (e) { ElMessage.error('操作失败') }
    }

    const handleToggle = async (row) => { try { await toggleUser(row.id); ElMessage.success('状态已更新'); loadUsers() } catch (e) { ElMessage.error('操作失败') } }
    const handleDelete = async (row) => { try { if (confirm('确定删除该用户吗？')) { await deleteUser(row.id); ElMessage.success('删除成功'); loadUsers() } } catch (e) { ElMessage.error('删除失败') } }

    onMounted(() => { loadUsers() })
    return { userList, dialogVisible, isEdit, form, showDialog, handleSubmit, handleToggle, handleDelete, getRoleType, getRoleText }
  }
}
</script>

<style scoped>
.user-root { padding: 20px; }
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

.role-tag, .status-tag { border-radius: 10px; padding: 2px 10px; font-weight: 500; }
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
.add-dialog .el-input.is-disabled .el-input__wrapper { background: rgba(255, 255, 255, 0.03) !important; opacity: 0.6; }
.add-dialog .el-select .el-input__wrapper { background: rgba(255, 255, 255, 0.06) !important; box-shadow: none !important; border: 1px solid rgba(80, 150, 220, 0.15); }
.add-dialog .el-select .el-input__inner { color: #d6e8f8 !important; }
.add-dialog .el-button { border-radius: 8px; }
.add-dialog .el-button--default { background: rgba(255, 255, 255, 0.06) !important; border: 1px solid rgba(255, 255, 255, 0.1) !important; color: #a0c8e0 !important; }
.add-dialog .el-button--default:hover { background: rgba(255, 255, 255, 0.1) !important; border-color: rgba(255, 255, 255, 0.2) !important; color: #d6e8f8 !important; }
.add-dialog .el-button--primary { background: linear-gradient(135deg, #5b9bd5, #2196f3); border: none; box-shadow: 0 4px 15px rgba(33, 150, 243, 0.25); }
.add-dialog .el-select-dropdown { background: #0f1c2e !important; border: 1px solid rgba(80, 150, 220, 0.15) !important; }
.add-dialog .el-select-dropdown__item { color: #d6e8f8 !important; }
.add-dialog .el-select-dropdown__item.hover,
.add-dialog .el-select-dropdown__item:hover { background: rgba(80, 150, 220, 0.1) !important; }
.add-dialog .el-select-dropdown__item.selected { color: #5b9bd5 !important; font-weight: 600; }
</style>