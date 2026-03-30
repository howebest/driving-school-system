<template>
  <div class="warning-management">
    <!-- 页面标题 -->
    <a-page-header
      title="预警管理"
      sub-title="管理和处理系统预警信息"
      style="padding: 0 0 16px 0"
    >
      <template #extra>
        <a-space>
          <a-badge :count="unreadCount">
            <a-button @click="refreshWarnings">
              <template #icon><ReloadOutlined /></template>
              刷新
            </a-button>
          </a-badge>
          <a-button type="primary" @click="handleBatchProcess">
            <template #icon><CheckOutlined /></template>
            批量处理
          </a-button>
        </a-space>
      </template>
    </a-page-header>

    <!-- 统计卡片 -->
    <a-row :gutter="16" style="margin-bottom: 16px">
      <a-col :xs="12" :sm="6">
        <a-card>
          <a-statistic
            title="今日预警"
            :value="todayWarnings"
            :value-style="{ color: '#cf1322' }"
            :prefix="h(AlertOutlined)"
          />
        </a-card>
      </a-col>
      <a-col :xs="12" :sm="6">
        <a-card>
          <a-statistic
            title="未处理"
            :value="unprocessedCount"
            :value-style="{ color: '#fa8c16' }"
            :prefix="h(ClockCircleOutlined)"
          />
        </a-card>
      </a-col>
      <a-col :xs="12" :sm="6">
        <a-card>
          <a-statistic
            title="已处理"
            :value="processedCount"
            :value-style="{ color: '#3f8600' }"
            :prefix="h(CheckCircleOutlined)"
          />
        </a-card>
      </a-col>
      <a-col :xs="12" :sm="6">
        <a-card>
          <a-statistic
            title="处理率"
            :value="processRate"
            suffix="%"
            :value-style="{ color: '#1890ff' }"
            :prefix="h(PercentageOutlined)"
          />
        </a-card>
      </a-col>
    </a-row>

    <!-- 筛选表单 -->
    <a-card style="margin-bottom: 16px">
      <a-form layout="inline" :model="filterForm">
        <a-row :gutter="16" style="width: 100%">
          <a-col :xs="24" :sm="12" :md="6">
            <a-form-item label="预警级别" style="width: 100%">
              <a-select v-model:value="filterForm.level" placeholder="全部" allow-clear>
                <a-select-option value="高">高</a-select-option>
                <a-select-option value="中">中</a-select-option>
                <a-select-option value="低">低</a-select-option>
              </a-select>
            </a-form-item>
          </a-col>

          <a-col :xs="24" :sm="12" :md="6">
            <a-form-item label="处理状态" style="width: 100%">
              <a-select v-model:value="filterForm.status" placeholder="全部" allow-clear>
                <a-select-option value="未处理">未处理</a-select-option>
                <a-select-option value="处理中">处理中</a-select-option>
                <a-select-option value="已处理">已处理</a-select-option>
              </a-select>
            </a-form-item>
          </a-col>

          <a-col :xs="24" :sm="12" :md="6">
            <a-form-item label="时间范围" style="width: 100%">
              <a-range-picker v-model:value="filterForm.dateRange" style="width: 100%" />
            </a-form-item>
          </a-col>

          <a-col :xs="24" :sm="12" :md="6">
            <a-space>
              <a-button type="primary" @click="handleFilter">筛选</a-button>
              <a-button @click="resetFilter">重置</a-button>
            </a-space>
          </a-col>
        </a-row>
      </a-form>
    </a-card>

    <!-- 预警列表 -->
    <a-card>
      <a-table
        :columns="columns"
        :data-source="warningList"
        :loading="loading"
        :pagination="pagination"
        :row-selection="rowSelection"
        @change="handleTableChange"
      >
        <template #title>
          <a-space>
            <span>预警列表</span>
            <a-tag color="red">{{ unprocessedCount }} 条待处理</a-tag>
          </a-space>
        </template>

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

          <template v-else-if="column.key === 'type'">
            <a-space>
              <AlertOutlined />
              <span>{{ record.type }}</span>
            </a-space>
          </template>

          <template v-else-if="column.key === 'status'">
            <a-badge
              :status="record.status === '已处理' ? 'success' : record.status === '处理中' ? 'processing' : 'warning'"
              :text="record.status"
            />
          </template>

          <template v-else-if="column.key === 'action'">
            <a-space>
              <a-button
                v-if="record.status !== '已处理'"
                type="primary"
                size="small"
                @click="handleProcess(record)"
              >
                处理
              </a-button>
              <a-button
                v-else
                type="link"
                size="small"
                @click="viewDetail(record)"
              >
                查看
              </a-button>
              <a-button type="link" size="small" @click="viewDetail(record)">
                详情
              </a-button>
            </a-space>
          </template>
        </template>
      </a-table>
    </a-card>

    <!-- 处理预警模态框 -->
    <a-modal
      v-model:open="processModalVisible"
      title="处理预警"
      width="600px"
      @ok="submitProcess"
      @cancel="processModalVisible = false"
    >
      <a-form :label-col="{ span: 4 }" :wrapper-col="{ span: 20 }">
        <a-form-item label="预警信息">
          <a-descriptions :column="1" size="small" bordered>
            <a-descriptions-item label="时间">{{ currentWarning.time }}</a-descriptions-item>
            <a-descriptions-item label="地点">{{ currentWarning.location }}</a-descriptions-item>
            <a-descriptions-item label="类型">{{ currentWarning.type }}</a-descriptions-item>
            <a-descriptions-item label="人员">{{ currentWarning.person }}</a-descriptions-item>
          </a-descriptions>
        </a-form-item>

        <a-form-item label="处理方式">
          <a-radio-group v-model:value="processForm.method">
            <a-radio value="警告提醒">警告提醒</a-radio>
            <a-radio value="现场处理">现场处理</a-radio>
            <a-radio value="通知教练">通知教练</a-radio>
            <a-radio value="其他">其他</a-radio>
          </a-radio-group>
        </a-form-item>

        <a-form-item label="处理说明">
          <a-textarea
            v-model:value="processForm.description"
            placeholder="请输入处理说明"
            :rows="4"
          />
        </a-form-item>

        <a-form-item label="后续措施">
          <a-checkbox-group v-model:value="processForm.followUp">
            <a-row>
              <a-col :span="12">
                <a-checkbox value="加强监控">加强监控</a-checkbox>
              </a-col>
              <a-col :span="12">
                <a-checkbox value="通知管理员">通知管理员</a-checkbox>
              </a-col>
              <a-col :span="12">
                <a-checkbox value="记录档案">记录档案</a-checkbox>
              </a-col>
              <a-col :span="12">
                <a-checkbox value="培训教育">培训教育</a-checkbox>
              </a-col>
            </a-row>
          </a-checkbox-group>
        </a-form-item>
      </a-form>
    </a-modal>

    <!-- 预警详情模态框 -->
    <a-modal
      v-model:open="detailModalVisible"
      title="预警详情"
      width="700px"
      :footer="null"
    >
      <a-descriptions :column="2" bordered>
        <a-descriptions-item label="预警ID">{{ currentWarning.id }}</a-descriptions-item>
        <a-descriptions-item label="预警级别">
          <a-tag v-if="currentWarning.level === '高'" color="red">高</a-tag>
          <a-tag v-else-if="currentWarning.level === '中'" color="orange">中</a-tag>
          <a-tag v-else color="blue">低</a-tag>
        </a-descriptions-item>
        <a-descriptions-item label="发生时间">{{ currentWarning.time }}</a-descriptions-item>
        <a-descriptions-item label="发生地点">{{ currentWarning.location }}</a-descriptions-item>
        <a-descriptions-item label="违规类型">{{ currentWarning.type }}</a-descriptions-item>
        <a-descriptions-item label="相关人员">{{ currentWarning.person }}</a-descriptions-item>
        <a-descriptions-item label="人员身份">{{ currentWarning.role }}</a-descriptions-item>
        <a-descriptions-item label="处理状态">
          <a-badge
            :status="currentWarning.status === '已处理' ? 'success' : 'warning'"
            :text="currentWarning.status"
          />
        </a-descriptions-item>
        <a-descriptions-item label="处理人" :span="2">{{ currentWarning.handler || '-' }}</a-descriptions-item>
        <a-descriptions-item label="处理时间" :span="2">{{ currentWarning.handleTime || '-' }}</a-descriptions-item>
        <a-descriptions-item label="处理说明" :span="2">{{ currentWarning.handleDescription || '-' }}</a-descriptions-item>
      </a-descriptions>

      <a-divider>关联截图</a-divider>
      <a-empty description="暂无截图" />
    </a-modal>
  </div>
