<template>
  <div class="site-management">
    <!-- 页面标题 -->
    <a-page-header
      title="场地管理"
      sub-title="管理训练场地区域和时段配置"
      style="padding: 0 0 16px 0"
    >
      <template #extra>
        <a-space>
          <a-button @click="showMapView">
            <template #icon><EnvironmentOutlined /></template>
            地图视图
          </a-button>
          <a-button type="primary" @click="showAddModal">
            <template #icon><PlusOutlined /></template>
            新增区域
          </a-button>
        </a-space>
      </template>
    </a-page-header>

    <!-- 统计卡片 -->
    <a-row :gutter="16" style="margin-bottom: 16px">
      <a-col :xs="12" :sm="6">
        <a-card>
          <a-statistic
            title="总区域数"
            :value="totalAreas"
            :prefix="h(AppstoreOutlined)"
          />
        </a-card>
      </a-col>
      <a-col :xs="12" :sm="6">
        <a-card>
          <a-statistic
            title="使用中"
            :value="inUseAreas"
            :value-style="{ color: '#3f8600' }"
            :prefix="h(CheckCircleOutlined)"
          />
        </a-card>
      </a-col>
      <a-col :xs="12" :sm="6">
        <a-card>
          <a-statistic
            title="空闲区域"
            :value="idleAreas"
            :value-style="{ color: '#1890ff' }"
            :prefix="h(ClockCircleOutlined)"
          />
        </a-card>
      </a-col>
      <a-col :xs="12" :sm="6">
        <a-card>
          <a-statistic
            title="维护中"
            :value="maintenanceAreas"
            :value-style="{ color: '#cf1322' }"
            :prefix="h(ToolOutlined)"
          />
        </a-card>
      </a-col>
    </a-row>

    <!-- 区域列表 -->
    <a-row :gutter="16">
      <a-col :xs="24" :lg="12">
        <a-card title="科目二区域" style="margin-bottom: 16px">
          <template #extra>
            <a-button type="link" size="small">
              管理 <RightOutlined />
            </a-button>
          </template>
          <a-list
            :data-source="ke2Areas"
            :grid="{ gutter: 16, xs: 1, sm: 2, md: 2, lg: 2, xl: 2, xxl: 2 }"
          >
            <template #renderItem="{ item }">
              <a-list-item>
                <a-card
                  hoverable
                  :class="['area-card', `area-${item.status}`]"
                  @click="showAreaDetail(item)"
                >
                  <a-card-meta :title="item.name">
                    <template #description>
                      <a-space direction="vertical" :size="4">
                        <span><EnvironmentOutlined /> {{ item.location }}</span>
                        <a-tag v-if="item.status === '使用中'" color="success">{{ item.status }}</a-tag>
                        <a-tag v-else-if="item.status === '空闲'" color="processing">{{ item.status }}</a-tag>
                        <a-tag v-else color="error">{{ item.status }}</a-tag>
                      </a-space>
                    </template>
                  </a-card-meta>
                  <template #actions>
                    <a-space>
                      <a-tooltip title="编辑">
                        <EditOutlined @click.stop="handleEdit(item)" />
                      </a-tooltip>
                      <a-tooltip title="时段配置">
                        <ClockCircleOutlined @click.stop="showTimeConfig(item)" />
                      </a-tooltip>
                      <a-popconfirm
                        title="确定删除该区域吗？"
                        @confirm="handleDelete(item)"
                      >
                        <DeleteOutlined @click.stop style="color: #ff4d4f" />
                      </a-popconfirm>
                    </a-space>
                  </template>
                </a-card>
              </a-list-item>
            </template>
          </a-list>
        </a-card>
      </a-col>

      <a-col :xs="24" :lg="12">
        <a-card title="科目三路线" style="margin-bottom: 16px">
          <template #extra>
            <a-button type="link" size="small">
              管理 <RightOutlined />
            </a-button>
          </template>
          <a-list
            :data-source="ke3Areas"
            :grid="{ gutter: 16, xs: 1, sm: 2, md: 2, lg: 2, xl: 2, xxl: 2 }"
          >
            <template #renderItem="{ item }">
              <a-list-item>
                <a-card
                  hoverable
                  :class="['area-card', `area-${item.status}`]"
                  @click="showAreaDetail(item)"
                >
                  <a-card-meta :title="item.name">
                    <template #description>
                      <a-space direction="vertical" :size="4">
                        <span><EnvironmentOutlined /> {{ item.location }}</span>
                        <a-tag v-if="item.status === '使用中'" color="success">{{ item.status }}</a-tag>
                        <a-tag v-else-if="item.status === '空闲'" color="processing">{{ item.status }}</a-tag>
                        <a-tag v-else color="error">{{ item.status }}</a-tag>
                      </a-space>
                    </template>
                  </a-card-meta>
                  <template #actions>
                    <a-space>
                      <a-tooltip title="编辑">
                        <EditOutlined @click.stop="handleEdit(item)" />
                      </a-tooltip>
                      <a-tooltip title="时段配置">
                        <ClockCircleOutlined @click.stop="showTimeConfig(item)" />
                      </a-tooltip>
                      <a-popconfirm
                        title="确定删除该区域吗？"
                        @confirm="handleDelete(item)"
                      >
                        <DeleteOutlined @click.stop style="color: #ff4d4f" />
                      </a-popconfirm>
                    </a-space>
                  </template>
                </a-card>
              </a-list-item>
            </template>
          </a-list>
        </a-card>
      </a-col>
    </a-row>

    <!-- 时段配置表格 -->
    <a-card title="时段配置" style="margin-top: 16px">
      <a-table
        :columns="timeColumns"
        :data-source="timeData"
        :pagination="false"
      >
        <template #bodyCell="{ column, record }">
          <template v-if="column.key === 'status'">
            <a-switch
              v-model:checked="record.enabled"
              checked-children="启用"
              un-checked-children="禁用"
              @change="(checked) => toggleTimeSlot(record, checked)"
            />
          </template>
          <template v-else-if="column.key === 'type'">
            <a-tag v-if="record.type === '科目二'" color="blue">{{ record.type }}</a-tag>
            <a-tag v-else color="green">{{ record.type }}</a-tag>
          </template>
        </template>
      </a-table>
    </a-card>

    <!-- 新增/编辑区域模态框 -->
    <a-modal
      v-model:open="modalVisible"
      :title="modalTitle"
      width="600px"
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
        <a-form-item label="区域名称" name="name">
          <a-input v-model:value="formState.name" placeholder="请输入区域名称" />
        </a-form-item>

        <a-form-item label="所属科目" name="subject">
          <a-select v-model:value="formState.subject" placeholder="请选择科目">
            <a-select-option value="科目二">科目二</a-select-option>
            <a-select-option value="科目三">科目三</a-select-option>
          </a-select>
        </a-form-item>

        <a-form-item label="位置描述" name="location">
          <a-input v-model:value="formState.location" placeholder="请输入位置描述" />
        </a-form-item>

        <a-form-item label="区域类型" name="areaType">
          <a-select v-model:value="formState.areaType" placeholder="请选择区域类型">
            <a-select-option value="倒车入库">倒车入库</a-select-option>
            <a-select-option value="侧方停车">侧方停车</a-select-option>
            <a-select-option value="坡道起步">坡道起步</a-select-option>
            <a-select-option value="曲线行驶">曲线行驶</a-select-option>
            <a-select-option value="直角转弯">直角转弯</a-select-option>
            <a-select-option value="道路驾驶">道路驾驶</a-select-option>
          </a-select>
        </a-form-item>

        <a-form-item label="状态" name="status">
          <a-radio-group v-model:value="formState.status">
            <a-radio value="空闲">空闲</a-radio>
            <a-radio value="使用中">使用中</a-radio>
            <a-radio value="维护">维护</a-radio>
          </a-radio-group>
        </a-form-item>

        <a-form-item label="备注" name="notes">
          <a-textarea
            v-model:value="formState.notes"
            placeholder="请输入备注信息"
            :rows="3"
          />
        </a-form-item>
      </a-form>
    </a-modal>

    <!-- 时段配置模态框 -->
    <a-modal
      v-model:open="timeConfigVisible"
      title="时段配置"
      width="800px"
      :footer="null"
    >
      <a-alert
        message="配置训练时段"
        description="设置该区域的可训练时段，系统将根据配置进行违规检测"
        type="info"
        show-icon
        style="margin-bottom: 16px"
      />
      <a-table
        :columns="configColumns"
        :data-source="configData"
        :pagination="false"
        size="small"
      >
        <template #bodyCell="{ column, record }">
          <template v-if="column.key === 'enabled'">
            <a-switch v-model:checked="record.enabled" />
          </template>
          <template v-else-if="column.key === 'maxVehicles'">
            <a-input-number v-model:value="record.maxVehicles" :min="1" :max="10" />
          </template>
        </template>
      </a-table>
      <div style="margin-top: 16px; text-align: right">
        <a-button type="primary" @click="saveTimeConfig">保存配置</a-button>
      </div>
    </a-modal>
  </div>
