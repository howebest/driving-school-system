<template>
  <div class="car-management">
    <!-- 页面标题 -->
    <a-page-header
      title="车辆管理"
      sub-title="管理驾校教学车辆信息和状态"
      style="padding: 0 0 16px 0"
    >
      <template #extra>
        <a-button type="primary" @click="showAddModal">
          <template #icon><PlusOutlined /></template>
          新增车辆
        </a-button>
      </template>
    </a-page-header>

    <!-- 搜索表单 -->
    <a-card style="margin-bottom: 16px">
      <a-form layout="inline" :model="searchForm">
        <a-row :gutter="16" style="width: 100%">
          <a-col :xs="24" :sm="12" :md="8" :lg="6">
            <a-form-item label="车牌号" style="width: 100%">
              <a-input
                v-model:value="searchForm.plateNumber"
                placeholder="请输入车牌号"
                allow-clear
              />
            </a-form-item>
          </a-col>

          <a-col :xs="24" :sm="12" :md="8" :lg="6">
            <a-form-item label="车辆类型" style="width: 100%">
              <a-select
                v-model:value="searchForm.carType"
                placeholder="请选择"
                allow-clear
              >
                <a-select-option value="小型汽车">小型汽车</a-select-option>
                <a-select-option value="小型自动挡汽车">小型自动挡汽车</a-select-option>
                <a-select-option value="大型货车">大型货车</a-select-option>
              </a-select>
            </a-form-item>
          </a-col>

          <a-col :xs="24" :sm="12" :md="8" :lg="6">
            <a-form-item label="状态" style="width: 100%">
              <a-select
                v-model:value="searchForm.status"
                placeholder="请选择"
                allow-clear
              >
                <a-select-option value="使用中">使用中</a-select-option>
                <a-select-option value="空闲">空闲</a-select-option>
                <a-select-option value="维修">维修</a-select-option>
              </a-select>
            </a-form-item>
          </a-col>

          <a-col :xs="24" :sm="12" :md="24" :lg="6">
            <a-space>
              <a-button type="primary" @click="handleSearch">
                <template #icon><SearchOutlined /></template>
                搜索
              </a-button>
              <a-button @click="handleReset">
                <template #icon><ReloadOutlined /></template>
                重置
              </a-button>
            </a-space>
          </a-col>
        </a-row>
      </a-form>
    </a-card>

    <!-- 统计卡片 -->
    <a-row :gutter="16" style="margin-bottom: 16px">
      <a-col :xs="12" :sm="6">
        <a-card>
          <a-statistic
            title="总车辆数"
            :value="totalCount"
            :prefix="h(CarOutlined)"
          />
        </a-card>
      </a-col>
      <a-col :xs="12" :sm="6">
        <a-card>
          <a-statistic
            title="使用中"
            :value="inUseCount"
            :value-style="{ color: '#3f8600' }"
            :prefix="h(CheckCircleOutlined)"
          />
        </a-card>
      </a-col>
      <a-col :xs="12" :sm="6">
        <a-card>
          <a-statistic
            title="空闲"
            :value="idleCount"
            :value-style="{ color: '#1890ff' }"
            :prefix="h(ClockCircleOutlined)"
          />
        </a-card>
      </a-col>
      <a-col :xs="12" :sm="6">
        <a-card>
          <a-statistic
            title="维修中"
            :value="maintenanceCount"
            :value-style="{ color: '#cf1322' }"
            :prefix="h(ToolOutlined)"
          />
        </a-card>
      </a-col>
    </a-row>

    <!-- 数据表格 -->
    <a-card>
      <a-table
        :columns="columns"
        :data-source="tableData"
        :loading="loading"
        :pagination="pagination"
        :scroll="{ x: 1200 }"
        @change="handleTableChange"
      >
        <template #title>
          <a-space>
            <span>车辆列表</span>
            <a-tag color="blue">{{ tableData.length }} 条记录</a-tag>
          </a-space>
        </template>

        <template #bodyCell="{ column, record }">
          <template v-if="column.key === 'plateNumber'">
            <a-space>
              <CarOutlined style="color: #1890ff" />
              <span style="font-weight: 500">{{ record.plateNumber }}</span>
            </a-space>
          </template>

          <template v-else-if="column.key === 'status'">
            <a-tag v-if="record.status === '使用中'" color="success">
              <template #icon><CheckCircleOutlined /></template>
              {{ record.status }}
            </a-tag>
            <a-tag v-else-if="record.status === '空闲'" color="processing">
              <template #icon><ClockCircleOutlined /></template>
              {{ record.status }}
            </a-tag>
            <a-tag v-else-if="record.status === '维修'" color="error">
              <template #icon><ToolOutlined /></template>
              {{ record.status }}
            </a-tag>
          </template>

          <template v-else-if="column.key === 'location'">
            <a-space>
              <EnvironmentOutlined />
              <span>{{ record.location }}</span>
            </a-space>
          </template>

          <template v-else-if="column.key === 'currentCoach'">
            <a-space v-if="record.currentCoach">
              <a-avatar :size="24">{{ record.currentCoach.substring(0, 1) }}</a-avatar>
              <span>{{ record.currentCoach }}</span>
            </a-space>
            <span v-else style="color: #8c8c8c">暂无</span>
          </template>

          <template v-else-if="column.key === 'action'">
            <a-space>
              <a-button
                type="link"
                size="small"
                @click="handleEdit(record)"
              >
                <EditOutlined />
                编辑
              </a-button>
              <a-button
                type="link"
                size="small"
                @click="showLocation(record)"
              >
                <EnvironmentOutlined />
                定位
              </a-button>
              <a-popconfirm
                title="确定删除该车辆吗？"
                ok-text="确定"
                cancel-text="取消"
                @confirm="handleDelete(record)"
              >
                <a-button type="link" danger size="small">
                  <DeleteOutlined />
                  删除
                </a-button>
              </a-popconfirm>
            </a-space>
          </template>
        </template>
      </a-table>
    </a-card>

    <!-- 新增/编辑车辆模态框 -->
    <a-modal
      v-model:open="modalVisible"
      :title="modalTitle"
      width="700px"
      :confirm-loading="modalLoading"
      @ok="handleSubmit"
      @cancel="handleCancel"
    >
      <a-form
        ref="formRef"
        :model="formState"
        :rules="rules"
        :label-col="{ span: 6 }"
        :wrapper-col="{ span: 16 }"
      >
        <a-form-item label="车牌号" name="plateNumber">
          <a-input v-model:value="formState.plateNumber" placeholder="请输入车牌号" />
        </a-form-item>

        <a-form-item label="车辆类型" name="carType">
          <a-select v-model:value="formState.carType" placeholder="请选择车辆类型">
            <a-select-option value="小型汽车">小型汽车（C1）</a-select-option>
            <a-select-option value="小型自动挡汽车">小型自动挡汽车（C2）</a-select-option>
            <a-select-option value="大型货车">大型货车（B2）</a-select-option>
          </a-select>
        </a-form-item>

        <a-form-item label="品牌型号" name="brand">
          <a-input v-model:value="formState.brand" placeholder="请输入品牌型号" />
        </a-form-item>

        <a-form-item label="购买日期" name="purchaseDate">
          <a-date-picker
            v-model:value="formState.purchaseDate"
            style="width: 100%"
          />
        </a-form-item>

        <a-form-item label="当前状态" name="status">
          <a-radio-group v-model:value="formState.status">
            <a-radio value="空闲">空闲</a-radio>
            <a-radio value="使用中">使用中</a-radio>
            <a-radio value="维修">维修</a-radio>
          </a-radio-group>
        </a-form-item>

        <a-form-item label="所属教练" name="currentCoach">
          <a-select
            v-model:value="formState.currentCoach"
            placeholder="请选择教练（可选）"
            allow-clear
          >
            <a-select-option value="李教练">李教练</a-select-option>
            <a-select-option value="王教练">王教练</a-select-option>
            <a-select-option value="张教练">张教练</a-select-option>
          </a-select>
        </a-form-item>

        <a-form-item label="当前位置" name="location">
          <a-input v-model:value="formState.location" placeholder="请输入当前位置" />
        </a-form-item>

        <a-form-item label="备注" name="notes">
          <a-textarea
            v-model:value="formState.notes"
            placeholder="请输入备注信息"
            :rows="4"
          />
        </a-form-item>
      </a-form>
    </a-modal>
  </div>
