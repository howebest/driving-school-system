<template>
  <div class="violation-record">
    <!-- 页面标题 -->
    <a-page-header
      title="违规记录"
      sub-title="查询和管理历史违规记录"
      style="padding: 0 0 16px 0"
    >
      <template #extra>
        <a-space>
          <a-button @click="exportRecords">
            <template #icon><DownloadOutlined /></template>
            导出记录
          </a-button>
          <a-button type="primary" @click="showStatistics">
            <template #icon><BarChartOutlined /></template>
            统计分析
          </a-button>
        </a-space>
      </template>
    </a-page-header>

    <!-- 统计卡片 -->
    <a-row :gutter="16" style="margin-bottom: 16px">
      <a-col :xs="12" :sm="6">
        <a-card>
          <a-statistic
            title="本月违规"
            :value="monthTotal"
            :prefix="h(FileTextOutlined)"
          />
        </a-card>
      </a-col>
      <a-col :xs="12" :sm="6">
        <a-card>
          <a-statistic
            title="学员违规"
            :value="studentViolations"
            :value-style="{ color: '#1890ff' }"
            :prefix="h(UserOutlined)"
          />
        </a-card>
      </a-col>
      <a-col :xs="12" :sm="6">
        <a-card>
          <a-statistic
            title="教练违规"
            :value="coachViolations"
            :value-style="{ color: '#722ed1' }"
            :prefix="h(TeamOutlined)"
          />
        </a-card>
      </a-col>
      <a-col :xs="12" :sm="6">
        <a-card>
          <a-statistic
            title="处理率"
            :value="handleRate"
            suffix="%"
            :value-style="{ color: '#3f8600' }"
            :prefix="h(CheckCircleOutlined)"
          />
        </a-card>
      </a-col>
    </a-row>

    <!-- 搜索表单 -->
    <a-card style="margin-bottom: 16px">
      <a-form layout="inline" :model="searchForm">
        <a-row :gutter="16" style="width: 100%">
          <a-col :xs="24" :sm="12" :md="6">
            <a-form-item label="关键词" style="width: 100%">
              <a-input
                v-model:value="searchForm.keyword"
                placeholder="姓名/车牌号"
                allow-clear
              />
            </a-form-item>
          </a-col>

          <a-col :xs="24" :sm="12" :md="6">
            <a-form-item label="违规类型" style="width: 100%">
              <a-select
                v-model:value="searchForm.violationType"
                placeholder="全部类型"
                allow-clear
              >
                <a-select-option value="非本校学员练车">非本校学员练车</a-select-option>
                <a-select-option value="教练不在场独自练车">教练不在场独自练车</a-select-option>
                <a-select-option value="禁止时段练车">禁止时段练车</a-select-option>
                <a-select-option value="私带外校学员">私带外校学员</a-select-option>
                <a-select-option value="非排班时间教学">非排班时间教学</a-select-option>
              </a-select>
            </a-form-item>
          </a-col>

          <a-col :xs="24" :sm="12" :md="6">
            <a-form-item label="时间范围" style="width: 100%">
              <a-range-picker v-model:value="searchForm.dateRange" style="width: 100%" />
            </a-form-item>
          </a-col>

          <a-col :xs="24" :sm="12" :md="6">
            <a-space>
              <a-button type="primary" @click="handleSearch">搜索</a-button>
              <a-button @click="resetSearch">重置</a-button>
            </a-space>
          </a-col>
        </a-row>
      </a-form>
    </a-card>

    <!-- 数据表格 -->
    <a-card>
      <a-table
        :columns="columns"
        :data-source="recordList"
        :loading="loading"
        :pagination="pagination"
        :scroll="{ x: 1500 }"
        @change="handleTableChange"
      >
        <template #title>
          <a-tabs v-model:activeKey="activeTab" @change="handleTabChange">
            <a-tab-pane key="all" tab="全部记录" />
            <a-tab-pane key="student" tab="学员违规" />
            <a-tab-pane key="coach" tab="教练违规" />
          </a-tabs>
        </template>

        <template #bodyCell="{ column, record }">
          <template v-if="column.key === 'person'">
            <a-space>
              <a-avatar :size="28" :style="{ backgroundColor: record.role === '学员' ? '#1890ff' : '#722ed1' }">
                {{ record.person.substring(0, 1) }}
              </a-avatar>
              <div>
                <div>{{ record.person }}</div>
                <div style="font-size: 12px; color: #8c8c8c">{{ record.role }}</div>
              </div>
            </a-space>
          </template>

          <template v-else-if="column.key === 'violationType'">
            <a-tag :color="getViolationColor(record.violationType)">
              {{ record.violationType }}
            </a-tag>
          </template>

          <template v-else-if="column.key === 'status'">
            <a-badge
              :status="record.status === '已处理' ? 'success' : record.status === '处理中' ? 'processing' : 'warning'"
              :text="record.status"
            />
          </template>

          <template v-else-if="column.key === 'level'">
            <a-tag v-if="record.level === '高'" color="red">高</a-tag>
            <a-tag v-else-if="record.level === '中'" color="orange">中</a-tag>
            <a-tag v-else color="blue">低</a-tag>
          </template>

          <template v-else-if="column.key === 'action'">
            <a-space>
              <a-button type="link" size="small" @click="viewRecord(record)">
                查看
              </a-button>
              <a-button
                v-if="record.status !== '已处理'"
                type="link"
                size="small"
                @click="handleRecord(record)"
              >
                处理
              </a-button>
              <a-button type="link" size="small" @click="printRecord(record)">
                打印
              </a-button>
            </a-space>
          </template>
        </template>
      </a-table>
    </a-card>

    <!-- 详情抽屉 -->
    <a-drawer
      v-model:open="drawerVisible"
      title="违规记录详情"
      width="600"
      :footer-style="{ textAlign: 'right' }"
    >
      <a-descriptions :column="1" bordered>
        <a-descriptions-item label="记录编号">{{ currentRecord.id }}</a-descriptions-item>
        <a-descriptions-item label="违规时间">{{ currentRecord.time }}</a-descriptions-item>
        <a-descriptions-item label="违规地点">{{ currentRecord.location }}</a-descriptions-item>
        <a-descriptions-item label="违规类型">
          <a-tag :color="getViolationColor(currentRecord.violationType)">
            {{ currentRecord.violationType }}
          </a-tag>
        </a-descriptions-item>
        <a-descriptions-item label="违规人员">{{ currentRecord.person }} ({{ currentRecord.role }})</a-descriptions-item>
        <a-descriptions-item label="关联车辆">{{ currentRecord.vehicle || '-' }}</a-descriptions-item>
        <a-descriptions-item label="违规级别">
          <a-tag v-if="currentRecord.level === '高'" color="red">高</a-tag>
          <a-tag v-else-if="currentRecord.level === '中'" color="orange">中</a-tag>
          <a-tag v-else color="blue">低</a-tag>
        </a-descriptions-item>
        <a-descriptions-item label="处理状态">
          <a-badge
            :status="currentRecord.status === '已处理' ? 'success' : 'warning'"
            :text="currentRecord.status"
          />
        </a-descriptions-item>
        <a-descriptions-item label="处理人">{{ currentRecord.handler || '-' }}</a-descriptions-item>
        <a-descriptions-item label="处理时间">{{ currentRecord.handleTime || '-' }}</a-descriptions-item>
        <a-descriptions-item label="处理方式">{{ currentRecord.handleMethod || '-' }}</a-descriptions-item>
        <a-descriptions-item label="处理说明">{{ currentRecord.handleDescription || '-' }}</a-descriptions-item>
      </a-descriptions>

      <a-divider>违规截图</a-divider>
      <a-empty description="暂无截图" />

      <template #footer>
        <a-space>
          <a-button @click="drawerVisible = false">关闭</a-button>
          <a-button v-if="currentRecord.status !== '已处理'" type="primary" @click="handleRecord(currentRecord)">
            处理
          </a-button>
        </a-space>
      </template>
    </a-drawer>

    <!-- 统计分析模态框 -->
    <a-modal
      v-model:open="statsModalVisible"
      title="违规统计分析"
      width="900px"
      :footer="null"
    >
      <a-row :gutter="16">
        <a-col :span="12">
          <a-card title="违规类型分布" size="small">
            <div style="height: 200px; display: flex; align-items: center; justify-content: center;">
              <div style="width: 150px; height: 150px; border-radius: 50%; background: conic-gradient(#ff4d4f 0% 30%, #fa8c16 30% 50%, #faad14 50% 70%, #1890ff 70% 90%, #52c41a 90% 100%);"></div>
            </div>
          </a-card>
        </a-col>
        <a-col :span="12">
          <a-card title="月度趋势" size="small">
            <div style="height: 200px; padding: 20px;">
              <div style="display: flex; align-items: flex-end; height: 100%; gap: 8px;">
                <div v-for="(val, idx) in [12, 15, 18, 22, 16, 20]" :key="idx" style="flex: 1; text-align: center;">
                  <div :style="{height: val * 5 + 'px', background: 'linear-gradient(to top, #1890ff, #4096ff)', borderRadius: '4px 4px 0 0'}"></div>
                  <div style="font-size: 10px; color: #8c8c8c; margin-top: 4px;">{{ idx + 1 }}月</div>
                </div>
              </div>
            </div>
          </a-card>
        </a-col>
      </a-row>

      <a-card title="详细统计" size="small" style="margin-top: 16px">
        <a-table
          :columns="statsColumns"
          :data-source="statsData"
          :pagination="false"
          size="small"
        />
      </a-card>
    </a-modal>
  </div>
