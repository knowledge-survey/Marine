<template>
  <el-container class="dashboard-container">
    <el-header class="main-header">
      <div class="header-left">
        <h2>海洋养殖管理系统</h2>
      </div>
      <div class="header-right">
        <el-button type="info" @click="$router.push('/admin')" class="admin-btn" v-if="isAdmin">
          管理后台
        </el-button>
        <el-button @click="logout" class="logout-btn">
          退出登录
        </el-button>
      </div>
    </el-header>
    <el-container>
      <el-aside width="250px" class="main-aside">
        <el-menu
          :default-active="activeMenu"
          @select="handleMenuSelect"
          class="main-menu"
        >
          <el-menu-item index="water">
            <el-icon><TrendCharts /></el-icon>
            <span>水质监测</span>
          </el-menu-item>
          <el-menu-item index="record">
            <el-icon><Document /></el-icon>
            <span>养殖记录</span>
          </el-menu-item>
          <el-menu-item index="disease">
            <el-icon><Warning /></el-icon>
            <span>病害防控</span>
          </el-menu-item>
          <el-menu-item index="trace">
            <el-icon><CircleCheck /></el-icon>
            <span>溯源管理</span>
          </el-menu-item>
        </el-menu>
        
        <el-divider class="zone-divider">养殖区域</el-divider>
        <el-tree
          :data="zoneTree"
          :props="{ label: 'name', children: 'children' }"
          @node-click="handleZoneClick"
          node-key="id"
          :default-expanded-keys="[currentZoneId]"
          :highlight-current="true"
          class="zone-tree"
        />
      </el-aside>
      <el-main class="main-content">
        <component :is="currentComponent" :zoneId="currentZoneId" />
      </el-main>
    </el-container>
  </el-container>
</template>

<script>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { getZones } from '@/api/zone'
import WaterQuality from '@/components/WaterQuality.vue'
import AquacultureRecord from '@/components/AquacultureRecord.vue'
import DiseaseControl from '@/components/DiseaseControl.vue'
import TraceManage from '@/components/TraceManage.vue'
import { TrendCharts, Document, Warning, CircleCheck } from '@element-plus/icons-vue'

export default {
  name: 'Dashboard',
  components: { 
    WaterQuality, 
    AquacultureRecord, 
    DiseaseControl, 
    TraceManage,
    TrendCharts, Document, Warning, CircleCheck
  },
  setup() {
    const router = useRouter()
    const activeMenu = ref('water')
    const zones = ref([])
    const currentZoneId = ref(null)

    const user = JSON.parse(localStorage.getItem('user'))
    const isAdmin = computed(() => user && user.role !== 'FARMER')

    const zoneTree = computed(() => {
      const buildTree = (parentId = null) => {
        return zones.value
          .filter(z => (parentId ? z.parent?.id === parentId : !z.parent))
          .map(z => ({
            ...z,
            children: buildTree(z.id)
          }))
      }
      return buildTree()
    })

    const currentComponent = computed(() => {
      switch (activeMenu.value) {
        case 'water': return 'WaterQuality'
        case 'record': return 'AquacultureRecord'
        case 'disease': return 'DiseaseControl'
        case 'trace': return 'TraceManage'
        default: return 'WaterQuality'
      }
    })

    const loadZones = async () => {
      try {
        const res = await getZones()
        zones.value = res.data
        if (zones.value.length > 0) {
          currentZoneId.value = zones.value[0].id
        }
      } catch (error) {
        console.error(error)
      }
    }

    const handleMenuSelect = (key) => {
      activeMenu.value = key
    }

    const handleZoneClick = (data) => {
      currentZoneId.value = data.id
    }

    const logout = () => {
      localStorage.removeItem('user')
      router.push('/login')
    }

    onMounted(() => {
      loadZones()
    })

    return {
      activeMenu,
      zoneTree,
      currentZoneId,
      currentComponent,
      isAdmin,
      handleMenuSelect,
      handleZoneClick,
      logout
    }
  }
}
</script>

