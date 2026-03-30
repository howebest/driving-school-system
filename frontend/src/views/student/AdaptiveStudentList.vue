<template>
  <div class="adaptive-student-container">
    <!-- 页面标题和操作区 -->
    <div class="page-header">
      <div class="header-left">
        <h2 class="page-title">学员管理</h2>
        <span class="page-subtitle">管理驾校学员信息、状态和人脸绑定</span>
      </div>
      <div class="header-right">
        <el-button 
          type="primary" 
          @click="handleAdd"
          class="main-action-btn"
        >
          <el-icon><Plus /></el-icon>
          新增学员
        </el-button>
        <el-button @click="handleRefresh">
          <el-icon><Refresh /></el-icon>
          刷新
        </el-button>
      </div>
    </div>

    <!-- 搜索筛选区 -->
    <div class="search-section">
      <el-row :gutter="16">
        <el-col :span="5">
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
        </el-col>
        <el-col :span="5">
          <el-select
            v-model="searchForm.status"
            placeholder="学员状态"
            clearable
            @change="handleSearch"
          >
            <el-option label="在读" value="active"></el-option>
            <el-option label="已结业" value="graduated"></el-option>
            <el-option label="退学" value="dropout"></el-option>
          </el-select>
        </el-col>
        <el-col :span="5">
          <el-date-picker
            v-model="searchForm.dateRange"
            type="daterange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            value-format="YYYY-MM-DD"
            @change="handleSearch"
          />
        </el-col>
        <el-col :span="4">
          <el-button type="primary" @click="handleSearch" class="search-btn">
            <el-icon><Search /></el-icon>
            搜索
          </el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-col>
      </el-row>
    </div>

    <!-- 数据统计卡片 -->
    <div class="stats-cards">
      <el-row :gutter="16">
        <el-col :span="6">
          <div class="stat-card total">
            <div class="stat-icon">
              <el-icon><User /></el-icon>
            </div>
            <div class="stat-content">
              <div class="stat-value">{{ stats.total }}</div>
              <div class="stat-label">学员总数</div>
            </div>
          </div>
        </el-col>
        <el-col :span="6">
          <div class="stat-card active">
            <div class="stat-icon">
              <el-icon><Check /></el-icon>
            </div>
            <div class="stat-content">
              <div class="stat-value">{{ stats.active }}</div>
              <div class="stat-label">在读学员</div>
            </div>
          </div>
        </el-col>
        <el-col :span="6">
          <div class="stat-card face-bound">
            <div class="stat-icon">
              <el-icon><Camera /></el-icon>
            </div>
            <div class="stat-content">
              <div class="stat-value">{{ stats.faceBound }}</div>
              <div class="stat-label">人脸绑定</div>
            </div>
          </div>
        </el-col>
        <el-col :span="6">
          <div class="stat-card graduation-rate">
            <div class="stat-icon">
              <el-icon><Trophy /></el-icon>
            </div>
            <div class="stat-content">
              <div class="stat-value">{{ stats.graduationRate }}%</div>
              <div class="stat-label">结业率</div>
            </div>
          </div>
        </el-col>
      </el-row>
    </div>

    <!-- 学员列表表格 -->
    <div class="table-section">
      <div class="table-toolbar">
        <div class="toolbar-left">
          <span class="table-title">学员列表 [共 {{ pagination.total }} 条记录]</span>
        </div>
        <div class="toolbar-right">
          <el-button @click="handleRefresh" size="small">
            <el-icon><Refresh /></el-icon>
            刷新
          </el-button>
          <el-button @click="handleExport" size="small">
            <el-icon><Download /></el-icon>
            导出
          </el-button>
        </div>
      </div>
      
      <div class="table-container">
        <el-table
          :data="studentList"
          v-loading="loading"
          border
          stripe
          class="adaptive-table"
          @selection-change="handleSelectionChange"
          height="calc(100vh - 400px)"
          :fit="true"
          :show-overflow-tooltip="true"
        >
          <el-table-column type="selection" width="45" align="center" class-name="col-checkbox" />
          <el-table-column prop="id" label="ID" width="60" align="center" class-name="col-id" />
          <el-table-column prop="name" label="姓名" min-width="100" class-name="col-avatar">
            <template #default="{ row }">
              <div class="student-name-cell">
                <el-avatar :size="32" :src="row.avatar" class="student-avatar">
                  {{ row.name.charAt(0) }}
                </el-avatar>
                <span class="student-name">{{ row.name }}</span>
              </div>
            </template>
          </el-table-column>
          <el-table-column prop="idCard" label="身份证号" min-width="160" show-overflow-tooltip />
          <el-table-column prop="phone" label="手机号" width="120" />
          <el-table-column prop="enrollDate" label="入学日期" width="110" align="center" class-name="col-date" />
          <el-table-column prop="status" label="状态" width="90" align="center" class-name="col-status">
            <template #default="{ row }">
              <el-tag 
                :type="getStatusTagType(row.status)"
                size="small"
                class="status-tag"
              >
                {{ getStatusText(row.status) }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="faceStatus" label="人脸绑定" width="110" align="center">
            <template #default="{ row }">
              <el-tag 
                :type="row.faceStatus === '已绑定' ? 'success' : 'danger'"
                size="small"
                class="status-tag"
              >
                {{ row.faceStatus === '已绑定' ? '✓ 已绑定' : '× 未绑定' }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="150" align="center" fixed="right" class-name="col-actions">
            <template #default="{ row }">
              <div class="action-buttons">
                <el-button
                  type="primary"
                  link
                  size="small"
                  @click="handleEdit(row)"
                  class="action-btn"
                >
                  编辑
                </el-button>
                <el-button
                  type="success"
                  link
                  size="small"
                  @click="handleBindFace(row)"
                  class="action-btn"
                >
                  绑定
                </el-button>
                <el-button
                  type="danger"
                  link
                  size="small"
                  @click="handleDelete(row)"
                  class="action-btn"
                >
                  删除
                </el-button>
              </div>
            </template>
          </el-table-column>
        </el-table>
      </div>

      <!-- 分页器 -->
      <div class="adaptive-pagination">
        <el-pagination
          v-model:current-page="pagination.currentPage"
          v-model:page-size="pagination.pageSize"
          :page-sizes="[10, 20, 50, 100]"
          :total="pagination.total"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
          small
        />
      </div>
    </div>

    <!-- 学员详情对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="600px"
      class="student-dialog"
    >
      <el-form
        :model="currentStudent"
        :rules="rules"
        ref="studentForm"
        label-width="100px"
      >
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="姓名" prop="name">
              <el-input v-model="currentStudent.name" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="身份证号" prop="idCard">
              <el-input v-model="currentStudent.idCard" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="手机号" prop="phone">
              <el-input v-model="currentStudent.phone" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="状态" prop="status">
              <el-select v-model="currentStudent.status" style="width: 100%">
                <el-option label="在读" value="active"></el-option>
                <el-option label="已结业" value="graduated"></el-option>
                <el-option label="退学" value="dropout"></el-option>
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="备注">
          <el-input
            v-model="currentStudent.remarks"
            type="textarea"
            :rows="3"
            placeholder="请输入学员备注信息"
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
  Plus, 
  Refresh, 
  Search, 
  User, 
  Check, 
  Camera, 
  Trophy, 
  Download 
} from '@element-plus/icons-vue'

const loading = ref(false)
const dialogVisible = ref(false)
const dialogTitle = ref('')
const studentForm = ref(null)

// 搜索表单
const searchForm = reactive({
  keyword: '',
  status: '',
  dateRange: []
})

// 统计数据
const stats = reactive({
  total: 156,
  active: 124,
  faceBound: 98,
  graduationRate: 85
})

// 分页数据
const pagination = reactive({
  currentPage: 1,
  pageSize: 20,
  total: 156
})

// 学员列表数据
const studentList = ref([
  {
    id: 1,
    name: '张三',
    idCard: '110101199001011234',
    phone: '13800138000',
    enrollDate: '2024-01-15',
    status: 'active',
    faceStatus: '已绑定',
    avatar: '',
    remarks: 'C1驾照学员'
  },
  {
    id: 2,
    name: '李四',
    idCard: '110101199002021235',
    phone: '13800138001',
    enrollDate: '2023-12-20',
    status: 'graduated',
    faceStatus: '未绑定',
    avatar: '',
    remarks: '已通过考试'
  }
])

// 当前编辑的学员
const currentStudent = reactive({
  id: null,
  name: '',
  idCard: '',
  phone: '',
  status: 'active',
  remarks: ''
})

// 表单验证规则
const rules = {
  name: [
    { required: true, message: '请输入学员姓名', trigger: 'blur' }
  ],
  idCard: [
    { required: true, message: '请输入身份证号', trigger: 'blur' },
    { pattern: /(^\d{15}$)|(^\d{18}$)|(^\d{17}(\d|X|x)$)/, message: '请输入正确的身份证号', trigger: 'blur' }
  ],
  phone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号码', trigger: 'blur' }
  ]
}

