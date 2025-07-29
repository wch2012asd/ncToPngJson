@echo off
chcp 65001 >nul 2>&1
echo ==========================================
echo    GitHub Token Setup Helper
echo ==========================================
echo.

echo Step 1: Create Personal Access Token
echo ====================================
echo.
echo 1. Visit GitHub Token page:
echo    https://github.com/settings/tokens
echo.
echo 2. Click "Generate new token (classic)"
echo.
echo 3. Fill in the information:
echo    - Note: NC File Processor
echo    - Expiration: 90 days
echo    - Scopes: Check "repo"
echo.
echo 4. Click "Generate token"
echo.
echo 5. Copy the generated token (format: ghp_xxxxxxxx...)
echo.

set /p continue=Press Enter after creating the token...

echo.
echo Step 2: Configure Git
echo =====================
echo.
git config --global credential.helper store
echo Git credential manager configured
echo.

echo Step 3: Push Code
echo =================
echo.
echo Now run the push command:
echo git push origin main
echo.
echo When prompted, enter:
echo Username: wch2012asd
echo Password: [Your Personal Access Token]
echo.

set /p push_now=Push now? (y/n): 
if /i "%push_now%"=="y" (
    echo.
    echo Starting push...
    echo Please enter your Personal Access Token when prompted
    echo.
    git push origin main
    
    if %errorlevel% equ 0 (
        echo.
        echo Push successful!
        echo View at: https://github.com/wch2012asd/ncToPngJson
    ) else (
        echo.
        echo Push failed, please check if Token is correct
    )
)

echo.
echo Done! You can use 'git push origin main' directly next time
pause