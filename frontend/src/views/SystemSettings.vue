<template>
  <div class="system-settings">
    <div class="settings-header">
      <h1>系统设置</h1>
      <p>管理和配置系统基本信息</p>
    </div>

    <div class="settings-content">
      <el-row :gutter="24">
        <el-col :span="16">
          <el-card class="settings-card">
            <template #header>
              <h3>基本设置</h3>
            </template>
            
            <el-form :model="systemSettings" label-width="120px">
              <el-form-item label="系统名称">
                <el-input v-model="systemSettings.name" placeholder="请输入系统名称" />
              </el-form-item>
              
              <el-form-item label="系统副标题">
                <el-input v-model="systemSettings.subtitle" placeholder="请输入系统副标题" />
              </el-form-item>
              
              <el-form-item label="Logo上传">
                <LogoUploader 
                  :system-name="systemSettings.name"
                  :system-subtitle="systemSettings.subtitle"
                  @logo-updated="handleLogoUpdated"
                />
              </el-form-item>
              
              <el-form-item label="系统描述">
                <el-input 
                  v-model="systemSettings.description" 
                  type="textarea" 
                  :rows="4"
                  placeholder="请输入系统描述"
                />
              </el-form-item>
              
              <el-form-item>
                <el-button type="primary" @click="saveSettings">保存设置</el-button>
                <el-button @click="resetSettings">重置</el-button>
              </el-form-item>
            </el-form>
          </el-card>
        </el-col>
        
        <el-col :span="8">
          <el-card class="preview-card">
            <template #header>
              <h3>预览效果</h3>
            </template>
            
            <div class="preview-content">
              <div class="top-nav-preview">
                <div class="nav-brand-preview">
                  <img 
                    v-if="previewLogo" 
                    :src="previewLogo" 
                    alt="Logo"
                    class="preview-logo"
                  />
                  <div v-else class="logo-placeholder-preview">LOGO</div>
                  <div class="brand-text-preview">
                    <div class="main-name">{{ systemSettings.name }}</div>
                    <div class="subtitle">{{ systemSettings.subtitle }}</div>
                  </div>
                </div>
              </div>
              
              <div class="info-preview">
                <h4>当前配置信息</h4>
                <div class="info-item">
                  <span class="label">系统名称:</span>
                  <span class="value">{{ systemSettings.name }}</span>
                </div>
                <div class="info-item">
                  <span class="label">副标题:</span>
                  <span class="value">{{ systemSettings.subtitle }}</span>
                </div>
                <div class="info-item">
                  <span class="label">Logo状态:</span>
                  <span class="value" :class="previewLogo ? 'success' : 'warning'">
                    {{ previewLogo ? '已上传' : '未上传' }}
                  </span>
                </div>
              </div>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import LogoUploader from '@/components/LogoUploader.vue'

const systemSettings = reactive({
  name: '驾校监管系统',
  subtitle: 'Driving School Management Platform',
  description: '专业的驾校场地智能监管平台'
})

const previewLogo = ref('')

// 加载保存的设置
onMounted(() => {
  const savedSettings = localStorage.getItem('system-settings')
  const savedLogo = localStorage.getItem('system-logo')
  
  if (savedSettings) {
    Object.assign(systemSettings, JSON.parse(savedSettings))
  }
  
  if (savedLogo) {
    previewLogo.value = savedLogo
  }
})

const handleLogoUpdated = (logoUrl) => {
  previewLogo.value = logoUrl
}

const saveSettings = () => {
  localStorage.setItem('system-settings', JSON.stringify(systemSettings))
  ElMessage.success('设置保存成功')
}

const resetSettings = () => {
  systemSettings.name = '驾校监管系统'
  systemSettings.subtitle = 'Driving School Management Platform'
  systemSettings.description = '专业的驾校场地智能监管平台'
  previewLogo.value = ''
  localStorage.removeItem('system-settings')
  localStorage.removeItem('system-logo')
  ElMessage.success('设置已重置')
}
</script>

<style scoped>
.system-settings {
  padding: 24px;
}

.settings-header {
  margin-bottom: 24px;
}

.settings-header h1 {
  margin: 0 0 8px 0;
  font-size: 24px;
  font-weight: 700;
  color: #1e293b;
}

.settings-header p {
  margin: 0;
  color: #64748b;
  font-size: 15px;
}

.settings-card {
  border-radius: 16px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
  border: 1px solid #e2e8f0;
}

.settings-card :deep(.el-card__header) {
  border-bottom: 1px solid #e2e8f0;
  padding: 20px 24px;
}

.settings-card :deep(.el-card__body) {
  padding: 24px;
}

.settings-card h3 {
  margin: 0;
  font-size: 18px;
  font-weight: 600;
  color: #1e293b;
}

.preview-card {
  border-radius: 16px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
  border: 1px solid #e2e8f0;
  position: sticky;
  top: 24px;
}

.preview-card :deep(.el-card__header) {
  border-bottom: 1px solid #e2e8f0;
  padding: 20px 24px;
}

.preview-card :deep(.el-card__body) {
  padding: 24px;
}

.preview-card h3 {
  margin: 0;
  font-size: 18px;
  font-weight: 600;
  color: #1e293b;
}

.top-nav-preview {
  background: linear-gradient(90deg, #1e293b 0%, #334155 100%);
  border-radius: 12px;
  padding: 16px;
  margin-bottom: 24px;
}

.nav-brand-preview {
  display: flex;
  align-items: center;
  gap: 12px;
}

.preview-logo {
  width: 48px;
  height: 48px;
  border-radius: 12px;
  object-fit: cover;
}

.logo-placeholder-preview {
  width: 48px;
  height: 48px;
  background: linear-gradient(135deg, #3b82f6, #60a5fa);
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 10px;
  font-weight: 600;
}

.brand-text-preview {
  color: white;
}

.main-name {
  font-size: 16px;
  font-weight: 600;
  margin-bottom: 2px;
}

.subtitle {
  font-size: 12px;
  opacity: 0.8;
}

.info-preview h4 {
  margin: 0 0 16px 0;
  font-size: 16px;
  font-weight: 600;
  color: #1e293b;
}

.info-item {
  display: flex;
  justify-content: space-between;
  padding: 8px 0;
  border-bottom: 1px solid #f1f5f9;
}

.info-item:last-child {
  border-bottom: none;
}

.label {
  font-weight: 500;
  color: #64748b;
}

.value {
  font-weight: 500;
  color: #1e293b;
}

.value.success {
  color: #10b981;
}

.value.warning {
  color: #f59e0b;
}
</style>