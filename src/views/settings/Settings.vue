<template>
  <div class="settings-page">
    <el-row :gutter="20">
      <el-col :span="6">
        <el-card>
          <el-menu
            :default-active="activeMenu"
            class="settings-menu"
            @select="handleMenuSelect"
          >
            <el-menu-item index="basic">基本设置</el-menu-item>
            <el-menu-item index="notification">通知设置</el-menu-item>
            <el-menu-item index="ai">AI参数设置</el-menu-item>
            <el-menu-item index="backup">数据备份</el-menu-item>
          </el-menu>
        </el-card>
      </el-col>
      
      <el-col :span="18">
        <el-card>
          <template #header>
            <span>{{ menuTitles[activeMenu] }}</span>
          </template>
          
          <div v-if="activeMenu === 'basic'" class="settings-content">
            <el-form label-width="120px">
              <el-form-item label="系统名称">
                <el-input v-model="settings.systemName" />
              </el-form-item>
              <el-form-item label="默认主题">
                <el-select v-model="settings.theme">
                  <el-option label="默认主题" value="default" />
                  <el-option label="深色主题" value="dark" />
                </el-select>
              </el-form-item>
              <el-form-item>
                <el-button type="primary" @click="saveSettings">保存设置</el-button>
              </el-form-item>
            </el-form>
          </div>
          
          <div v-if="activeMenu === 'notification'" class="settings-content">
            <el-form label-width="120px">
              <el-form-item label="预警推送">
                <el-switch v-model="settings.pushEnabled" />
              </el-form-item>
              <el-form-item label="推送方式">
                <el-checkbox-group v-model="settings.pushMethods">
                  <el-checkbox label="sms">短信</el-checkbox>
                  <el-checkbox label="email">邮件</el-checkbox>
                  <el-checkbox label="app">APP推送</el-checkbox>
                </el-checkbox-group>
              </el-form-item>
              <el-form-item>
                <el-button type="primary" @click="saveSettings">保存设置</el-button>
              </el-form-item>
            </el-form>
          </div>
          
          <div v-if="activeMenu === 'ai'" class="settings-content">
            <el-form label-width="150px">
              <el-form-item label="识别精度">
                <el-slider v-model="settings.aiAccuracy" :min="70" :max="100" />
              </el-form-item>
              <el-form-item label="预警延迟(秒)">
                <el-input-number v-model="settings.warningDelay" :min="0" :max="30" />
              </el-form-item>
              <el-form-item>
                <el-button type="primary" @click="saveSettings">保存设置</el-button>
              </el-form-item>
            </el-form>
          </div>
          
          <div v-if="activeMenu === 'backup'" class="settings-content">
            <el-button type="primary" @click="backupData">立即备份</el-button>
            <p style="margin-top: 20px; color: #666;">上次备份: {{ lastBackup }}</p>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { ElMessage } from 'element-plus'

const activeMenu = ref('basic')
const lastBackup = ref('2024-01-15 14:30:25')

const menuTitles = {
  'basic': '基本设置',
  'notification': '通知设置',
  'ai': 'AI参数设置',
  'backup': '数据备份'
}

const settings = reactive({
  systemName: '驾校场地监管系统',
  theme: 'default',
  pushEnabled: true,
  pushMethods: ['app'],
  aiAccuracy: 90,
  warningDelay: 5
})

const handleMenuSelect = (index) => {
  activeMenu.value = index
}

const saveSettings = () => {
  ElMessage.success('设置保存成功')
}

const backupData = () => {
  ElMessage.success('数据备份已完成')
  lastBackup.value = new Date().toLocaleString('zh-CN')
}
</script>

<style scoped>
.settings-menu {
  border: none;
}

.settings-content {
  min-height: 300px;
}
</style>