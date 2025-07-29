# Git使用指南

## 🚀 快速推送代码

### 方法1: 使用Token推送脚本（推荐）
```bash
push-with-token.bat
```

### 方法2: 手动推送
```bash
git add .
git commit -m "你的提交信息"
git push origin main
```

## 🔑 GitHub Token配置

### 创建Personal Access Token
1. 访问：https://github.com/settings/tokens
2. 点击 "Generate new token (classic)"
3. 设置名称：NC File Processor
4. 选择过期时间：90 days
5. 勾选权限：repo
6. 复制生成的token

### 使用Token
运行 `push-with-token.bat` 并输入你的token

## 📚 详细文档

- **完整Token设置**：[GITHUB-TOKEN-SETUP.md](GITHUB-TOKEN-SETUP.md)
- **手动配置方法**：[MANUAL-TOKEN-SETUP.md](MANUAL-TOKEN-SETUP.md)
- **Git详细设置**：[GIT-SETUP.md](GIT-SETUP.md)

## 🔗 仓库地址

https://github.com/wch2012asd/ncToPngJson