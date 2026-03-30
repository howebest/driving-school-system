<template>
  <div class="site-area">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>场地管理</span>
          <el-button type="primary" @click="handleAdd">新增区域</el-button>
        </div>
      </template>
      
      <el-table :data="siteAreas" border stripe>
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="areaName" label="区域名称" width="150" />
        <el-table-column prop="areaType" label="区域类型" width="100">
          <template #default="{ row }">
            <el-tag>{{ row.areaType === 1 ? '科目二' : '科目三' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="projectType" label="项目类型" width="120" />
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

const siteAreas = ref([
  {
    id: 1,
    areaName: '科目二A区',
    areaType: 1,
    projectType: '倒车入库'
  }
])

const handleAdd = () => {
  // 新增区域逻辑
}
</script>

<style scoped>
.site-area {
  padding: 20px;
}
</style>