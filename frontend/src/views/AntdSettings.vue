<template>
  <div class="system-settings">
    <!-- 页面标题 -->
    <a-page-header
      title="系统设置"
      sub-title="管理系统配置和参数"
      style="padding: 0 0 16px 0"
    />

    <a-row :gutter="16">
      <a-col :xs="24" :lg="16">
        <!-- 基础设置 -->
        <a-card title="基础设置" style="margin-bottom: 16px">
          <a-form :label-col="{ span: 6 }" :wrapper-col="{ span: 14 }">
            <a-form-item label="系统名称">
              <a-input v-model:value="settings.systemName" />
            </a-form-item>
            <a-form-item label="系统Logo">
              <a-upload
                list-type="picture-card"
                :max-count="1"
                :show-upload-list="false"
              >
                <div v-if="settings.logoUrl">
                  <img :src="settings.logoUrl" alt="logo" style="width: 100%" />
                </div>
                <div v-else>
                  <PlusOutlined />
                  <div style="margin-top: 8px">上传Logo</div>
                </div>
              </a-upload>
            </a-form-item>
            <a-form-item label="管理员邮箱">
              <a-input v-model:value="settings.adminEmail" />
            </a-form-item>
            <a-form-item label="联系电话">
              <a-input v-model:value="settings.contactPhone" />
            </a-form-item>
            <a-form-item :wrapper-col="{ offset: 6, span: 14 }">
              <a-button type="primary" @click="saveBasicSettings">保存设置</a-button>
            </a-form-item>
          </a-form>
        </a-card>

        <!-- 预警设置 -->
        <a-card title="预警设置" style="margin-bottom: 16px">
          <a-form :label-col="{ span: 6 }" :wrapper-col="{ span: 14 }">
            <a-form-item label="启用短信通知">
              <a-switch v-model:checked="settings.smsEnabled" />
            </a-form-item>
            <a-form-item label="启用邮件通知">
              <a-switch v-model:checked="settings.emailEnabled" />
            </a-form-item>
            <a-form-item label="预警级别阈值">
              <a-slider
                v-model:value="settings.alertThreshold"
                :marks="{ 0: '低', 50: '中', 100: '高' }"
              />
            </a-form-item>
            <a-form-item label="通知接收人">
              <a-select
                v-model:value="settings.notifyUsers"
                mode="multiple"
                placeholder="选择接收通知的用户"
              >
                <a-select-option value="admin">系统管理员</a-select-option>
                <a-select-option value="operator">运营人员</a-select-option>
                <a-select-option value="coach">教练</a-select-option>
              </a-select>
            </a-form-item>
            <a-form-item :wrapper-col="{ offset: 6, span: 14 }">
              <a-button type="primary" @click="saveAlertSettings">保存设置</a-button>
            </a-form-item>
          </a-form>
        </a-card>

        <!-- AI识别设置 -->
        <a-card title="AI识别设置" style="margin-bottom: 16px">
          <a-form :label-col="{ span: 6 }" :wrapper-col="{ span: 14 }">
            <a-form-item label="AI识别引擎">
              <a-select v-model:value="settings.aiEngine">
                <a-select-option value="baidu">百度AI</a-select-option>
                <a-select-option value="aliyun">阿里云AI</a-select-option>
                <a-select-option value="local">本地模型</a-select-option>
              </a-select>
            </a-form-item>
            <a-form-item label="识别置信度阈值">
              <a-input-number
                v-model:value="settings.confidenceThreshold"
                :min="0"
                :max="100"
                :formatter="value => `${value}%`"
                :parser="value => value.replace('%', '')"
              />
            </a-form-item>
            <a-form-item label="人脸识别开关">
              <a-switch v-model:checked="settings.faceRecognition" />
            </a-form-item>
            <a-form-item label="车牌识别开关">
              <a-switch v-model:checked="settings.plateRecognition" />
            </a-form-item>
            <a-form-item :wrapper-col="{ offset: 6, span: 14 }">
              <a-button type="primary" @click="saveAISettings">保存设置</a-button>
            </a-form-item>
          </a-form>
        </a-card>
      </a-col>

      <a-col :xs="24" :lg="8">
        <!-- 系统信息 -->
        <a-card title="系统信息" style="margin-bottom: 16px">
          <a-descriptions :column="1" size="small">
            <a-descriptions-item label="版本号">v1.0.0</a-descriptions-item>
            <a-descriptions-item label="数据库">MySQL 8.0</a-descriptions-item>
            <a-descriptions-item label="缓存">Redis 6.0</a-descriptions-item>
            <a-descriptions-item label="前端框架">Vue 3.2</a-descriptions-item>
            <a-descriptions-item label="UI组件">Ant Design Vue 3.x</a-descriptions-item>
            <a-descriptions-item label="后端框架">Spring Boot 2.7</a-descriptions-item>
          </a-descriptions>
        </a-card>

        <!-- 快捷操作 -->
        <a-card title="快捷操作" style="margin-bottom: 16px">
          <a-space direction="vertical" style="width: 100%">
            <a-button block @click="clearCache">
              <template #icon><DeleteOutlined /></template>
              清除缓存
            </a-button>
            <a-button block @click="exportData">
              <template #icon><DownloadOutlined /></template>
              导出数据
            </a-button>
            <a-button block @click="importData">
              <template #icon><UploadOutlined /></template>
              导入数据
            </a-button>
            <a-button block danger @click="resetSystem">
              <template #icon><ReloadOutlined /></template>
              重置系统
            </a-button>
          </a-space>
        </a-card>

        <!-- 操作日志 -->
        <a-card title="最近操作">
          <a-timeline>
            <a-timeline-item v-for="log in recentLogs" :key="log.id">
              <p><strong>{{ log.time }}</strong></p>
              <p>{{ log.action }}</p>
              <p style="color: #8c8c8c">{{ log.user }}</p>
            </a-timeline-item>
          </a-timeline>
        </a-card>
      </a-col>
    </a-row>
  </div>
