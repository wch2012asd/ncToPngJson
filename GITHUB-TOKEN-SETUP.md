# GitHub Personal Access Token 设置指南

## 🔑 什么是Personal Access Token？

Personal Access Token (PAT) 是GitHub提供的安全认证方式，用于替代传统的用户名密码认证。从2021年8月13日开始，GitHub不再接受密码认证。

## 📋 创建Personal Access Token

### 步骤1: 访问GitHub Token设置页面

1. 登录你的GitHub账户
2. 点击右上角头像 → **Settings**
3. 在左侧菜单中找到 **Developer settings**
4. 点击 **Personal access tokens** → **Tokens (classic)**

**直接链接**: https://github.com/settings/tokens

### 步骤2: 生成新Token

1. 点击 **Generate new token** → **Generate new token (classic)**
2. 填写Token信息：
   - **Note**: 输入描述，如 "NC File Processor Project"
   - **Expiration**: 选择过期时间（建议选择90天或自定义）
   - **Select scopes**: 勾选权限

### 步骤3: 选择权限范围

**必需权限**（至少勾选这些）：
- ✅ **repo** - 完整的仓库访问权限
  - repo:status
  - repo_deployment
  - public_repo
  - repo:invite
  - security_events

**可选权限**（根据需要）：
- ✅ **workflow** - 如果使用GitHub Actions
- ✅ **write:packages** - 如果发布包
- ✅ **delete_repo** - 如果需要删除仓库

### 步骤4: 生成并复制Token

1. 点击 **Generate token**
2. **重要**: 立即复制生成的token（格式如：`ghp_xxxxxxxxxxxxxxxxxxxx`）
3. **注意**: Token只显示一次，请妥善保存

## 🔧 配置Git使用Token

### 方法1: 使用Git凭据管理器（推荐）

```bash
# 配置Git使用凭据存储
git config --global credential.helper store

# 推送时会提示输入凭据
git push origin main
# 用户名: wch2012asd
# 密码: 你的Personal Access Token
```

### 方法2: 在URL中包含Token

```bash
# 临时使用（不推荐长期使用）
git push https://wch2012asd:你的token@github.com/wch2012asd/ncToPngJson.git main

# 或者修改远程URL
git remote set-url origin https://wch2012asd:你的token@github.com/wch2012asd/ncToPngJson.git
git push origin main
```

### 方法3: 使用环境变量

```bash
# Windows
set GITHUB_TOKEN=你的token
git push origin main

# Linux/Mac
export GITHUB_TOKEN=你的token
git push origin main
```

## 🛠️ 自动化配置脚本

我已经为你创建了配置脚本，运行以下命令：

```bash
# Windows
setup-github-token.bat

# Linux/Mac
./setup-github-token.sh
```

## 🔒 安全最佳实践

### Token安全性
- ✅ 定期更新Token（建议90天）
- ✅ 只授予必要的权限
- ✅ 不要在代码中硬编码Token
- ✅ 使用环境变量或凭据管理器
- ✅ 如果Token泄露，立即删除并重新生成

### 存储方式
- ✅ **推荐**: Git凭据管理器
- ✅ **可选**: 环境变量
- ❌ **不推荐**: 硬编码在脚本中
- ❌ **不推荐**: 提交到代码仓库

## 🚀 快速推送代码

Token配置完成后，你可以：

```bash
# 推送代码
git push origin main

# 查看推送结果
# 成功后访问: https://github.com/wch2012asd/ncToPngJson
```

## 🔄 Token管理

### 查看现有Token
1. 访问: https://github.com/settings/tokens
2. 查看所有已创建的Token
3. 可以编辑、删除或重新生成

### 更新Token
1. 在Token过期前重新生成
2. 更新本地Git配置
3. 测试推送功能

### 删除Token
1. 如果Token泄露或不再使用
2. 在GitHub设置中删除
3. 重新生成新Token

## ❓ 常见问题

### Q: Token在哪里输入？
A: 在Git推送时，用户名输入GitHub用户名，密码处输入Token

### Q: Token格式是什么样的？
A: 以 `ghp_` 开头，后跟40个字符，如：`ghp_1234567890abcdef1234567890abcdef12345678`

### Q: Token过期了怎么办？
A: 重新生成新Token，更新本地配置

### Q: 忘记保存Token怎么办？
A: 删除旧Token，重新生成新的

### Q: 推送时还是提示认证失败？
A: 检查Token权限，确保包含 `repo` 权限

## 📞 获取帮助

如果遇到问题：
1. 检查Token权限设置
2. 确认Token未过期
3. 验证网络连接
4. 查看Git配置：`git config --list`
5. 参考GitHub官方文档：https://docs.github.com/en/authentication

---

**下一步**: 配置完Token后，运行 `git push origin main` 推送你的代码！