</template>

<script setup>
import { ref, reactive, h } from 'vue'
import { message } from 'ant-design-vue'
import {
  DownloadOutlined,
  BarChartOutlined,
  FileTextOutlined,
  UserOutlined,
  TeamOutlined,
  CheckCircleOutlined
} from '@ant-design/icons-vue'

const loading = ref(false)
const drawerVisible = ref(false)
const statsModalVisible = ref(false)
const activeTab = ref('all')

const monthTotal = ref(156)
const studentViolations = ref(112)
const coachViolations = ref(44)
const handleRate = ref(92)

const searchForm = reactive({
  keyword: '',
  violationType: undefined,
  dateRange: null
})

const currentRecord = ref({})

const columns = [
  {
    title: '记录编号',
    dataIndex: 'id',
    key: 'id',
    width: 130
  },
  {
    title: '违规时间',
    dataIndex: 'time',
    key: 'time',
    width: 160
  },
  {
    title: '违规人员',
    dataIndex: 'person',
    key: 'person',
    width: 120
  },
  {
    title: '违规地点',
    dataIndex: 'location',
    key: 'location',
    width: 150
  },
  {
    title: '违规类型',
    dataIndex: 'violationType',
    key: 'violationType',
    width: 160
  },
  {
    title: '级别',
    dataIndex: 'level',
    key: 'level',
    width: 70
  },
  {
    title: '状态',
    dataIndex: 'status',
    key: 'status',
    width: 90
  },
  {
    title: '操作',
    key: 'action',
    width: 150,
    fixed: 'right'
  }
]

