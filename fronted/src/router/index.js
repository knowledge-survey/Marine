import { createRouter, createWebHistory } from 'vue-router'
import Login from '@/views/Login.vue'
import Dashboard from '@/views/Dashboard.vue'
import Admin from '@/views/Admin.vue'
import Traceability from '@/views/Traceability.vue'
import TraceabilityPublic from '@/views/TraceabilityPublic.vue'

const routes = [
  {
    path: '/',
    redirect: '/login'
  },
  {
    path: '/login',
    name: 'Login',
    component: Login
  },
  {
    path: '/dashboard',
    name: 'Dashboard',
    component: Dashboard
  },
  {
    path: '/admin',
    name: 'Admin',
    component: Admin
  },
  {
    path: '/traceability',
    name: 'Traceability',
    component: Traceability
  },
  {
    path: '/traceability/:code',
    name: 'TraceabilityPublic',
    component: TraceabilityPublic
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

export default router
