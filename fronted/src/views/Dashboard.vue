<template>
  <div class="dashboard-root">
    <div class="dash-header">
      <div class="header-left">
        <div class="brand">
          <el-icon class="brand-icon"><Ship /></el-icon>
          <span class="brand-text">海洋养殖管理系统</span>
        </div>
      </div>
      <div class="header-right">
        <el-button class="hdr-btn" @click="$router.push('/admin')" v-if="isAdmin">
          <el-icon><Setting /></el-icon>
          <span class="hdr-btn-text">管理后台</span>
        </el-button>
        <el-button class="hdr-btn logout" @click="logout">
          <el-icon><SwitchButton /></el-icon>
          <span class="hdr-btn-text">退出登录</span>
        </el-button>
      </div>
    </div>
    <div class="dash-body">
      <div class="dash-aside">
        <div class="menu-section">
          <el-menu :default-active="activeMenu" @select="handleMenuSelect" class="side-menu">
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
            <el-menu-item index="weather">
              <el-icon><PartlyCloudy /></el-icon>
              <span>天气预报</span>
            </el-menu-item>
          </el-menu>
        </div>
        <div class="zone-section">
          <div class="zone-title">养殖区域</div>
          <el-tree
            :data="zoneTree"
            :props="{ label: 'name', children: 'children' }"
            @node-click="handleZoneClick"
            node-key="id"
            :default-expanded-keys="[currentZoneId]"
            :highlight-current="true"
            class="zone-tree"
          />
        </div>
      </div>
      <div class="dash-main">
        <component :is="currentComponent" :zoneId="currentZoneId" />
      </div>
    </div>
  </div>
</template>

<script>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { getZones } from '@/api/zone'
import WaterQuality from '@/components/WaterQuality.vue'
import AquacultureRecord from '@/components/AquacultureRecord.vue'
import DiseaseControl from '@/components/DiseaseControl.vue'
import WeatherForecast from '@/components/WeatherForecast.vue'
import { TrendCharts, Document, Warning, PartlyCloudy, Ship, Setting, SwitchButton } from '@element-plus/icons-vue'

export default {
  name: 'Dashboard',
  components: { WaterQuality, AquacultureRecord, DiseaseControl, WeatherForecast, TrendCharts, Document, Warning, PartlyCloudy, Ship, Setting, SwitchButton },
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
          .map(z => ({ ...z, children: buildTree(z.id) }))
      }
      return buildTree()
    })

    const currentComponent = computed(() => {
      switch (activeMenu.value) {
        case 'water': return 'WaterQuality'
        case 'record': return 'AquacultureRecord'
        case 'disease': return 'DiseaseControl'
        case 'weather': return 'WeatherForecast'
        default: return 'WaterQuality'
      }
    })

    const loadZones = async () => {
      try {
        const res = await getZones()
        zones.value = res.data
        if (zones.value.length > 0) currentZoneId.value = zones.value[0].id
      } catch (error) { console.error(error) }
    }

    const handleMenuSelect = (key) => { activeMenu.value = key }
    const handleZoneClick = (data) => { currentZoneId.value = data.id }
    const logout = () => { localStorage.removeItem('user'); router.push('/login') }

    onMounted(() => { loadZones() })

    return { activeMenu, zoneTree, currentZoneId, currentComponent, isAdmin, handleMenuSelect, handleZoneClick, logout }
  }
}
</script>

<style scoped>
.dashboard-root {
  min-height: 100vh;
  background: linear-gradient(170deg, #060d1a 0%, #0d1b2a 25%, #132742 50%, #0f1f35 75%, #080f1e 100%);
  display: flex;
  flex-direction: column;
}

.dash-header {
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

.brand {
  display: flex;
  align-items: center;
  gap: 10px;
}

.brand-icon {
  font-size: 22px;
  color: #5b9bd5;
  filter: drop-shadow(0 0 8px rgba(91, 155, 213, 0.5));
}

.brand-text {
  font-size: 18px;
  font-weight: 700;
  color: #d6e8f8;
  letter-spacing: 1.5px;
}

.header-right { display: flex; gap: 10px; }

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

.hdr-btn.logout { border-color: rgba(255, 107, 53, 0.2) !important; color: #ff8a65 !important; }
.hdr-btn.logout:hover { background: rgba(255, 107, 53, 0.12) !important; }

.hdr-btn-text { margin-left: 5px; }

.dash-body {
  display: flex;
  flex: 1;
  overflow: hidden;
}

.dash-aside {
  width: 250px;
  background: rgba(8, 18, 32, 0.7);
  border-right: 1px solid rgba(80, 150, 220, 0.06);
  display: flex;
  flex-direction: column;
  flex-shrink: 0;
}

.menu-section {
  padding: 16px 12px 8px;
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

.zone-section {
  flex: 1;
  padding: 0 12px 16px;
  overflow-y: auto;
}

.zone-title {
  font-size: 11px;
  font-weight: 600;
  color: rgba(80, 150, 220, 0.5);
  text-transform: uppercase;
  letter-spacing: 1px;
  padding: 12px 12px 8px;
  border-top: 1px solid rgba(80, 150, 220, 0.06);
}

.zone-tree {
  background: transparent;
}

.zone-tree :deep(.el-tree-node__content) {
  color: rgba(255, 255, 255, 0.65);
  border-radius: 8px;
  padding: 6px 10px;
  margin: 1px 0;
  height: auto;
  background: transparent;
  transition: all 0.25s;
}

.zone-tree :deep(.el-tree-node__content:hover) {
  background: rgba(80, 150, 220, 0.08);
  color: #8cb8e0;
}

.zone-tree :deep(.el-tree-node.is-current > .el-tree-node__content) {
  background: rgba(33, 120, 220, 0.15);
  color: #5b9bd5;
  font-weight: 500;
}

.zone-tree :deep(.el-tree-node__expand-icon) {
  color: rgba(80, 150, 220, 0.5);
}

.dash-main {
  flex: 1;
  overflow-y: auto;
  background: transparent;
}

@media (max-width: 768px) {
  .dash-aside { display: none; }
  .hdr-btn-text { display: none; }
}
</style>