@echo off
REM NC文件处理器Docker运行脚本 (Windows版本)

echo 启动NC文件处理器...

REM 创建必要的目录
if not exist "input" mkdir input
if not exist "output" mkdir output

REM 检查Docker镜像是否存在
docker images | findstr nc-file-processor >nul
if %errorlevel% neq 0 (
    echo Docker镜像不存在，请先运行 build-docker.bat 构建镜像
    pause
    exit /b 1
)

REM 选择运行方式
echo 请选择运行方式:
echo 1. 仅运行NC处理器（需要外部数据库）
echo 2. 运行完整服务（包含PostgreSQL数据库）
set /p choice=请输入选择 (1 或 2): 

if "%choice%"=="1" (
    echo 启动NC处理器容器...
    docker run -d ^
        --name nc-file-processor ^
        -v %cd%\input:/app/input ^
        -v %cd%\output:/app/output ^
        -v %cd%\config.properties:/app/config.properties ^
        --restart unless-stopped ^
        nc-file-processor:latest
    
    echo 容器已启动，容器名称: nc-file-processor
) else if "%choice%"=="2" (
    echo 启动完整服务（包含数据库）...
    docker-compose up -d
    
    echo 服务已启动，包含以下容器:
    echo - nc-file-processor: NC文件处理器
    echo - nc-postgres: PostgreSQL数据库
) else (
    echo 无效选择，退出
    pause
    exit /b 1
)

echo.
echo 查看运行状态:
echo docker ps
echo.
echo 查看日志:
echo docker logs nc-file-processor
echo.
echo 停止服务:
if "%choice%"=="1" (
    echo docker stop nc-file-processor
    echo docker rm nc-file-processor
) else (
    echo docker-compose down
)

pause