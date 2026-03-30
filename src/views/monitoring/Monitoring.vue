<template>
  <div class="monitoring-system">
    <el-row :gutter="20">
      <!-- 左侧监控画面区域 -->
      <el-col :span="16">
        <el-card class="monitor-card">
          <template #header>
            <div class="card-header">
              <span>实时监控画面</span>
              <div class="monitor-controls">
                <el-select v-model="selectedArea" placeholder="选择监控区域" size="small">
                  <el-option label="全部区域" value="all" />
                  <el-option label="科目二倒车入库" value="subject2_parking" />
                  <el-option label="科目二侧方停车" value="subject2_side" />
                  <el-option label="科目三路线1" value="subject3_route1" />
                  <el-option label="科目三路线2" value="subject3_route2" />
                </el-select>
                
                <el-select v-model="timeRange" placeholder="选择时段" size="small" style="margin-left: 10px;">
                  <el-option label="全天" value="all" />
                  <el-option label="上午 (8:00-12:00)" value="morning" />
                  <el-option label="下午 (12:00-18:00)" value="afternoon" />
                  <el-option label="晚上 (18:00-22:00)" value="evening" />
                </el-select>
              </div>
            </div>
          </template>
          
          <div class="monitor-grid">
            <div
              v-for="camera in cameras"
              :key="camera.id"
              class="camera-item"
              :class="{ active: selectedCamera === camera.id }"
              @click="selectCamera(camera.id)"
            >
              <div class="camera-header">
                <span class="camera-name">{{ camera.name }}</span>
                <el-tag :type="camera.status === 'online' ? 'success' : 'danger'" size="small">
                  {{ camera.status === 'online' ? '在线' : '离线' }}
                </el-tag>
              </div>
              
              <div class="camera-feed">
                <img
                  v-if="camera.status === 'online'"
                  :src="camera.feedUrl"
                  :alt="camera.name"
                  class="feed-image"
                />
                <div v-else class="offline-placeholder">
                  <el-icon size="32"><VideoCameraOff /></el-icon>
                  <p>摄像头离线</p>
                </div>
                
                <!-- 违规标识 -->
                <div
                  v-if="camera.violation"
                  class="violation-indicator"
                  :class="camera.violation.level"
                >
                  <el-icon><Warning /></el-icon>
                  <span>{{ camera.violation.type }}</span>
                </div>
              </div>
            </div>
          </div>
        </el-card>
      </el-col>
      
      <!-- 右侧违规记录区域 -->
      <el-col :span="8">
        <el-card class="violations-card">
          <template #header>
            <div class="card-header">
              <span>违规监测列表</span>
              <el-button
                type="primary"
                size="small"
                @click="refreshViolations"
              >
                <el-icon><Refresh /></el-icon>
                刷新
              </el-button>
            </div>
          </template>
          
          <div class="violations-list">
            <div
              v-for="violation in violations"
              :key="violation.id"
              class="violation-item"
              :class="{ unread: !violation.read }"
              @click="viewViolationDetail(violation)"
            >
              <div class="violation-header">
                <div class="violation-time">{{ formatTime(violation.time) }}</div>
                <el-tag :type="getViolationLevelType(violation.level)" size="small">
                  {{ violation.level === 'high' ? '高危' : violation.level === 'medium' ? '中危' : '低危' }}
                </el-tag>
              </div>
              
              <div class="violation-content">
                <div class="violation-type">{{ violation.type }}</div>
                <div class="violation-location">{{ violation.location }}</div>
                <div class="violation-person">{{ violation.personName }}</div>
              </div>
              
              <div class="violation-actions">
                <el-button
                  v-if="!violation.read"
                  type="primary"
                  size="small"
                  link
                  @click.stop="markAsRead(violation)"
                >
                  标记已读
                </el-button>
                <el-button
                  type="primary"
                  size="small"
                  link
                  @click.stop="viewEvidence(violation)"
                >
                  查看证据
                </el-button>
              </div>
            </div>
            
            <div v-if="violations.length === 0" class="no-violations">
              <el-icon size="48"><SuccessFilled /></el-icon>
              <p>暂无违规记录</p>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>
    
    <!-- 违规详情弹窗 -->
    <el-dialog
      v-model="detailDialogVisible"
      title="违规详情"
      width="600px"
    >
      <div v-if="selectedViolation" class="violation-detail">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="违规时间">
            {{ formatTime(selectedViolation.time) }}
          </el-descriptions-item>
          <el-descriptions-item label="违规级别">
            <el-tag :type="getViolationLevelType(selectedViolation.level)">
              {{ selectedViolation.level === 'high' ? '高危' : selectedViolation.level === 'medium' ? '中危' : '低危' }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="违规类型">{{ selectedViolation.type }}</el-descriptions-item>
          <el-descriptions-item label="发生地点">{{ selectedViolation.location }}</el-descriptions-item>
          <el-descriptions-item label="涉及人员">{{ selectedViolation.personName }}</el-descriptions-item>
          <el-descriptions-item label="所属教练">{{ selectedViolation.coachName }}</el-descriptions-item>
          <el-descriptions-item label="涉及车辆">{{ selectedViolation.vehiclePlate }}</el-descriptions-item>
          <el-descriptions-item label="状态">
            <el-tag :type="selectedViolation.read ? 'success' : 'warning'">
              {{ selectedViolation.read ? '已读' : '未读' }}
            </el-tag>
          </el-descriptions-item>
        </el-descriptions>
        
        <div class="evidence-section" style="margin-top: 20px;">
          <h4>现场证据</h4>
          <div class="evidence-images">
            <el-image
              v-for="(image, index) in selectedViolation.evidenceImages"
              :key="index"
              :src="image"
              :preview-src-list="selectedViolation.evidenceImages"
              :initial-index="index"
              style="width: 120px; height: 120px; margin-right: 10px;"
              fit="cover"
            />
          </div>
        </div>
      </div>
      
      <template #footer>
        <el-button @click="detailDialogVisible = false">关闭</el-button>
        <el-button
          v-if="selectedViolation && !selectedViolation.read"
          type="primary"
          @click="markAsRead(selectedViolation)"
        >
          标记已读
        </el-button>
        <el-button type="primary" @click="handleViolation">处理违规</el-button>
      </template>
    </el-dialog>
    
    <!-- 证据查看弹窗 -->
    <el-dialog
      v-model="evidenceDialogVisible"
      title="违规证据"
      width="800px"
    >
      <div v-if="evidenceViolation" class="evidence-viewer">
        <div class="evidence-info">
          <h4>{{ evidenceViolation.type }}</h4>
          <p>{{ evidenceViolation.description }}</p>
          <p class="evidence-time">{{ formatTime(evidenceViolation.time) }}</p>
        </div>
        
        <div class="evidence-gallery">
          <el-carousel height="400px" indicator-position="outside">
            <el-carousel-item
              v-for="(image, index) in evidenceViolation.evidenceImages"
              :key="index"
            >
              <img :src="image" alt="证据图片" class="evidence-image" />
            </el-carousel-item>
          </el-carousel>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, onUnmounted } from 'vue'
