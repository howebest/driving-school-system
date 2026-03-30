<template>
  <div class="optimized-student-management">
    <!-- 页面头部 -->
    <div class="page-header">
      <div class="header-main">
        <div class="header-title">
          <h1>学员管理</h1>
          <p>管理驾校学员信息、状态和人脸绑定</p>
        </div>
        <div class="header-actions">
          <el-button type="primary" size="large" @click="handleAdd">
            <el-icon><Plus /></el-icon>新增学员
          </el-button>
        </div>
      </div>
    </div>

    <!-- 搜索筛选区域 -->
    <div class="search-section">
      <el-card class="search-card" shadow="never">
        <el-form :model="searchForm" label-position="left" label-width="60px">
          <el-row :gutter="16">
            <el-col :span="5">
              <el-form-item label="关键词">
                <el-input
                  v-model="searchForm.keyword"
                  placeholder="姓名/身份证号/手机号"
                  clearable
                  @keyup.enter="handleSearch"
                >
                  <template #prefix>
                    <el-icon><Search /></el-icon>
                  </template>
                </el-input>
              </el-form-item>
            </el-col>
            <el-col :span="4">
              <el-form-item label="状态">
                <el-select v-model="searchForm.status" placeholder="请选择" clearable>
                  <el-option label="在读" :value="1" />
                  <el-option label="已结业" :value="2" />
                  <el-option label="无效" :value="3" />
                </el-select>
              </el-form-item>
            </el-col>
            <el-col :span="6">
              <el-form-item label="入学时间">
                <el-date-picker
                  v-model="searchForm.dateRange"
                  type="daterange"
                  range-separator="至"
                  start-placeholder="开始日期"
                  end-placeholder="结束日期"
                  format="YYYY-MM-DD"
                  value-format="YYYY-MM-DD"
                  style="width: 100%"
                />
              </el-form-item>
            </el-col>
            <el-col :span="5">
              <el-form-item label="">
                <el-button-group>
                  <el-button type="primary" @click="handleSearch">
                    <el-icon><Search /></el-icon>搜索
                  </el-button>
                  <el-button @click="handleReset">
                    <el-icon><Refresh /></el-icon>重置
                  </el-button>
                </el-button-group>
              </el-form-item>
            </el-col>
          </el-row>
        </el-form>
      </el-card>
    </div>

    <!-- 数据表格区域 -->
    <div class="table-section">
      <el-card class="table-card" shadow="never">
        <template #header>
          <div class="table-header">
            <div class="table-title">
              <h3>学员列表</h3>
              <el-tag type="info" effect="plain">共 {{ total }} 条记录</el-tag>
            </div>
            <div class="table-controls">
              <el-button-group>
                <el-button @click="handleRefresh" size="small">
                  <el-icon><Refresh /></el-icon>刷新
                </el-button>
                <el-button @click="handleExport" size="small">
                  <el-icon><Download /></el-icon>导出
                </el-button>
              </el-button-group>
            </div>
          </div>
        </template>

        <div class="table-container">
          <el-table
            :data="studentList"
            border
            stripe
            style="width: 100%"
            v-loading="loading"
            :header-cell-style="{ background: '#f8fafc', fontWeight: '600' }"
            :cell-style="{ padding: '8px 0' }"
            size="small"
          >
            <el-table-column type="selection" width="45" align="center" />
            <el-table-column prop="id" label="ID" width="60" sortable align="center" />
            <el-table-column prop="studentName" label="姓名" width="90" align="center">
              <template #default="{ row }">
                <div class="name-cell">
                  <el-avatar :size="28" :src="row.avatar" class="avatar">
                    <span>{{ row.studentName.charAt(0) }}</span>
                  </el-avatar>
                  <span class="name-text">{{ row.studentName }}</span>
                </div>
              </template>
            </el-table-column>
            <el-table-column prop="idCard" label="身份证号" width="160" show-overflow-tooltip />
            <el-table-column prop="phone" label="手机号" width="110" align="center" />
            <el-table-column prop="enrollmentDate" label="入学日期" width="100" align="center">
              <template #default="{ row }">
                {{ formatDate(row.enrollmentDate) }}
              </template>
            </el-table-column>
            <el-table-column prop="studentStatus" label="状态" width="80" align="center">
              <template #default="{ row }">
                <el-tag :type="getStatusType(row.studentStatus)" size="small" round>
                  {{ getStatusText(row.studentStatus) }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column label="人脸绑定" width="90" align="center">
              <template #default="{ row }">
                <div class="face-status">
                  <el-icon :class="row.faceImageUrl ? 'success' : 'warning'" size="14">
                    <Check v-if="row.faceImageUrl" />
                    <Close v-else />
                  </el-icon>
                  <span :class="row.faceImageUrl ? 'success' : 'warning'" class="status-text">
                    {{ row.faceImageUrl ? '已绑定' : '未绑定' }}
                  </span>
                </div>
              </template>
            </el-table-column>
            <el-table-column label="操作" width="180" fixed="right" align="center">
              <template #default="{ row }">
                <div class="action-buttons">
                  <el-button size="small" type="primary" link @click="handleEdit(row)">
                    <el-icon><Edit /></el-icon>编辑
                  </el-button>
                  <el-button 
                    size="small" 
                    type="success" 
                    link 
                    @click="handleBindFace(row)"
                    :disabled="!!row.faceImageUrl"
                  >
                    <el-icon><Camera /></el-icon>绑定
                  </el-button>
                  <el-button size="small" type="danger" link @click="handleDelete(row)">
                    <el-icon><Delete /></el-icon>删除
                  </el-button>
                </div>
              </template>
            </el-table-column>
          </el-table>
        </div>

        <!-- 分页 -->
        <div class="pagination-container">
          <el-pagination
            v-model:current-page="currentPage"
            v-model:page-size="pageSize"
            :page-sizes="[10, 20, 50, 100]"
            :total="total"
            layout="total, sizes, prev, pager, next, jumper"
            background
            @size-change="handleSizeChange"
            @current-change="handleCurrentChange"
          />
        </div>
      </el-card>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { 
  Plus, Search, Refresh, Download, 
  Edit, Delete, Camera, Check, Close 
} from '@element-plus/icons-vue'

const studentList = ref([])
const loading = ref(false)
const total = ref(0)
const currentPage = ref(1)
const pageSize = ref(10)

const searchForm = reactive({
  keyword: '',
  status: null,
  dateRange: []
})

const getStatusType = (status) => {
  const typeMap = { 1: 'primary', 2: 'success', 3: 'danger' }
  return typeMap[status] || 'info'
}

const getStatusText = (status) => {
  const textMap = { 1: '在读', 2: '已结业', 3: '无效' }
  return textMap[status] || '未知'
}

const formatDate = (date) => {
  if (!date) return ''
  return new Date(date).toLocaleDateString()
}

const loadData = () => {
  loading.value = true
  // 模拟API调用
  setTimeout(() => {
    studentList.value = [
      {
        id: 1,
        studentName: '张三',
        idCard: '110101199001011234',
        phone: '13800138000',
        studentStatus: 1,
        faceImageUrl: '',
        enrollmentDate: '2024-01-01',
        avatar: ''
      },
      {
        id: 2,
        studentName: '李四',
        idCard: '110101199002021235',
        phone: '13800138001',
        studentStatus: 2,
        faceImageUrl: 'face.jpg',
        enrollmentDate: '2023-12-01',
        graduationDate: '2024-01-10',
        avatar: ''
      }
    ]
    total.value = studentList.value.length
    loading.value = false
  }, 500)
}

const handleSearch = () => {
  currentPage.value = 1
  loadData()
}

const handleReset = () => {
  searchForm.keyword = ''
  searchForm.status = null
  searchForm.dateRange = []
  loadData()
}

const handleAdd = () => {
  ElMessage.info('新增学员功能开发中...')
}

const handleEdit = (row) => {
  ElMessage.info('编辑学员功能开发中...')
}

const handleDelete = (row) => {
  ElMessageBox.confirm(`确定要删除学员 "${row.studentName}" 吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    ElMessage.success('删除成功')
    loadData()
  })
}

const handleBindFace = (row) => {
  ElMessage.info('人脸绑定功能开发中...')
}

const handleSizeChange = (val) => {
  pageSize.value = val
  loadData()
}

const handleCurrentChange = (val) => {
  currentPage.value = val
  loadData()
}

const handleRefresh = () => {
  loadData()
}

const handleExport = () => {
  ElMessage.success('导出功能开发中...')
}

onMounted(() => {
  loadData()
})
</script>

<style scoped>
.optimized-student-management {
  height: 100%;
  display: flex;
  flex-direction: column;
  background: #f8fafc;
  padding: 20px;
}

/* 页面头部 */
.page-header {
  background: white;
  border-radius: 12px;
  padding: 20px;
  margin-bottom: 16px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);
}

.header-main {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.header-title h1 {
  margin: 0 0 6px 0;
  font-size: 22px;
  font-weight: 700;
  color: #1e293b;
}

.header-title p {
  margin: 0;
  color: #64748b;
  font-size: 14px;
}

.header-actions :deep(.el-button) {
  border-radius: 8px;
  height: 40px;
  padding: 0 20px;
  font-weight: 500;
}

/* 搜索区域 */
.search-section {
  margin-bottom: 16px;
}

.search-card {
  border-radius: 12px;
  border: 1px solid #e2e8f0;
}

.search-card :deep(.el-card__body) {
  padding: 16px;
}

/* 表格区域 */
.table-section {
  flex: 1;
  overflow: hidden;
}

.table-card {
  height: 100%;
  border-radius: 12px;
  border: 1px solid #e2e8f0;
  display: flex;
  flex-direction: column;
}

.table-card :deep(.el-card__header) {
  padding: 16px 20px;
  border-bottom: 1px solid #e2e8f0;
}

.table-card :deep(.el-card__body) {
  flex: 1;
  padding: 0;
  overflow: hidden;
  display: flex;
  flex-direction: column;
}

.table-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.table-title {
  display: flex;
  align-items: center;
  gap: 12px;
}

.table-title h3 {
  margin: 0;
  font-size: 16px;
  font-weight: 600;
  color: #1e293b;
}

.table-controls :deep(.el-button-group .el-button) {
  border-radius: 6px;
  padding: 8px 12px;
}

/* 表格容器 */
.table-container {
  flex: 1;
  overflow: auto;
}

.table-container :deep(.el-table) {
  height: 100%;
}

.table-container :deep(.el-table__row:hover) {
  background-color: #f8fafc !important;
}

/* 表格单元格优化 */
.name-cell {
  display: flex;
  align-items: center;
  gap: 8px;
}

.avatar {
  background: linear-gradient(135deg, #3b82f6, #60a5fa);
  color: white;
  font-weight: 500;
  font-size: 12px;
}

.name-text {
  font-weight: 500;
  color: #1e293b;
  font-size: 13px;
}

.face-status {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 4px;
}

.face-status .success {
  color: #10b981;
}

.face-status .warning {
  color: #f59e0b;
}

.status-text {
  font-size: 12px;
  font-weight: 500;
}

.action-buttons {
  display: flex;
  gap: 6px;
  justify-content: center;
}

.action-buttons :deep(.el-button) {
  padding: 4px 8px;
  font-size: 12px;
  border-radius: 4px;
}

/* 分页 */
.pagination-container {
  padding: 16px 20px;
  border-top: 1px solid #e2e8f0;
  background: #f8fafc;
}

.pagination-container :deep(.el-pagination) {
  justify-content: flex-end;
}

/* 响应式优化 */
@media (max-width: 1200px) {
  .optimized-student-management {
    padding: 16px;
  }
  
  .table-container :deep(.el-table) {
    font-size: 13px;
  }
  
  .action-buttons {
    flex-direction: column;
    gap: 4px;
  }
  
  .action-buttons :deep(.el-button) {
    width: 100%;
    justify-content: center;
  }
}
</style>