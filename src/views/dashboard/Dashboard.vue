<template>
  <div class="dashboard-layout">
    <!-- 侧边栏 -->
    <el-aside width="220px" class="sidebar">
      <div class="logo">
        <h2>驾校监管系统</h2>
      </div>
      
      <el-menu
        :default-active="activeMenu"
        class="sidebar-menu"
        background-color="#304156"
        text-color="#bfcbd9"
        active-text-color="#409eff"
        router
      >
        <el-menu-item index="/dashboard">
          <el-icon><House /></el-icon>
          <span>首页概览</span>
        </el-menu-item>
        
        <el-sub-menu index="management">
          <template #title>
            <el-icon><Management /></el-icon>
            <span>基础信息管理</span>
          </template>
          <el-menu-item index="/dashboard/student-management" v-if="hasPermission('view_student')">
            <el-icon><User /></el-icon>
            <span>学员管理</span>
          </el-menu-item>
          <el-menu-item index="/dashboard/coach-management" v-if="hasPermission('view_coach')">
            <el-icon><UserFilled /></el-icon>
            <span>教练管理</span>
          </el-menu-item>
          <el-menu-item index="/dashboard/vehicle-management" v-if="hasPermission('view_vehicle')">
            <el-icon><Van /></el-icon>
            <span>车辆管理</span>
          </el-menu-item>
          <el-menu-item index="/dashboard/area-management" v-if="hasPermission('view_area')">
            <el-icon><Location /></el-icon>
            <span>场地管理</span>
          </el-menu-item>
        </el-sub-menu>
        
        <el-sub-menu index="verification">
          <template #title>
            <el-icon><CreditCard /></el-icon>
            <span>身份核验</span>
          </template>
          <el-menu-item index="/dashboard/face-verification">
            <el-icon><User /></el-icon>
            <span>人脸识别</span>
          </el-menu-item>
          <el-menu-item index="/dashboard/plate-verification">
            <el-icon><CreditCard /></el-icon>
            <span>车牌识别</span>
          </el-menu-item>
        </el-sub-menu>
        
        <el-menu-item index="/dashboard/monitoring" v-if="hasPermission('view_monitoring')">
          <el-icon><VideoCamera /></el-icon>
          <span>违规监测</span>
        </el-menu-item>
        
        <el-menu-item index="/dashboard/warning" v-if="hasPermission('view_warning')">
          <el-icon><Bell /></el-icon>
          <span>预警告警</span>
          <el-badge v-if="unreadWarningCount > 0" :value="unreadWarningCount" class="warning-badge" />
        </el-menu-item>
        
        <el-menu-item index="/dashboard/records" v-if="hasPermission('view_records')">
          <el-icon><Document /></el-icon>
          <span>记录追溯</span>
        </el-menu-item>
        
        <el-menu-item index="/dashboard/statistics" v-if="hasPermission('view_statistics')">
          <el-icon><PieChart /></el-icon>
          <span>统计报表</span>
        </el-menu-item>
        
        <el-menu-item index="/dashboard/settings" v-if="hasPermission('system_settings')">
          <el-icon><Setting /></el-icon>
          <span>系统设置</span>
        </el-menu-item>
      </el-menu>
    </el-aside>
    
    <!-- 主体内容 -->
    <el-container>
      <!-- 顶部导航栏 -->
      <el-header class="header">
        <div class="header-left">
          <h3>{{ currentPageTitle }}</h3>
        </div>
        
        <div class="header-right">
          <!-- 消息提醒 -->
          <el-popover
            placement="bottom"
            :width="300"
            trigger="click"
          >
            <template #reference>
              <el-badge :value="unreadWarningCount" :max="99" class="notification-badge">
                <el-button icon="Bell" circle />
              </el-badge>
            </template>
            
            <div class="notification-panel">
              <h4>最新预警</h4>
              <div v-if="recentWarnings.length === 0" class="no-warnings">
                暂无新预警
              </div>
              <div v-else class="warning-list">
                <div
                  v-for="warning in recentWarnings"
                  :key="warning.id"
                  class="warning-item"
                  @click="goToWarningDetail(warning.id)"
                >
                  <div class="warning-title">{{ warning.type }}预警</div>
                  <div class="warning-content">{{ warning.content }}</div>
                  <div class="warning-time">{{ formatTime(warning.createTime) }}</div>
                </div>
              </div>
            </div>
          </el-popover>
          
          <!-- 用户信息 -->
          <el-dropdown @command="handleUserCommand">
            <span class="user-info">
              <el-avatar :size="32" :src="userInfo.avatar">{{ userName.charAt(0) }}</el-avatar>
              <span class="user-name">{{ userName }}</span>
              <el-icon><ArrowDown /></el-icon>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="profile">个人信息</el-dropdown-item>
                <el-dropdown-item command="logout" divided>退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </el-header>
      
      <!-- 内容区域 -->
      <el-main class="main-content">
        <router-view />
      </el-main>
    </el-container>
  </div>
