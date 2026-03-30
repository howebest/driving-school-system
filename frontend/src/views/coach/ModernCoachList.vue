<template>
  <div class="coach-list-modern">
    <!-- 页面头部 -->
    <div class="page-header">
      <div class="header-content">
        <div class="header-info">
          <h1>教练管理</h1>
          <p>管理驾校教练信息、排班和状态</p>
        </div>
        <div class="header-actions">
          <el-button type="primary" size="large" @click="handleAdd">
            <el-icon><Plus /></el-icon>新增教练
          </el-button>
        </div>
      </div>
    </div>

    <!-- 搜索筛选区域 -->
    <div class="search-section">
      <el-card class="search-card">
        <el-form :model="searchForm" label-position="left" label-width="80px">
          <el-row :gutter="20">
            <el-col :span="6">
              <el-form-item label="关键词">
                <el-input
                  v-model="searchForm.keyword"
                  placeholder="姓名/教练证号/手机号"
                  clearable
                  @keyup.enter="handleSearch"
                >
                  <template #prefix>
                    <el-icon><Search /></el-icon>
                  </template>
                </el-input>
              </el-form-item>
            </el-col>
            <el-col :span="6">
              <el-form-item label="状态">
                <el-select v-model="searchForm.status" placeholder="请选择状态" clearable>
                  <el-option label="在职" :value="1" />
                  <el-option label="离职" :value="2" />
                </el-select>
              </el-form-item>
            </el-col>
            <el-col :span="6">
              <el-form-item label="入职时间">
                <el-date-picker
                  v-model="searchForm.dateRange"
                  type="daterange"
                  range-separator="至"
                  start-placeholder="开始日期"
                  end-placeholder="结束日期"
                  format="YYYY-MM-DD"
                  value-format="YYYY-MM-DD"
                />
              </el-form-item>
            </el-col>
            <el-col :span="6">
              <el-form-item>
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

    <!-- 数据表格 -->
    <div class="table-section">
      <el-card class="table-card">
        <template #header>
          <div class="table-header">
            <div class="table-title">
              <h3>教练列表</h3>
              <span class="record-count">共 {{ total }} 条记录</span>
            </div>
            <div class="table-actions">
              <el-button-group>
                <el-button @click="handleRefresh">
                  <el-icon><Refresh /></el-icon>刷新
                </el-button>
                <el-button @click="handleExport">
                  <el-icon><Download /></el-icon>导出
                </el-button>
              </el-button-group>
            </div>
          </div>
        </template>

        <el-table
          :data="coachList"
          border
          stripe
          style="width: 100%"
          v-loading="loading"
          :scroll="{ x: 1200 }"
        >
          <el-table-column prop="id" label="ID" width="80" sortable fixed="left" />
          <el-table-column prop="coachName" label="姓名" width="120">
            <template #default="{ row }">
              <div class="name-cell">
                <el-avatar :size="32" :src="row.avatar" class="avatar">
                  <span>{{ row.coachName.charAt(0) }}</span>
                </el-avatar>
                <span class="name-text">{{ row.coachName }}</span>
              </div>
            </template>
          </el-table-column>
          <el-table-column prop="coachLicense" label="教练证号" width="150" />
          <el-table-column prop="idCard" label="身份证号" width="180" />
          <el-table-column prop="phone" label="手机号" width="120" />
          <el-table-column prop="hireDate" label="入职日期" width="120">
            <template #default="{ row }">
              {{ formatDate(row.hireDate) }}
            </template>
          </el-table-column>
          <el-table-column prop="coachStatus" label="状态" width="100">
            <template #default="{ row }">
              <el-tag :type="row.coachStatus === 1 ? 'success' : 'danger'" round>
                {{ row.coachStatus === 1 ? '在职' : '离职' }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column label="排班状态" width="120">
            <template #default="{ row }">
              <el-tag :type="getScheduleStatusType(row.scheduleStatus)" round>
                {{ getScheduleStatusText(row.scheduleStatus) }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column label="人脸绑定" width="120">
            <template #default="{ row }">
              <div class="face-status">
                <el-icon :class="row.faceImageUrl ? 'success' : 'warning'">
                  <Check v-if="row.faceImageUrl" />
                  <Close v-else />
                </el-icon>
                <span :class="row.faceImageUrl ? 'success' : 'warning'">
                  {{ row.faceImageUrl ? '已绑定' : '未绑定' }}
                </span>
              </div>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="250" fixed="right">
            <template #default="{ row }">
              <div class="action-buttons">
                <el-button size="small" type="primary" link @click="handleEdit(row)">
                  <el-icon><Edit /></el-icon>编辑
                </el-button>
                <el-button size="small" type="success" link @click="handleSchedule(row)">
                  <el-icon><Calendar /></el-icon>排班
                </el-button>
                <el-button size="small" type="warning" link @click="handleBindFace(row)" :disabled="!!row.faceImageUrl">
                  <el-icon><Camera /></el-icon>人脸绑定
                </el-button>
                <el-button size="small" type="danger" link @click="handleDelete(row)">
                  <el-icon><Delete /></el-icon>删除
                </el-button>
              </div>
            </template>
          </el-table-column>
        </el-table>

        <!-- 分页 -->
        <div class="pagination-container">
          <el-pagination
            v-model:current-page="currentPage"
            v-model:page-size="pageSize"
            :page-sizes="[10, 20, 50, 100]"
            :total="total"
            layout="total, sizes, prev, pager, next, jumper"
            @size-change="handleSizeChange"
            @current-change="handleCurrentChange"
          />
        </div>
      </el-card>
    </div>

    <!-- 新增/编辑对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="600px"
      class="coach-dialog"
      @close="handleDialogClose"
    >
      <el-form
        ref="formRef"
        :model="formData"
        :rules="formRules"
        label-width="100px"
      >
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="教练姓名" prop="coachName">
              <el-input v-model="formData.coachName" placeholder="请输入教练姓名" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="教练证号" prop="coachLicense">
              <el-input v-model="formData.coachLicense" placeholder="请输入教练证号" />
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="身份证号" prop="idCard">
              <el-input v-model="formData.idCard" placeholder="请输入身份证号" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="手机号" prop="phone">
              <el-input v-model="formData.phone" placeholder="请输入手机号" />
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="状态" prop="coachStatus">
              <el-select v-model="formData.coachStatus" placeholder="请选择状态" style="width: 100%">
                <el-option label="在职" :value="1" />
                <el-option label="离职" :value="2" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="入职日期" prop="hireDate">
              <el-date-picker
                v-model="formData.hireDate"
                type="date"
                placeholder="请选择入职日期"
                format="YYYY-MM-DD"
                value-format="YYYY-MM-DD"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-form-item label="备注">
          <el-input
            v-model="formData.remark"
            type="textarea"
            placeholder="请输入备注信息"
            :rows="3"
          />
        </el-form-item>
      </el-form>
      
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleSubmit">确定</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { 
  Plus, Search, Refresh, Download, 
  Edit, Delete, Calendar, Camera, Check, Close 
} from '@element-plus/icons-vue'

const coachList = ref([])
const loading = ref(false)
const total = ref(0)
const currentPage = ref(1)
const pageSize = ref(10)
const dialogVisible = ref(false)
const dialogTitle = ref('')
const formRef = ref()

const searchForm = reactive({
  keyword: '',
  status: null,
  dateRange: []
})

const formData = reactive({
  id: null,
  coachName: '',
  coachLicense: '',
  idCard: '',
  phone: '',
  coachStatus: 1,
  hireDate: '',
  remark: ''
})

const formRules = {
  coachName: [
    { required: true, message: '请输入教练姓名', trigger: 'blur' }
  ],
  coachLicense: [
    { required: true, message: '请输入教练证号', trigger: 'blur' }
  ],
  idCard: [
    { required: true, message: '请输入身份证号', trigger: 'blur' },
    { pattern: /(^\d{15}$)|(^\d{18}$)|(^\d{17}(\d|X|x)$)/, message: '身份证号格式不正确', trigger: 'blur' }
  ],
  phone: [
    { pattern: /^1[3-9]\d{9}$/, message: '手机号格式不正确', trigger: 'blur' }
  ]
}

const getScheduleStatusType = (status) => {
  const typeMap = { 1: 'success', 2: 'warning', 3: 'info' }
  return typeMap[status] || 'info'
}

const getScheduleStatusText = (status) => {
  const textMap = { 1: '当班', 2: '休息', 3: '请假' }
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
    coachList.value = [
      {
        id: 1,
        coachName: '王教练',
        coachLicense: 'J2024001',
        idCard: '110101198001011234',
        phone: '13800138002',
        coachStatus: 1,
        scheduleStatus: 1,
        faceImageUrl: '',
        hireDate: '2024-01-01',
        avatar: ''
      }
    ]
    total.value = coachList.value.length
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
  dialogTitle.value = '新增教练'
  Object.assign(formData, {
    id: null,
    coachName: '',
    coachLicense: '',
    idCard: '',
    phone: '',
    coachStatus: 1,
    hireDate: '',
    remark: ''
  })
  dialogVisible.value = true
}

const handleEdit = (row) => {
  dialogTitle.value = '编辑教练'
  Object.assign(formData, row)
  dialogVisible.value = true
}

const handleDelete = (row) => {
  ElMessageBox.confirm(`确定要删除教练 "${row.coachName}" 吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    ElMessage.success('删除成功')
    loadData()
  })
}

const handleSchedule = (row) => {
  ElMessage.info('排班功能开发中...')
}

const handleBindFace = (row) => {
  ElMessage.info('人脸绑定功能开发中...')
}

const handleSubmit = async () => {
  if (!formRef.value) return
  
  await formRef.value.validate((valid) => {
    if (valid) {
      ElMessage.success(formData.id ? '编辑成功' : '新增成功')
      dialogVisible.value = false
      loadData()
    }
  })
}

const handleDialogClose = () => {
  formRef.value?.resetFields()
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
.coach-list-modern {
  height: 100%;
  display: flex;
  flex-direction: column;
}

/* 页面头部 */
.page-header {
  background: white;
  border-radius: 16px;
  padding: 24px;
  margin-bottom: 20px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
}

.header-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.header-info h1 {
  margin: 0 0 8px 0;
  font-size: 24px;
  font-weight: 700;
  color: #1e293b;
}

.header-info p {
  margin: 0;
  color: #64748b;
  font-size: 15px;
}

.header-actions :deep(.el-button) {
  border-radius: 10px;
  height: 44px;
  padding: 0 24px;
  font-weight: 500;
}

/* 搜索区域 */
.search-section {
  margin-bottom: 20px;
}

.search-card {
  border-radius: 16px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
  border: 1px solid #e2e8f0;
}

.search-card :deep(.el-card__body) {
  padding: 24px;
}

/* 表格区域 */
.table-section {
  flex: 1;
  overflow: hidden;
}

.table-card {
  height: 100%;
  border-radius: 16px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
  border: 1px solid #e2e8f0;
  display: flex;
  flex-direction: column;
}

.table-card :deep(.el-card__header) {
  padding: 20px 24px;
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
  gap: 16px;
}

.table-title h3 {
  margin: 0;
  font-size: 18px;
  font-weight: 600;
  color: #1e293b;
}

.record-count {
  font-size: 14px;
  color: #64748b;
  background: #f1f5f9;
  padding: 4px 12px;
  border-radius: 20px;
}

.table-actions :deep(.el-button-group .el-button) {
  border-radius: 8px;
}

/* 表格样式优化 */
:deep(.el-table) {
  flex: 1;
}

:deep(.el-table__header th) {
  background-color: #f8fafc;
  font-weight: 600;
  color: #1e293b;
}

:deep(.el-table__row:hover) {
  background-color: #f8fafc !important;
}

.name-cell {
  display: flex;
  align-items: center;
  gap: 12px;
}

.avatar {
  background: linear-gradient(135deg, #3b82f6, #60a5fa);
  color: white;
  font-weight: 500;
}

.name-text {
  font-weight: 500;
  color: #1e293b;
}

.face-status {
  display: flex;
  align-items: center;
  gap: 6px;
}

.face-status .success {
  color: #10b981;
}

.face-status .warning {
  color: #f59e0b;
}

.action-buttons {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.action-buttons :deep(.el-button) {
  padding: 6px 12px;
  border-radius: 6px;
}

/* 分页 */
.pagination-container {
  padding: 20px 24px;
  border-top: 1px solid #e2e8f0;
  background: #f8fafc;
}

.pagination-container :deep(.el-pagination) {
  justify-content: flex-end;
}

/* 对话框样式 */
.coach-dialog :deep(.el-dialog) {
  border-radius: 16px;
}

.coach-dialog :deep(.el-dialog__header) {
  padding: 24px 24px 16px;
  border-bottom: 1px solid #e2e8f0;
}

.coach-dialog :deep(.el-dialog__body) {
  padding: 24px;
}

.coach-dialog :deep(.el-dialog__footer) {
  padding: 16px 24px 24px;
  border-top: 1px solid #e2e8f0;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
}

.dialog-footer :deep(.el-button) {
  border-radius: 8px;
  padding: 10px 24px;
}
</style>