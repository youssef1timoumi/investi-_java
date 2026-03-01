# Tesseract OCR Setup Script for Windows
# This script automatically adds Tesseract to your system PATH

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "  Tesseract OCR Setup Script" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

# Check if running as Administrator
$isAdmin = ([Security.Principal.WindowsPrincipal] [Security.Principal.WindowsIdentity]::GetCurrent()).IsInRole([Security.Principal.WindowsBuiltInRole]::Administrator)

if (-not $isAdmin) {
    Write-Host "ERROR: This script must be run as Administrator!" -ForegroundColor Red
    Write-Host ""
    Write-Host "To run as Administrator:" -ForegroundColor Yellow
    Write-Host "1. Right-click on PowerShell" -ForegroundColor Yellow
    Write-Host "2. Select 'Run as Administrator'" -ForegroundColor Yellow
    Write-Host "3. Navigate to this folder and run the script again" -ForegroundColor Yellow
    Write-Host ""
    Read-Host "Press Enter to exit"
    exit
}

Write-Host "Running as Administrator... OK" -ForegroundColor Green
Write-Host ""

# Find Tesseract installation
Write-Host "Searching for Tesseract installation..." -ForegroundColor Cyan

$possiblePaths = @(
    "E:\Tesseract",
    "C:\Program Files\Tesseract-OCR",
    "C:\Program Files (x86)\Tesseract-OCR",
    "C:\Tesseract-OCR"
)

$tesseractPath = $null
foreach ($path in $possiblePaths) {
    if (Test-Path $path) {
        $tesseractPath = $path
        Write-Host "Found Tesseract at: $tesseractPath" -ForegroundColor Green
        break
    }
}

if (-not $tesseractPath) {
    Write-Host "ERROR: Tesseract not found!" -ForegroundColor Red
    Write-Host ""
    Write-Host "Please install Tesseract first:" -ForegroundColor Yellow
    Write-Host "1. Download from: https://github.com/UB-Mannheim/tesseract/wiki" -ForegroundColor Yellow
    Write-Host "2. Run the installer" -ForegroundColor Yellow
    Write-Host "3. Run this script again" -ForegroundColor Yellow
    Write-Host ""
    Read-Host "Press Enter to exit"
    exit
}

# Check if already in PATH
$currentPath = [Environment]::GetEnvironmentVariable("Path", "Machine")
if ($currentPath -like "*$tesseractPath*") {
    Write-Host "Tesseract is already in PATH!" -ForegroundColor Green
    Write-Host ""
    Write-Host "Testing Tesseract..." -ForegroundColor Cyan
    try {
        $version = & tesseract --version 2>&1
        Write-Host "Tesseract is working!" -ForegroundColor Green
        Write-Host $version -ForegroundColor Gray
    } catch {
        Write-Host "Warning: Tesseract command not found. You may need to restart your computer." -ForegroundColor Yellow
    }
} else {
    # Add to PATH
    Write-Host "Adding Tesseract to system PATH..." -ForegroundColor Cyan
    
    try {
        $newPath = $currentPath + ";" + $tesseractPath
        [Environment]::SetEnvironmentVariable("Path", $newPath, "Machine")
        Write-Host "Successfully added to PATH!" -ForegroundColor Green
    } catch {
        Write-Host "ERROR: Failed to add to PATH: $_" -ForegroundColor Red
        Read-Host "Press Enter to exit"
        exit
    }
}

# Set TESSDATA_PREFIX environment variable
Write-Host ""
Write-Host "Setting TESSDATA_PREFIX environment variable..." -ForegroundColor Cyan

$tessdataPath = Join-Path $tesseractPath "tessdata"
if (Test-Path $tessdataPath) {
    try {
        [Environment]::SetEnvironmentVariable("TESSDATA_PREFIX", $tessdataPath, "Machine")
        Write-Host "TESSDATA_PREFIX set to: $tessdataPath" -ForegroundColor Green
    } catch {
        Write-Host "Warning: Could not set TESSDATA_PREFIX: $_" -ForegroundColor Yellow
    }
} else {
    Write-Host "Warning: tessdata folder not found at: $tessdataPath" -ForegroundColor Yellow
}

# Verify tessdata files
Write-Host ""
Write-Host "Checking language data files..." -ForegroundColor Cyan

$engFile = Join-Path $tessdataPath "eng.traineddata"
if (Test-Path $engFile) {
    Write-Host "English language data found!" -ForegroundColor Green
} else {
    Write-Host "Warning: English language data not found!" -ForegroundColor Yellow
    Write-Host "Download from: https://github.com/tesseract-ocr/tessdata" -ForegroundColor Yellow
}

# Summary
Write-Host ""
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "  Setup Complete!" -ForegroundColor Green
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""
Write-Host "Next steps:" -ForegroundColor Yellow
Write-Host "1. Restart your computer (or at least restart IntelliJ)" -ForegroundColor White
Write-Host "2. Open your Java project in IntelliJ" -ForegroundColor White
Write-Host "3. Right-click pom.xml -> Maven -> Reload project" -ForegroundColor White
Write-Host "4. Run your application" -ForegroundColor White
Write-Host "5. Test PDF extraction with 'Extract & Read Text' button" -ForegroundColor White
Write-Host ""
Write-Host "Tesseract Path: $tesseractPath" -ForegroundColor Gray
Write-Host "Tessdata Path: $tessdataPath" -ForegroundColor Gray
Write-Host ""

Read-Host "Press Enter to exit"
