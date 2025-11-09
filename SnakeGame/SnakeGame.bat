@echo off
cd /d "C:\Users\smrut\OneDrive\Documents\IDEA\Snake game\SnakeGame"
echo Compiling latest version...
javac -d bin src\snakegame\*.java
if %errorlevel% neq 0 (
    echo Compilation failed. Press any key to exit.
    pause >nul
    exit /b
)
echo Running Snake Game...
cd bin
java snakegame.SnakeGame
pause
