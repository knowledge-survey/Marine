<template>
  <div class="record-container">
    <el-card class="main-card">
      <template #header>
        <div class="card-header">
          <span class="title">养殖记录管理</span>
          <span v-if="!zoneId" class="zone-warning">请先选择养殖区域</span>
        </div>
      </template>

      <el-tabs v-model="activeTab" class="record-tabs">
        <el-tab-pane label="苗种投放" name="seedling" lazy>
          <div class="action-bar">
            <el-button type="primary" @click="showSeedlingDialog" :disabled="!zoneId" class="add-btn">
              添加记录
            </el-button>
          </div>
          <el-table :data="seedlingList" border class="data-table" v-loading="loading">
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
              添加记录
            </el-button>
          </div>
          <el-table :data="feedingList" border class="data-table" v-loading="loading">
            <el-table-column prop="feedType" label="饲料类型" width="150" />
            <el-table-column prop="quantity" label="投喂量" width="120" />
            <el-table-column prop="feedingTime" label="投喂时间" width="180" />
            <el-table-column prop="remarks" label="备注" show-overflow-tooltip />
          </el-table>
        </el-tab-pane>

        <el-tab-pane label="捕捞记录" name="harvest" lazy>
          <div class="action-bar">
            <el-button type="primary" @click="showHarvestDialog" :disabled="!zoneId" class="add-btn">
              添加记录
            </el-button>
          </div>
          <el-table :data="harvestList" border class="data-table" v-loading="loading">
            <el-table-column prop="species" label="品种" width="120" />
            <el-table-column prop="quantity" label="数量" width="120" />
            <el-table-column prop="avgWeight" label="平均重量" width="120" />
            <el-table-column prop="harvestDate" label="捕捞日期" width="120" />
            <el-table-column prop="remarks" label="备注" show-overflow-tooltip />
          </el-table>
        </el-tab-pane>
      </el-tabs>
    </el-card>

    <el-dialog v-model="seedlingDialogVisible" title="添加苗种记录" width="500px" custom-class="aquaculture-add-dialog">
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

    <el-dialog v-model="feedingDialogVisible" title="添加投喂记录" width="500px" custom-class="aquaculture-add-dialog">
      <el-form :model="feedingForm" label-width="100px" class="add-form">
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

    <el-dialog v-model="harvestDialogVisible" title="添加捕捞记录" width="500px" custom-class="aquaculture-add-dialog">
      <el-form :model="harvestForm" label-width="100px" class="add-form">
        <el-form-item label="品种">
          <el-input v-model="harvestForm.species" class="full-width" />
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

