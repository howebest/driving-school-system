<template>
  <div class="monitor-center">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>违规监测中心</span>
          <el-switch
            v-model="monitorEnabled"
            active-text="开启监测"
            inactive-text="关闭监测"
          />
        </div>
      </template>
      
      <div class="monitor-grid">
        <div class="camera-item" v-for="camera in cameras" :key="camera.id">
          <el-card>
            <template #header>
              <div class="camera-header">
                <span>{{ camera.name }}</span>
                <el-tag :type="camera.status === 'online' ? 'success' : 'danger'">
                  {{ camera.status === 'online' ? '在线' : '离线' }}
                </el-tag>
              </div>
            </template>
            <div class="camera-preview">
              <el-empty description="实时监控画面" />
            </div>
          </el-card>
        </div>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref } from 'vue'

const monitorEnabled = ref(true)

const cameras = ref([
  { id: 1, name: '入口摄像头1', status: 'online' },
  { id: 2, name: '科目二A区', status: 'online' },
  { id: 3, name: '科目三B区', status: 'offline' },
  { id: 4, name: '出口摄像头', status: 'online' }
])
</script>

<style scoped>
.monitor-center {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.monitor-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 20px;
}

.camera-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.camera-preview {
  height: 200px;
  display: flex;
  align-items: center;
  justify-content: center;
}
</style>