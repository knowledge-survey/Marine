<template>
  <el-container class="admin-container">
    <el-header class="admin-header">
      <div class="header-left">
        <el-icon class="back-icon" @click="$router.push('/dashboard')" title="单击以返回"><ArrowLeft /></el-icon>
        <h2 class="header-title">系统管理</h2>
      </div>
      <el-button @click="$router.push('/dashboard')" class="back-btn">
        返回
      </el-button>
    </el-header>
    <el-container>
      <el-aside width="220px" class="admin-aside">
        <el-menu :default-active="activeMenu" @select="handleMenuSelect" class="admin-menu">
          <el-menu-item index="user">
            <el-icon><User /></el-icon>
            <span>用户管理</span>
          </el-menu-item>
          <el-menu-item index="zone">
            <el-icon><Grid /></el-icon>
            <span>区域管理</span>
          </el-menu-item>
        </el-menu>
      </el-aside>
      <el-main class="admin-main">
        <component :is="currentComponent" />
      </el-main>
    </el-container>
  </el-container>
</template>

<script>
import { ref, computed } from 'vue'
import UserManage from '@/components/UserManage.vue'
import ZoneManage from '@/components/ZoneManage.vue'
import { ArrowLeft, User, Grid } from '@element-plus/icons-vue'

export default {
  name: 'Admin',
  components: { UserManage, ZoneManage, ArrowLeft, User, Grid },
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

<style scoped>
.admin-container {
  background: #0c1929;
  height: 100vh;
}

.admin-header {
  background: linear-gradient(135deg, #0c1929 0%, #1e3a5f 100%);
  border-bottom: 1px solid rgba(0, 212, 255, 0.3);
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 24px;
  height: 60px;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 16px;
}

.back-icon {
  font-size: 20px;
  color: rgba(255, 255, 255, 0.7);
  cursor: pointer;
  transition: all 0.3s;
}

.back-icon:hover {
  color: #00d4ff;
}

.header-title {
  margin: 0;
  color: #00d4ff;
  font-size: 22px;
  font-weight: 700;
  letter-spacing: 1px;
  text-shadow: 0 0 10px rgba(0, 212, 255, 0.5);
}

.back-btn {
  background: rgba(0, 212, 255, 0.2);
  border: 1px solid rgba(0, 212, 255, 0.4);
  color: #00d4ff;
}

.back-btn:hover {
  background: rgba(0, 212, 255, 0.3);
  border-color: #00d4ff;
  color: #fff;
}

.admin-aside {
  background: rgba(20, 40, 60, 0.9);
  border-right: 1px solid rgba(0, 212, 255, 0.2);
  padding: 20px 12px;
}

.admin-menu {
  border: none;
  background: transparent;
}

.admin-menu :deep(.el-menu-item) {
  color: rgba(255, 255, 255, 0.8);
  border-radius: 8px;
  margin-bottom: 4px;
  transition: all 0.3s;
}

.admin-menu :deep(.el-menu-item:hover) {
  background: rgba(0, 212, 255, 0.1);
  color: #00d4ff;
}

.admin-menu :deep(.el-menu-item.is-active) {
  background: rgba(0, 212, 255, 0.2);
  color: #00d4ff;
}

.admin-main {
  background: #0c1929;
  padding: 20px;
  overflow-y: auto;
}
</style>