<style scoped>
.dashboard-container {
  background: #0c1929;
  min-height: 100vh;
}

.main-header {
  background: linear-gradient(135deg, #0c1929 0%, #1e3a5f 100%);
  border-bottom: 1px solid rgba(0, 212, 255, 0.3);
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 24px;
  height: 60px;
}

.header-left h2 {
  margin: 0;
  color: #00d4ff;
  font-size: 22px;
  font-weight: 700;
  letter-spacing: 1px;
  text-shadow: 0 0 10px rgba(0, 212, 255, 0.5);
}

.header-right {
  display: flex;
  gap: 12px;
}

.admin-btn {
  background: rgba(0, 212, 255, 0.2);
  border: 1px solid rgba(0, 212, 255, 0.4);
  color: #00d4ff;
}

.admin-btn:hover {
  background: rgba(0, 212, 255, 0.3);
  border-color: #00d4ff;
  color: #fff;
}

.logout-btn {
  background: rgba(255, 107, 53, 0.2);
  border: 1px solid rgba(255, 107, 53, 0.4);
  color: #ff6b35;
}

.logout-btn:hover {
  background: rgba(255, 107, 53, 0.3);
  border-color: #ff6b35;
  color: #fff;
}

.main-aside {
  background: rgba(20, 40, 60, 0.9);
  border-right: 1px solid rgba(0, 212, 255, 0.2);
  padding: 20px;
}

.main-menu {
  border: none;
  background: transparent;
}

.main-menu :deep(.el-menu-item) {
  color: rgba(255, 255, 255, 0.8);
  border-radius: 8px;
  margin-bottom: 4px;
  transition: all 0.3s;
}

.main-menu :deep(.el-menu-item:hover) {
  background: rgba(0, 212, 255, 0.1);
  color: #00d4ff;
}

.main-menu :deep(.el-menu-item.is-active) {
  background: rgba(0, 212, 255, 0.2);
  color: #00d4ff;
}

.zone-divider {
  border-color: rgba(0, 212, 255, 0.2);
  margin: 20px 0;
}

.zone-divider :deep(.el-divider__text) {
  background: rgba(20, 40, 60, 0.9);
  color: rgba(255, 255, 255, 0.7);
}

.zone-tree {
  background: transparent;
  padding: 8px;
}

.zone-tree :deep(.el-tree-node__content) {
  color: rgba(255, 255, 255, 0.85);
  border-radius: 8px;
  transition: all 0.3s ease;
  padding: 8px 12px;
  margin: 2px 0;
  height: auto;
  background: transparent;
}

.zone-tree :deep(.el-tree-node__content:hover) {
  background: linear-gradient(90deg, rgba(0, 212, 255, 0.15) 0%, rgba(0, 212, 255, 0.05) 100%);
  color: #00d4ff;
}

.zone-tree :deep(.el-tree-node.is-current > .el-tree-node__content) {
  background: linear-gradient(135deg, rgba(0, 212, 255, 0.25) 0%, rgba(0, 150, 200, 0.15) 100%);
  color: #00d4ff;
  box-shadow: 0 2px 8px rgba(0, 212, 255, 0.15);
  font-weight: 500;
}

.zone-tree :deep(.el-tree-node__expand-icon) {
  color: rgba(0, 212, 255, 0.7);
  transition: all 0.3s ease;
}

.zone-tree :deep(.el-tree-node__expand-icon:hover) {
  color: #00d4ff;
}

.zone-tree :deep(.el-tree-node__label) {
  font-size: 14px;
}

.zone-tree :deep(.el-tree--highlight-current .el-tree-node.is-current > .el-tree-node__content) {
  background: linear-gradient(135deg, rgba(0, 212, 255, 0.25) 0%, rgba(0, 150, 200, 0.15) 100%);
}

.zone-tree :deep(.el-tree-node.is-expanded > .el-tree-node__children) {
  padding-left: 8px;
}

.main-content {
  background: #0c1929;
  padding: 0;
  overflow-y: auto;
}
</style>
