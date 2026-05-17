<template>
  <div class="record-root">
    <el-card class="main-card" shadow="never">
      <template #header>
        <div class="card-header">
          <span class="title">
            <el-icon><Document /></el-icon>养殖记录管理
          </span>
          <span v-if="!zoneId" class="zone-warning">请先选择养殖区域</span>
        </div>
      </template>

      <el-tabs v-model="activeTab" class="record-tabs">
        <el-tab-pane label="苗种投放" name="seedling" lazy>
          <div class="action-bar">
            <el-button type="primary" @click="showSeedlingDialog" :disabled="!zoneId" class="add-btn">
              <el-icon><Plus /></el-icon>添加记录
            </el-button>
          </div>
          <el-table :data="seedlingList" class="data-table" v-loading="loading" empty-text="暂无数据">
            <el-table-column prop="species" label="品种" width="120" />
            <el-table-column prop="batchNo" label="批次号" width="160" />
            <el-table-column prop="quantity" label="数量" width="120" />
            <el-table-column prop="source" label="来源" />
            <el-table-column prop="releaseDate" label="投放日期" width="120" />
            <el-table-column prop="remarks" label="备注" show-overflow-tooltip />
          </el-table>
        </el-tab-pane>

        <el-tab-pane label="饲料投喂" name="feeding" lazy>
          <div class="action-bar">
            <el-button type="primary" @click="showFeedingDialog" :disabled="!zoneId" class="add-btn">
              <el-icon><Plus /></el-icon>添加记录
            </el-button>
          </div>
          <el-table :data="feedingList" class="data-table" v-loading="loading" empty-text="暂无数据">
            <el-table-column prop="species" label="品种" width="120" />
            <el-table-column prop="batchNo" label="批次号" width="160" />
            <el-table-column prop="feedType" label="饲料类型" width="150" />
            <el-table-column prop="quantity" label="投喂量" width="120" />
            <el-table-column prop="feedingTime" label="投喂时间" width="180" />
            <el-table-column prop="remarks" label="备注" show-overflow-tooltip />
          </el-table>
        </el-tab-pane>

        <el-tab-pane label="捕捞记录" name="harvest" lazy>
          <div class="action-bar">
            <el-button type="primary" @click="showHarvestDialog" :disabled="!zoneId" class="add-btn">
              <el-icon><Plus /></el-icon>添加记录
            </el-button>
          </div>
          <el-table :data="harvestList" class="data-table" v-loading="loading" empty-text="暂无数据">
            <el-table-column prop="species" label="品种" width="120" />
            <el-table-column prop="batchNo" label="批次号" width="160" />
            <el-table-column prop="quantity" label="数量" width="120" />
            <el-table-column prop="avgWeight" label="平均重量" width="120" />
            <el-table-column prop="harvestDate" label="捕捞日期" width="120" />
            <el-table-column prop="remarks" label="备注" show-overflow-tooltip />
          </el-table>
        </el-tab-pane>
      </el-tabs>
    </el-card>

    <el-dialog v-model="seedlingDialogVisible" title="添加苗种记录" width="500px" class="add-dialog">
      <el-form :model="seedlingForm" label-width="100px" class="add-form">
        <el-form-item label="品种">
          <el-input v-model="seedlingForm.species" class="full-width" />
        </el-form-item>
        <el-form-item label="批次号">
          <el-input v-model="seedlingForm.batchNo" class="full-width" placeholder="自动生成，可修改">
            <template #append>
              <el-button @click="generateBatchNo">生成</el-button>
            </template>
          </el-input>
        </el-form-item>
        <el-form-item label="数量">
          <el-input-number v-model="seedlingForm.quantity" :min="0" class="full-width" />
        </el-form-item>
        <el-form-item label="来源">
          <el-input v-model="seedlingForm.source" class="full-width" />
        </el-form-item>
        <el-form-item label="投放日期">
          <el-date-picker v-model="seedlingForm.releaseDate" type="date" value-format="YYYY-MM-DD" class="full-width" />
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="seedlingForm.remarks" type="textarea" :rows="2" class="full-width" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="seedlingDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="addSeedling">确定</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="feedingDialogVisible" title="添加投喂记录" width="500px" class="add-dialog">
      <el-form :model="feedingForm" label-width="100px" class="add-form">
        <el-form-item label="批次号">
          <el-select v-model="feedingForm.batchNo" class="full-width" placeholder="请选择批次" @change="handleFeedingBatchChange" filterable>
            <el-option v-for="seedling in seedlingList" :key="seedling.batchNo" :label="seedling.batchNo" :value="seedling.batchNo" />
          </el-select>
        </el-form-item>
        <el-form-item label="品种">
          <el-input v-model="feedingForm.species" class="full-width" disabled />
        </el-form-item>
        <el-form-item label="饲料类型">
          <el-input v-model="feedingForm.feedType" class="full-width" />
        </el-form-item>
        <el-form-item label="投喂量">
          <el-input-number v-model="feedingForm.quantity" :min="0" class="full-width" />
        </el-form-item>
        <el-form-item label="投喂时间">
          <el-date-picker v-model="feedingForm.feedingTime" type="datetime" value-format="YYYY-MM-DD HH:mm:ss" class="full-width" />
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="feedingForm.remarks" type="textarea" :rows="2" class="full-width" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="feedingDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="addFeeding">确定</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="harvestDialogVisible" title="添加捕捞记录" width="500px" class="add-dialog">
      <el-form :model="harvestForm" label-width="100px" class="add-form">
        <el-form-item label="批次号">
          <el-select v-model="harvestForm.batchNo" class="full-width" placeholder="请选择批次" @change="handleHarvestBatchChange" filterable>
            <el-option v-for="seedling in seedlingList" :key="seedling.batchNo" :label="seedling.batchNo" :value="seedling.batchNo" />
          </el-select>
        </el-form-item>
        <el-form-item label="品种">
          <el-input v-model="harvestForm.species" class="full-width" disabled />
        </el-form-item>
        <el-form-item label="数量">
          <el-input-number v-model="harvestForm.quantity" :min="0" class="full-width" />
        </el-form-item>
        <el-form-item label="平均重量">
          <el-input-number v-model="harvestForm.avgWeight" :min="0" class="full-width" />
        </el-form-item>
        <el-form-item label="捕捞日期">
          <el-date-picker v-model="harvestForm.harvestDate" type="date" value-format="YYYY-MM-DD" class="full-width" />
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="harvestForm.remarks" type="textarea" :rows="2" class="full-width" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="harvestDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="addHarvest">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import { ref, watch, onMounted } from 'vue'
import { getSeedlingsByZone, getFeedingByZone, getHarvestByZone, createSeedling, createFeeding, createHarvest } from '@/api/record'
import { ElMessage } from 'element-plus'
import { Document, Plus } from '@element-plus/icons-vue'

