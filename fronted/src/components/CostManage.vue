<template>
  <div class="comp-root">
    <div class="comp-card">
      <div class="comp-header">
        <div class="header-left">
          <el-icon class="header-icon"><Money /></el-icon>
          <span class="header-title">成本核算</span>
        </div>
        <div class="header-actions">
          <el-button class="add-btn-green" @click="handleAddRevenue"><el-icon><Plus /></el-icon>新增收入</el-button>
          <el-button class="add-btn" @click="handleAddCost"><el-icon><Plus /></el-icon>新增成本</el-button>
        </div>
      </div>

      <el-row :gutter="16" class="stats-row">
        <el-col :span="6" v-for="s in statsItems" :key="s.key">
          <div class="stat-box" :class="s.cls">
            <div class="stat-inner">
              <el-icon class="stat-icon" :size="24"><component :is="s.icon" /></el-icon>
              <div class="stat-body">
                <span class="stat-lbl">{{ s.label }}</span>
                <span class="stat-val" :class="s.valCls">{{ s.prefix }}{{ s.val }}</span>
              </div>
            </div>
          </div>
        </el-col>
      </el-row>

      <div class="filter-bar">
        <el-input v-model="filterForm.keyword" placeholder="搜索项目名称" clearable size="small" class="filter-input" />
        <el-select v-model="filterForm.type" placeholder="全部类型" clearable size="small" class="filter-sel">
          <el-option v-for="t in allTypes" :key="t" :label="t" :value="t" />
        </el-select>
        <el-button size="small" class="filter-btn" @click="loadAll">查询</el-button>
        <el-button size="small" class="filter-btn-reset" @click="resetFilter">重置</el-button>
      </div>

      <el-tabs v-model="activeTab" class="data-tabs">
        <el-tab-pane label="成本记录" name="cost">
          <el-table :data="costList" class="data-table" empty-text="暂无数据" v-loading="loading">
            <el-table-column prop="costName" label="项目名称" min-width="150" />
            <el-table-column prop="costType" label="类型" min-width="110">
              <template #default="{ row }"><span class="cost-tag">{{ row.costType }}</span></template>
            </el-table-column>
            <el-table-column label="金额" min-width="120">
              <template #default="{ row }"><span class="val-minus">-¥{{ formatNum(row.amount) }}</span></template>
            </el-table-column>
            <el-table-column prop="recordDate" label="日期" min-width="110" />
            <el-table-column prop="remarks" label="备注" min-width="160" show-overflow-tooltip />
            <el-table-column label="操作" min-width="140" fixed="right">
              <template #default="{ row }">
                <el-button size="small" class="action-btn" @click="handleEditCost(row)">编辑</el-button>
                <el-button size="small" class="action-btn-del" @click="handleDeleteCost(row)">删除</el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-tab-pane>

        <el-tab-pane label="收入记录" name="revenue">
          <el-table :data="revenueList" class="data-table" empty-text="暂无数据" v-loading="loading">
            <el-table-column prop="revenueName" label="项目名称" min-width="150" />
            <el-table-column prop="revenueType" label="类型" min-width="110">
              <template #default="{ row }"><span class="rev-tag">{{ row.revenueType }}</span></template>
            </el-table-column>
            <el-table-column label="金额" min-width="120">
              <template #default="{ row }"><span class="val-plus">+¥{{ formatNum(row.amount) }}</span></template>
            </el-table-column>
            <el-table-column prop="recordDate" label="日期" min-width="110" />
            <el-table-column prop="remarks" label="备注" min-width="160" show-overflow-tooltip />
            <el-table-column label="操作" min-width="140" fixed="right">
              <template #default="{ row }">
                <el-button size="small" class="action-btn" @click="handleEditRevenue(row)">编辑</el-button>
                <el-button size="small" class="action-btn-del" @click="handleDeleteRevenue(row)">删除</el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-tab-pane>
      </el-tabs>
    </div>

    <el-dialog v-model="costDlg" title="成本记录" width="520px" class="add-dialog" :append-to-body="true" :close-on-click-modal="false">
      <el-form :model="costForm" label-width="90px">
        <el-form-item label="项目名称"><el-input v-model="costForm.costName" /></el-form-item>
        <el-form-item label="成本类型"><el-select v-model="costForm.costType" style="width:100%"><el-option v-for="t in costTypes" :key="t" :label="t" :value="t" /></el-select></el-form-item>
        <el-form-item label="金额"><el-input-number v-model="costForm.amount" :min="0" :precision="2" style="width:100%" /></el-form-item>
        <el-form-item label="日期"><el-date-picker v-model="costForm.recordDate" type="date" style="width:100%" value-format="YYYY-MM-DD" /></el-form-item>
        <el-form-item label="备注"><el-input v-model="costForm.remarks" type="textarea" :rows="2" /></el-form-item>
      </el-form>
      <template #footer><el-button @click="costDlg = false">取消</el-button><el-button type="primary" @click="handleSaveCost" :loading="saving">保存</el-button></template>
    </el-dialog>

    <el-dialog v-model="revDlg" title="收入记录" width="520px" class="add-dialog" :append-to-body="true" :close-on-click-modal="false">
      <el-form :model="revForm" label-width="90px">
        <el-form-item label="项目名称"><el-input v-model="revForm.revenueName" /></el-form-item>
        <el-form-item label="收入类型"><el-select v-model="revForm.revenueType" style="width:100%"><el-option v-for="t in revTypes" :key="t" :label="t" :value="t" /></el-select></el-form-item>
        <el-form-item label="金额"><el-input-number v-model="revForm.amount" :min="0" :precision="2" style="width:100%" /></el-form-item>
        <el-form-item label="日期"><el-date-picker v-model="revForm.recordDate" type="date" style="width:100%" value-format="YYYY-MM-DD" /></el-form-item>
        <el-form-item label="备注"><el-input v-model="revForm.remarks" type="textarea" :rows="2" /></el-form-item>
      </el-form>
      <template #footer><el-button @click="revDlg = false">取消</el-button><el-button type="primary" @click="handleSaveRevenue" :loading="saving">保存</el-button></template>
    </el-dialog>
  </div>
