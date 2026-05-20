import { ElMessage } from 'element-plus'
import { createRouter, createWebHistory } from 'vue-router'
import Login from '@/views/Login.vue'
import Dashboard from '@/views/Dashboard.vue'
import Admin from '@/views/Admin.vue'
import Traceability from '@/views/Traceability.vue'
import TraceabilityPublic from '@/views/TraceabilityPublic.vue'
import { ROLES } from '@/utils/permission'

const routes = [
  { path: '/', redirect: '/login' },
  { path: '/login', name: 'Login', component: Login, meta: { title: '登录' } },
  { path: '/dashboard', name: 'Dashboard', component: Dashboard, meta: { title: '工作台', requiresAuth: true } },
  { path: '/admin', name: 'Admin', component: Admin, meta: { title: '管理后台', requiresAuth: true, minRole: ROLES.MANAGER } },
  { path: '/traceability', name: 'Traceability', component: Traceability, meta: { title: '溯源管理', requiresAuth: true, minRole: ROLES.MANAGER } },
  { path: '/traceability/:code', name: 'TraceabilityPublic', component: TraceabilityPublic, meta: { title: '溯源查询' } }
]

const router = createRouter({ history: createWebHistory(), routes })

router.beforeEach((to, from, next) => {
  document.title = to.meta.title ? `${to.meta.title} | 海洋养殖管理系统` : '海洋养殖管理系统'

  const storedUser = localStorage.getItem('user')
  const user = storedUser ? JSON.parse(storedUser) : null

  if (to.meta.requiresAuth) {
    if (!user || !user.username) {
      next({ name: 'Login', query: { redirect: to.fullPath } })
      return
    }
    if (to.meta.minRole) {
      const userRoleLevel = getRoleLevel(user.role)
      const minRoleLevel = getRoleLevel(to.meta.minRole)
      if (userRoleLevel < minRoleLevel) {
        ElMessage.warning('权限不足')
        next('/dashboard')
        return
      }
    }
  }

  if (to.name === 'Login' && user && user.username) {
    next('/dashboard')
    return
  }

  next()
})

function getRoleLevel(role) {
  switch (role) {
    case ROLES.SUPER_ADMIN: return 4
    case ROLES.OPERATOR: return 3
    case ROLES.MANAGER: return 2
    case ROLES.BREEDER: return 1
    default: return 0
  }
}

export default router
