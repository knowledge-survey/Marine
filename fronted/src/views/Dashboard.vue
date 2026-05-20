<template>
  <div class="dashboard-root">
    <div class="dash-header">
      <div class="header-left">
        <div class="collapse-btn" @click="sidebarCollapsed = !sidebarCollapsed">
          <el-icon :size="20"><Fold v-if="!sidebarCollapsed" /><Expand v-else /></el-icon>
        </div>
        <div class="brand">
          <el-icon class="brand-icon"><Ship /></el-icon>
          <span class="brand-text" :class="{ hide: sidebarCollapsed }">{{ systemSettings.basic.systemName }}</span>
        </div>
      </div>
      <div class="header-right">
        <el-button class="hdr-btn" @click="$router.push('/admin')" v-if="isManagerOrAboveUser">
          <el-icon><Setting /></el-icon>
          <span class="hdr-btn-text">管理后台</span>
        </el-button>
        <el-dropdown trigger="click" placement="bottom-end" popper-class="user-dropdown">
          <div class="user-menu-trigger">
            <div class="avatar-wrapper">
              <img v-if="currentUser.avatar" :src="currentUser.avatar" class="avatar-img" />
              <el-icon v-else :size="18"><UserFilled /></el-icon>
            </div>
            <span class="user-name">{{ currentUser.realName || currentUser.username }}</span>
            <el-tag class="role-tag" :class="roleClass" size="small">{{ roleLabel }}</el-tag>
            <el-icon :size="12" class="dropdown-arrow"><ArrowDown /></el-icon>
          </div>
          <template #dropdown>
            <el-dropdown-menu class="user-dropdown-menu">
              <div class="dropdown-header">
                <div class="dropdown-avatar">
                  <img v-if="currentUser.avatar" :src="currentUser.avatar" />
                  <el-icon v-else :size="28"><UserFilled /></el-icon>
                </div>
                <div class="dropdown-info">
                  <span class="dropdown-name">{{ currentUser.realName || currentUser.username }}</span>
                  <span class="dropdown-role">{{ roleLabel }}</span>
                </div>
              </div>
              <el-dropdown-item divided @click="showProfileDialog = true">
                <el-icon><User /></el-icon>个人信息
              </el-dropdown-item>
              <el-dropdown-item @click="logout">
                <el-icon><SwitchButton /></el-icon>退出登录
              </el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </div>
    </div>
    <div v-if="systemSettings.basic.announcement" class="announcement-bar">
      <el-icon class="announcement-icon"><Warning /></el-icon>
      <span class="announcement-text">{{ systemSettings.basic.announcement }}</span>
    </div>
    <div class="dash-body">
      <div class="dash-aside" :class="{ collapsed: sidebarCollapsed }">
        <div class="menu-section">
          <el-menu :default-active="activeMenu" @select="handleMenuSelect" class="side-menu" :collapse="sidebarCollapsed">
            
            <!-- 监测记录模块 - 所有角色可见 -->
            <div class="menu-group-title" v-show="!sidebarCollapsed">监测记录</div>
            <el-menu-item index="water">
              <el-icon><TrendCharts /></el-icon>
              <template #title>水质监测</template>
            </el-menu-item>
            <el-menu-item index="record">
              <el-icon><Document /></el-icon>
              <template #title>养殖记录</template>
            </el-menu-item>
            <el-menu-item index="disease">
              <el-icon><Warning /></el-icon>
              <template #title>病害防控</template>
            </el-menu-item>
            <el-menu-item index="weather">
              <el-icon><PartlyCloudy /></el-icon>
              <template #title>天气预报</template>
            </el-menu-item>

            <!-- 生产管理模块 - 管理人员及以上可见 -->
            <div class="menu-group-title" v-show="!sidebarCollapsed && isManagerOrAboveUser" style="margin-top: 8px;">生产管理</div>
            <el-menu-item index="production" v-if="isManagerOrAboveUser">
              <el-icon><Calendar /></el-icon>
              <template #title>生产计划</template>
            </el-menu-item>
            <el-menu-item index="inventory" v-if="isManagerOrAboveUser">
              <el-icon><Box /></el-icon>
              <template #title>库存管理</template>
            </el-menu-item>

            <!-- 财务管理模块 - 管理人员及以上可见 -->
            <div class="menu-group-title" v-show="!sidebarCollapsed && isManagerOrAboveUser" style="margin-top: 8px;">财务管理</div>
            <el-menu-item index="cost" v-if="isManagerOrAboveUser">
              <el-icon><Money /></el-icon>
              <template #title>成本核算</template>
            </el-menu-item>
            <el-menu-item index="sales" v-if="isManagerOrAboveUser">
              <el-icon><ShoppingCart /></el-icon>
              <template #title>销售管理</template>
            </el-menu-item>

            <!-- 智能服务模块 - 所有角色可见 -->
            <div class="menu-group-title" v-show="!sidebarCollapsed" style="margin-top: 8px;">智能服务</div>
            <el-menu-item index="notification">
              <el-icon><Bell /></el-icon>
              <template #title>通知告警</template>
            </el-menu-item>
            <el-menu-item index="reminder">
              <el-icon><AlarmClock /></el-icon>
              <template #title>智能提醒</template>
            </el-menu-item>
            <el-menu-item index="knowledge">
              <el-icon><Reading /></el-icon>
              <template #title>知识库</template>
            </el-menu-item>
            <el-menu-item index="trace" v-if="isManagerOrAboveUser">
              <el-icon><CircleCheck /></el-icon>
              <template #title>溯源管理</template>
            </el-menu-item>
          </el-menu>
        </div>
        <div class="zone-section" v-show="!sidebarCollapsed">
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
        <div class="user-profile" :class="{ 'profile-collapsed': sidebarCollapsed }">
          <div class="profile-avatar" @click="showProfileDialog = true">
            <img v-if="currentUser.avatar" :src="currentUser.avatar" class="profile-avatar-img" />
            <el-icon v-else :size="22"><UserFilled /></el-icon>
          </div>
          <div class="profile-info" v-show="!sidebarCollapsed">
            <span class="profile-name">{{ currentUser.realName || currentUser.username }}</span>
            <span class="profile-role">{{ roleLabel }}</span>
          </div>
        </div>
      </div>
      <div class="dash-main">
        <component :is="currentComponent" :zone-id="currentZoneId" />
      </div>
    </div>

    <el-dialog v-model="showProfileDialog" :close-on-click-modal="false" class="profile-dialog" width="520px">
      <template #header>
        <div class="dialog-header-custom">
          <div class="dialog-dot"></div>
          <span class="dialog-title">个人信息</span>
          <el-button class="edit-toggle-btn" size="small" @click="toggleEditMode">
            <el-icon><Edit v-if="!isEditing" /><Check v-else /></el-icon>
            <span>{{ isEditing ? '完成' : '编辑' }}</span>
          </el-button>
        </div>
      </template>
      <div class="profile-content">
        <div class="profile-main">
          <div class="avatar-col">
            <div class="avatar-large" :class="{ 'avatar-glow': isEditing }">
              <img v-if="currentUser.avatar" :src="currentUser.avatar" class="avatar-large-img" />
              <el-icon v-else :size="42"><UserFilled /></el-icon>
            </div>
            <div class="avatar-actions" v-if="isEditing">
              <el-upload
                class="avatar-upload"
                :show-file-list="false"
                :before-upload="beforeAvatarUpload"
                :http-request="handleAvatarUpload"
                accept="image/*"
              >
                <el-button type="primary" size="small" class="upload-btn">
                  <el-icon><Camera /></el-icon>
                  <span>更换</span>
                </el-button>
              </el-upload>
              <el-button v-if="currentUser.avatar" size="small" class="remove-btn" @click="handleRemoveAvatar" text>
                <el-icon><Delete /></el-icon>
                <span>移除</span>
              </el-button>
            </div>
          </div>
          <div class="fields-col">
            <div class="field-group">
              <div class="field-item">
                <span class="field-name">用户名</span>
                <span class="field-val readonly">{{ currentUser.username }}</span>
              </div>
              <div class="field-item">
                <span class="field-name">姓名</span>
                <el-input v-if="isEditing" v-model="editForm.realName" placeholder="请输入姓名" class="field-input" maxlength="20" size="small" />
                <span v-else class="field-val">{{ currentUser.realName || '--' }}</span>
              </div>
              <div class="field-item">
                <span class="field-name">电话</span>
                <el-input v-if="isEditing" v-model="editForm.phone" placeholder="请输入手机号" class="field-input" maxlength="11" size="small" />
                <span v-else class="field-val">{{ currentUser.phone || '--' }}</span>
              </div>
              <div class="field-item">
                <span class="field-name">邮箱</span>
                <el-input v-if="isEditing" v-model="editForm.email" placeholder="请输入邮箱" class="field-input" maxlength="50" size="small" />
                <span v-else class="field-val">{{ currentUser.email || '--' }}</span>
              </div>
              <div class="field-item">
                <span class="field-name">角色</span>
                <span class="field-val role-badge" :class="roleClass">{{ roleLabel }}</span>
              </div>
            </div>
          </div>
        </div>
        <div class="profile-footer" v-if="isEditing">
          <el-button class="cancel-btn" @click="cancelEdit" size="small">取消</el-button>
          <el-button class="save-btn" @click="saveProfile" :loading="saving" size="small">保存修改</el-button>
        </div>
      </div>
    </el-dialog>

    <el-dialog v-model="reminderPopupVisible" width="500px" class="reminder-popup-dialog" :close-on-click-modal="false" :show-close="false">
      <template #header>
        <div class="reminder-dialog-header">
          <div class="reminder-icon-wrapper">
            <el-icon class="reminder-icon"><AlarmClock /></el-icon>
          </div>
          <div class="reminder-header-text">
            <div class="reminder-title">智能提醒</div>
            <div class="reminder-subtitle">您有 {{ dueReminders.length }} 条待处理提醒</div>
          </div>
        </div>
      </template>
      <div class="reminder-popup-list" v-if="dueReminders.length > 0">
        <div v-for="r in dueReminders" :key="r.id" class="reminder-popup-item">
          <div class="reminder-item-header">
            <div class="reminder-item-title">
              <el-icon class="item-icon"><Bell /></el-icon>
              <span>{{ r.title }}</span>
            </div>
            <div class="reminder-item-time">
              <el-icon><Calendar /></el-icon>
              <span>{{ r.reminderDate }} {{ r.reminderTime }}</span>
            </div>
          </div>
          <div class="reminder-item-content">{{ r.content }}</div>
          <div class="reminder-item-footer">
            <el-button class="dismiss-btn" @click="handleDismissReminder(r)">
              <el-icon><CircleCheck /></el-icon>
              <span>标记已读</span>
            </el-button>
          </div>
        </div>
      </div>
      <template #footer>
        <div class="reminder-dialog-footer">
          <el-button class="footer-cancel-btn" @click="handleDismissAll">
            <el-icon><Check /></el-icon>
            <span>全部已读</span>
          </el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import { ref, reactive, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { getZones } from '@/api/zone'
import { getUserById, uploadAvatar, deleteAvatar, updateUser } from '@/api/user'
import { getSystemSettings } from '@/api/systemSettings'
import WaterQuality from '@/components/WaterQuality.vue'
import AquacultureRecord from '@/components/AquacultureRecord.vue'
import DiseaseControl from '@/components/DiseaseControl.vue'
import WeatherForecast from '@/components/WeatherForecast.vue'
import InventoryManage from '@/components/InventoryManage.vue'
import CostManage from '@/components/CostManage.vue'
import NotificationManage from '@/components/NotificationManage.vue'
import ProductionManage from '@/components/ProductionManage.vue'
import SalesManage from '@/components/SalesManage.vue'
import ReminderManage from '@/components/ReminderManage.vue'
import { getDueReminders, dismissReminder } from '@/api/reminder'
import KnowledgeManage from '@/components/KnowledgeManage.vue'
import TraceManage from '@/components/TraceManage.vue'
import { TrendCharts, Document, Warning, PartlyCloudy, Ship, Setting, SwitchButton, Fold, Expand, UserFilled, ArrowDown, Edit, Check, Camera, Delete, User, EditPen, Phone, Message, Avatar, Calendar, Box, Money, Bell, AlarmClock, Reading, CircleCheck, ShoppingCart } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import { getRoleLabel, getRoleClass, isBreeder, isManagerOrAbove, isOperatorOrAbove, isSuperAdmin } from '@/utils/permission'

export default {
  name: 'Dashboard',
  components: { 
    WaterQuality, AquacultureRecord, DiseaseControl, WeatherForecast,
    InventoryManage, CostManage, NotificationManage, ProductionManage, SalesManage,
    ReminderManage, KnowledgeManage, TraceManage,
    TrendCharts, Document, Warning, PartlyCloudy, Ship, Setting, SwitchButton, Fold, Expand, UserFilled, ArrowDown, Edit, Check, Camera, Delete, User, EditPen, Phone, Message, Avatar, Calendar, Box, Money, Bell, AlarmClock, Reading, CircleCheck, ShoppingCart
  },
  setup() {
    const router = useRouter()
    const activeMenu = ref('water')
    const zones = ref([])
    const currentZoneId = ref(null)
    const sidebarCollapsed = ref(false)
    const showProfileDialog = ref(false)
    const isEditing = ref(false)
    const saving = ref(false)
    const editForm = reactive({
      realName: '',
      phone: '',
      email: ''
    })
    const systemSettings = reactive({
      basic: {
        systemName: '海洋养殖管理系统',
        announcement: ''
      }
    })

    const storedUser = JSON.parse(localStorage.getItem('user'))
    const currentUser = reactive({
      id: storedUser?.id,
      username: storedUser?.username || '',
      realName: storedUser?.realName || '',
      phone: storedUser?.phone || '',
      email: storedUser?.email || '',
      role: storedUser?.role || 'BREEDER',
      avatar: storedUser?.avatar || ''
    })

    const isBreederUser = computed(() => isBreeder(currentUser.role))
    const isManagerOrAboveUser = computed(() => isManagerOrAbove(currentUser.role))
    const isOperatorOrAboveUser = computed(() => isOperatorOrAbove(currentUser.role))
    const isSuperAdminUser = computed(() => isSuperAdmin(currentUser.role))
    const roleLabel = computed(() => getRoleLabel(currentUser.role))
    const roleClass = computed(() => getRoleClass(currentUser.role))

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
        case 'production': return isManagerOrAboveUser.value ? 'ProductionManage' : 'WaterQuality'
        case 'inventory': return isManagerOrAboveUser.value ? 'InventoryManage' : 'WaterQuality'
        case 'cost': return isManagerOrAboveUser.value ? 'CostManage' : 'WaterQuality'
        case 'sales': return isManagerOrAboveUser.value ? 'SalesManage' : 'WaterQuality'
        case 'notification': return 'NotificationManage'
        case 'reminder': return 'ReminderManage'
        case 'knowledge': return 'KnowledgeManage'
        case 'trace': return isManagerOrAboveUser.value ? 'TraceManage' : 'WaterQuality'
        default: return 'WaterQuality'
      }
    })

    const syncUserToLocal = () => {
      const user = JSON.parse(localStorage.getItem('user'))
      if (user) {
        user.avatar = currentUser.avatar
        user.realName = currentUser.realName
        user.phone = currentUser.phone
        user.email = currentUser.email
        localStorage.setItem('user', JSON.stringify(user))
      }
    }

    const toggleEditMode = () => {
      if (isEditing.value) {
        saveProfile()
      } else {
        editForm.realName = currentUser.realName || ''
        editForm.phone = currentUser.phone || ''
        editForm.email = currentUser.email || ''
        isEditing.value = true
      }
    }

    const cancelEdit = () => {
      isEditing.value = false
    }

    const saveProfile = async () => {
      saving.value = true
      try {
        await updateUser(currentUser.id, {
          username: currentUser.username,
          password: undefined,
          realName: editForm.realName || null,
          phone: editForm.phone || null,
          email: editForm.email || null,
          role: currentUser.role,
          enabled: true,
          id: currentUser.id
        })
        currentUser.realName = editForm.realName
        currentUser.phone = editForm.phone
        currentUser.email = editForm.email
        syncUserToLocal()
        isEditing.value = false
        ElMessage.success('个人信息已更新')
      } catch (error) {
        ElMessage.error('更新失败，请重试')
      } finally {
        saving.value = false
      }
    }

    const refreshUser = async () => {
      if (!currentUser.id) return
      try {
        const res = await getUserById(currentUser.id)
        if (res.data) {
          currentUser.avatar = res.data.avatar || ''
          currentUser.realName = res.data.realName || ''
          currentUser.phone = res.data.phone || ''
          currentUser.email = res.data.email || ''
          syncUserToLocal()
        }
      } catch (error) { console.error(error) }
    }

    const beforeAvatarUpload = (file) => {
      const isImage = file.type.startsWith('image/')
      if (!isImage) {
        ElMessage.error('请选择图片文件')
        return false
      }
      const isLt500K = file.size / 1024 < 500
      if (!isLt500K) {
        ElMessage.error('头像大小不能超过 500KB')
        return false
      }
      return true
    }

    const handleAvatarUpload = async (options) => {
      try {
        const res = await uploadAvatar(currentUser.id, options.file)
        currentUser.avatar = res.data.avatar
        syncUserToLocal()
        ElMessage.success('头像更新成功')
      } catch (error) {
        ElMessage.error('头像上传失败')
      }
    }

    const handleRemoveAvatar = async () => {
      try {
        await deleteAvatar(currentUser.id)
        currentUser.avatar = ''
        syncUserToLocal()
        ElMessage.success('头像已移除')
      } catch (error) {
        ElMessage.error('操作失败')
      }
    }

    const loadZones = async () => {
      try {
        const res = await getZones()
        zones.value = res.data
        if (zones.value.length > 0) currentZoneId.value = zones.value[0].id
      } catch (error) { console.error(error) }
    }

    const loadSystemSettings = async () => {
      try {
        const res = await getSystemSettings()
        if (res.data) {
          if (res.data.basic) {
            systemSettings.basic.systemName = res.data.basic.systemName || '海洋养殖管理系统'
            systemSettings.basic.announcement = res.data.basic.announcement || ''
          }
        }
      } catch (error) { console.error(error) }
    }

    const handleMenuSelect = (key) => { 
      const managerMenus = ['production', 'inventory', 'cost', 'sales', 'trace']
      
      if (managerMenus.includes(key) && !isManagerOrAboveUser.value) {
        ElMessage.warning('您没有权限访问此模块')
        activeMenu.value = 'water'
      } else {
        activeMenu.value = key 
      }
    }

    const reminderPopupVisible = ref(false)
    const dueReminders = ref([])

    const checkDueReminders = async () => {
      if (!currentUser.id) return
      try {
        const res = await getDueReminders({ userId: currentUser.id })
        const list = Array.isArray(res.data) ? res.data : []
        if (list.length > 0) {
          dueReminders.value = list
          reminderPopupVisible.value = true
        }
      } catch (e) { /* silent */ }
    }

    const handleDismissReminder = async (reminder) => {
      try {
        await dismissReminder(reminder.id)
        dueReminders.value = dueReminders.value.filter(r => r.id !== reminder.id)
        if (dueReminders.value.length === 0) reminderPopupVisible.value = false
      } catch (e) { /* ignore */ }
    }

    const handleDismissAll = async () => {
      for (const r of dueReminders.value) {
        try { await dismissReminder(r.id) } catch (e) { /* ignore */ }
      }
      dueReminders.value = []
      reminderPopupVisible.value = false
    }
    const handleZoneClick = (data) => { currentZoneId.value = data.id }
    const logout = () => { localStorage.removeItem('user'); router.push('/login') }

    onMounted(() => {
      loadZones()
      refreshUser()
      checkDueReminders()
      loadSystemSettings()
      setInterval(checkDueReminders, 60000)
    })

    return {
      activeMenu, zoneTree, currentZoneId, currentComponent, 
      isBreederUser, isManagerOrAboveUser, isOperatorOrAboveUser, isSuperAdminUser, roleLabel, roleClass,
      sidebarCollapsed, showProfileDialog, currentUser, isEditing, saving, editForm,
      handleMenuSelect, handleZoneClick, logout,
      beforeAvatarUpload, handleAvatarUpload, handleRemoveAvatar, refreshUser,
      toggleEditMode, cancelEdit, saveProfile,
      reminderPopupVisible, dueReminders, handleDismissReminder, handleDismissAll,
      systemSettings
    }
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
  padding: 0 20px;
  height: 56px;
  background: rgba(12, 22, 40, 0.95);
  backdrop-filter: blur(12px);
  border-bottom: 1px solid rgba(80, 150, 220, 0.1);
  box-shadow: 0 2px 20px rgba(0, 0, 0, 0.3);
  z-index: 100;
  flex-shrink: 0;
}

