<template>
  <div class="login-container">
    <div class="bg-layer">
      <div class="circuit-pattern">
        <div class="circuit-line h1"></div>
        <div class="circuit-line h2"></div>
        <div class="circuit-line h3"></div>
        <div class="circuit-line v1"></div>
        <div class="circuit-line v2"></div>
        <div class="circuit-line v3"></div>
      </div>
      <div class="floating-shapes">
        <div class="shape shape-1"></div>
        <div class="shape shape-2"></div>
        <div class="shape shape-3"></div>
        <div class="shape shape-4"></div>
        <div class="shape shape-5"></div>
        <div class="shape shape-6"></div>
      </div>
      <div class="glow-orbs">
        <div class="orb orb-1"></div>
        <div class="orb orb-2"></div>
        <div class="orb orb-3"></div>
      </div>
      <div class="cube-wrapper">
        <div class="cube">
          <div class="cube-face front"></div>
          <div class="cube-face back"></div>
          <div class="cube-face left"></div>
          <div class="cube-face right"></div>
          <div class="cube-face top"></div>
          <div class="cube-face bottom"></div>
        </div>
        <div class="platform"></div>
      </div>
      <div class="particles">
        <div class="particle" v-for="i in 30" :key="i" :style="particleStyle(i)"></div>
      </div>
    </div>
    <div class="login-card">
      <div class="login-header">
        <div class="logo-icon">
          <el-icon><Cpu /></el-icon>
        </div>
        <h2>海洋养殖管理系统</h2>
        <p>Marine Aquaculture Management System</p>
      </div>
      <el-form :model="loginForm" class="login-form">
        <el-form-item>
          <div class="input-wrapper">
            <el-icon class="input-icon"><User /></el-icon>
            <el-input v-model="loginForm.username" placeholder="请输入账号" size="large" />
          </div>
        </el-form-item>
        <el-form-item>
          <div class="input-wrapper">
            <el-icon class="input-icon"><Lock /></el-icon>
            <el-input v-model="loginForm.password" type="password" placeholder="请输入密码" size="large" show-password />
          </div>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleLogin" size="large" class="login-button">
            登录
          </el-button>
        </el-form-item>
      </el-form>
      <div class="login-footer">
        <el-icon><Cpu /></el-icon>
        <span>林志祥</span>
      </div>
    </div>
  </div>
</template>

<script>
import { login } from '@/api/user'
import { useRouter } from 'vue-router'
import { reactive } from 'vue'
import { User, Lock, Cpu } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'

