import { defineStore } from 'pinia'
import { ref, computed } from 'vue'

export const useAuthStore = defineStore('auth', () => {
  const token = ref(localStorage.getItem('token') || '')
  const userInfo = ref(JSON.parse(localStorage.getItem('userInfo') || '{}'))
  const role = ref(localStorage.getItem('role') || '')

  const isAuthenticated = computed(() => !!token.value)
  const userName = computed(() => userInfo.value.name || '')
  const roleName = computed(() => {
    const roleMap = {
      'admin': '管理员',
      'manager': '场控负责人',
      'operator': '普通操作员'
    }
    return roleMap[role.value] || '未知角色'
  })

  // 登录
  function login(loginData) {
    // 模拟登录验证
    token.value = 'fake-token-' + Date.now()
    userInfo.value = {
      id: 1,
      name: loginData.username,
      avatar: ''
    }
    role.value = loginData.role
    
    localStorage.setItem('token', token.value)
    localStorage.setItem('userInfo', JSON.stringify(userInfo.value))
    localStorage.setItem('role', role.value)
  }

  // 登出
  function logout() {
    token.value = ''
    userInfo.value = {}
    role.value = ''
    
    localStorage.removeItem('token')
    localStorage.removeItem('userInfo')
    localStorage.removeItem('role')
  }

  // 检查权限
  function hasPermission(permission) {
    const permissions = {
      'admin': ['all'],
      'manager': [
        'view_student', 'edit_student', 'view_coach', 'edit_coach',
        'view_vehicle', 'edit_vehicle', 'view_area', 'edit_area',
        'view_monitoring', 'view_warning', 'handle_warning',
        'view_records', 'export_records', 'view_statistics'
      ],
      'operator': [
        'view_student', 'view_coach', 'view_vehicle', 'view_area',
        'face_verification', 'plate_verification',
        'view_monitoring', 'view_warning'
      ]
    }
    
    const userPermissions = permissions[role.value] || []
    return userPermissions.includes('all') || userPermissions.includes(permission)
  }

  return {
    token,
    userInfo,
    role,
    isAuthenticated,
    userName,
    roleName,
    login,
    logout,
    hasPermission
  }
})