</template>

<script>
import { ref, computed, onMounted, reactive } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Money, TrendCharts, DataLine } from '@element-plus/icons-vue'
import { getCosts, createCost, updateCost, deleteCost, getRevenues, createRevenue, updateRevenue, deleteRevenue } from '@/api/cost'

export default {
  name: 'CostManage',
  components: { Plus, Money, TrendCharts, DataLine },
  setup() {
    const activeTab = ref('cost')
    const costList = ref([])
    const revenueList = ref([])
    const loading = ref(false)
    const saving = ref(false)
    const costDlg = ref(false)
    const revDlg = ref(false)
    const editingCost = ref(null)
    const editingRevenue = ref(null)
    const filterForm = reactive({ keyword: '', type: '' })
    const costForm = reactive({ costName: '', costType: '', amount: 0, recordDate: '', remarks: '' })
    const revForm = reactive({ revenueName: '', revenueType: '', amount: 0, recordDate: '', remarks: '' })
    const costTypes = ['苗种成本', '饲料成本', '药品成本', '水电成本', '人工成本', '设备成本', '其他成本']
    const revTypes = ['销售收入', '补贴收入', '其他收入']
    const allTypes = [...costTypes, ...revTypes]
    const totalCost = computed(() => costList.value.reduce((s, i) => s + (parseFloat(i.amount) || 0), 0))
    const totalRevenue = computed(() => revenueList.value.reduce((s, i) => s + (parseFloat(i.amount) || 0), 0))
    const profit = computed(() => totalRevenue.value - totalCost.value)
    const profitRate = computed(() => totalRevenue.value === 0 ? '0.00' : ((profit.value / totalRevenue.value) * 100).toFixed(2))
    const statsItems = computed(() => [
      { key: 'cost', label: '总成本', icon: 'Money', cls: 's-cost', val: totalCost.value.toLocaleString(), prefix: '¥', valCls: '' },
      { key: 'revenue', label: '总收入', icon: 'TrendCharts', cls: 's-rev', val: totalRevenue.value.toLocaleString(), prefix: '¥', valCls: '' },
      { key: 'profit', label: '总利润', icon: 'TrendCharts', cls: profit.value < 0 ? 's-loss' : 's-profit', val: profit.value.toLocaleString(), prefix: profit.value < 0 ? '-¥' : '¥', valCls: profit.value < 0 ? 'clr-red' : 'clr-green' },
      { key: 'rate', label: '利润率', icon: 'DataLine', cls: 's-rate', val: profitRate.value + '%', prefix: '', valCls: '' }
    ])
    const formatNum = (v) => (parseFloat(v) || 0).toLocaleString()
    const resetCostForm = () => { costForm.costName = ''; costForm.costType = ''; costForm.amount = 0; costForm.recordDate = ''; costForm.remarks = '' }
    const resetRevForm = () => { revForm.revenueName = ''; revForm.revenueType = ''; revForm.amount = 0; revForm.recordDate = ''; revForm.remarks = '' }

    const loadAll = async () => {
      loading.value = true
      try {
        const params = {}
        if (filterForm.keyword) params.keyword = filterForm.keyword
        const [cRes, rRes] = await Promise.all([getCosts(params), getRevenues(params)])
        let costs = Array.isArray(cRes.data) ? cRes.data : []
        let revenues = Array.isArray(rRes.data) ? rRes.data : []
        
        // 按类型筛选
        if (filterForm.type) {
          if (costTypes.includes(filterForm.type)) {
            costs = costs.filter(item => item.costType === filterForm.type)
            revenues = []
          } else if (revTypes.includes(filterForm.type)) {
            revenues = revenues.filter(item => item.revenueType === filterForm.type)
            costs = []
          }
        }
        
        // 按关键词筛选项目名称
        if (filterForm.keyword) {
          costs = costs.filter(item => 
            item.costName && item.costName.includes(filterForm.keyword)
          )
          revenues = revenues.filter(item => 
            item.revenueName && item.revenueName.includes(filterForm.keyword)
          )
        }
        
        costList.value = costs
        revenueList.value = revenues
      } catch (e) { console.error(e) }
      finally { loading.value = false }
    }

    const handleAddCost = () => { editingCost.value = null; resetCostForm(); costDlg.value = true }
    const handleEditCost = (row) => { editingCost.value = row; Object.assign(costForm, row); costDlg.value = true }
    const handleSaveCost = async () => {
      saving.value = true
      try {
        const data = { ...costForm }
        if (editingCost.value) { await updateCost(editingCost.value.id, data); ElMessage.success('编辑成功') }
        else { await createCost(data); ElMessage.success('添加成功') }
        costDlg.value = false; loadAll()
      } catch (e) { ElMessage.error('操作失败') }
      finally { saving.value = false }
    }
    const handleDeleteCost = async (row) => {
      try { await ElMessageBox.confirm('确定删除？', '提示', { type: 'warning' }); await deleteCost(row.id); ElMessage.success('已删除'); loadAll() } catch { /* ignore */ }
    }

    const handleAddRevenue = () => { editingRevenue.value = null; resetRevForm(); revDlg.value = true }
    const handleEditRevenue = (row) => { editingRevenue.value = row; Object.assign(revForm, row); revDlg.value = true }
    const handleSaveRevenue = async () => {
      saving.value = true
      try {
        const data = { ...revForm }
        if (editingRevenue.value) { await updateRevenue(editingRevenue.value.id, data); ElMessage.success('编辑成功') }
        else { await createRevenue(data); ElMessage.success('添加成功') }
        revDlg.value = false; loadAll()
      } catch (e) { ElMessage.error('操作失败') }
      finally { saving.value = false }
    }
    const handleDeleteRevenue = async (row) => {
      try { await ElMessageBox.confirm('确定删除？', '提示', { type: 'warning' }); await deleteRevenue(row.id); ElMessage.success('已删除'); loadAll() } catch { /* ignore */ }
    }
    const resetFilter = () => { filterForm.keyword = ''; filterForm.type = ''; loadAll() }
    onMounted(() => { loadAll() })

    return { activeTab, costList, revenueList, loading, saving, costDlg, revDlg, filterForm, costForm, revForm,
      costTypes, revTypes, allTypes, totalCost, totalRevenue, profit, profitRate, statsItems, formatNum,
      loadAll, handleAddCost, handleEditCost, handleSaveCost, handleDeleteCost,
      handleAddRevenue, handleEditRevenue, handleSaveRevenue, handleDeleteRevenue, resetFilter }
  }
}
</script>

