<template>
  <div class="dashboard">
    <el-row :gutter="20" class="stats-row">
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon bg-blue">
              <User />
            </div>
            <div class="stat-info">
              <div class="stat-number">{{ stats.studentCount }}</div>
              <div class="stat-label">在读学员</div>
            </div>
          </div>
        </el-card>
      </el-col>
      
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon bg-green">
              <UserFilled />
            </div>
            <div class="stat-info">
              <div class="stat-number">{{ stats.coachCount }}</div>
              <div class="stat-label">在职教练</div>
            </div>
          </div>
        </el-card>
      </el-col>
      
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon bg-orange">
              <Van />
            </div>
            <div class="stat-info">
              <div class="stat-number">{{ stats.carCount }}</div>
              <div class="stat-label">可用车辆</div>
            </div>
          </div>
        </el-card>
      </el-col>
      
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon bg-red">
              <Bell />
            </div>
            <div class="stat-info">
              <div class="stat-number">{{ stats.warningCount }}</div>
              <div class="stat-label">今日预警</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" class="charts-row">
      <el-col :span="12">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>违规类型统计</span>
            </div>
          </template>
          <div class="chart-placeholder">
            <el-empty description="违规类型统计图表" />
          </div>
        </el-card>
      </el-col>
      
      <el-col :span="12">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>近7日违规趋势</span>
            </div>
          </template>
          <div class="chart-placeholder">
            <el-empty description="违规趋势图表" />
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20">
      <el-col :span="24">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>最新违规记录</span>
              <el-button link type="primary">查看更多</el-button>
            </div>
          </template>
          <el-table :data="recentRecords" style="width: 100%">
            <el-table-column prop="time" label="时间" width="180" />
            <el-table-column prop="type" label="违规类型" width="120" />
            <el-table-column prop="person" label="涉及人员" width="120" />
            <el-table-column prop="location" label="地点" />
            <el-table-column prop="status" label="处理状态" width="100">
              <template #default="{ row }">
                <el-tag :type="getStatusType(row.status)">
                  {{ row.status }}
                </el-tag>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { User, UserFilled, Van, Bell } from '@element-plus/icons-vue'

const stats = ref({
  studentCount: 156,
  coachCount: 23,
  carCount: 18,
  warningCount: 5
})

const recentRecords = ref([
  {
    time: '2024-01-15 14:30:22',
    type: '无预约练车',
    person: '张三',
    location: '科目二训练场A区',
    status: '未处理'
  },
  {
    time: '2024-01-15 13:45:18',
    type: '非排班时间教学',
    person: '李教练',
    location: '科目三训练场B区',
    status: '已处理'
  },
  {
    time: '2024-01-15 11:20:05',
    type: '禁止时段练车',
    person: '王五',
    location: '科目二训练场C区',
    status: '未处理'
  }
])

const getStatusType = (status) => {
  const statusMap = {
    '未处理': 'danger',
    '已处理': 'success',
    '已追责': 'warning'
  }
  return statusMap[status] || 'info'
}

onMounted(() => {
  // 可以在这里调用API获取统计数据
})
</script>

<style scoped>
.dashboard {
  padding: 20px;
}

.stats-row {
  margin-bottom: 20px;
}

.stat-card {
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.stat-content {
  display: flex;
  align-items: center;
}

.stat-icon {
  width: 60px;
  height: 60px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 20px;
}

.stat-icon :deep(svg) {
  width: 24px;
  height: 24px;
  color: white;
}

.bg-blue {
  background-color: #409eff;
}

.bg-green {
  background-color: #67c23a;
}

.bg-orange {
  background-color: #e6a23c;
}

.bg-red {
  background-color: #f56c6c;
}

.stat-info {
  flex: 1;
}

.stat-number {
  font-size: 24px;
  font-weight: bold;
  color: #303133;
  margin-bottom: 5px;
}

.stat-label {
  font-size: 14px;
  color: #909399;
}

.charts-row {
  margin-bottom: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.chart-placeholder {
  height: 300px;
  display: flex;
  align-items: center;
  justify-content: center;
}
</style>