// 获取状态标签类型
const getStatusTagType = (status) => {
  const typeMap = {
    active: 'primary',
    graduated: 'success',
    dropout: 'danger'
  }
  return typeMap[status] || 'info'
}

// 获取状态文本
const getStatusText = (status) => {
  const textMap = {
    active: '在读',
    graduated: '已结业',
    dropout: '退学'
  }
  return textMap[status] || status
}

// 处理搜索
const handleSearch = () => {
  loading.value = true
  setTimeout(() => {
    loading.value = false
    ElMessage.success('搜索完成')
  }, 500)
}

// 处理重置
const handleReset = () => {
  Object.keys(searchForm).forEach(key => {
    searchForm[key] = Array.isArray(searchForm[key]) ? [] : ''
  })
  handleSearch()
}

// 处理刷新
const handleRefresh = () => {
  loading.value = true
  setTimeout(() => {
    loading.value = false
    ElMessage.success('刷新成功')
  }, 800)
}

// 处理导出
const handleExport = () => {
  ElMessage.success('正在导出数据...')
}

// 处理新增
const handleAdd = () => {
  dialogTitle.value = '新增学员'
  Object.keys(currentStudent).forEach(key => {
    currentStudent[key] = ''
  })
  currentStudent.status = 'active'
  dialogVisible.value = true
}

