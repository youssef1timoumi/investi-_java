@echo off
echo Starting INVESTI in debug mode...
echo.

cd /d "%~dp0"

java -jar target\investi-integration-1.0-SNAPSHOT.jar

echo.
echo.
echo Application closed. Press any key to exit...
pause > nul
