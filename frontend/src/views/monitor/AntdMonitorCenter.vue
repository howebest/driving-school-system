<template>
  <div class="monitor-center">
    <!-- 页面标题 -->
    <a-page-header
      title="违规监测中心"
      sub-title="AI智能识别违规行为，实时预警"
      style="padding: 0 0 16px 0"
    >
      <template #extra>
        <a-space>
          <a-switch
            v-model:checked="monitoring"
            checked-children="监测中"
            un-checked-children="已暂停"
            @change="toggleMonitoring"
          />
          <a-button type="primary" @click="startAI">
            <template #icon><ThunderboltOutlined /></template>
            启动AI识别
          </a-button>
        </a-space>
      </template>
    </a-page-header>

    <!-- 监控区域卡片 -->
    <a-row :gutter="16" style="margin-bottom: 16px">
      <a-col :xs="24" :lg="16">
        <a-card title="实时监控画面" :loading="loading">
          <template #extra>
            <a-space>
              <a-select v-model:value="selectedArea" style="width: 200px">
                <a-select-option value="all">全部区域</a-select-option>
                <a-select-option value="ke2-daoche">科二倒车入库</a-select-option>
                <a-select-option value="ke2-podao">科二坡道起步</a-select-option>
                <a-select-option value="ke3-route1">科三路线1</a-select-option>
                <a-select-option value="ke3-route2">科三路线2</a-select-option>
              </a-select>
              <a-button @click="captureScreen">
                <CameraOutlined />
                截图
              </a-button>
            </a-space>
          </template>

          <div class="monitor-grid">
            <div
              v-for="item in monitorList"
              :key="item.id"
              class="monitor-item"
              :class="{ 'alert-active': item.hasAlert }"
            >
              <div class="monitor-header">
                <span class="monitor-title">{{ item.name }}</span>
                <a-badge :status="item.status === '在线' ? 'success' : 'error'" :text="item.status" />
              </div>
              <div class="monitor-video">
                <img v-if="item.thumbnail" :src="item.thumbnail" :alt="item.name" />
                <div v-else class="monitor-placeholder">
                  <VideoCameraOutlined style="font-size: 32px" />
                  <p>视频流加载中...</p>
                </div>
                <div v-if="item.hasAlert" class="alert-overlay">
                  <ExclamationCircleFilled style="font-size: 48px" />
                  <p>检测到违规行为</p>
                </div>
              </div>
              <div class="monitor-footer">
                <a-space>
                  <a-tag v-if="item.currentPerson" color="blue">
                    {{ item.currentPerson }} ({{ item.personRole }})
                  </a-tag>
                  <a-tag v-else color="default">空闲</a-tag>
                </a-space>
              </div>
            </div>
          </div>
        </a-card>
      </a-col>

      <a-col :xs="24" :lg="8">
        <!-- 今日统计 -->
        <a-card title="今日统计" style="margin-bottom: 16px">
          <a-row :gutter="16">
            <a-col :span="12">
              <a-statistic
                title="违规总数"
                :value="todayViolations"
                :value-style="{ color: '#cf1322' }"
              >
                <template #prefix>
                  <AlertOutlined />
                </template>
              </a-statistic>
            </a-col>
            <a-col :span="12">
              <a-statistic
                title="未处理"
                :value="unprocessedCount"
                :value-style="{ color: '#fa8c16' }"
              >
                <template #prefix>
                  <ClockCircleOutlined />
                </template>
              </a-statistic>
            </a-col>
          </a-row>
        </a-card>

        <!-- AI识别统计 -->
        <a-card title="AI识别统计" style="margin-bottom: 16px">
          <a-space direction="vertical" style="width: 100%">
            <div v-for="item in aiStatistics" :key="item.type" style="width: 100%">
              <div style="display: flex; justify-content: space-between; margin-bottom: 4px">
                <span>{{ item.type }}</span>
                <span>{{ item.count }} 次</span>
              </div>
              <a-progress :percent="item.percent" :stroke-color="item.color" size="small" />
            </div>
          </a-space>
        </a-card>

        <!-- 实时预警列表 -->
        <a-card title="实时预警" :loading="alertLoading">
          <a-timeline>
            <a-timeline-item
              v-for="alert in recentAlerts"
              :key="alert.id"
              :color="alert.level === '高' ? 'red' : alert.level === '中' ? 'orange' : 'blue'"
            >
              <p><strong>{{ alert.time }}</strong></p>
              <p>{{ alert.location }}</p>
              <p>{{ alert.content }}</p>
              <a-button type="link" size="small" @click="handleAlert(alert)">
                立即处理
              </a-button>
            </a-timeline-item>
          </a-timeline>
        </a-card>
      </a-col>
    </a-row>

    <!-- 违规类型识别设置 -->
    <a-card title="违规类型识别配置" style="margin-bottom: 16px">
      <a-checkbox-group v-model:value="enabledViolations">
        <a-row>
          <a-col :span="6" v-for="item in violationTypes" :key="item.value">
            <a-checkbox :value="item.value">{{ item.label }}</a-checkbox>
          </a-col>
        </a-row>
      </a-checkbox-group>
    </a-card>

    <!-- AI识别日志表格 -->
    <a-card title="AI识别日志">
      <a-table
        :columns="logColumns"
        :data-source="logData"
        :loading="logLoading"
        :pagination="logPagination"
      >
        <template #bodyCell="{ column, record }">
          <template v-if="column.key === 'level'">
            <a-tag v-if="record.level === '高'" color="red">
              <ExclamationCircleOutlined />
              高
            </a-tag>
            <a-tag v-else-if="record.level === '中'" color="orange">
              中
            </a-tag>
            <a-tag v-else color="blue">
              低
            </a-tag>
          </template>
          <template v-else-if="column.key === 'confidence'">
            <a-progress
              :percent="record.confidence"
              :status="record.confidence > 80 ? 'success' : 'active'"
              size="small"
            />
          </template>
          <template v-else-if="column.key === 'action'">
            <a-space>
              <a-button type="link" size="small" @click="viewDetail(record)">
                查看详情
              </a-button>
              <a-button type="link" size="small" @click="handleViolation(record)">
                处理
              </a-button>
            </a-space>
          </template>
        </template>
      </a-table>
    </a-card>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { message } from 'ant-design-vue'
