<template>
  <div class="car-list">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>车辆管理</span>
          <el-button type="primary" @click="handleAdd">新增车辆</el-button>
        </div>
      </template>
      
      <el-table :data="carList" border stripe>
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="licensePlate" label="车牌号" width="120" />
        <el-table-column prop="carNumber" label="车辆编号" width="120" />
        <el-table-column prop="brand" label="品牌" width="100" />
        <el-table-column prop="model" label="型号" width="120" />
        <el-table-column prop="carStatus" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.carStatus === 1 ? 'success' : 'danger'">
              {{ row.carStatus === 1 ? '可用' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="150">
          <template #default="{ row }">
            <el-button link type="primary" size="small">编辑</el-button>
            <el-button link type="danger" size="small">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref } from 'vue'

const carList = ref([
  {
    id: 1,
    licensePlate: '京A12345',
    carNumber: 'CAR001',
    brand: '大众',
    model: '桑塔纳',
    carStatus: 1
  }
])

const handleAdd = () => {
  // 新增车辆逻辑
}
</script>

<style scoped>
.car-list {
  padding: 20px;
}
</style>