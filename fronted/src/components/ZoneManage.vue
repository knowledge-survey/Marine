<template>
  <el-card>
    <template #header>
      <div style="display: flex; justify-content: space-between">
        <span>区域管理</span>
        <el-button type="primary" @click="showDialog">添加区域</el-button>
      </div>
    </template>

    <el-table :data="zoneList" border row-key="id">
      <el-table-column prop="name" label="区域名称" />
      <el-table-column prop="location" label="位置" />
      <el-table-column prop="area" label="面积" />
      <el-table-column prop="species" label="养殖品种" />
      <el-table-column label="操作" width="200">
        <template #default="{ row }">
          <el-button size="small" @click="handleEdit(row)">编辑</el-button>
          <el-button size="small" type="danger" @click="handleDelete(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
  </el-card>

  <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑区域' : '添加区域'">
    <el-form :model="form" label-width="100px">
      <el-form-item label="区域名称">
        <el-input v-model="form.name" />
      </el-form-item>
      <el-form-item label="位置">
        <el-input v-model="form.location" />
      </el-form-item>
      <el-form-item label="面积">
        <el-input-number v-model="form.area" />
      </el-form-item>
      <el-form-item label="养殖品种">
        <el-input v-model="form.species" />
      </el-form-item>
      <el-form-item label="描述">
        <el-input v-model="form.description" type="textarea" />
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
import { getZones, createZone, updateZone, deleteZone } from '@/api/zone'

export default {
  name: 'ZoneManage',
  setup() {
    const zoneList = ref([])
    const dialogVisible = ref(false)
    const isEdit = ref(false)
    const form = ref({})

    const loadZones = async () => {
      try {
        const res = await getZones()
        zoneList.value = res.data
      } catch (error) {
        console.error(error)
      }
    }

    const showDialog = () => {
      isEdit.value = false
      form.value = { name: '', location: '', area: null, species: '', description: '' }
      dialogVisible.value = true
    }

    const handleEdit = (row) => {
      isEdit.value = true
      form.value = { ...row }
      dialogVisible.value = true
    }

    const handleSubmit = async () => {
      try {
        if (isEdit.value) {
          await updateZone(form.value.id, form.value)
        } else {
          await createZone(form.value)
        }
        dialogVisible.value = false
        loadZones()
      } catch (error) {
        alert('操作失败')
      }
    }

    const handleDelete = async (row) => {
      try {
        if (confirm('确定删除该区域吗？')) {
          await deleteZone(row.id)
          loadZones()
        }
      } catch (error) {
        alert('删除失败')
      }
    }

    onMounted(() => {
      loadZones()
    })

    return {
      zoneList,
      dialogVisible,
      isEdit,
      form,
      showDialog,
      handleEdit,
      handleSubmit,
      handleDelete
    }
  }
}
</script>
