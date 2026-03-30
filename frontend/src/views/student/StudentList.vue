<template>
  <div class="student-list">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>学员管理</span>
          <el-button type="primary" @click="handleAdd">新增学员</el-button>
        </div>
      </template>

      <!-- 搜索条件 -->
      <el-form :model="searchForm" inline class="search-form">
        <el-form-item label="关键词">
          <el-input
            v-model="searchForm.keyword"
            placeholder="姓名/身份证号/手机号"
            clearable
          />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="searchForm.status" placeholder="请选择状态" clearable>
            <el-option label="在读" :value="1" />
            <el-option label="已结业" :value="2" />
            <el-option label="无效" :value="3" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>

      <!-- 学员列表 -->
      <el-table :data="studentList" border stripe style="width: 100%">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="studentName" label="姓名" width="120" />
        <el-table-column prop="idCard" label="身份证号" width="180" />
        <el-table-column prop="phone" label="手机号" width="120" />
        <el-table-column prop="enrollmentDate" label="入学日期" width="120">
          <template #default="{ row }">
            {{ formatDate(row.enrollmentDate) }}
          </template>
        </el-table-column>
        <el-table-column prop="studentStatus" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.studentStatus)">
              {{ getStatusText(row.studentStatus) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="人脸绑定" width="100">
          <template #default="{ row }">
            <el-tag :type="row.faceImageUrl ? 'success' : 'danger'">
              {{ row.faceImageUrl ? '已绑定' : '未绑定' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" size="small" @click="handleEdit(row)">
              编辑
            </el-button>
            <el-button link type="primary" size="small" @click="handleBindFace(row)">
              人脸绑定
            </el-button>
            <el-button link type="danger" size="small" @click="handleDelete(row)">
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="pagination">
        <el-pagination
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          :page-sizes="[10, 20, 50, 100]"
          :total="total"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-card>

    <!-- 新增/编辑对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="500px"
      @close="handleDialogClose"
    >
      <el-form
        ref="formRef"
        :model="formData"
        :rules="formRules"
        label-width="100px"
      >
        <el-form-item label="学员姓名" prop="studentName">
          <el-input v-model="formData.studentName" placeholder="请输入学员姓名" />
        </el-form-item>
        <el-form-item label="身份证号" prop="idCard">
          <el-input v-model="formData.idCard" placeholder="请输入身份证号" />
        </el-form-item>
        <el-form-item label="手机号" prop="phone">
          <el-input v-model="formData.phone" placeholder="请输入手机号" />
        </el-form-item>
        <el-form-item label="状态" prop="studentStatus">
          <el-select v-model="formData.studentStatus" placeholder="请选择状态">
            <el-option label="在读" :value="1" />
            <el-option label="已结业" :value="2" />
            <el-option label="无效" :value="3" />
          </el-select>
        </el-form-item>
        <el-form-item label="备注">
          <el-input
            v-model="formData.remark"
            type="textarea"
            placeholder="请输入备注"
            :rows="3"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleSubmit">确定</el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 人脸绑定对话框 -->
    <el-dialog
      v-model="faceDialogVisible"
      title="人脸绑定"
      width="400px"
    >
      <div class="face-bind-content">
        <div class="camera-preview">
          <el-empty description="摄像头预览区域" />
        </div>
        <div class="face-actions">
          <el-button type="primary">拍照</el-button>
          <el-button>上传照片</el-button>
        </div>
        <div v-if="selectedFaceImage" class="preview-image">
          <img :src="selectedFaceImage" alt="人脸照片" />
        </div>
      </div>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="faceDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="confirmFaceBind">确定绑定</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'

const studentList = ref([])
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)
const dialogVisible = ref(false)
const faceDialogVisible = ref(false)
const dialogTitle = ref('')
const formRef = ref()
const selectedStudent = ref(null)
const selectedFaceImage = ref('')

const searchForm = reactive({
  keyword: '',
  status: null
})

const formData = reactive({
  id: null,
  studentName: '',
  idCard: '',
  phone: '',
  studentStatus: 1,
  remark: ''
})

const formRules = {
  studentName: [
    { required: true, message: '请输入学员姓名', trigger: 'blur' }
  ],
  idCard: [
    { required: true, message: '请输入身份证号', trigger: 'blur' },
    { pattern: /(^\d{15}$)|(^\d{18}$)|(^\d{17}(\d|X|x)$)/, message: '身份证号格式不正确', trigger: 'blur' }
  ],
  phone: [
    { pattern: /^1[3-9]\d{9}$/, message: '手机号格式不正确', trigger: 'blur' }
  ]
}

const getStatusType = (status) => {
  const typeMap = { 1: 'primary', 2: 'success', 3: 'danger' }
  return typeMap[status] || 'info'
}

const getStatusText = (status) => {
  const textMap = { 1: '在读', 2: '已结业', 3: '无效' }
  return textMap[status] || '未知'
}

const formatDate = (date) => {
  if (!date) return ''
  return new Date(date).toLocaleDateString()
}

const loadStudentList = () => {
  // 模拟API调用
  studentList.value = [
    {
      id: 1,
      studentName: '张三',
      idCard: '110101199001011234',
      phone: '13800138000',
      studentStatus: 1,
      faceImageUrl: '',
      enrollmentDate: '2024-01-01'
    },
    {
      id: 2,
      studentName: '李四',
      idCard: '110101199002021235',
      phone: '13800138001',
      studentStatus: 2,
      faceImageUrl: 'face.jpg',
      enrollmentDate: '2023-12-01',
      graduationDate: '2024-01-10'
    }
  ]
  total.value = studentList.value.length
}

const handleSearch = () => {
  loadStudentList()
}

const handleReset = () => {
  searchForm.keyword = ''
  searchForm.status = null
  loadStudentList()
}

const handleAdd = () => {
  dialogTitle.value = '新增学员'
  Object.assign(formData, {
    id: null,
    studentName: '',
    idCard: '',
    phone: '',
    studentStatus: 1,
    remark: ''
  })
  dialogVisible.value = true
}

const handleEdit = (row) => {
  dialogTitle.value = '编辑学员'
  Object.assign(formData, row)
  dialogVisible.value = true
}

const handleDelete = (row) => {
  ElMessageBox.confirm(`确定要删除学员 "${row.studentName}" 吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    ElMessage.success('删除成功')
    loadStudentList()
  })
}

const handleBindFace = (row) => {
  selectedStudent.value = row
  faceDialogVisible.value = true
}

const handleSubmit = async () => {
  if (!formRef.value) return
  
  await formRef.value.validate((valid) => {
    if (valid) {
      ElMessage.success(formData.id ? '编辑成功' : '新增成功')
      dialogVisible.value = false
      loadStudentList()
    }
  })
}

const confirmFaceBind = () => {
  if (!selectedFaceImage.value) {
    ElMessage.warning('请先拍照或上传照片')
    return
  }
  ElMessage.success('人脸绑定成功')
  faceDialogVisible.value = false
  selectedFaceImage.value = ''
  loadStudentList()
}

const handleDialogClose = () => {
  formRef.value?.resetFields()
}

const handleSizeChange = (val) => {
  pageSize.value = val
  loadStudentList()
}

const handleCurrentChange = (val) => {
  currentPage.value = val
  loadStudentList()
}

onMounted(() => {
  loadStudentList()
})
</script>

<style scoped>
.student-list {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.search-form {
  margin-bottom: 20px;
  padding: 20px;
  background-color: #f5f7fa;
  border-radius: 4px;
}

.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}

.face-bind-content {
  text-align: center;
}

.camera-preview {
  height: 200px;
  margin-bottom: 20px;
}

.face-actions {
  margin-bottom: 20px;
}

.preview-image img {
  max-width: 100%;
  max-height: 200px;
  border: 1px solid #dcdfe6;
  border-radius: 4px;
}
</style>