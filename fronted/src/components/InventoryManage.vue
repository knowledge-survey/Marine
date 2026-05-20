<template>
  <div class="comp-root">
    <div class="comp-card">
      <div class="comp-header">
        <div class="header-left">
          <el-icon class="header-icon"><Box /></el-icon>
          <span class="header-title">库存管理</span>
        </div>
      </div>

      <el-row :gutter="16" class="stats-row">
        <el-col :span="8" v-for="s in statsItems" :key="s.key">
          <div class="stat-box" :class="s.cls" @click="activeTab = s.key">
            <div class="stat-inner">
              <el-icon class="stat-icon" :size="28"><component :is="s.icon" /></el-icon>
              <div class="stat-body">
                <span class="stat-val">{{ s.count }}</span>
                <span class="stat-lbl">{{ s.label }}</span>
              </div>
            </div>
          </div>
        </el-col>
      </el-row>

      <el-tabs v-model="activeTab" class="inv-tabs">
        <el-tab-pane label="饲料库存" name="feed">
          <FeedInventory />
        </el-tab-pane>
        <el-tab-pane label="苗种库存" name="seedling">
          <SeedlingInventory />
        </el-tab-pane>
        <el-tab-pane label="药品库存" name="medicine">
          <MedicineInventory />
        </el-tab-pane>
      </el-tabs>
    </div>
  </div>
</template>

<script>
import { ref } from 'vue'
import { Box, Coin, Present, Orange } from '@element-plus/icons-vue'
import FeedInventory from './FeedInventory.vue'
import SeedlingInventory from './SeedlingInventory.vue'
import MedicineInventory from './MedicineInventory.vue'

export default {
  name: 'InventoryManage',
  components: { FeedInventory, SeedlingInventory, MedicineInventory, Box, Coin, Present, Orange },
  setup() {
    const activeTab = ref('feed')
    const statsItems = [
      { key: 'feed', label: '饲料库存', icon: 'Coin', cls: 's-feed', count: '管理饲料' },
      { key: 'seedling', label: '苗种库存', icon: 'Present', cls: 's-seed', count: '管理苗种' },
      { key: 'medicine', label: '药品库存', icon: 'Orange', cls: 's-med', count: '管理药品' }
    ]
    return { activeTab, statsItems }
  }
}
</script>

<style scoped>
.comp-root { padding: 0; }
.comp-card { background: rgba(12,22,40,0.5); border: 1px solid rgba(80,150,220,0.08); border-radius: 16px; padding: 20px; }
.comp-header { display: flex; align-items: center; gap: 10px; margin-bottom: 20px; }
.header-icon { font-size: 22px; color: #5b9bd5; }
.header-title { font-size: 17px; font-weight: 700; color: #d6e8f8; }

.stats-row { margin-bottom: 16px; }
.stat-box { background: rgba(255,255,255,0.03); border: 1px solid rgba(80,150,220,0.08); border-radius: 14px; padding: 14px 16px; cursor: pointer; transition: all 0.3s; }
.stat-box:hover { background: rgba(80,150,220,0.05); border-color: rgba(80,150,220,0.2); transform: translateY(-1px); }
.stat-box.s-feed { border-left: 3px solid #5b9bd5; }
.stat-box.s-seed { border-left: 3px solid #00ff88; }
.stat-box.s-med { border-left: 3px solid #ffcc00; }
.stat-inner { display: flex; align-items: center; gap: 12px; }
.s-feed .stat-icon { color: #5b9bd5; }
.s-seed .stat-icon { color: #00ff88; }
.s-med .stat-icon { color: #ffcc00; }
.stat-body { display: flex; flex-direction: column; gap: 2px; }
.stat-val { font-size: 16px; font-weight: 700; color: #e8f2fc; }
.stat-lbl { font-size: 12px; color: rgba(255,255,255,0.4); }

.inv-tabs :deep(.el-tabs__header) { margin: 0; border-bottom: 1px solid rgba(80,150,220,0.08); }
.inv-tabs :deep(.el-tabs__item) { color: rgba(255,255,255,0.4) !important; }
.inv-tabs :deep(.el-tabs__item.is-active) { color: #5b9bd5 !important; }
.inv-tabs :deep(.el-tabs__active-bar) { background: linear-gradient(90deg, #2196f3, #5b9bd5); }
.inv-tabs :deep(.el-tab-pane) { padding-top: 0; }
</style>