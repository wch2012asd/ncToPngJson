# NC文件处理器

这是一个Java后端程序，用于监控目录变化并将NetCDF文件转换为红黑图像。

## 功能特性

- 定时每3秒检查配置的输入目录
- 使用PostgreSQL数据库记录文件转换状态
- 根据数据库记录智能判断新文件夹和新文件
- 读取文件夹中的NC文件
- 将NC文件数据转换为红黑渐变图像
- 保持输入目录结构，在输出目录中创建相同的文件夹结构
- 保存图像到配置的输出目录
- 记录转换成功/失败状态到数据库

## 配置文件

程序使用 `config.properties` 文件进行配置：

```properties
# NC File Processor Configuration
input.directory=./input
output.directory=./output

# Database Configuration
database.url=jdbc:postgresql://localhost:5432/postgres
database.user=postgres
database.password=geovis123
database.schema=public
```

### 配置项说明

**目录配置**:
- `input.directory`: 监控的输入目录路径
- `output.directory`: 生成图像的输出目录路径

**数据库配置**:
- `database.url`: PostgreSQL数据库连接URL
- `database.user`: 数据库用户名
- `database.password`: 数据库密码
- `database.schema`: 数据库模式名（通常为public）

### 数据库表结构

程序会自动创建 `file_conversion_records` 表：

```sql
CREATE TABLE {schema}.file_conversion_records (
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

注：`{schema}` 会被配置文件中的 `database.schema` 值替换。

## 运行方式

### 编译项目
```bash
mvn clean compile
```

### 运行程序
```bash
run.bat
```

或者直接使用Java命令：
```bash
java -cp "target/classes" com.example.NCFileProcessor
```

## 使用说明

1. 确保PostgreSQL数据库服务正在运行
2. 根据实际情况修改 `config.properties` 中的数据库连接配置
3. 确保配置文件中的目录路径正确
4. 启动程序后，它会自动连接数据库并创建必要的表
4. 程序每3秒检查输入目录，根据数据库记录判断新文件夹和新文件
5. 在输入目录中添加包含NC文件的新文件夹
6. 程序会自动处理NC文件并在输出目录生成对应的PNG图像
7. 转换记录会保存到数据库中，包括成功和失败的状态
8. 输出目录会保持与输入目录相同的文件夹结构

## 目录结构示例

```
input/
├── weather-data-2024/
│   ├── temperature.nc
│   └── humidity.nc
└── climate-data/
    └── precipitation.nc

output/
├── weather-data-2024/
│   ├── temperature.png
│   └── humidity.png
└── climate-data/
    └── precipitation.png
```

## 图像生成规则

- 使用红黑渐变色彩映射
- 数据最小值对应黑色 (RGB: 0,0,0)
- 数据最大值对应红色 (RGB: 255,0,0)
- 基于文件内容生成图像数据
- 输出格式为PNG
- 图像尺寸根据文件大小自动调整

## 程序控制

- 启动程序：运行 `run.bat`
- 停止程序：按 `Ctrl+C`
- 程序会在控制台显示处理进度和结果# ncToPngJson
