@echo off
chcp 65001 >nul 2>&1
echo ==========================================
echo    Quick Git Push
echo ==========================================
echo.

echo 📋 Current status:
git status --short
echo.

set /p msg=Enter commit message: 
if "%msg%"=="" set msg=Update files

echo.
echo 📦 Committing changes...
git add .
git commit -m "%msg%"

echo.
echo 🚀 Pushing to GitHub...
git push origin main

if %errorlevel% equ 0 (
    echo.
    echo ✅ SUCCESS! Code pushed to GitHub
    echo 🎉 View at: https://github.com/wch2012asd/ncToPngJson
) else (
    echo.
    echo ❌ Push failed. Check your credentials.
)

echo.
pause