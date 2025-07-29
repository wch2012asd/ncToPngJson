@echo off
chcp 65001 >nul 2>&1
cls
echo ==========================================
echo    Git Push Tool - NC File Processor
echo ==========================================
echo.

echo Repository: https://github.com/wch2012asd/ncToPngJson
echo Branch: main
echo.

echo STEP 1: Create GitHub Personal Access Token
echo ===========================================
echo.
echo 1. Go to: https://github.com/settings/tokens
echo 2. Click "Generate new token (classic)"
echo 3. Set name: NC File Processor
echo 4. Set expiration: 90 days
echo 5. Check permissions: repo
echo 6. Click "Generate token"
echo 7. Copy the token (starts with ghp_)
echo.

set /p token_ready=Have you created the token? (y/n): 
if /i not "%token_ready%"=="y" (
    echo Please create the token first, then run this script again.
    start https://github.com/settings/tokens
    pause
    exit /b 0
)

echo.
echo STEP 2: Configure Git
echo =====================
git config --global credential.helper store
echo Git credential helper configured.
echo.

echo STEP 3: Push to GitHub
echo ======================
echo.
echo Running: git push origin main
echo.
echo When prompted:
echo   Username: wch2012asd
echo   Password: [Enter your Personal Access Token]
echo.

set /p ready=Ready to push? (y/n): 
if /i "%ready%"=="y" (
    echo.
    git push origin main
    
    if %errorlevel% equ 0 (
        echo.
        echo ========================================
        echo SUCCESS! Code pushed to GitHub
        echo ========================================
        echo.
        echo View your repository at:
        echo https://github.com/wch2012asd/ncToPngJson
        echo.
        echo Your project includes:
        echo - Complete NC file processor source code
        echo - Docker containerization setup
        echo - Detailed deployment documentation
        echo - Build and run scripts
        echo - Database configuration
        echo.
    ) else (
        echo.
        echo ========================================
        echo PUSH FAILED
        echo ========================================
        echo.
        echo Possible solutions:
        echo 1. Check if token is correct
        echo 2. Ensure token has 'repo' permission
        echo 3. Check network connection
        echo 4. Try regenerating the token
        echo.
    )
) else (
    echo.
    echo Push cancelled. Run this script again when ready.
)

echo.
echo Next time you can simply use: git push origin main
echo.
pause