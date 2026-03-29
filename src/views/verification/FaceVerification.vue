<template>
  <div class="face-verification">
    <el-row :gutter="20">
      <!-- 左侧摄像头区域 -->
      <el-col :span="14">
        <el-card class="camera-card">
          <template #header>
            <div class="card-header">
              <span>实时人脸采集</span>
              <el-tag :type="cameraStatus.type">{{ cameraStatus.text }}</el-tag>
            </div>
          </template>
          
          <div class="camera-container">
            <video
              ref="videoRef"
              autoplay
              playsinline
              v-show="cameraActive"
              class="camera-video"
            ></video>
            
            <div v-if="!cameraActive" class="camera-placeholder">
              <el-icon size="64"><VideoCamera /></el-icon>
              <p>摄像头未启动</p>
              <el-button type="primary" @click="startCamera">启动摄像头</el-button>
            </div>
            
            <div v-if="isVerifying" class="verifying-overlay">
              <div class="verifying-spinner">
                <el-icon class="is-loading"><Loading /></el-icon>
                <p>正在识别中...</p>
              </div>
            </div>
          </div>
          
          <div class="camera-controls">
            <el-button
              v-if="cameraActive"
              type="danger"
              @click="stopCamera"
            >
              <el-icon><VideoPause /></el-icon>
              停止摄像头
            </el-button>
            
            <el-button
              v-if="cameraActive && !isVerifying"
              type="primary"
              @click="captureAndVerify"
            >
              <el-icon><Camera /></el-icon>
              开始识别
            </el-button>
            
            <el-button
              v-if="cameraActive && isVerifying"
              @click="cancelVerification"
            >
              <el-icon><Close /></el-icon>
              取消识别
            </el-button>
          </div>
        </el-card>
      </el-col>
      
      <!-- 右侧识别结果区域 -->
      <el-col :span="10">
        <el-card class="result-card">
          <template #header>
            <span>识别结果</span>
          </template>
          
          <div v-if="!verificationResult" class="no-result">
            <el-icon size="48"><User /></el-icon>
            <p>等待识别结果</p>
          </div>
          
          <div v-else class="result-content">
            <!-- 识别成功 -->
            <div v-if="verificationResult.success" class="success-result">
              <div class="result-icon success">
                <el-icon size="32"><SuccessFilled /></el-icon>
              </div>
              <h3>身份核验通过</h3>
              <div class="student-info">
                <el-descriptions :column="1" size="small" border>
                  <el-descriptions-item label="姓名">{{ verificationResult.student.name }}</el-descriptions-item>
                  <el-descriptions-item label="身份证号">{{ verificationResult.student.idCard }}</el-descriptions-item>
                  <el-descriptions-item label="状态">
                    <el-tag type="success">{{ verificationResult.student.status }}</el-tag>
                  </el-descriptions-item>
                  <el-descriptions-item label="所属教练">{{ verificationResult.student.coachName }}</el-descriptions-item>
                </el-descriptions>
              </div>
              
              <div class="access-control">
                <el-button type="success" @click="allowAccess">允许进入</el-button>
                <el-button @click="resetVerification">重新识别</el-button>
              </div>
            </div>
            
            <!-- 识别失败 -->
            <div v-else class="fail-result">
              <div class="result-icon fail">
                <el-icon size="32"><CircleCloseFilled /></el-icon>
              </div>
              <h3>身份核验失败</h3>
              <p class="fail-message">{{ verificationResult.message }}</p>
              
              <div class="fail-actions">
                <el-button type="primary" @click="retryVerification">重新识别</el-button>
                <el-button @click="resetVerification">返回</el-button>
              </div>
            </div>
          </div>
        </el-card>
        
        <!-- 历史记录 -->
        <el-card class="history-card" style="margin-top: 20px;">
          <template #header>
            <div class="card-header">
              <span>识别历史</span>
              <el-button link @click="clearHistory">清空</el-button>
            </div>
          </template>
          
          <div class="history-list">
            <div
              v-for="record in historyRecords"
              :key="record.id"
              class="history-item"
              :class="{ success: record.success, fail: !record.success }"
            >
              <div class="history-time">{{ formatTime(record.time) }}</div>
              <div class="history-name">{{ record.name || '未知人员' }}</div>
              <div class="history-status">
                <el-tag :type="record.success ? 'success' : 'danger'" size="small">
                  {{ record.success ? '成功' : '失败' }}
                </el-tag>
              </div>
            </div>
            
            <div v-if="historyRecords.length === 0" class="no-history">
              暂无识别记录
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>
    
    <!-- 允许进入确认弹窗 -->
    <el-dialog
      v-model="accessDialogVisible"
      title="入场确认"
      width="400px"
    >
      <div class="access-confirm">
        <p>确认允许以下人员进入训练场地：</p>
        <el-descriptions :column="1" size="small" border>
          <el-descriptions-item label="姓名">{{ verificationResult?.student?.name }}</el-descriptions-item>
          <el-descriptions-item label="身份证号">{{ verificationResult?.student?.idCard }}</el-descriptions-item>
          <el-descriptions-item label="入场时间">{{ formatTime(new Date()) }}</el-descriptions-item>
        </el-descriptions>
      </div>
      
      <template #footer>
        <el-button @click="accessDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="confirmAccess">确认入场</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, onUnmounted } from 'vue'
