# NC文件处理器 Docker部署完整指南

## 前置条件

### 1. 安装Docker
- **Windows**: 下载并安装 [Docker Desktop for Windows](https://www.docker.com/products/docker-desktop)
- **Linux**: 
  ```bash
  curl -fsSL https://get.docker.com -o get-docker.sh
  sudo sh get-docker.sh
  ```
- **macOS**: 下载并安装 [Docker Desktop for Mac](https://www.docker.com/products/docker-desktop)

### 2. 安装Docker Compose
Docker Desktop已包含Docker Compose，Linux用户需要单独安装：
```bash
sudo curl -L "https://github.com/docker/compose/releases/download/1.29.2/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose
sudo chmod +x /usr/local/bin/docker-compose
```

### 3. 验证安装
```bash
docker --version
docker-compose --version
```

## 项目结构

```
nc-file-processor/
├── src/                    # 源代码
├── target/                 # Maven构建输出
├── input/                  # NC文件输入目录
├── output/                 # PNG输出目录
├── config.properties       # 配置文件
├── Dockerfile             # Docker镜像构建文件
├── docker-compose.yml     # Docker Compose配置
├── init.sql              # 数据库初始化脚本
├── build-docker.bat      # Windows构建脚本
├── build-docker.sh       # Linux/Mac构建脚本
├── run-docker.bat        # Windows运行脚本
├── run-docker.sh         # Linux/Mac运行脚本
└── README-Docker.md      # Docker使用说明
```

## 部署步骤

### 步骤1: 准备项目
```bash
# 克隆或下载项目到本地
cd nc-file-processor

# 确保有必要的目录
mkdir -p input output
```

### 步骤2: 构建项目

#### Windows用户:
```cmd
# 双击运行或在命令行执行
build-docker.bat
```

#### Linux/Mac用户:
```bash
# 给脚本添加执行权限
chmod +x build-docker.sh run-docker.sh

# 构建项目
./build-docker.sh
```

#### 手动构建:
```bash
# 1. Maven打包
mvn clean package -DskipTests

# 2. 构建Docker镜像
docker build -t nc-file-processor:latest .
```

### 步骤3: 运行服务

#### 方式1: 使用脚本运行

**Windows:**
```cmd
run-docker.bat
```

**Linux/Mac:**
```bash
./run-docker.sh
```

#### 方式2: 使用Docker Compose
```bash
# 启动完整服务（包含数据库）
docker-compose up -d

# 查看服务状态
docker-compose ps

# 查看日志
docker-compose logs -f nc-file-processor
```

#### 方式3: 单独运行容器
```bash
# 仅运行NC处理器（需要外部数据库）
docker run -d \
  --name nc-file-processor \
  -v $(pwd)/input:/app/input \
  -v $(pwd)/output:/app/output \
  -v $(pwd)/config.properties:/app/config.properties \
  -e DB_URL=jdbc:postgresql://your-db-host:5432/ncprocessor \
  -e DB_USER=your-username \
  -e DB_PASSWORD=your-password \
  --restart unless-stopped \
  nc-file-processor:latest
```

## 配置说明

### 环境变量配置

| 变量名 | 默认值 | 说明 |
|--------|--------|------|
| `INPUT_DIR` | `/app/input` | NC文件输入目录 |
| `OUTPUT_DIR` | `/app/output` | PNG输出目录 |
| `DB_URL` | `jdbc:postgresql://postgres:5432/ncprocessor` | 数据库连接URL |
| `DB_USER` | `ncuser` | 数据库用户名 |
| `DB_PASSWORD` | `ncpassword` | 数据库密码 |
| `DB_SCHEMA` | `public` | 数据库模式 |
| `JAVA_OPTS` | `-Xmx2g -Xms512m` | JVM参数 |

### 修改配置

#### 方式1: 修改docker-compose.yml
```yaml
services:
  nc-processor:
    environment:
      - INPUT_DIR=/app/input
      - OUTPUT_DIR=/app/output
      - DB_URL=jdbc:postgresql://postgres:5432/ncprocessor
      - DB_USER=ncuser
      - DB_PASSWORD=your-new-password
      - JAVA_OPTS=-Xmx4g -Xms1g
```

#### 方式2: 修改config.properties
```properties
input.directory=./input
output.directory=./output
database.url=jdbc:postgresql://localhost:5432/ncprocessor
database.user=ncuser
database.password=your-password
database.schema=public
```

## 使用说明

### 1. 添加NC文件
将NC文件放入 `input` 目录中，程序会自动检测并处理：
```
input/
├── folder1/
│   ├── file1.nc
│   └── file2.nc
└── folder2/
    └── file3.nc
```

### 2. 查看处理结果
处理后的PNG文件会保存在 `output` 目录中，保持原有的目录结构：
```
output/
├── folder1/
│   ├── file1_variable1_timestamp.png
│   ├── file1_variable1_timestamp.json
│   ├── file2_variable1_timestamp.png
│   └── file2_variable1_timestamp.json
└── folder2/
    ├── file3_variable1_timestamp.png
    └── file3_variable1_timestamp.json
```

### 3. 查看处理状态
```bash
# 查看应用日志
docker logs nc-file-processor

# 查看数据库记录
docker exec -it nc-postgres psql -U ncuser -d ncprocessor -c "SELECT * FROM file_conversion_records ORDER BY conversion_time DESC LIMIT 10;"
```

## 管理命令

### 查看服务状态
```bash
# 查看所有容器
docker ps

# 查看服务状态
docker-compose ps
```

### 查看日志
```bash
# 查看应用日志
docker logs nc-file-processor
docker logs -f nc-file-processor  # 实时查看

# 查看数据库日志
docker logs nc-postgres

# 查看所有服务日志
docker-compose logs -f
```

### 停止服务
```bash
# 停止docker-compose服务
docker-compose down

# 停止单个容器
docker stop nc-file-processor
docker rm nc-file-processor
```

### 重启服务
```bash
# 重启docker-compose服务
docker-compose restart

# 重启单个容器
docker restart nc-file-processor
```

### 更新应用
```bash
# 1. 重新构建镜像
mvn clean package -DskipTests
docker build -t nc-file-processor:latest .

# 2. 重启服务
docker-compose down
docker-compose up -d
```

## 数据持久化

### 数据库数据
数据库数据通过Docker volume持久化：
```yaml
volumes:
  postgres_data:  # 数据库数据卷
```

### 备份数据库
```bash
# 备份数据库
docker exec nc-postgres pg_dump -U ncuser ncprocessor > backup.sql

# 恢复数据库
docker exec -i nc-postgres psql -U ncuser ncprocessor < backup.sql
```

### 备份处理结果
```bash
# 备份输出目录
tar -czf output-backup-$(date +%Y%m%d).tar.gz output/
```

## 故障排除

### 常见问题

1. **Docker服务未启动**
   ```bash
   # Windows: 启动Docker Desktop
   # Linux: 启动Docker服务
   sudo systemctl start docker
   ```

2. **端口冲突**
   ```bash
   # 修改docker-compose.yml中的端口映射
   ports:
     - "5433:5432"  # 改为其他端口
   ```

3. **内存不足**
   ```bash
   # 修改JVM参数
   environment:
     - JAVA_OPTS=-Xmx1g -Xms256m
   ```

4. **权限问题**
   ```bash
   # Linux下给目录添加权限
   sudo chown -R $USER:$USER input output
   chmod -R 755 input output
   ```

### 日志分析

#### 应用启动失败
```bash
# 查看详细启动日志
docker logs nc-file-processor

# 常见错误:
# - 数据库连接失败: 检查数据库配置和网络
# - 文件权限问题: 检查目录权限
# - 内存不足: 调整JVM参数
```

#### 文件处理失败
```bash
# 查看处理日志
docker logs nc-file-processor | grep "处理NC文件"

# 检查文件格式
# 检查磁盘空间
# 检查文件权限
```

### 性能优化

#### JVM调优
```yaml
environment:
  - JAVA_OPTS=-Xmx4g -Xms1g -XX:+UseG1GC -XX:MaxGCPauseMillis=200
```

#### 数据库调优
```yaml
postgres:
  environment:
    - POSTGRES_SHARED_PRELOAD_LIBRARIES=pg_stat_statements
    - POSTGRES_MAX_CONNECTIONS=200
    - POSTGRES_SHARED_BUFFERS=256MB
```

## 监控和维护

### 健康检查
```bash
# 检查容器健康状态
docker ps --format "table {{.Names}}\t{{.Status}}\t{{.Ports}}"

# 检查资源使用
docker stats nc-file-processor
```

### 定期维护
```bash
# 清理未使用的镜像
docker image prune -f

# 清理未使用的容器
docker container prune -f

# 清理未使用的网络
docker network prune -f
```

## 安全建议

1. **修改默认密码**
   ```yaml
   environment:
     - POSTGRES_PASSWORD=your-strong-password
     - DB_PASSWORD=your-strong-password
   ```

2. **限制网络访问**
   ```yaml
   networks:
     - internal
   ```

3. **使用非root用户**
   ```dockerfile
   RUN adduser --disabled-password --gecos '' appuser
   USER appuser
   ```

4. **定期更新镜像**
   ```bash
   docker pull openjdk:8-jre-slim
   docker build -t nc-file-processor:latest .
   ```

## 支持和帮助

如果遇到问题，请检查：
1. Docker和Docker Compose版本
2. 系统资源（内存、磁盘空间）
3. 网络连接
4. 文件权限
5. 应用日志

更多帮助请参考：
- [Docker官方文档](https://docs.docker.com/)
- [Docker Compose文档](https://docs.docker.com/compose/)
- 项目README文件