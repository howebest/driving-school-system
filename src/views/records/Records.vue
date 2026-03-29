<template>
  <div class="records-management">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>违规记录追溯</span>
          <el-button type="primary" @click="exportRecords">导出记录</el-button>
        </div>
      </template>
      
      <el-table :data="records" v-loading="loading">
        <el-table-column type="index" label="序号" width="60" />
        <el-table-column prop="time" label="时间" width="180" />
        <el-table-column prop="location" label="地点" />
        <el-table-column prop="personName" label="人员姓名" />
        <el-table-column prop="plate" label="车牌号" />
        <el-table-column prop="type" label="违规类型" />
        <el-table-column prop="status" label="处理状态">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">
              {{ getStatusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作">
          <template #default="{ row }">
            <el-button type="primary" link @click="viewEvidence(row)">查看证据</el-button>
            <el-button type="primary" link @click="handleRecord(row)">处理</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { ElMessage } from 'element-plus'

const records = ref([
  {
    id: 1,
    time: '2024-01-15 14:30:25',
    location: '科目二倒车入库',
    personName: '张三',
    plate: '粤A12345',
    type: '越界行驶',
    status: 'handled'
  }
])

const loading = ref(false)

const exportRecords = () => {
  ElMessage.success('记录导出成功')
}

const viewEvidence = (row) => {
  ElMessage.info(`查看证据: 记录${row.id}`)
}

const handleRecord = (row) => {
  ElMessage.info(`处理记录: ${row.type}`)
}

const getStatusType = (status) => {
  const map = { 'handled': 'success', 'unhandled': 'warning' }
  return map[status] || 'info'
}

const getStatusText = (status) => {
  const map = { 'handled': '已处理', 'unhandled': '未处理' }
  return map[status] || '未知'
}
</script>

<style scoped>
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>