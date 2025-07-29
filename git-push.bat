@echo off
chcp 65001 >nul 2>&1
cls
echo ==========================================
echo    Quick Git Push
echo ==========================================
echo.

REM 检查是否有未提交的更改
git status --porcelain >nul 2>&1
if %errorlevel% neq 0 (
    echo ❌ Not a git repository
    pause
    exit /b 1
)

REM 显示当前状态
echo 📋 Repository Status:
git status --short
echo.

REM 检查是否有更改需要提交
for /f %%i in ('git status --porcelain 2^>nul ^| find /c /v ""') do set CHANGES=%%i

if %CHANGES% gtr 0 (
    echo 📝 Found %CHANGES% changes to commit
    echo.
    
    REM 显示更改的文件
    echo Changed files:
    git status --short
    echo.
    
    set /p commit_msg=Enter commit message (or press Enter for default): 
    if "%commit_msg%"=="" (
        set commit_msg=Update project files
    )
    
    echo.
    echo 📦 Adding and committing changes...
    git add .
    git commit -m "%commit_msg%"
    
    if %errorlevel% neq 0 (
        echo ❌ Commit failed
        pause
        exit /b 1
    )
    echo ✅ Changes committed
) else (
    echo ✅ No changes to commit
)

echo.
echo 🚀 Pushing to GitHub...
git push origin main

if %errorlevel% equ 0 (
    echo.
    echo ========================================
    echo ✅ SUCCESS! Code pushed to GitHub
    echo ========================================
    echo.
    echo 🎉 View your repository:
    echo https://github.com/wch2012asd/ncToPngJson
    echo.
    
    REM 显示最新的提交
    echo 📋 Latest commit:
    git log --oneline -1
) else (
    echo.
    echo ========================================
    echo ❌ PUSH FAILED
    echo ========================================
    echo.
    echo 🔧 Possible solutions:
    echo 1. Run setup-git-credentials.bat to configure token
    echo 2. Check network connection
    echo 3. Verify token permissions
    echo.
    echo 💡 Need to setup credentials? Run: setup-git-credentials.bat
)

echo.
pause