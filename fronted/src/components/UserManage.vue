<template>
  <div class="user-manage">
    <el-card class="main-card">
      <template #header>
        <div class="card-header">
          <span class="title">用户管理</span>
          <el-button type="primary" @click="showDialog" class="add-btn">
            添加用户
          </el-button>
        </div>
      </template>

      <div class="table-wrapper">
        <el-table :data="userList" border class="data-table">
          <el-table-column prop="username" label="用户名" min-width="150" />
          <el-table-column prop="realName" label="姓名" min-width="120" />
          <el-table-column prop="phone" label="电话" min-width="160" />
          <el-table-column prop="role" label="角色" min-width="140">
            <template #default="{ row }">
              <el-tag class="role-tag" :type="getRoleType(row.role)">
                {{ getRoleText(row.role) }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="enabled" label="状态" min-width="100">
            <template #default="{ row }">
              <el-tag :type="row.enabled ? 'success' : 'danger'" class="status-tag">
                {{ row.enabled ? '启用' : '禁用' }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column label="操作" min-width="180">
            <template #default="{ row }">
              <el-button size="small" class="action-btn" @click="handleToggle(row)">
                {{ row.enabled ? '禁用' : '启用' }}
              </el-button>
              <el-button size="small" type="danger" class="action-btn" @click="handleDelete(row)">
                删除
              </el-button>
            </template>
          </el-table-column>
        </el-table>
      </div>
    </el-card>

    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑用户' : '添加用户'" class="add-dialog">
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
        <el-button @click="dialogVisible = false" class="cancel-btn">取消</el-button>
        <el-button type="primary" @click="handleSubmit" class="confirm-btn">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import { ref, onMounted } from 'vue'
import { getUsers, createUser, updateUser, deleteUser, toggleUser } from '@/api/user'
import { ElMessage } from 'element-plus'

export default {
  name: 'UserManage',
  setup() {
    const userList = ref([])
    const dialogVisible = ref(false)
    const isEdit = ref(false)
    const form = ref({})

    const getRoleType = (role) => {
      switch (role) {
        case 'SUPER_ADMIN': return 'danger'
        case 'ADMIN': return 'warning'
        default: return ''
      }
    }

    const getRoleText = (role) => {
      switch (role) {
        case 'SUPER_ADMIN': return '超级管理员'
        case 'ADMIN': return '管理员'
        default: return '养殖人员'
      }
    }

    const loadUsers = async () => {
      try {
        const res = await getUsers()
        userList.value = res.data
      } catch (error) {
        console.error(error)
      }
    }

    const showDialog = () => {
      isEdit.value = false
      form.value = { username: '', password: '', realName: '', phone: '', role: 'FARMER' }
      dialogVisible.value = true
    }

    const handleSubmit = async () => {
      try {
        if (isEdit.value) {
          await updateUser(form.value.id, form.value)
        } else {
          await createUser(form.value)
        }
        dialogVisible.value = false
        ElMessage.success('操作成功')
        loadUsers()
      } catch (error) {
        ElMessage.error('操作失败')
      }
    }

    const handleToggle = async (row) => {
      try {
        await toggleUser(row.id)
        ElMessage.success('状态已更新')
        loadUsers()
      } catch (error) {
        ElMessage.error('操作失败')
      }
    }

    const handleDelete = async (row) => {
      try {
        if (confirm('确定删除该用户吗？')) {
          await deleteUser(row.id)
          ElMessage.success('删除成功')
          loadUsers()
        }
      } catch (error) {
        ElMessage.error('删除失败')
      }
    }

    onMounted(() => {
      loadUsers()
    })

    return {
      userList,
      dialogVisible,
      isEdit,
      form,
      showDialog,
      handleSubmit,
      handleToggle,
      handleDelete,
      getRoleType,
      getRoleText
    }
  }
}
</script>

<style scoped>
.user-manage {
  width: 100%;
  height: 100%;
}

.main-card {
  background: rgba(20, 40, 60, 0.9) !important;
  border: 1px solid rgba(0, 212, 255, 0.3) !important;
  border-radius: 12px;
  width: 100%;
}

.main-card :deep(.el-card__body) {
  width: 100%;
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
  width: 100%;
}

.data-table {
  border-radius: 8px;
  overflow: hidden;
  width: 100%;
}

.data-table :deep(.el-table) {
  background-color: rgba(10, 25, 40, 0.8) !important;
  width: 100% !important;
}

.data-table :deep(.el-table__header-wrapper) {
  background-color: rgba(10, 25, 40, 0.9) !important;
  width: 100%;
}

.data-table :deep(.el-table__header) {
  background-color: rgba(10, 25, 40, 0.9) !important;
  width: 100%;
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
  width: 100%;
}

.data-table :deep(.el-table__body) {
  background-color: rgba(10, 25, 40, 0.8) !important;
  width: 100%;
}

.data-table :deep(.el-table__row) {
  background-color: rgba(10, 25, 40, 0.8) !important;
  width: 100%;
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
  width: 100%;
}

.data-table :deep(.el-table__empty-text) {
  color: rgba(255, 255, 255, 0.5) !important;
}

.role-tag {
  border-radius: 12px;
  padding: 4px 12px;
  font-weight: 500;
}

.status-tag {
  border-radius: 12px;
  padding: 4px 12px;
  font-weight: 500;
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

.add-form :deep(.el-select .el-input__wrapper) {
  background-color: rgba(10, 25, 40, 0.8) !important;
  border: 1px solid rgba(0, 212, 255, 0.2) !important;
}

.add-form :deep(.el-select .el-input__inner) {
  color: #fff !important;
}

.add-dialog :deep(.el-popper) {
  background-color: rgba(20, 40, 60, 0.95) !important;
  border: 1px solid rgba(0, 212, 255, 0.3) !important;
}

.add-dialog :deep(.el-select-dropdown__item) {
  color: rgba(255, 255, 255, 0.9) !important;
}

.add-dialog :deep(.el-select-dropdown__item:hover) {
  background-color: rgba(0, 212, 255, 0.1) !important;
}

.add-dialog :deep(.el-select-dropdown__item.selected) {
  color: #00d4ff !important;
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