.announcement-bar {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 10px 20px;
  background: linear-gradient(90deg, rgba(255, 204, 0, 0.15), rgba(255, 204, 0, 0.05));
  border-bottom: 1px solid rgba(255, 204, 0, 0.15);
  color: #ffcc00;
  font-size: 13px;
}

.announcement-icon {
  flex-shrink: 0;
  font-size: 16px;
}

.announcement-text {
  color: rgba(255, 204, 0, 0.9);
  font-weight: 500;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 12px;
}

.collapse-btn {
  width: 34px;
  height: 34px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 8px;
  color: rgba(255, 255, 255, 0.5);
  cursor: pointer;
  transition: all 0.3s;
}
.collapse-btn:hover {
  background: rgba(80, 150, 220, 0.12);
  color: #5b9bd5;
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
  font-size: 17px;
  font-weight: 700;
  color: #d6e8f8;
  letter-spacing: 1.5px;
  transition: opacity 0.3s;
}
.brand-text.hide { opacity: 0; width: 0; overflow: hidden; }

.header-right { display: flex; align-items: center; gap: 12px; }

.hdr-btn {
  background: rgba(255, 255, 255, 0.06) !important;
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

.user-menu-trigger {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 5px 12px 5px 5px;
  background: rgba(255, 255, 255, 0.06);
  border: 1px solid rgba(80, 150, 220, 0.1);
  border-radius: 24px;
  cursor: pointer;
  transition: all 0.3s;
}
.user-menu-trigger:hover {
  background: rgba(80, 150, 220, 0.1);
  border-color: rgba(80, 150, 220, 0.25);
}
.user-menu-trigger:focus-visible {
  outline: none;
}

.avatar-wrapper {
  width: 30px;
  height: 30px;
  border-radius: 50%;
  background: rgba(80, 150, 220, 0.15);
  display: flex;
  align-items: center;
  justify-content: center;
  color: #5b9bd5;
  overflow: hidden;
  flex-shrink: 0;
}
.avatar-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}
.user-name {
  font-size: 13px;
  color: #c0d8f0;
  font-weight: 500;
}
.role-tag {
  border-radius: 6px;
  padding: 0 8px;
  font-size: 11px;
  font-weight: 500;
  border: none;
}
.role-tag.role-super {
  background: rgba(255, 107, 53, 0.15);
  color: #ff8a65;
}
.role-tag.role-operator {
  background: rgba(0, 212, 255, 0.15);
  color: #00d4ff;
}
.role-tag.role-manager {
  background: rgba(255, 204, 0, 0.15);
  color: #ffcc00;
}
.role-tag.role-breeder {
  background: rgba(0, 255, 136, 0.12);
  color: #00ff88;
}
.dropdown-arrow {
  color: rgba(255, 255, 255, 0.3);
  transition: transform 0.3s;
}
.user-menu-trigger:hover .dropdown-arrow {
  color: rgba(255, 255, 255, 0.6);
}

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
  transition: width 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}