</template>

<script setup>
import { ref, reactive, h, computed } from 'vue'
import { message } from 'ant-design-vue'
import {
  AlertOutlined,
  ClockCircleOutlined,
  CheckCircleOutlined,
  PercentageOutlined,
  ReloadOutlined,
  CheckOutlined,
  ExclamationCircleOutlined
} from '@ant-design/icons-vue'

const loading = ref(false)
const processModalVisible = ref(false)
const detailModalVisible = ref(false)
const unreadCount = ref(5)

const todayWarnings = ref(18)
const unprocessedCount = ref(5)
const processedCount = ref(13)
const processRate = computed(() => {
  return Math.round((processedCount.value / todayWarnings.value) * 100)
})

const filterForm = reactive({
  level: undefined,
  status: undefined,
  dateRange: null
})

const processForm = reactive({
  method: '警告提醒',
  description: '',
  followUp: ['记录档案']
})

const currentWarning = ref({})

const columns = [
  {
    title: '时间',
    dataIndex: 'time',
    key: 'time',
    width: 150
  },
  {
    title: '预警级别',
    dataIndex: 'level',
    key: 'level',
    width: 100
  },
  {
    title: '违规类型',
    dataIndex: 'type',
    key: 'type',
    width: 180
  },
  {
    title: '地点',
    dataIndex: 'location',
    key: 'location',
    ellipsis: true
  },
  {
    title: '人员',
    dataIndex: 'person',
    key: 'person',
    width: 100
  },
  {
    title: '状态',
    dataIndex: 'status',
    key: 'status',
    width: 100
  },
  {
    title: '操作',
    key: 'action',
    width: 150,
    fixed: 'right'
  }
]

