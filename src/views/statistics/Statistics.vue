<template>
  <div class="statistics-report">
    <el-row :gutter="20">
      <el-col :span="24">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>统计报表</span>
              <el-button type="primary" @click="exportReport">导出报表</el-button>
            </div>
          </template>
          
          <el-tabs v-model="activeTab">
            <el-tab-pane label="违规统计" name="violation">
              <div ref="violationChart" style="height: 400px;"></div>
            </el-tab-pane>
            <el-tab-pane label="人员统计" name="person">
              <div ref="personChart" style="height: 400px;"></div>
            </el-tab-pane>
            <el-tab-pane label="车辆统计" name="vehicle">
              <div ref="vehicleChart" style="height: 400px;"></div>
            </el-tab-pane>
          </el-tabs>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted, nextTick } from 'vue'
import { ElMessage } from 'element-plus'
import * as echarts from 'echarts'

const activeTab = ref('violation')
const violationChart = ref()
const personChart = ref()
const vehicleChart = ref()

const exportReport = () => {
  ElMessage.success('报表导出成功')
}

const initCharts = () => {
  nextTick(() => {
    // 违规统计图
    const violationChartInstance = echarts.init(violationChart.value)
    violationChartInstance.setOption({
      title: { text: '违规类型统计' },
      tooltip: {},
      xAxis: {
        type: 'category',
        data: ['越界行驶', '超速行驶', '违规停车', '其他']
      },
      yAxis: { type: 'value' },
      series: [{
        data: [35, 25, 20, 15],
        type: 'bar'
      }]
    })
    
    // 人员统计图
    const personChartInstance = echarts.init(personChart.value)
    personChartInstance.setOption({
      title: { text: '教练违规次数排行' },
      tooltip: {},
      xAxis: {
        type: 'category',
        data: ['李教练', '王教练', '张教练']
      },
      yAxis: { type: 'value' },
      series: [{
        data: [12, 8, 5],
        type: 'bar'
      }]
    })
    
    // 车辆统计图
    const vehicleChartInstance = echarts.init(vehicleChart.value)
    vehicleChartInstance.setOption({
      title: { text: '车辆使用统计' },
      tooltip: {},
      legend: { top: 'bottom' },
      series: [{
        type: 'pie',
        radius: '50%',
        data: [
          { value: 35, name: '粤A12345' },
          { value: 25, name: '粤B67890' },
          { value: 20, name: '粤C11111' },
          { value: 20, name: '其他车辆' }
        ]
      }]
    })
    
    window.addEventListener('resize', () => {
      violationChartInstance.resize()
      personChartInstance.resize()
      vehicleChartInstance.resize()
    })
  })
}

onMounted(() => {
  initCharts()
})
</script>

<style scoped>
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>