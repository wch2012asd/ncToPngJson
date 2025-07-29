# 项目文件结构

## 📁 核心文件

### 源代码
```
src/main/java/
├── com/example/
│   ├── NCFileProcessor.java      # 主程序
│   ├── DatabaseManager.java      # 数据库管理
│   ├── ConfigManager.java        # 配置管理
│   ├── NcToPngUtils.java         # NC转PNG工具
│   ├── NcReader.java             # NC文件读取
│   ├── NcDataModel.java          # 数据模型
│   ├── NcBeanModel.java          # Bean模型
│   ├── NcReaderUtils.java        # 读取工具
│   └── NumberUtils.java          # 数字工具
└── com/geovis/tools/
    ├── json/JsonUtils.java       # JSON工具
    └── png/PngUtils.java         # PNG工具
```

### 配置文件
```
├── config.properties             # 主配置文件
├── config.properties.example     # 配置示例
├── pom.xml                      # Maven配置
└── src/main/resources/
    └── logback.xml              # 日志配置
```

## 🐳 Docker文件

```
├── Dockerfile                   # Docker镜像构建
├── docker-compose.yml          # 服务编排
├── .dockerignore               # Docker忽略文件
└── init.sql                    # 数据库初始化
```

## 🔧 构建和运行脚本

```
├── build-docker.bat            # Windows Docker构建
├── build-docker.sh             # Linux Docker构建
├── run-docker.bat              # Windows Docker运行
├── run-docker.sh               # Linux Docker运行
└── run.bat                     # 本地运行脚本
```

## 📚 文档

```
├── README.md                   # 项目主说明
├── README-Docker.md            # Docker使用指南
├── DEPLOYMENT.md               # 完整部署文档
├── LICENSE                     # 许可证
└── PROJECT-STRUCTURE.md        # 本文件
```

## 🔑 Git工具

```
├── .gitignore                  # Git忽略文件
├── GIT-GUIDE.md               # Git使用指南
├── GITHUB-TOKEN-SETUP.md      # Token详细设置
├── MANUAL-TOKEN-SETUP.md      # 手动Token配置
├── github-token-helper.bat    # Token配置助手
└── push-with-token.bat        # Token推送脚本
```

## 📂 运行时目录

```
├── input/                      # NC文件输入目录
├── output/                     # PNG输出目录
├── logs/                       # 日志目录（运行时生成）
└── target/                     # Maven构建输出
    └── nc-file-processor-1.0.0.jar
```

## 🗑️ 已清理的文件

以下文件已被删除以简化项目结构：

- ❌ `git-push-interactive.bat` - 重复的推送脚本
- ❌ `git-push-simple.bat` - 简化版推送脚本
- ❌ `git-push-en.bat` - 英文版推送脚本
- ❌ `push-to-git.bat` - 原始推送脚本
- ❌ `setup-github-token.bat` - 复杂Token设置脚本
- ❌ `setup-github-token.sh` - Linux Token设置脚本
- ❌ `check-encoding.bat` - 编码检查脚本
- ❌ `QUICK-TOKEN-GUIDE.md` - 快速指南（已合并）
- ❌ `GIT-SETUP.md` - Git设置文档（已合并）
- ❌ `docker-config.properties` - Docker配置文件
- ❌ `logs/*.log` - 临时日志文件
- ❌ `target/*.jar.original` - 构建备份文件

## 📊 项目统计

- **总文件数**: ~35个核心文件
- **源代码文件**: 11个Java文件
- **文档文件**: 6个Markdown文件
- **配置文件**: 8个配置和脚本文件
- **Docker文件**: 4个容器化文件

项目现在结构清晰，只保留必要的文件，便于维护和使用。