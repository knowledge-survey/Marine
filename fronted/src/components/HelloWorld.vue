<template>
  <div class="hello-app">
    <div class="app-wrapper">
      <div class="top-bar">
        <div class="brand">
          <el-icon class="brand-icon"><Ship /></el-icon>
          <span class="brand-text">海洋养殖管理系统</span>
        </div>
      </div>

      <div class="welcome">
        <div class="welcome-visual">
          <div class="welcome-ring">
            <el-icon :size="80"><Ship /></el-icon>
          </div>
        </div>
        <h2 class="welcome-h2">{{ msg }}</h2>
        <p class="welcome-p">海洋养殖综合管理平台 - 水质监测 / 病害防控 / 溯源管理</p>
        <div class="nav-buttons">
          <el-button type="primary" size="large" round @click="goLogin">
            <el-icon><User /></el-icon>
            登录系统
          </el-button>
          <el-button size="large" round @click="goTrace">
            <el-icon><Search /></el-icon>
            溯源查询
          </el-button>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { useRouter } from 'vue-router'
import { Ship, User, Search } from '@element-plus/icons-vue'

export default {
  name: 'HelloWorld',
  components: { Ship, User, Search },
  props: {
    msg: {
      type: String,
      default: '海洋养殖管理系统'
    }
  },
  setup() {
    const router = useRouter()
    const goLogin = () => router.push('/login')
    const goTrace = () => router.push('/traceability')
    return { goLogin, goTrace }
  }
}
</script>

<style scoped>
.hello-app {
  min-height: 100vh;
  padding: 20px;
  background: linear-gradient(170deg, #060d1a 0%, #0d1b2a 25%, #132742 50%, #0f1f35 75%, #080f1e 100%);
  display: flex;
  justify-content: center;
}

.app-wrapper {
  width: 100%;
  max-width: 800px;
  background: rgba(12, 22, 40, 0.88);
  backdrop-filter: blur(24px);
  -webkit-backdrop-filter: blur(24px);
  border: 1px solid rgba(80, 150, 220, 0.12);
  border-radius: 24px;
  box-shadow: 0 30px 80px rgba(0, 0, 0, 0.55), 0 0 0 1px rgba(255, 255, 255, 0.03) inset;
  overflow: hidden;
}

.top-bar {
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 14px 28px;
  background: rgba(8, 18, 32, 0.6);
  border-bottom: 1px solid rgba(80, 150, 220, 0.08);
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

.welcome {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 80px 20px;
  gap: 20px;
}

.welcome-visual { margin-bottom: 8px; }

.welcome-ring {
  width: 160px;
  height: 160px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 50%;
  background: rgba(80, 150, 220, 0.06);
  border: 2px solid rgba(80, 150, 220, 0.15);
  color: rgba(80, 150, 220, 0.4);
  animation: ringPulse 3s ease-in-out infinite;
}

@keyframes ringPulse {
  0%, 100% { box-shadow: 0 0 30px rgba(80, 150, 220, 0.1); }
  50% { box-shadow: 0 0 60px rgba(80, 150, 220, 0.2); }
}

.welcome-h2 {
  margin: 0;
  font-size: 28px;
  font-weight: 700;
  color: #d6e8f8;
}

.welcome-p {
  margin: 0;
  font-size: 14px;
  color: rgba(255, 255, 255, 0.35);
}

.nav-buttons {
  margin-top: 12px;
  display: flex;
  gap: 16px;
}
</style>