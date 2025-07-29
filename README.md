# NC文件处理器

一个用于监控目录变化并将NC文件转换为红黑图的Java应用程序，支持Docker容器化部署。

## 🚀 功能特性

- **自动监控**: 定时监控指定目录中的NC文件变化
- **格式转换**: 将NC文件转换为PNG格式的红黑图
- **数据库记录**: 使用PostgreSQL记录处理状态，避免重复处理
- **目录结构保持**: 输出目录保持与输入目录相同的结构
- **Docker支持**: 完整的Docker容器化方案
- **错误处理**: 完善的错误处理和日志记录

## 📋 系统要求

- Java 8+
- PostgreSQL数据库（可选，支持无数据库模式）
- Maven 3.6+
- Docker & Docker Compose（用于容器化部署）

## 🛠️ 快速开始

### 方式1: Docker部署（推荐）

```bash
# 1. 构建Docker镜像
./build-docker.sh  # Linux/Mac
# 或
build-docker.bat   # Windows

# 2. 启动服务
docker-compose up -d

# 3. 将NC文件放入input目录
# 4. 查看output目录中的处理结果
```

详细的Docker部署说明请参考 [README-Docker.md](README-Docker.md) 和 [DEPLOYMENT.md](DEPLOYMENT.md)

### 方式2: 本地运行

```bash
# 1. 配置数据库和目录
cp config.properties.example config.properties
# 编辑config.properties文件

# 2. 编译项目
mvn compile

# 3. 运行项目
mvn exec:java -Dexec.mainClass=com.example.NCFileProcessor

# 或打包后运行
mvn package
java -jar target/nc-file-processor-1.0.0.jar
```

## ⚙️ 配置说明

### 配置文件 (config.properties)

```properties
# 目录配置
input.directory=./input
output.directory=./output

# 数据库配置
database.url=jdbc:postgresql://localhost:5432/postgres
database.user=postgres
database.password=geovis123
database.schema=public
```

### 环境变量配置（Docker）

| 变量名 | 默认值 | 说明 |
|--------|--------|------|
| `INPUT_DIR` | `/app/input` | NC文件输入目录 |
| `OUTPUT_DIR` | `/app/output` | PNG输出目录 |
| `DB_URL` | `jdbc:postgresql://postgres:5432/ncprocessor` | 数据库连接URL |
| `DB_USER` | `ncuser` | 数据库用户名 |
| `DB_PASSWORD` | `ncpassword` | 数据库密码 |

## 📁 项目结构

```
nc-file-processor/
├── src/main/java/
│   ├── com/example/
│   │   ├── NCFileProcessor.java    # 主程序
│   │   ├── DatabaseManager.java    # 数据库管理
│   │   ├── ConfigManager.java      # 配置管理
│   │   ├── NcToPngUtils.java      # NC转PNG工具
│   │   └── ...
│   └── com/geovis/tools/
│       ├── json/JsonUtils.java     # JSON工具
│       └── png/PngUtils.java       # PNG工具
├── input/                          # NC文件输入目录
├── output/                         # PNG输出目录
├── config.properties              # 配置文件
├── Dockerfile                     # Docker镜像构建
├── docker-compose.yml            # Docker服务编排
├── pom.xml                       # Maven配置
└── README.md                     # 项目说明
```

## 🗄️ 数据库表结构

程序会自动创建以下表来记录文件处理状态：

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

## 🔧 开发和构建

### 编译项目
```bash
mvn compile
```

### 运行项目
```bash
mvn exec:java -Dexec.mainClass=com.example.NCFileProcessor
```

### 打包项目
```bash
mvn clean package -DskipTests
```

### 运行测试
```bash
mvn test
```

## 📊 使用示例

### 输入目录结构
```
input/
├── 2024-01-01/
│   ├── temperature.nc
│   └── humidity.nc
└── 2024-01-02/
    └── pressure.nc
```

### 输出目录结构
```
output/
├── 2024-01-01/
│   ├── temperature/
│   │   ├── Temperature_isobaric_2024-01-01_00_1234567890.png
│   │   └── Temperature_isobaric_2024-01-01_00_1234567890.json
│   └── humidity/
│       ├── Humidity_isobaric_2024-01-01_00_1234567890.png
│       └── Humidity_isobaric_2024-01-01_00_1234567890.json
└── 2024-01-02/
    └── pressure/
        ├── Pressure_isobaric_2024-01-02_00_1234567890.png
        └── Pressure_isobaric_2024-01-02_00_1234567890.json
```

## 🐛 故障排除

### 常见问题

1. **数据库连接失败**
   - 检查数据库服务是否运行
   - 验证连接参数是否正确
   - 程序支持无数据库模式运行

2. **文件处理失败**
   - 检查NC文件格式是否正确
   - 确认有足够的磁盘空间
   - 查看应用程序日志

3. **Docker启动失败**
   - 确保Docker服务正在运行
   - 检查端口是否被占用
   - 查看容器日志

### 查看日志
```bash
# 本地运行日志
tail -f logs/application.log

# Docker容器日志
docker logs nc-file-processor
```

## 📚 文档

- [Docker部署指南](README-Docker.md)
- [完整部署文档](DEPLOYMENT.md)

## 🤝 贡献

欢迎提交Issue和Pull Request来改进这个项目。

## 📄 许可证

本项目采用 MIT 许可证 - 查看 [LICENSE](LICENSE) 文件了解详情。

## 📞 支持

如果你遇到任何问题或需要帮助，请：

1. 查看文档和FAQ
2. 检查已有的Issues
3. 创建新的Issue描述问题

---

**注意**: 请确保在生产环境中修改默认的数据库密码和其他敏感配置。