import { ElMessage } from 'element-plus'

// 摄像头相关
const videoRef = ref()
const cameraActive = ref(false)
const isVerifying = ref(false)

// 状态管理
const cameraStatus = reactive({
  type: 'info',
  text: '未启动'
})

// 识别结果
const verificationResult = ref(null)

// 历史记录
const historyRecords = ref([
  {
    id: 1,
    time: new Date(Date.now() - 1000 * 60 * 5),
    name: '张三',
    success: true
  },
  {
    id: 2,
    time: new Date(Date.now() - 1000 * 60 * 15),
    name: '未知人员',
    success: false
  }
])

// 弹窗控制
const accessDialogVisible = ref(false)

// 启动摄像头
const startCamera = async () => {
  try {
    const stream = await navigator.mediaDevices.getUserMedia({ 
      video: { 
        width: { ideal: 1280 },
        height: { ideal: 720 }
      } 
    })
    
    if (videoRef.value) {
      videoRef.value.srcObject = stream
      cameraActive.value = true
      cameraStatus.type = 'success'
      cameraStatus.text = '运行中'
      ElMessage.success('摄像头启动成功')
    }
  } catch (error) {
    ElMessage.error('无法访问摄像头，请检查权限设置')
    cameraStatus.type = 'danger'
    cameraStatus.text = '启动失败'
  }
}

// 停止摄像头
const stopCamera = () => {
  if (videoRef.value && videoRef.value.srcObject) {
    const tracks = videoRef.value.srcObject.getTracks()
    tracks.forEach(track => track.stop())
    videoRef.value.srcObject = null
  }
  cameraActive.value = false
  cameraStatus.type = 'info'
  cameraStatus.text = '已停止'
  ElMessage.info('摄像头已停止')
}

// 拍照并识别
const captureAndVerify = async () => {
  if (!cameraActive.value) return
  
  isVerifying.value = true
  
  try {
    // 模拟识别过程
    await new Promise(resolve => setTimeout(resolve, 2000))
    
    // 模拟识别结果（80%成功率）
    const isSuccess = Math.random() > 0.2
    
    if (isSuccess) {
      // 识别成功
      verificationResult.value = {
        success: true,
        student: {
          name: '张三',
          idCard: '440101199001011234',
          status: '在读学员',
          coachName: '李教练'
        }
      }
      
      // 添加到历史记录
      addToHistory('张三', true)
      ElMessage.success('身份识别成功')
    } else {
      // 识别失败
      verificationResult.value = {
        success: false,
        message: '未在白名单中找到匹配人员'
      }
      
      // 添加到历史记录
      addToHistory('未知人员', false)
      ElMessage.warning('身份识别失败')
    }
  } catch (error) {
    ElMessage.error('识别过程出现错误')
  } finally {
    isVerifying.value = false
  }
}

