<template>
  <div class="coach-list">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>教练管理</span>
          <el-button type="primary" @click="handleAdd">新增教练</el-button>
        </div>
      </template>
      
      <el-table :data="coachList" border stripe>
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="coachName" label="姓名" width="120" />
        <el-table-column prop="coachLicense" label="教练证号" width="150" />
        <el-table-column prop="idCard" label="身份证号" width="180" />
        <el-table-column prop="phone" label="手机号" width="120" />
        <el-table-column prop="coachStatus" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.coachStatus === 1 ? 'success' : 'danger'">
              {{ row.coachStatus === 1 ? '在职' : '离职' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200">
          <template #default="{ row }">
            <el-button link type="primary" size="small">编辑</el-button>
            <el-button link type="primary" size="small">排班</el-button>
            <el-button link type="danger" size="small">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref } from 'vue'

const coachList = ref([
  {
    id: 1,
    coachName: '王教练',
    coachLicense: 'J2024001',
    idCard: '110101198001011234',
    phone: '13800138002',
    coachStatus: 1
  }
])

const handleAdd = () => {
  // 新增教练逻辑
}
</script>

<style scoped>
.coach-list {
  padding: 20px;
}
</style>