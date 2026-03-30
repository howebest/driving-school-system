# 第三方API集成说明

## 百度人脸识别API集成

### 配置说明
在 `application-thirdparty.yml` 中配置百度AI相关参数：

```yaml
baidu:
  face:
    api-key: your-baidu-api-key
    secret-key: your-baidu-secret-key
    auth-url: https://aip.baidubce.com/oauth/2.0/token
    verify-url: https://aip.baidubce.com/rest/2.0/face/v3/search
```

### 核心功能
1. **人脸验证** - `/api/verify/face`
   - 上传图片进行人脸识别比对
   - 返回匹配结果和相似度分数

2. **人脸注册** - `/api/verify/face/register`
   - 将用户人脸信息注册到人脸库
   - 用于后续的身份验证

3. **人脸更新** - `/api/verify/face/update`
   - 更新已有用户的面部信息

### 使用示例
```javascript
// 人脸验证
const formData = new FormData();
formData.append('image', imageFile);
const response = await axios.post('/api/verify/face', formData);

// 人脸注册
const registerData = new FormData();
registerData.append('userId', 'student_001');
registerData.append('userInfo', '张三');
registerData.append('image', imageFile);
const result = await axios.post('/api/verify/face/register', registerData);
```

## 阿里云车牌识别API集成

### 配置说明
```yaml
aliyun:
  plate:
    access-key-id: your-aliyun-access-key-id
    access-key-secret: your-aliyun-access-key-secret
    endpoint: ocr.cn-shanghai.aliyuncs.com
```

### 核心功能
1. **车牌识别** - `/api/verify/plate`
   - 上传车辆图片识别车牌号码
   - 返回车牌信息和置信度

2. **车辆信息查询** - `/api/verify/vehicle/{plateNumber}`
   - 根据车牌号查询车辆详细信息

### 使用示例
```javascript
// 车牌识别
const plateFormData = new FormData();
plateFormData.append('image', carImageFile);
const plateResult = await axios.post('/api/verify/plate', plateFormData);

// 车辆信息查询
const vehicleInfo = await axios.get('/api/verify/vehicle/京A12345');
```

## 前端集成示例

### 人脸采集组件
```vue
<template>
  <div class="face-capture">
    <video ref="video" autoplay></video>
    <canvas ref="canvas" style="display:none;"></canvas>
    <el-button @click="captureFace">拍照识别</el-button>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import axios from 'axios'

const video = ref(null)
const canvas = ref(null)

const captureFace = async () => {
  const ctx = canvas.value.getContext('2d')
  ctx.drawImage(video.value, 0, 0, 300, 300)
  
  canvas.value.toBlob(async (blob) => {
    const formData = new FormData()
    formData.append('image', blob, 'face.jpg')
    
    try {
      const response = await axios.post('/api/verify/face', formData)
      console.log('识别结果:', response.data)
    } catch (error) {
      console.error('识别失败:', error)
    }
  })
}
</script>
```

## 错误处理

### 常见错误码
- **百度人脸识别**: 
  - 0: 成功
  - 222202: 图片中没有人脸
  - 222203: 无法解析人脸

- **阿里云车牌识别**:
  - 200: 成功
  - 400: 请求参数错误
  - 401: 认证失败

### 异常处理策略
1. 网络超时重试机制
2. 服务降级处理
3. 本地缓存白名单
4. 异步处理队列

## 性能优化建议

1. **缓存策略**
   - 人脸特征向量本地缓存
   - 白名单数据Redis缓存
   - 频繁查询结果缓存

2. **并发控制**
   - 限制同时处理的请求数量
   - 使用消息队列异步处理
   - 批量处理优化

3. **资源管理**
   - 图片压缩处理
   - 连接池管理
   - 内存泄漏监控