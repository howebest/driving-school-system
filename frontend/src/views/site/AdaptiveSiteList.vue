<template>
  <div class="adaptive-site-container">
    <!-- 页面标题和操作区 -->
    <div class="page-header">
      <div class="header-left">
        <h2 class="page-title">场地管理</h2>
        <span class="page-subtitle">管理驾校各个训练场地信息</span>
      </div>
      <div class="header-right">
        <el-button 
          type="primary" 
          @click="handleAdd"
          class="main-action-btn"
        >
          <el-icon><Plus /></el-icon>
          新增场地
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
            placeholder="搜索场地名称"
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
            v-model="searchForm.type"
            placeholder="场地类型"
            clearable
            @change="handleSearch"
          >
            <el-option label="科目一考场" value="subject1"></el-option>
            <el-option label="科目二考场" value="subject2"></el-option>
            <el-option label="科目三考场" value="subject3"></el-option>
            <el-option label="理论教室" value="theory"></el-option>
          </el-select>
        </el-col>
        <el-col :span="5">
          <el-select
            v-model="searchForm.status"
            placeholder="使用状态"
            clearable
            @change="handleSearch"
          >
            <el-option label="正常使用" value="active"></el-option>
            <el-option label="维护中" value="maintenance"></el-option>
            <el-option label="暂停使用" value="inactive"></el-option>
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
              <el-icon><OfficeBuilding /></el-icon>
            </div>
            <div class="stat-content">
              <div class="stat-value">{{ stats.total }}</div>
              <div class="stat-label">场地总数</div>
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
              <div class="stat-label">正常使用</div>
            </div>
          </div>
        </el-col>
        <el-col :span="6">
          <div class="stat-card subject2">
            <div class="stat-icon">
              <el-icon><Location /></el-icon>
            </div>
            <div class="stat-content">
              <div class="stat-value">{{ stats.subject2 }}</div>
              <div class="stat-label">科目二场地</div>
            </div>
          </div>
        </el-col>
        <el-col :span="6">
          <div class="stat-card capacity">
            <div class="stat-icon">
              <el-icon><User /></el-icon>
            </div>
            <div class="stat-content">
              <div class="stat-value">{{ stats.totalCapacity }}</div>
              <div class="stat-label">总容量</div>
            </div>
          </div>
        </el-col>
      </el-row>
    </div>

    <!-- 场地列表表格 -->
    <div class="table-section">
      <div class="table-toolbar">
        <div class="toolbar-left">
          <span class="table-title">场地列表 [共 {{ pagination.total }} 条记录]</span>
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
          :data="siteList"
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
          <el-table-column prop="name" label="场地名称" min-width="140">
            <template #default="{ row }">
              <div class="site-name-cell">
                <el-icon class="site-icon"><Location /></el-icon>
                <span class="site-name">{{ row.name }}</span>
              </div>
            </template>
          </el-table-column>
          <el-table-column prop="type" label="类型" width="110" align="center" class-name="col-status">
            <template #default="{ row }">
              <el-tag 
                :type="getTypeTagType(row.type)"
                size="small"
                class="status-tag"
              >
                {{ getTypeText(row.type) }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="location" label="位置" min-width="160" />
          <el-table-column prop="area" label="面积(㎡)" width="100" align="center" />
          <el-table-column prop="capacity" label="容量" width="80" align="center" />
          <el-table-column prop="equipment" label="设备情况" width="120">
            <template #default="{ row }">
              <el-tag 
                :type="row.equipment === '完善' ? 'success' : 'warning'"
                size="small"
                class="status-tag"
              >
                {{ row.equipment }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="status" label="状态" width="100" align="center" class-name="col-status">
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
          <el-table-column prop="manager" label="负责人" width="100" />
          <el-table-column prop="contact" label="联系方式" width="120" />
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

    <!-- 场地详情对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="700px"
      class="site-dialog"
    >
      <el-form
        :model="currentSite"
        :rules="rules"
        ref="siteForm"
        label-width="100px"
      >
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="场地名称" prop="name">
              <el-input v-model="currentSite.name" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="场地类型" prop="type">
              <el-select v-model="currentSite.type" style="width: 100%">
                <el-option label="科目一考场" value="subject1"></el-option>
                <el-option label="科目二考场" value="subject2"></el-option>
                <el-option label="科目三考场" value="subject3"></el-option>
                <el-option label="理论教室" value="theory"></el-option>
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="位置" prop="location">
              <el-input v-model="currentSite.location" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="面积(㎡)" prop="area">
              <el-input-number
                v-model="currentSite.area"
                :min="0"
                controls-position="right"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="容量" prop="capacity">
              <el-input-number
                v-model="currentSite.capacity"
                :min="0"
                controls-position="right"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="状态" prop="status">
              <el-select v-model="currentSite.status" style="width: 100%">
                <el-option label="正常使用" value="active"></el-option>
                <el-option label="维护中" value="maintenance"></el-option>
                <el-option label="暂停使用" value="inactive"></el-option>
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="负责人" prop="manager">
              <el-input v-model="currentSite.manager" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="联系方式" prop="contact">
              <el-input v-model="currentSite.contact" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="设备情况">
          <el-select v-model="currentSite.equipment" style="width: 100%">
            <el-option label="完善" value="完善"></el-option>
            <el-option label="一般" value="一般"></el-option>
            <el-option label="需改进" value="需改进"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="备注">
          <el-input
            v-model="currentSite.remarks"
            type="textarea"
            :rows="3"
            placeholder="请输入场地备注信息"
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
  OfficeBuilding, 
  Check, 
  Location, 
  User,
  Download 
} from '@element-plus/icons-vue'

const loading = ref(false)
const dialogVisible = ref(false)
const dialogTitle = ref('')
const siteForm = ref(null)

// 搜索表单
const searchForm = reactive({
  name: '',
  type: '',
  status: ''
})

// 统计数据
const stats = reactive({
  total: 12,
  active: 10,
  subject2: 4,
  totalCapacity: 240
})

// 分页数据
const pagination = reactive({
  currentPage: 1,
  pageSize: 20,
  total: 12
})

// 场地列表数据
const siteList = ref([
  {
    id: 1,
    name: '科目二训练场A区',
    type: 'subject2',
    location: '广州市天河区XXX路123号',
    area: 2500,
    capacity: 30,
    equipment: '完善',
    status: 'active',
    manager: '张主任',
    contact: '13800138001',
    remarks: '主要用于倒车入库、侧方停车等科目二项目训练'
  },
  {
    id: 2,
    name: '科目一理论教室',
    type: 'theory',
    location: '广州市天河区XXX路125号',
    area: 120,
    capacity: 40,
    equipment: '完善',
    status: 'active',
    manager: '李老师',
    contact: '13800138002',
    remarks: '配备多媒体教学设备的理论学习教室'
  }
])

// 当前编辑的场地
const currentSite = reactive({
  id: null,
  name: '',
  type: '',
  location: '',
  area: 0,
  capacity: 0,
  equipment: '完善',
  status: 'active',
  manager: '',
  contact: '',
  remarks: ''
})

// 表单验证规则
const rules = {
  name: [
    { required: true, message: '请输入场地名称', trigger: 'blur' }
  ],
  type: [
    { required: true, message: '请选择场地类型', trigger: 'change' }
  ],
  location: [
    { required: true, message: '请输入场地位置', trigger: 'blur' }
  ],
  area: [
    { required: true, message: '请输入场地面积', trigger: 'blur' }
  ],
  manager: [
    { required: true, message: '请输入负责人', trigger: 'blur' }
  ]
}

// 获取类型标签类型
const getTypeTagType = (type) => {
  const typeMap = {
    subject1: 'primary',
    subject2: 'success',
    subject3: 'warning',
    theory: 'info'
  }
  return typeMap[type] || 'info'
}

// 获取类型文本
const getTypeText = (type) => {
  const textMap = {
    subject1: '科目一',
    subject2: '科目二',
    subject3: '科目三',
    theory: '理论教室'
  }
  return textMap[type] || type
}

// 获取状态标签类型
const getStatusTagType = (status) => {
  const typeMap = {
    active: 'success',
    maintenance: 'warning',
    inactive: 'danger'
  }
  return typeMap[status] || 'info'
}

// 获取状态文本
const getStatusText = (status) => {
  const textMap = {
    active: '正常使用',
    maintenance: '维护中',
    inactive: '暂停使用'
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
  dialogTitle.value = '新增场地'
  Object.keys(currentSite).forEach(key => {
    currentSite[key] = key === 'area' || key === 'capacity' ? 0 : ''
  })
  currentSite.status = 'active'
  currentSite.equipment = '完善'
  dialogVisible.value = true
}

// 处理查看
const handleView = (row) => {
  dialogTitle.value = '查看场地'
  Object.assign(currentSite, row)
  dialogVisible.value = true
}

// 处理编辑
const handleEdit = (row) => {
  dialogTitle.value = '编辑场地'
  Object.assign(currentSite, row)
  dialogVisible.value = true
}

// 处理删除
const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm(
      `确定要删除场地 "${row.name}" 吗？`,
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
    await siteForm.value.validate()
    
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
  console.log('选中的场地:', selection)
}

onMounted(() => {
  handleRefresh()
})
</script>

<style scoped>
@import '@/styles/table-adaptive-optimization.css';

.adaptive-site-container {
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
  background: linear-gradient(135deg, #d4fc79 0%, #96e6a1 100%);
  border-radius: 12px;
  color: #333;
}

.header-left h2 {
  margin: 0 0 8px 0;
  font-size: 24px;
  font-weight: 600;
  color: #333;
}

.page-subtitle {
  opacity: 0.8;
  font-size: 14px;
  color: #666;
}

.main-action-btn {
  background: rgba(51, 51, 51, 0.2);
  border-color: rgba(51, 51, 51, 0.3);
  color: #333;
}

.main-action-btn:hover {
  background: rgba(51, 51, 51, 0.3);
  border-color: rgba(51, 51, 51, 0.5);
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
  background: linear-gradient(135deg, #d4fc79 0%, #96e6a1 100%);
}

.stat-card.active .stat-icon {
  background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
}

.stat-card.subject2 .stat-icon {
  background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%);
}

.stat-card.capacity .stat-icon {
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

.site-name-cell {
  display: flex;
  align-items: center;
}

.site-icon {
  margin-right: 8px;
  color: #409eff;
}

.action-buttons {
  display: flex;
  gap: 8px;
}

.action-btn {
  padding: 4px 8px;
  font-size: 12px;
}

.site-dialog :deep(.el-dialog__body) {
  padding: 20px;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
}
</style>