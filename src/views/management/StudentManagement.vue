<template>
  <div class="student-management">
    <!-- 筛选面板 -->
    <el-card class="filter-card">
      <el-form :model="filterForm" inline>
        <el-form-item label="姓名">
          <el-input
            v-model="filterForm.name"
            placeholder="请输入姓名"
            clearable
          />
        </el-form-item>
        
        <el-form-item label="身份证号">
          <el-input
            v-model="filterForm.idCard"
            placeholder="请输入身份证号"
            clearable
          />
        </el-form-item>
        
        <el-form-item label="状态">
          <el-select v-model="filterForm.status" placeholder="请选择状态" clearable>
            <el-option label="在读" value="studying" />
            <el-option label="已结业" value="graduated" />
            <el-option label="无效" value="invalid" />
          </el-select>
        </el-form-item>
        
        <el-form-item>
          <el-button type="primary" @click="handleFilter">筛选</el-button>
          <el-button @click="resetFilter">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>
    
    <!-- 操作栏 -->
    <el-card class="operation-card">
      <div class="operation-bar">
        <div class="left-buttons">
          <el-button
            v-if="hasPermission('edit_student')"
            type="primary"
            @click="handleAddStudent"
          >
            <el-icon><Plus /></el-icon>
            新增学员
          </el-button>
          <el-button
            v-if="hasPermission('edit_student')"
            @click="handleBatchDelete"
            :disabled="selectedStudents.length === 0"
          >
            <el-icon><Delete /></el-icon>
            批量删除
          </el-button>
          <el-button @click="handleRefresh">
            <el-icon><Refresh /></el-icon>
            刷新
          </el-button>
        </div>
        
        <div class="right-info">
          <span>共 {{ total }} 条记录</span>
        </div>
      </div>
    </el-card>
    
    <!-- 学员列表 -->
    <el-card>
      <el-table
        :data="studentList"
        style="width: 100%"
        @selection-change="handleSelectionChange"
        v-loading="loading"
      >
        <el-table-column type="selection" width="55" />
        <el-table-column type="index" label="序号" width="60" />
        <el-table-column prop="name" label="姓名" width="100" />
        <el-table-column prop="idCard" label="身份证号" width="180" />
        <el-table-column prop="phone" label="联系电话" width="120" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-select
              v-if="hasPermission('edit_student')"
              v-model="row.status"
              size="small"
              @change="handleStatusChange(row)"
            >
              <el-option label="在读" value="studying" />
              <el-option label="已结业" value="graduated" />
              <el-option label="无效" value="invalid" />
            </el-select>
            <el-tag
              v-else
              :type="getStatusType(row.status)"
              disable-transitions
            >
              {{ getStatusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="coachName" label="所属教练" width="100" />
        <el-table-column prop="enrollDate" label="报名日期" width="120" />
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button
              v-if="hasPermission('view_student')"
              type="primary"
              link
              size="small"
              @click="handleView(row)"
            >
              查看
            </el-button>
            <el-button
              v-if="hasPermission('edit_student')"
              type="primary"
              link
              size="small"
              @click="handleEdit(row)"
            >
              编辑
            </el-button>
            <el-button
              v-if="hasPermission('edit_student')"
              type="danger"
              link
              size="small"
              @click="handleDelete(row)"
            >
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
          :page-sizes="[10, 20, 50]"
          :total="total"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-card>
    
    <!-- 新增/编辑学员弹窗 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="600px"
      @close="handleDialogClose"
    >
      <el-form
        ref="studentFormRef"
        :model="studentForm"
        :rules="studentRules"
        label-width="100px"
      >
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="姓名" prop="name">
              <el-input v-model="studentForm.name" placeholder="请输入姓名" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="性别" prop="gender">
              <el-select v-model="studentForm.gender" placeholder="请选择性别" style="width: 100%">
                <el-option label="男" value="male" />
                <el-option label="女" value="female" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-form-item label="身份证号" prop="idCard">
          <el-input v-model="studentForm.idCard" placeholder="请输入身份证号" />
        </el-form-item>
        
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="联系电话" prop="phone">
              <el-input v-model="studentForm.phone" placeholder="请输入联系电话" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="紧急联系人" prop="emergencyContact">
              <el-input v-model="studentForm.emergencyContact" placeholder="请输入紧急联系人" />
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="所属教练" prop="coachId">
              <el-select v-model="studentForm.coachId" placeholder="请选择教练" style="width: 100%">
                <el-option
                  v-for="coach in coachList"
                  :key="coach.id"
                  :label="coach.name"
                  :value="coach.id"
                />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="状态" prop="status">
              <el-select v-model="studentForm.status" placeholder="请选择状态" style="width: 100%">
                <el-option label="在读" value="studying" />
                <el-option label="已结业" value="graduated" />
                <el-option label="无效" value="invalid" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-form-item label="地址" prop="address">
          <el-input
            v-model="studentForm.address"
            type="textarea"
            :rows="2"
            placeholder="请输入地址"
          />
        </el-form-item>
        
        <el-form-item label="人脸照片">
          <div class="face-photo-upload">
            <el-upload
              class="avatar-uploader"
              action="#"
              :show-file-list="false"
              :before-upload="beforeAvatarUpload"
              :http-request="handleAvatarUpload"
            >
              <img v-if="studentForm.facePhoto" :src="studentForm.facePhoto" class="avatar" />
              <el-icon v-else class="avatar-uploader-icon"><Plus /></el-icon>
            </el-upload>
            <el-button @click="openFaceCapture">现场采集</el-button>
          </div>
        </el-form-item>
      </el-form>
      
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">提交</el-button>
      </template>
    </el-dialog>
    
    <!-- 人脸采集弹窗 -->
    <el-dialog
      v-model="faceCaptureVisible"
      title="人脸采集"
      width="500px"
    >
      <div class="face-capture">
        <div v-if="!capturedImage" class="camera-container">
          <video ref="videoRef" autoplay playsinline style="width: 100%; height: 300px; background: #000;"></video>
          <div class="capture-controls">
            <el-button type="primary" @click="captureFace">拍照</el-button>
          </div>
        </div>
        <div v-else class="capture-result">
          <img :src="capturedImage" alt=" captured face" style="width: 100%; max-height: 300px;" />
          <div class="result-controls">
            <el-button @click="retakePhoto">重新拍摄</el-button>
            <el-button type="primary" @click="confirmCapture">确认使用</el-button>
          </div>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useAuthStore } from '@/store/auth'

const authStore = useAuthStore()

// 权限检查
const hasPermission = (permission) => {
  return authStore.hasPermission(permission)
}

// 筛选表单
const filterForm = reactive({
  name: '',
  idCard: '',
  status: ''
})

// 表格数据
const studentList = ref([
  {
    id: 1,
    name: '张三',
    idCard: '440101199001011234',
    phone: '13800138000',
    gender: 'male',
    status: 'studying',
    coachName: '李教练',
    coachId: 1,
    enrollDate: '2024-01-15',
    address: '广东省广州市天河区',
    facePhoto: ''
  },
  {
    id: 2,
    name: '李四',
    idCard: '440101199102022345',
    phone: '13800138001',
    gender: 'female',
    status: 'graduated',
    coachName: '王教练',
    coachId: 2,
    enrollDate: '2023-12-10',
    address: '广东省广州市越秀区',
    facePhoto: ''
  }
])

const coachList = ref([
  { id: 1, name: '李教练' },
  { id: 2, name: '王教练' },
  { id: 3, name: '张教练' }
])

const loading = ref(false)
const total = ref(2)
const currentPage = ref(1)
const pageSize = ref(10)
const selectedStudents = ref([])

// 弹窗相关
const dialogVisible = ref(false)
const dialogTitle = ref('')
const studentFormRef = ref()
const faceCaptureVisible = ref(false)
const videoRef = ref()
const capturedImage = ref('')

// 表单数据
const studentForm = reactive({
  id: null,
  name: '',
  idCard: '',
  phone: '',
  gender: '',
  emergencyContact: '',
  coachId: null,
  status: 'studying',
  address: '',
  facePhoto: ''
})

// 表单验证规则
const studentRules = {
  name: [{ required: true, message: '请输入姓名', trigger: 'blur' }],
  idCard: [
    { required: true, message: '请输入身份证号', trigger: 'blur' },
    { pattern: /(^\d{15}$)|(^\d{18}$)|(^\d{17}(\d|X|x)$)/, message: '身份证号格式不正确', trigger: 'blur' }
  ],
  phone: [
    { required: true, message: '请输入联系电话', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '手机号格式不正确', trigger: 'blur' }
  ],
  gender: [{ required: true, message: '请选择性别', trigger: 'change' }],
  coachId: [{ required: true, message: '请选择教练', trigger: 'change' }],
  status: [{ required: true, message: '请选择状态', trigger: 'change' }]
}

// 状态转换
const getStatusType = (status) => {
  const typeMap = {
    'studying': 'primary',
    'graduated': 'success',
    'invalid': 'danger'
  }
  return typeMap[status] || 'info'
}

const getStatusText = (status) => {
  const textMap = {
    'studying': '在读',
    'graduated': '已结业',
    'invalid': '无效'
  }
  return textMap[status] || '未知'
}

// 操作方法
const handleFilter = () => {
  ElMessage.success('筛选功能已执行')
  // 实际项目中这里会调用API
}

const resetFilter = () => {
  filterForm.name = ''
  filterForm.idCard = ''
  filterForm.status = ''
  ElMessage.info('筛选条件已重置')
}

const handleSelectionChange = (selection) => {
  selectedStudents.value = selection
}

const handleAddStudent = () => {
  dialogTitle.value = '新增学员'
  resetStudentForm()
  dialogVisible.value = true
}

const handleEdit = (row) => {
  dialogTitle.value = '编辑学员'
  Object.assign(studentForm, row)
  dialogVisible.value = true
}

const handleView = (row) => {
  ElMessage.info(`查看学员: ${row.name}`)
  // 实际项目中跳转到详情页
}

const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm(
      `确定删除学员 "${row.name}" 吗？删除后不可恢复`,
      '删除确认',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    // 实际项目中调用删除API
    ElMessage.success('删除成功')
    refreshStudentList()
  } catch {
    // 用户取消删除
  }
}

