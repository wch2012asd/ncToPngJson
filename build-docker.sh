#!/bin/bash

# NC文件处理器Docker构建脚本

echo "开始构建NC文件处理器Docker镜像..."

# 检查Maven是否安装
if ! command -v mvn &> /dev/null; then
    echo "错误: Maven未安装，请先安装Maven"
    exit 1
fi

# 清理并编译项目
echo "1. 清理并编译项目..."
mvn clean package -DskipTests

# 检查jar文件是否生成
if [ ! -f "target/nc-file-processor-1.0.0.jar" ]; then
    echo "错误: jar文件未生成，请检查Maven构建"
    exit 1
fi

# 构建Docker镜像
echo "2. 构建Docker镜像..."
docker build -t nc-file-processor:latest .

# 检查构建是否成功
if [ $? -eq 0 ]; then
    echo "3. Docker镜像构建成功!"
    echo "镜像名称: nc-file-processor:latest"
    
    # 显示镜像信息
    docker images | grep nc-file-processor
    
    echo ""
    echo "使用方法:"
    echo "1. 单独运行容器:"
    echo "   docker run -d --name nc-processor -v ./input:/app/input -v ./output:/app/output nc-file-processor:latest"
    echo ""
    echo "2. 使用docker-compose运行（包含数据库）:"
    echo "   docker-compose up -d"
    echo ""
    echo "3. 查看日志:"
    echo "   docker logs nc-file-processor"
else
    echo "错误: Docker镜像构建失败"
    exit 1
fi