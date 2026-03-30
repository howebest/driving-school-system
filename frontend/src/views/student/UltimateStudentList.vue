<template>
  <div class="ultimate-student-management">
    <!-- 页面头部 -->
    <div class="page-header">
      <div class="header-main">
        <div class="header-title">
          <h1>学员管理</h1>
          <p>管理驾校学员信息、状态和人脸绑定</p>
        </div>
        <div class="header-actions">
          <el-button type="primary" size="default" @click="handleAdd">
            <el-icon><Plus /></el-icon>新增学员
          </el-button>
        </div>
      </div>
    </div>

    <!-- 搜索筛选区域 -->
    <div class="search-section">
      <el-card class="search-card" shadow="never">
        <el-form :model="searchForm" label-position="left" label-width="50px" size="small">
          <el-row :gutter="12">
            <el-col :span="5">
              <el-form-item label="关键词">
                <el-input
                  v-model="searchForm.keyword"
                  placeholder="姓名/身份证号/手机号"
                  clearable
                  size="small"
                  @keyup.enter="handleSearch"
                >
                  <template #prefix>
                    <el-icon size="14"><Search /></el-icon>
                  </template>
                </el-input>
              </el-form-item>
            </el-col>
            <el-col :span="4">
              <el-form-item label="状态">
                <el-select v-model="searchForm.status" placeholder="请选择" clearable size="small">
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
                  size="small"
                  style="width: 100%"
                />
              </el-form-item>
            </el-col>
            <el-col :span="5">
              <el-form-item label-width="0">
                <el-button-group size="small">
                  <el-button type="primary" @click="handleSearch">
                    <el-icon size="14"><Search /></el-icon>搜索
                  </el-button>
                  <el-button @click="handleReset">
                    <el-icon size="14"><Refresh /></el-icon>重置
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
          <div class="table-header toolbar-compact">
            <div class="toolbar-title">
              学员列表 <el-tag type="info" effect="plain" size="small">共 {{ total }} 条记录</el-tag>
            </div>
            <div class="toolbar-actions">
              <el-button-group size="small">
                <el-button @click="handleRefresh">
                  <el-icon size="14"><Refresh /></el-icon>刷新
                </el-button>
                <el-button @click="handleExport">
                  <el-icon size="14"><Download /></el-icon>导出
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
            class="adaptive-table"
            v-loading="loading"
            height="calc(100vh - 350px)"
            :fit="true"
            :show-overflow-tooltip="true"
            :header-cell-style="{ background: '#f8fafc', fontWeight: '600', fontSize: '13px' }"
            :cell-style="{ padding: '8px 12px', fontSize: '13px' }"
          >
            <el-table-column type="selection" width="45" align="center" class-name="col-checkbox" />
            <el-table-column prop="id" label="ID" width="60" sortable align="center" class-name="col-id" />
            <el-table-column prop="studentName" label="姓名" min-width="100" align="center" class-name="col-avatar">
              <template #default="{ row }">
                <div class="table-avatar">
                  <el-avatar :size="28" :src="row.avatar" class="avatar-compact">
                    <span>{{ row.studentName.charAt(0) }}</span>
                  </el-avatar>
                  <span class="name-text">{{ row.studentName }}</span>
                </div>
              </template>
            </el-table-column>
            <el-table-column prop="idCard" label="身份证号" min-width="160" show-overflow-tooltip />
            <el-table-column prop="phone" label="手机号" width="120" align="center" />
            <el-table-column prop="enrollmentDate" label="入学日期" width="110" align="center" class-name="col-date" />
            <el-table-column prop="studentStatus" label="状态" width="90" align="center" class-name="col-status">
              <template #default="{ row }">
                <el-tag :type="getStatusType(row.studentStatus)" size="small" class="status-tag">
                  {{ getStatusText(row.studentStatus) }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column label="人脸绑定" width="110" align="center">
              <template #default="{ row }">
                <div class="face-status">
                  <el-icon :class="row.faceImageUrl ? 'success' : 'warning'" size="14">
                    <Check v-if="row.faceImageUrl" />
                    <Close v-else />
                  </el-icon>
                  <span :class="row.faceImageUrl ? 'success' : 'warning'" class="status-text">
                    {{ row.faceImageUrl ? '✓ 已绑定' : '× 未绑定' }}
                  </span>
                </div>
              </template>
            </el-table-column>
            <el-table-column label="操作" width="150" fixed="right" align="center" class-name="col-actions">
              <template #default="{ row }">
                <div class="action-buttons">
                  <el-button size="small" type="primary" link @click="handleEdit(row)" class="action-btn">
                    编辑
                  </el-button>
                  <el-button 
                    size="small" 
                    type="success" 
                    link 
                    @click="handleBindFace(row)"
                    :disabled="!!row.faceImageUrl"
                    class="action-btn"
                  >
                    绑定
                  </el-button>
                  <el-button size="small" type="danger" link @click="handleDelete(row)" class="action-btn">
                    删除
                  </el-button>
                </div>
              </template>
            </el-table-column>
          </el-table>
        </div>

        <!-- 分页 -->
        <div class="pagination-compact">
          <el-pagination
            v-model:current-page="currentPage"
            v-model:page-size="pageSize"
            :page-sizes="[10, 20, 50, 100]"
            :total="total"
            layout="total, sizes, prev, pager, next, jumper"
            background
            size="small"
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
.ultimate-student-management {
  height: 100%;
  display: flex;
  flex-direction: column;
  background: #f8fafc;
  padding: 16px;
}

/* 页面头部 */
.page-header {
  background: white;
  border-radius: 10px;
  padding: 16px;
  margin-bottom: 12px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);
}

.header-main {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.header-title h1 {
  margin: 0 0 4px 0;
  font-size: 20px;
  font-weight: 700;
  color: #1e293b;
}

.header-title p {
  margin: 0;
  color: #64748b;
  font-size: 13px;
}

.header-actions :deep(.el-button) {
  border-radius: 6px;
  height: 36px;
  padding: 0 16px;
  font-weight: 500;
  font-size: 13px;
}

/* 搜索区域 */
.search-section {
  margin-bottom: 12px;
}

.search-card {
  border-radius: 10px;
  border: 1px solid #e2e8f0;
}

.search-card :deep(.el-card__body) {
  padding: 12px;
}

/* 表格区域 */
.table-section {
  flex: 1;
  overflow: hidden;
}

.table-card {
  height: 100%;
  border-radius: 10px;
  border: 1px solid #e2e8f0;
  display: flex;
  flex-direction: column;
}

.table-card :deep(.el-card__header) {
  padding: 0;
  border-bottom: none;
}

.table-card :deep(.el-card__body) {
  flex: 1;
  padding: 0;
  overflow: hidden;
  display: flex;
  flex-direction: column;
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

/* 表格元素优化 */
.table-avatar {
  display: flex;
  align-items: center;
  gap: 6px;
}

.avatar-compact {
  background: linear-gradient(135deg, #3b82f6, #60a5fa);
  color: white;
  font-weight: 500;
  font-size: 10px;
}

.name-text {
  font-weight: 500;
  color: #1e293b;
  font-size: 12px;
}

.face-status {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 3px;
}

.face-status .success {
  color: #10b981;
}

.face-status .warning {
  color: #f59e0b;
}

.status-text {
  font-size: 11px;
  font-weight: 500;
}
</style>