</template>

<script setup>
import { ref, reactive, h } from 'vue'
import { message } from 'ant-design-vue'
import {
  PlusOutlined,
  EnvironmentOutlined,
  EditOutlined,
  DeleteOutlined,
  ClockCircleOutlined,
  CheckCircleOutlined,
  ToolOutlined,
  AppstoreOutlined,
  RightOutlined
} from '@ant-design/icons-vue'

const modalVisible = ref(false)
const modalLoading = ref(false)
const modalTitle = ref('新增区域')
const timeConfigVisible = ref(false)

const totalAreas = ref(15)
const inUseAreas = ref(8)
const idleAreas = ref(5)
const maintenanceAreas = ref(2)

const ke2Areas = ref([
  { id: 1, name: '倒车入库A区', location: '训练场东侧', status: '使用中', areaType: '倒车入库' },
  { id: 2, name: '倒车入库B区', location: '训练场东侧', status: '空闲', areaType: '倒车入库' },
  { id: 3, name: '侧方停车区', location: '训练场南侧', status: '使用中', areaType: '侧方停车' },
  { id: 4, name: '坡道起步区', location: '训练场北侧', status: '维护', areaType: '坡道起步' },
  { id: 5, name: '曲线行驶区', location: '训练场西侧', status: '空闲', areaType: '曲线行驶' },
  { id: 6, name: '直角转弯区', location: '训练场西侧', status: '使用中', areaType: '直角转弯' }
])