export default {
  name: 'AquacultureRecord',
  components: { Document, Plus },
  props: ['zoneId'],
  setup(props) {
    const activeTab = ref('seedling')
    const seedlingList = ref([])
    const feedingList = ref([])
    const harvestList = ref([])
    const loading = ref(false)
    const seedlingDialogVisible = ref(false)
    const feedingDialogVisible = ref(false)
    const harvestDialogVisible = ref(false)
    const seedlingForm = ref({})
    const feedingForm = ref({})
    const harvestForm = ref({})

    const loadData = async () => {
      if (!props.zoneId) { seedlingList.value = []; feedingList.value = []; harvestList.value = []; return }
      loading.value = true
      try {
        const [seedlingRes, feedingRes, harvestRes] = await Promise.all([
          getSeedlingsByZone(props.zoneId), getFeedingByZone(props.zoneId), getHarvestByZone(props.zoneId)
        ])
        seedlingList.value = seedlingRes.data || []
        feedingList.value = feedingRes.data || []
        harvestList.value = harvestRes.data || []
      } catch (e) { ElMessage.error('加载失败') } finally { loading.value = false }
    }

    const showSeedlingDialog = () => {
      const now = new Date()
      const batchNo = `B${props.zoneId}${now.getFullYear()}${String(now.getMonth()+1).padStart(2,'0')}${String(now.getDate()).padStart(2,'0')}${String(now.getHours()).padStart(2,'0')}${String(now.getMinutes()).padStart(2,'0')}`
      seedlingForm.value = { species: '', batchNo, quantity: null, source: '', releaseDate: '', remarks: '' }
      seedlingDialogVisible.value = true
    }

    const generateBatchNo = () => {
      const now = new Date()
      seedlingForm.value.batchNo = `B${props.zoneId}${now.getFullYear()}${String(now.getMonth()+1).padStart(2,'0')}${String(now.getDate()).padStart(2,'0')}${String(now.getHours()).padStart(2,'0')}${String(now.getMinutes()).padStart(2,'0')}`
    }

    const showFeedingDialog = () => { feedingForm.value = { species: '', batchNo: '', feedType: '', quantity: null, feedingTime: '', remarks: '' }; feedingDialogVisible.value = true }
    const showHarvestDialog = () => { harvestForm.value = { species: '', batchNo: '', quantity: null, avgWeight: null, harvestDate: '', remarks: '' }; harvestDialogVisible.value = true }
    const handleFeedingBatchChange = (batchNo) => { const s = seedlingList.value.find(s => s.batchNo === batchNo); if (s) feedingForm.value.species = s.species }
    const handleHarvestBatchChange = (batchNo) => { const s = seedlingList.value.find(s => s.batchNo === batchNo); if (s) harvestForm.value.species = s.species }

    const addSeedling = async () => {
      if (!seedlingForm.value.species) { ElMessage.warning('请输入品种'); return }
      if (!seedlingForm.value.quantity) { ElMessage.warning('请输入数量'); return }
      try { await createSeedling({ ...seedlingForm.value, zoneId: props.zoneId }); seedlingDialogVisible.value = false; ElMessage.success('添加成功'); loadData() } catch (e) { ElMessage.error('添加失败') }
    }

    const addFeeding = async () => {
      if (!feedingForm.value.batchNo) { ElMessage.warning('请选择批次号'); return }
      if (!feedingForm.value.feedType) { ElMessage.warning('请输入饲料类型'); return }
      if (!feedingForm.value.quantity) { ElMessage.warning('请输入投喂量'); return }
      if (!feedingForm.value.feedingTime) { ElMessage.warning('请选择投喂时间'); return }
      try {
        await createFeeding({ zoneId: props.zoneId, species: feedingForm.value.species, batchNo: feedingForm.value.batchNo, feedType: feedingForm.value.feedType, quantity: feedingForm.value.quantity, feedingTime: feedingForm.value.feedingTime, remarks: feedingForm.value.remarks })
        feedingDialogVisible.value = false; ElMessage.success('添加成功'); loadData()
      } catch (e) { ElMessage.error('添加失败: ' + (e.response?.data?.message || e.message)) }
    }

    const addHarvest = async () => {
      if (!harvestForm.value.batchNo) { ElMessage.warning('请选择批次号'); return }
      if (!harvestForm.value.quantity) { ElMessage.warning('请输入数量'); return }
      if (!harvestForm.value.harvestDate) { ElMessage.warning('请选择捕捞日期'); return }
      try { await createHarvest({ ...harvestForm.value, zoneId: props.zoneId }); harvestDialogVisible.value = false; ElMessage.success('添加成功'); loadData() } catch (e) { ElMessage.error('添加失败') }
    }

    watch(() => props.zoneId, (v) => { if (v) loadData(); else { seedlingList.value = []; feedingList.value = []; harvestList.value = [] } })
    onMounted(() => { loadData() })

    return { activeTab, seedlingList, feedingList, harvestList, loading, seedlingDialogVisible, feedingDialogVisible, harvestDialogVisible, seedlingForm, feedingForm, harvestForm, showSeedlingDialog, showFeedingDialog, showHarvestDialog, generateBatchNo, addSeedling, addFeeding, addHarvest, handleFeedingBatchChange, handleHarvestBatchChange }
  }
}
</script>

