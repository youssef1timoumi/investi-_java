@echo off
echo ========================================
echo   Tesseract OCR Setup
echo ========================================
echo.
echo This will add Tesseract to your system PATH
echo.
echo IMPORTANT: This requires Administrator privileges
echo.
pause

:: Check for admin privileges
net session >nul 2>&1
if %errorLevel% == 0 (
    echo Running as Administrator...
    echo.
    powershell -ExecutionPolicy Bypass -File "%~dp0setup-tesseract.ps1"
) else (
    echo Requesting Administrator privileges...
    echo.
    powershell -Command "Start-Process PowerShell -ArgumentList '-ExecutionPolicy Bypass -File \"%~dp0setup-tesseract.ps1\"' -Verb RunAs"
)

pause