export default {
  name: 'AquacultureRecord',
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
      if (!props.zoneId) {
        seedlingList.value = []
        feedingList.value = []
        harvestList.value = []
        return
      }
      loading.value = true
      try {
        const [seedlingRes, feedingRes, harvestRes] = await Promise.all([
          getSeedlingsByZone(props.zoneId),
          getFeedingByZone(props.zoneId),
          getHarvestByZone(props.zoneId)
        ])
        seedlingList.value = seedlingRes.data || []
        feedingList.value = feedingRes.data || []
        harvestList.value = harvestRes.data || []
      } catch (error) {
        console.error('加载数据失败:', error)
        ElMessage.error('加载数据失败: ' + (error.response?.data?.message || error.message || '未知错误'))
      } finally {
        loading.value = false
      }
    }

    const showSeedlingDialog = () => {
      if (!props.zoneId) {
        ElMessage.warning('请先选择养殖区域')
        return
      }
      const now = new Date()
      const batchNo = `B${props.zoneId}${now.getFullYear()}${String(now.getMonth() + 1).padStart(2, '0')}${String(now.getDate()).padStart(2, '0')}${String(now.getHours()).padStart(2, '0')}${String(now.getMinutes()).padStart(2, '0')}`
      seedlingForm.value = { 
        species: '', 
        batchNo: batchNo, 
        quantity: null, 
        source: '', 
        releaseDate: '', 
        remarks: '' 
      }
      seedlingDialogVisible.value = true
    }

    const generateBatchNo = () => {
      if (!props.zoneId) return
      const now = new Date()
      const batchNo = `B${props.zoneId}${now.getFullYear()}${String(now.getMonth() + 1).padStart(2, '0')}${String(now.getDate()).padStart(2, '0')}${String(now.getHours()).padStart(2, '0')}${String(now.getMinutes()).padStart(2, '0')}`
      seedlingForm.value.batchNo = batchNo
    }

    const showFeedingDialog = () => {
      if (!props.zoneId) {
        ElMessage.warning('请先选择养殖区域')
        return
      }
      feedingForm.value = { 
        feedType: '', 
        quantity: null, 
        feedingTime: '', 
        remarks: '' 
      }
      feedingDialogVisible.value = true
    }

    const showHarvestDialog = () => {
      if (!props.zoneId) {
        ElMessage.warning('请先选择养殖区域')
        return
      }
      harvestForm.value = { 
        species: '', 
        quantity: null, 
        avgWeight: null, 
        harvestDate: '', 
        remarks: '' 
      }
      harvestDialogVisible.value = true
    }

    const addSeedling = async () => {
      if (!seedlingForm.value.species) {
        ElMessage.warning('请输入品种')
        return
      }
      if (!seedlingForm.value.quantity) {
        ElMessage.warning('请输入数量')
        return
      }
      try {
        await createSeedling({ 
          ...seedlingForm.value, 
          zoneId: props.zoneId 
        })
        seedlingDialogVisible.value = false
        ElMessage.success('添加成功')
        loadData()
      } catch (error) {
        console.error('添加失败:', error)
        ElMessage.error('添加失败: ' + (error.response?.data?.message || error.message))
      }
    }

    const addFeeding = async () => {
      if (!feedingForm.value.feedType) {
        ElMessage.warning('请输入饲料类型')
        return
      }
      if (!feedingForm.value.quantity) {
        ElMessage.warning('请输入投喂量')
        return
      }
      try {
        await createFeeding({ 
          ...feedingForm.value, 
          zoneId: props.zoneId 
        })
        feedingDialogVisible.value = false
        ElMessage.success('添加成功')
        loadData()
      } catch (error) {
        console.error('添加失败:', error)
        ElMessage.error('添加失败: ' + (error.response?.data?.message || error.message))
      }
    }

    const addHarvest = async () => {
      if (!harvestForm.value.species) {
        ElMessage.warning('请输入品种')
        return
      }
      if (!harvestForm.value.quantity) {
        ElMessage.warning('请输入数量')
        return
      }
      try {
        await createHarvest({ 
          ...harvestForm.value, 
          zoneId: props.zoneId 
        })
        harvestDialogVisible.value = false
        ElMessage.success('添加成功')
        loadData()
      } catch (error) {
        console.error('添加失败:', error)
        ElMessage.error('添加失败: ' + (error.response?.data?.message || error.message))
      }
    }

    watch(() => props.zoneId, (newVal) => {
      if (newVal) {
        loadData()
      } else {
        seedlingList.value = []
        feedingList.value = []
        harvestList.value = []
      }
    })

    onMounted(() => {
      loadData()
    })

    return {
      activeTab,
      seedlingList,
      feedingList,
      harvestList,
      loading,
      seedlingDialogVisible,
      feedingDialogVisible,
      harvestDialogVisible,
      seedlingForm,
      feedingForm,
      harvestForm,
      showSeedlingDialog,
      showFeedingDialog,
      showHarvestDialog,
      generateBatchNo,
      addSeedling,
      addFeeding,
      addHarvest
    }
  }
}
</script>

<style scoped>
.record-container {
  background: #0c1929;
  min-height: 100vh;
  padding: 20px;
}

.main-card {
  background: rgba(20, 40, 60, 0.9) !important;
  border: 1px solid rgba(0, 212, 255, 0.3) !important;
}

