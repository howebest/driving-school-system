import { createRouter, createWebHistory } from 'vue-router'
import { useAuthStore } from '@/store/auth'

const routes = [
  {
    path: '/',
    redirect: '/login'
  },
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/auth/Login.vue'),
    meta: { requiresAuth: false }
  },
  {
    path: '/dashboard',
    name: 'Dashboard',
    component: () => import('@/views/dashboard/Dashboard.vue'),
    meta: { requiresAuth: true },
    children: [
      {
        path: '',
        name: 'Home',
        component: () => import('@/views/home/Home.vue')
      },
      // 基础信息管理
      {
        path: 'student-management',
        name: 'StudentManagement',
        component: () => import('@/views/management/StudentManagement.vue')
      },
      {
        path: 'coach-management',
        name: 'CoachManagement',
        component: () => import('@/views/management/CoachManagement.vue')
      },
      {
        path: 'vehicle-management',
        name: 'VehicleManagement',
        component: () => import('@/views/management/VehicleManagement.vue')
      },
      {
        path: 'area-management',
        name: 'AreaManagement',
        component: () => import('@/views/management/AreaManagement.vue')
      },
      // 身份核验
      {
        path: 'face-verification',
        name: 'FaceVerification',
        component: () => import('@/views/verification/FaceVerification.vue')
      },
      {
        path: 'plate-verification',
        name: 'PlateVerification',
        component: () => import('@/views/verification/PlateVerification.vue')
      },
      // 违规监测
      {
        path: 'monitoring',
        name: 'Monitoring',
        component: () => import('@/views/monitoring/Monitoring.vue')
      },
      // 预警告警
      {
        path: 'warning',
        name: 'Warning',
        component: () => import('@/views/warning/Warning.vue')
      },
      // 记录追溯
      {
        path: 'records',
        name: 'Records',
        component: () => import('@/views/records/Records.vue')
      },
      // 统计报表
      {
        path: 'statistics',
        name: 'Statistics',
        component: () => import('@/views/statistics/Statistics.vue')
      },
      // 系统设置
      {
        path: 'settings',
        name: 'Settings',
        component: () => import('@/views/settings/Settings.vue')
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 路由守卫
router.beforeEach((to, from, next) => {
  const authStore = useAuthStore()
  
  if (to.meta.requiresAuth && !authStore.isAuthenticated) {
    next('/login')
  } else if (to.path === '/login' && authStore.isAuthenticated) {
    next('/dashboard')
  } else {
    next()
  }
})

export default router