.dash-aside.collapsed { width: 64px; }

.menu-section {
  padding: 12px 8px 4px;
  overflow: hidden;
  overflow-y: auto;
}

.menu-group-title {
  font-size: 10px;
  font-weight: 600;
  color: rgba(80, 150, 220, 0.45);
  text-transform: uppercase;
  letter-spacing: 1px;
  padding: 8px 12px 4px;
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
  padding: 0 12px 0;
  overflow-y: auto;
}
.zone-title {
  font-size: 11px;
  font-weight: 600;
  color: rgba(80, 150, 220, 0.5);
  text-transform: uppercase;
  letter-spacing: 1px;
  padding: 8px 12px 8px;
  border-top: 1px solid rgba(80, 150, 220, 0.06);
  margin-top: 4px;
}
.zone-tree { background: transparent; }
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
.zone-tree :deep(.el-tree-node__expand-icon) { color: rgba(80, 150, 220, 0.5); }

.user-profile {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 12px 16px;
  border-top: 1px solid rgba(80, 150, 220, 0.08);
  background: rgba(12, 22, 40, 0.5);
  flex-shrink: 0;
  transition: all 0.3s;
}
.user-profile.profile-collapsed {
  justify-content: center;
  padding: 12px 8px;
}
.profile-avatar {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  background: rgba(80, 150, 220, 0.15);
  display: flex;
  align-items: center;
  justify-content: center;
  color: #5b9bd5;
  overflow: hidden;
  flex-shrink: 0;
  cursor: pointer;
  transition: all 0.3s;
  border: 2px solid rgba(80, 150, 220, 0.2);
}
.profile-avatar:hover {
  border-color: #5b9bd5;
  box-shadow: 0 0 12px rgba(91, 155, 213, 0.3);
}
.profile-avatar-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}
.profile-info {
  display: flex;
  flex-direction: column;
  gap: 1px;
  min-width: 0;
}
.profile-name {
  font-size: 13px;
  font-weight: 600;
  color: #d6e8f8;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}
.profile-role {
  font-size: 11px;
  color: rgba(80, 150, 220, 0.6);
}

.dash-main {
  flex: 1;
  overflow-y: auto;
  background: transparent;
}



.dialog-header-custom {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 18px 24px 14px;
}
.dialog-dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background: #5b9bd5;
  box-shadow: 0 0 8px rgba(91, 155, 213, 0.5);
  flex-shrink: 0;
}
.dialog-title {
  font-size: 17px;
  font-weight: 700;
  color: #d6e8f8 !important;
  letter-spacing: 1px;
  flex: 1;
}
.edit-toggle-btn {
  background: rgba(91, 155, 213, 0.1) !important;
  border: 1px solid rgba(91, 155, 213, 0.18) !important;
  color: #8cb8e0 !important;
  border-radius: 8px !important;
  padding: 5px 14px !important;
  font-size: 12px !important;
  transition: all 0.3s;
}
.edit-toggle-btn:hover {
  background: rgba(91, 155, 213, 0.2) !important;
  border-color: #5b9bd5 !important;
  color: #fff !important;
}
.edit-toggle-btn span {
  color: inherit !important;
}

