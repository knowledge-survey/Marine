<template>
  <el-container style="height: 100vh">
    <el-header style="display: flex; align-items: center; justify-content: space-between; background: #409eff; color: white">
      <h2 style="margin: 0">系统管理</h2>
      <el-button @click="$router.push('/dashboard')">返回</el-button>
    </el-header>
    <el-container>
      <el-aside width="200px" style="background: #f5f7fa; padding: 20px">
        <el-menu :default-active="activeMenu" @select="handleMenuSelect">
          <el-menu-item index="user">用户管理</el-menu-item>
          <el-menu-item index="zone">区域管理</el-menu-item>
        </el-menu>
      </el-aside>
      <el-main style="background: #f0f2f5">
        <component :is="currentComponent" />
      </el-main>
    </el-container>
  </el-container>
</template>

<script>
import { ref, computed } from 'vue'
import UserManage from '@/components/UserManage.vue'
import ZoneManage from '@/components/ZoneManage.vue'

export default {
  name: 'Admin',
  components: { UserManage, ZoneManage },
  setup() {
    const activeMenu = ref('user')

    const currentComponent = computed(() => {
      return activeMenu.value === 'user' ? 'UserManage' : 'ZoneManage'
    })

    const handleMenuSelect = (key) => {
      activeMenu.value = key
    }

    return {
      activeMenu,
      currentComponent,
      handleMenuSelect
    }
  }
}
</script>
