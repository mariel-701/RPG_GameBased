#!/bin/bash
# RPG Battle System — Compile & Run (Linux / macOS)
# Requires Java 17+ (javac and java in PATH).

# Change to the directory where this script is located
cd "$(dirname "$0")" || exit 1

echo "=== Compiling RPG Battle System ==="

mkdir -p build

# Collect and compile all Java source files recursively
find src -name "*.java" > sources.txt
javac -d build @sources.txt
COMPILE_RESULT=$?
rm sources.txt

if [ $COMPILE_RESULT -ne 0 ]; then
    echo "=== Compilation failed ==="
    exit $COMPILE_RESULT
fi

echo "Compilation successful!"
echo ""
echo "=== Running RPG Battle System ==="

# Include assets directory on classpath so images load correctly
java -cp "build:assets" main.Main

EXIT_CODE=$?
if [ $EXIT_CODE -ne 0 ]; then
    echo "=== Game exited with code $EXIT_CODE ==="
    read -p "Press Enter to exit..."
    exit $EXIT_CODE
fi

read -p "Press Enter to exit..."
