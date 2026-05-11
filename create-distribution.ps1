# Create Distribution Package for INVESTI
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "Creating INVESTI Distribution Package" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

$distFolder = "INVESTI-Distribution"
$zipFile = "INVESTI-Distribution.zip"

# Remove old distribution if exists
if (Test-Path $distFolder) {
    Remove-Item $distFolder -Recurse -Force
}
if (Test-Path $zipFile) {
    Remove-Item $zipFile -Force
}

# Create distribution folder
New-Item -ItemType Directory -Path $distFolder | Out-Null
New-Item -ItemType Directory -Path "$distFolder\target" | Out-Null

# Copy necessary files
Write-Host "Copying files..." -ForegroundColor Yellow

# Copy the JAR
Copy-Item "target\investi-integration-1.0-SNAPSHOT.jar" "$distFolder\target\" -ErrorAction Stop
Write-Host "✓ Copied JAR file" -ForegroundColor Green

# Copy launcher
Copy-Item "INVESTI-Launcher.bat" "$distFolder\" -ErrorAction Stop
Write-Host "✓ Copied launcher" -ForegroundColor Green

# Copy pom.xml (needed for mvn javafx:run)
Copy-Item "pom.xml" "$distFolder\" -ErrorAction Stop
Write-Host "✓ Copied pom.xml" -ForegroundColor Green

# Copy resources folder
Copy-Item "src\main\resources" "$distFolder\src\main\" -Recurse -ErrorAction Stop
Write-Host "✓ Copied resources" -ForegroundColor Green

# Copy README
Copy-Item "README_FOR_USERS.txt" "$distFolder\README.txt" -ErrorAction Stop
Write-Host "✓ Copied README" -ForegroundColor Green

# Create ZIP
Write-Host ""
Write-Host "Creating ZIP archive..." -ForegroundColor Yellow
Compress-Archive -Path "$distFolder\*" -DestinationPath $zipFile -Force

Write-Host ""
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "SUCCESS!" -ForegroundColor Green
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""
Write-Host "Distribution package created: $zipFile" -ForegroundColor Green
Write-Host "Size: $((Get-Item $zipFile).Length / 1MB) MB" -ForegroundColor Cyan
Write-Host ""
Write-Host "Users need:" -ForegroundColor Yellow
Write-Host "  1. Java 17+" -ForegroundColor White
Write-Host "  2. Maven 3.6+" -ForegroundColor White
Write-Host "  3. Extract ZIP and run INVESTI-Launcher.bat" -ForegroundColor White
Write-Host ""

# Clean up temp folder
Remove-Item $distFolder -Recurse -Force

Read-Host "Press Enter to exit"
