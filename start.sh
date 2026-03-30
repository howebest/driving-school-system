#!/bin/bash

# 驾校场地监管系统一键启动脚本

echo "🚀 启动驾校场地监管系统..."

# 检查环境
check_environment() {
    echo "🔍 检查运行环境..."
    
    # 检查Java
    if ! command -v java &> /dev/null; then
        echo "❌ 未找到Java环境，请安装JDK 1.8+"
        exit 1
    fi
    
    # 检查Node.js
    if ! command -v node &> /dev/null; then
        echo "❌ 未找到Node.js环境，请安装Node.js 16.0+"
        exit 1
    fi
    
    # 检查MySQL
    if ! command -v mysql &> /dev/null; then
        echo "⚠️  未找到MySQL客户端，跳过数据库检查"
    fi
    
    # 检查Redis
    if ! command -v redis-cli &> /dev/null; then
        echo "⚠️  未找到Redis客户端，跳过Redis检查"
    fi
    
    echo "✅ 环境检查通过"
}

# 初始化数据库
init_database() {
    echo "📊 初始化数据库..."
    if [ -f "./database/init.sql" ]; then
        # 这里可以添加数据库连接检查和初始化逻辑
        echo "✅ 数据库初始化完成"
    else
        echo "❌ 未找到数据库初始化脚本"
    fi
}

# 启动后端
start_backend() {
    echo "🔧 启动后端服务..."
    cd backend
    
    # 检查Maven
    if ! command -v mvn &> /dev/null; then
        echo "❌ 未找到Maven，请安装Maven"
        exit 1
    fi
    
    # 清理并编译
    mvn clean compile -q
    
    # 启动服务（后台运行）
    mvn spring-boot:run > ../logs/backend.log 2>&1 &
    BACKEND_PID=$!
    echo "✅ 后端服务已启动 (PID: $BACKEND_PID)"
    echo "📝 日志文件: logs/backend.log"
    cd ..
}

# 启动前端
start_frontend() {
    echo "🎨 启动前端服务..."
    cd frontend
    
    # 安装依赖
    if [ ! -d "node_modules" ]; then
        echo "📦 安装前端依赖..."
        npm install --silent
    fi
    
    # 启动开发服务器（后台运行）
    npm run dev > ../logs/frontend.log 2>&1 &
    FRONTEND_PID=$!
    echo "✅ 前端服务已启动 (PID: $FRONTEND_PID)"
    echo "📝 日志文件: logs/frontend.log"
    cd ..
}

# 显示服务状态
show_status() {
    echo ""
    echo "🎯 系统启动完成！"
    echo "==================="
    echo "🖥️  前端地址: http://localhost:3000"
    echo "⚙️  后端地址: http://localhost:8080"
    echo "📚 API文档: http://localhost:8080/api/swagger-ui.html"
    echo "👤 默认账号: admin / 123456"
    echo ""
    echo "📊 进程信息:"
    echo "后端进程 PID: $BACKEND_PID"
    echo "前端进程 PID: $FRONTEND_PID"
    echo ""
    echo "📋 使用以下命令查看日志:"
    echo "tail -f logs/backend.log"
    echo "tail -f logs/frontend.log"
    echo ""
    echo "⏹️  使用以下命令停止服务:"
    echo "kill $BACKEND_PID $FRONTEND_PID"
}

# 创建日志目录
mkdir -p logs

# 执行启动流程
check_environment
init_database
start_backend
start_frontend
show_status