<template>
  <div class="feishu-page-container">
    <!-- 页面头部 -->
    <div class="feishu-page-header">
      <div class="feishu-header-left">
        <h2>学员管理</h2>
        <span class="feishu-page-subtitle">管理驾校学员信息、状态和人脸绑定</span>
      </div>
      <div class="feishu-header-right">
        <button 
          class="feishu-btn feishu-btn-primary"
          @click="handleAdd"
        >
          <i class="el-icon-plus"></i>
          新增学员
        </button>
        <button 
          class="feishu-btn feishu-btn-secondary"
          @click="handleRefresh"
        >
          <i class="el-icon-refresh"></i>
          刷新
        </button>
      </div>
    </div>

    <!-- 搜索区域 -->
    <div class="feishu-search-section">
      <div class="feishu-search-row">
        <input
          v-model="searchForm.keyword"
          class="feishu-input"
          placeholder="姓名/身份证号/手机号"
          @keyup.enter="handleSearch"
        />
        <select
          v-model="searchForm.status"
          class="feishu-select"
          @change="handleSearch"
        >
          <option value="">学员状态</option>
          <option value="active">在读</option>
          <option value="graduated">已结业</option>
          <option value="dropout">退学</option>
        </select>
        <button 
          class="feishu-btn feishu-btn-primary"
          @click="handleSearch"
        >
          <i class="el-icon-search"></i>
          搜索
        </button>
        <button 
          class="feishu-btn feishu-btn-secondary"
          @click="handleReset"
        >
          重置
        </button>
      </div>
    </div>

    <!-- 统计卡片 -->
    <div class="feishu-stats-cards">
      <div class="feishu-stats-row">
        <div class="feishu-stat-card">
          <div class="feishu-stat-icon">
            <i class="el-icon-user"></i>
          </div>
          <div class="feishu-stat-content">
            <div class="feishu-stat-value">{{ stats.total }}</div>
            <div class="feishu-stat-label">学员总数</div>
          </div>
        </div>
        <div class="feishu-stat-card">
          <div class="feishu-stat-icon" style="background: #2e7d32;">
            <i class="el-icon-check"></i>
          </div>
          <div class="feishu-stat-content">
            <div class="feishu-stat-value">{{ stats.active }}</div>
            <div class="feishu-stat-label">在读学员</div>
          </div>
        </div>
        <div class="feishu-stat-card">
          <div class="feishu-stat-icon" style="background: #1565c0;">
            <i class="el-icon-camera"></i>
          </div>
          <div class="feishu-stat-content">
            <div class="feishu-stat-value">{{ stats.faceBound }}</div>
            <div class="feishu-stat-label">人脸绑定</div>
          </div>
        </div>
        <div class="feishu-stat-card">
          <div class="feishu-stat-icon" style="background: #f57c00;">
            <i class="el-icon-trophy"></i>
          </div>
          <div class="feishu-stat-content">
            <div class="feishu-stat-value">{{ stats.graduationRate }}%</div>
            <div class="feishu-stat-label">结业率</div>
          </div>
        </div>
      </div>
    </div>

    <!-- 表格区域 -->
    <div class="feishu-table-section">
      <div class="feishu-table-toolbar">
        <div class="feishu-toolbar-left">
          <span class="feishu-table-title">学员列表 [共 {{ pagination.total }} 条记录]</span>
        </div>
        <div class="feishu-toolbar-right">
          <button 
            class="feishu-btn feishu-btn-secondary feishu-btn-small"
            @click="handleRefresh"
          >
            <i class="el-icon-refresh"></i>
            刷新
          </button>
          <button 
            class="feishu-btn feishu-btn-secondary feishu-btn-small"
            @click="handleExport"
          >
            <i class="el-icon-download"></i>
            导出
          </button>
        </div>
      </div>
      
      <div class="feishu-table-container">
        <table class="feishu-table">
          <thead>
            <tr>
              <th width="50">
                <input type="checkbox" v-model="selectAll" @change="handleSelectAll">
              </th>
              <th width="60">ID</th>
              <th width="120">姓名</th>
              <th width="180">身份证号</th>
              <th width="120">手机号</th>
              <th width="110">入学日期</th>
              <th width="90">状态</th>
              <th width="110">人脸绑定</th>
              <th width="150">操作</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="item in studentList" :key="item.id" @click="handleRowClick(item)">
              <td>
                <input type="checkbox" :value="item.id" v-model="selectedIds">
              </td>
              <td>{{ item.id }}</td>
              <td>
                <div class="feishu-avatar-cell">
                  <div class="feishu-avatar">{{ item.name.charAt(0) }}</div>
                  <span>{{ item.name }}</span>
                </div>
              </td>
              <td>{{ item.idCard }}</td>
              <td>{{ item.phone }}</td>
              <td>{{ item.enrollDate }}</td>
              <td>
                <span :class="getStatusTagClass(item.status)">
                  {{ getStatusText(item.status) }}
                </span>
              </td>
              <td>
                <span :class="getFaceTagClass(item.faceStatus)">
                  {{ item.faceStatus }}
                </span>
              </td>
              <td>
                <div class="feishu-action-buttons">
                  <button 
                    class="feishu-btn feishu-btn-text feishu-btn-small"
                    @click.stop="handleEdit(item)"
                  >
                    编辑
                  </button>
                  <button 
                    class="feishu-btn feishu-btn-text feishu-btn-small"
                    @click.stop="handleBindFace(item)"
                  >
                    绑定
                  </button>
                  <button 
                    class="feishu-btn feishu-btn-text feishu-btn-small"
                    @click.stop="handleDelete(item)"
                  >
                    删除
                  </button>
                </div>
              </td>
            </tr>
          </tbody>
        </table>
        
        <div v-if="studentList.length === 0" class="feishu-empty">
          <i class="el-icon-document" style="font-size: 48px; margin-bottom: 16px;"></i>
          <p>暂无数据</p>
        </div>
      </div>

      <!-- 分页 -->
      <div class="feishu-pagination">
        <div class="feishu-pagination-info">
          显示第 {{ (pagination.currentPage - 1) * pagination.pageSize + 1 }} -
          {{ Math.min(pagination.currentPage * pagination.pageSize, pagination.total) }} 条，
          共 {{ pagination.total }} 条记录
        </div>
        <div class="feishu-pagination-controls">
          <button 
            class="feishu-btn feishu-btn-secondary feishu-btn-small"
            :disabled="pagination.currentPage === 1"
            @click="handlePrevPage"
          >
            上一页
          </button>
          <span class="feishu-pagination-current">
            第 {{ pagination.currentPage }} 页
          </span>
          <button 
            class="feishu-btn feishu-btn-secondary feishu-btn-small"
            :disabled="pagination.currentPage * pagination.pageSize >= pagination.total"
            @click="handleNextPage"
          >
            下一页
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'