import {
  ThunderboltOutlined,
  CameraOutlined,
  VideoCameraOutlined,
  AlertOutlined,
  ClockCircleOutlined,
  ExclamationCircleFilled,
  ExclamationCircleOutlined
} from '@ant-design/icons-vue'

const monitoring = ref(true)
const loading = ref(false)
const alertLoading = ref(false)
const logLoading = ref(false)
const selectedArea = ref('all')

const todayViolations = ref(12)
const unprocessedCount = ref(3)

const monitorList = ref([
  {
    id: 1,
    name: '科二倒车入库A区',
    status: '在线',
    thumbnail: '',
    currentPerson: '张三',
    personRole: '学员',
    hasAlert: false
  },
  {
    id: 2,
    name: '科二倒车入库B区',
    status: '在线',
    thumbnail: '',
    currentPerson: '李四',
    personRole: '教练',
    hasAlert: true
  },
  {
    id: 3,
    name: '科二坡道起步',
    status: '在线',
    thumbnail: '',
    currentPerson: null,
    personRole: '',
    hasAlert: false
  },
  {
    id: 4,
    name: '科三路线1',
    status: '离线',
    thumbnail: '',
    currentPerson: '王五',
    personRole: '学员',
    hasAlert: false
  }
])

const aiStatistics = ref([
  { type: '非本校学员练车', count: 5, percent: 42, color: '#ff4d4f' },
  { type: '教练不在场独自练车', count: 3, percent: 25, color: '#fa8c16' },
  { type: '禁止时段练车', count: 2, percent: 17, color: '#faad14' },
  { type: '私带外校学员', count: 2, percent: 17, color: '#1890ff' }
])