</template>

<script setup>
import { reactive, ref } from 'vue'
import { message, Modal } from 'ant-design-vue'
import {
  PlusOutlined,
  DeleteOutlined,
  DownloadOutlined,
  UploadOutlined,
  ReloadOutlined
} from '@ant-design/icons-vue'

const settings = reactive({
  systemName: '驾校场地智能监管系统',
  logoUrl: '',
  adminEmail: 'admin@driving-school.com',
  contactPhone: '400-123-4567',
  smsEnabled: true,
  emailEnabled: true,
  alertThreshold: 50,
  notifyUsers: ['admin', 'operator'],
  aiEngine: 'baidu',
  confidenceThreshold: 80,
  faceRecognition: true,
  plateRecognition: true
})

const recentLogs = ref([
  { id: 1, time: '2024-03-25 14:30', action: '修改系统设置', user: '管理员' },
  { id: 2, time: '2024-03-25 12:15', action: '导出学员数据', user: '管理员' },
  { id: 3, time: '2024-03-24 16:45', action: '添加新教练', user: '管理员' },
  { id: 4, time: '2024-03-24 10:20', action: '处理违规记录', user: '操作员' }
])

const saveBasicSettings = () => {
  message.success('基础设置保存成功')
}

const saveAlertSettings = () => {
  message.success('预警设置保存成功')
}

const saveAISettings = () => {
  message.success('AI识别设置保存成功')
}

const clearCache = () => {
  Modal.confirm({
    title: '确认清除缓存？',
    content: '清除缓存可能会影响系统性能',
    onOk() {
      message.loading('正在清除缓存...', 2).then(() => {
        message.success('缓存清除成功')
      })
    }
  })
}

const exportData = () => {
  message.loading('正在导出数据...', 2).then(() => {
    message.success('数据导出成功')
  })
}

const importData = () => {
  message.info('请选择要导入的数据文件')
}

const resetSystem = () => {
  Modal.confirm({
    title: '确认重置系统？',
    content: '重置系统将恢复所有设置为默认值，此操作不可恢复！',
    okText: '确认重置',
    okType: 'danger',
    cancelText: '取消',
    onOk() {
      message.success('系统已重置')
    }
  })
}
</script>

<style scoped>
.system-settings {
  padding: 0;
}
</style>