export default {
  name: 'Login',
  components: { User, Lock, Cpu },
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

    const particleStyle = () => {
      const x = Math.random() * 100
      const y = Math.random() * 100
      const size = Math.random() * 4 + 1
      const delay = Math.random() * 10
      const duration = Math.random() * 10 + 10
      return {
        left: `${x}%`,
        top: `${y}%`,
        width: `${size}px`,
        height: `${size}px`,
        animationDelay: `${delay}s`,
        animationDuration: `${duration}s`
      }
    }

    return {
      loginForm,
      handleLogin,
      particleStyle
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
  background: linear-gradient(135deg, #0a1628 0%, #0d2137 50%, #0a1929 100%);
  position: relative;
  overflow: hidden;
}

.bg-layer {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  z-index: 0;
}

.circuit-pattern {
  position: absolute;
  width: 100%;
  height: 100%;
  opacity: 0.15;
}

.circuit-line {
  position: absolute;
  background: linear-gradient(90deg, transparent, #00d4ff, transparent);
}

.circuit-line.h1 {
  top: 25%;
  left: 0;
  width: 40%;
  height: 1px;
}

.circuit-line.h2 {
  top: 50%;
  left: 30%;
  width: 50%;
  height: 1px;
}

.circuit-line.h3 {
  top: 75%;
  left: 10%;
  width: 35%;
  height: 1px;
}

.circuit-line.v1 {
  top: 10%;
  left: 20%;
  width: 1px;
  height: 30%;
  background: linear-gradient(180deg, transparent, #00d4ff, transparent);
}

.circuit-line.v2 {
  top: 40%;
  left: 60%;
  width: 1px;
  height: 40%;
  background: linear-gradient(180deg, transparent, #00d4ff, transparent);
}

.circuit-line.v3 {
  top: 20%;
  left: 80%;
  width: 1px;
  height: 25%;
  background: linear-gradient(180deg, transparent, #00d4ff, transparent);
}

.floating-shapes {
  position: absolute;
  width: 100%;
  height: 100%;
}

.shape {
  position: absolute;
  border: 1px solid rgba(0, 212, 255, 0.3);
  animation: floatShape 20s ease-in-out infinite;
}

.shape-1 {
  top: 15%;
  left: 15%;
  width: 60px;
  height: 60px;
  transform: rotate(45deg);
  animation-delay: 0s;
}

.shape-2 {
  top: 60%;
  left: 10%;
  width: 40px;
  height: 40px;
  border-radius: 50%;
  animation-delay: -3s;
}

.shape-3 {
  top: 20%;
  right: 20%;
  width: 80px;
  height: 80px;
  border-radius: 50%;
  border-color: rgba(0, 212, 255, 0.15);
  animation-delay: -5s;
}

.shape-4 {
  bottom: 25%;
  left: 25%;
  width: 30px;
  height: 30px;
  transform: rotate(30deg);
  animation-delay: -7s;
}

.shape-5 {
  top: 45%;
  left: 45%;
  width: 50px;
  height: 50px;
  border-color: rgba(0, 119, 255, 0.2);
  animation-delay: -9s;
}

.shape-6 {
  bottom: 15%;
  right: 15%;
  width: 45px;
  height: 45px;
  transform: rotate(60deg);
  animation-delay: -11s;
}

@keyframes floatShape {
  0%, 100% {
    transform: translateY(0) rotate(0deg);
    opacity: 0.5;
  }
  50% {
    transform: translateY(-20px) rotate(180deg);
    opacity: 0.8;
  }
}

.glow-orbs {
  position: absolute;
  width: 100%;
  height: 100%;
}

.orb {
  position: absolute;
  border-radius: 50%;
  filter: blur(60px);
  animation: orbPulse 8s ease-in-out infinite;
}

.orb-1 {
  top: 30%;
  left: 25%;
  width: 200px;
  height: 200px;
  background: rgba(0, 212, 255, 0.15);
  animation-delay: 0s;
}

.orb-2 {
  top: 50%;
  right: 30%;
  width: 150px;
  height: 150px;
  background: rgba(0, 119, 255, 0.1);
  animation-delay: -3s;
}

.orb-3 {
  bottom: 20%;
  left: 40%;
  width: 180px;
  height: 180px;
  background: rgba(0, 212, 255, 0.08);
  animation-delay: -5s;
}

@keyframes orbPulse {
  0%, 100% {
    transform: scale(1);
    opacity: 0.5;
  }
  50% {
    transform: scale(1.2);
    opacity: 0.8;
  }
}

.cube-wrapper {
  position: absolute;
  top: 35%;
  left: 35%;
  perspective: 800px;
}

.cube {
  width: 120px;
  height: 120px;
  position: relative;
  transform-style: preserve-3d;
  animation: rotateCube 15s linear infinite;
}

@keyframes rotateCube {
  0% {
    transform: rotateX(0deg) rotateY(0deg);
  }
  100% {
    transform: rotateX(360deg) rotateY(360deg);
  }
}

.cube-face {
  position: absolute;
  width: 120px;
  height: 120px;
  border: 1px solid rgba(0, 212, 255, 0.4);
  background: rgba(0, 212, 255, 0.05);
  box-shadow: inset 0 0 20px rgba(0, 212, 255, 0.1);
}

.cube-face.front {
  transform: translateZ(60px);
}

.cube-face.back {
  transform: translateZ(-60px) rotateY(180deg);
}

.cube-face.left {
  transform: translateX(-60px) rotateY(-90deg);
}

.cube-face.right {
  transform: translateX(60px) rotateY(90deg);
}

.cube-face.top {
  transform: translateY(-60px) rotateX(90deg);
}

.cube-face.bottom {
  transform: translateY(60px) rotateX(-90deg);
}

.platform {
  position: absolute;
  top: 140px;
  left: 50%;
  transform: translateX(-50%);
  width: 180px;
  height: 40px;
  border: 2px solid rgba(0, 212, 255, 0.3);
  border-radius: 50%;
  box-shadow: 
    0 0 30px rgba(0, 212, 255, 0.3),
    inset 0 0 20px rgba(0, 212, 255, 0.1);
  animation: platformGlow 3s ease-in-out infinite;
}

@keyframes platformGlow {
  0%, 100% {
    box-shadow: 
      0 0 30px rgba(0, 212, 255, 0.3),
      inset 0 0 20px rgba(0, 212, 255, 0.1);
  }
  50% {
    box-shadow: 
      0 0 50px rgba(0, 212, 255, 0.5),
      inset 0 0 30px rgba(0, 212, 255, 0.2);
  }
}

.particles {
  position: absolute;
  width: 100%;
  height: 100%;
}

.particle {
  position: absolute;
  background: rgba(0, 212, 255, 0.6);
  border-radius: 50%;
  animation: particleFloat linear infinite;
}

@keyframes particleFloat {
  0% {
    transform: translateY(100vh) scale(0);
    opacity: 0;
  }
  10% {
    opacity: 1;
  }
  90% {
    opacity: 1;
  }
  100% {
    transform: translateY(-100vh) scale(1);
    opacity: 0;
  }
}

.login-card {
  width: 380px;
  padding: 40px;
  background: rgba(20, 40, 60, 0.45) !important;
  backdrop-filter: blur(20px);
  border: 1px solid rgba(0, 212, 255, 0.2) !important;
  border-radius: 16px !important;
  box-shadow: 
    0 0 40px rgba(0, 212, 255, 0.15),
    0 20px 60px rgba(0, 0, 0, 0.5);
  z-index: 10;
  position: relative;
}

.login-card::before {
  content: '';
  position: absolute;
  top: -1px;
  left: 20%;
  right: 20%;
  height: 2px;
  background: linear-gradient(90deg, transparent, #00d4ff, transparent);
}

.login-header {
  text-align: center;
  margin-bottom: 30px;
}

.logo-icon {
  width: 60px;
  height: 60px;
  margin: 0 auto 16px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, rgba(0, 212, 255, 0.2), rgba(0, 119, 255, 0.2));
  border-radius: 16px;
  border: 1px solid rgba(0, 212, 255, 0.3);
}

.logo-icon .el-icon {
  font-size: 32px;
  color: #00d4ff;
}

.login-header h2 {
  margin: 0 0 8px 0;
  color: #00d4ff;
  font-size: 24px;
  font-weight: 700;
  letter-spacing: 2px;
  text-shadow: 0 0 10px rgba(0, 212, 255, 0.5);
}

.login-header p {
  margin: 0;
  color: rgba(255, 255, 255, 0.5);
  font-size: 12px;
  letter-spacing: 1px;
}

.login-form {
  padding: 0;
}

.login-form :deep(.el-form-item) {
  margin-bottom: 20px;
}

.input-wrapper {
  position: relative;
  display: flex;
  align-items: center;
  width: 100%;
}

.input-wrapper :deep(.el-input) {
  width: 100% !important;
}

.input-icon {
  position: absolute;
  left: 14px;
  color: rgba(0, 212, 255, 0.7);
  font-size: 18px;
  z-index: 1;
  transition: color 0.3s;
}

.input-wrapper:focus-within .input-icon {
  color: #00d4ff;
}

.input-wrapper :deep(.el-input__wrapper) {
  padding-left: 44px !important;
  background: rgba(0, 0, 0, 0.3) !important;
  border: 1px solid rgba(0, 212, 255, 0.2) !important;
  box-shadow: none !important;
  transition: all 0.3s;
  border-radius: 10px !important;
  height: 48px;
}

.input-wrapper :deep(.el-input__wrapper:hover) {
  border-color: rgba(0, 212, 255, 0.5) !important;
}

.input-wrapper :deep(.el-input__wrapper.is-focus) {
  border-color: #00d4ff !important;
  box-shadow: 0 0 20px rgba(0, 212, 255, 0.2) !important;
}

.input-wrapper :deep(.el-input__inner) {
  color: #ffffff !important;
  font-size: 14px;
}

.input-wrapper :deep(.el-input__inner::placeholder) {
  color: rgba(255, 255, 255, 0.4) !important;
}

.login-button {
  width: 100%;
  height: 48px;
  font-size: 16px;
  font-weight: 600;
  background: linear-gradient(135deg, #00d4ff 0%, #0077ff 100%);
  border: none;
  border-radius: 10px;
  box-shadow: 0 4px 20px rgba(0, 212, 255, 0.4);
  transition: all 0.3s;
  letter-spacing: 2px;
}

.login-button:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 25px rgba(0, 212, 255, 0.6);
}

.login-button:active {
  transform: translateY(0);
}

.login-footer {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  margin-top: 24px;
  padding-top: 20px;
  border-top: 1px solid rgba(0, 212, 255, 0.1);
  color: rgba(255, 255, 255, 0.4);
  font-size: 12px;
}

.login-footer .el-icon {
  font-size: 16px;
  color: rgba(0, 212, 255, 0.5);
}
</style>
