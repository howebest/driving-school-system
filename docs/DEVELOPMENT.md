# 驾校场地监管系统 - 开发环境启动指南

## 项目结构
```
driving-school-system/
├── backend/           # 后端SpringBoot项目
├── frontend/          # 前端Vue3项目
├── database/          # 数据库脚本
├── ai-model/          # AI模型文件
└── docs/             # 文档资料
```

## 环境准备

### 后端环境
1. **Java环境**: JDK 1.8+
2. **数据库**: MySQL 8.0+
3. **缓存**: Redis 6.0+

### 前端环境
1. **Node.js**: 16.0+
2. **包管理器**: npm 或 yarn

## 快速启动

### 1. 数据库初始化
```bash
# 登录MySQL
mysql -u root -p

# 执行初始化脚本
source /path/to/driving-school-system/database/init.sql
```

### 2. 启动后端服务
```bash
cd backend
mvn clean install
mvn spring-boot:run
# 默认端口: 8080
# 访问地址: http://localhost:8080/api/swagger-ui.html
```

### 3. 启动前端服务
```bash
cd frontend
npm install
npm run dev
# 默认端口: 3000
# 访问地址: http://localhost:3000
```

## 默认账号
- **用户名**: admin
- **密码**: 123456

## API文档
后端集成Swagger，访问: http://localhost:8080/api/swagger-ui.html

## 开发工具推荐
- **IDE**: IntelliJ IDEA / VS Code
- **数据库工具**: Navicat / MySQL Workbench
- **API测试**: Postman

## 注意事项
1. 确保MySQL和Redis服务正常运行
2. 前端默认代理到后端8080端口
3. 开发环境下使用模拟数据，生产环境需配置真实API

## 项目特色
✅ 完整的RBAC权限管理
✅ 人脸识别和车牌识别集成
✅ AI违规行为智能监测
✅ 实时预警告警系统
✅ 移动端H5适配
✅ 丰富的统计报表功能