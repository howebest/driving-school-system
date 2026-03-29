<template>
  <div class="area-management">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>场地管理</span>
          <el-button type="primary" @click="handleAddArea">新增区域</el-button>
        </div>
      </template>
      
      <el-table :data="areaList" v-loading="loading">
        <el-table-column type="index" label="序号" width="60" />
        <el-table-column prop="name" label="区域名称" />
        <el-table-column prop="subject" label="所属科目" />
        <el-table-column prop="coaches" label="关联教练" />
        <el-table-column prop="vehicles" label="关联车辆" />
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

const areaList = ref([
  { id: 1, name: '科目二倒车入库', subject: '科目二', coaches: '李教练,王教练', vehicles: '粤A12345,粤B67890' },
  { id: 2, name: '科目三路线1', subject: '科目三', coaches: '张教练', vehicles: '粤C11111' }
])

const loading = ref(false)

const handleAddArea = () => {
  ElMessage.info('新增区域功能')
}

const handleEdit = (row) => {
  ElMessage.info(`编辑区域: ${row.name}`)
}

const handleDelete = (row) => {
  ElMessage.info(`删除区域: ${row.name}`)
}
</script>

<style scoped>
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>