</template>

<script setup>
import { ref, reactive, h } from 'vue'
import { message } from 'ant-design-vue'
import {
  PlusOutlined,
  SearchOutlined,
  ReloadOutlined,
  EditOutlined,
  DeleteOutlined,
  CarOutlined,
  CheckCircleOutlined,
  ClockCircleOutlined,
  ToolOutlined,
  EnvironmentOutlined
} from '@ant-design/icons-vue'

const loading = ref(false)
const modalVisible = ref(false)
const modalLoading = ref(false)
const modalTitle = ref('新增车辆')

const totalCount = ref(25)
const inUseCount = ref(18)
const idleCount = ref(5)
const maintenanceCount = ref(2)

const searchForm = reactive({
  plateNumber: '',
  carType: undefined,
  status: undefined
})

const formRef = ref()
const formState = reactive({
  plateNumber: '',
  carType: undefined,
  brand: '',
  purchaseDate: null,
  status: '空闲',
  currentCoach: undefined,
  location: '',
  notes: ''
})

const rules = {
  plateNumber: [
    { required: true, message: '请输入车牌号', trigger: 'blur' },
    { pattern: /^[京津沪渝冀豫云辽黑湘皖鲁新苏浙赣鄂桂甘晋蒙陕吉闽贵粤青藏川宁琼使领A-Z][A-Z][A-Z0-9]{5,6}$/, message: '车牌号格式不正确', trigger: 'blur' }
  ],
  carType: [{ required: true, message: '请选择车辆类型', trigger: 'change' }],
  status: [{ required: true, message: '请选择状态', trigger: 'change' }]
}