import { ElMessage } from 'element-plus'
import { useWarningStore } from '@/store/warning'

const warningStore = useWarningStore()

// 筛选条件
const selectedArea = ref('all')
const timeRange = ref('all')
const selectedCamera = ref(1)

// 监控摄像头数据
const cameras = ref([
  {
    id: 1,
    name: '科目二倒车入库1号',
    status: 'online',
    feedUrl: 'https://picsum.photos/400/300?random=1',
    violation: null
  },
  {
    id: 2,
    name: '科目二倒车入库2号',
    status: 'online',
    feedUrl: 'https://picsum.photos/400/300?random=2',
    violation: {
      type: '越界行驶',
      level: 'high'
    }
  },
  {
    id: 3,
    name: '科目三路线1号',
    status: 'online',
    feedUrl: 'https://picsum.photos/400/300?random=3',
    violation: null
  },
  {
    id: 4,
    name: '科目三路线2号',
    status: 'offline',
    feedUrl: '',
    violation: null
  }
])

// 违规记录
const violations = ref([
  {
    id: 1,
    time: new Date(Date.now() - 1000 * 60 * 2),
    level: 'high',
    type: '未授权人员闯入',
    location: '科目二倒车入库区域',
    personName: '陌生人员',
    coachName: '-',
    vehiclePlate: '-',
    read: false,
    evidenceImages: [
      'https://picsum.photos/300/200?random=10',
      'https://picsum.photos/300/200?random=11'
    ],
    description: '检测到未在白名单中的人员进入训练区域'
  },
  {
    id: 2,
    time: new Date(Date.now() - 1000 * 60 * 15),
    level: 'medium',
    type: '车辆超速行驶',
    location: '科目三路线1',
    personName: '张三',
    coachName: '李教练',
    vehiclePlate: '粤A12345',
    read: true,
    evidenceImages: [
      'https://picsum.photos/300/200?random=12'
    ],
    description: '车辆在训练路线上超速20%'
  },
  {
    id: 3,
    time: new Date(Date.now() - 1000 * 60 * 30),
    level: 'low',
    type: '违规停车',
    location: '科目二训练场',
    personName: '李四',
    coachName: '王教练',
    vehiclePlate: '粤B67890',
    read: true,
    evidenceImages: [
      'https://picsum.photos/300/200?random=13',
      'https://picsum.photos/300/200?random=14'
    ],
    description: '车辆在非指定区域停车'
  }
])

