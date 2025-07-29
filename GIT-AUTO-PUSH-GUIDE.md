# Git自动推送指南

## 🎯 目标

设置一次GitHub Token，以后推送代码时不需要重复输入凭据。

## 🚀 快速设置（推荐）

### 步骤1: 运行凭据设置脚本
```bash
setup-git-credentials.bat
```

这个脚本会：
- 配置Git凭据存储
- 打开GitHub Token页面
- 引导你完成首次推送
- 保存你的凭据供以后使用

### 步骤2: 以后的推送
```bash
git-push.bat
```

这个脚本会：
- 自动检查更改
- 提示输入提交信息
- 自动提交和推送
- 使用已保存的凭据

## 🔧 手动设置方法

### 方法1: 使用Git凭据存储
```bash
# 配置凭据存储
git config --global credential.helper store

# 首次推送（会提示输入凭据）
git push origin main
# 用户名: wch2012asd
# 密码: [你的Personal Access Token]

# 以后直接推送
git push origin main
```

### 方法2: 使用自动Token脚本
```bash
git-push-auto.bat
```

这个脚本会：
- 保存Token到本地文件
- 下次自动使用保存的Token
- 提供Token管理功能

## 🔑 创建GitHub Token

1. 访问：https://github.com/settings/tokens
2. 点击 "Generate new token (classic)"
3. 设置：
   - **Name**: NC File Processor
   - **Expiration**: 90 days (或更长)
   - **Scopes**: ✅ repo
4. 复制生成的Token（格式：`ghp_xxxxxxxxxx...`）

## 📋 可用脚本

| 脚本 | 功能 | 推荐度 |
|------|------|--------|
| `setup-git-credentials.bat` | 一次性设置凭据 | ⭐⭐⭐⭐⭐ |
| `git-push.bat` | 智能推送（检查更改+提交+推送） | ⭐⭐⭐⭐⭐ |
| `git-push-auto.bat` | 自动Token管理推送 | ⭐⭐⭐⭐ |
| `push-with-token.bat` | 每次输入Token推送 | ⭐⭐⭐ |

## 🔒 安全说明

### Token存储位置
- **Git凭据存储**: `~/.git-credentials` (加密存储)
- **本地Token文件**: `.git/github-token.txt` (已加入.gitignore)

### 安全最佳实践
- ✅ Token文件已加入.gitignore，不会被提交
- ✅ 使用Git官方凭据管理器
- ✅ 定期更新Token（建议90天）
- ✅ 如果Token泄露，立即删除并重新生成

### 删除保存的凭据
```bash
# 删除Git凭据存储
git config --global --unset credential.helper

# 删除本地Token文件
del .git\github-token.txt
```

## 🎉 使用流程

### 首次设置
1. 运行 `setup-git-credentials.bat`
2. 按提示创建GitHub Token
3. 完成首次推送测试

### 日常使用
1. 修改代码
2. 运行 `git-push.bat`
3. 输入提交信息
4. 自动推送完成

## ❓ 常见问题

### Q: 凭据保存在哪里？
A: Git会将凭据保存在系统的凭据管理器中，安全且加密。

### Q: Token过期了怎么办？
A: 重新生成Token，然后运行 `setup-git-credentials.bat` 重新设置。

### Q: 如何查看保存的凭据？
A: 运行 `git config --list | findstr credential`

### Q: 如何删除保存的凭据？
A: 删除 `~/.git-credentials` 文件或使用Windows凭据管理器。

## 🔗 相关文档

- [GITHUB-TOKEN-SETUP.md](GITHUB-TOKEN-SETUP.md) - 详细Token设置
- [MANUAL-TOKEN-SETUP.md](MANUAL-TOKEN-SETUP.md) - 手动配置方法
- [GIT-GUIDE.md](GIT-GUIDE.md) - Git基础使用

---

**推荐**: 使用 `setup-git-credentials.bat` 进行一次性设置，然后使用 `git-push.bat` 进行日常推送。