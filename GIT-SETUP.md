# Git设置和推送指南

## 当前状态

你的代码已经成功提交到本地Git仓库，但推送到GitHub时需要身份验证。

## 解决方案

### 方式1: 使用Personal Access Token（推荐）

1. **创建GitHub Personal Access Token**：
   - 访问 https://github.com/settings/tokens
   - 点击 "Generate new token" -> "Generate new token (classic)"
   - 设置Token名称，如 "NC File Processor"
   - 选择权限：至少需要 `repo` 权限
   - 点击 "Generate token"
   - **重要**：复制生成的token，它只会显示一次

2. **配置Git使用Token**：
   ```bash
   # 方式1: 在推送时输入
   git push origin main
   # 用户名: 你的GitHub用户名
   # 密码: 刚才生成的Personal Access Token

   # 方式2: 配置Git记住凭据
   git config --global credential.helper store
   git push origin main
   # 输入一次后会自动保存
   ```

### 方式2: 使用SSH密钥

1. **生成SSH密钥**：
   ```bash
   ssh-keygen -t rsa -b 4096 -C "your-email@example.com"
   ```

2. **添加SSH密钥到GitHub**：
   - 复制公钥内容：`cat ~/.ssh/id_rsa.pub`
   - 访问 https://github.com/settings/keys
   - 点击 "New SSH key"
   - 粘贴公钥内容

3. **修改远程仓库URL**：
   ```bash
   git remote set-url origin git@github.com:wch2012asd/ncToPngJson.git
   git push origin main
   ```

### 方式3: 使用GitHub CLI

1. **安装GitHub CLI**：
   - 下载：https://cli.github.com/

2. **登录和推送**：
   ```bash
   gh auth login
   git push origin main
   ```

## 快速推送

运行以下脚本进行推送：

### Windows
```cmd
push-to-git.bat
```

### Linux/Mac
```bash
chmod +x push-to-git.sh
./push-to-git.sh
```

## 验证推送成功

推送成功后，你可以在以下地址查看代码：
https://github.com/wch2012asd/ncToPngJson

## 后续操作

推送成功后，你可以：

1. **查看仓库**：访问 GitHub 仓库页面
2. **创建Release**：为项目创建版本发布
3. **设置GitHub Pages**：展示项目文档
4. **邀请协作者**：添加其他开发者
5. **设置CI/CD**：自动化构建和部署

## 常见问题

### 问题1: 身份验证失败
**解决方案**：使用Personal Access Token替代密码

### 问题2: 权限被拒绝
**解决方案**：检查仓库权限或使用SSH密钥

### 问题3: 网络连接问题
**解决方案**：检查网络连接或使用代理

## 项目信息

- **仓库地址**：https://github.com/wch2012asd/ncToPngJson
- **分支**：main
- **最新提交**：feat: 完整的NC文件处理器项目

## 下一步

1. 推送代码到GitHub
2. 更新仓库描述和标签
3. 创建项目文档
4. 设置Issues和Projects
5. 考虑添加CI/CD流水线