const loading = ref(false)
const selectAll = ref(false)
const selectedIds = ref([])

// 搜索表单
const searchForm = reactive({
  keyword: '',
  status: ''
})

// 统计数据
const stats = reactive({
  total: 156,
  active: 124,
  faceBound: 98,
  graduationRate: 85
})

// 分页数据
const pagination = reactive({
  currentPage: 1,
  pageSize: 20,
  total: 156
})

// 学员列表数据
const studentList = ref([
  {
    id: 1,
    name: '张三',
    idCard: '110101199001011234',
    phone: '13800138000',
    enrollDate: '2024-01-15',
    status: 'active',
    faceStatus: '✓ 已绑定'
  },
  {
    id: 2,
    name: '李四',
    idCard: '110101199002021235',
    phone: '13800138001',
    enrollDate: '2023-12-20',
    status: 'graduated',
    faceStatus: '× 未绑定'
  }
])

// 计算属性
const getStatusTagClass = (status) => {
  const classMap = {
    active: 'feishu-tag feishu-tag-primary',
    graduated: 'feishu-tag feishu-tag-success',
    dropout: 'feishu-tag feishu-tag-danger'
  }
  return classMap[status] || 'feishu-tag feishu-tag-info'
}

const getStatusText = (status) => {
  const textMap = {
    active: '在读',
    graduated: '已结业',
    dropout: '退学'
  }
  return textMap[status] || status
}

const getFaceTagClass = (faceStatus) => {
  return faceStatus.includes('已绑定') 
    ? 'feishu-tag feishu-tag-success' 
    : 'feishu-tag feishu-tag-danger'
}

// 方法
const handleSearch = () => {
  loading.value = true
  setTimeout(() => {
    loading.value = false
    ElMessage.success('搜索完成')
  }, 500)
}

const handleReset = () => {
  searchForm.keyword = ''
  searchForm.status = ''
  handleSearch()
}

const handleRefresh = () => {
  loading.value = true
  setTimeout(() => {
    loading.value = false
    ElMessage.success('刷新成功')
  }, 800)
}

const handleExport = () => {
  ElMessage.success('正在导出数据...')
}

const handleAdd = () => {
  ElMessage.info('新增学员功能')
}

const handleEdit = (row) => {
  ElMessage.info(`编辑学员: ${row.name}`)
}

const handleBindFace = (row) => {
  ElMessage.info(`为人脸绑定: ${row.name}`)
}

const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm(
      `确定要删除学员 "${row.name}" 吗？`,
      '删除确认',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    ElMessage.success('删除成功')
    handleRefresh()
  } catch {
    // 用户取消删除
  }
}

const handleSelectAll = () => {
  if (selectAll.value) {
    selectedIds.value = studentList.value.map(item => item.id)
  } else {
    selectedIds.value = []
  }
}

const handleRowClick = (row) => {
  console.log('点击行:', row)
}

const handlePrevPage = () => {
  if (pagination.currentPage > 1) {
    pagination.currentPage--
    handleRefresh()
  }
}

const handleNextPage = () => {
  if (pagination.currentPage * pagination.pageSize < pagination.total) {
    pagination.currentPage++
    handleRefresh()
  }
}
</script>

<style scoped>
@import '@/styles/feishu-ui-library.css';

.feishu-search-row {
  display: flex;
  gap: var(--feishu-spacing-sm);
  align-items: center;
}

.feishu-stats-row {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(240px, 1fr));
  gap: var(--feishu-spacing-md);
}

.feishu-table-container {
  overflow-x: auto;
}

.feishu-avatar-cell {
  display: flex;
  align-items: center;
  gap: var(--feishu-spacing-xs);
}

.feishu-avatar {
  width: 28px;
  height: 28px;
  border-radius: 50%;
  background: var(--feishu-primary);
  color: white;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 12px;
  font-weight: 500;
}

.feishu-action-buttons {
  display: flex;
  gap: var(--feishu-spacing-xs);
}

.feishu-pagination {
  display: flex;
  justify-content: space-between;
  align-items: center;
  flex-wrap: wrap;
  gap: var(--feishu-spacing-sm);
}

.feishu-pagination-info {
  font-size: var(--feishu-font-size-small);
  color: var(--feishu-gray-5);
}

.feishu-pagination-controls {
  display: flex;
  align-items: center;
  gap: var(--feishu-spacing-xs);
}

.feishu-pagination-current {
  padding: 0 var(--feishu-spacing-sm);
  font-size: var(--feishu-font-size-small);
  color: var(--feishu-gray-6);
}
</style>