<template>
  <div class="adaptive-car-container">
    <!-- 页面标题和操作区 -->
    <div class="page-header">
      <div class="header-left">
        <h2 class="page-title">车辆管理</h2>
        <span class="page-subtitle">管理驾校所有训练车辆信息</span>
      </div>
      <div class="header-right">
        <el-button 
          type="primary" 
          @click="handleAdd"
          class="main-action-btn"
        >
          <el-icon><Plus /></el-icon>
          新增车辆
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
            v-model="searchForm.plateNumber"
            placeholder="搜索车牌号"
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
            placeholder="车辆类型"
            clearable
            @change="handleSearch"
          >
            <el-option label="手动挡" value="manual"></el-option>
            <el-option label="自动挡" value="automatic"></el-option>
            <el-option label="电动车" value="electric"></el-option>
          </el-select>
        </el-col>
        <el-col :span="5">
          <el-select
            v-model="searchForm.status"
            placeholder="车辆状态"
            clearable
            @change="handleSearch"
          >
            <el-option label="正常" value="normal"></el-option>
            <el-option label="维修中" value="maintenance"></el-option>
            <el-option label="已报废" value="scrap"></el-option>
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
              <el-icon><Van /></el-icon>
            </div>
            <div class="stat-content">
              <div class="stat-value">{{ stats.total }}</div>
              <div class="stat-label">车辆总数</div>
            </div>
          </div>
        </el-col>
        <el-col :span="6">
          <div class="stat-card normal">
            <div class="stat-icon">
              <el-icon><Check /></el-icon>
            </div>
            <div class="stat-content">
              <div class="stat-value">{{ stats.normal }}</div>
              <div class="stat-label">正常车辆</div>
            </div>
          </div>
        </el-col>
        <el-col :span="6">
          <div class="stat-card maintenance">
            <div class="stat-icon">
              <el-icon><Tools /></el-icon>
            </div>
            <div class="stat-content">
              <div class="stat-value">{{ stats.maintenance }}</div>
              <div class="stat-label">维修中</div>
            </div>
          </div>
        </el-col>
        <el-col :span="6">
          <div class="stat-card electric">
            <div class="stat-icon">
              <el-icon><Lightning /></el-icon>
            </div>
            <div class="stat-content">
              <div class="stat-value">{{ stats.electric }}</div>
              <div class="stat-label">电动车</div>
            </div>
          </div>
        </el-col>
      </el-row>
    </div>

    <!-- 车辆列表表格 -->
    <div class="table-section">
      <div class="table-toolbar">
        <div class="toolbar-left">
          <span class="table-title">车辆列表 [共 {{ pagination.total }} 条记录]</span>
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
          :data="carList"
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
          <el-table-column prop="plateNumber" label="车牌号" min-width="120">
            <template #default="{ row }">
              <div class="plate-cell">
                <el-tag 
                  :type="getPlateTagType(row.plateNumber)"
                  size="small"
                  class="status-tag"
                >
                  {{ row.plateNumber }}
                </el-tag>
              </div>
            </template>
          </el-table-column>
          <el-table-column prop="brand" label="品牌型号" min-width="140" />
          <el-table-column prop="type" label="类型" width="90" align="center" class-name="col-status">
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
          <el-table-column prop="vin" label="车架号" min-width="160" />
          <el-table-column prop="engineNumber" label="发动机号" min-width="140" />
          <el-table-column prop="purchaseDate" label="购买日期" width="110" />
          <el-table-column prop="mileage" label="里程(km)" width="100" align="center" />
          <el-table-column prop="lastMaintenance" label="上次保养" width="110" />
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

    <!-- 车辆详情对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="700px"
      class="car-dialog"
    >
      <el-form
        :model="currentCar"
        :rules="rules"
        ref="carForm"
        label-width="100px"
      >
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="车牌号" prop="plateNumber">
              <el-input v-model="currentCar.plateNumber" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="品牌型号" prop="brand">
              <el-input v-model="currentCar.brand" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="车辆类型" prop="type">
              <el-select v-model="currentCar.type" style="width: 100%">
                <el-option label="手动挡" value="manual"></el-option>
                <el-option label="自动挡" value="automatic"></el-option>
                <el-option label="电动车" value="electric"></el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="状态" prop="status">
              <el-select v-model="currentCar.status" style="width: 100%">
                <el-option label="正常" value="normal"></el-option>
                <el-option label="维修中" value="maintenance"></el-option>
                <el-option label="已报废" value="scrap"></el-option>
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="车架号" prop="vin">
              <el-input v-model="currentCar.vin" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="发动机号" prop="engineNumber">
              <el-input v-model="currentCar.engineNumber" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="购买日期" prop="purchaseDate">
              <el-date-picker
                v-model="currentCar.purchaseDate"
                type="date"
                placeholder="选择购买日期"
                style="width: 100%"
                value-format="YYYY-MM-DD"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="当前里程" prop="mileage">
              <el-input-number
                v-model="currentCar.mileage"
                :min="0"
                controls-position="right"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="备注">
          <el-input
            v-model="currentCar.remarks"
            type="textarea"
            :rows="3"
            placeholder="请输入车辆备注信息"
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
  Van, 
  Check, 
  Tools, 
  Lightning,
  Download 
} from '@element-plus/icons-vue'

