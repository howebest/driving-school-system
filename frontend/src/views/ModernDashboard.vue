<template>
  <div class="modern-dashboard">
    <!-- 数据概览卡片 -->
    <div class="stats-grid">
      <div class="stat-card" v-for="stat in statsData" :key="stat.key" :class="stat.colorClass">
        <div class="stat-icon">
          <component :is="stat.icon" />
        </div>
        <div class="stat-content">
          <div class="stat-value">{{ stat.value }}</div>
          <div class="stat-label">{{ stat.label }}</div>
          <div class="stat-trend" :class="stat.trendClass">
            <el-icon><ArrowUp v-if="stat.trend > 0" /><ArrowDown v-else /></el-icon>
            <span>{{ Math.abs(stat.trend) }}%</span>
          </div>
        </div>
      </div>
    </div>

    <!-- 图表区域 -->
    <div class="charts-section">
      <el-row :gutter="20">
        <el-col :span="16">
          <el-card class="chart-card">
            <template #header>
              <div class="card-header">
                <h3>违规趋势分析</h3>
                <el-select v-model="timeRange" size="small" style="width: 120px;">
                  <el-option label="本周" value="week" />
                  <el-option label="本月" value="month" />
                  <el-option label="本季度" value="quarter" />
                </el-select>
              </div>
            </template>
            <div class="chart-placeholder">
              <div class="chart-container">
                <div class="trend-chart">
                  <div class="chart-grid">
                    <div v-for="i in 7" :key="i" class="grid-line"></div>
                  </div>
                  <div class="trend-line">
                    <div 
                      v-for="(point, index) in trendData" 
                      :key="index" 
                      class="data-point"
                      :style="{ height: point.height + '%' }"
                    ></div>
                  </div>
                </div>
              </div>
            </div>
          </el-card>
        </el-col>
        
        <el-col :span="8">
          <el-card class="chart-card">
            <template #header>
              <div class="card-header">
                <h3>违规类型分布</h3>
              </div>
            </template>
            <div class="pie-chart-placeholder">
              <div class="pie-chart">
                <div class="pie-segment" v-for="(item, index) in pieData" :key="index" 
                     :style="{ '--percentage': item.percentage + '%', '--color': item.color }">
                  <div class="segment-label">{{ item.label }}</div>
                </div>
              </div>
              <div class="pie-legend">
                <div v-for="item in pieData" :key="item.label" class="legend-item">
                  <div class="legend-color" :style="{ backgroundColor: item.color }"></div>
                  <span>{{ item.label }} {{ item.percentage }}%</span>
                </div>
              </div>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </div>

    <!-- 最新活动 -->
    <div class="activity-section">
      <el-row :gutter="20">
        <el-col :span="12">
          <el-card class="activity-card">
            <template #header>
              <div class="card-header">
                <h3>最新违规记录</h3>
                <el-link type="primary" :underline="false">查看全部</el-link>
              </div>
            </template>
            <div class="activity-list">
              <div v-for="record in recentRecords" :key="record.id" class="activity-item">
                <div class="activity-icon" :class="record.typeClass">
                  <component :is="record.icon" />
                </div>
                <div class="activity-content">
                  <div class="activity-title">{{ record.title }}</div>
                  <div class="activity-desc">{{ record.desc }}</div>
                  <div class="activity-time">{{ record.time }}</div>
                </div>
                <div class="activity-status">
                  <el-tag :type="record.statusType" size="small">{{ record.status }}</el-tag>
                </div>
              </div>
            </div>
          </el-card>
        </el-col>
        
        <el-col :span="12">
          <el-card class="activity-card">
            <template #header>
              <div class="card-header">
                <h3>系统状态监控</h3>
                <el-tag type="success">正常运行</el-tag>
              </div>
            </template>
            <div class="system-status">
              <div v-for="system in systemStatus" :key="system.name" class="status-item">
                <div class="status-info">
                  <div class="status-name">{{ system.name }}</div>
                  <div class="status-desc">{{ system.desc }}</div>
                </div>
                <div class="status-indicator">
                  <div class="indicator" :class="system.status"></div>
                  <span class="status-text">{{ system.statusText }}</span>
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
import { ref } from 'vue'
import { 
  User, UserFilled, Van, Bell,
  ArrowUp, ArrowDown,
  Warning, Clock, Location, Camera
} from '@element-plus/icons-vue'

