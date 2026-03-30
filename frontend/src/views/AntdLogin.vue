<template>
  <div class="login-container">
    <div class="login-content">
      <div class="login-left">
        <div class="welcome">
          <SafetyCertificateTwoTone two-tone-color="#1890ff" style="font-size: 64px" />
          <h1>驾校场地智能监管系统</h1>
          <p>AI驱动 · 智能监测 · 实时预警</p>
          <div class="features">
            <div class="feature-item">
              <CheckCircleFilled style="color: #52c41a" />
              <span>AI违规行为智能识别</span>
            </div>
            <div class="feature-item">
              <CheckCircleFilled style="color: #52c41a" />
              <span>实时预警告警推送</span>
            </div>
            <div class="feature-item">
              <CheckCircleFilled style="color: #52c41a" />
              <span>学员教练车辆统一管理</span>
            </div>
            <div class="feature-item">
              <CheckCircleFilled style="color: #52c41a" />
              <span>数据统计分析报表</span>
            </div>
          </div>
        </div>
      </div>

      <div class="login-right">
        <a-card class="login-card">
          <div class="login-header">
            <h2>欢迎登录</h2>
            <p>请输入您的账号和密码</p>
          </div>

          <a-form
            :model="formState"
            :rules="rules"
            @finish="handleLogin"
          >
            <a-form-item name="username">
              <a-input
                v-model:value="formState.username"
                size="large"
                placeholder="用户名"
              >
                <template #prefix>
                  <UserOutlined />
                </template>
              </a-input>
            </a-form-item>

            <a-form-item name="password">
              <a-input-password
                v-model:value="formState.password"
                size="large"
                placeholder="密码"
              >
                <template #prefix>
                  <LockOutlined />
                </template>
              </a-input-password>
            </a-form-item>

            <a-form-item>
              <div class="login-options">
                <a-checkbox v-model:checked="formState.remember">
                  记住我
                </a-checkbox>
                <a class="forgot-password">忘记密码?</a>
              </div>
            </a-form-item>

            <a-form-item>
              <a-button
                type="primary"
                html-type="submit"
                size="large"
                block
                :loading="loading"
              >
                登录
              </a-button>
            </a-form-item>
          </a-form>

          <a-divider>其他登录方式</a-divider>

          <div class="other-login">
            <a-button shape="circle" size="large">
              <template #icon>
                <WechatOutlined />
              </template>
            </a-button>
            <a-button shape="circle" size="large">
              <template #icon>
                <QqOutlined />
              </template>
            </a-button>
            <a-button shape="circle" size="large">
              <template #icon>
                <AlipayCircleOutlined />
              </template>
            </a-button>
          </div>

          <div class="login-footer">
            还没有账号？<a>立即注册</a>
          </div>
        </a-card>
      </div>
    </div>
  </div>
</template>

<script setup>
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { message } from 'ant-design-vue'
import {
  UserOutlined,
  LockOutlined,
  CheckCircleFilled,
  WechatOutlined,
  QqOutlined,
  AlipayCircleOutlined,
  SafetyCertificateTwoTone
} from '@ant-design/icons-vue'

const router = useRouter()
const loading = ref(false)

const formState = reactive({
  username: 'admin',
  password: '123456',
  remember: true
})

const rules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码长度至少6位', trigger: 'blur' }
  ]
}

const handleLogin = async () => {
  loading.value = true

  try {
    // 模拟登录请求
    await new Promise(resolve => setTimeout(resolve, 1000))

    if (formState.username === 'admin' && formState.password === '123456') {
      localStorage.setItem('token', 'mock-token-123456')
      message.success('登录成功')
      router.push('/dashboard')
    } else {
      message.error('用户名或密码错误')
    }
  } catch (error) {
    message.error('登录失败，请重试')
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.login-container {
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 20px;
}

.login-content {
  display: flex;
  background: white;
  border-radius: 16px;
  overflow: hidden;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.3);
  max-width: 1000px;
  width: 100%;
  min-height: 600px;
}

.login-left {
  flex: 1;
  background: linear-gradient(135deg, #1890ff 0%, #096dd9 100%);
  padding: 60px 50px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
}

.welcome {
  text-align: center;
}

.welcome h1 {
  color: white;
  font-size: 32px;
  margin: 24px 0 16px;
}

.welcome p {
  font-size: 16px;
  opacity: 0.9;
  margin-bottom: 40px;
}

.features {
  text-align: left;
  display: grid;
  gap: 16px;
}

.feature-item {
  display: flex;
  align-items: center;
  gap: 12px;
  font-size: 15px;
}

.login-right {
  flex: 1;
  padding: 60px 50px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.login-card {
  width: 100%;
  border: none;
  box-shadow: none;
}

.login-header {
  text-align: center;
  margin-bottom: 32px;
}

.login-header h2 {
  font-size: 28px;
  margin-bottom: 8px;
}

.login-header p {
  color: #8c8c8c;
}

.login-options {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.forgot-password {
  color: #1890ff;
  cursor: pointer;
}

.other-login {
  display: flex;
  justify-content: center;
  gap: 16px;
  margin-bottom: 24px;
}

.login-footer {
  text-align: center;
  color: #8c8c8c;
}

.login-footer a {
  color: #1890ff;
}

@media (max-width: 768px) {
  .login-content {
    flex-direction: column;
  }

  .login-left {
    padding: 40px 30px;
  }

  .login-right {
    padding: 40px 30px;
  }
}
</style>