const handleBatchDelete = async () => {
  if (selectedStudents.value.length === 0) return
  
  try {
    await ElMessageBox.confirm(
      `确定删除选中的 ${selectedStudents.value.length} 个学员吗？`,
      '批量删除确认',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    // 实际项目中调用批量删除API
    ElMessage.success('批量删除成功')
    refreshStudentList()
  } catch {
    // 用户取消删除
  }
}

const handleStatusChange = (row) => {
  ElMessage.success(`学员状态已更新为: ${getStatusText(row.status)}`)
  // 实际项目中调用API更新状态
}

const handleRefresh = () => {
  refreshStudentList()
  ElMessage.success('列表已刷新')
}

const handleSizeChange = (val) => {
  pageSize.value = val
  refreshStudentList()
}

const handleCurrentChange = (val) => {
  currentPage.value = val
  refreshStudentList()
}

const handleSubmit = async () => {
  if (!studentFormRef.value) return
  
  await studentFormRef.value.validate(async (valid) => {
    if (valid) {
      try {
        // 实际项目中调用API保存数据
        ElMessage.success(dialogTitle.value === '新增学员' ? '新增成功' : '编辑成功')
        dialogVisible.value = false
        refreshStudentList()
      } catch (error) {
        ElMessage.error('操作失败')
      }
    }
  })
}

const handleDialogClose = () => {
  studentFormRef.value?.resetFields()
  resetStudentForm()
}

const resetStudentForm = () => {
  Object.assign(studentForm, {
    id: null,
    name: '',
    idCard: '',
    phone: '',
    gender: '',
    emergencyContact: '',
    coachId: null,
    status: 'studying',
    address: '',
    facePhoto: ''
  })
}

const refreshStudentList = () => {
  loading.value = true
  // 模拟API调用
  setTimeout(() => {
    loading.value = false
  }, 500)
}

// 人脸相关方法
const beforeAvatarUpload = (file) => {
  const isJPG = file.type === 'image/jpeg' || file.type === 'image/png'
  const isLt2M = file.size / 1024 / 1024 < 2
  
  if (!isJPG) {
    ElMessage.error('上传头像图片只能是 JPG/PNG 格式!')
  }
  if (!isLt2M) {
    ElMessage.error('上传头像图片大小不能超过 2MB!')
  }
  return isJPG && isLt2M
}

const handleAvatarUpload = (options) => {
  // 模拟上传过程
  const reader = new FileReader()
  reader.onload = (e) => {
    studentForm.facePhoto = e.target.result
    ElMessage.success('图片上传成功')
  }
  reader.readAsDataURL(options.file)
}

const openFaceCapture = async () => {
  faceCaptureVisible.value = true
  await nextTick()
  
  try {
    const stream = await navigator.mediaDevices.getUserMedia({ video: true })
    if (videoRef.value) {
      videoRef.value.srcObject = stream
    }
  } catch (error) {
    ElMessage.error('无法访问摄像头，请检查权限设置')
  }
}

const captureFace = () => {
  if (videoRef.value) {
    const canvas = document.createElement('canvas')
    canvas.width = videoRef.value.videoWidth
    canvas.height = videoRef.value.videoHeight
    const ctx = canvas.getContext('2d')
    ctx.drawImage(videoRef.value, 0, 0)
    capturedImage.value = canvas.toDataURL('image/png')
  }
}

const retakePhoto = () => {
  capturedImage.value = ''
}

const confirmCapture = () => {
  studentForm.facePhoto = capturedImage.value
  faceCaptureVisible.value = false
  capturedImage.value = ''
  // 停止摄像头
  if (videoRef.value && videoRef.value.srcObject) {
    const tracks = videoRef.value.srcObject.getTracks()
    tracks.forEach(track => track.stop())
  }
  ElMessage.success('人脸采集成功')
}

onMounted(() => {
  refreshStudentList()
})
</script>

<style scoped>
.student-management {
  padding: 0;
}

.filter-card,
.operation-card {
  margin-bottom: 20px;
}

.operation-bar {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}

.face-photo-upload {
  display: flex;
  align-items: center;
  gap: 20px;
}

.avatar-uploader .avatar {
  width: 120px;
  height: 120px;
  display: block;
  border-radius: 6px;
}

.avatar-uploader .el-upload {
  border: 1px dashed #d9d9d9;
  border-radius: 6px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
  transition: var(--el-transition-duration-fast);
}

.avatar-uploader .el-upload:hover {
  border-color: var(--el-color-primary);
}

.avatar-uploader-icon {
  font-size: 28px;
  color: #8c939d;
  width: 120px;
  height: 120px;
  text-align: center;
  line-height: 120px;
}

.face-capture {
  text-align: center;
}

.camera-container {
  position: relative;
}

.capture-controls {
  margin-top: 20px;
}

.capture-result {
  position: relative;
}

.result-controls {
  margin-top: 20px;
  display: flex;
  justify-content: center;
  gap: 20px;
}
</style>