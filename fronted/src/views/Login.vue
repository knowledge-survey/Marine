<template>
  <div class="login-container">
    <el-card class="login-card">
      <template #header>
        <div class="login-header">
          <h2>海洋养殖管理系统</h2>
          <p>Marine Aquaculture Management System</p>
        </div>
      </template>
      <el-form :model="loginForm" label-width="0px" class="login-form">
        <el-form-item>
          <div class="input-wrapper">
            <el-icon class="input-icon"><User /></el-icon>
            <el-input v-model="loginForm.username" placeholder="请输入用户名" size="large" />
          </div>
        </el-form-item>
        <el-form-item>
          <div class="input-wrapper">
            <el-icon class="input-icon"><Lock /></el-icon>
            <el-input v-model="loginForm.password" type="password" placeholder="请输入密码" size="large" show-password />
          </div>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleLogin" size="large" class="login-button">登录</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script>
import { login } from '@/api/user'
import { useRouter } from 'vue-router'
import { reactive } from 'vue'
import { User, Lock } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'

export default {
  name: 'Login',
  components: { User, Lock },
  setup() {
    const router = useRouter()
    const loginForm = reactive({
      username: '',
      password: ''
    })

    const handleLogin = async () => {
      try {
        const res = await login(loginForm)
        if (res.data) {
          localStorage.setItem('user', JSON.stringify(res.data))
          router.push('/dashboard')
        }
      } catch (error) {
        ElMessage.error('登录失败，请检查用户名和密码')
      }
    }

    return {
      loginForm,
      handleLogin
    }
  }
}
</script>

<style scoped>
.login-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background: linear-gradient(135deg, #0c1929 0%, #1e3a5f 50%, #0d2b4a 100%);
  position: relative;
  overflow: hidden;
}

.login-container::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-image: 
    radial-gradient(circle at 20% 80%, rgba(0, 212, 255, 0.1) 0%, transparent 50%),
    radial-gradient(circle at 80% 20%, rgba(0, 212, 255, 0.1) 0%, transparent 50%);
  animation: pulse 8s ease-in-out infinite;
}

@keyframes pulse {
  0%, 100% { opacity: 0.3; }
  50% { opacity: 0.6; }
}

.login-card {
  width: 420px;
  background: rgba(20, 40, 60, 0.9) !important;
  border: 1px solid rgba(0, 212, 255, 0.3) !important;
  border-radius: 12px !important;
  box-shadow: 
    0 0 30px rgba(0, 212, 255, 0.2),
    0 10px 40px rgba(0, 0, 0, 0.4);
  z-index: 1;
}

.login-card :deep(.el-card__header) {
  border-bottom: 1px solid rgba(0, 212, 255, 0.2) !important;
  padding: 24px;
}

.login-header {
  text-align: center;
}

.login-header h2 {
  margin: 0 0 8px 0;
  color: #00d4ff;
  font-size: 28px;
  font-weight: 700;
  letter-spacing: 2px;
  text-shadow: 0 0 10px rgba(0, 212, 255, 0.5);
}

.login-header p {
  margin: 0;
  color: rgba(255, 255, 255, 0.7);
  font-size: 13px;
  letter-spacing: 1px;
}

.login-form {
  padding: 12px 0;
}

.input-wrapper {
  position: relative;
  display: flex;
  align-items: center;
}

.input-icon {
  position: absolute;
  left: 12px;
  color: #00d4ff;
  font-size: 18px;
  z-index: 1;
}

.input-wrapper :deep(.el-input__wrapper) {
  padding-left: 40px !important;
  background: rgba(0, 0, 0, 0.3) !important;
  border: 1px solid rgba(0, 212, 255, 0.3) !important;
  box-shadow: none !important;
  transition: all 0.3s;
}

.input-wrapper :deep(.el-input__wrapper:hover) {
  border-color: rgba(0, 212, 255, 0.6) !important;
}

.input-wrapper :deep(.el-input__wrapper.is-focus) {
  border-color: #00d4ff !important;
  box-shadow: 0 0 15px rgba(0, 212, 255, 0.3) !important;
}

.input-wrapper :deep(.el-input__inner) {
  color: #ffffff !important;
}

.input-wrapper :deep(.el-input__inner::placeholder) {
  color: rgba(255, 255, 255, 0.5) !important;
}

.login-button {
  width: 100%;
  height: 48px;
  font-size: 16px;
  font-weight: 600;
  background: linear-gradient(135deg, #00d4ff 0%, #0077ff 100%);
  border: none;
  box-shadow: 0 4px 15px rgba(0, 212, 255, 0.4);
  transition: all 0.3s;
}

.login-button:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(0, 212, 255, 0.6);
}

.login-button:active {
  transform: translateY(0);
}
</style>