.profile-content {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.profile-main {
  display: flex;
  gap: 24px;
}

.avatar-col {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 12px;
  padding: 16px 12px;
  flex-shrink: 0;
}
.avatar-large {
  width: 88px;
  height: 88px;
  border-radius: 50%;
  background: linear-gradient(135deg, rgba(91, 155, 213, 0.15), rgba(33, 120, 220, 0.06));
  display: flex;
  align-items: center;
  justify-content: center;
  color: #5b9bd5;
  overflow: hidden;
  border: 2px solid rgba(80, 150, 220, 0.2);
  transition: all 0.3s;
}
.avatar-large.avatar-glow {
  border-color: #5b9bd5;
  box-shadow: 0 0 24px rgba(91, 155, 213, 0.3);
}
.avatar-large-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}
.avatar-actions {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 6px;
}
.upload-btn {
  background: rgba(91, 155, 213, 0.12) !important;
  border: 1px solid rgba(91, 155, 213, 0.2) !important;
  color: #8cb8e0 !important;
  border-radius: 8px !important;
  padding: 6px 14px !important;
  font-size: 12px !important;
  gap: 4px;
}
.upload-btn:hover {
  background: rgba(91, 155, 213, 0.25) !important;
  border-color: #5b9bd5 !important;
  color: #fff !important;
}
.upload-btn span {
  color: inherit !important;
}
.remove-btn {
  color: rgba(255, 107, 53, 0.5) !important;
  font-size: 12px !important;
  padding: 2px 8px !important;
  gap: 3px;
}
.remove-btn:hover {
  color: #ff6b35 !important;
  background: transparent !important;
}
.remove-btn span {
  color: inherit !important;
}

