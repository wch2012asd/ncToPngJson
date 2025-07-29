# NC文件处理器 Docker部署指南

## 概述

这个项目提供了将NC文件转换为红黑图的功能，支持定时监控目录变化并自动处理新文件。

## 功能特性

- 定时监控输入目录中的NC文件
- 将NC文件转换为PNG红黑图
- PostgreSQL数据库记录处理状态
- 避免重复处理已处理的文件
- 支持Docker容器化部署

## 快速开始

### 1. 构建Docker镜像

```bash
# 给脚本添加执行权限
chmod +x build-docker.sh run-docker.sh

# 构建Docker镜像
./build-docker.sh
```

### 2. 运行服务

```bash
# 运行完整服务（包含数据库）
./run-docker.sh
# 选择选项 2

# 或者使用docker-compose直接启动
docker-compose up -d
```

### 3. 使用服务

1. 将NC文件放入 `input` 目录
2. 程序会自动检测并处理文件
3. 处理结果保存在 `output` 目录
4. 数据库记录处理状态

## 目录结构

```
project/
├── input/          # NC文件输入目录
├── output/         # PNG图像输出目录
├── config.properties # 配置文件
├── Dockerfile      # Docker镜像构建文件
├── docker-compose.yml # Docker Compose配置
└── init.sql        # 数据库初始化脚本
```

## 配置说明

### 环境变量

| 变量名 | 默认值 | 说明 |
|--------|--------|------|
| `INPUT_DIR` | `/app/input` | NC文件输入目录 |
| `OUTPUT_DIR` | `/app/output` | PNG输出目录 |
| `DB_URL` | `jdbc:postgresql://postgres:5432/ncprocessor` | 数据库连接URL |
| `DB_USER` | `ncuser` | 数据库用户名 |
| `DB_PASSWORD` | `ncpassword` | 数据库密码 |
| `DB_SCHEMA` | `public` | 数据库模式 |
| `JAVA_OPTS` | `-Xmx2g -Xms512m` | JVM参数 |

### 配置文件

可以通过修改 `config.properties` 文件来配置应用程序：

```properties
# 目录配置
input.directory=./input
output.directory=./output

# 数据库配置
database.url=jdbc:postgresql://localhost:5432/ncprocessor
database.user=ncuser
database.password=ncpassword
database.schema=public
```

## Docker命令

### 构建镜像
```bash
mvn clean package -DskipTests
docker build -t nc-file-processor:latest .
```

### 运行容器
```bash
# 单独运行（需要外部数据库）
docker run -d \
  --name nc-file-processor \
  -v $(pwd)/input:/app/input \
  -v $(pwd)/output:/app/output \
  -e DB_URL=jdbc:postgresql://your-db:5432/ncprocessor \
  nc-file-processor:latest

# 使用docker-compose运行完整服务
docker-compose up -d
```

### 查看日志
```bash
docker logs nc-file-processor
docker logs -f nc-file-processor  # 实时查看
```

### 停止服务
```bash
# 停止单个容器
docker stop nc-file-processor
docker rm nc-file-processor

# 停止docker-compose服务
docker-compose down
```

## 数据库

### 表结构

```sql
CREATE TABLE file_conversion_records (
    id SERIAL PRIMARY KEY,
    folder_name VARCHAR(255) NOT NULL,
    file_name VARCHAR(255) NOT NULL,
    file_path TEXT NOT NULL,
    output_path TEXT NOT NULL,
    file_size BIGINT,
    conversion_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    status VARCHAR(50) DEFAULT 'SUCCESS',
    UNIQUE(folder_name, file_name)
);
```

### 查询处理记录

```sql
-- 查看所有处理记录
SELECT * FROM file_conversion_records ORDER BY conversion_time DESC;

-- 查看特定文件夹的记录
SELECT * FROM file_conversion_records WHERE folder_name = 'your_folder';

-- 查看处理失败的记录
SELECT * FROM file_conversion_records WHERE status LIKE 'FAILED%';
```

## 故障排除

### 常见问题

1. **容器启动失败**
   - 检查端口是否被占用
   - 检查目录权限
   - 查看容器日志

2. **数据库连接失败**
   - 检查数据库服务是否启动
   - 检查网络连接
   - 验证数据库配置

3. **文件处理失败**
   - 检查输入文件格式
   - 查看应用程序日志
   - 检查磁盘空间

### 日志查看

```bash
# 查看应用程序日志
docker logs nc-file-processor

# 查看数据库日志
docker logs nc-postgres

# 查看所有服务状态
docker-compose ps
```

## 性能调优

### JVM参数调整

```bash
# 修改docker-compose.yml中的JAVA_OPTS
environment:
  - JAVA_OPTS=-Xmx4g -Xms1g -XX:+UseG1GC
```

### 数据库优化

```sql
-- 创建索引提高查询性能
CREATE INDEX idx_conversion_time ON file_conversion_records(conversion_time);
CREATE INDEX idx_status ON file_conversion_records(status);
```

## 开发和调试

### 本地开发

```bash
# 编译项目
mvn compile

# 运行项目
mvn exec:java -Dexec.mainClass=com.example.NCFileProcessor
```

### 调试模式

```bash
# 启用调试模式
docker run -d \
  --name nc-file-processor-debug \
  -p 5005:5005 \
  -e JAVA_OPTS="-Xmx2g -Xms512m -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005" \
  -v $(pwd)/input:/app/input \
  -v $(pwd)/output:/app/output \
  nc-file-processor:latest
```

## 许可证

本项目采用 MIT 许可证。