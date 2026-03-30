<template>
  <div class="adaptive-coach-container">
    <!-- 页面标题和操作区 -->
    <div class="page-header">
      <div class="header-left">
        <h2 class="page-title">教练管理</h2>
        <span class="page-subtitle">管理系统中的所有教练信息</span>
      </div>
      <div class="header-right">
        <el-button 
          type="primary" 
          @click="handleAdd"
          class="main-action-btn"
        >
          <el-icon><Plus /></el-icon>
          新增教练
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
            v-model="searchForm.name"
            placeholder="搜索教练姓名"
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
            placeholder="教练状态"
            clearable
            @change="handleSearch"
          >
            <el-option label="在职" value="active"></el-option>
            <el-option label="离职" value="inactive"></el-option>
            <el-option label="休假" value="leave"></el-option>
          </el-select>
        </el-col>
        <el-col :span="5">
          <el-select
            v-model="searchForm.level"
            placeholder="教练等级"
            clearable
            @change="handleSearch"
          >
            <el-option label="初级教练" value="junior"></el-option>
            <el-option label="中级教练" value="intermediate"></el-option>
            <el-option label="高级教练" value="senior"></el-option>
            <el-option label="特级教练" value="master"></el-option>
          </el-select>
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
              <div class="stat-label">教练总数</div>
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
              <div class="stat-label">在职教练</div>
            </div>
          </div>
        </el-col>
        <el-col :span="6">
          <div class="stat-card senior">
            <div class="stat-icon">
              <el-icon><Star /></el-icon>
            </div>
            <div class="stat-content">
              <div class="stat-value">{{ stats.senior }}</div>
              <div class="stat-label">高级教练</div>
            </div>
          </div>
        </el-col>
        <el-col :span="6">
          <div class="stat-card avg-score">
            <div class="stat-icon">
              <el-icon><TrendCharts /></el-icon>
            </div>
            <div class="stat-content">
              <div class="stat-value">{{ stats.avgScore }}</div>
              <div class="stat-label">平均评分</div>
            </div>
          </div>
        </el-col>
      </el-row>
    </div>

    <!-- 教练列表表格 -->
    <div class="table-section">
      <div class="table-toolbar">
        <div class="toolbar-left">
          <span class="table-title">教练列表 [共 {{ pagination.total }} 条记录]</span>
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
          :data="coachList"
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
              <div class="coach-name-cell">
                <el-avatar :size="32" :src="row.avatar" class="coach-avatar">
                  {{ row.name.charAt(0) }}
                </el-avatar>
                <span class="coach-name">{{ row.name }}</span>
              </div>
            </template>
          </el-table-column>
          <el-table-column prop="phone" label="联系电话" width="120" />
          <el-table-column prop="licenseNumber" label="教练证号" min-width="140" />
          <el-table-column prop="level" label="等级" width="90" align="center" class-name="col-status">
            <template #default="{ row }">
              <el-tag 
                :type="getLevelTagType(row.level)"
                size="small"
                class="status-tag"
              >
                {{ getLevelText(row.level) }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="teachingAge" label="教龄" width="70" align="center" />
          <el-table-column prop="studentCount" label="学员数" width="80" align="center" />
          <el-table-column prop="avgScore" label="评分" width="80" align="center">
            <template #default="{ row }">
              <div class="score-cell">
                <el-rate
                  v-model="row.avgScore"
                  disabled
                  size="small"
                  :max="5"
                />
                <span class="score-text">{{ row.avgScore }}</span>
              </div>
            </template>
          </el-table-column>
          <el-table-column prop="status" label="状态" width="80" align="center" class-name="col-status">
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
          <el-table-column prop="hireDate" label="入职时间" width="110" />
          <el-table-column label="操作" width="150" align="center" fixed="right" class-name="col-actions">
            <template #default="{ row }">
              <div class="action-buttons">
                <el-button
                  type="primary"
                  link
                  size="small"
                  @click="handleView(row)"
                  class="action-btn"
                >
                  查看
                </el-button>
                <el-button
                  type="warning"
                  link
                  size="small"
                  @click="handleEdit(row)"
                  class="action-btn"
                >
                  编辑
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

    <!-- 教练详情对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="600px"
      class="coach-dialog"
    >
      <el-form
        :model="currentCoach"
        :rules="rules"
        ref="coachForm"
        label-width="100px"
      >
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="姓名" prop="name">
              <el-input v-model="currentCoach.name" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="联系电话" prop="phone">
              <el-input v-model="currentCoach.phone" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="教练证号" prop="licenseNumber">
              <el-input v-model="currentCoach.licenseNumber" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="等级" prop="level">
              <el-select v-model="currentCoach.level" style="width: 100%">
                <el-option label="初级教练" value="junior"></el-option>
                <el-option label="中级教练" value="intermediate"></el-option>
                <el-option label="高级教练" value="senior"></el-option>
                <el-option label="特级教练" value="master"></el-option>
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="教龄" prop="teachingAge">
              <el-input-number
                v-model="currentCoach.teachingAge"
                :min="0"
                :max="50"
                controls-position="right"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="状态" prop="status">
              <el-select v-model="currentCoach.status" style="width: 100%">
                <el-option label="在职" value="active"></el-option>
                <el-option label="离职" value="inactive"></el-option>
                <el-option label="休假" value="leave"></el-option>
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="简介">
          <el-input
            v-model="currentCoach.introduction"
            type="textarea"
            :rows="3"
            placeholder="请输入教练简介"
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
  Star, 
  TrendCharts,
  Download 
} from '@element-plus/icons-vue'