.fields-col {
  flex: 1;
  min-width: 0;
}
.field-group {
  display: flex;
  flex-direction: column;
  gap: 0;
}
.field-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 11px 0;
  border-bottom: 1px solid rgba(80, 150, 220, 0.05);
  min-height: 42px;
}
.field-item:last-child {
  border-bottom: none;
}
.field-name {
  font-size: 13px;
  color: rgba(255, 255, 255, 0.55) !important;
  flex-shrink: 0;
}
.field-val {
  font-size: 14px;
  color: #c0d8f0 !important;
  font-weight: 500;
  text-align: right;
  word-break: break-all;
}
.field-val.readonly {
  color: rgba(255, 255, 255, 0.4) !important;
}
.role-badge {
  padding: 2px 10px;
  border-radius: 12px;
  font-size: 11px;
  font-weight: 600;
}
.role-badge.role-super {
  background: rgba(255, 107, 53, 0.15);
  color: #ff8a65;
}
.role-badge.role-operator {
  background: rgba(0, 212, 255, 0.15);
  color: #00d4ff;
}
.role-badge.role-manager {
  background: rgba(255, 204, 0, 0.15);
  color: #ffcc00;
}
.role-badge.role-breeder {
  background: rgba(0, 255, 136, 0.12);
  color: #00ff88;
}

