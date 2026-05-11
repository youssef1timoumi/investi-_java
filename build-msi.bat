@echo off
echo ========================================
echo Building INVESTI Windows Installer
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
echo [3/3] Creating Windows Installer (.msi) with jpackage...
echo Note: This requires WiX Toolset to be installed
echo.

jpackage ^
  --input target ^
  --name INVESTI ^
  --main-jar investi-integration-1.0-SNAPSHOT.jar ^
  --main-class edu.connexion3a8.InvestiApp ^
  --type msi ^
  --app-version 1.0 ^
  --vendor "Connexion3A8" ^
  --description "INVESTI Investment Platform" ^
  --win-dir-chooser ^
  --win-menu ^
  --win-shortcut ^
  --java-options "-Xmx2G"

if %ERRORLEVEL% EQU 0 (
    echo.
    echo ========================================
    echo SUCCESS! MSI installer created!
    echo Location: INVESTI-1.0.msi
    echo ========================================
) else (
    echo.
    echo ERROR: jpackage failed!
    echo.
    echo Common issues:
    echo 1. JDK 17+ not installed or not in PATH
    echo 2. WiX Toolset not installed (required for .msi)
    echo    Download from: https://wixtoolset.org/releases/
    echo.
    echo To check: jpackage --version
)

pause
