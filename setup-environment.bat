@echo off
echo ========================================
echo Setting up Java and Maven Environment
echo ========================================

echo.
echo Adding to System PATH:
echo - Java: C:\graalvm\graalvm-community-openjdk-17.0.9+9.1\bin
echo - Maven: C:\xampp\apache\maven\bin
echo.

REM Add Java to PATH permanently
setx JAVA_HOME "C:\graalvm\graalvm-community-openjdk-17.0.9+9.1" /M
setx PATH "%PATH%;C:\graalvm\graalvm-community-openjdk-17.0.9+9.1\bin" /M

REM Add Maven to PATH permanently
setx PATH "%PATH%;C:\xampp\apache\maven\bin" /M

echo.
echo ========================================
echo SUCCESS! Environment variables set.
echo ========================================
echo.
echo IMPORTANT: You must RESTART your terminal/PowerShell
echo for the changes to take effect!
echo.
echo After restart, verify with:
echo   java -version
echo   mvn -version
echo   jpackage --version
echo.

pause