// 弹窗控制
const detailDialogVisible = ref(false)
const evidenceDialogVisible = ref(false)
const selectedViolation = ref(null)
const evidenceViolation = ref(null)

// 方法定义
const selectCamera = (cameraId) => {
  selectedCamera.value = cameraId
  ElMessage.info(`切换到摄像头: ${cameras.value.find(c => c.id === cameraId)?.name}`)
}

const refreshViolations = () => {
  // 模拟刷新数据
  ElMessage.success('违规记录已刷新')
}

const viewViolationDetail = (violation) => {
  selectedViolation.value = violation
  detailDialogVisible.value = true
}

const viewEvidence = (violation) => {
  evidenceViolation.value = violation
  evidenceDialogVisible.value = true
}

const markAsRead = (violation) => {
  violation.read = true
  warningStore.markAsRead(violation.id)
  ElMessage.success('已标记为已读')
  
  if (detailDialogVisible.value) {
    detailDialogVisible.value = false
  }
}

const handleViolation = () => {
  if (selectedViolation.value) {
    // 触发预警处理
    warningStore.addWarning({
      type: '违规处理',
      title: selectedViolation.value.type,
      content: `${selectedViolation.value.personName} 在 ${selectedViolation.value.location} 发生 ${selectedViolation.value.type}`,
      level: selectedViolation.value.level
    })
    
    ElMessage.success('违规已处理并生成预警')
    detailDialogVisible.value = false
  }
}

const getViolationLevelType = (level) => {
  const typeMap = {
    'high': 'danger',
    'medium': 'warning',
    'low': 'info'
  }
  return typeMap[level] || 'info'
}

const formatTime = (time) => {
  return new Date(time).toLocaleString('zh-CN', {
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit',
    second: '2-digit'
  })
}

// 模拟实时违规检测
let violationInterval = null

const simulateRealTimeViolations = () => {
  violationInterval = setInterval(() => {
    // 10%概率产生新的违规
    if (Math.random() > 0.9) {
      const newViolation = {
        id: Date.now(),
        time: new Date(),
        level: ['high', 'medium', 'low'][Math.floor(Math.random() * 3)],
        type: ['越界行驶', '超速行驶', '违规停车', '未授权闯入'][Math.floor(Math.random() * 4)],
        location: ['科目二倒车入库', '科目三路线1', '科目三路线2'][Math.floor(Math.random() * 3)],
        personName: ['张三', '李四', '王五', '陌生人员'][Math.floor(Math.random() * 4)],
        coachName: Math.random() > 0.3 ? ['李教练', '王教练', '张教练'][Math.floor(Math.random() * 3)] : '-',
        vehiclePlate: Math.random() > 0.5 ? `粤${String.fromCharCode(65 + Math.floor(Math.random() * 26))}${Math.floor(Math.random() * 100000)}` : '-',
        read: false,
        evidenceImages: [
          `https://picsum.photos/300/200?random=${Date.now()}`
        ],
        description: '系统自动检测到违规行为'
      }
      
      violations.value.unshift(newViolation)
      
      // 限制列表长度
      if (violations.value.length > 50) {
        violations.value.pop()
      }
      
      // 显示通知
      ElNotification({
        title: '新违规警报',
        message: `${newViolation.type} - ${newViolation.location}`,
        type: 'warning',
        duration: 5000
      })
    }
  }, 5000)
}

