<template>
  <div class="warning-list">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>预警管理</span>
          <el-button type="success" @click="handleRefresh">刷新预警</el-button>
        </div>
      </template>
      
      <el-table :data="warnings" border stripe>
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="time" label="预警时间" width="180" />
        <el-table-column prop="type" label="预警类型" width="120" />
        <el-table-column prop="location" label="地点" width="150" />
        <el-table-column prop="person" label="涉及人员" width="120" />
        <el-table-column prop="status" label="处理状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getWarningStatusType(row.status)">
              {{ row.status }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="150">
          <template #default="{ row }">
            <el-button link type="primary" size="small">查看详情</el-button>
            <el-button link type="success" size="small">处理</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref } from 'vue'

const warnings = ref([
  {
    id: 1,
    time: '2024-01-15 14:30:22',
    type: '无预约练车',
    location: '科目二训练场A区',
    person: '张三',
    status: '未处理'
  }
])

const getWarningStatusType = (status) => {
  const typeMap = {
    '未处理': 'danger',
    '已处理': 'success',
    '已追责': 'warning'
  }
  return typeMap[status] || 'info'
}

const handleRefresh = () => {
  // 刷新预警逻辑
}
</script>

<style scoped>
.warning-list {
  padding: 20px;
}
</style>