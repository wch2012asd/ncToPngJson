@echo off
chcp 65001 >nul
echo ==========================================
echo    GitHub Personal Access Token 配置工具
echo ==========================================
echo.

echo 🔑 GitHub Token 配置向导
echo.

echo 📋 当前Git配置:
git config --global user.name
git config --global user.email
echo.

echo 🌐 仓库信息:
echo    仓库地址: https://github.com/wch2012asd/ncToPngJson
echo    用户名: wch2012asd
echo.

echo 📝 Token创建步骤:
echo    1. 访问: https://github.com/settings/tokens
echo    2. 点击 "Generate new token (classic)"
echo    3. 设置名称: NC File Processor
echo    4. 选择过期时间: 90 days (推荐)
echo    5. 勾选权限: repo (必需)
echo    6. 点击 "Generate token"
echo    7. 复制生成的token (以 ghp_ 开头)
echo.

echo ⚠️  重要提示:
echo    - Token只显示一次，请立即复制保存
echo    - Token格式: ghp_xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
echo    - 不要分享给他人
echo.

set /p open_browser=是否打开GitHub Token页面? (y/n): 
if /i "%open_browser%"=="y" (
    start https://github.com/settings/tokens
    echo 浏览器已打开GitHub Token页面
    echo.
)

echo 🔧 配置Git凭据管理器...
git config --global credential.helper store
echo ✅ 凭据管理器已配置

echo.
echo 📋 配置选项:

echo.
echo 选项1: 手动推送 (推荐)
echo ----------------------
echo 运行以下命令，然后在提示时输入凭据:
echo    git push origin main
echo.
echo 用户名: wch2012asd
echo 密码: [输入你的Personal Access Token]
echo.

echo 选项2: 使用Token URL
echo -------------------
set /p use_token_url=是否配置Token到远程URL? (y/n): 
if /i "%use_token_url%"=="y" (
    echo.
    set /p token=请输入你的Personal Access Token: 
    if not "!token!"=="" (
        echo 配置远程URL...
        git remote set-url origin https://wch2012asd:!token!@github.com/wch2012asd/ncToPngJson.git
        echo ✅ 远程URL已更新
        echo.
        echo 🚀 尝试推送代码...
        git push origin main
        
        if !errorlevel! equ 0 (
            echo.
            echo ✅ 推送成功!
            echo 🎉 代码已上传到: https://github.com/wch2012asd/ncToPngJson
        ) else (
            echo.
            echo ❌ 推送失败，请检查Token是否正确
            echo 恢复原始URL...
            git remote set-url origin https://github.com/wch2012asd/ncToPngJson.git
        )
    )
) else (
    echo.
    echo 📝 手动推送步骤:
    echo    1. 运行: git push origin main
    echo    2. 输入用户名: wch2012asd
    echo    3. 输入密码: [你的Personal Access Token]
    echo.
    
    set /p manual_push=是否现在尝试手动推送? (y/n): 
    if /i "%manual_push%"=="y" (
        echo.
        echo 🚀 开始推送...
        echo 注意: 在密码提示处输入你的Personal Access Token
        echo.
        git push origin main
        
        if !errorlevel! equ 0 (
            echo.
            echo ✅ 推送成功!
            echo 🎉 代码已上传到: https://github.com/wch2012asd/ncToPngJson
        ) else (
            echo.
            echo ❌ 推送失败
            echo 请检查:
            echo    1. Token是否正确
            echo    2. Token权限是否包含 repo
            echo    3. 网络连接是否正常
        )
    )
)

echo.
echo 📚 更多帮助:
echo    - 详细指南: GITHUB-TOKEN-SETUP.md
echo    - GitHub文档: https://docs.github.com/en/authentication
echo.

echo 🔄 后续操作:
echo    - Token配置成功后，以后可以直接使用: git push origin main
echo    - 建议定期更新Token (90天)
echo    - 保存Token到安全的地方
echo.

pause