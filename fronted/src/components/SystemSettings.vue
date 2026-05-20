<template>
  <div class="comp-root">
    <div class="comp-card">
      <div class="comp-header">
        <div class="header-left">
          <el-icon class="header-icon"><Tools /></el-icon>
          <span class="header-title">系统设置</span>
        </div>
      </div>

      <el-tabs v-model="activeTab" class="settings-tabs">
        <el-tab-pane label="基本设置" name="basic">
          <div class="section-title">系统基本信息</div>
          <el-form :model="basicForm" label-width="120px" class="settings-form">
            <el-form-item label="系统名称"><el-input v-model="basicForm.systemName" /></el-form-item>
            <el-form-item label="版本号"><el-input v-model="basicForm.version" /></el-form-item>
            <el-form-item label="联系电话"><el-input v-model="basicForm.contactPhone" /></el-form-item>
            <el-form-item label="系统公告"><el-input v-model="basicForm.announcement" type="textarea" :rows="3" /></el-form-item>
          </el-form>
        </el-tab-pane>

        <el-tab-pane label="安全设置" name="security">
          <div class="section-title">安全策略配置</div>
          <el-form :model="securityForm" label-width="140px" class="settings-form">
            <el-form-item label="登录失败锁定次数"><el-input-number v-model="securityForm.maxLoginAttempts" :min="1" :max="10" /></el-form-item>
            <el-form-item label="会话超时时间(分钟)"><el-input-number v-model="securityForm.sessionTimeout" :min="5" :max="1440" /></el-form-item>
            <el-form-item label="密码最小长度"><el-input-number v-model="securityForm.minPasswordLength" :min="4" :max="32" /></el-form-item>
          </el-form>
        </el-tab-pane>

        <el-tab-pane label="通知设置" name="notification">
          <div class="section-title">系统通知配置</div>
          <el-form :model="notificationForm" label-width="160px" class="settings-form">
            <el-form-item label="水质告警阈值启用"><el-switch v-model="notificationForm.enableWaterAlarm" /></el-form-item>
            <el-form-item label="库存预警启用"><el-switch v-model="notificationForm.enableInventoryAlarm" /></el-form-item>
            <el-form-item label="病害防控提醒启用"><el-switch v-model="notificationForm.enableDiseaseAlarm" /></el-form-item>
            <el-form-item label="提醒提前时间(小时)"><el-input-number v-model="notificationForm.remindAdvanceHours" :min="1" :max="168" /></el-form-item>
          </el-form>
        </el-tab-pane>

        <el-tab-pane label="系统信息" name="info">
          <div class="system-info-container">
            <div class="info-row">
              <div class="info-label">系统版本</div>
              <div class="info-value">V{{ systemInfo.version }}</div>
            </div>
            <div class="info-row">
              <div class="info-label">数据库类型</div>
              <div class="info-value">SQLite</div>
            </div>
            <div class="info-row">
              <div class="info-label">后端框架</div>
              <div class="info-value">Spring Boot 3.3.8</div>
            </div>
            <div class="info-row">
              <div class="info-label">前端框架</div>
              <div class="info-value">Vue 3 + Element Plus</div>
            </div>
            <div class="info-row">
              <div class="info-label">API基础版本</div>
              <div class="info-value">{{ systemInfo.apiVersion }}</div>
            </div>
          </div>
        </el-tab-pane>
      </el-tabs>

      <div class="save-bar">
        <el-button @click="loadSettings">取消</el-button>
        <el-button type="primary" @click="handleSave" :loading="saving">保存设置</el-button>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Tools } from '@element-plus/icons-vue'
import { getSystemSettings, saveSystemSettings } from '@/api/systemSettings'

