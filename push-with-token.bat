@echo off
chcp 65001 >nul 2>&1
cls
echo ==========================================
echo    GitHub Push with Token
echo ==========================================
echo.

echo Repository: https://github.com/wch2012asd/ncToPngJson
echo User: wch2012asd
echo.

echo IMPORTANT: You need a Personal Access Token
echo.
echo 1. Go to: https://github.com/settings/tokens
echo 2. Click "Generate new token (classic)"
echo 3. Name: NC File Processor
echo 4. Expiration: 90 days
echo 5. Permissions: Check "repo"
echo 6. Copy the generated token
echo.

set /p TOKEN=Enter your Personal Access Token: 

if "%TOKEN%"=="" (
    echo Error: Token cannot be empty
    pause
    exit /b 1
)

echo.
echo Configuring remote URL with token...
git remote set-url origin https://wch2012asd:%TOKEN%@github.com/wch2012asd/ncToPngJson.git

echo.
echo Pushing to GitHub...
git push origin main

if %errorlevel% equ 0 (
    echo.
    echo ========================================
    echo SUCCESS! Code pushed to GitHub
    echo ========================================
    echo.
    echo View your repository:
    echo https://github.com/wch2012asd/ncToPngJson
    echo.
    echo Your NC File Processor project is now online!
    
    REM Restore original URL for security
    git remote set-url origin https://github.com/wch2012asd/ncToPngJson.git
    echo.
    echo Remote URL restored for security.
) else (
    echo.
    echo ========================================
    echo PUSH FAILED
    echo ========================================
    echo.
    echo Please check:
    echo 1. Token is correct and starts with ghp_
    echo 2. Token has 'repo' permission
    echo 3. Network connection is working
    echo 4. Token is not expired
    
    REM Restore original URL
    git remote set-url origin https://github.com/wch2012asd/ncToPngJson.git
    echo.
    echo Remote URL restored.
)

echo.
pause