</template>

<script setup>
import { ref, computed, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { useAuthStore } from '@/store/auth'
import { useWarningStore } from '@/store/warning'

const route = useRoute()
const router = useRouter()
const authStore = useAuthStore()
const warningStore = useWarningStore()

const menuTitles = {
  '/dashboard': '首页概览',
  '/dashboard/student-management': '学员管理',
  '/dashboard/coach-management': '教练管理',
  '/dashboard/vehicle-management': '车辆管理',
  '/dashboard/area-management': '场地管理',
  '/dashboard/face-verification': '人脸识别',
  '/dashboard/plate-verification': '车牌识别',
  '/dashboard/monitoring': '违规监测',
  '/dashboard/warning': '预警告警',
  '/dashboard/records': '记录追溯',
  '/dashboard/statistics': '统计报表',
  '/dashboard/settings': '系统设置'
}

const activeMenu = computed(() => route.path)
const currentPageTitle = computed(() => menuTitles[route.path] || '未知页面')
const userName = computed(() => authStore.userName)
const userInfo = computed(() => authStore.userInfo)
const unreadWarningCount = computed(() => warningStore.getUnreadCount())
const recentWarnings = computed(() => warningStore.warnings.slice(0, 5))

const hasPermission = (permission) => {
  return authStore.hasPermission(permission)
}

const formatTime = (time) => {
  return new Date(time).toLocaleString('zh-CN')
}

const goToWarningDetail = (id) => {
  warningStore.markAsRead(id)
  router.push('/dashboard/warning')
}

const handleUserCommand = (command) => {
  switch (command) {
    case 'profile':
      ElMessage.info('个人信息功能待开发')
      break
    case 'logout':
      authStore.logout()
      router.push('/login')
      ElMessage.success('已退出登录')
      break
  }
}
</script>

<style scoped>
.dashboard-layout {
  height: 100vh;
  display: flex;
}

.sidebar {
  background-color: #304156;
  overflow-y: auto;
}

.logo {
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: #2c3e50;
  color: white;
}

.logo h2 {
  margin: 0;
  font-size: 18px;
  font-weight: 600;
}

.sidebar-menu {
  border: none;
  height: calc(100% - 60px);
}

.warning-badge {
  margin-left: 10px;
}

.header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 20px;
  background-color: white;
  border-bottom: 1px solid #eee;
}

.header-left h3 {
  margin: 0;
  color: #333;
}

.header-right {
  display: flex;
  align-items: center;
  gap: 20px;
}

.notification-badge {
  cursor: pointer;
}

.notification-panel h4 {
  margin: 0 0 15px 0;
  color: #333;
}

.no-warnings {
  text-align: center;
  color: #999;
  padding: 20px 0;
}

.warning-list {
  max-height: 300px;
  overflow-y: auto;
}

.warning-item {
  padding: 10px;
  border-bottom: 1px solid #eee;
  cursor: pointer;
}

.warning-item:hover {
  background-color: #f5f7fa;
}

.warning-title {
  font-weight: 500;
  color: #333;
  margin-bottom: 5px;
}

.warning-content {
  font-size: 13px;
  color: #666;
  margin-bottom: 5px;
}

.warning-time {
  font-size: 12px;
  color: #999;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  padding: 5px 10px;
  border-radius: 4px;
}

.user-info:hover {
  background-color: #f5f7fa;
}

.user-name {
  font-size: 14px;
  color: #333;
}

.main-content {
  background-color: #f0f2f5;
  padding: 20px;
  overflow-y: auto;
}
</style>