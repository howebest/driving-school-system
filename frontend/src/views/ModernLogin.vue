<template>
  <div class="modern-login">
    <div class="login-container">
      <div class="login-card">
        <!-- Logo区域 -->
        <div class="logo-section">
          <div class="logo-wrapper">
            <div class="logo-icon">
              <svg viewBox="0 0 24 24" width="32" height="32">
                <path fill="currentColor" d="M12 2L2 7v10c0 5.55 3.84 9.74 9 11 5.16-1.26 9-5.45 9-11V7l-10-5z"/>
              </svg>
            </div>
            <h1 class="system-title">驾校场地监管系统</h1>
            <p class="system-subtitle">Driving School Management Platform</p>
          </div>
        </div>

        <!-- 登录表单 -->
        <div class="form-section">
          <el-form
            ref="loginFormRef"
            :model="loginForm"
            :rules="loginRules"
            class="login-form"
            @keyup.enter="handleLogin"
          >
            <div class="form-title">
              <h2>欢迎回来</h2>
              <p>请使用您的账号登录系统</p>
            </div>

            <el-form-item prop="username">
              <div class="input-wrapper">
                <el-input
                  v-model="loginForm.username"
                  size="large"
                  placeholder="请输入用户名"
                  prefix-icon="User"
                  class="custom-input"
                />
              </div>
            </el-form-item>

            <el-form-item prop="password">
              <div class="input-wrapper">
                <el-input
                  v-model="loginForm.password"
                  type="password"
                  size="large"
                  placeholder="请输入密码"
                  prefix-icon="Lock"
                  show-password
                  class="custom-input"
                />
              </div>
            </el-form-item>

            <el-form-item>
              <div class="remember-forgot">
                <el-checkbox v-model="rememberMe">记住我</el-checkbox>
                <el-link type="primary" :underline="false">忘记密码？</el-link>
              </div>
            </el-form-item>

            <el-form-item>
              <el-button
                type="primary"
                size="large"
                class="login-button"
                :loading="loading"
                @click="handleLogin"
              >
                {{ loading ? '登录中...' : '立即登录' }}
              </el-button>
            </el-form-item>

            <div class="divider">
              <span>或</span>
            </div>

            <div class="social-login">
              <el-button class="social-btn" @click="demoLogin">
                <svg viewBox="0 0 24 24" width="18" height="18">
                  <path fill="#4285F4" d="M22.56 12.25c0-.78-.07-1.53-.2-2.25H12v4.26h5.92c-.26 1.37-1.04 2.53-2.21 3.31v2.77h3.57c2.08-1.92 3.28-4.74 3.28-8.09z"/>
                  <path fill="#34A853" d="M12 23c2.97 0 5.46-.98 7.28-2.66l-3.57-2.77c-.98.66-2.23 1.06-3.71 1.06-2.86 0-5.29-1.93-6.16-4.53H2.18v2.84C3.99 20.53 7.7 23 12 23z"/>
                  <path fill="#FBBC05" d="M5.84 14.09c-.22-.66-.35-1.36-.35-2.09s.13-1.43.35-2.09V7.07H2.18C1.43 8.55 1 10.22 1 12s.43 3.45 1.18 4.93l2.85-2.22.81-.62z"/>
                  <path fill="#EA4335" d="M12 5.38c1.62 0 3.06.56 4.21 1.64l3.15-3.15C17.45 2.09 14.97 1 12 1 7.7 1 3.99 3.47 2.18 7.07l3.66 2.84c.87-2.6 3.3-4.53 6.16-4.53z"/>
                </svg>
                演示登录
              </el-button>
            </div>
          </el-form>
        </div>

        <!-- 底部信息 -->
        <div class="footer-section">
          <p>© 2024 驾校场地监管系统. 保留所有权利.</p>
        </div>
      </div>

      <!-- 装饰元素 -->
      <div class="decorative-elements">
        <div class="circle circle-1"></div>
        <div class="circle circle-2"></div>
        <div class="circle circle-3"></div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { User, Lock } from '@element-plus/icons-vue'

const router = useRouter()
const loginFormRef = ref()
const loading = ref(false)
const rememberMe = ref(false)

const loginForm = reactive({
  username: 'admin',
  password: '123456'
})

const loginRules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码长度不能少于6位', trigger: 'blur' }
  ]
}

const handleLogin = async () => {
  if (!loginFormRef.value) return
  
  await loginFormRef.value.validate(async (valid) => {
    if (valid) {
      loading.value = true
      try {
        // 模拟登录请求
        setTimeout(() => {
          localStorage.setItem('token', 'fake-token')
          ElMessage.success('登录成功')
          router.push('/')
          loading.value = false
        }, 1500)
      } catch (error) {
        ElMessage.error('登录失败')
        loading.value = false
      }
    }
  })
}

const demoLogin = () => {
  loginForm.username = 'admin'
  loginForm.password = '123456'
  handleLogin()
}
</script>

<style scoped>
.modern-login {
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
  overflow: hidden;
}

.login-container {
  position: relative;
  z-index: 10;
  width: 100%;
  max-width: 420px;
  margin: 20px;
}

