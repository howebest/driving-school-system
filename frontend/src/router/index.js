import { createRouter, createWebHistory } from 'vue-router'
import AntdLayout from '@/components/AntdLayout.vue'

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/AntdLogin.vue'),
    meta: { title: '登录' }
  },
  {
    path: '/',
    component: AntdLayout,
    redirect: '/dashboard',
    children: [
      {
        path: 'dashboard',
        name: 'Dashboard',
        component: () => import('@/views/AntdDashboard.vue'),
        meta: { title: '首页概览' }
      },
      {
        path: 'student',
        name: 'Student',
        component: () => import('@/views/student/AntdStudentList.vue'),
        meta: { title: '学员管理' }
      },
      {
        path: 'coach',
        name: 'Coach',
        component: () => import('@/views/coach/AntdCoachList.vue'),
        meta: { title: '教练管理' }
      },
      {
        path: 'car',
        name: 'Car',
        component: () => import('@/views/car/AntdCarList.vue'),
        meta: { title: '车辆管理' }
      },
      {
        path: 'site',
        name: 'Site',
        component: () => import('@/views/site/AntdSiteList.vue'),
        meta: { title: '场地管理' }
      },
      {
        path: 'monitor',
        name: 'Monitor',
        component: () => import('@/views/monitor/AntdMonitorCenter.vue'),
        meta: { title: '违规监测' }
      },
      {
        path: 'warning',
        name: 'Warning',
        component: () => import('@/views/warning/AntdWarningList.vue'),
        meta: { title: '预警管理' }
      },
      {
        path: 'record',
        name: 'Record',
        component: () => import('@/views/record/AntdIllegalRecord.vue'),
        meta: { title: '违规记录' }
      },
      {
        path: 'report',
        name: 'Report',
        component: () => import('@/views/report/AntdReportCenter.vue'),
        meta: { title: '统计报表' }
      },
      {
        path: 'settings',
        name: 'Settings',
        component: () => import('@/views/AntdSettings.vue'),
        meta: { title: '系统设置' }
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
  const token = localStorage.getItem('token')
  if (to.path !== '/login' && !token) {
    next('/login')
  } else {
    next()
  }
})

export default router