// 处理编辑
const handleEdit = (row) => {
  dialogTitle.value = '编辑学员'
  Object.assign(currentStudent, row)
  dialogVisible.value = true
}

// 处理人脸绑定
const handleBindFace = (row) => {
  ElMessage.info(`正在为 ${row.name} 绑定人脸...`)
}

// 处理删除
const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm(
      `确定要删除学员 "${row.name}" 吗？`,
      '删除确认',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    // 模拟删除操作
    ElMessage.success('删除成功')
    handleRefresh()
  } catch {
    // 用户取消删除
  }
}

// 处理提交
const handleSubmit = async () => {
  try {
    await studentForm.value.validate()
    
    // 模拟保存操作
    ElMessage.success(`${dialogTitle.value}成功`)
    dialogVisible.value = false
    handleRefresh()
  } catch (error) {
    console.error('表单验证失败:', error)
  }
}

// 处理分页变化
const handleSizeChange = (val) => {
  pagination.pageSize = val
  handleRefresh()
}

const handleCurrentChange = (val) => {
  pagination.currentPage = val
  handleRefresh()
}

// 处理选择变化
const handleSelectionChange = (selection) => {
  console.log('选中的学员:', selection)
}

onMounted(() => {
  handleRefresh()
})
</script>

<style scoped>
@import '@/styles/table-adaptive-optimization.css';

.adaptive-student-container {
  padding: 20px;
  background-color: var(--el-bg-color-page);
  min-height: calc(100vh - 60px);
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  padding: 20px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 12px;
  color: white;
}

.header-left h2 {
  margin: 0 0 8px 0;
  font-size: 24px;
  font-weight: 600;
}

.page-subtitle {
  opacity: 0.9;
  font-size: 14px;
}

.main-action-btn {
  background: rgba(255, 255, 255, 0.2);
  border-color: rgba(255, 255, 255, 0.3);
  color: white;
}

.main-action-btn:hover {
  background: rgba(255, 255, 255, 0.3);
  border-color: rgba(255, 255, 255, 0.5);
}

.search-section {
  background: white;
  padding: 20px;
  border-radius: 8px;
  margin-bottom: 20px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.stats-cards {
  margin-bottom: 20px;
}

.stat-card {
  display: flex;
  align-items: center;
  padding: 20px;
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  transition: transform 0.3s ease;
}

.stat-card:hover {
  transform: translateY(-2px);
}

.stat-icon {
  width: 48px;
  height: 48px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 16px;
  font-size: 24px;
  color: white;
}

.stat-card.total .stat-icon {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.stat-card.active .stat-icon {
  background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
}

.stat-card.face-bound .stat-icon {
  background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%);
}

.stat-card.graduation-rate .stat-icon {
  background: linear-gradient(135deg, #fa709a 0%, #fee140 100%);
}

.stat-content {
  flex: 1;
}

.stat-value {
  font-size: 24px;
  font-weight: 600;
  margin-bottom: 4px;
  color: #333;
}

.stat-label {
  font-size: 14px;
  color: #666;
}

.table-section {
  background: white;
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.student-name-cell {
  display: flex;
  align-items: center;
}

.student-avatar {
  margin-right: 10px;
  background: #667eea;
  color: white;
}

.action-buttons {
  display: flex;
  gap: 8px;
}

.action-btn {
  padding: 4px 8px;
  font-size: 12px;
}

.student-dialog :deep(.el-dialog__body) {
  padding: 20px;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
}
</style>