.field-input {
  width: 160px;
}
.field-input :deep(.el-input__wrapper) {
  background: rgba(255, 255, 255, 0.06) !important;
  border: 1px solid rgba(80, 150, 220, 0.12) !important;
  border-radius: 8px !important;
  box-shadow: none !important;
  transition: all 0.3s;
}
.field-input :deep(.el-input__wrapper:hover) {
  border-color: rgba(80, 150, 220, 0.25) !important;
}
.field-input :deep(.el-input__wrapper.is-focus) {
  border-color: #5b9bd5 !important;
  box-shadow: 0 0 0 1px rgba(91, 155, 213, 0.15) !important;
}
.field-input :deep(.el-input__inner) {
  color: #d6e8f8 !important;
  font-size: 13px;
  height: 28px;
}
.field-input :deep(.el-input__inner::placeholder) {
  color: rgba(255, 255, 255, 0.2);
}

.profile-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
  padding-top: 4px;
  border-top: 1px solid rgba(80, 150, 220, 0.05);
}
.cancel-btn {
  background: rgba(255, 255, 255, 0.06) !important;
  border: 1px solid rgba(255, 255, 255, 0.1) !important;
  color: rgba(255, 255, 255, 0.45) !important;
  border-radius: 8px !important;
}
.cancel-btn:hover {
  background: rgba(255, 255, 255, 0.1) !important;
  color: rgba(255, 255, 255, 0.65) !important;
}
.save-btn {
  background: rgba(91, 155, 213, 0.18) !important;
  border: 1px solid rgba(91, 155, 213, 0.3) !important;
  color: #8cb8e0 !important;
  border-radius: 8px !important;
  transition: all 0.3s;
}
.save-btn:hover {
  background: rgba(91, 155, 213, 0.3) !important;
  border-color: #5b9bd5 !important;
  color: #fff !important;
}

