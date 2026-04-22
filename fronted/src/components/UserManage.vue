<template>
  <el-card>
    <template #header>
      <div style="display: flex; justify-content: space-between">
        <span>用户管理</span>
        <el-button type="primary" @click="showDialog">添加用户</el-button>
      </div>
    </template>

    <el-table :data="userList" border>
      <el-table-column prop="username" label="用户名" />
      <el-table-column prop="realName" label="姓名" />
      <el-table-column prop="phone" label="电话" />
      <el-table-column prop="role" label="角色">
        <template #default="{ row }">
          <el-tag>{{ row.role === 'FARMER' ? '养殖人员' : row.role === 'ADMIN' ? '管理员' : '超级管理员' }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="enabled" label="状态" width="100">
        <template #default="{ row }">
          <el-tag :type="row.enabled ? 'success' : 'danger'">
            {{ row.enabled ? '启用' : '禁用' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="200">
        <template #default="{ row }">
          <el-button size="small" @click="handleToggle(row)">
            {{ row.enabled ? '禁用' : '启用' }}
          </el-button>
          <el-button size="small" type="danger" @click="handleDelete(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
  </el-card>

  <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑用户' : '添加用户'">
    <el-form :model="form" label-width="100px">
      <el-form-item label="用户名">
        <el-input v-model="form.username" :disabled="isEdit" />
      </el-form-item>
      <el-form-item label="密码" v-if="!isEdit">
        <el-input v-model="form.password" type="password" />
      </el-form-item>
      <el-form-item label="姓名">
        <el-input v-model="form.realName" />
      </el-form-item>
      <el-form-item label="电话">
        <el-input v-model="form.phone" />
      </el-form-item>
      <el-form-item label="角色">
        <el-select v-model="form.role">
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
</template>

<script>
import { ref, onMounted } from 'vue'
import { getUsers, createUser, updateUser, deleteUser, toggleUser } from '@/api/user'

export default {
  name: 'UserManage',
  setup() {
    const userList = ref([])
    const dialogVisible = ref(false)
    const isEdit = ref(false)
    const form = ref({})

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
        loadUsers()
      } catch (error) {
        alert('操作失败')
      }
    }

    const handleToggle = async (row) => {
      try {
        await toggleUser(row.id)
        loadUsers()
      } catch (error) {
        alert('操作失败')
      }
    }

    const handleDelete = async (row) => {
      try {
        if (confirm('确定删除该用户吗？')) {
          await deleteUser(row.id)
          loadUsers()
        }
      } catch (error) {
        alert('删除失败')
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
      handleDelete
    }
  }
}
</script>
