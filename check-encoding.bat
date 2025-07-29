@echo off
echo ==========================================
echo    System Encoding Check
echo ==========================================
echo.

echo Current Code Page:
chcp
echo.

echo Setting UTF-8 encoding...
chcp 65001
echo.

echo Testing Chinese characters:
echo 测试中文字符显示
echo GitHub Token 配置
echo 推送代码到仓库
echo.

echo If you see Chinese characters correctly above, encoding is working.
echo If you see question marks or boxes, there may be font issues.
echo.

echo Git Configuration:
git config --global --list | findstr user
echo.

echo Repository Status:
git status
echo.

pause