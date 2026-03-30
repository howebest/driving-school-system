<template>
  <div class="vehicle-management">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>车辆管理</span>
          <el-button type="primary" @click="handleAddVehicle">新增车辆</el-button>
        </div>
      </template>
      
      <el-table :data="vehicleList" v-loading="loading">
        <el-table-column type="index" label="序号" width="60" />
        <el-table-column prop="plate" label="车牌号" />
        <el-table-column prop="vehicleNo" label="车辆编号" />
        <el-table-column prop="coachName" label="所属教练" />
        <el-table-column prop="model" label="车辆型号" />
        <el-table-column prop="status" label="状态">
          <template #default="{ row }">
            <el-tag :type="row.status === 'available' ? 'success' : 'danger'">
              {{ row.status === 'available' ? '可用' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleEdit(row)">编辑</el-button>
            <el-button type="danger" link @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { ElMessage } from 'element-plus'

const vehicleList = ref([
  { id: 1, plate: '粤A12345', vehicleNo: 'V2024001', coachName: '李教练', model: '桑塔纳', status: 'available' },
  { id: 2, plate: '粤B67890', vehicleNo: 'V2024002', coachName: '王教练', model: '捷达', status: 'available' }
])

const loading = ref(false)

const handleAddVehicle = () => {
  ElMessage.info('新增车辆功能')
}

const handleEdit = (row) => {
  ElMessage.info(`编辑车辆: ${row.plate}`)
}

const handleDelete = (row) => {
  ElMessage.info(`删除车辆: ${row.plate}`)
}
</script>

<style scoped>
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>