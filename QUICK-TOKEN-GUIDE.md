# 🚀 GitHub Token 快速配置指南

## 📋 简单3步完成配置

### 步骤1: 创建Personal Access Token

1. **打开链接**: https://github.com/settings/tokens
2. **点击**: "Generate new token" → "Generate new token (classic)"
3. **填写信息**:
   - **Note**: `NC File Processor`
   - **Expiration**: `90 days`
   - **Select scopes**: ✅ 勾选 `repo`
4. **点击**: "Generate token"
5. **复制Token**: 格式类似 `ghp_1234567890abcdef...`

### 步骤2: 配置Git凭据

在命令行运行：
```bash
git config --global credential.helper store
```

### 步骤3: 推送代码

运行推送命令：
```bash
git push origin main
```

在提示时输入：
- **Username**: `wch2012asd`
- **Password**: `[粘贴你的Personal Access Token]`

## 🎯 一键执行

运行配置助手：
```bash
github-token-helper.bat
```

## ✅ 验证成功

推送成功后，访问查看你的代码：
https://github.com/wch2012asd/ncToPngJson

## 🔧 如果遇到问题

1. **Token格式错误**: 确保以 `ghp_` 开头
2. **权限不足**: 确保勾选了 `repo` 权限
3. **网络问题**: 检查代理设置
4. **Token过期**: 重新生成新Token

## 📞 需要帮助？

查看详细文档：
- [GITHUB-TOKEN-SETUP.md](GITHUB-TOKEN-SETUP.md)
- [GIT-SETUP.md](GIT-SETUP.md)

---

**记住**: Token只显示一次，请立即保存！