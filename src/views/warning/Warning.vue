<template>
  <div class="warning-system">
    <!-- 统计概览 -->
    <el-row :gutter="20" class="stats-overview">
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon primary">
              <el-icon size="24"><Bell /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-number">{{ totalWarnings }}</div>
              <div class="stat-label">总预警数</div>
            </div>
          </div>
        </el-card>
      </el-col>
      
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon danger">
              <el-icon size="24"><Warning /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-number">{{ unreadWarnings }}</div>
              <div class="stat-label">未读预警</div>
            </div>
          </div>
        </el-card>
      </el-col>
      
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon warning">
              <el-icon size="24"><Clock /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-number">{{ unhandledWarnings }}</div>
              <div class="stat-label">未处理预警</div>
            </div>
          </div>
        </el-card>
      </el-col>
      
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon success">
              <el-icon size="24"><Check /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-number">{{ handledWarnings }}</div>
              <div class="stat-label">已处理预警</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>
    
    <!-- 筛选和操作栏 -->
    <el-card class="filter-card">
      <el-form :model="filterForm" inline>
        <el-form-item label="预警级别">
          <el-select v-model="filterForm.level" placeholder="请选择级别" clearable>
            <el-option label="高危" value="high" />
            <el-option label="中危" value="medium" />
            <el-option label="低危" value="low" />
          </el-select>
        </el-form-item>
        
        <el-form-item label="处理状态">
          <el-select v-model="filterForm.status" placeholder="请选择状态" clearable>
            <el-option label="未读" value="unread" />
            <el-option label="已读未处理" value="read" />
            <el-option label="已处理" value="handled" />
          </el-select>
        </el-form-item>
        
        <el-form-item label="时间范围">
          <el-date-picker
            v-model="filterForm.dateRange"
            type="daterange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            value-format="YYYY-MM-DD"
          />
        </el-form-item>
        
        <el-form-item>
          <el-button type="primary" @click="handleFilter">筛选</el-button>
          <el-button @click="resetFilter">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>
    
    <!-- 预警列表 -->
    <el-row :gutter="20">
      <el-col :span="16">
        <el-card class="warnings-card">
          <template #header>
            <div class="card-header">
              <span>预警列表</span>
              <div class="header-actions">
                <el-button @click="refreshWarnings">
                  <el-icon><Refresh /></el-icon>
                  刷新
                </el-button>
                <el-button
                  type="primary"
                  @click="handleBatchProcess"
                  :disabled="selectedWarnings.length === 0"
                >
                  批量处理
                </el-button>
              </div>
            </div>
          </template>
          
          <el-table
            :data="filteredWarnings"
            @selection-change="handleSelectionChange"
            v-loading="loading"
          >
            <el-table-column type="selection" width="55" />
            <el-table-column type="index" label="序号" width="60" />
            <el-table-column prop="createTime" label="预警时间" width="180">
              <template #default="{ row }">
                {{ formatTime(row.createTime) }}
              </template>
            </el-table-column>
            <el-table-column prop="level" label="预警级别" width="100">
              <template #default="{ row }">
                <el-tag :type="getLevelType(row.level)" size="small">
                  {{ getLevelText(row.level) }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="title" label="预警标题" width="200" />
            <el-table-column prop="content" label="预警内容" show-overflow-tooltip />
            <el-table-column prop="status" label="处理状态" width="120">
              <template #default="{ row }">
                <el-tag :type="getStatusType(row.status)" size="small">
                  {{ getStatusText(row.status) }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column label="操作" width="200" fixed="right">
              <template #default="{ row }">
                <el-button
                  v-if="row.status !== 'handled'"
                  type="primary"
                  size="small"
                  @click="handleWarning(row)"
                >
                  处理
                </el-button>
                <el-button
                  type="primary"
                  link
                  size="small"
                  @click="viewWarningDetail(row)"
                >
                  查看详情
                </el-button>
              </template>
            </el-table-column>
          </el-table>
          
          <div class="pagination">
            <el-pagination
              v-model:current-page="currentPage"
              v-model:page-size="pageSize"
              :total="filteredWarnings.length"
              layout="total, sizes, prev, pager, next, jumper"
              @size-change="handleSizeChange"
              @current-change="handleCurrentChange"
            />
          </div>
        </el-card>
      </el-col>
      
      <el-col :span="8">
        <el-card class="summary-card">
          <template #header>
            <span>预警统计</span>
          </template>
          
          <div class="level-statistics">
            <div class="stat-item">
              <div class="stat-label">高危预警</div>
              <div class="stat-value danger">{{ levelStats.high }}</div>
            </div>
            <div class="stat-item">
              <div class="stat-label">中危预警</div>
              <div class="stat-value warning">{{ levelStats.medium }}</div>
            </div>
            <div class="stat-item">
              <div class="stat-label">低危预警</div>
              <div class="stat-value info">{{ levelStats.low }}</div>
            </div>
          </div>
          
          <div class="trend-chart" ref="trendChartRef" style="height: 200px; margin-top: 20px;"></div>
        </el-card>
      </el-col>
    </el-row>
    
    <!-- 预警处理弹窗 -->
    <el-dialog
      v-model="handleDialogVisible"
      title="处理预警"
      width="600px"
    >
      <div v-if="selectedWarning" class="warning-handle">
        <el-descriptions :column="2" border size="small">
          <el-descriptions-item label="预警时间">
            {{ formatTime(selectedWarning.createTime) }}
          </el-descriptions-item>
          <el-descriptions-item label="预警级别">
            <el-tag :type="getLevelType(selectedWarning.level)">
              {{ getLevelText(selectedWarning.level) }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="预警标题" :span="2">
            {{ selectedWarning.title }}
          </el-descriptions-item>
          <el-descriptions-item label="预警内容" :span="2">
            {{ selectedWarning.content }}
          </el-descriptions-item>
        </el-descriptions>
        
        <el-form style="margin-top: 20px;">
          <el-form-item label="处理方式">
            <el-radio-group v-model="handleForm.method">
              <el-radio label="ignore">忽略</el-radio>
              <el-radio label="process">处理</el-radio>
              <el-radio label="escalate">升级</el-radio>
            </el-radio-group>
          </el-form-item>
          
          <el-form-item label="处理备注">
            <el-input
              v-model="handleForm.remark"
              type="textarea"
              :rows="4"
              placeholder="请输入处理备注..."
            />
          </el-form-item>
        </el-form>
      </div>
      
      <template #footer>
        <el-button @click="handleDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="confirmHandle">确认处理</el-button>
      </template>
    </el-dialog>
    
    <!-- 预警详情弹窗 -->
    <el-dialog
      v-model="detailDialogVisible"
      title="预警详情"
      width="700px"
    >
      <div v-if="detailWarning" class="warning-detail">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="预警ID">{{ detailWarning.id }}</el-descriptions-item>
          <el-descriptions-item label="预警时间">{{ formatTime(detailWarning.createTime) }}</el-descriptions-item>
          <el-descriptions-item label="预警级别">
            <el-tag :type="getLevelType(detailWarning.level)">
              {{ getLevelText(detailWarning.level) }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="处理状态">
            <el-tag :type="getStatusType(detailWarning.status)">
              {{ getStatusText(detailWarning.status) }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="预警标题" :span="2">{{ detailWarning.title }}</el-descriptions-item>
          <el-descriptions-item label="预警内容" :span="2">{{ detailWarning.content }}</el-descriptions-item>
          <el-descriptions-item v-if="detailWarning.handleTime" label="处理时间">
            {{ formatTime(detailWarning.handleTime) }}
          </el-descriptions-item>
          <el-descriptions-item v-if="detailWarning.remark" label="处理备注" :span="detailWarning.handleTime ? 1 : 2">
            {{ detailWarning.remark }}
          </el-descriptions-item>
        </el-descriptions>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted, nextTick } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import * as echarts from 'echarts'
import { useWarningStore } from '@/store/warning'

const warningStore = useWarningStore()

// 筛选表单
const filterForm = reactive({
  level: '',
  status: '',
  dateRange: []
})

// 表格数据
const allWarnings = ref([
  {
    id: 1,
    createTime: new Date(Date.now() - 1000 * 60 * 5),
    level: 'high',
    title: '未授权人员闯入',
    content: '检测到陌生人员进入科目二训练区域，已触发高级别预警',
    status: 'unread'
  },
  {
    id: 2,
    createTime: new Date(Date.now() - 1000 * 60 * 15),
    level: 'medium',
    title: '车辆超速行驶',
    content: '粤A12345在科目三路线上超速20%，请立即处理',
    status: 'read'
  },
  {
    id: 3,
    createTime: new Date(Date.now() - 1000 * 60 * 30),
    level: 'low',
    title: '设备离线提醒',
    content: '科目二倒车入库2号摄像头离线，请及时检查',
    status: 'handled',
    handleTime: new Date(Date.now() - 1000 * 60 * 25),
    remark: '已联系技术人员处理'
  },
  {
    id: 4,
    createTime: new Date(Date.now() - 1000 * 60 * 45),
    level: 'high',
    title: '多次违规警告',
    content: '学员张三在短时间内连续违反多项规定，请重点关注',
    status: 'unread'
  }
])

const loading = ref(false)
const currentPage = ref(1)
const pageSize = ref(10)
const selectedWarnings = ref([])
const trendChartRef = ref()

// 弹窗控制
const handleDialogVisible = ref(false)
const detailDialogVisible = ref(false)
const selectedWarning = ref(null)
const detailWarning = ref(null)

// 处理表单
const handleForm = reactive({
  method: 'process',
  remark: ''
})

// 计算属性
const filteredWarnings = computed(() => {
  let result = [...allWarnings.value]
  
  // 级别筛选
  if (filterForm.level) {
    result = result.filter(w => w.level === filterForm.level)
  }
  
  // 状态筛选
  if (filterForm.status) {
    result = result.filter(w => {
      if (filterForm.status === 'unread') return w.status === 'unread'
      if (filterForm.status === 'read') return w.status === 'read'
      if (filterForm.status === 'handled') return w.status === 'handled'
      return true
    })
  }
  
  // 时间筛选
  if (filterForm.dateRange && filterForm.dateRange.length === 2) {
    const [startDate, endDate] = filterForm.dateRange
    result = result.filter(w => {
      const warningDate = new Date(w.createTime).toISOString().split('T')[0]
      return warningDate >= startDate && warningDate <= endDate
    })
  }
  
  return result
})

const totalWarnings = computed(() => allWarnings.value.length)
const unreadWarnings = computed(() => allWarnings.value.filter(w => w.status === 'unread').length)
const unhandledWarnings = computed(() => allWarnings.value.filter(w => w.status !== 'handled').length)
const handledWarnings = computed(() => allWarnings.value.filter(w => w.status === 'handled').length)

const levelStats = computed(() => {
  const stats = { high: 0, medium: 0, low: 0 }
  allWarnings.value.forEach(w => {
    stats[w.level]++
  })
  return stats
})

// 方法定义
const handleFilter = () => {
  ElMessage.success('筛选完成')
}

const resetFilter = () => {
  filterForm.level = ''
  filterForm.status = ''
  filterForm.dateRange = []
  ElMessage.info('筛选条件已重置')
}

const refreshWarnings = () => {
  loading.value = true
  setTimeout(() => {
    loading.value = false
    ElMessage.success('数据已刷新')
  }, 500)
}

const handleSelectionChange = (selection) => {
  selectedWarnings.value = selection
}

const handleBatchProcess = async () => {
  try {
    await ElMessageBox.confirm(
      `确定批量处理选中的 ${selectedWarnings.value.length} 条预警吗？`,
      '批量处理确认',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    // 批量处理逻辑
    selectedWarnings.value.forEach(warning => {
      const index = allWarnings.value.findIndex(w => w.id === warning.id)
      if (index > -1) {
        allWarnings.value[index].status = 'handled'
        allWarnings.value[index].handleTime = new Date()
        allWarnings.value[index].remark = '批量处理'
      }
    })
    
    ElMessage.success('批量处理完成')
    selectedWarnings.value = []
  } catch {
    // 用户取消
  }
}

const handleWarning = (warning) => {
  selectedWarning.value = warning
  handleForm.method = 'process'
  handleForm.remark = ''
  handleDialogVisible.value = true
}

const viewWarningDetail = (warning) => {
  detailWarning.value = warning
  detailDialogVisible.value = true
}

const confirmHandle = () => {
  if (!handleForm.remark.trim()) {
    ElMessage.warning('请输入处理备注')
    return
  }
  
  const index = allWarnings.value.findIndex(w => w.id === selectedWarning.value.id)
  if (index > -1) {
    allWarnings.value[index].status = 'handled'
    allWarnings.value[index].handleTime = new Date()
    allWarnings.value[index].remark = handleForm.remark
  }
  
  handleDialogVisible.value = false
  ElMessage.success('预警处理完成')
}

const handleSizeChange = (val) => {
  pageSize.value = val
}

const handleCurrentChange = (val) => {
  currentPage.value = val
}

// 工具方法
const getLevelType = (level) => {
  const typeMap = {
    'high': 'danger',
    'medium': 'warning',
    'low': 'info'
  }
  return typeMap[level] || 'info'
}

const getLevelText = (level) => {
  const textMap = {
    'high': '高危',
    'medium': '中危',
    'low': '低危'
  }
  return textMap[level] || '未知'
}

const getStatusType = (status) => {
  const typeMap = {
    'unread': 'danger',
    'read': 'warning',
    'handled': 'success'
  }
  return typeMap[status] || 'info'
}

const getStatusText = (status) => {
  const textMap = {
    'unread': '未读',
    'read': '已读未处理',
    'handled': '已处理'
  }
  return textMap[status] || '未知'
}

const formatTime = (time) => {
  return new Date(time).toLocaleString('zh-CN')
}

// 初始化图表
const initChart = () => {
  nextTick(() => {
    if (!trendChartRef.value) return
    
    const chart = echarts.init(trendChartRef.value)
    chart.setOption({
      tooltip: {
        trigger: 'axis'
      },
      xAxis: {
        type: 'category',
        data: ['周一', '周二', '周三', '周四', '周五', '周六', '周日']
      },
      yAxis: {
        type: 'value'
      },
      series: [{
        data: [12, 8, 15, 10, 18, 9, 14],
        type: 'line',
        smooth: true,
        areaStyle: {
          opacity: 0.3
        }
      }]
    })
    
    window.addEventListener('resize', () => {
      chart.resize()
    })
  })
}

onMounted(() => {
  initChart()
})
</script>

<style scoped>
.warning-system {
  padding: 0;
}

.stats-overview {
  margin-bottom: 20px;
}

.stat-card {
  height: 100px;
}

.stat-content {
  display: flex;
  align-items: center;
  gap: 20px;
  height: 100%;
}

.stat-icon {
  width: 50px;
  height: 50px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
}

.stat-icon.primary {
  background: linear-gradient(135deg, #409eff 0%, #79bbff 100%);
}

.stat-icon.danger {
  background: linear-gradient(135deg, #f56c6c 0%, #ff7875 100%);
}

.stat-icon.warning {
  background: linear-gradient(135deg, #e6a23c 0%, #f3d19e 100%);
}

.stat-icon.success {
  background: linear-gradient(135deg, #67c23a 0%, #85ce61 100%);
}

.stat-info {
  flex: 1;
}

.stat-number {
  font-size: 24px;
  font-weight: bold;
  color: #333;
  margin-bottom: 5px;
}

.stat-label {
  font-size: 14px;
  color: #666;
}

.filter-card {
  margin-bottom: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.header-actions {
  display: flex;
  gap: 10px;
}

.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}

.summary-card .level-statistics {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.stat-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 10px;
  background-color: #f5f7fa;
  border-radius: 6px;
}

.stat-label {
  font-size: 14px;
  color: #666;
}

.stat-value {
  font-size: 18px;
  font-weight: bold;
}

.stat-value.danger {
  color: #f56c6c;
}

.stat-value.warning {
  color: #e6a23c;
}

.stat-value.info {
  color: #909399;
}

.warning-handle .el-descriptions {
  margin-bottom: 20px;
}

.warning-detail .el-descriptions {
  margin-bottom: 20px;
}
</style>