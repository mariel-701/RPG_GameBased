@echo off
REM RPG Battle System — Compile & Run (Windows CMD)
REM Requires Java 17+ (javac and java in PATH).
REM Run this script from the project root directory.

echo === Compiling RPG Battle System ===

REM Create build directory if it doesn't exist
if not exist build mkdir build

REM Collect and compile all Java source files recursively
dir /s /b src\*.java > sources_temp.txt
javac -d build @sources_temp.txt
set COMPILE_RESULT=%ERRORLEVEL%
del sources_temp.txt

if %COMPILE_RESULT% neq 0 (
    echo === Compilation failed ===
    pause
    exit /b %COMPILE_RESULT%
)

echo Compilation successful!
echo.
echo === Running RPG Battle System ===

java -cp build main.Main
if %ERRORLEVEL% neq 0 (
    echo === Game exited with code %ERRORLEVEL% ===
    pause
    exit /b %ERRORLEVEL%
)

pause
