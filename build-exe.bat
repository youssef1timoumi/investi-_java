@echo off
echo ========================================
echo Building INVESTI Desktop Application
echo ========================================

echo.
echo [1/3] Cleaning previous builds...
call mvn clean

echo.
echo [2/3] Building JAR with dependencies...
call mvn package
if %ERRORLEVEL% NEQ 0 (
    echo ERROR: Maven build failed!
    pause
    exit /b 1
)

echo.
echo [3/3] Creating Windows EXE with jpackage...
jpackage ^
  --input target ^
  --name INVESTI ^
  --main-jar investi-integration-1.0-SNAPSHOT.jar ^
  --main-class edu.connexion3a8.InvestiApp ^
  --type exe ^
  --app-version 1.0 ^
  --vendor "Connexion3A8" ^
  --description "INVESTI Investment Platform" ^
  --win-console ^
  --win-shortcut ^
  --win-menu ^
  --java-options "-Xmx2G"

if %ERRORLEVEL% EQU 0 (
    echo.
    echo ========================================
    echo SUCCESS! EXE created successfully!
    echo Location: INVESTI-1.0.exe
    echo ========================================
) else (
    echo.
    echo ERROR: jpackage failed!
    echo Make sure JDK 17+ is installed and jpackage is available.
    echo.
    echo To check: jpackage --version
)

pause
