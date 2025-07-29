@echo off
chcp 65001 >nul
echo ==========================================
echo    Git推送 - 简化版本
echo ==========================================
echo.

echo 📋 推送信息:
echo    仓库: https://github.com/wch2012asd/ncToPngJson
echo    分支: main
echo.

echo 🔑 身份验证方法:
echo.
echo 方法1: 使用Personal Access Token
echo --------------------------------
echo 1. 访问: https://github.com/settings/tokens
echo 2. 点击 "Generate new token (classic)"
echo 3. 勾选 "repo" 权限
echo 4. 复制生成的token
echo 5. 使用以下命令推送:
echo.
echo    git push https://你的用户名:你的token@github.com/wch2012asd/ncToPngJson.git main
echo.
echo 示例:
echo    git push https://wch2012asd:ghp_xxxxxxxxxxxx@github.com/wch2012asd/ncToPngJson.git main
echo.

echo 方法2: 使用GitHub CLI (推荐)
echo ---------------------------
echo 1. 下载安装: https://cli.github.com/
echo 2. 运行: gh auth login
echo 3. 选择 GitHub.com
echo 4. 选择 HTTPS
echo 5. 选择 Login with a web browser
echo 6. 完成后运行: git push origin main
echo.

echo 方法3: 手动输入凭据
echo ------------------
set /p manual=是否尝试手动输入凭据推送? (y/n): 
if /i "%manual%"=="y" (
    echo.
    echo 注意: 密码处请输入Personal Access Token，不是GitHub密码
    echo.
    git push origin main
    
    if %errorlevel% equ 0 (
        echo.
        echo ✅ 推送成功!
        echo 查看地址: https://github.com/wch2012asd/ncToPngJson
    ) else (
        echo.
        echo ❌ 推送失败，请尝试其他方法
    )
) else (
    echo.
    echo 请选择上述方法之一进行推送
)

echo.
pause