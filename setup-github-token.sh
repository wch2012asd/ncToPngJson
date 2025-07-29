#!/bin/bash

echo "=========================================="
echo "    GitHub Personal Access Token 配置工具"
echo "=========================================="
echo

echo "🔑 GitHub Token 配置向导"
echo

echo "📋 当前Git配置:"
git config --global user.name
git config --global user.email
echo

echo "🌐 仓库信息:"
echo "    仓库地址: https://github.com/wch2012asd/ncToPngJson"
echo "    用户名: wch2012asd"
echo

echo "📝 Token创建步骤:"
echo "    1. 访问: https://github.com/settings/tokens"
echo "    2. 点击 'Generate new token (classic)'"
echo "    3. 设置名称: NC File Processor"
echo "    4. 选择过期时间: 90 days (推荐)"
echo "    5. 勾选权限: repo (必需)"
echo "    6. 点击 'Generate token'"
echo "    7. 复制生成的token (以 ghp_ 开头)"
echo

echo "⚠️  重要提示:"
echo "    - Token只显示一次，请立即复制保存"
echo "    - Token格式: ghp_xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx"
echo "    - 不要分享给他人"
echo

read -p "是否打开GitHub Token页面? (y/n): " open_browser
if [[ $open_browser =~ ^[Yy]$ ]]; then
    if command -v xdg-open > /dev/null; then
        xdg-open https://github.com/settings/tokens
    elif command -v open > /dev/null; then
        open https://github.com/settings/tokens
    else
        echo "请手动访问: https://github.com/settings/tokens"
    fi
    echo "浏览器已打开GitHub Token页面"
    echo
fi

echo "🔧 配置Git凭据管理器..."
git config --global credential.helper store
echo "✅ 凭据管理器已配置"

echo
echo "📋 配置选项:"

echo
echo "选项1: 手动推送 (推荐)"
echo "----------------------"
echo "运行以下命令，然后在提示时输入凭据:"
echo "    git push origin main"
echo
echo "用户名: wch2012asd"
echo "密码: [输入你的Personal Access Token]"
echo

echo "选项2: 使用Token URL"
echo "-------------------"
read -p "是否配置Token到远程URL? (y/n): " use_token_url
if [[ $use_token_url =~ ^[Yy]$ ]]; then
    echo
    read -s -p "请输入你的Personal Access Token: " token
    echo
    if [[ ! -z "$token" ]]; then
        echo "配置远程URL..."
        git remote set-url origin https://wch2012asd:$token@github.com/wch2012asd/ncToPngJson.git
        echo "✅ 远程URL已更新"
        echo
        echo "🚀 尝试推送代码..."
        git push origin main
        
        if [ $? -eq 0 ]; then
            echo
            echo "✅ 推送成功!"
            echo "🎉 代码已上传到: https://github.com/wch2012asd/ncToPngJson"
        else
            echo
            echo "❌ 推送失败，请检查Token是否正确"
            echo "恢复原始URL..."
            git remote set-url origin https://github.com/wch2012asd/ncToPngJson.git
        fi
    fi
else
    echo
    echo "📝 手动推送步骤:"
    echo "    1. 运行: git push origin main"
    echo "    2. 输入用户名: wch2012asd"
    echo "    3. 输入密码: [你的Personal Access Token]"
    echo
    
    read -p "是否现在尝试手动推送? (y/n): " manual_push
    if [[ $manual_push =~ ^[Yy]$ ]]; then
        echo
        echo "🚀 开始推送..."
        echo "注意: 在密码提示处输入你的Personal Access Token"
        echo
        git push origin main
        
        if [ $? -eq 0 ]; then
            echo
            echo "✅ 推送成功!"
            echo "🎉 代码已上传到: https://github.com/wch2012asd/ncToPngJson"
        else
            echo
            echo "❌ 推送失败"
            echo "请检查:"
            echo "    1. Token是否正确"
            echo "    2. Token权限是否包含 repo"
            echo "    3. 网络连接是否正常"
        fi
    fi
fi

echo
echo "📚 更多帮助:"
echo "    - 详细指南: GITHUB-TOKEN-SETUP.md"
echo "    - GitHub文档: https://docs.github.com/en/authentication"
echo

echo "🔄 后续操作:"
echo "    - Token配置成功后，以后可以直接使用: git push origin main"
echo "    - 建议定期更新Token (90天)"
echo "    - 保存Token到安全的地方"
echo

read -p "按Enter键继续..."