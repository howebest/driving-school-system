<template>
  <div class="plate-verification">
    <el-row :gutter="20">
      <el-col :span="14">
        <el-card class="camera-card">
          <template #header>
            <span>车牌识别</span>
          </template>
          
          <div class="camera-container">
            <video ref="videoRef" autoplay playsinline class="camera-video"></video>
            <div class="placeholder" v-if="!cameraActive">
              <el-icon size="64"><VideoCamera /></el-icon>
              <p>摄像头未启动</p>
              <el-button type="primary" @click="startCamera">启动摄像头</el-button>
            </div>
          </div>
          
          <div class="controls">
            <el-button type="primary" @click="recognizePlate">识别车牌</el-button>
          </div>
        </el-card>
      </el-col>
      
      <el-col :span="10">
        <el-card>
          <template #header>
            <span>识别结果</span>
          </template>
          
          <div v-if="!recognitionResult" class="no-result">
            <el-icon size="48"><CreditCard /></el-icon>
            <p>等待识别结果</p>
          </div>
          
          <div v-else class="result">
            <div v-if="recognitionResult.success" class="success">
              <el-icon size="32" color="#67c23a"><SuccessFilled /></el-icon>
              <h3>车牌识别成功</h3>
              <p>车牌号: {{ recognitionResult.plate }}</p>
              <p>车辆状态: {{ recognitionResult.status }}</p>
            </div>
            <div v-else class="fail">
              <el-icon size="32" color="#f56c6c"><CircleCloseFilled /></el-icon>
              <h3>车牌识别失败</h3>
              <p>{{ recognitionResult.message }}</p>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { ElMessage } from 'element-plus'

const videoRef = ref()
const cameraActive = ref(false)
const recognitionResult = ref(null)

const startCamera = async () => {
  try {
    const stream = await navigator.mediaDevices.getUserMedia({ video: true })
    if (videoRef.value) {
      videoRef.value.srcObject = stream
      cameraActive.value = true
      ElMessage.success('摄像头启动成功')
    }
  } catch (error) {
    ElMessage.error('无法访问摄像头')
  }
}

const recognizePlate = async () => {
  // 模拟车牌识别
  await new Promise(resolve => setTimeout(resolve, 2000))
  
  recognitionResult.value = {
    success: Math.random() > 0.3,
    plate: '粤A12345',
    status: '正常',
    message: '未找到匹配车辆信息'
  }
  
  ElMessage.success('识别完成')
}
</script>

<style scoped>
.camera-container {
  position: relative;
  background: #000;
  border-radius: 8px;
  overflow: hidden;
  margin-bottom: 20px;
}

.camera-video {
  width: 100%;
  height: 400px;
  object-fit: cover;
}

.placeholder {
  height: 400px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  color: #999;
}

.no-result {
  text-align: center;
  padding: 60px 0;
  color: #999;
}

.result {
  min-height: 300px;
  display: flex;
  align-items: center;
  justify-content: center;
  text-align: center;
}

.success h3,
.fail h3 {
  margin: 15px 0;
}
</style>