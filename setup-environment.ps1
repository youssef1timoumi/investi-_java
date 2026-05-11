# Setup Java and Maven Environment Variables
# Run as Administrator!

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "Setting up Java and Maven Environment" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

$javaHome = "C:\graalvm\graalvm-community-openjdk-17.0.9+9.1"
$javaBin = "$javaHome\bin"
$mavenBin = "C:\xampp\apache\maven\bin"

# Check if paths exist
if (-not (Test-Path $javaHome)) {
    Write-Host "ERROR: Java path not found: $javaHome" -ForegroundColor Red
    exit 1
}

if (-not (Test-Path $mavenBin)) {
    Write-Host "ERROR: Maven path not found: $mavenBin" -ForegroundColor Red
    exit 1
}

Write-Host "Found Java at: $javaHome" -ForegroundColor Green
Write-Host "Found Maven at: $mavenBin" -ForegroundColor Green
Write-Host ""

# Get current system PATH
$currentPath = [Environment]::GetEnvironmentVariable("Path", "Machine")

# Check if already in PATH
$needsUpdate = $false

if ($currentPath -notlike "*$javaBin*") {
    Write-Host "Adding Java to PATH..." -ForegroundColor Yellow
    $needsUpdate = $true
} else {
    Write-Host "Java already in PATH" -ForegroundColor Green
}

if ($currentPath -notlike "*$mavenBin*") {
    Write-Host "Adding Maven to PATH..." -ForegroundColor Yellow
    $needsUpdate = $true
} else {
    Write-Host "Maven already in PATH" -ForegroundColor Green
}

if ($needsUpdate) {
    try {
        # Set JAVA_HOME
        [Environment]::SetEnvironmentVariable("JAVA_HOME", $javaHome, "Machine")
        Write-Host "Set JAVA_HOME" -ForegroundColor Green
        
        # Add to PATH if not already there
        $newPath = $currentPath
        if ($currentPath -notlike "*$javaBin*") {
            $newPath = $newPath + ";" + $javaBin
        }
        if ($currentPath -notlike "*$mavenBin*") {
            $newPath = $newPath + ";" + $mavenBin
        }
        
        [Environment]::SetEnvironmentVariable("Path", $newPath, "Machine")
        Write-Host "Updated System PATH" -ForegroundColor Green
        
        Write-Host ""
        Write-Host "========================================" -ForegroundColor Cyan
        Write-Host "SUCCESS! Environment configured." -ForegroundColor Green
        Write-Host "========================================" -ForegroundColor Cyan
        Write-Host ""
        Write-Host "IMPORTANT: Close and reopen your terminal!" -ForegroundColor Yellow
        Write-Host ""
        Write-Host "After restart, verify with:" -ForegroundColor Cyan
        Write-Host "  java -version" -ForegroundColor White
        Write-Host "  mvn -version" -ForegroundColor White
        Write-Host "  jpackage --version" -ForegroundColor White
        
    } catch {
        Write-Host ""
        Write-Host "ERROR: Failed to set environment variables" -ForegroundColor Red
        Write-Host "Make sure you run PowerShell as Administrator!" -ForegroundColor Yellow
        Write-Host ""
        Write-Host "Error details: $_" -ForegroundColor Red
        exit 1
    }
} else {
    Write-Host ""
    Write-Host "All paths already configured!" -ForegroundColor Green
}

Write-Host ""
Read-Host "Press Enter to exit"