const recordList = ref([
  {
    key: '1',
    id: 'V20240325001',
    time: '2024-03-25 14:32:15',
    person: '张三',
    role: '学员',
    location: '科二倒车入库B区',
    violationType: '非本校学员练车',
    level: '高',
    status: '已处理',
    vehicle: '京A12345',
    handler: '管理员',
    handleTime: '2024-03-25 14:45:00',
    handleMethod: '警告提醒',
    handleDescription: '已对该学员进行警告教育'
  },
  {
    key: '2',
    id: 'V20240325002',
    time: '2024-03-25 14:15:20',
    person: '王五',
    role: '学员',
    location: '科二坡道起步区',
    violationType: '教练不在场独自练车',
    level: '中',
    status: '已处理',
    vehicle: '京A11111',
    handler: '管理员',
    handleTime: '2024-03-25 14:30:00',
    handleMethod: '通知教练',
    handleDescription: '已通知相关教练到场'
  },
  {
    key: '3',
    id: 'V20240325003',
    time: '2024-03-25 13:58:42',
    person: '李四',
    role: '教练',
    location: '科三路线2',
    violationType: '非排班时间教学',
    level: '低',
    status: '已处理',
    vehicle: '京A67890',
    handler: '管理员',
    handleTime: '2024-03-25 14:10:30',
    handleMethod: '警告提醒',
    handleDescription: '已提醒教练按时排班'
  },
  {
    key: '4',
    id: 'V20240325004',
    time: '2024-03-25 13:45:10',
    person: '赵六',
    role: '教练',
    location: '科三路线1',
    violationType: '私带外校学员',
    level: '高',
    status: '处理中',
    vehicle: '京A22222',
    handler: null,
    handleTime: null,
    handleMethod: null,
    handleDescription: null
  }
])

