<template>
  <div class="home-page">
    <!-- 数据概览卡片 -->
    <el-row :gutter="20" class="overview-cards">
      <el-col :span="6">
        <el-card class="overview-card" @click="goToRecords">
          <div class="card-content">
            <div class="card-icon warning">
              <el-icon size="32"><Warning /></el-icon>
            </div>
            <div class="card-info">
              <div class="card-title">今日违规次数</div>
              <div class="card-number">{{ todayViolations }}</div>
              <div class="card-trend">
                <el-icon color="#f56c6c"><Top /></el-icon>
                <span class="trend-up">较昨日 +{{ yesterdayViolationsDiff }}%</span>
              </div>
            </div>
          </div>
        </el-card>
      </el-col>
      
      <el-col :span="6">
        <el-card class="overview-card" @click="goToWarning">
          <div class="card-content">
            <div class="card-icon danger">
              <el-icon size="32"><Bell /></el-icon>
            </div>
            <div class="card-info">
              <div class="card-title">未处理记录数</div>
              <div class="card-number">{{ unhandledRecords }}</div>
              <div class="card-trend">
                <span class="trend-normal">需及时处理</span>
              </div>
            </div>
          </div>
        </el-card>
      </el-col>
      
      <el-col :span="6">
        <el-card class="overview-card" @click="goToMonitoring">
          <div class="card-content">
            <div class="card-icon success">
              <el-icon size="32"><User /></el-icon>
            </div>
            <div class="card-info">
              <div class="card-title">今日练车人数</div>
              <div class="card-number">{{ todayTrainees }}</div>
              <div class="card-trend">
                <el-icon color="#67c23a"><TrendCharts /></el-icon>
                <span class="trend-up">正常进行中</span>
              </div>
            </div>
          </div>
        </el-card>
      </el-col>
      
      <el-col :span="6">
        <el-card class="overview-card" @click="goToVehicle">
          <div class="card-content">
            <div class="card-icon info">
              <el-icon size="32"><Van /></el-icon>
            </div>
            <div class="card-info">
              <div class="card-title">正常车辆数</div>
              <div class="card-number">{{ normalVehicles }}</div>
              <div class="card-trend">
                <span class="trend-normal">运行状态良好</span>
              </div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>
    
    <!-- 实时预警提示 -->
    <el-row :gutter="20" class="warning-section">
      <el-col :span="16">
        <el-card class="real-time-warning">
          <template #header>
            <div class="card-header">
              <span>实时预警</span>
              <el-tag type="danger" v-if="activeWarnings.length > 0">
                有{{ activeWarnings.length }}条新预警
              </el-tag>
            </div>
          </template>
          
          <div v-if="activeWarnings.length === 0" class="no-warnings">
            <el-icon size="48"><SuccessFilled /></el-icon>
            <p>当前无预警信息</p>
          </div>
          
          <div v-else class="warning-list">
            <div
              v-for="warning in activeWarnings"
              :key="warning.id"
              class="warning-item"
              @click="handleWarningClick(warning)"
            >
              <div class="warning-type" :class="warning.level">
                {{ warning.level === 'high' ? '高危' : warning.level === 'medium' ? '中危' : '低危' }}
              </div>
              <div class="warning-content">
                <div class="warning-title">{{ warning.title }}</div>
                <div class="warning-desc">{{ warning.description }}</div>
                <div class="warning-time">{{ formatTime(warning.time) }}</div>
              </div>
              <div class="warning-actions">
                <el-button size="small" type="primary" @click.stop="handleWarning(warning)">处理</el-button>
              </div>
            </div>
          </div>
        </el-card>
      </el-col>
      
      <el-col :span="8">
        <el-card class="quick-actions">
          <template #header>
            <span>快捷操作</span>
          </template>
          
          <div class="actions-grid">
            <div class="action-item" @click="goToFaceVerification">
              <el-icon size="24"><User /></el-icon>
              <span>人脸识别</span>
            </div>
            <div class="action-item" @click="goToPlateVerification">
              <el-icon size="24"><CreditCard /></el-icon>
              <span>车牌识别</span>
            </div>
            <div class="action-item" @click="goToMonitoring">
              <el-icon size="24"><VideoCamera /></el-icon>
              <span>实时监控</span>
            </div>
            <div class="action-item" @click="goToRecords">
              <el-icon size="24"><Document /></el-icon>
              <span>违规记录</span>
            </div>
            <div class="action-item" @click="goToStatistics">
              <el-icon size="24"><PieChart /></el-icon>
              <span>统计报表</span>
            </div>
            <div class="action-item" @click="goToSettings">
              <el-icon size="24"><Setting /></el-icon>
              <span>系统设置</span>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>
    
    <!-- 图表统计区域 -->
    <el-row :gutter="20" class="charts-section">
      <el-col :span="12">
        <el-card>
          <template #header>
            <span>近7日违规趋势</span>
          </template>
          <div ref="trendChartRef" style="height: 300px;"></div>
        </el-card>
      </el-col>
      
      <el-col :span="12">
        <el-card>
          <template #header>
            <span>违规类型分布</span>
          </template>
          <div ref="typeChartRef" style="height: 300px;"></div>
        </el-card>
      </el-col>
    </el-row>
    
    <!-- 预警处理弹窗 -->
    <el-dialog
      v-model="warningDialogVisible"
      title="处理预警"
      width="500px"
    >
      <div v-if="selectedWarning" class="warning-detail">
        <el-descriptions :column="1" border>
          <el-descriptions-item label="预警类型">{{ selectedWarning.title }}</el-descriptions-item>
          <el-descriptions-item label="预警级别">
            <el-tag :type="getWarningLevelType(selectedWarning.level)">
              {{ selectedWarning.level === 'high' ? '高危' : selectedWarning.level === 'medium' ? '中危' : '低危' }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="发生时间">{{ formatTime(selectedWarning.time) }}</el-descriptions-item>
          <el-descriptions-item label="详细描述">{{ selectedWarning.description }}</el-descriptions-item>
        </el-descriptions>
        
        <el-form style="margin-top: 20px;">
          <el-form-item label="处理备注">
            <el-input
              v-model="handleRemark"
              type="textarea"
              :rows="3"
              placeholder="请输入处理备注..."
            />
          </el-form-item>
        </el-form>
      </div>
      
      <template #footer>
        <el-button @click="warningDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="confirmHandleWarning">确认处理</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, nextTick } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import * as echarts from 'echarts'
import { useWarningStore } from '@/store/warning'

const router = useRouter()
const warningStore = useWarningStore()

// 数据概览
const todayViolations = ref(12)
const yesterdayViolationsDiff = ref(15)
const unhandledRecords = ref(5)
const todayTrainees = ref(48)
const normalVehicles = ref(25)

// 预警相关
const activeWarnings = ref([
  {
    id: 1,
    level: 'high',
    title: '未授权人员闯入',
    description: '检测到陌生人员进入科目二训练区域',
    time: new Date(Date.now() - 1000 * 60 * 5),
    handled: false
  },
  {
    id: 2,
    level: 'medium',
    title: '车辆超速行驶',
    description: '粤A12345在科目三路线上超速20%',
    time: new Date(Date.now() - 1000 * 60 * 15),
    handled: false
  }
])

const warningDialogVisible = ref(false)
const selectedWarning = ref(null)
const handleRemark = ref('')

// 图表引用
const trendChartRef = ref()
const typeChartRef = ref()

// 导航方法
const goToRecords = () => router.push('/dashboard/records')
const goToWarning = () => router.push('/dashboard/warning')
const goToMonitoring = () => router.push('/dashboard/monitoring')
const goToVehicle = () => router.push('/dashboard/vehicle-management')
const goToFaceVerification = () => router.push('/dashboard/face-verification')
const goToPlateVerification = () => router.push('/dashboard/plate-verification')
const goToStatistics = () => router.push('/dashboard/statistics')
const goToSettings = () => router.push('/dashboard/settings')

// 时间格式化
const formatTime = (time) => {
  return new Date(time).toLocaleString('zh-CN', {
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit'
  })
}

// 预警处理
const handleWarningClick = (warning) => {
  selectedWarning.value = warning
  warningDialogVisible.value = true
}

const handleWarning = (warning) => {
  selectedWarning.value = warning
  warningDialogVisible.value = true
}

const confirmHandleWarning = () => {
  if (!handleRemark.value.trim()) {
    ElMessage.warning('请输入处理备注')
    return
  }
  
  warningStore.handleWarning(selectedWarning.value.id, handleRemark.value)
  warningStore.markAsRead(selectedWarning.value.id)
  
  // 更新本地数据
  const index = activeWarnings.value.findIndex(w => w.id === selectedWarning.value.id)
  if (index > -1) {
    activeWarnings.value.splice(index, 1)
  }
  
  warningDialogVisible.value = false
  handleRemark.value = ''
  selectedWarning.value = null
  
  ElMessage.success('预警处理成功')
}

const getWarningLevelType = (level) => {
  const typeMap = {
    'high': 'danger',
    'medium': 'warning',
    'low': 'info'
  }
  return typeMap[level] || 'info'
}

// 初始化图表
const initCharts = () => {
  nextTick(() => {
    // 违规趋势图
    const trendChart = echarts.init(trendChartRef.value)
    trendChart.setOption({
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
    
    // 违规类型分布图
    const typeChart = echarts.init(typeChartRef.value)
    typeChart.setOption({
      tooltip: {
        trigger: 'item'
      },
      legend: {
        bottom: '5%'
      },
      series: [{
        type: 'pie',
        radius: ['40%', '70%'],
        data: [
          { value: 35, name: '越界行驶' },
          { value: 25, name: '超速行驶' },
          { value: 20, name: '违规停车' },
          { value: 15, name: '其他违规' },
          { value: 5, name: '设备故障' }
        ]
      }]
    })
    
    // 监听窗口大小变化
    window.addEventListener('resize', () => {
      trendChart.resize()
      typeChart.resize()
    })
  })
}

onMounted(() => {
  initCharts()
})
</script>

<style scoped>
.home-page {
  padding: 0;
}

.overview-cards {
  margin-bottom: 20px;
}

.overview-card {
  cursor: pointer;
  transition: transform 0.2s;
}

.overview-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.card-content {
  display: flex;
  align-items: center;
  gap: 20px;
}

.card-icon {
  width: 60px;
  height: 60px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
}

.card-icon.warning {
  background: linear-gradient(135deg, #ff9a9e 0%, #fecfef 100%);
}

.card-icon.danger {
  background: linear-gradient(135deg, #ff6b6b 0%, #ffa5a5 100%);
}

.card-icon.success {
  background: linear-gradient(135deg, #51cf66 0%, #8ce99a 100%);
}

.card-icon.info {
  background: linear-gradient(135deg, #339af0 0%, #74c0fc 100%);
}

.card-info {
  flex: 1;
}

.card-title {
  font-size: 14px;
  color: #666;
  margin-bottom: 8px;
}

.card-number {
  font-size: 28px;
  font-weight: bold;
  color: #333;
  margin-bottom: 5px;
}

.card-trend {
  display: flex;
  align-items: center;
  gap: 5px;
  font-size: 12px;
}

.trend-up {
  color: #f56c6c;
}

.trend-normal {
  color: #909399;
}

.warning-section {
  margin-bottom: 20px;
}

.real-time-warning .card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.no-warnings {
  text-align: center;
  padding: 40px 0;
  color: #999;
}

.no-warnings .el-icon {
  margin-bottom: 15px;
  color: #67c23a;
}

.warning-list {
  max-height: 300px;
  overflow-y: auto;
}

.warning-item {
  display: flex;
  align-items: center;
  padding: 15px;
  border: 1px solid #eee;
  border-radius: 8px;
  margin-bottom: 10px;
  cursor: pointer;
  transition: all 0.2s;
}

.warning-item:hover {
  border-color: #409eff;
  box-shadow: 0 2px 8px rgba(64, 158, 255, 0.2);
}

.warning-type {
  padding: 4px 12px;
  border-radius: 12px;
  font-size: 12px;
  font-weight: 500;
  margin-right: 15px;
  color: white;
}

.warning-type.high {
  background-color: #f56c6c;
}

.warning-type.medium {
  background-color: #e6a23c;
}

.warning-type.low {
  background-color: #909399;
}

.warning-content {
  flex: 1;
}

.warning-title {
  font-weight: 500;
  color: #333;
  margin-bottom: 5px;
}

.warning-desc {
  font-size: 13px;
  color: #666;
  margin-bottom: 5px;
}

.warning-time {
  font-size: 12px;
  color: #999;
}

.quick-actions .actions-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 15px;
}

.action-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 20px;
  border: 1px solid #eee;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.2s;
}

.action-item:hover {
  border-color: #409eff;
  background-color: #f5f7fa;
  transform: translateY(-2px);
}

.action-item .el-icon {
  margin-bottom: 8px;
  color: #409eff;
}

.action-item span {
  font-size: 14px;
  color: #333;
}

.charts-section {
  margin-bottom: 20px;
}

.warning-detail {
  line-height: 1.6;
}
</style>