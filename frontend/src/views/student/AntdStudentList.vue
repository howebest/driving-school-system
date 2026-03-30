<template>
  <div class="student-management">
    <!-- 页面标题 -->
    <a-page-header
      title="学员管理"
      sub-title="管理驾校学员信息、状态和人脸绑定"
      style="padding: 0 0 16px 0"
    >
      <template #extra>
        <a-button type="primary" @click="showAddModal">
          <template #icon><PlusOutlined /></template>
          新增学员
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
                placeholder="姓名/身份证号/手机号"
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
                <a-select-option value="在读">在读</a-select-option>
                <a-select-option value="已结业">已结业</a-select-option>
                <a-select-option value="休学">休学</a-select-option>
              </a-select>
            </a-form-item>
          </a-col>

          <a-col :xs="24" :sm="12" :md="8" :lg="6">
            <a-form-item label="入学时间" style="width: 100%">
              <a-range-picker
                v-model:value="searchForm.dateRange"
                style="width: 100%"
              />
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
        :scroll="{ x: 1300 }"
        @change="handleTableChange"
      >
        <template #title>
          <a-space>
            <span>学员列表</span>
            <a-tag color="blue">{{ tableData.length }} 条记录</a-tag>
          </a-space>
        </template>

        <template #bodyCell="{ column, record }">
          <template v-if="column.key === 'name'">
            <a-space>
              <a-avatar :src="record.avatar" :size="32">
                {{ record.name.substring(0, 1) }}
              </a-avatar>
              <span>{{ record.name }}</span>
            </a-space>
          </template>

          <template v-else-if="column.key === 'status'">
            <a-tag v-if="record.status === '在读'" color="processing">
              {{ record.status }}
            </a-tag>
            <a-tag v-else-if="record.status === '已结业'" color="success">
              {{ record.status }}
            </a-tag>
            <a-tag v-else color="default">
              {{ record.status }}
            </a-tag>
          </template>

          <template v-else-if="column.key === 'faceBinding'">
            <a-tag v-if="record.faceBinding" color="success">
              <template #icon><CheckCircleOutlined /></template>
              已绑定
            </a-tag>
            <a-tag v-else color="error">
              <template #icon><CloseCircleOutlined /></template>
              未绑定
            </a-tag>
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
                @click="handleBindFace(record)"
              >
                <SafetyCertificateOutlined />
                绑定人脸
              </a-button>
              <a-popconfirm
                title="确定删除该学员吗？"
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

    <!-- 新增/编辑学员模态框 -->
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
            <a-form-item label="驾照类型" name="licenseType">
              <a-select v-model:value="formState.licenseType" placeholder="请选择驾照类型">
                <a-select-option value="C1">C1（小型汽车）</a-select-option>
                <a-select-option value="C2">C2（小型自动挡汽车）</a-select-option>
                <a-select-option value="B1">B1（中型客车）</a-select-option>
                <a-select-option value="B2">B2（大型货车）</a-select-option>
              </a-select>
            </a-form-item>
          </a-col>

          <a-col :span="12">
            <a-form-item label="报名日期" name="enrollmentDate">
              <a-date-picker
                v-model:value="formState.enrollmentDate"
                style="width: 100%"
              />
            </a-form-item>
          </a-col>

          <a-col :span="24">
            <a-form-item label="头像上传" name="avatar">
              <a-upload
                v-model:file-list="formState.avatarList"
                list-type="picture-card"
                :max-count="1"
                :before-upload="beforeUpload"
                @preview="handlePreview"
              >
                <div v-if="formState.avatarList.length < 1">
                  <PlusOutlined />
                  <div style="margin-top: 8px">上传头像</div>
                </div>
              </a-upload>
              <a-modal
                v-model:open="previewVisible"
                :footer="null"
                @cancel="previewVisible = false"
              >
                <img alt="预览" style="width: 100%" :src="previewImage" />
              </a-modal>
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
  SafetyCertificateOutlined,
  CheckCircleOutlined,
  CloseCircleOutlined
} from '@ant-design/icons-vue'
import dayjs from 'dayjs'