export default {
  name: 'SystemSettings',
  components: { Tools },
  setup() {
    const activeTab = ref('basic')
    const saving = ref(false)

    const basicForm = reactive({ systemName: '海洋养殖管理系统', version: '2.0.0', contactPhone: '', announcement: '' })
    const securityForm = reactive({ maxLoginAttempts: 5, sessionTimeout: 120, minPasswordLength: 6 })
    const notificationForm = reactive({ enableWaterAlarm: true, enableInventoryAlarm: true, enableDiseaseAlarm: true, remindAdvanceHours: 24 })
    const systemInfo = reactive({ version: '2.0.0', apiVersion: '1.0' })

    const loadSettings = () => {
      getSystemSettings().then(res => {
        if (res.data) {
          Object.assign(basicForm, res.data.basic || {})
          Object.assign(securityForm, res.data.security || {})
          Object.assign(notificationForm, res.data.notification || {})
        }
      }).catch(e => console.error(e))
    }

    const handleSave = async () => {
      saving.value = true
      try {
        await saveSystemSettings({ basic: basicForm, security: securityForm, notification: notificationForm })
        ElMessage.success('保存成功')
      } catch (e) { ElMessage.error('保存失败') }
      finally { saving.value = false }
    }

    onMounted(() => { loadSettings() })
    return { activeTab, saving, basicForm, securityForm, notificationForm, systemInfo, loadSettings, handleSave }
  }
}
</script>

<style scoped>
.comp-root { padding: 0; }
.comp-card { background: rgba(12,22,40,0.5); border: 1px solid rgba(80,150,220,0.08); border-radius: 16px; padding: 20px; }
.comp-header { display: flex; align-items: center; gap: 10px; margin-bottom: 20px; }
.header-icon { font-size: 22px; color: #5b9bd5; }
.header-title { font-size: 17px; font-weight: 700; color: #d6e8f8; }

.settings-tabs :deep(.el-tabs__header) { margin: 0; border-bottom: 1px solid rgba(80,150,220,0.08); }
.settings-tabs :deep(.el-tabs__item) { color: rgba(255,255,255,0.4) !important; }
.settings-tabs :deep(.el-tabs__item.is-active) { color: #5b9bd5 !important; }
.settings-tabs :deep(.el-tabs__active-bar) { background: linear-gradient(90deg, #2196f3, #5b9bd5); }

.section-title { color: #8cb8e0; font-size: 14px; font-weight: 600; margin: 16px 0 12px 0; padding-left: 12px; border-left: 3px solid #5b9bd5; }
.settings-form { padding: 0 20px; }
.settings-form :deep(.el-form-item__label) { color: #a0c8e8 !important; }
.settings-form :deep(.el-input__wrapper) { background: rgba(255,255,255,0.06) !important; border: 1px solid rgba(80,150,220,0.15); border-radius: 8px; box-shadow: none !important; }
.settings-form :deep(.el-input__inner) { color: #d6e8f8 !important; }
.settings-form :deep(.el-textarea__inner) { background: rgba(255,255,255,0.06) !important; border: 1px solid rgba(80,150,220,0.15); border-radius: 8px; color: #d6e8f8 !important; }

.system-info-container {
    background: rgba(12,22,40,0.5);
    border: 1px solid rgba(80,150,220,0.12);
    border-radius: 12px;
    overflow: hidden;
}
.info-row {
    display: flex;
    border-bottom: 1px solid rgba(80,150,220,0.1);
}
.info-row:last-child {
    border-bottom: none;
}
.info-label {
    width: 200px;
    padding: 14px 20px;
    background: rgba(80,150,220,0.08);
    color: #8cb8e0;
    font-weight: 500;
    flex-shrink: 0;
}
.info-value {
    flex: 1;
    padding: 14px 20px;
    background: rgba(12,22,40,0.2);
    color: #d6e8f8;
}

.save-bar { display: flex; justify-content: flex-end; gap: 10px; margin-top: 24px; padding-top: 16px; border-top: 1px solid rgba(80,150,220,0.08); }
.save-bar .el-button--default { background: rgba(255,255,255,0.06) !important; border: 1px solid rgba(255,255,255,0.1) !important; color: #a0c8e8 !important; border-radius: 8px; }
.save-bar .el-button--default:hover { background: rgba(255,255,255,0.1) !important; }
.save-bar .el-button--primary { background: linear-gradient(135deg, #5b9bd5, #2196f3); border: none; border-radius: 8px; }
</style>