import { defineStore } from 'pinia'
import { ref } from 'vue'

export const useWarningStore = defineStore('warning', () => {
  const warnings = ref([])
  const unreadCount = ref(0)

  // 添加预警
  function addWarning(warning) {
    warnings.value.unshift({
      id: Date.now(),
      ...warning,
      createTime: new Date().toISOString(),
      status: 'unread'
    })
    unreadCount.value++
  }

  // 标记为已读
  function markAsRead(id) {
    const warning = warnings.value.find(w => w.id === id)
    if (warning && warning.status === 'unread') {
      warning.status = 'read'
      unreadCount.value--
    }
  }

  // 处理预警
  function handleWarning(id, remark) {
    const warning = warnings.value.find(w => w.id === id)
    if (warning) {
      warning.status = 'handled'
      warning.handleTime = new Date().toISOString()
      warning.remark = remark
      if (warning.status === 'unread') {
        unreadCount.value--
      }
    }
  }

  // 获取未读数量
  function getUnreadCount() {
    return warnings.value.filter(w => w.status === 'unread').length
  }

  return {
    warnings,
    unreadCount,
    addWarning,
    markAsRead,
    handleWarning,
    getUnreadCount
  }
})