# RPG Battle System — Compile & Run (PowerShell)
# Run this script from the project root directory.
# Requires Java 17+ (javac and java in PATH).

Write-Host "=== Compiling RPG Battle System ===" -ForegroundColor Cyan

# Create build directory if it doesn't exist
if (-not (Test-Path "build")) {
    New-Item -ItemType Directory -Path "build" | Out-Null
}

# Collect all Java source files recursively
$sources = Get-ChildItem -Path "src" -Recurse -Filter "*.java" | ForEach-Object { $_.FullName }

if ($sources.Count -eq 0) {
    Write-Host "ERROR: No Java source files found in src/" -ForegroundColor Red
    exit 1
}

# Compile
javac -d build $sources
if ($LASTEXITCODE -ne 0) {
    Write-Host "=== Compilation failed ===" -ForegroundColor Red
    exit $LASTEXITCODE
}

Write-Host "Compilation successful!" -ForegroundColor Green
Write-Host ""
Write-Host "=== Running RPG Battle System ===" -ForegroundColor Cyan

# Run
java -cp build main.Main
if ($LASTEXITCODE -ne 0) {
    Write-Host "=== Game exited with code $LASTEXITCODE ===" -ForegroundColor Yellow
    pause
    exit $LASTEXITCODE
}

pause