.main-card :deep(.el-card__header) {
  border-bottom: 1px solid rgba(0, 212, 255, 0.2) !important;
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

.zone-warning {
  color: #ff6b35;
  font-size: 14px;
}

.record-tabs :deep(.el-tabs__item) {
  color: rgba(255, 255, 255, 0.7);
}

.record-tabs :deep(.el-tabs__item.is-active) {
  color: #00d4ff;
}

.record-tabs :deep(.el-tabs__active-bar) {
  background-color: #00d4ff;
}

.action-bar {
  margin-bottom: 16px;
}

.add-btn {
  background: linear-gradient(135deg, #00d4ff 0%, #0077ff 100%);
  border: none;
  box-shadow: 0 4px 15px rgba(0, 212, 255, 0.4);
}

.data-table {
  border-radius: 8px;
  overflow: hidden;
}

.data-table :deep(.el-table) {
  background-color: rgba(10, 25, 40, 0.8) !important;
}

.data-table :deep(.el-table::before) {
  background-color: transparent !important;
}

.data-table :deep(.el-table__header-wrapper) {
  background-color: rgba(10, 25, 40, 0.9) !important;
}

.data-table :deep(.el-table__header) {
  background-color: rgba(10, 25, 40, 0.9) !important;
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
}

.data-table :deep(.el-table__body) {
  background-color: rgba(10, 25, 40, 0.8) !important;
}

.data-table :deep(.el-table__row) {
  background-color: rgba(10, 25, 40, 0.8) !important;
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
}

.data-table :deep(.el-table__empty-text) {
  color: rgba(255, 255, 255, 0.5) !important;
}

/* ===== Dialog Dark Theme ===== */
.add-dialog :deep(.el-dialog) {
  background: rgba(20, 40, 60, 0.95) !important;
  border: 1px solid rgba(0, 212, 255, 0.3) !important;
  border-radius: 12px !important;
  backdrop-filter: blur(10px);
}

.add-dialog :deep(.el-overlay) {
  background-color: rgba(0, 0, 0, 0.5) !important;
}

.add-dialog :deep(.el-dialog__header) {
  border-bottom: 1px solid rgba(0, 212, 255, 0.2) !important;
  background: transparent !important;
  padding: 20px;
}

.add-dialog :deep(.el-dialog__title) {
  color: #00d4ff !important;
  font-size: 18px;
  font-weight: 600;
}

.add-dialog :deep(.el-dialog__close) {
  color: rgba(255, 255, 255, 0.6) !important;
  font-size: 18px;
}

.add-dialog :deep(.el-dialog__close:hover) {
  color: #00d4ff !important;
}

.add-dialog :deep(.el-dialog__body) {
  background: transparent !important;
  padding: 20px;
}

.add-dialog :deep(.el-dialog__footer) {
  border-top: 1px solid rgba(0, 212, 255, 0.15) !important;
  background: transparent !important;
  padding: 15px 20px;
}

.add-form :deep(.el-form-item__label) {
  color: rgba(255, 255, 255, 0.9) !important;
  font-size: 14px;
  font-weight: 500;
  line-height: 40px;
  text-align: right;
  padding-right: 12px;
}

.add-form :deep(.el-form-item) {
  margin-bottom: 20px;
}

.add-form :deep(.el-form-item__content) {
  justify-content: flex-start;
}

/* Input fields */
.add-form :deep(.el-input__wrapper) {
  background-color: rgba(10, 25, 40, 0.8) !important;
  border: 1px solid rgba(0, 212, 255, 0.2) !important;
  border-radius: 8px !important;
  box-shadow: none !important;
  transition: all 0.3s ease;
}

.add-form :deep(.el-input__wrapper:hover) {
  border-color: rgba(0, 212, 255, 0.4) !important;
}

.add-form :deep(.el-input__wrapper.is-focus) {
  border-color: #00d4ff !important;
  box-shadow: 0 0 15px rgba(0, 212, 255, 0.2) !important;
}

.add-form :deep(.el-input__inner) {
  color: rgba(255, 255, 255, 0.9) !important;
  font-size: 14px;
}

.add-form :deep(.el-input__inner::placeholder) {
  color: rgba(255, 255, 255, 0.4) !important;
}

/* Textarea */
.add-form :deep(.el-textarea__inner) {
  background-color: rgba(10, 25, 40, 0.8) !important;
  border: 1px solid rgba(0, 212, 255, 0.2) !important;
  border-radius: 8px !important;
  color: rgba(255, 255, 255, 0.9) !important;
  box-shadow: none !important;
  transition: all 0.3s ease;
}

.add-form :deep(.el-textarea__inner:hover) {
  border-color: rgba(0, 212, 255, 0.4) !important;
}

.add-form :deep(.el-textarea__inner:focus) {
  border-color: #00d4ff !important;
  box-shadow: 0 0 15px rgba(0, 212, 255, 0.2) !important;
}

.add-form :deep(.el-textarea__inner::placeholder) {
  color: rgba(255, 255, 255, 0.4) !important;
}

/* Date picker */
.add-form :deep(.el-date-editor.el-input__wrapper) {
  background-color: rgba(10, 25, 40, 0.8) !important;
  border: 1px solid rgba(0, 212, 255, 0.2) !important;
  border-radius: 8px !important;
  box-shadow: none !important;
}

.add-form :deep(.el-date-editor.el-input__wrapper:hover) {
  border-color: rgba(0, 212, 255, 0.4) !important;
}

.add-form :deep(.el-date-editor .el-input__inner) {
  color: rgba(255, 255, 255, 0.9) !important;
}

.add-form :deep(.el-input__icon) {
  color: rgba(0, 212, 255, 0.6) !important;
}

/* Number input */
.add-form :deep(.el-input-number__decrease),
.add-form :deep(.el-input-number__increase) {
  background-color: rgba(0, 212, 255, 0.1) !important;
  color: #00d4ff !important;
  border-color: rgba(0, 212, 255, 0.2) !important;
}

.add-form :deep(.el-input-number__decrease:hover),
.add-form :deep(.el-input-number__increase:hover) {
  background-color: rgba(0, 212, 255, 0.2) !important;
  color: #00d4ff !important;
}

.add-form :deep(.el-input-number .el-input__wrapper) {
  background-color: rgba(10, 25, 40, 0.8) !important;
  border: 1px solid rgba(0, 212, 255, 0.2) !important;
}

/* Input group append (for batch number generator) */
.add-form :deep(.el-input-group__append) {
  background-color: rgba(0, 212, 255, 0.1) !important;
  border-color: rgba(0, 212, 255, 0.2) !important;
}

.add-form :deep(.el-input-group__append .el-button) {
  background: linear-gradient(135deg, #00d4ff 0%, #0077ff 100%) !important;
  border: none !important;
  color: #fff !important;
  border-radius: 0 6px 6px 0 !important;
}

/* Buttons */
.add-dialog :deep(.el-button) {
  border-radius: 8px !important;
  padding: 8px 20px;
  font-weight: 500;
}

.add-dialog :deep(.el-button--default) {
  background: rgba(255, 255, 255, 0.1) !important;
  border: 1px solid rgba(255, 255, 255, 0.2) !important;
  color: rgba(255, 255, 255, 0.8) !important;
}

.add-dialog :deep(.el-button--default:hover) {
  background: rgba(255, 255, 255, 0.15) !important;
  color: #fff !important;
  border-color: rgba(255, 255, 255, 0.3) !important;
}

.add-dialog :deep(.el-button--primary) {
  background: linear-gradient(135deg, #00d4ff 0%, #0077ff 100%) !important;
  border: none !important;
  color: #fff !important;
  box-shadow: 0 4px 15px rgba(0, 212, 255, 0.4) !important;
}

.add-dialog :deep(.el-button--primary:hover) {
  box-shadow: 0 6px 20px rgba(0, 212, 255, 0.6) !important;
  transform: translateY(-1px);
}

.full-width {
  width: 100%;
}
</style>

<style>
/* Global dialog styles - must be unscoped for teleported dialogs */
.aquaculture-add-dialog .el-dialog {
  background: rgba(20, 40, 60, 0.95) !important;
  border: 1px solid rgba(0, 212, 255, 0.3) !important;
  border-radius: 12px !important;
  backdrop-filter: blur(10px);
}

.aquaculture-add-dialog .el-overlay {
  background-color: rgba(0, 0, 0, 0.5) !important;
}

.aquaculture-add-dialog .el-dialog__header {
  border-bottom: 1px solid rgba(0, 212, 255, 0.2) !important;
  background: transparent !important;
  padding: 20px !important;
}

.aquaculture-add-dialog .el-dialog__title {
  color: #00d4ff !important;
  font-size: 18px !important;
  font-weight: 600 !important;
}

.aquaculture-add-dialog .el-dialog__close {
  color: rgba(255, 255, 255, 0.6) !important;
  font-size: 18px !important;
}

.aquaculture-add-dialog .el-dialog__close:hover {
  color: #00d4ff !important;
}

.aquaculture-add-dialog .el-dialog__body {
  background: transparent !important;
  padding: 20px !important;
  color: rgba(255, 255, 255, 0.9);
}

.aquaculture-add-dialog .el-dialog__footer {
  border-top: 1px solid rgba(0, 212, 255, 0.15) !important;
  background: transparent !important;
  padding: 15px 20px !important;
}

.aquaculture-add-dialog .el-form-item__label {
  color: rgba(255, 255, 255, 0.9) !important;
  font-size: 14px !important;
  font-weight: 500 !important;
  line-height: 40px !important;
  text-align: right !important;
  padding-right: 12px !important;
}

.aquaculture-add-dialog .el-input__wrapper {
  background-color: rgba(10, 25, 40, 0.8) !important;
  border: 1px solid rgba(0, 212, 255, 0.2) !important;
  border-radius: 8px !important;
  box-shadow: none !important;
}

.aquaculture-add-dialog .el-input__wrapper:hover {
  border-color: rgba(0, 212, 255, 0.4) !important;
}

.aquaculture-add-dialog .el-input__wrapper.is-focus {
  border-color: #00d4ff !important;
  box-shadow: 0 0 15px rgba(0, 212, 255, 0.2) !important;
}

.aquaculture-add-dialog .el-input__inner {
  color: rgba(255, 255, 255, 0.9) !important;
  font-size: 14px !important;
}

.aquaculture-add-dialog .el-input__inner::placeholder {
  color: rgba(255, 255, 255, 0.4) !important;
}

.aquaculture-add-dialog .el-textarea__inner {
  background-color: rgba(10, 25, 40, 0.8) !important;
  border: 1px solid rgba(0, 212, 255, 0.2) !important;
  border-radius: 8px !important;
  color: rgba(255, 255, 255, 0.9) !important;
  box-shadow: none !important;
}

.aquaculture-add-dialog .el-textarea__inner:hover {
  border-color: rgba(0, 212, 255, 0.4) !important;
}

.aquaculture-add-dialog .el-textarea__inner:focus {
  border-color: #00d4ff !important;
  box-shadow: 0 0 15px rgba(0, 212, 255, 0.2) !important;
}

.aquaculture-add-dialog .el-textarea__inner::placeholder {
  color: rgba(255, 255, 255, 0.4) !important;
}

.aquaculture-add-dialog .el-date-editor.el-input__wrapper {
  background-color: rgba(10, 25, 40, 0.8) !important;
  border: 1px solid rgba(0, 212, 255, 0.2) !important;
  border-radius: 8px !important;
  box-shadow: none !important;
}

.aquaculture-add-dialog .el-date-editor .el-input__inner {
  color: rgba(255, 255, 255, 0.9) !important;
}

.aquaculture-add-dialog .el-input__icon {
  color: rgba(0, 212, 255, 0.6) !important;
}

.aquaculture-add-dialog .el-input-number__decrease,
.aquaculture-add-dialog .el-input-number__increase {
  background-color: rgba(0, 212, 255, 0.1) !important;
  color: #00d4ff !important;
  border-color: rgba(0, 212, 255, 0.2) !important;
}

.aquaculture-add-dialog .el-input-number__decrease:hover,
.aquaculture-add-dialog .el-input-number__increase:hover {
  background-color: rgba(0, 212, 255, 0.2) !important;
  color: #00d4ff !important;
}

.aquaculture-add-dialog .el-input-number .el-input__wrapper {
  background-color: rgba(10, 25, 40, 0.8) !important;
  border: 1px solid rgba(0, 212, 255, 0.2) !important;
}

.aquaculture-add-dialog .el-input-group__append {
  background-color: rgba(0, 212, 255, 0.1) !important;
  border-color: rgba(0, 212, 255, 0.2) !important;
}

.aquaculture-add-dialog .el-input-group__append .el-button {
  background: linear-gradient(135deg, #00d4ff 0%, #0077ff 100%) !important;
  border: none !important;
  color: #fff !important;
  border-radius: 0 6px 6px 0 !important;
}

.aquaculture-add-dialog .el-button {
  border-radius: 8px !important;
  padding: 8px 20px !important;
  font-weight: 500 !important;
}

.aquaculture-add-dialog .el-button--default {
  background: rgba(255, 255, 255, 0.1) !important;
  border: 1px solid rgba(255, 255, 255, 0.2) !important;
  color: rgba(255, 255, 255, 0.8) !important;
}

.aquaculture-add-dialog .el-button--default:hover {
  background: rgba(255, 255, 255, 0.15) !important;
  color: #fff !important;
  border-color: rgba(255, 255, 255, 0.3) !important;
}

.aquaculture-add-dialog .el-button--primary {
  background: linear-gradient(135deg, #00d4ff 0%, #0077ff 100%) !important;
  border: none !important;
  color: #fff !important;
  box-shadow: 0 4px 15px rgba(0, 212, 255, 0.4) !important;
}

.aquaculture-add-dialog .el-button--primary:hover {
  box-shadow: 0 6px 20px rgba(0, 212, 255, 0.6) !important;
  transform: translateY(-1px);
}
</style>
