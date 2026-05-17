<template>
  <div class="admin-root">
    <div class="admin-header">
      <div class="header-left">
        <el-icon class="back-icon" @click="$router.push('/dashboard')"><ArrowLeft /></el-icon>
        <div class="brand">
          <el-icon class="brand-icon"><Setting /></el-icon>
          <span class="brand-text">系统管理</span>
        </div>
      </div>
      <el-button class="hdr-btn" @click="$router.push('/dashboard')">
        <el-icon><ArrowLeft /></el-icon>
        <span class="hdr-btn-text">返回首页</span>
      </el-button>
    </div>
    <div class="admin-body">
      <div class="admin-aside">
        <el-menu :default-active="activeMenu" @select="handleMenuSelect" class="side-menu">
          <el-menu-item index="user">
            <el-icon><User /></el-icon>
            <span>用户管理</span>
          </el-menu-item>
          <el-menu-item index="zone">
            <el-icon><Grid /></el-icon>
            <span>区域管理</span>
          </el-menu-item>
          <el-menu-item index="trace">
            <el-icon><CircleCheck /></el-icon>
            <span>溯源管理</span>
          </el-menu-item>
        </el-menu>
      </div>
      <div class="admin-main">
        <component :is="currentComponent" />
      </div>
    </div>
  </div>
</template>

<script>
import { ref, computed } from 'vue'
import UserManage from '@/components/UserManage.vue'
import ZoneManage from '@/components/ZoneManage.vue'
import TraceManage from '@/components/TraceManage.vue'
import { ArrowLeft, User, Grid, CircleCheck, Setting } from '@element-plus/icons-vue'

export default {
  name: 'Admin',
  components: { UserManage, ZoneManage, TraceManage, ArrowLeft, User, Grid, CircleCheck, Setting },
  setup() {
    const activeMenu = ref('user')
    const currentComponent = computed(() => {
      switch (activeMenu.value) {
        case 'user': return 'UserManage'
        case 'zone': return 'ZoneManage'
        case 'trace': return 'TraceManage'
        default: return 'UserManage'
      }
    })
    const handleMenuSelect = (key) => { activeMenu.value = key }
    return { activeMenu, currentComponent, handleMenuSelect }
  }
}
</script>

<style scoped>
.admin-root {
  min-height: 100vh;
  background: linear-gradient(170deg, #060d1a 0%, #0d1b2a 25%, #132742 50%, #0f1f35 75%, #080f1e 100%);
  display: flex;
  flex-direction: column;
}

.admin-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 28px;
  height: 56px;
  background: rgba(12, 22, 40, 0.92);
  border-bottom: 1px solid rgba(80, 150, 220, 0.1);
  box-shadow: 0 2px 20px rgba(0, 0, 0, 0.3);
  z-index: 100;
  flex-shrink: 0;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 14px;
}

.back-icon {
  font-size: 20px;
  color: rgba(255, 255, 255, 0.5);
  cursor: pointer;
  transition: color 0.3s;
}

.back-icon:hover { color: #5b9bd5; }

.brand {
  display: flex;
  align-items: center;
  gap: 10px;
}

.brand-icon {
  font-size: 20px;
  color: #5b9bd5;
  filter: drop-shadow(0 0 6px rgba(91, 155, 213, 0.4));
}

.brand-text {
  font-size: 17px;
  font-weight: 700;
  color: #d6e8f8;
}

.hdr-btn {
  background: rgba(255, 255, 255, 0.04) !important;
  border: 1px solid rgba(80, 150, 220, 0.12) !important;
  color: #a0c4e0 !important;
  border-radius: 10px !important;
  padding: 6px 16px !important;
  transition: all 0.3s;
}

.hdr-btn:hover {
  background: rgba(80, 150, 220, 0.12) !important;
  border-color: rgba(80, 150, 220, 0.25) !important;
  color: #7ab0e0 !important;
}

.hdr-btn-text { margin-left: 5px; }

.admin-body {
  display: flex;
  flex: 1;
  overflow: hidden;
}

.admin-aside {
  width: 220px;
  background: rgba(8, 18, 32, 0.7);
  border-right: 1px solid rgba(80, 150, 220, 0.06);
  padding: 16px 12px;
  flex-shrink: 0;
}

.side-menu {
  border: none;
  background: transparent;
}

.side-menu :deep(.el-menu-item) {
  color: rgba(255, 255, 255, 0.6);
  border-radius: 10px;
  margin-bottom: 2px;
  transition: all 0.3s;
  height: 44px;
  line-height: 44px;
}

.side-menu :deep(.el-menu-item:hover) {
  background: rgba(80, 150, 220, 0.08);
  color: #8cb8e0;
}

.side-menu :deep(.el-menu-item.is-active) {
  background: rgba(33, 120, 220, 0.15);
  color: #5b9bd5;
  font-weight: 600;
}

.admin-main {
  flex: 1;
  overflow-y: auto;
  padding: 24px;
  background: transparent;
}

@media (max-width: 768px) {
  .admin-aside { display: none; }
  .hdr-btn-text { display: none; }
}
</style>