/* ===== 用户下拉菜单样式 ===== */
.user-dropdown-menu {
  background: #0f1c2e !important;
  border: 1px solid rgba(80, 150, 220, 0.12) !important;
  border-radius: 12px !important;
  box-shadow: 0 12px 40px rgba(0, 0, 0, 0.5) !important;
  padding: 8px !important;
  min-width: 200px;
}
.user-dropdown-menu .el-dropdown-menu__item {
  color: rgba(255, 255, 255, 0.7) !important;
  border-radius: 8px;
  padding: 10px 12px !important;
  font-size: 13px;
  display: flex;
  align-items: center;
  gap: 8px;
}
.user-dropdown-menu .el-dropdown-menu__item:hover {
  background: rgba(80, 150, 220, 0.1) !important;
  color: #c0d8f0 !important;
}
.user-dropdown-menu .el-dropdown-menu__item .el-icon {
  font-size: 16px;
  color: rgba(80, 150, 220, 0.6);
}
.user-dropdown-menu .el-dropdown-menu__item:hover .el-icon {
  color: #5b9bd5;
}
.user-dropdown-menu .el-dropdown-menu__item--divided {
  border-top: 1px solid rgba(80, 150, 220, 0.08) !important;
  margin: 6px 8px !important;
}
.user-dropdown-menu .el-dropdown-menu__item--divided::before {
  display: none;
}
.dropdown-header {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 8px 12px 12px;
}
.dropdown-avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  background: rgba(80, 150, 220, 0.15);
  display: flex;
  align-items: center;
  justify-content: center;
  color: #5b9bd5;
  overflow: hidden;
  flex-shrink: 0;
  border: 2px solid rgba(80, 150, 220, 0.2);
}
.dropdown-avatar img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}
.dropdown-info {
  display: flex;
  flex-direction: column;
  gap: 2px;
  min-width: 0;
}
.dropdown-name {
  font-size: 14px;
  font-weight: 600;
  color: #d6e8f8;
}
.dropdown-role {
  font-size: 11px;
  color: rgba(80, 150, 220, 0.6);
}

/* ===== 提醒弹窗样式 ===== */
.reminder-popup-dialog {
  border-radius: 16px !important;
  overflow: hidden;
}
.reminder-popup-dialog :deep(.el-dialog) {
  background: linear-gradient(145deg, #0f1c2e, #0b1522) !important;
  border: 1px solid rgba(80, 150, 220, 0.15) !important;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.6), 0 0 40px rgba(91, 155, 213, 0.1) !important;
  border-radius: 16px !important;
}
.reminder-popup-dialog :deep(.el-dialog__header) {
  background: linear-gradient(135deg, rgba(91, 155, 213, 0.1), rgba(33, 120, 220, 0.03)) !important;
  border-bottom: 1px solid rgba(80, 150, 220, 0.08) !important;
  padding: 20px 24px !important;
  margin: 0 !important;
}
.reminder-popup-dialog :deep(.el-dialog__body) {
  padding: 24px !important;
}
.reminder-popup-dialog :deep(.el-dialog__footer) {
  padding: 16px 24px 24px !important;
  border-top: 1px solid rgba(80, 150, 220, 0.08) !important;
}

