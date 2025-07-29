# 使用OpenJDK 8作为基础镜像
FROM openjdk:8-jre-slim

# 设置工作目录
WORKDIR /app

# 安装必要的系统依赖
RUN apt-get update && apt-get install -y \
    libnetcdf-dev \
    libhdf5-dev \
    && rm -rf /var/lib/apt/lists/*

# 复制Maven构建的jar文件
COPY target/nc-file-processor-1.0.0.jar app.jar

# 复制配置文件
COPY config.properties config.properties

# 创建输入和输出目录
RUN mkdir -p /app/input /app/output

# 设置环境变量
ENV JAVA_OPTS="-Xmx2g -Xms512m"

# 暴露端口（如果需要的话）
# EXPOSE 8080

# 设置启动命令
CMD ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]