<style scoped>
.record-root { padding: 20px; }
.main-card {
  background: rgba(12, 22, 40, 0.5) !important;
  border: 1px solid rgba(80, 150, 220, 0.08) !important;
  border-radius: 16px;
  box-shadow: none;
}
.main-card :deep(.el-card__header) { border-bottom: 1px solid rgba(80, 150, 220, 0.06); padding: 16px 20px; }
.main-card :deep(.el-card__body) { padding: 20px; }
.card-header { display: flex; justify-content: space-between; align-items: center; }
.title { display: flex; align-items: center; gap: 8px; color: #c8ddf8; font-size: 16px; font-weight: 600; }
.title .el-icon { font-size: 18px; color: #5b9bd5; }
.zone-warning { color: #ff8a65; font-size: 13px; }

.record-tabs :deep(.el-tabs__header) { border-bottom-color: rgba(80, 150, 220, 0.06); margin-bottom: 16px; }
.record-tabs :deep(.el-tabs__item) { color: rgba(255,255,255,0.6) !important; font-size: 14px; font-weight: 500; transition: all 0.3s; }
.record-tabs :deep(.el-tabs__item:hover) { color: rgba(255,255,255,0.85) !important; }
.record-tabs :deep(.el-tabs__item.is-active) { color: #5b9bd5 !important; font-weight: 600; }
.record-tabs :deep(.el-tabs__active-bar) { background: linear-gradient(90deg, #2196f3, #5b9bd5); }
.record-tabs :deep(.el-tabs__nav-wrap::after) { display: none; }

.action-bar { margin-bottom: 16px; }
.add-btn { background: rgba(33, 150, 243, 0.15) !important; border: 1px solid rgba(33, 150, 243, 0.25) !important; color: #64b5f6 !important; border-radius: 10px; transition: all 0.3s; }
.add-btn:hover { background: rgba(33, 150, 243, 0.25) !important; }
.add-btn:disabled { opacity: 0.4; }

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
.data-table :deep(.el-table__cell .cell) {
  color: #d6e8f8 !important;
}
.data-table :deep(.el-table__row:hover > td) {
  background: #132742 !important;
}
.data-table :deep(.el-table__empty-block) { background: #0c1628 !important; }
.data-table :deep(.el-table__empty-text) { color: rgba(255,255,255,0.4); }
.data-table :deep(.el-loading-mask) { background: rgba(12, 22, 40, 0.8) !important; }

.full-width { width: 100%; }
</style>

<style>
.add-dialog {
  --el-dialog-bg-color: #0f1c2e;
}
.add-dialog .el-dialog {
  background: #0f1c2e;
  border: 1px solid rgba(80, 150, 220, 0.15);
  border-radius: 16px;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.6);
}
.add-dialog .el-dialog__header {
  padding: 20px 24px 0;
  border-bottom: none;
}
.add-dialog .el-dialog__title {
  color: #d6e8f8;
  font-size: 18px;
  font-weight: 600;
}
.add-dialog .el-dialog__close {
  color: rgba(255, 255, 255, 0.5);
}
.add-dialog .el-dialog__close:hover {
  color: #5b9bd5;
}
.add-dialog .el-dialog__body {
  padding: 20px 24px;
}
.add-dialog .el-dialog__footer {
  padding: 0 24px 20px;
  border-top: none;
}
.add-dialog .el-form-item__label {
  color: #a0c8e8 !important;
  font-weight: 500;
  font-size: 14px;
}
.add-dialog .el-input__wrapper {
  background: rgba(255, 255, 255, 0.06) !important;
  box-shadow: none !important;
  border: 1px solid rgba(80, 150, 220, 0.15);
  border-radius: 8px;
}
.add-dialog .el-input__wrapper:hover {
  border-color: rgba(80, 150, 220, 0.3);
}
.add-dialog .el-input__wrapper.is-focus {
  border-color: #5b9bd5 !important;
  box-shadow: 0 0 0 2px rgba(91, 155, 213, 0.15) !important;
}
.add-dialog .el-input__inner {
  color: #d6e8f8 !important;
  font-size: 14px;
}
.add-dialog .el-input__inner::placeholder {
  color: rgba(255, 255, 255, 0.3) !important;
}
.add-dialog .el-input.is-disabled .el-input__wrapper {
  background: rgba(255, 255, 255, 0.03) !important;
  opacity: 0.6;
}
.add-dialog .el-input-number .el-input__wrapper {
  width: 100%;
}
.add-dialog .el-select .el-input__wrapper {
  background: rgba(255, 255, 255, 0.06) !important;
  box-shadow: none !important;
  border: 1px solid rgba(80, 150, 220, 0.15);
}
.add-dialog .el-select .el-input__inner {
  color: #d6e8f8 !important;
}
.add-dialog .el-textarea__inner {
  background: rgba(255, 255, 255, 0.06) !important;
  border: 1px solid rgba(80, 150, 220, 0.15);
  color: #d6e8f8 !important;
  border-radius: 8px;
}
.add-dialog .el-textarea__inner:focus {
  border-color: #5b9bd5 !important;
  box-shadow: 0 0 0 2px rgba(91, 155, 213, 0.15) !important;
}
.add-dialog .el-textarea__inner::placeholder {
  color: rgba(255, 255, 255, 0.3) !important;
}
.add-dialog .el-date-editor .el-input__wrapper {
  background: rgba(255, 255, 255, 0.06) !important;
  box-shadow: none !important;
  border: 1px solid rgba(80, 150, 220, 0.15);
}
.add-dialog .el-button {
  border-radius: 8px;
}
.add-dialog .el-button--default {
  background: rgba(255, 255, 255, 0.06) !important;
  border: 1px solid rgba(255, 255, 255, 0.1) !important;
  color: #a0c8e0 !important;
}
.add-dialog .el-button--default:hover {
  background: rgba(255, 255, 255, 0.1) !important;
  border-color: rgba(255, 255, 255, 0.2) !important;
  color: #d6e8f8 !important;
}
.add-dialog .el-button--primary {
  background: linear-gradient(135deg, #5b9bd5, #2196f3);
  border: none;
  box-shadow: 0 4px 15px rgba(33, 150, 243, 0.25);
}
.add-dialog .el-select-dropdown {
  background: #0f1c2e !important;
  border: 1px solid rgba(80, 150, 220, 0.15) !important;
}
.add-dialog .el-select-dropdown__item {
  color: #d6e8f8 !important;
}
.add-dialog .el-select-dropdown__item.hover,
.add-dialog .el-select-dropdown__item:hover {
  background: rgba(80, 150, 220, 0.1) !important;
}
.add-dialog .el-select-dropdown__item.selected {
  color: #5b9bd5 !important;
  font-weight: 600;
}
.add-dialog .el-popper.is-light {
  background: #0f1c2e !important;
  border: 1px solid rgba(80, 150, 220, 0.15) !important;
  color: #d6e8f8 !important;
}
.add-dialog .el-picker-panel {
  background: #0f1c2e !important;
  border: 1px solid rgba(80, 150, 220, 0.15) !important;
  color: #d6e8f8 !important;
}
</style>