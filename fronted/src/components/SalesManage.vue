<template>
  <div class="comp-root">
    <div class="comp-card">
      <div class="comp-header">
        <div class="header-left">
          <el-icon class="header-icon"><ShoppingCart /></el-icon>
          <span class="header-title">销售管理</span>
        </div>
        <el-button class="add-btn" @click="handleAdd"><el-icon><Plus /></el-icon>新增销售订单</el-button>
      </div>

      <el-row :gutter="16" class="stats-row">
        <el-col :span="6" v-for="s in statsItems" :key="s.key">
          <div class="stat-box" :class="s.cls">
            <div class="stat-inner">
              <el-icon class="stat-icon" :size="24"><component :is="s.icon" /></el-icon>
              <div class="stat-body">
                <span class="stat-lbl">{{ s.label }}</span>
                <span class="stat-val">{{ s.val }}</span>
              </div>
            </div>
          </div>
        </el-col>
      </el-row>

      <div class="filter-bar">
        <el-input v-model="filterForm.keyword" placeholder="搜索订单/客户/产品" clearable size="small" class="filter-input" />
        <el-select v-model="filterForm.status" placeholder="全部状态" clearable size="small" class="filter-sel">
          <el-option label="待发货" value="待发货" />
          <el-option label="已发货" value="已发货" />
          <el-option label="已完成" value="已完成" />
          <el-option label="已取消" value="已取消" />
        </el-select>
        <el-button size="small" class="filter-btn" @click="loadData">查询</el-button>
        <el-button size="small" class="filter-btn-reset" @click="resetFilter">重置</el-button>
        <el-button size="small" class="export-btn" @click="handleExport">导出</el-button>
      </div>

      <el-table :data="tableData" class="data-table" empty-text="暂无数据" v-loading="loading">
        <el-table-column prop="orderNo" label="订单编号" min-width="140" />
        <el-table-column prop="customerName" label="客户名称" min-width="120" />
        <el-table-column prop="productName" label="产品名称" min-width="110" />
        <el-table-column label="数量" min-width="100">
          <template #default="{ row }">{{ row.quantity }} {{ row.unit }}</template>
        </el-table-column>
        <el-table-column label="单价(元)" min-width="100">
          <template #default="{ row }">¥{{ formatNum(row.unitPrice) }}</template>
        </el-table-column>
        <el-table-column label="总金额(元)" min-width="120">
          <template #default="{ row }"><span class="val-total">¥{{ formatNum(row.totalAmount) }}</span></template>
        </el-table-column>
        <el-table-column prop="saleDate" label="销售日期" min-width="110" />
        <el-table-column label="状态" min-width="100">
          <template #default="{ row }">
            <el-tag :class="'sale-status ' + getStatusCls(row.status)" size="small">{{ row.status }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="remarks" label="备注" min-width="140" show-overflow-tooltip />
        <el-table-column label="操作" min-width="140" fixed="right">
          <template #default="{ row }">
            <el-button size="small" class="action-btn" @click="handleEdit(row)">编辑</el-button>
            <el-button size="small" class="action-btn-del" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <el-dialog v-model="dialogVisible" :title="editing ? '编辑订单' : '新增订单'" width="520px" class="add-dialog" :append-to-body="true" :close-on-click-modal="false">
      <el-form :model="form" label-width="90px">
        <el-form-item label="订单编号"><el-input v-model="form.orderNo" placeholder="自动生成或手动输入" /></el-form-item>
        <el-form-item label="客户名称"><el-input v-model="form.customerName" placeholder="请输入" /></el-form-item>
        <el-form-item label="产品名称"><el-input v-model="form.productName" placeholder="请输入" /></el-form-item>
        <el-form-item label="数量">
          <el-input-number v-model="form.quantity" :min="0" style="width:160px" />
          <el-select v-model="form.unit" style="width:100px;margin-left:8px">
            <el-option label="斤" value="斤" /><el-option label="尾" value="尾" /><el-option label="只" value="只" /><el-option label="公斤" value="公斤" />
          </el-select>
        </el-form-item>
        <el-form-item label="单价(元)"><el-input-number v-model="form.unitPrice" :min="0" :precision="2" style="width:100%" /></el-form-item>
        <el-form-item label="总金额(元)"><el-input-number v-model="form.totalAmount" :min="0" :precision="2" style="width:100%" /></el-form-item>
        <el-form-item label="销售日期"><el-date-picker v-model="form.saleDate" type="date" style="width:100%" value-format="YYYY-MM-DD" /></el-form-item>
        <el-form-item label="状态">
          <el-select v-model="form.status" style="width:100%">
            <el-option label="待发货" value="待发货" /><el-option label="已发货" value="已发货" /><el-option label="已完成" value="已完成" /><el-option label="已取消" value="已取消" />
          </el-select>
        </el-form-item>
        <el-form-item label="备注"><el-input v-model="form.remarks" type="textarea" :rows="2" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSave" :loading="saving">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import { ref, computed, onMounted, reactive } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, ShoppingCart, List, Document } from '@element-plus/icons-vue'
import { getSalesRecords, createSalesRecord, updateSalesRecord, deleteSalesRecord } from '@/api/sales'

export default {
  name: 'SalesManage',
  components: { Plus, ShoppingCart, List, Document },
  setup() {
    const tableData = ref([])
    const loading = ref(false)
    const saving = ref(false)
    const dialogVisible = ref(false)
    const editing = ref(null)
    const filterForm = reactive({ keyword: '', status: '' })
    const form = reactive({ orderNo: '', customerName: '', productName: '',
      quantity: 0, unit: '斤', unitPrice: 0, totalAmount: 0, saleDate: '', status: '待发货', remarks: '' })

    const totalAmount = computed(() => tableData.value.reduce((s, i) => s + (parseFloat(i.totalAmount) || 0), 0))
    const totalOrders = computed(() => tableData.value.length)
    const completed = computed(() => tableData.value.filter(i => i.status === '已完成').length)
    const pending = computed(() => tableData.value.filter(i => i.status === '待发货' || i.status === '已发货').length)

    const statsItems = computed(() => [
      { key: 'orders', label: '总订单数', icon: 'List', cls: 's-total', val: totalOrders.value + '单' },
      { key: 'amount', label: '总销售额', icon: 'ShoppingCart', cls: 's-amount', val: '¥' + totalAmount.value.toLocaleString() },
      { key: 'completed', label: '已完成', icon: 'Document', cls: 's-done', val: completed.value + '单' },
      { key: 'pending', label: '待处理', icon: 'Document', cls: 's-pending', val: pending.value + '单' }
    ])

    const formatNum = (v) => (parseFloat(v) || 0).toLocaleString()
    const getStatusCls = (s) => ({ '待发货': 's-pend', '已发货': 's-sent', '已完成': 's-done', '已取消': 's-cancel' })[s] || ''
    const resetForm = () => { form.orderNo = ''; form.customerName = ''; form.productName = '';
      form.quantity = 0; form.unit = '斤'; form.unitPrice = 0; form.totalAmount = 0; form.saleDate = ''; form.status = '待发货'; form.remarks = '' }

    const loadData = async () => {
      loading.value = true
      try {
        const res = await getSalesRecords()
        let data = Array.isArray(res.data) ? res.data : []
        
        // 按状态筛选
        if (filterForm.status) {
          data = data.filter(item => item.status === filterForm.status)
        }
        
        // 按关键词筛选（订单编号、客户名称、产品名称）
        if (filterForm.keyword) {
          data = data.filter(item => 
            (item.orderNo && item.orderNo.includes(filterForm.keyword)) ||
            (item.customerName && item.customerName.includes(filterForm.keyword)) ||
            (item.productName && item.productName.includes(filterForm.keyword))
          )
        }
        
        tableData.value = data
      } catch (e) { console.error(e); tableData.value = [] }
      finally { loading.value = false }
    }

    const handleAdd = () => { editing.value = null; resetForm(); dialogVisible.value = true }
    const handleEdit = (row) => { editing.value = row; Object.assign(form, row); dialogVisible.value = true }

    const handleSave = async () => {
      saving.value = true
      try {
        const data = { ...form }
        if (editing.value) { await updateSalesRecord(editing.value.id, data); ElMessage.success('编辑成功') }
        else { await createSalesRecord(data); ElMessage.success('添加成功') }
        dialogVisible.value = false; loadData()
      } catch (e) { ElMessage.error('操作失败') }
      finally { saving.value = false }
    }

    const handleDelete = async (row) => {
      try { await ElMessageBox.confirm('确定删除？', '提示', { type: 'warning' }); await deleteSalesRecord(row.id); ElMessage.success('已删除'); loadData() }
      catch { /* ignore */ }
    }

    const handleExport = () => { ElMessage.info('导出功能开发中') }
    const resetFilter = () => { filterForm.keyword = ''; filterForm.status = ''; loadData() }

    onMounted(() => { loadData() })
    return { tableData, loading, saving, dialogVisible, editing, filterForm, form, statsItems, formatNum,
      getStatusCls, loadData, handleAdd, handleEdit, handleSave, handleDelete, handleExport, resetFilter }
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
.add-btn { background: rgba(33,150,243,0.15) !important; border: 1px solid rgba(33,150,243,0.25) !important; color: #64b5f6 !important; border-radius: 10px; }
.add-btn:hover { background: rgba(33,150,243,0.25) !important; }

.stats-row { margin-bottom: 16px; }
.stat-box { background: rgba(255,255,255,0.03); border: 1px solid rgba(80,150,220,0.08); border-radius: 14px; padding: 14px 16px; transition: all 0.3s; }
.stat-box:hover { background: rgba(80,150,220,0.05); transform: translateY(-1px); }
.stat-box.s-total { border-left: 3px solid #5b9bd5; }
.stat-box.s-amount { border-left: 3px solid #00ff88; }
.stat-box.s-done { border-left: 3px solid #5b9bd5; }
.stat-box.s-pending { border-left: 3px solid #ffcc00; }
.stat-inner { display: flex; align-items: center; gap: 12px; }
.s-total .stat-icon { color: #5b9bd5; }
.s-amount .stat-icon { color: #00ff88; }
.s-done .stat-icon { color: #5b9bd5; }
.s-pending .stat-icon { color: #ffcc00; }
.stat-body { display: flex; flex-direction: column; gap: 2px; }
.stat-lbl { font-size: 12px; color: rgba(255,255,255,0.4); }
.stat-val { font-size: 18px; font-weight: 700; color: #e8f2fc; }

.filter-bar { display: flex; gap: 10px; margin-bottom: 16px; align-items: center; }
.filter-input { width: 220px; }
.filter-sel { width: 150px; }
.filter-btn { background: rgba(33,150,243,0.12) !important; border: 1px solid rgba(33,150,243,0.2) !important; color: #64b5f6 !important; border-radius: 8px; }
.filter-btn:hover { background: rgba(33,150,243,0.22) !important; }
.filter-btn-reset { background: rgba(255,255,255,0.04) !important; border: 1px solid rgba(255,255,255,0.08) !important; color: rgba(255,255,255,0.4) !important; border-radius: 8px; }
.export-btn { background: rgba(0,255,136,0.1) !important; border: 1px solid rgba(0,255,136,0.2) !important; color: #00bb66 !important; border-radius: 8px; }
.export-btn:hover { background: rgba(0,255,136,0.2) !important; }

.data-table { border-radius: 10px; overflow: hidden; }
.data-table :deep(.el-table) { background: #0c1628 !important; }
.data-table :deep(.el-table::before) { display: none; }
.data-table :deep(.el-table__header th) { background: #0f1f35 !important; color: #8cb8e0 !important; font-weight: 600; font-size: 13px; border-bottom: 1px solid rgba(80,150,220,0.15) !important; }
.data-table :deep(.el-table__cell) { background: #0c1628 !important; color: #d6e8f8 !important; border-color: rgba(80,150,220,0.08) !important; font-size: 14px; }
.data-table :deep(.el-table__row:hover > td) { background: #132742 !important; }
.data-table :deep(.el-table__empty-block) { background: #0c1628 !important; }
.data-table :deep(.el-table__empty-text) { color: rgba(255,255,255,0.4); }
.val-total { color: #00ff88; font-weight: 600; }

.sale-status { border-radius: 10px; padding: 2px 10px; font-weight: 500; border: none; }
.sale-status.s-pend { background: rgba(255,204,0,0.12); color: #ffcc00; }
.sale-status.s-sent { background: rgba(0,212,255,0.12); color: #00d4ff; }
.sale-status.s-done { background: rgba(0,255,136,0.1); color: #00ff88; }
.sale-status.s-cancel { background: rgba(255,107,53,0.12); color: #ff8a65; }

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
  background: rgba(255,255,255,0.06) !important;
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
.add-dialog .el-select .el-input__wrapper {
  background: rgba(255,255,255,0.06) !important;
  box-shadow: none !important;
  border: 1px solid rgba(80,150,220,0.15);
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
.add-dialog .el-input-number .el-input__wrapper {
  width: 100%;
}
</style>