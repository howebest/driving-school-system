<template>
  <a-layout style="min-height: 100vh">
    <!-- 侧边栏 -->
    <a-layout-sider
      v-model:collapsed="collapsed"
      collapsible
      :width="240"
      :theme="'light'"
      style="border-right: 1px solid #f0f0f0"
    >
      <!-- Logo -->
      <div class="logo">
        <SafetyCertificateTwoTone two-tone-color="#1890ff" style="font-size: 24px" />
        <span v-if="!collapsed" class="logo-text">驾校监管系统</span>
      </div>

      <!-- 菜单 -->
      <a-menu
        v-model:selectedKeys="selectedKeys"
        mode="inline"
        :style="{ borderRight: 0 }"
      >
        <a-menu-item key="dashboard" @click="navigateTo('/dashboard')">
          <template #icon>
            <HomeOutlined />
          </template>
          <span>首页概览</span>
        </a-menu-item>

        <a-sub-menu key="info">
          <template #icon>
            <TeamOutlined />
          </template>
          <template #title>基础信息管理</template>
          <a-menu-item key="student" @click="navigateTo('/student')">
            <UserOutlined />
            <span>学员管理</span>
          </a-menu-item>
          <a-menu-item key="coach" @click="navigateTo('/coach')">
            <SolutionOutlined />
            <span>教练管理</span>
          </a-menu-item>
          <a-menu-item key="car" @click="navigateTo('/car')">
            <CarOutlined />
            <span>车辆管理</span>
          </a-menu-item>
          <a-menu-item key="site" @click="navigateTo('/site')">
            <EnvironmentOutlined />
            <span>场地管理</span>
          </a-menu-item>
        </a-sub-menu>

        <a-sub-menu key="monitor">
          <template #icon>
            <MonitorOutlined />
          </template>
          <template #title>监控与预警</template>
          <a-menu-item key="monitor-realtime" @click="navigateTo('/monitor')">
            <EyeOutlined />
            <span>实时监测</span>
          </a-menu-item>
          <a-menu-item key="warning" @click="navigateTo('/warning')">
            <AlertOutlined />
            <span>预警管理</span>
          </a-menu-item>
          <a-menu-item key="record" @click="navigateTo('/record')">
            <FileTextOutlined />
            <span>违规记录</span>
          </a-menu-item>
        </a-sub-menu>

        <a-menu-item key="report" @click="navigateTo('/report')">
          <template #icon>
            <BarChartOutlined />
          </template>
          <span>统计报表</span>
        </a-menu-item>

        <a-menu-item key="settings" @click="navigateTo('/settings')">
          <template #icon>
            <SettingOutlined />
          </template>
          <span>系统设置</span>
        </a-menu-item>
      </a-menu>
    </a-layout-sider>

    <!-- 主内容区 -->
    <a-layout>
      <!-- 顶部导航栏 -->
      <a-layout-header class="header">
        <div class="header-left">
          <a-breadcrumb>
            <a-breadcrumb-item>
              <HomeOutlined />
            </a-breadcrumb-item>
            <a-breadcrumb-item>{{ currentPageTitle }}</a-breadcrumb-item>
          </a-breadcrumb>
        </div>

        <div class="header-right">
          <!-- 今日告警 -->
          <a-badge :count="alertCount" :offset="[-5, 5]">
            <a-button type="primary" danger @click="showAlerts">
              <BellOutlined /> 今日告警
            </a-button>
          </a-badge>

          <!-- 数据分析 -->
          <a-button type="primary" @click="navigateTo('/report')">
            <BarChartOutlined /> 数据分析
          </a-button>

          <!-- 通知 -->
          <a-badge :count="notificationCount" :offset="[-5, 5]">
            <a-button shape="circle">
              <template #icon>
                <MessageOutlined />
              </template>
            </a-button>
          </a-badge>

          <!-- 用户信息 -->
          <a-dropdown>
            <a-button>
              <a-avatar style="background-color: #1890ff" :size="24">
                {{ userName.substring(0, 1) }}
              </a-avatar>
              <span style="margin-left: 8px">{{ userName }}</span>
              <DownOutlined />
            </a-button>
            <template #overlay>
              <a-menu>
                <a-menu-item key="profile">
                  <UserOutlined />
                  <span>个人中心</span>
                </a-menu-item>
                <a-menu-divider />
                <a-menu-item key="logout" @click="handleLogout">
                  <LogoutOutlined />
                  <span>退出登录</span>
                </a-menu-item>
              </a-menu>
            </template>
          </a-dropdown>
        </div>
      </a-layout-header>

      <!-- 内容区域 -->
      <a-layout-content class="content">
        <router-view />
      </a-layout-content>

      <!-- 底部 -->
      <a-layout-footer class="footer">
        驾校场地智能监管系统 ©2024 版权所有
      </a-layout-footer>
    </a-layout>
  </a-layout>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { message } from 'ant-design-vue'
import {
  HomeOutlined,
  TeamOutlined,
  UserOutlined,
  SolutionOutlined,
  CarOutlined,
  EnvironmentOutlined,
  MonitorOutlined,
  EyeOutlined,
  AlertOutlined,
  FileTextOutlined,
  BarChartOutlined,
  SettingOutlined,
  BellOutlined,
  MessageOutlined,
  LogoutOutlined,
  DownOutlined,
  SafetyCertificateTwoTone
} from '@ant-design/icons-vue'

const router = useRouter()
const route = useRoute()

const collapsed = ref(false)
const selectedKeys = ref(['dashboard'])
const userName = ref('管理员')
const alertCount = ref(18)
const notificationCount = ref(3)

const currentPageTitle = computed(() => {
  return route.meta?.title || '首页'
})

const navigateTo = (path) => {
  router.push(path)
}

const showAlerts = () => {
  message.info('查看今日告警详情')
}

const handleLogout = () => {
  localStorage.removeItem('token')
  router.push('/login')
  message.success('已退出登录')
}
</script>

<style scoped>
.logo {
  height: 48px;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 16px;
  border-bottom: 1px solid #f0f0f0;
  background: #fff;
}

.logo-text {
  font-size: 16px;
  font-weight: 600;
  color: #1890ff;
  margin-left: 12px;
}

.header {
  background: #fff;
  padding: 0 24px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  box-shadow: 0 1px 4px rgba(0, 21, 41, 0.08);
  z-index: 10;
}

.header-left {
  display: flex;
  align-items: center;
}

.header-right {
  display: flex;
  align-items: center;
  gap: 12px;
}

.content {
  margin: 16px;
  padding: 24px;
  background: #fff;
  border-radius: 8px;
  min-height: 280px;
  overflow-y: auto;
}

.footer {
  text-align: center;
  background: #fff;
  border-top: 1px solid #f0f0f0;
}
</style>
