@echo off
chcp 65001 >nul
echo ==========================================
echo    NC文件处理器 - Git推送工具
echo ==========================================
echo.

echo 📋 当前项目信息:
echo    仓库: https://github.com/wch2012asd/ncToPngJson
echo    分支: main
echo    状态: 准备推送
echo.

echo 🔐 GitHub身份验证说明:
echo    由于GitHub已停止支持密码验证，你需要使用以下方式之一:
echo.
echo    方式1: Personal Access Token (推荐)
echo    1. 访问: https://github.com/settings/tokens
echo    2. 点击 "Generate new token" -> "Generate new token (classic)"
echo    3. 选择权限: 至少勾选 "repo" 权限
echo    4. 复制生成的token
echo    5. 在下面的密码提示中输入token (不是GitHub密码)
echo.
echo    方式2: GitHub CLI
echo    1. 安装GitHub CLI: https://cli.github.com/
echo    2. 运行: gh auth login
echo.

echo ⚠️  重要提示:
echo    - 用户名: 输入你的GitHub用户名 (wch2012asd)
echo    - 密码: 输入Personal Access Token (不是GitHub密码!)
echo    - Token只显示一次，请妥善保存
echo.

set /p confirm=是否继续推送? (y/n): 
if /i "%confirm%" neq "y" (
    echo 取消推送操作
    pause
    exit /b 0
)

echo.
echo 🚀 开始推送代码...
echo.

REM 推送代码
git push origin main

if %errorlevel% equ 0 (
    echo.
    echo ✅ 推送成功!
    echo.
    echo 🎉 你的代码已成功上传到GitHub!
    echo 📍 查看地址: https://github.com/wch2012asd/ncToPngJson
    echo.
    echo 📋 项目包含:
    echo    ✓ 完整的NC文件处理器源代码
    echo    ✓ Docker容器化配置
    echo    ✓ 详细的部署文档
    echo    ✓ 构建和运行脚本
    echo    ✓ 数据库配置和初始化脚本
    echo.
    echo 🔄 下次推送:
    echo    如果凭据已保存，下次可以直接运行: git push origin main
) else (
    echo.
    echo ❌ 推送失败!
    echo.
    echo 🔧 可能的解决方案:
    echo    1. 检查网络连接
    echo    2. 确认使用Personal Access Token而不是密码
    echo    3. 验证Token权限包含 "repo"
    echo    4. 尝试重新生成Token
    echo.
    echo 📚 详细帮助请查看: GIT-SETUP.md
)

echo.
pause