<template>
  <div class="report-center">
    <el-tabs v-model="activeTab">
      <el-tab-pane label="违规统计" name="violation">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-card>
              <template #header>
                <div class="card-header">
                  <span>违规类型分布</span>
                </div>
              </template>
              <div class="chart-container">
                <el-empty description="违规类型统计图表" />
              </div>
            </el-card>
          </el-col>
          <el-col :span="12">
            <el-card>
              <template #header>
                <div class="card-header">
                  <span>违规趋势分析</span>
                </div>
              </template>
              <div class="chart-container">
                <el-empty description="违规趋势图表" />
              </div>
            </el-card>
          </el-col>
        </el-row>
      </el-tab-pane>
      
      <el-tab-pane label="教练排行" name="coach">
        <el-card>
          <el-table :data="coachRank" border stripe>
            <el-table-column prop="rank" label="排名" width="80" />
            <el-table-column prop="name" label="教练姓名" width="120" />
            <el-table-column prop="violationCount" label="违规次数" width="120" />
            <el-table-column prop="score" label="评分" width="100" />
          </el-table>
        </el-card>
      </el-tab-pane>
      
      <el-tab-pane label="高发区域" name="area">
        <el-card>
          <div class="chart-container">
            <el-empty description="高发区域统计图表" />
          </div>
        </el-card>
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<script setup>
import { ref } from 'vue'

const activeTab = ref('violation')

const coachRank = ref([
  { rank: 1, name: '王教练', violationCount: 5, score: 85 },
  { rank: 2, name: '李教练', violationCount: 3, score: 92 }
])
</script>

<style scoped>
.report-center {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.chart-container {
  height: 300px;
  display: flex;
  align-items: center;
  justify-content: center;
}
</style>