const timeRange = ref('week')

const statsData = ref([
  {
    key: 'students',
    value: 156,
    label: '在读学员',
    icon: User,
    colorClass: 'blue',
    trend: 12.5,
    trendClass: 'positive'
  },
  {
    key: 'coaches',
    value: 23,
    label: '在职教练',
    icon: UserFilled,
    colorClass: 'green',
    trend: 4.2,
    trendClass: 'positive'
  },
  {
    key: 'cars',
    value: 18,
    label: '可用车辆',
    icon: Van,
    colorClass: 'orange',
    trend: -2.1,
    trendClass: 'negative'
  },
  {
    key: 'warnings',
    value: 5,
    label: '今日预警',
    icon: Bell,
    colorClass: 'red',
    trend: -15.8,
    trendClass: 'positive'
  }
])

const trendData = ref([
  { height: 30 }, { height: 45 }, { height: 25 }, 
  { height: 60 }, { height: 35 }, { height: 70 }, { height: 40 }
])

const pieData = ref([
  { label: '无预约练车', percentage: 35, color: '#ef4444' },
  { label: '非排班教学', percentage: 25, color: '#f59e0b' },
  { label: '禁止时段', percentage: 20, color: '#8b5cf6' },
  { label: '其他违规', percentage: 20, color: '#3b82f6' }
])

const recentRecords = ref([
  {
    id: 1,
    title: '学员张三无预约练车',
    desc: '科目二训练场A区',
    time: '2小时前',
    status: '待处理',
    statusType: 'danger',
    icon: Warning,
    typeClass: 'warning'
  },
  {
    id: 2,
    title: '教练李四非排班时间教学',
    desc: '科目三训练场B区',
    time: '3小时前',
    status: '已处理',
    statusType: 'success',
    icon: Clock,
    typeClass: 'info'
  },
  {
    id: 3,
    title: '外来人员进入场地',
    desc: '入口摄像头检测',
    time: '5小时前',
    status: '已记录',
    statusType: 'info',
    icon: Camera,
    typeClass: 'secondary'
  }
])

const systemStatus = ref([
  {
    name: '人脸识别服务',
    desc: '百度AI接口',
    status: 'online',
    statusText: '正常'
  },
  {
    name: '车牌识别服务',
    desc: '阿里云OCR',
    status: 'online',
    statusText: '正常'
  },
  {
    name: '数据库连接',
    desc: 'MySQL 8.0',
    status: 'online',
    statusText: '正常'
  },
  {
    name: '缓存服务',
    desc: 'Redis 6.0',
    status: 'online',
    statusText: '正常'
  }
])
</script>

<style scoped>
.modern-dashboard {
  padding: 24px;
  background: #f8fafc;
  min-height: calc(100vh - 116px);
}

/* 统计卡片网格 */
.stats-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
  gap: 20px;
  margin-bottom: 24px;
}

.stat-card {
  background: white;
  border-radius: 16px;
  padding: 24px;
  display: flex;
  align-items: center;
  gap: 20px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
  transition: all 0.3s ease;
  border: 1px solid #e2e8f0;
}

.stat-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.1);
}

.stat-card.blue {
  border-left: 4px solid #3b82f6;
}

.stat-card.green {
  border-left: 4px solid #10b981;
}

.stat-card.orange {
  border-left: 4px solid #f59e0b;
}

.stat-card.red {
  border-left: 4px solid #ef4444;
}

.stat-icon {
  width: 56px;
  height: 56px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
  color: white;
}

