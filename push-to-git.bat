@echo off
REM Git推送脚本

echo 准备推送代码到GitHub...
echo.

REM 检查Git状态
echo 1. 检查Git状态...
git status

echo.
echo 2. 推送到远程仓库...
echo 注意：如果需要身份验证，请输入GitHub用户名和密码（或Personal Access Token）

REM 推送代码
git push origin main

if %errorlevel% equ 0 (
    echo.
    echo ✅ 代码推送成功！
    echo 你可以在以下地址查看你的代码：
    echo https://github.com/wch2012asd/ncToPngJson
) else (
    echo.
    echo ❌ 推送失败，可能的原因：
    echo 1. 需要GitHub身份验证
    echo 2. 网络连接问题
    echo 3. 权限问题
    echo.
    echo 解决方案：
    echo 1. 配置GitHub Personal Access Token
    echo 2. 使用SSH密钥
    echo 3. 检查网络连接
)

echo.
pause