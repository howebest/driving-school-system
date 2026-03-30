<template>
  <div class="dashboard">
    <!-- 统计卡片 -->
    <a-row :gutter="[16, 16]">
      <a-col :xs="24" :sm="12" :md="6">
        <a-card class="stat-card stat-card-danger">
          <a-statistic
            title="今日违规"
            :value="violationCount"
            :prefix="h(AlertOutlined)"
          >
            <template #suffix>
              <span class="stat-unit">次</span>
            </template>
          </a-statistic>
        </a-card>
      </a-col>

      <a-col :xs="24" :sm="12" :md="6">
        <a-card class="stat-card stat-card-warning">
          <a-statistic
            title="未处理记录"
            :value="unprocessedCount"
            :prefix="h(ClockCircleOutlined)"
          >
            <template #suffix>
              <span class="stat-unit">条</span>
            </template>
          </a-statistic>
        </a-card>
      </a-col>

      <a-col :xs="24" :sm="12" :md="6">
        <a-card class="stat-card stat-card-success">
          <a-statistic
            title="今日注册人数"
            :value="registrationCount"
            :prefix="h(UserAddOutlined)"
          >
            <template #suffix>
              <span class="stat-unit">人</span>
            </template>
          </a-statistic>
        </a-card>
      </a-col>

      <a-col :xs="24" :sm="12" :md="6">
        <a-card class="stat-card stat-card-info">
          <a-statistic
            title="正在车辆"
            :value="activeVehicles"
            :prefix="h(CarOutlined)"
          >
            <template #suffix>
              <span class="stat-unit">辆</span>
            </template>
          </a-statistic>
        </a-card>
      </a-col>
    </a-row>

    <!-- 图表区域 -->
    <a-row :gutter="[16, 16]" style="margin-top: 16px">
      <a-col :xs="24" :lg="16">
        <a-card title="近7天违规趋势">
          <template #extra>
            <a-button type="link" @click="viewDetails('trend')">
              查看详情 <RightOutlined />
            </a-button>
          </template>
          <div class="chart-placeholder">
            <div ref="trendChart" style="width: 100%; height: 300px"></div>
          </div>
        </a-card>
      </a-col>

      <a-col :xs="24" :lg="8">
        <a-card title="违规类型分布">
          <template #extra>
            <a-button type="link" @click="viewDetails('distribution')">
              查看详情 <RightOutlined />
            </a-button>
          </template>
          <div class="chart-placeholder">
            <div ref="pieChart" style="width: 100%; height: 300px"></div>
          </div>
        </a-card>
      </a-col>
    </a-row>

    <!-- 实时预警表格 -->
    <a-card title="实时预警" style="margin-top: 16px">
      <template #extra>
        <a-space>
          <a-button @click="refreshData">
            <template #icon><ReloadOutlined /></template>
            刷新
          </a-button>
        </a-space>
      </template>

      <a-table
        :columns="columns"
        :data-source="alertData"
        :loading="loading"
        :pagination="pagination"
        @change="handleTableChange"
      >
        <template #bodyCell="{ column, record }">
          <template v-if="column.key === 'status'">
            <a-tag v-if="record.status === '未处理'" color="warning">
              {{ record.status }}
            </a-tag>
            <a-tag v-else-if="record.status === '已处理'" color="success">
              {{ record.status }}
            </a-tag>
          </template>
          <template v-else-if="column.key === 'action'">
            <a-button
              v-if="record.status === '未处理'"
              type="primary"
              size="small"
              @click="handleAlert(record)"
            >
              处理
            </a-button>
            <a-button v-else type="link" size="small">
              查看
            </a-button>
          </template>
        </template>
      </a-table>
    </a-card>
  </div>
</template>

<script setup>
import { ref, onMounted, h } from 'vue'
import { message } from 'ant-design-vue'
import {
  AlertOutlined,
  ClockCircleOutlined,
  UserAddOutlined,
  CarOutlined,
  RightOutlined,
  ReloadOutlined
} from '@ant-design/icons-vue'

const violationCount = ref(12)
const unprocessedCount = ref(3)
const registrationCount = ref(45)
const activeVehicles = ref(18)
const loading = ref(false)

const columns = [
  {
    title: '时间',
    dataIndex: 'time',
    key: 'time',
    width: 100
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
    width: 80
  },
  {
    title: '身份',
    dataIndex: 'role',
    key: 'role',
    width: 80
  },
  {
    title: '违规类型',
    dataIndex: 'violationType',
    key: 'violationType',
    ellipsis: true
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
    width: 80,
    fixed: 'right'
  }
]

const alertData = ref([
  {
    key: '1',
    time: '14:32-15',
    location: '科二倒车入库区域',
    person: '张三',
    role: '学员',
    violationType: '非本校学员练车',
    status: '未处理'
  },
  {
    key: '2',
    time: '14:28-33',
    location: '科三路线1',
    person: '李四',
    role: '教练',
    violationType: '非排班时间教学',
    status: '已处理'
  },
  {
    key: '3',
    time: '14:15-20',
    location: '科二倒车入库区域',
    person: '王五',
    role: '学员',
    violationType: '教练不在场独自练车',
    status: '未处理'
  },
  {
    key: '4',
    time: '13:58-42',
    location: '科二坡道起步区域',
    person: '赵六',
    role: '学员',
    violationType: '禁止时段练车',
    status: '已处理'
  },
  {
    key: '5',
    time: '13:45-10',
    location: '科三路线2',
    person: '钱七',
    role: '教练',
    violationType: '私带外校学员',
    status: '未处理'
  }
])

const pagination = ref({
  current: 1,
  pageSize: 10,
  total: 50,
  showSizeChanger: true,
  showQuickJumper: true,
  showTotal: (total) => `共 ${total} 条记录`
})

const viewDetails = (type) => {
  message.info(`查看${type === 'trend' ? '趋势' : '分布'}详情`)
}

const refreshData = () => {
  loading.value = true
  setTimeout(() => {
    loading.value = false
    message.success('数据已刷新')
  }, 1000)
}

const handleTableChange = (pag) => {
  pagination.value.current = pag.current
  pagination.value.pageSize = pag.pageSize
}

const handleAlert = (record) => {
  message.success(`已处理: ${record.person} 的违规记录`)
  record.status = '已处理'
}

onMounted(() => {
  // 这里可以初始化图表
  console.log('Dashboard mounted')
})
</script>

<style scoped>
.dashboard {
  padding: 0;
}

.stat-card {
  border-radius: 8px;
  transition: all 0.3s;
}

.stat-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.stat-card-danger {
  border-left: 4px solid #ff4d4f;
}

.stat-card-warning {
  border-left: 4px solid #fa8c16;
}

.stat-card-success {
  border-left: 4px solid #52c41a;
}

.stat-card-info {
  border-left: 4px solid #1890ff;
}

.stat-unit {
  font-size: 14px;
  color: #8c8c8c;
  margin-left: 4px;
}

.chart-placeholder {
  display: flex;
  align-items: center;
  justify-content: center;
  background: #fafafa;
  border-radius: 8px;
}

@media (max-width: 768px) {
  .stat-card {
    margin-bottom: 8px;
  }
}
</style>