const loading = ref(false)
const modalVisible = ref(false)
const modalLoading = ref(false)
const modalTitle = ref('新增学员')
const previewVisible = ref(false)
const previewImage = ref('')

const searchForm = reactive({
  keyword: '',
  status: undefined,
  dateRange: null
})

const formRef = ref()
const formState = reactive({
  name: '',
  phone: '',
  idCard: '',
  gender: undefined,
  licenseType: undefined,
  enrollmentDate: null,
  notes: '',
  avatarList: []
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
  licenseType: [{ required: true, message: '请选择驾照类型', trigger: 'change' }],
  enrollmentDate: [{ required: true, message: '请选择报名日期', trigger: 'change' }]
}

const columns = [
  {
    title: 'ID',
    dataIndex: 'id',
    key: 'id',
    width: 60,
    fixed: 'left'
  },
  {
    title: '姓名',
    dataIndex: 'name',
    key: 'name',
    width: 150,
    fixed: 'left'
  },
  {
    title: '身份证号',
    dataIndex: 'idCard',
    key: 'idCard',
    width: 180
  },
  {
    title: '手机号',
    dataIndex: 'phone',
    key: 'phone',
    width: 130
  },
  {
    title: '入学日期',
    dataIndex: 'enrollmentDate',
    key: 'enrollmentDate',
    width: 120
  },
  {
    title: '状态',
    dataIndex: 'status',
    key: 'status',
    width: 100
  },
  {
    title: '人脸绑定',
    dataIndex: 'faceBinding',
    key: 'faceBinding',
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
    id: 1,
    name: '张三',
    idCard: '110101199001011234',
    phone: '13800138000',
    enrollmentDate: '2024-01-15',
    status: '在读',
    faceBinding: true,
    avatar: ''
  },
  {
    key: '2',
    id: 2,
    name: '李四',
    idCard: '110101199002021235',
    phone: '13800138001',
    enrollmentDate: '2023-12-20',
    status: '已结业',
    faceBinding: false,
    avatar: ''
  }
])

const pagination = ref({
  current: 1,
  pageSize: 10,
  total: 156,
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
  searchForm.dateRange = null
  message.success('已重置')
}

const handleTableChange = (pag) => {
  pagination.value.current = pag.current
  pagination.value.pageSize = pag.pageSize
}

const showAddModal = () => {
  modalTitle.value = '新增学员'
  modalVisible.value = true
  formState.enrollmentDate = dayjs()
}

const handleEdit = (record) => {
  modalTitle.value = '编辑学员'
  modalVisible.value = true
  // 填充表单数据
  Object.assign(formState, {
    ...record,
    enrollmentDate: record.enrollmentDate ? dayjs(record.enrollmentDate) : null
  })
}

const handleBindFace = (record) => {
  message.info(`为 ${record.name} 绑定人脸`)
}

const handleDelete = (record) => {
  message.success(`已删除学员: ${record.name}`)
}

const handleSubmit = async () => {
  try {
    await formRef.value.validate()
    modalLoading.value = true

    setTimeout(() => {
      modalVisible.value = false
      modalLoading.value = false
      message.success(modalTitle.value === '新增学员' ? '新增成功' : '编辑成功')
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
  formState.avatarList = []
}

const beforeUpload = (file) => {
  const isJpgOrPng = file.type === 'image/jpeg' || file.type === 'image/png'
  if (!isJpgOrPng) {
    message.error('只能上传 JPG/PNG 格式的图片!')
    return false
  }
  const isLt2M = file.size / 1024 / 1024 < 2
  if (!isLt2M) {
    message.error('图片大小不能超过 2MB!')
    return false
  }
  return false // 阻止自动上传，手动处理
}

const handlePreview = async (file) => {
  if (!file.url && !file.preview) {
    file.preview = await getBase64(file.originFileObj)
  }
  previewImage.value = file.url || file.preview
  previewVisible.value = true
}

const getBase64 = (file) => {
  return new Promise((resolve, reject) => {
    const reader = new FileReader()
    reader.readAsDataURL(file)
    reader.onload = () => resolve(reader.result)
    reader.onerror = error => reject(error)
  })
}
</script>

<style scoped>
.student-management {
  padding: 0;
}
</style>
