@echo off
chcp 65001 >nul 2>&1
cls
echo ==========================================
echo    Git Credentials Setup
echo ==========================================
echo.

echo 🔧 Configuring Git credential storage...
echo.

REM 配置Git使用凭据存储
git config --global credential.helper store
echo ✅ Git credential helper configured

REM 配置用户信息（如果还没有配置）
git config --global user.name "wcheng"
git config --global user.email "442939757@qq.com"
echo ✅ Git user info configured

echo.
echo 🔑 GitHub Token Setup
echo.
echo 1. Go to: https://github.com/settings/tokens
echo 2. Click "Generate new token (classic)"
echo 3. Name: NC File Processor
echo 4. Expiration: 90 days (or longer)
echo 5. Permissions: Check "repo"
echo 6. Copy the generated token
echo.

set /p open_browser=Open GitHub token page? (y/n): 
if /i "%open_browser%"=="y" (
    start https://github.com/settings/tokens
    echo Browser opened to GitHub token page
    echo.
)

echo 📝 Now we'll do a test push to save your credentials
echo.
echo When prompted:
echo   Username: wch2012asd
echo   Password: [Enter your Personal Access Token]
echo.
echo ⚠️  Important: Enter the TOKEN as password, not your GitHub password!
echo.

set /p ready=Ready to test push? (y/n): 
if /i "%ready%"=="y" (
    echo.
    echo 🚀 Testing push...
    git push origin main
    
    if %errorlevel% equ 0 (
        echo.
        echo ========================================
        echo ✅ SUCCESS! Credentials saved
        echo ========================================
        echo.
        echo 🎉 Your GitHub token is now saved!
        echo 💡 From now on, you can use: git push origin main
        echo    (No need to enter credentials again)
        echo.
        echo 📍 Repository: https://github.com/wch2012asd/ncToPngJson
    ) else (
        echo.
        echo ❌ Push failed. Please check your token and try again.
    )
) else (
    echo.
    echo Setup completed. Run 'git push origin main' when ready.
)

echo.
echo 📚 What was configured:
echo   - Git credential helper: store
echo   - User name: wcheng
echo   - User email: 442939757@qq.com
echo.
echo 🔄 Future pushes:
echo   Just use: git push origin main
echo   (Credentials will be remembered)
echo.

pause