const loading = ref(false)
const dialogVisible = ref(false)
const dialogTitle = ref('')
const coachForm = ref(null)

// 搜索表单
const searchForm = reactive({
  name: '',
  status: '',
  level: ''
})

// 统计数据
const stats = reactive({
  total: 24,
  active: 21,
  senior: 8,
  avgScore: 4.6
})

// 分页数据
const pagination = reactive({
  currentPage: 1,
  pageSize: 20,
  total: 24
})

// 教练列表数据
const coachList = ref([
  {
    id: 1,
    name: '张教练',
    phone: '13800138001',
    licenseNumber: 'JL20240001',
    level: 'senior',
    teachingAge: 8,
    studentCount: 32,
    avgScore: 4.8,
    status: 'active',
    hireDate: '2018-03-15',
    avatar: '',
    introduction: '资深驾驶教练，拥有8年教学经验'
  },
  {
    id: 2,
    name: '李教练',
    phone: '13800138002',
    licenseNumber: 'JL20240002',
    level: 'intermediate',
    teachingAge: 5,
    studentCount: 28,
    avgScore: 4.5,
    status: 'active',
    hireDate: '2019-07-22',
    avatar: '',
    introduction: '中级教练，擅长新手教学'
  }
])

// 当前编辑的教练
const currentCoach = reactive({
  id: null,
  name: '',
  phone: '',
  licenseNumber: '',
  level: '',
  teachingAge: 0,
  status: 'active',
  introduction: ''
})

// 表单验证规则
const rules = {
  name: [
    { required: true, message: '请输入教练姓名', trigger: 'blur' }
  ],
  phone: [
    { required: true, message: '请输入联系电话', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号码', trigger: 'blur' }
  ],
  licenseNumber: [
    { required: true, message: '请输入教练证号', trigger: 'blur' }
  ],
  level: [
    { required: true, message: '请选择教练等级', trigger: 'change' }
  ]
}

// 获取等级标签类型
const getLevelTagType = (level) => {
  const typeMap = {
    junior: 'info',
    intermediate: 'primary',
    senior: 'warning',
    master: 'danger'
  }
  return typeMap[level] || 'info'
}

// 获取等级文本
const getLevelText = (level) => {
  const textMap = {
    junior: '初级',
    intermediate: '中级',
    senior: '高级',
    master: '特级'
  }
  return textMap[level] || level
}

// 获取状态标签类型
const getStatusTagType = (status) => {
  const typeMap = {
    active: 'success',
    inactive: 'danger',
    leave: 'warning'
  }
  return typeMap[status] || 'info'
}

// 获取状态文本
const getStatusText = (status) => {
  const textMap = {
    active: '在职',
    inactive: '离职',
    leave: '休假'
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
    searchForm[key] = ''
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
  dialogTitle.value = '新增教练'
  Object.keys(currentCoach).forEach(key => {
    currentCoach[key] = ''
  })
  currentCoach.status = 'active'
  dialogVisible.value = true
}

// 处理查看
const handleView = (row) => {
  dialogTitle.value = '查看教练'
  Object.assign(currentCoach, row)
  dialogVisible.value = true
}

// 处理编辑
const handleEdit = (row) => {
  dialogTitle.value = '编辑教练'
  Object.assign(currentCoach, row)
  dialogVisible.value = true
}

// 处理删除
const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm(
      `确定要删除教练 "${row.name}" 吗？`,
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
    await coachForm.value.validate()
    
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
  console.log('选中的教练:', selection)
}

onMounted(() => {
  handleRefresh()
})
</script>

<style scoped>
@import '@/styles/table-adaptive-optimization.css';

.adaptive-coach-container {
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
  background: linear-gradient(135deg, #ff9a9e 0%, #fecfef 100%);
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
  background: linear-gradient(135deg, #ff9a9e 0%, #fecfef 100%);
}

.stat-card.active .stat-icon {
  background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
}

.stat-card.senior .stat-icon {
  background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%);
}

.stat-card.avg-score .stat-icon {
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

.coach-name-cell {
  display: flex;
  align-items: center;
}

.coach-avatar {
  margin-right: 10px;
  background: #ff9a9e;
  color: white;
}

.score-cell {
  display: flex;
  flex-direction: column;
  align-items: center;
}

.score-text {
  font-size: 12px;
  color: #666;
  margin-top: 4px;
}

.action-buttons {
  display: flex;
  gap: 8px;
}

.action-btn {
  padding: 4px 8px;
  font-size: 12px;
}

.coach-dialog :deep(.el-dialog__body) {
  padding: 20px;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
}
</style>