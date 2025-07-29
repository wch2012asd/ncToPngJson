@echo off
echo Starting NC File Processor (monitoring mode)...
echo This program will monitor the input directory every 3 seconds and convert NC files to red-black images.
echo The output directory will maintain the same structure as the input directory.
echo Press Ctrl+C to stop the program.
echo.

echo Checking PostgreSQL database connection...
echo If database connection fails, the program will continue without database features.
echo.

REM 使用Maven运行程序以确保所有依赖都在classpath中
mvn exec:java -Dexec.mainClass=com.example.NCFileProcessor -q