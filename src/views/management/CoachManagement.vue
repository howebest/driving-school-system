<template>
  <div class="coach-management">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>教练管理</span>
          <el-button type="primary" @click="handleAddCoach">新增教练</el-button>
        </div>
      </template>
      
      <el-table :data="coachList" v-loading="loading">
        <el-table-column type="index" label="序号" width="60" />
        <el-table-column prop="name" label="姓名" />
        <el-table-column prop="coachCert" label="教练证号" />
        <el-table-column prop="phone" label="联系电话" />
        <el-table-column prop="status" label="状态">
          <template #default="{ row }">
            <el-tag :type="row.status === 'active' ? 'success' : 'danger'">
              {{ row.status === 'active' ? '在职' : '离职' }}
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

const coachList = ref([
  { id: 1, name: '李教练', coachCert: 'J2024001', phone: '13800138000', status: 'active' },
  { id: 2, name: '王教练', coachCert: 'J2024002', phone: '13800138001', status: 'active' }
])

const loading = ref(false)

const handleAddCoach = () => {
  ElMessage.info('新增教练功能')
}

const handleEdit = (row) => {
  ElMessage.info(`编辑教练: ${row.name}`)
}

const handleDelete = (row) => {
  ElMessage.info(`删除教练: ${row.name}`)
}
</script>

<style scoped>
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>