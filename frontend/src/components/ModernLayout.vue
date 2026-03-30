<template>
  <div class="layout-container">
    <el-container style="height: 100vh;">
      <!-- 侧边栏 -->
      <el-aside width="240px" class="sidebar">
        <div class="logo">驾校监管系统</div>
        <el-menu
          :default-active="$route.path"
          class="menu"
          router
          background-color="#304156"
          text-color="#bfcbd9"
          active-text-color="#409eff"
        >
          <el-menu-item index="/dashboard">
            <el-icon><House /></el-icon>
            <span>首页</span>
          </el-menu-item>
          
          <el-sub-menu index="basic">
            <template #title>
              <el-icon><Management /></el-icon>
              <span>基础管理</span>
            </template>
            <el-menu-item index="/student">
              <el-icon><User /></el-icon>
              <span>学员管理</span>
            </el-menu-item>
            <el-menu-item index="/coach">
              <el-icon><UserFilled /></el-icon>
              <span>教练管理</span>
            </el-menu-item>
            <el-menu-item index="/car">
              <el-icon><Van /></el-icon>
              <span>车辆管理</span>
            </el-menu-item>
            <el-menu-item index="/site">
              <el-icon><Location /></el-icon>
              <span>场地管理</span>
            </el-menu-item>
          </el-sub-menu>
          
          <el-sub-menu index="monitor">
            <template #title>
              <el-icon><VideoCamera /></el-icon>
              <span>监控管理</span>
            </template>
            <el-menu-item index="/monitor">
              <el-icon><VideoCamera /></el-icon>
              <span>违规监测</span>
            </el-menu-item>
            <el-menu-item index="/warning">
              <el-icon><Bell /></el-icon>
              <span>预警管理</span>
            </el-menu-item>
            <el-menu-item index="/record">
              <el-icon><Document /></el-icon>
              <span>违规记录</span>
            </el-menu-item>
          </el-sub-menu>
          
          <el-menu-item index="/report">
            <el-icon><PieChart /></el-icon>
            <span>统计报表</span>
          </el-menu-item>
        </el-menu>
      </el-aside>

      <!-- 主体内容 -->
      <el-container>
        <!-- 头部 -->
        <el-header class="header">
          <div class="header-left">
            <h2>{{ $route.meta.title || '驾校场地监管系统' }}</h2>
          </div>
          <div class="header-right">
            <el-dropdown @command="handleCommand">
              <span class="user-info">
                <el-avatar :size="30" src="" />
                <span class="username">管理员</span>
              </span>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item command="settings">系统设置</el-dropdown-item>
                  <el-dropdown-item command="logout">退出登录</el-dropdown-item>
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
    </el-container>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router'
import { ElMessageBox } from 'element-plus'
import { House, Management, VideoCamera, PieChart, User, UserFilled, Van, Location, Bell, Document } from '@element-plus/icons-vue'

const router = useRouter()

const handleCommand = (command) => {
  if (command === 'settings') {
    router.push('/settings')
  } else if (command === 'logout') {
    ElMessageBox.confirm('确定要退出登录吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }).then(() => {
      localStorage.removeItem('token')
      router.push('/login')
    })
  }
}
</script>

<style scoped>
.layout-container {
  height: 100vh;
}

.sidebar {
  background-color: #304156;
  overflow-y: auto;
}

.logo {
  height: 60px;
  line-height: 60px;
  text-align: center;
  color: white;
  font-size: 18px;
  font-weight: bold;
  background-color: #2b2f3a;
}

.menu {
  border-right: none;
}

.header {
  background-color: #fff;
  border-bottom: 1px solid #dcdfe6;
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 20px;
}

.header-left h2 {
  margin: 0;
  color: #303133;
}

.user-info {
  display: flex;
  align-items: center;
  cursor: pointer;
}

.username {
  margin-left: 10px;
  color: #606266;
}

.main-content {
  background-color: #f5f7fa;
  padding: 20px;
  overflow-y: auto;
}
</style>