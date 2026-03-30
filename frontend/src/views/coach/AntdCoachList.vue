<template>
  <div class="coach-management">
    <!-- 页面标题 -->
    <a-page-header
      title="教练管理"
      sub-title="管理驾校教练信息、排班和教学情况"
      style="padding: 0 0 16px 0"
    >
      <template #extra>
        <a-button type="primary" @click="showAddModal">
          <template #icon><PlusOutlined /></template>
          新增教练
        </a-button>
      </template>
    </a-page-header>

    <!-- 搜索表单 -->
    <a-card style="margin-bottom: 16px">
      <a-form layout="inline" :model="searchForm">
        <a-row :gutter="16" style="width: 100%">
          <a-col :xs="24" :sm="12" :md="8" :lg="6">
            <a-form-item label="关键词" style="width: 100%">
              <a-input
                v-model:value="searchForm.keyword"
                placeholder="姓名/手机号/教练编号"
                allow-clear
              />
            </a-form-item>
          </a-col>

          <a-col :xs="24" :sm="12" :md="8" :lg="6">
            <a-form-item label="状态" style="width: 100%">
              <a-select
                v-model:value="searchForm.status"
                placeholder="请选择"
                allow-clear
              >
                <a-select-option value="在职">在职</a-select-option>
                <a-select-option value="离职">离职</a-select-option>
                <a-select-option value="休假">休假</a-select-option>
              </a-select>
            </a-form-item>
          </a-col>

          <a-col :xs="24" :sm="12" :md="8" :lg="6">
            <a-form-item label="教学科目" style="width: 100%">
              <a-select
                v-model:value="searchForm.subject"
                placeholder="请选择"
                allow-clear
              >
                <a-select-option value="科目二">科目二</a-select-option>
                <a-select-option value="科目三">科目三</a-select-option>
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

    <!-- 数据表格 -->
    <a-card>
      <a-table
        :columns="columns"
        :data-source="tableData"
        :loading="loading"
        :pagination="pagination"
        :scroll="{ x: 1400 }"
        @change="handleTableChange"
      >
        <template #title>
          <a-space>
            <span>教练列表</span>
            <a-tag color="blue">{{ tableData.length }} 条记录</a-tag>
          </a-space>
        </template>

        <template #bodyCell="{ column, record }">
          <template v-if="column.key === 'name'">
            <a-space>
              <a-avatar :src="record.avatar" :size="32">
                {{ record.name.substring(0, 1) }}
              </a-avatar>
              <div>
                <div>{{ record.name }}</div>
                <div style="font-size: 12px; color: #8c8c8c">{{ record.coachNo }}</div>
              </div>
            </a-space>
          </template>

          <template v-else-if="column.key === 'status'">
            <a-badge
              :status="record.status === '在职' ? 'success' : record.status === '离职' ? 'default' : 'warning'"
              :text="record.status"
            />
          </template>

          <template v-else-if="column.key === 'subject'">
            <a-tag v-if="record.subject === '科目二'" color="blue">
              {{ record.subject }}
            </a-tag>
            <a-tag v-else-if="record.subject === '科目三'" color="green">
              {{ record.subject }}
            </a-tag>
            <a-tag v-else color="purple">
              {{ record.subject }}
            </a-tag>
          </template>

          <template v-else-if="column.key === 'teachingProgress'">
            <a-progress
              :percent="record.teachingProgress"
              :status="record.teachingProgress === 100 ? 'success' : 'active'"
              size="small"
            />
          </template>

          <template v-else-if="column.key === 'schedule'">
            <a-button type="link" size="small" @click="showSchedule(record)">
              <CalendarOutlined />
              查看排班
            </a-button>
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
                @click="showSchedule(record)"
              >
                <CalendarOutlined />
                排班
              </a-button>
              <a-popconfirm
                title="确定删除该教练吗？"
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

    <!-- 新增/编辑教练模态框 -->
    <a-modal
      v-model:open="modalVisible"
      :title="modalTitle"
      width="800px"
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
        <a-row :gutter="16">
          <a-col :span="12">
            <a-form-item label="姓名" name="name">
              <a-input v-model:value="formState.name" placeholder="请输入姓名" />
            </a-form-item>
          </a-col>

          <a-col :span="12">
            <a-form-item label="手机号" name="phone">
              <a-input v-model:value="formState.phone" placeholder="请输入手机号" />
            </a-form-item>
          </a-col>

          <a-col :span="12">
            <a-form-item label="身份证号" name="idCard">
              <a-input v-model:value="formState.idCard" placeholder="请输入身份证号" />
            </a-form-item>
          </a-col>

          <a-col :span="12">
            <a-form-item label="性别" name="gender">
              <a-select v-model:value="formState.gender" placeholder="请选择性别">
                <a-select-option value="男">男</a-select-option>
                <a-select-option value="女">女</a-select-option>
              </a-select>
            </a-form-item>
          </a-col>

          <a-col :span="12">
            <a-form-item label="教学科目" name="subject">
              <a-select v-model:value="formState.subject" placeholder="请选择教学科目" mode="multiple">
                <a-select-option value="科目二">科目二</a-select-option>
                <a-select-option value="科目三">科目三</a-select-option>
              </a-select>
            </a-form-item>
          </a-col>

          <a-col :span="12">
            <a-form-item label="入职日期" name="hireDate">
              <a-date-picker
                v-model:value="formState.hireDate"
                style="width: 100%"
              />
            </a-form-item>
          </a-col>

          <a-col :span="12">
            <a-form-item label="教练编号" name="coachNo">
              <a-input v-model:value="formState.coachNo" placeholder="系统自动生成" disabled />
            </a-form-item>
          </a-col>

          <a-col :span="12">
            <a-form-item label="状态" name="status">
              <a-select v-model:value="formState.status" placeholder="请选择状态">
                <a-select-option value="在职">在职</a-select-option>
                <a-select-option value="休假">休假</a-select-option>
                <a-select-option value="离职">离职</a-select-option>
              </a-select>
            </a-form-item>
          </a-col>

          <a-col :span="24">
            <a-form-item label="备注" name="notes">
              <a-textarea
                v-model:value="formState.notes"
                placeholder="请输入备注信息"
                :rows="4"
              />
            </a-form-item>
          </a-col>
        </a-row>
      </a-form>
    </a-modal>

    <!-- 排班模态框 -->
    <a-modal
      v-model:open="scheduleVisible"
      title="教练排班"
      width="900px"
      :footer="null"
    >
      <a-calendar :fullscreen="false" @select="onSelectDate">
        <template #dateFullCellRender="{ current }">
          <div class="date-cell">
            {{ current.date() }}
          </div>
        </template>
      </a-calendar>
      <a-divider />
      <a-form :label-col="{ span: 4 }" :wrapper-col="{ span: 20 }">
        <a-form-item label="选择时段">
          <a-checkbox-group v-model:value="selectedTimeSlots">
            <a-row>
              <a-col :span="8">
                <a-checkbox value="08:00-10:00">08:00-10:00</a-checkbox>
              </a-col>
              <a-col :span="8">
                <a-checkbox value="10:00-12:00">10:00-12:00</a-checkbox>
              </a-col>
              <a-col :span="8">
                <a-checkbox value="14:00-16:00">14:00-16:00</a-checkbox>
              </a-col>
              <a-col :span="8">
                <a-checkbox value="16:00-18:00">16:00-18:00</a-checkbox>
              </a-col>
            </a-row>
          </a-checkbox-group>
        </a-form-item>
        <a-form-item :wrapper-col="{ offset: 4, span: 20 }">
          <a-button type="primary" @click="saveSchedule">保存排班</a-button>
        </a-form-item>
      </a-form>
    </a-modal>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { message } from 'ant-design-vue'
