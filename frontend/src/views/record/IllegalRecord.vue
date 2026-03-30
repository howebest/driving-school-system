<template>
  <div class="illegal-record">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>违规记录</span>
          <el-button type="primary" @click="handleExport">导出记录</el-button>
        </div>
      </template>
      
      <el-table :data="records" border stripe>
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="time" label="违规时间" width="180" />
        <el-table-column prop="type" label="违规类型" width="120" />
        <el-table-column prop="person" label="涉及人员" width="120" />
        <el-table-column prop="location" label="地点" width="150" />
        <el-table-column prop="status" label="处理状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getRecordStatusType(row.status)">
              {{ row.status }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="证据" width="100">
          <template #default="{ row }">
            <el-button link type="primary" size="small">查看</el-button>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="150">
          <template #default="{ row }">
            <el-button link type="primary" size="small">详情</el-button>
            <el-button link type="success" size="small">处理</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref } from 'vue'

const records = ref([
  {
    id: 1,
    time: '2024-01-15 14:30:22',
    type: '无预约练车',
    person: '张三',
    location: '科目二训练场A区',
    status: '未处理'
  }
])

const getRecordStatusType = (status) => {
  const typeMap = {
    '未处理': 'danger',
    '已处理': 'success',
    '已追责': 'warning'
  }
  return typeMap[status] || 'info'
}

const handleExport = () => {
  // 导出记录逻辑
}
</script>

<style scoped>
.illegal-record {
  padding: 20px;
}
</style>