const columns = [
  {
    title: '车牌号',
    dataIndex: 'plateNumber',
    key: 'plateNumber',
    width: 150,
    fixed: 'left'
  },
  {
    title: '车辆类型',
    dataIndex: 'carType',
    key: 'carType',
    width: 150
  },
  {
    title: '品牌型号',
    dataIndex: 'brand',
    key: 'brand',
    width: 150
  },
  {
    title: '当前教练',
    dataIndex: 'currentCoach',
    key: 'currentCoach',
    width: 120
  },
  {
    title: '当前位置',
    dataIndex: 'location',
    key: 'location',
    width: 150
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
    width: 200,
    fixed: 'right'
  }
]

const tableData = ref([
  {
    key: '1',
    plateNumber: '京A12345',
    carType: '小型汽车',
    brand: '大众捷达',
    currentCoach: '李教练',
    location: '科二倒车入库区域',
    status: '使用中'
  },
  {
    key: '2',
    plateNumber: '京A67890',
    carType: '小型自动挡汽车',
    brand: '丰田卡罗拉',
    currentCoach: '王教练',
    location: '科三路线1',
    status: '使用中'
  },
  {
    key: '3',
    plateNumber: '京A11111',
    carType: '小型汽车',
    brand: '大众捷达',
    currentCoach: null,
    location: '停车场A区',
    status: '空闲'
  },
  {
    key: '4',
    plateNumber: '京A22222',
    carType: '大型货车',
    brand: '东风货车',
    currentCoach: null,
    location: '维修车间',
    status: '维修'
  }
])

const pagination = ref({
  current: 1,
  pageSize: 10,
  total: 25,
  showSizeChanger: true,
  showQuickJumper: true,
  showTotal: (total) => `共 ${total} 条记录`
})

const handleSearch = () => {
  loading.value = true
  setTimeout(() => {
    loading.value = false
    message.success('搜索完成')
  }, 500)
}

const handleReset = () => {
  searchForm.plateNumber = ''
  searchForm.carType = undefined
  searchForm.status = undefined
  message.success('已重置')
}

const handleTableChange = (pag) => {
  pagination.value.current = pag.current
  pagination.value.pageSize = pag.pageSize
}

const showAddModal = () => {
  modalTitle.value = '新增车辆'
  modalVisible.value = true
}

const handleEdit = (record) => {
  modalTitle.value = '编辑车辆'
  modalVisible.value = true
  Object.assign(formState, record)
}

const showLocation = (record) => {
  message.info(`查看车辆 ${record.plateNumber} 的位置`)
}

const handleDelete = (record) => {
  message.success(`已删除车辆: ${record.plateNumber}`)
}

const handleSubmit = async () => {
  try {
    await formRef.value.validate()
    modalLoading.value = true

    setTimeout(() => {
      modalVisible.value = false
      modalLoading.value = false
      message.success(modalTitle.value === '新增车辆' ? '新增成功' : '编辑成功')
      resetForm()
    }, 1000)
  } catch (error) {
    console.error('Validation failed:', error)
  }
}

const handleCancel = () => {
  modalVisible.value = false
  resetForm()
}

const resetForm = () => {
  formRef.value?.resetFields()
}
</script>

<style scoped>
.car-management {
  padding: 0;
}
</style>
