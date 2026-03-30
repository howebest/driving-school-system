<template>
  <div class="logo-upload-container">
    <div class="logo-display" @click="triggerUpload">
      <img 
        v-if="logoUrl" 
        :src="logoUrl" 
        :alt="systemName"
        class="logo-image"
      />
      <div v-else class="logo-placeholder">
        <el-icon size="24"><Picture /></el-icon>
        <span>上传Logo</span>
      </div>
    </div>
    
    <input
      ref="fileInput"
      type="file"
      accept="image/*"
      class="file-input"
      @change="handleFileChange"
    />
    
    <div class="logo-info">
      <h3>{{ systemName }}</h3>
      <p>{{ systemSubtitle }}</p>
    </div>
    
    <div class="logo-actions" v-if="logoUrl">
      <el-button 
        type="danger" 
        size="small" 
        @click="removeLogo"
        link
      >
        <el-icon><Delete /></el-icon>移除Logo
      </el-button>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Picture, Delete } from '@element-plus/icons-vue'

const props = defineProps({
  systemName: {
    type: String,
    default: '驾校监管系统'
  },
  systemSubtitle: {
    type: String,
    default: 'Driving School Management Platform'
  }
})

const emit = defineEmits(['logo-updated'])

const logoUrl = ref('')
const fileInput = ref()

// 从本地存储加载Logo
onMounted(() => {
  const savedLogo = localStorage.getItem('system-logo')
  if (savedLogo) {
    logoUrl.value = savedLogo
  }
})

const triggerUpload = () => {
  fileInput.value?.click()
}

const handleFileChange = (event) => {
  const file = event.target.files[0]
  if (!file) return

  // 验证文件类型
  if (!file.type.startsWith('image/')) {
    ElMessage.error('请选择图片文件')
    return
  }

  // 验证文件大小 (2MB)
  if (file.size > 2 * 1024 * 1024) {
    ElMessage.error('图片大小不能超过2MB')
    return
  }

  // 读取文件并预览
  const reader = new FileReader()
  reader.onload = (e) => {
    const result = e.target.result
    logoUrl.value = result
    localStorage.setItem('system-logo', result)
    emit('logo-updated', result)
    ElMessage.success('Logo上传成功')
  }
  reader.readAsDataURL(file)
}

const removeLogo = () => {
  logoUrl.value = ''
  localStorage.removeItem('system-logo')
  emit('logo-updated', '')
  ElMessage.success('Logo已移除')
}
</script>

<style scoped>
.logo-upload-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 16px;
}

.logo-display {
  width: 80px;
  height: 80px;
  border-radius: 16px;
  background: linear-gradient(135deg, #f8fafc, #e2e8f0);
  border: 2px dashed #cbd5e1;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all 0.3s ease;
  position: relative;
  overflow: hidden;
}

.logo-display:hover {
  border-color: #3b82f6;
  background: linear-gradient(135deg, #eff6ff, #dbeafe);
  transform: scale(1.05);
}

.logo-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.logo-placeholder {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
  color: #94a3b8;
  font-size: 12px;
}

.logo-placeholder :deep(.el-icon) {
  font-size: 24px;
}

.logo-info {
  text-align: center;
}

.logo-info h3 {
  margin: 0 0 4px 0;
  font-size: 20px;
  font-weight: 700;
  color: #1e293b;
}

.logo-info p {
  margin: 0;
  font-size: 13px;
  color: #64748b;
}

.logo-actions {
  margin-top: 8px;
}

.file-input {
  display: none;
}
</style>