@echo off
REM NC文件处理器Docker构建脚本 (Windows版本)

echo 开始构建NC文件处理器Docker镜像...

REM 检查Maven是否安装
where mvn >nul 2>nul
if %errorlevel% neq 0 (
    echo 错误: Maven未安装，请先安装Maven
    pause
    exit /b 1
)

REM 清理并编译项目
echo 1. 清理并编译项目...
call mvn clean package -DskipTests

REM 检查jar文件是否生成
if not exist "target\nc-file-processor-1.0.0.jar" (
    echo 错误: jar文件未生成，请检查Maven构建
    pause
    exit /b 1
)

REM 构建Docker镜像
echo 2. 构建Docker镜像...
docker build -t nc-file-processor:latest .

REM 检查构建是否成功
if %errorlevel% equ 0 (
    echo 3. Docker镜像构建成功!
    echo 镜像名称: nc-file-processor:latest
    
    REM 显示镜像信息
    docker images | findstr nc-file-processor
    
    echo.
    echo 使用方法:
    echo 1. 单独运行容器:
    echo    docker run -d --name nc-processor -v %cd%\input:/app/input -v %cd%\output:/app/output nc-file-processor:latest
    echo.
    echo 2. 使用docker-compose运行（包含数据库）:
    echo    docker-compose up -d
    echo.
    echo 3. 查看日志:
    echo    docker logs nc-file-processor
) else (
    echo 错误: Docker镜像构建失败
    pause
    exit /b 1
)

pause