import {
  PlusOutlined,
  SearchOutlined,
  ReloadOutlined,
  EditOutlined,
  DeleteOutlined,
  CalendarOutlined
} from '@ant-design/icons-vue'
import dayjs from 'dayjs'

const loading = ref(false)
const modalVisible = ref(false)
const modalLoading = ref(false)
const modalTitle = ref('新增教练')
const scheduleVisible = ref(false)
const selectedTimeSlots = ref([])

const searchForm = reactive({
  keyword: '',
  status: undefined,
  subject: undefined
})

const formRef = ref()
const formState = reactive({
  name: '',
  phone: '',
  idCard: '',
  gender: undefined,
  subject: [],
  hireDate: null,
  coachNo: '',
  status: '在职',
  notes: ''
})

const rules = {
  name: [{ required: true, message: '请输入姓名', trigger: 'blur' }],
  phone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '手机号格式不正确', trigger: 'blur' }
  ],
  idCard: [
    { required: true, message: '请输入身份证号', trigger: 'blur' },
    { pattern: /^[1-9]\d{5}(18|19|20)\d{2}((0[1-9])|(1[0-2]))(([0-2][1-9])|10|20|30|31)\d{3}[0-9Xx]$/, message: '身份证号格式不正确', trigger: 'blur' }
  ],
  gender: [{ required: true, message: '请选择性别', trigger: 'change' }],
  subject: [{ required: true, message: '请选择教学科目', trigger: 'change' }],
  hireDate: [{ required: true, message: '请选择入职日期', trigger: 'change' }]
}

