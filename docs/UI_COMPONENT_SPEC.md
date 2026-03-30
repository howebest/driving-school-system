# UI组件设计规范

## 🎨 设计原则

### 1. 统一性原则
所有页面必须遵循相同的设计语言和交互模式

### 2. 一致性原则  
相同功能的组件在不同页面应保持样式和行为一致

### 3. 可用性原则
确保信息完整展示，提供良好的用户体验

## 📐 组件规范

### 按钮规范
```
主按钮: 
- 样式: 蓝色背景 + 白色文字 + 圆角
- 图标: 左侧添加对应功能图标
- 文案: 动词+名词格式 (如: "新增学员"、"新增教练")
- 尺寸: large (44px高度)

次要按钮:
- 样式: 白色背景 + 蓝色边框 + 蓝色文字
- 图标: 左侧添加对应功能图标
- 尺寸: default (32px高度)
```

### 表格规范
```
固定列宽:
- ID: 80px
- 姓名: 120px (含头像)
- 证件号: 150-180px
- 手机号: 120px
- 日期: 120px
- 状态: 100px
- 操作: 根据按钮数量调整(建议200-250px)

滚动优化:
- 横向滚动: 当列数较多时启用
- 固定列: ID和操作列固定
- 最小宽度: 1200px
```

### 状态标签规范
```
成功状态: 绿色(success) - 在职、已绑定、已处理
警告状态: 橙色(warning) - 未绑定、待处理
危险状态: 红色(danger) - 离职、未处理
信息状态: 蓝色(primary) - 在读、当班
```

## 🎯 页面结构模板

### 标准页面布局
```vue
<template>
  <div class="page-container">
    <!-- 页面头部 -->
    <div class="page-header">
      <div class="header-content">
        <div class="header-info">
          <h1>页面标题</h1>
          <p>页面描述</p>
        </div>
        <div class="header-actions">
          <el-button type="primary" size="large">
            <el-icon><Plus /></el-icon>新增项目
          </el-button>
        </div>
      </div>
    </div>

    <!-- 搜索区域 -->
    <div class="search-section">
      <el-card class="search-card">
        <!-- 搜索表单 -->
      </el-card>
    </div>

    <!-- 数据表格 -->
    <div class="table-section">
      <el-card class="table-card">
        <template #header>
          <!-- 表格头部 -->
        </template>
        <el-table :scroll="{ x: 1200 }">
          <!-- 表格列 -->
        </el-table>
        <!-- 分页 -->
      </el-card>
    </div>
  </div>
</template>
```

## 📊 信息展示优化

### 解决信息截断问题
1. **合理设置列宽**: 根据内容长度预估合适宽度
2. **启用横向滚动**: 当内容超出屏幕宽度时
3. **固定关键列**: ID和操作列始终保持可见
4. **省略号处理**: 长文本使用ellipsis显示

### 响应式适配
```css
/* 大屏幕 */
@media (min-width: 1200px) {
  /* 显示完整信息 */
}

/* 中等屏幕 */
@media (max-width: 1199px) {
  /* 适当减少列宽 */
}

/* 小屏幕 */
@media (max-width: 768px) {
  /* 启用垂直滚动 */
  /* 简化操作按钮 */
}
```

## 🔧 技术实现要点

### 组件复用
```javascript
// 统一的状态处理函数
const getStatusType = (status) => {
  const typeMap = { 1: 'success', 2: 'danger', 3: 'warning' }
  return typeMap[status] || 'info'
}

const getStatusText = (status) => {
  const textMap = { 1: '在职', 2: '离职', 3: '请假' }
  return textMap[status] || '未知'
}
```

### 图标统一
使用Element Plus官方图标库，确保图标风格一致

### 样式变量
```css
:root {
  --primary-color: #3b82f6;
  --success-color: #10b981;
  --warning-color: #f59e0b;
  --danger-color: #ef4444;
  --border-radius: 8px;
}
```

## ✅ 质量检查清单

- [ ] 按钮样式统一
- [ ] 图标使用一致
- [ ] 表格列宽合理
- [ ] 信息完整展示
- [ ] 响应式适配良好
- [ ] 交互反馈及时
- [ ] 状态标识清晰

## 📝 最佳实践

1. **保持简洁**: 避免过度设计
2. **注重细节**: 统一间距、圆角、阴影
3. **用户导向**: 优先展示重要信息
4. **性能优化**: 合理使用虚拟滚动
5. **可维护性**: 组件化设计，易于扩展

---
*规范版本: v1.0*  
*最后更新: 2026-03-29*