onMounted(() => {
  simulateRealTimeViolations()
})

onUnmounted(() => {
  if (violationInterval) {
    clearInterval(violationInterval)
  }
})
</script>

<style scoped>
.monitoring-system {
  padding: 0;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.monitor-controls {
  display: flex;
  align-items: center;
}

.monitor-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 20px;
}

.camera-item {
  border: 2px solid #eee;
  border-radius: 8px;
  overflow: hidden;
  cursor: pointer;
  transition: all 0.2s;
}

.camera-item:hover {
  border-color: #409eff;
}

.camera-item.active {
  border-color: #409eff;
  box-shadow: 0 0 10px rgba(64, 158, 255, 0.3);
}

.camera-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 10px;
  background-color: #f5f7fa;
}

.camera-name {
  font-weight: 500;
  color: #333;
}

.camera-feed {
  position: relative;
  height: 200px;
  background-color: #000;
}

.feed-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.offline-placeholder {
  height: 100%;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  color: #999;
}

.offline-placeholder .el-icon {
  margin-bottom: 10px;
}

.violation-indicator {
  position: absolute;
  top: 10px;
  right: 10px;
  padding: 5px 10px;
  border-radius: 15px;
  display: flex;
  align-items: center;
  gap: 5px;
  color: white;
  font-size: 12px;
  font-weight: 500;
}

.violation-indicator.high {
  background-color: #f56c6c;
}

.violation-indicator.medium {
  background-color: #e6a23c;
}

.violation-indicator.low {
  background-color: #909399;
}

.violations-list {
  max-height: 600px;
  overflow-y: auto;
}

.violation-item {
  padding: 15px;
  border: 1px solid #eee;
  border-radius: 8px;
  margin-bottom: 15px;
  cursor: pointer;
  transition: all 0.2s;
}

.violation-item:hover {
  border-color: #409eff;
  box-shadow: 0 2px 8px rgba(64, 158, 255, 0.2);
}

.violation-item.unread {
  border-left: 4px solid #409eff;
  background-color: #f0f8ff;
}

.violation-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
}

.violation-time {
  font-size: 12px;
  color: #999;
}

.violation-content {
  margin-bottom: 15px;
}

.violation-type {
  font-weight: 500;
  color: #333;
  margin-bottom: 5px;
}

.violation-location,
.violation-person {
  font-size: 13px;
  color: #666;
  margin-bottom: 3px;
}

.violation-actions {
  display: flex;
  gap: 10px;
}

.no-violations {
  text-align: center;
  padding: 40px 0;
  color: #999;
}

.no-violations .el-icon {
  margin-bottom: 15px;
  color: #67c23a;
}

.evidence-section h4 {
  margin: 0 0 15px 0;
  color: #333;
}

.evidence-images {
  display: flex;
  flex-wrap: wrap;
}

.evidence-viewer .evidence-info {
  margin-bottom: 20px;
}

.evidence-viewer .evidence-info h4 {
  margin: 0 0 10px 0;
  color: #333;
}

.evidence-viewer .evidence-info p {
  margin: 5px 0;
  color: #666;
}

.evidence-time {
  font-size: 12px;
  color: #999;
}

.evidence-gallery {
  text-align: center;
}

.evidence-image {
  width: 100%;
  height: 400px;
  object-fit: cover;
}
</style>