.reminder-dialog-header {
  display: flex;
  align-items: center;
  gap: 16px;
}
.reminder-icon-wrapper {
  width: 52px;
  height: 52px;
  border-radius: 14px;
  background: linear-gradient(135deg, rgba(255, 204, 0, 0.18), rgba(255, 138, 101, 0.12));
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 4px 20px rgba(255, 204, 0, 0.15), inset 0 1px 0 rgba(255, 204, 0, 0.1);
  flex-shrink: 0;
  animation: pulse-glow 2s ease-in-out infinite;
}
@keyframes pulse-glow {
  0%, 100% { box-shadow: 0 4px 20px rgba(255, 204, 0, 0.15), inset 0 1px 0 rgba(255, 204, 0, 0.1); }
  50% { box-shadow: 0 4px 30px rgba(255, 204, 0, 0.3), inset 0 1px 0 rgba(255, 204, 0, 0.15); }
}
.reminder-icon {
  font-size: 28px;
  color: #ffcc00;
  filter: drop-shadow(0 0 8px rgba(255, 204, 0, 0.4));
}
.reminder-header-text {
  display: flex;
  flex-direction: column;
  gap: 4px;
}
.reminder-title {
  font-size: 18px;
  font-weight: 700;
  color: #d6e8f8;
  letter-spacing: 0.5px;
}
.reminder-subtitle {
  font-size: 13px;
  color: rgba(140, 184, 224, 0.7);
}

.reminder-popup-list {
  display: flex;
  flex-direction: column;
  gap: 14px;
  max-height: 400px;
  overflow-y: auto;
}
.reminder-popup-list::-webkit-scrollbar {
  width: 6px;
}
.reminder-popup-list::-webkit-scrollbar-thumb {
  background: rgba(80, 150, 220, 0.2);
  border-radius: 3px;
}

.reminder-popup-item {
  background: linear-gradient(135deg, rgba(12, 22, 40, 0.6), rgba(8, 14, 26, 0.4));
  border: 1px solid rgba(80, 150, 220, 0.12);
  border-radius: 14px;
  padding: 18px;
  transition: all 0.3s;
}
.reminder-popup-item:hover {
  border-color: rgba(80, 150, 220, 0.25);
  background: linear-gradient(135deg, rgba(12, 22, 40, 0.75), rgba(8, 14, 26, 0.5));
  transform: translateY(-2px);
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.3);
}

.reminder-item-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  gap: 12px;
  margin-bottom: 10px;
}
.reminder-item-title {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 15px;
  font-weight: 600;
  color: #d6e8f8;
}
.reminder-item-title .item-icon {
  font-size: 18px;
  color: #ff8a65;
  flex-shrink: 0;
}
.reminder-item-time {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 12px;
  color: rgba(140, 184, 224, 0.7);
  background: rgba(80, 150, 220, 0.08);
  padding: 4px 10px;
  border-radius: 8px;
  flex-shrink: 0;
}
.reminder-item-time .el-icon {
  font-size: 13px;
}

.reminder-item-content {
  font-size: 14px;
  color: #a0c4e0;
  line-height: 1.6;
  margin-bottom: 14px;
}

.reminder-item-footer {
  display: flex;
  justify-content: flex-end;
}
.dismiss-btn {
  background: linear-gradient(135deg, rgba(91, 155, 213, 0.15), rgba(33, 120, 220, 0.08)) !important;
  border: 1px solid rgba(91, 155, 213, 0.25) !important;
  color: #8cb8e0 !important;
  border-radius: 10px !important;
  padding: 8px 18px !important;
  font-size: 13px !important;
  font-weight: 500 !important;
  transition: all 0.3s !important;
  display: flex !important;
  align-items: center !important;
  gap: 6px !important;
}
.dismiss-btn:hover {
  background: linear-gradient(135deg, rgba(91, 155, 213, 0.28), rgba(33, 120, 220, 0.15)) !important;
  border-color: #5b9bd5 !important;
  color: #fff !important;
  transform: translateY(-1px);
  box-shadow: 0 4px 14px rgba(91, 155, 213, 0.25) !important;
}

.reminder-dialog-footer {
  display: flex;
  justify-content: flex-end;
}
.footer-cancel-btn {
  background: rgba(255, 255, 255, 0.06) !important;
  border: 1px solid rgba(255, 255, 255, 0.12) !important;
  color: #a0c4e0 !important;
  border-radius: 10px !important;
  padding: 10px 24px !important;
  font-size: 14px !important;
  font-weight: 500 !important;
  transition: all 0.3s !important;
  display: flex !important;
  align-items: center !important;
  gap: 6px !important;
}
.footer-cancel-btn:hover {
  background: rgba(80, 150, 220, 0.12) !important;
  border-color: rgba(80, 150, 220, 0.25) !important;
  color: #c0d8f0 !important;
}

@media (max-width: 768px) {
  .dash-aside { position: fixed; left: 0; top: 56px; bottom: 0; z-index: 99; transform: translateX(-100%); }
  .dash-aside.collapsed { transform: translateX(0); width: 250px; }
  .hdr-btn-text { display: none; }
  .user-name { display: none; }
  .brand-text { display: none; }
}
</style>