.login-card {
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10px);
  border-radius: 20px;
  box-shadow: 0 25px 50px rgba(0, 0, 0, 0.15);
  overflow: hidden;
  animation: slideUp 0.6s ease-out;
}

@keyframes slideUp {
  from {
    opacity: 0;
    transform: translateY(30px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

/* Logo区域 */
.logo-section {
  padding: 40px 40px 20px;
  text-align: center;
  background: linear-gradient(135deg, #f8fafc 0%, #e2e8f0 100%);
}

.logo-wrapper {
  margin-bottom: 20px;
}

.logo-icon {
  width: 64px;
  height: 64px;
  background: linear-gradient(135deg, #3b82f6, #60a5fa);
  border-radius: 16px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  margin: 0 auto 20px;
  box-shadow: 0 10px 25px rgba(59, 130, 246, 0.3);
}

.system-title {
  font-size: 24px;
  font-weight: 700;
  color: #1e293b;
  margin: 0 0 8px;
  letter-spacing: -0.5px;
}

.system-subtitle {
  font-size: 14px;
  color: #64748b;
  margin: 0;
  font-weight: 400;
}

/* 表单区域 */
.form-section {
  padding: 30px 40px;
}

.form-title {
  text-align: center;
  margin-bottom: 32px;
}

.form-title h2 {
  font-size: 28px;
  font-weight: 700;
  color: #1e293b;
  margin: 0 0 8px;
}

.form-title p {
  font-size: 15px;
  color: #64748b;
  margin: 0;
}

.input-wrapper {
  position: relative;
}

.custom-input :deep(.el-input__wrapper) {
  border-radius: 12px;
  padding: 2px 16px;
  height: 52px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
  transition: all 0.3s ease;
}

.custom-input :deep(.el-input__wrapper:hover) {
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.12);
}

.custom-input :deep(.el-input__wrapper.is-focus) {
  box-shadow: 0 0 0 3px rgba(59, 130, 246, 0.1);
  border-color: #3b82f6;
}

.custom-input :deep(.el-input__prefix) {
  margin-right: 12px;
}

.custom-input :deep(.el-input__inner) {
  font-size: 16px;
  color: #1e293b;
}

.login-form :deep(.el-form-item) {
  margin-bottom: 24px;
}

.remember-forgot {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 14px;
}

.remember-forgot :deep(.el-checkbox__label) {
  color: #64748b;
}

.login-button {
  width: 100%;
  height: 52px;
  border-radius: 12px;
  font-size: 16px;
  font-weight: 600;
  background: linear-gradient(135deg, #3b82f6, #60a5fa);
  border: none;
  box-shadow: 0 4px 15px rgba(59, 130, 246, 0.3);
  transition: all 0.3s ease;
}

.login-button:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(59, 130, 246, 0.4);
}

.divider {
  position: relative;
  text-align: center;
  margin: 28px 0;
  color: #94a3b8;
  font-size: 14px;
}

.divider::before {
  content: '';
  position: absolute;
  top: 50%;
  left: 0;
  right: 0;
  height: 1px;
  background: linear-gradient(90deg, transparent, #cbd5e1, transparent);
}

.divider span {
  background: white;
  padding: 0 16px;
  position: relative;
}

.social-login {
  text-align: center;
}

.social-btn {
  width: 100%;
  height: 48px;
  border-radius: 12px;
  border: 1px solid #e2e8f0;
  color: #64748b;
  font-weight: 500;
  transition: all 0.3s ease;
}

.social-btn:hover {
  border-color: #3b82f6;
  color: #3b82f6;
  background: rgba(59, 130, 246, 0.05);
}

.social-btn svg {
  margin-right: 8px;
  vertical-align: middle;
}

/* 底部区域 */
.footer-section {
  padding: 20px 40px;
  text-align: center;
  background: #f8fafc;
  border-top: 1px solid #e2e8f0;
}

.footer-section p {
  font-size: 13px;
  color: #94a3b8;
  margin: 0;
}

/* 装饰元素 */
.decorative-elements {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  pointer-events: none;
}

.circle {
  position: absolute;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.1);
  animation: float 6s ease-in-out infinite;
}

.circle-1 {
  width: 120px;
  height: 120px;
  top: 10%;
  left: 5%;
  animation-delay: 0s;
}

.circle-2 {
  width: 80px;
  height: 80px;
  top: 60%;
  right: 8%;
  animation-delay: 2s;
}

.circle-3 {
  width: 60px;
  height: 60px;
  bottom: 20%;
  left: 15%;
  animation-delay: 4s;
}

@keyframes float {
  0%, 100% {
    transform: translateY(0px) rotate(0deg);
  }
  50% {
    transform: translateY(-20px) rotate(180deg);
  }
}

/* 响应式设计 */
@media (max-width: 480px) {
  .login-container {
    margin: 10px;
  }
  
  .login-card {
    border-radius: 16px;
  }
  
  .logo-section {
    padding: 30px 30px 15px;
  }
  
  .form-section {
    padding: 25px 30px;
  }
  
  .system-title {
    font-size: 20px;
  }
  
  .form-title h2 {
    font-size: 24px;
  }
}
</style>