const pagination = ref({
  current: 1,
  pageSize: 10,
  total: 156,
  showSizeChanger: true,
  showQuickJumper: true,
  showTotal: (total) => `共 ${total} 条记录`
})

const statsColumns = [
  { title: '违规类型', dataIndex: 'type', key: 'type' },
  { title: '发生次数', dataIndex: 'count', key: 'count' },
  { title: '占比', dataIndex: 'percent', key: 'percent' },
  { title: '处理率', dataIndex: 'handleRate', key: 'handleRate' }
]

const statsData = ref([
  { key: '1', type: '非本校学员练车', count: 45, percent: '28.8%', handleRate: '95%' },
  { key: '2', type: '教练不在场独自练车', count: 38, percent: '24.4%', handleRate: '92%' },
  { key: '3', type: '禁止时段练车', count: 32, percent: '20.5%', handleRate: '88%' },
  { key: '4', type: '私带外校学员', count: 25, percent: '16.0%', handleRate: '85%' },
  { key: '5', type: '非排班时间教学', count: 16, percent: '10.3%', handleRate: '90%' }
])

const getViolationColor = (type) => {
  const colorMap = {
    '非本校学员练车': 'red',
    '教练不在场独自练车': 'orange',
    '禁止时段练车': 'gold',
    '私带外校学员': 'purple',
    '非排班时间教学': 'blue'
  }
  return colorMap[type] || 'default'
}

const handleSearch = () => {
  loading.value = true
  setTimeout(() => {
    loading.value = false
    message.success('搜索完成')
  }, 500)
}

const resetSearch = () => {
  searchForm.keyword = ''
  searchForm.violationType = undefined
  searchForm.dateRange = null
  message.success('已重置')
}

const handleTableChange = (pag) => {
  pagination.value.current = pag.current
  pagination.value.pageSize = pag.pageSize
}

const handleTabChange = (key) => {
  message.info(`切换到: ${key === 'all' ? '全部' : key === 'student' ? '学员违规' : '教练违规'}`)
}

const viewRecord = (record) => {
  currentRecord.value = record
  drawerVisible.value = true
}

const handleRecord = (record) => {
  message.info(`处理记录: ${record.id}`)
}

const printRecord = (record) => {
  message.success(`正在打印记录: ${record.id}`)
}

const exportRecords = () => {
  message.loading('正在导出...', 2).then(() => {
    message.success('导出成功')
  })
}

const showStatistics = () => {
  statsModalVisible.value = true
}
</script>

<style scoped>
.violation-record {
  padding: 0;
}

:deep(.ant-tabs-nav) {
  margin-bottom: 0;
}
</style>