const warningList = ref([
  {
    key: '1',
    id: 'W2024032501',
    time: '2024-03-25 14:32:15',
    level: '高',
    type: '非本校学员练车',
    location: '科二倒车入库B区',
    person: '张三',
    role: '学员',
    status: '未处理',
    handler: null,
    handleTime: null,
    handleDescription: null
  },
  {
    key: '2',
    id: 'W2024032502',
    time: '2024-03-25 14:15:20',
    level: '中',
    type: '教练不在场独自练车',
    location: '科二坡道起步区',
    person: '王五',
    role: '学员',
    status: '处理中',
    handler: '管理员',
    handleTime: null,
    handleDescription: null
  },
  {
    key: '3',
    id: 'W2024032503',
    time: '2024-03-25 13:58:42',
    level: '低',
    type: '非排班时间教学',
    location: '科三路线2',
    person: '李四',
    role: '教练',
    status: '已处理',
    handler: '管理员',
    handleTime: '2024-03-25 14:10:30',
    handleDescription: '已通知教练按时排班'
  },
  {
    key: '4',
    id: 'W2024032504',
    time: '2024-03-25 13:45:10',
    level: '高',
    type: '私带外校学员',
    location: '科三路线1',
    person: '赵六',
    role: '教练',
    status: '未处理',
    handler: null,
    handleTime: null,
    handleDescription: null
  }
])

const pagination = ref({
  current: 1,
  pageSize: 10,
  total: 50,
  showSizeChanger: true,
  showTotal: (total) => `共 ${total} 条记录`
})

const selectedRowKeys = ref([])
const rowSelection = {
  selectedRowKeys: selectedRowKeys,
  onChange: (keys) => {
    selectedRowKeys.value = keys
  }
}

const refreshWarnings = () => {
  loading.value = true
  setTimeout(() => {
    loading.value = false
    message.success('预警列表已刷新')
  }, 1000)
}

const handleFilter = () => {
  message.success('筛选完成')
}

const resetFilter = () => {
  filterForm.level = undefined
  filterForm.status = undefined
  filterForm.dateRange = null
  message.success('已重置筛选条件')
}

const handleTableChange = (pag) => {
  pagination.value.current = pag.current
  pagination.value.pageSize = pag.pageSize
}

const handleProcess = (record) => {
  currentWarning.value = record
  processModalVisible.value = true
}

const submitProcess = () => {
  message.loading('正在处理...', 1).then(() => {
    message.success('预警处理成功')
    processModalVisible.value = false
    record.status = '已处理'
  })
}

const viewDetail = (record) => {
  currentWarning.value = record
  detailModalVisible.value = true
}

const handleBatchProcess = () => {
  if (selectedRowKeys.value.length === 0) {
    message.warning('请选择要处理的预警')
    return
  }
  message.success(`批量处理 ${selectedRowKeys.value.length} 条预警`)
  selectedRowKeys.value = []
}
</script>

<style scoped>
.warning-management {
  padding: 0;
}
</style>