<style scoped>
.comp-root { padding: 0; }
.comp-card { background: rgba(12,22,40,0.5); border: 1px solid rgba(80,150,220,0.08); border-radius: 16px; padding: 20px; }
.comp-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 20px; }
.header-left { display: flex; align-items: center; gap: 10px; }
.header-icon { font-size: 22px; color: #5b9bd5; }
.header-title { font-size: 17px; font-weight: 700; color: #d6e8f8; }
.header-actions { display: flex; gap: 10px; }
.add-btn, .add-btn-green { border-radius: 10px; }
.add-btn { background: rgba(33,150,243,0.15) !important; border: 1px solid rgba(33,150,243,0.25) !important; color: #64b5f6 !important; }
.add-btn:hover { background: rgba(33,150,243,0.25) !important; }
.add-btn-green { background: rgba(0,255,136,0.12) !important; border: 1px solid rgba(0,255,136,0.2) !important; color: #00ff88 !important; }
.add-btn-green:hover { background: rgba(0,255,136,0.22) !important; }
.stats-row { margin-bottom: 16px; }
.stat-box { background: rgba(255,255,255,0.03); border: 1px solid rgba(80,150,220,0.08); border-radius: 14px; padding: 14px 16px; }
.stat-box.s-cost { border-left: 3px solid #ff6b35; }
.stat-box.s-rev { border-left: 3px solid #00ff88; }
.stat-box.s-profit { border-left: 3px solid #5b9bd5; }
.stat-box.s-loss { border-left: 3px solid #ff4444; }
.stat-box.s-rate { border-left: 3px solid #ffcc00; }
.stat-inner { display: flex; align-items: center; gap: 12px; }
.s-cost .stat-icon { color: #ff6b35; }
.s-rev .stat-icon { color: #00ff88; }
.s-profit .stat-icon { color: #5b9bd5; }
.s-loss .stat-icon { color: #ff4444; }
.s-rate .stat-icon { color: #ffcc00; }
.stat-body { display: flex; flex-direction: column; gap: 2px; }
.stat-lbl { font-size: 12px; color: rgba(255,255,255,0.4); }
.stat-val { font-size: 20px; font-weight: 700; color: #e8f2fc; }
.clr-red { color: #ff4444 !important; }
.clr-green { color: #00ff88 !important; }
.filter-bar { display: flex; gap: 10px; margin-bottom: 16px; align-items: center; }
.filter-btn { background: rgba(33,150,243,0.12) !important; border: 1px solid rgba(33,150,243,0.2) !important; color: #64b5f6 !important; border-radius: 8px; }
.filter-btn:hover { background: rgba(33,150,243,0.22) !important; }
.filter-btn-reset { background: rgba(255,255,255,0.04) !important; border: 1px solid rgba(255,255,255,0.08) !important; color: rgba(255,255,255,0.4) !important; border-radius: 8px; }
.data-tabs :deep(.el-tabs__header) { margin: 0; border-bottom: 1px solid rgba(80,150,220,0.08); }
.data-tabs :deep(.el-tabs__item) { color: rgba(255,255,255,0.4) !important; }
.data-tabs :deep(.el-tabs__item.is-active) { color: #5b9bd5 !important; }
.data-tabs :deep(.el-tabs__active-bar) { background: linear-gradient(90deg, #2196f3, #5b9bd5); }
.data-table { border-radius: 10px; overflow: hidden; margin-top: 8px; }
.data-table :deep(.el-table) { background: #0c1628 !important; }
.data-table :deep(.el-table::before) { display: none; }
.data-table :deep(.el-table__header th) { background: #0f1f35 !important; color: #8cb8e0 !important; font-weight: 600; font-size: 13px; border-bottom: 1px solid rgba(80,150,220,0.15) !important; }
.data-table :deep(.el-table__cell) { background: #0c1628 !important; color: #d6e8f8 !important; border-color: rgba(80,150,220,0.08) !important; font-size: 14px; }
.data-table :deep(.el-table__row:hover > td) { background: #132742 !important; }
.data-table :deep(.el-table__empty-block) { background: #0c1628 !important; }
.data-table :deep(.el-table__empty-text) { color: rgba(255,255,255,0.4); }
.val-minus { color: #ff6b35; font-weight: 600; }
.val-plus { color: #00ff88; font-weight: 600; }
.cost-tag { background: rgba(255,107,53,0.1); color: #ff8a65; padding: 2px 8px; border-radius: 6px; font-size: 12px; }
.rev-tag { background: rgba(0,255,136,0.1); color: #00ff88; padding: 2px 8px; border-radius: 6px; font-size: 12px; }
.action-btn { background: rgba(80,150,220,0.1) !important; border: 1px solid rgba(80,150,220,0.15) !important; color: #8cb8e0 !important; border-radius: 6px; }
.action-btn:hover { background: rgba(80,150,220,0.2) !important; }
.action-btn-del { background: rgba(255,68,68,0.1) !important; border: 1px solid rgba(255,68,68,0.15) !important; color: #ff6b6b !important; border-radius: 6px; }
.action-btn-del:hover { background: rgba(255,68,68,0.2) !important; }
</style>

<style>
.add-dialog {
  --el-dialog-bg-color: #0f1c2e;
}
.add-dialog .el-dialog {
  background: #0f1c2e;
  border: 1px solid rgba(80,150,220,0.15);
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
  border: 1px solid rgba(80,150,220,0.15);
  border-radius: 8px;
}
.add-dialog .el-input__wrapper:hover {
  border-color: rgba(80,150,220,0.3);
}
.add-dialog .el-input__wrapper.is-focus {
  border-color: #5b9bd5 !important;
  box-shadow: 0 0 0 2px rgba(91,155,213,0.15) !important;
}
.add-dialog .el-input__inner {
  color: #d6e8f8 !important;
  font-size: 14px;
}
.add-dialog .el-input__inner::placeholder {
  color: rgba(255,255,255,0.3) !important;
}
.add-dialog .el-input.is-disabled .el-input__wrapper {
  background: rgba(255,255,255,0.03) !important;
  opacity: 0.6;
}
.add-dialog .el-input-number .el-input__wrapper {
  width: 100%;
}
.add-dialog .el-select .el-input__wrapper {
  background: rgba(255,255,255,0.06) !important;
  box-shadow: none !important;
  border: 1px solid rgba(80,150,220,0.15);
}
.add-dialog .el-select .el-input__inner {
  color: #d6e8f8 !important;
}
.add-dialog .el-textarea__inner {
  background: rgba(255,255,255,0.06) !important;
  border: 1px solid rgba(80,150,220,0.15);
  color: #d6e8f8 !important;
  border-radius: 8px;
}
.add-dialog .el-textarea__inner:focus {
  border-color: #5b9bd5 !important;
  box-shadow: 0 0 0 2px rgba(91,155,213,0.15) !important;
}
.add-dialog .el-textarea__inner::placeholder {
  color: rgba(255,255,255,0.3) !important;
}
.add-dialog .el-date-editor .el-input__wrapper {
  background: rgba(255,255,255,0.06) !important;
  box-shadow: none !important;
  border: 1px solid rgba(80,150,220,0.15);
}
.add-dialog .el-button {
  border-radius: 8px;
}
.add-dialog .el-button--default {
  background: rgba(255,255,255,0.06) !important;
  border: 1px solid rgba(255,255,255,0.1) !important;
  color: #a0c8e0 !important;
}
.add-dialog .el-button--default:hover {
  background: rgba(255,255,255,0.1) !important;
  border-color: rgba(255,255,255,0.2) !important;
  color: #d6e8f8 !important;
}
.add-dialog .el-button--primary {
  background: linear-gradient(135deg, #5b9bd5, #2196f3);
  border: none;
  box-shadow: 0 4px 15px rgba(33,150,243,0.25);
}
.add-dialog .el-select-dropdown {
  background: #0f1c2e !important;
  border: 1px solid rgba(80,150,220,0.15) !important;
}
.add-dialog .el-select-dropdown__item {
  color: #d6e8f8 !important;
}
.add-dialog .el-select-dropdown__item.hover,
.add-dialog .el-select-dropdown__item:hover {
  background: rgba(80,150,220,0.1) !important;
}
.add-dialog .el-select-dropdown__item.selected {
  color: #5b9bd5 !important;
  font-weight: 600;
}
.add-dialog .el-popper.is-light {
  background: #0f1c2e !important;
  border: 1px solid rgba(80,150,220,0.15) !important;
  color: #d6e8f8 !important;
}
.add-dialog .el-picker-panel {
  background: #0f1c2e !important;
  border: 1px solid rgba(80,150,220,0.15) !important;
  color: #d6e8f8 !important;
}
</style>