const recentAlerts = ref([
  {
    id: 1,
    time: '14:32',
    location: '科二倒车入库B区',
    content: '检测到非本校学员练车',
    level: '高'
  },
  {
    id: 2,
    time: '14:15',
    location: '科二坡道起步',
    content: '教练不在场，学员独自练车',
    level: '中'
  },
  {
    id: 3,
    time: '13:58',
    location: '科三路线2',
    content: '非排班时间教学',
    level: '低'
  }
])

const violationTypes = ref([
  { label: '非本校学员练车', value: 'non_student' },
  { label: '教练不在场独自练车', value: 'no_coach' },
  { label: '禁止时段练车', value: 'forbidden_time' },
  { label: '私带外校学员', value: 'external_student' },
  { label: '非排班时间教学', value: 'off_schedule' },
  { label: '车辆超速', value: 'speeding' },
  { label: '闯红灯', value: 'red_light' },
  { label: '逆行', value: 'wrong_way' }
])

const enabledViolations = ref(['non_student', 'no_coach', 'forbidden_time', 'external_student'])

const logColumns = [
  { title: '时间', dataIndex: 'time', key: 'time', width: 100 },
  { title: '地点', dataIndex: 'location', key: 'location' },
  { title: '违规类型', dataIndex: 'violationType', key: 'violationType' },
  { title: '识别置信度', dataIndex: 'confidence', key: 'confidence', width: 150 },
  { title: '级别', dataIndex: 'level', key: 'level', width: 80 },
  { title: '操作', key: 'action', width: 150 }
]

const logData = ref([
  {
    key: '1',
    time: '14:32:15',
    location: '科二倒车入库B区',
    violationType: '非本校学员练车',
    confidence: 95,
    level: '高'
  },
  {
    key: '2',
    time: '14:15:20',
    location: '科二坡道起步',
    violationType: '教练不在场独自练车',
    confidence: 88,
    level: '中'
  },
  {
    key: '3',
    time: '13:58:42',
    location: '科三路线2',
    violationType: '非排班时间教学',
    confidence: 92,
    level: '低'
  }
])

const logPagination = ref({
  current: 1,
  pageSize: 10,
  total: 50
})

const toggleMonitoring = (checked) => {
  message.success(checked ? '监测已启动' : '监测已暂停')
}

const startAI = () => {
  message.loading('AI识别引擎启动中...', 2).then(() => {
    message.success('AI识别引擎已启动')
  })
}

const captureScreen = () => {
  message.success('截图已保存')
}

const handleAlert = (alert) => {
  message.info(`处理预警: ${alert.content}`)
}

const viewDetail = (record) => {
  message.info(`查看详情: ${record.violationType}`)
}

const handleViolation = (record) => {
  message.success(`已处理违规记录`)
}
</script>

<style scoped>
.monitor-center {
  padding: 0;
}

.monitor-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
  gap: 16px;
}

.monitor-item {
  border: 1px solid #f0f0f0;
  border-radius: 8px;
  overflow: hidden;
  transition: all 0.3s;
}

.monitor-item.alert-active {
  border-color: #ff4d4f;
  box-shadow: 0 0 10px rgba(255, 77, 79, 0.3);
  animation: pulse 2s infinite;
}

@keyframes pulse {
  0%, 100% {
    box-shadow: 0 0 10px rgba(255, 77, 79, 0.3);
  }
  50% {
    box-shadow: 0 0 20px rgba(255, 77, 79, 0.6);
  }
}

.monitor-header {
  padding: 12px;
  background: #fafafa;
  border-bottom: 1px solid #f0f0f0;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.monitor-title {
  font-weight: 500;
}

.monitor-video {
  position: relative;
  height: 200px;
  background: #000;
  display: flex;
  align-items: center;
  justify-content: center;
}

.monitor-video img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.monitor-placeholder {
  text-align: center;
  color: #8c8c8c;
}

.monitor-placeholder p {
  margin-top: 8px;
}

.alert-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(255, 77, 79, 0.9);
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  color: white;
}

.alert-overlay p {
  margin-top: 16px;
  font-size: 16px;
}

.monitor-footer {
  padding: 12px;
  background: #fafafa;
  border-top: 1px solid #f0f0f0;
}
</style>