const columns = [
  {
    title: '教练信息',
    dataIndex: 'name',
    key: 'name',
    width: 200,
    fixed: 'left'
  },
  {
    title: '手机号',
    dataIndex: 'phone',
    key: 'phone',
    width: 130
  },
  {
    title: '教学科目',
    dataIndex: 'subject',
    key: 'subject',
    width: 120
  },
  {
    title: '学员数量',
    dataIndex: 'studentCount',
    key: 'studentCount',
    width: 100
  },
  {
    title: '教学进度',
    dataIndex: 'teachingProgress',
    key: 'teachingProgress',
    width: 150
  },
  {
    title: '排班情况',
    key: 'schedule',
    width: 120
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
    name: '李教练',
    coachNo: 'JL20240001',
    phone: '13900139000',
    subject: '科目二',
    studentCount: 15,
    teachingProgress: 75,
    status: '在职',
    avatar: ''
  },
  {
    key: '2',
    name: '王教练',
    coachNo: 'JL20240002',
    phone: '13900139001',
    subject: '科目三',
    studentCount: 12,
    teachingProgress: 60,
    status: '在职',
    avatar: ''
  }
])

const pagination = ref({
  current: 1,
  pageSize: 10,
  total: 45,
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
  searchForm.keyword = ''
  searchForm.status = undefined
  searchForm.subject = undefined
  message.success('已重置')
}

const handleTableChange = (pag) => {
  pagination.value.current = pag.current
  pagination.value.pageSize = pag.pageSize
}

const showAddModal = () => {
  modalTitle.value = '新增教练'
  modalVisible.value = true
  formState.coachNo = 'JL' + Date.now()
  formState.hireDate = dayjs()
}

const handleEdit = (record) => {
  modalTitle.value = '编辑教练'
  modalVisible.value = true
  Object.assign(formState, {
    ...record,
    subject: record.subject.split(','),
    hireDate: record.hireDate ? dayjs(record.hireDate) : null
  })
}

const showSchedule = (record) => {
  scheduleVisible.value = true
  message.info(`查看 ${record.name} 的排班`)
}

const onSelectDate = (date) => {
  message.info(`选择日期: ${date.format('YYYY-MM-DD')}`)
}

const saveSchedule = () => {
  message.success('排班保存成功')
  scheduleVisible.value = false
}

const handleDelete = (record) => {
  message.success(`已删除教练: ${record.name}`)
}

const handleSubmit = async () => {
  try {
    await formRef.value.validate()
    modalLoading.value = true

    setTimeout(() => {
      modalVisible.value = false
      modalLoading.value = false
      message.success(modalTitle.value === '新增教练' ? '新增成功' : '编辑成功')
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
.coach-management {
  padding: 0;
}

.date-cell {
  padding: 4px;
  text-align: center;
}
</style>
