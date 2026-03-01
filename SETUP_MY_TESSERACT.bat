@echo off
echo ========================================
echo   Quick Tesseract Setup for E:\Tesseract
echo ========================================
echo.

:: Set environment variables for your Tesseract installation
echo Setting TESSDATA_PREFIX...
setx TESSDATA_PREFIX "E:\Tesseract\tessdata" /M

echo.
echo Adding Tesseract to PATH...
setx PATH "%PATH%;E:\Tesseract" /M

echo.
echo ========================================
echo   Setup Complete!
echo ========================================
echo.
echo IMPORTANT: You must restart IntelliJ (or your computer) for changes to take effect.
echo.
echo Next steps:
echo 1. Close and restart IntelliJ IDEA
echo 2. Run your application
echo 3. Test PDF extraction
echo.
pause