.stat-card.blue .stat-icon {
  background: linear-gradient(135deg, #3b82f6, #60a5fa);
}

.stat-card.green .stat-icon {
  background: linear-gradient(135deg, #10b981, #34d399);
}

.stat-card.orange .stat-icon {
  background: linear-gradient(135deg, #f59e0b, #fbbf24);
}

.stat-card.red .stat-icon {
  background: linear-gradient(135deg, #ef4444, #f87171);
}

.stat-content {
  flex: 1;
}

.stat-value {
  font-size: 28px;
  font-weight: 700;
  color: #1e293b;
  margin-bottom: 4px;
}

.stat-label {
  font-size: 14px;
  color: #64748b;
  margin-bottom: 8px;
}

.stat-trend {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 13px;
  font-weight: 500;
}

.stat-trend.positive {
  color: #10b981;
}

.stat-trend.negative {
  color: #ef4444;
}

/* 图表区域 */
.charts-section {
  margin-bottom: 24px;
}

.chart-card {
  border-radius: 16px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
  border: 1px solid #e2e8f0;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.card-header h3 {
  margin: 0;
  font-size: 18px;
  font-weight: 600;
  color: #1e293b;
}

.chart-placeholder {
  height: 320px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.chart-container {
  width: 100%;
  height: 280px;
  position: relative;
}

.trend-chart {
  position: relative;
  width: 100%;
  height: 100%;
}

.chart-grid {
  position: absolute;
  width: 100%;
  height: 100%;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
}

.grid-line {
  height: 1px;
  background: linear-gradient(90deg, #e2e8f0, transparent);
}

.trend-line {
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  height: 80%;
  display: flex;
  align-items: flex-end;
  justify-content: space-around;
  padding: 0 20px;
}

.data-point {
  width: 8px;
  background: linear-gradient(to top, #3b82f6, #60a5fa);
  border-radius: 4px 4px 0 0;
  min-height: 20px;
  transition: height 0.5s ease;
}

.pie-chart-placeholder {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 20px;
}

.pie-chart {
  width: 180px;
  height: 180px;
  position: relative;
  border-radius: 50%;
  background: conic-gradient(
    var(--color) 0% var(--percentage),
    #e2e8f0 var(--percentage) 100%
  );
}

.pie-segment {
  position: absolute;
  width: 100%;
  height: 100%;
  border-radius: 50%;
  clip-path: polygon(50% 50%, 50% 0%, 100% 0%, 100% 100%, 50% 100%);
  transform: rotate(calc(var(--percentage) * 3.6deg));
}

.segment-label {
  position: absolute;
  top: 50%;
  left: 70%;
  transform: translateY(-50%);
  font-size: 12px;
  font-weight: 500;
  color: #1e293b;
  white-space: nowrap;
}

.pie-legend {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.legend-item {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 14px;
  color: #64748b;
}

.legend-color {
  width: 12px;
  height: 12px;
  border-radius: 50%;
}

/* 活动区域 */
.activity-section {
  margin-bottom: 24px;
}

.activity-card {
  border-radius: 16px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
  border: 1px solid #e2e8f0;
  height: 100%;
}

.activity-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.activity-item {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 16px;
  border-radius: 12px;
  background: #f8fafc;
  transition: all 0.2s ease;
}

.activity-item:hover {
  background: #f1f5f9;
  transform: translateX(4px);
}

.activity-icon {
  width: 40px;
  height: 40px;
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 18px;
}

.activity-icon.warning {
  background: linear-gradient(135deg, #ef4444, #f87171);
}

.activity-icon.info {
  background: linear-gradient(135deg, #3b82f6, #60a5fa);
}

.activity-icon.secondary {
  background: linear-gradient(135deg, #64748b, #94a3b8);
}

.activity-content {
  flex: 1;
}

.activity-title {
  font-weight: 600;
  color: #1e293b;
  margin-bottom: 4px;
}

.activity-desc {
  font-size: 14px;
  color: #64748b;
  margin-bottom: 4px;
}

.activity-time {
  font-size: 12px;
  color: #94a3b8;
}

.activity-status {
  flex-shrink: 0;
}

.system-status {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.status-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px;
  background: #f8fafc;
  border-radius: 12px;
}

.status-info {
  flex: 1;
}

.status-name {
  font-weight: 600;
  color: #1e293b;
  margin-bottom: 4px;
}

.status-desc {
  font-size: 14px;
  color: #64748b;
}

.status-indicator {
  display: flex;
  align-items: center;
  gap: 8px;
}

.indicator {
  width: 12px;
  height: 12px;
  border-radius: 50%;
}

.indicator.online {
  background: #10b981;
  box-shadow: 0 0 0 3px rgba(16, 185, 129, 0.3);
}

.indicator.offline {
  background: #ef4444;
  box-shadow: 0 0 0 3px rgba(239, 68, 68, 0.3);
}

.status-text {
  font-size: 14px;
  font-weight: 500;
  color: #64748b;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .modern-dashboard {
    padding: 16px;
  }
  
  .stats-grid {
    grid-template-columns: 1fr;
  }
  
  .stat-card {
    padding: 20px;
  }
  
  .activity-item {
    padding: 12px;
  }
}
</style>