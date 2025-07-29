# 手动配置GitHub Token指南

## 🎯 问题说明

由于Kiro IDE的Git集成可能影响凭据输入，我们需要手动配置GitHub Personal Access Token。

## 📋 解决方案

### 方法1: 使用Token URL（推荐）

1. **创建Personal Access Token**：
   - 访问：https://github.com/settings/tokens
   - 点击 "Generate new token (classic)"
   - 名称：`NC File Processor`
   - 过期时间：`90 days`
   - 权限：勾选 `repo`
   - 点击 "Generate token"
   - 复制Token（格式：`ghp_xxxxxxxxxx...`）

2. **配置远程URL**：
   ```bash
   git remote set-url origin https://wch2012asd:你的Token@github.com/wch2012asd/ncToPngJson.git
   ```

3. **推送代码**：
   ```bash
   git push origin main
   ```

### 方法2: 使用命令行Git

1. **打开Windows命令提示符**（不是PowerShell）
2. **导航到项目目录**：
   ```cmd
   cd /d D:\work\ZRHJCL\java\code\ZRHJCL\new-receiver
   ```

3. **配置凭据存储**：
   ```cmd
   git config --global credential.helper store
   ```

4. **推送代码**：
   ```cmd
   git push origin main
   ```
   - 用户名：`wch2012asd`
   - 密码：`你的Personal Access Token`

### 方法3: 使用GitHub CLI

1. **安装GitHub CLI**：
   - 下载：https://cli.github.com/

2. **登录**：
   ```bash
   gh auth login
   ```

3. **推送**：
   ```bash
   git push origin main
   ```

## 🔧 快速执行脚本

创建一个批处理文件 `push-with-token.bat`：

```batch
@echo off
echo 请输入你的Personal Access Token:
set /p TOKEN=Token: 

git remote set-url origin https://wch2012asd:%TOKEN%@github.com/wch2012asd/ncToPngJson.git
git push origin main

if %errorlevel% equ 0 (
    echo 推送成功！
    echo 查看：https://github.com/wch2012asd/ncToPngJson
    
    REM 恢复原始URL以保护Token
    git remote set-url origin https://github.com/wch2012asd/ncToPngJson.git
) else (
    echo 推送失败，请检查Token
    git remote set-url origin https://github.com/wch2012asd/ncToPngJson.git
)

pause
```

## ✅ 验证推送成功

推送成功后，访问以下地址查看你的代码：
https://github.com/wch2012asd/ncToPngJson

## 🔒 安全提醒

- Token具有完整的仓库访问权限
- 不要在代码中硬编码Token
- 定期更新Token（建议90天）
- 如果Token泄露，立即删除并重新生成

## 📞 需要帮助？

如果仍然遇到问题：
1. 检查Token权限是否包含 `repo`
2. 确认Token未过期
3. 检查网络连接
4. 尝试重新生成Token