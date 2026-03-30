<template>
  <div class="report-center">
    <!-- 页面标题 -->
    <a-page-header
      title="统计报表"
      sub-title="数据分析和统计报表"
      style="padding: 0 0 16px 0"
    >
      <template #extra>
        <a-space>
          <a-range-picker v-model:value="dateRange" />
          <a-button type="primary" @click="exportReport">
            <template #icon><DownloadOutlined /></template>
            导出报表
          </a-button>
        </a-space>
      </template>
    </a-page-header>

    <!-- 关键指标 -->
    <a-row :gutter="16" style="margin-bottom: 16px">
      <a-col :xs="12" :sm="6">
        <a-card>
          <a-statistic
            title="总学员数"
            :value="totalStudents"
            :prefix="h(UserOutlined)"
          />
        </a-card>
      </a-col>
      <a-col :xs="12" :sm="6">
        <a-card>
          <a-statistic
            title="总教练数"
            :value="totalCoaches"
            :prefix="h(TeamOutlined)"
          />
        </a-card>
      </a-col>
      <a-col :xs="12" :sm="6">
        <a-card>
          <a-statistic
            title="总违规数"
            :value="totalViolations"
            :value-style="{ color: '#cf1322' }"
            :prefix="h(AlertOutlined)"
          />
        </a-card>
      </a-col>
      <a-col :xs="12" :sm="6">
        <a-card>
          <a-statistic
            title="处理率"
            :value="handleRate"
            :precision="1"
            suffix="%"
            :value-style="{ color: '#3f8600' }"
            :prefix="h(CheckCircleOutlined)"
          />
        </a-card>
      </a-col>
    </a-row>

    <!-- 图表区域 -->
    <a-row :gutter="16" style="margin-bottom: 16px">
      <a-col :xs="24" :lg="12">
        <a-card title="学员增长趋势">
          <div ref="studentChart" style="height: 300px">
            <div style="height: 100%; background: linear-gradient(to top, #e6f7ff, #fff); border-radius: 8px; display: flex; align-items: flex-end; padding: 20px;">
              <div v-for="(item, index) in studentTrend" :key="index" style="flex: 1; margin: 0 4px;">
                <a-tooltip :title="`${item.month}: ${item.count}人`">
                  <div :style="{height: item.count * 2 + 'px', background: 'linear-gradient(to top, #1890ff, #4096ff)', borderRadius: '4px 4px 0 0'}"></div>
                </a-tooltip>
                <div style="text-align: center; margin-top: 8px; font-size: 12px; color: #8c8c8c">{{ item.month }}</div>
              </div>
            </div>
          </div>
        </a-card>
      </a-col>

      <a-col :xs="24" :lg="12">
        <a-card title="违规类型分布">
          <div style="height: 300px; display: flex; align-items: center; justify-content: center;">
            <div>
              <div style="width: 200px; height: 200px; border-radius: 50%; background: conic-gradient(#ff4d4f 0% 30%, #fa8c16 30% 50%, #faad14 50% 70%, #1890ff 70% 85%, #52c41a 85% 100%); margin: 0 auto 16px;"></div>
              <a-space wrap style="justify-content: center; width: 100%">
                <a-tag color="red">非本校学员 30%</a-tag>
                <a-tag color="orange">无教练练车 20%</a-tag>
                <a-tag color="gold">禁止时段 20%</a-tag>
                <a-tag color="blue">私带学员 15%</a-tag>
                <a-tag color="green">其他 15%</a-tag>
              </a-space>
            </div>
          </div>
        </a-card>
      </a-col>
    </a-row>

    <a-row :gutter="16" style="margin-bottom: 16px">
      <a-col :xs="24" :lg="12">
        <a-card title="教练教学排行">
          <a-table
            :columns="coachRankColumns"
            :data-source="coachRankData"
            :pagination="false"
            size="small"
          >
            <template #bodyCell="{ column, record, index }">
              <template v-if="column.key === 'rank'">
                <a-tag v-if="index === 0" color="gold">🥇</a-tag>
                <a-tag v-else-if="index === 1" color="silver">🥈</a-tag>
                <a-tag v-else-if="index === 2" color="#cd7f32">🥉</a-tag>
                <span v-else>{{ index + 1 }}</span>
              </template>
            </template>
          </a-table>
        </a-card>
      </a-col>

      <a-col :xs="24" :lg="12">
        <a-card title="车辆使用率">
          <a-table
            :columns="carUsageColumns"
            :data-source="carUsageData"
            :pagination="false"
            size="small"
          >
            <template #bodyCell="{ column, record }">
              <template v-if="column.key === 'usageRate'">
                <a-progress
                  :percent="record.usageRate"
                  :status="record.usageRate > 80 ? 'success' : 'active'"
                  size="small"
                />
              </template>
            </template>
          </a-table>
        </a-card>
      </a-col>
    </a-row>

    <!-- 详细报表 -->
    <a-card title="详细数据报表">
      <a-table
        :columns="detailColumns"
        :data-source="detailData"
        :loading="loading"
        :pagination="pagination"
      >
        <template #title>
          <a-space>
            <a-button @click="loadData">
              <template #icon><ReloadOutlined /></template>
              刷新数据
            </a-button>
          </a-space>
        </template>
      </a-table>
    </a-card>
  </div>