const loading = ref(false)
const dialogVisible = ref(false)
const dialogTitle = ref('')
const carForm = ref(null)

// 搜索表单
const searchForm = reactive({
  plateNumber: '',
  type: '',
  status: ''
})

// 统计数据
const stats = reactive({
  total: 36,
  normal: 32,
  maintenance: 3,
  electric: 8
})

// 分页数据
const pagination = reactive({
  currentPage: 1,
  pageSize: 20,
  total: 36
})

// 车辆列表数据
const carList = ref([
  {
    id: 1,
    plateNumber: '粤A12345',
    brand: '大众桑塔纳',
    type: 'manual',
    vin: 'LSVXX123456789012',
    engineNumber: '123456789',
    purchaseDate: '2020-03-15',
    mileage: 45000,
    lastMaintenance: '2024-02-20',
    status: 'normal',
    remarks: '日常训练用车'
  },
  {
    id: 2,
    plateNumber: '粤B67890',
    brand: '丰田卡罗拉',
    type: 'automatic',
    vin: 'JTDBB123456789012',
    engineNumber: '987654321',
    purchaseDate: '2021-07-22',
    mileage: 32000,
    lastMaintenance: '2024-01-15',
    status: 'normal',
    remarks: '自动挡训练用车'
  }
])

// 当前编辑的车辆
const currentCar = reactive({
  id: null,
  plateNumber: '',
  brand: '',
  type: '',
  vin: '',
  engineNumber: '',
  purchaseDate: '',
  mileage: 0,
  status: 'normal',
  remarks: ''
})

// 表单验证规则
const rules = {
  plateNumber: [
    { required: true, message: '请输入车牌号', trigger: 'blur' },
    { pattern: /^[京津沪渝冀豫云辽黑湘皖鲁新苏浙赣鄂桂甘晋蒙陕吉闽贵粤青藏川宁琼使领A-Z]{1}[A-Z]{1}[A-Z0-9]{4}[A-Z0-9挂学警港澳]{1}$/, message: '请输入正确的车牌号格式', trigger: 'blur' }
  ],
  brand: [
    { required: true, message: '请输入品牌型号', trigger: 'blur' }
  ],
  type: [
    { required: true, message: '请选择车辆类型', trigger: 'change' }
  ],
  vin: [
    { required: true, message: '请输入车架号', trigger: 'blur' }
  ]
}

// 获取车牌标签类型
const getPlateTagType = (plateNumber) => {
  if (plateNumber.startsWith('粤A')) return 'primary'
  if (plateNumber.startsWith('粤B')) return 'success'
  if (plateNumber.startsWith('粤C')) return 'warning'
  return 'info'
}

// 获取类型标签类型
const getTypeTagType = (type) => {
  const typeMap = {
    manual: 'info',
    automatic: 'primary',
    electric: 'success'
  }
  return typeMap[type] || 'info'
}

// 获取类型文本
const getTypeText = (type) => {
  const textMap = {
    manual: '手动挡',
    automatic: '自动挡',
    electric: '电动车'
  }
  return textMap[type] || type
}

// 获取状态标签类型
const getStatusTagType = (status) => {
  const typeMap = {
    normal: 'success',
    maintenance: 'warning',
    scrap: 'danger'
  }
  return typeMap[status] || 'info'
}

// 获取状态文本
const getStatusText = (status) => {
  const textMap = {
    normal: '正常',
    maintenance: '维修中',
    scrap: '已报废'
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
  dialogTitle.value = '新增车辆'
  Object.keys(currentCar).forEach(key => {
    currentCar[key] = key === 'mileage' ? 0 : ''
  })
  currentCar.status = 'normal'
  dialogVisible.value = true
}

// 处理查看
const handleView = (row) => {
  dialogTitle.value = '查看车辆'
  Object.assign(currentCar, row)
  dialogVisible.value = true
}

// 处理编辑
const handleEdit = (row) => {
  dialogTitle.value = '编辑车辆'
  Object.assign(currentCar, row)
  dialogVisible.value = true
}

// 处理删除
const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm(
      `确定要删除车辆 "${row.plateNumber}" 吗？`,
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
    await carForm.value.validate()
    
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
  console.log('选中的车辆:', selection)
}

onMounted(() => {
  handleRefresh()
})
</script>

<style scoped>
@import '@/styles/table-adaptive-optimization.css';

.adaptive-car-container {
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
  background: linear-gradient(135deg, #a8edea 0%, #fed6e3 100%);
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
  background: linear-gradient(135deg, #a8edea 0%, #fed6e3 100%);
}

.stat-card.normal .stat-icon {
  background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
}

.stat-card.maintenance .stat-icon {
  background: linear-gradient(135deg, #fa709a 0%, #fee140 100%);
}

.stat-card.electric .stat-icon {
  background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%);
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

.plate-cell {
  display: flex;
  justify-content: center;
}

.action-buttons {
  display: flex;
  gap: 8px;
}

.action-btn {
  padding: 4px 8px;
  font-size: 12px;
}

.car-dialog :deep(.el-dialog__body) {
  padding: 20px;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
}
</style>