const ke3Areas = ref([
  { id: 7, name: '科三路线1', location: '城市道路A段', status: '使用中', areaType: '道路驾驶' },
  { id: 8, name: '科三路线2', location: '城市道路B段', status: '空闲', areaType: '道路驾驶' },
  { id: 9, name: '科三路线3', location: '城市道路C段', status: '使用中', areaType: '道路驾驶' }
])

const formRef = ref()
const formState = reactive({
  name: '',
  subject: undefined,
  location: '',
  areaType: undefined,
  status: '空闲',
  notes: ''
})

const rules = {
  name: [{ required: true, message: '请输入区域名称', trigger: 'blur' }],
  subject: [{ required: true, message: '请选择科目', trigger: 'change' }],
  location: [{ required: true, message: '请输入位置描述', trigger: 'blur' }],
  areaType: [{ required: true, message: '请选择区域类型', trigger: 'change' }]
}

const timeColumns = [
  { title: '时段', dataIndex: 'timeSlot', key: 'timeSlot', width: 150 },
  { title: '类型', dataIndex: 'type', key: 'type', width: 100 },
  { title: '状态', key: 'status', width: 100 },
  { title: '备注', dataIndex: 'notes', key: 'notes' }
]

const timeData = ref([
  { key: '1', timeSlot: '08:00 - 10:00', type: '科目二', enabled: true, notes: '早间训练时段' },
  { key: '2', timeSlot: '10:00 - 12:00', type: '科目二', enabled: true, notes: '上午训练时段' },
  { key: '3', timeSlot: '14:00 - 16:00', type: '科目二', enabled: true, notes: '下午训练时段' },
  { key: '4', timeSlot: '16:00 - 18:00', type: '科目三', enabled: false, notes: '傍晚训练时段（已禁用）' },
  { key: '5', timeSlot: '19:00 - 21:00', type: '科目三', enabled: false, notes: '夜间训练时段（已禁用）' }
])

const configColumns = [
  { title: '时段', dataIndex: 'timeSlot', key: 'timeSlot', width: 150 },
  { title: '启用', key: 'enabled', width: 80 },
  { title: '最大车辆数', key: 'maxVehicles', width: 120 },
  { title: '备注', dataIndex: 'notes', key: 'notes' }
]

const configData = ref([
  { key: '1', timeSlot: '08:00 - 10:00', enabled: true, maxVehicles: 3, notes: '' },
  { key: '2', timeSlot: '10:00 - 12:00', enabled: true, maxVehicles: 3, notes: '' },
  { key: '3', timeSlot: '14:00 - 16:00', enabled: true, maxVehicles: 4, notes: '' },
  { key: '4', timeSlot: '16:00 - 18:00', enabled: true, maxVehicles: 4, notes: '' }
])

const showMapView = () => {
  message.info('打开地图视图')
}

const showAddModal = () => {
  modalTitle.value = '新增区域'
  modalVisible.value = true
}

const handleEdit = (record) => {
  modalTitle.value = '编辑区域'
  modalVisible.value = true
  Object.assign(formState, record)
}

const showAreaDetail = (record) => {
  message.info(`查看区域详情: ${record.name}`)
}

const showTimeConfig = (record) => {
  timeConfigVisible.value = true
  message.info(`配置 ${record.name} 的训练时段`)
}

const handleDelete = (record) => {
  message.success(`已删除区域: ${record.name}`)
}

const toggleTimeSlot = (record, checked) => {
  message.success(`${checked ? '启用' : '禁用'}时段: ${record.timeSlot}`)
}

const saveTimeConfig = () => {
  message.success('时段配置保存成功')
  timeConfigVisible.value = false
}

const handleSubmit = async () => {
  try {
    await formRef.value.validate()
    modalLoading.value = true

    setTimeout(() => {
      modalVisible.value = false
      modalLoading.value = false
      message.success(modalTitle.value === '新增区域' ? '新增成功' : '编辑成功')
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
.site-management {
  padding: 0;
}

.area-card {
  transition: all 0.3s;
}

.area-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.area-使用中 {
  border-left: 3px solid #52c41a;
}

.area-空闲 {
  border-left: 3px solid #1890ff;
}

.area-维护 {
  border-left: 3px solid #ff4d4f;
}
</style>