</template>

<script setup>
import { ref, h } from 'vue'
import { message } from 'ant-design-vue'
import {
  DownloadOutlined,
  UserOutlined,
  TeamOutlined,
  AlertOutlined,
  CheckCircleOutlined,
  ReloadOutlined
} from '@ant-design/icons-vue'

const loading = ref(false)
const dateRange = ref([])

const totalStudents = ref(1256)
const totalCoaches = ref(45)
const totalViolations = ref(328)
const handleRate = ref(87.5)

const studentTrend = ref([
  { month: '1月', count: 45 },
  { month: '2月', count: 62 },
  { month: '3月', count: 78 },
  { month: '4月', count: 95 },
  { month: '5月', count: 110 },
  { month: '6月', count: 125 }
])

const coachRankColumns = [
  { title: '排名', key: 'rank', width: 60 },
  { title: '教练', dataIndex: 'name', key: 'name' },
  { title: '学员数', dataIndex: 'studentCount', key: 'studentCount', sorter: true },
  { title: '通过率', dataIndex: 'passRate', key: 'passRate' }
]

const coachRankData = ref([
  { key: '1', name: '李教练', studentCount: 32, passRate: '92%' },
  { key: '2', name: '王教练', studentCount: 28, passRate: '88%' },
  { key: '3', name: '张教练', studentCount: 26, passRate: '85%' },
  { key: '4', name: '赵教练', studentCount: 24, passRate: '82%' },
  { key: '5', name: '刘教练', studentCount: 22, passRate: '80%' }
])

const carUsageColumns = [
  { title: '车牌号', dataIndex: 'plateNumber', key: 'plateNumber' },
  { title: '类型', dataIndex: 'carType', key: 'carType' },
  { title: '使用率', dataIndex: 'usageRate', key: 'usageRate' }
]

const carUsageData = ref([
  { key: '1', plateNumber: '京A12345', carType: '小型汽车', usageRate: 92 },
  { key: '2', plateNumber: '京A67890', carType: '自动挡', usageRate: 88 },
  { key: '3', plateNumber: '京A11111', carType: '小型汽车', usageRate: 75 },
  { key: '4', plateNumber: '京A22222', carType: '大型货车', usageRate: 68 },
  { key: '5', plateNumber: '京A33333', carType: '小型汽车', usageRate: 55 }
])

const detailColumns = [
  { title: '日期', dataIndex: 'date', key: 'date' },
  { title: '新增学员', dataIndex: 'newStudents', key: 'newStudents' },
  { title: '结业学员', dataIndex: 'graduates', key: 'graduates' },
  { title: '违规次数', dataIndex: 'violations', key: 'violations' },
  { title: '处理次数', dataIndex: 'handled', key: 'handled' },
  { title: '教练出勤', dataIndex: 'coachAttendance', key: 'coachAttendance' },
  { title: '车辆使用', dataIndex: 'carUsage', key: 'carUsage' }
]

const detailData = ref([
  { key: '1', date: '2024-03-25', newStudents: 12, graduates: 8, violations: 5, handled: 4, coachAttendance: '42/45', carUsage: '23/25' },
  { key: '2', date: '2024-03-24', newStudents: 15, graduates: 10, violations: 8, handled: 7, coachAttendance: '43/45', carUsage: '22/25' },
  { key: '3', date: '2024-03-23', newStudents: 18, graduates: 12, violations: 6, handled: 6, coachAttendance: '44/45', carUsage: '24/25' },
  { key: '4', date: '2024-03-22', newStudents: 10, graduates: 6, violations: 4, handled: 4, coachAttendance: '41/45', carUsage: '21/25' },
  { key: '5', date: '2024-03-21', newStudents: 14, graduates: 9, violations: 7, handled: 5, coachAttendance: '42/45', carUsage: '22/25' }
])

const pagination = ref({
  current: 1,
  pageSize: 10,
  total: 30
})

const exportReport = () => {
  message.loading('正在生成报表...', 2).then(() => {
    message.success('报表导出成功')
  })
}

const loadData = () => {
  loading.value = true
  setTimeout(() => {
    loading.value = false
    message.success('数据已刷新')
  }, 1000)
}
</script>

<style scoped>
.report-center {
  padding: 0;
}
</style>