// 取消识别
const cancelVerification = () => {
  isVerifying.value = false
  ElMessage.info('识别已取消')
}

// 重置识别
const resetVerification = () => {
  verificationResult.value = null
}

// 重新识别
const retryVerification = () => {
  resetVerification()
  captureAndVerify()
}

// 允许进入
const allowAccess = () => {
  accessDialogVisible.value = true
}

// 确认入场
const confirmAccess = () => {
  ElMessage.success(`${verificationResult.value.student.name} 入场成功`)
  accessDialogVisible.value = false
  resetVerification()
}

// 添加到历史记录
const addToHistory = (name, success) => {
  historyRecords.value.unshift({
    id: Date.now(),
    time: new Date(),
    name: name,
    success: success
  })
  
  // 限制历史记录数量
  if (historyRecords.value.length > 20) {
    historyRecords.value.pop()
  }
}

// 清空历史记录
const clearHistory = () => {
  historyRecords.value = []
  ElMessage.success('历史记录已清空')
}

// 格式化时间
const formatTime = (time) => {
  return new Date(time).toLocaleString('zh-CN', {
    hour: '2-digit',
    minute: '2-digit',
    second: '2-digit'
  })
}

// 组件卸载时清理资源
onUnmounted(() => {
  stopCamera()
})

onMounted(() => {
  // 组件挂载时不自动启动摄像头，需要用户主动点击
})
</script>

<style scoped>
.face-verification {
  padding: 0;
}

.camera-card,
.result-card,
.history-card {
  height: 100%;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.camera-container {
  position: relative;
  background-color: #000;
  border-radius: 8px;
  overflow: hidden;
  margin-bottom: 20px;
}

.camera-video {
  width: 100%;
  height: 400px;
  object-fit: cover;
}

.camera-placeholder {
  height: 400px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  color: #999;
  background-color: #f5f7fa;
}

.camera-placeholder .el-icon {
  margin-bottom: 15px;
  color: #c0c4cc;
}

.verifying-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(0, 0, 0, 0.7);
  display: flex;
  align-items: center;
  justify-content: center;
}

.verifying-spinner {
  text-align: center;
  color: white;
}

.verifying-spinner .el-icon {
  font-size: 48px;
  margin-bottom: 15px;
}

.camera-controls {
  display: flex;
  gap: 15px;
  justify-content: center;
}

.no-result {
  text-align: center;
  padding: 60px 0;
  color: #999;
}

.no-result .el-icon {
  margin-bottom: 15px;
  color: #c0c4cc;
}

.result-content {
  min-height: 400px;
  display: flex;
  flex-direction: column;
  justify-content: center;
}

.success-result,
.fail-result {
  text-align: center;
}

.result-icon {
  width: 80px;
  height: 80px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 0 auto 20px;
  color: white;
}

.result-icon.success {
  background: linear-gradient(135deg, #67c23a 0%, #85ce61 100%);
}

.result-icon.fail {
  background: linear-gradient(135deg, #f56c6c 0%, #ff7875 100%);
}

.success-result h3 {
  color: #67c23a;
  margin-bottom: 20px;
}

.fail-result h3 {
  color: #f56c6c;
  margin-bottom: 15px;
}

.fail-message {
  color: #999;
  margin-bottom: 30px;
}

.student-info {
  margin: 25px 0;
}

.access-control,
.fail-actions {
  display: flex;
  gap: 15px;
  justify-content: center;
  margin-top: 30px;
}

.history-list {
  max-height: 300px;
  overflow-y: auto;
}

.history-item {
  display: flex;
  align-items: center;
  padding: 12px 0;
  border-bottom: 1px solid #eee;
}

.history-item:last-child {
  border-bottom: none;
}

.history-time {
  flex: 1;
  font-size: 12px;
  color: #999;
}

.history-name {
  flex: 2;
  font-size: 14px;
  color: #333;
}

.history-status {
  flex: 1;
  text-align: right;
}

.no-history {
  text-align: center;
  color: #999;
  padding: 30px 0;
}

.access-confirm p {
  margin-bottom: 20px;
  color: #666;
}
</style>