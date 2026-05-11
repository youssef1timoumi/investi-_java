# Building INVESTI Desktop App as .exe

## 🎯 Recommended Approach: jpackage (Official Oracle Tool)

**Why jpackage?**
- ✅ Official Oracle/OpenJDK tool (included in JDK 17+)
- ✅ Creates native Windows .exe with bundled Java runtime
- ✅ Users don't need Java installed
- ✅ Professional installer (.msi) option
- ✅ No third-party tools needed

## Prerequisites

### Required Software
1. **Java JDK 17 or higher** (includes jpackage)
   - Download: [Eclipse Temurin JDK 17](https://adoptium.net/) or [Oracle JDK](https://www.oracle.com/java/technologies/downloads/)
   - Verify installation: `java -version` and `jpackage --version`

2. **Maven 3.6+**
   - Download: [Apache Maven](https://maven.apache.org/download.cgi)
   - Verify: `mvn -version`

3. **WiX Toolset** (Optional - for .msi installer)
   - Download: [WiX Toolset](https://wixtoolset.org/releases/)
   - Only needed if you want .msi installer instead of .exe

## 🚀 Quick Start: Build EXE in 3 Steps

### Step 1: Build the Fat JAR

```bash
cd investi-_java
mvn clean package
```

This creates: `target/investi-integration-1.0-SNAPSHOT.jar`

### Step 2: Test the JAR (Optional)

```bash
java -jar target/investi-integration-1.0-SNAPSHOT.jar
```

### Step 3: Create EXE with Bundled Java Runtime

**Windows Command Prompt / PowerShell:**

```cmd
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
```

**Git Bash / Linux-style shell:**

```bash
jpackage \
  --input target \
  --name INVESTI \
  --main-jar investi-integration-1.0-SNAPSHOT.jar \
  --main-class edu.connexion3a8.InvestiApp \
  --type exe \
  --app-version 1.0 \
  --vendor "Connexion3A8" \
  --description "INVESTI Investment Platform" \
  --win-console \
  --win-shortcut \
  --win-menu \
  --java-options "-Xmx2G"
```

**Output:** `INVESTI-1.0.exe` (approximately 200-300 MB with bundled JRE)

## 📦 Advanced Options

### Option A: Create Windows Installer (.msi)

```cmd
jpackage ^
  --input target ^
  --name INVESTI ^
  --main-jar investi-integration-1.0-SNAPSHOT.jar ^
  --main-class edu.connexion3a8.InvestiApp ^
  --type msi ^
  --app-version 1.0 ^
  --vendor "Connexion3A8" ^
  --win-dir-chooser ^
  --win-menu ^
  --win-shortcut
```

### Option B: Add Custom Icon

1. Convert your PNG to ICO format (use online converter or ImageMagick)
2. Save as `src/main/resources/INVESTI.ico`
3. Add to jpackage command:

```cmd
--icon src/main/resources/INVESTI.ico
```

### Option C: Specify JavaFX Modules Explicitly

```cmd
jpackage ^
  --input target ^
  --name INVESTI ^
  --main-jar investi-integration-1.0-SNAPSHOT.jar ^
  --main-class edu.connexion3a8.InvestiApp ^
  --type exe ^
  --module-path "%JAVA_HOME%\jmods" ^
  --add-modules javafx.controls,javafx.fxml,javafx.web,javafx.media ^
  --java-options "-Xmx2G"
```

## 🛠️ Automated Build Script

Create `build-exe.bat` in the project root:

```batch
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
)

pause
```

**Usage:** Double-click `build-exe.bat` or run `.\build-exe.bat`

## 🌐 Database Setup (VPS Connection)

### Step 1: Open MySQL Port on VPS (AWS)

1. Go to AWS Console → EC2 → Security Groups
2. Find your instance's security group
3. Add inbound rule:
   - **Type:** MySQL/Aurora
   - **Port:** 3306
   - **Source:** 0.0.0.0/0 (or your specific IP for security)
4. Save

### Step 2: Expose MySQL Port in Docker

Edit `docker-compose.yml` on VPS and add under `mysql` service:

```yaml
mysql:
  ports:
    - "3306:3306"  # Add this line
```

Then restart: `docker-compose restart mysql`

## 📤 Distribution

The generated .exe file can be distributed to users. They need:
- ✅ Windows 10/11 (64-bit)
- ✅ **NO Java installation required** (bundled in EXE)
- ✅ Internet connection to reach VPS MySQL

### Database Connection

The app connects to:
- **Host:** 54.90.222.165
- **Port:** 3306
- **Database:** 3a8
- **User:** investi
- **Password:** investi123

## 🔒 Security Notes

⚠️ **Important:**
- Exposing MySQL port 3306 to the internet is a security risk
- Consider using:
  - IP whitelist (restrict to specific IPs)
  - VPN/SSH tunnel
  - REST API instead of direct database access
  - SSL/TLS encryption for MySQL connections

## 🐛 Troubleshooting

### "jpackage: command not found"
- Ensure JDK 17+ is installed (not just JRE)
- Add JDK bin folder to PATH: `C:\Program Files\Java\jdk-17\bin`
- Restart terminal after PATH changes

### "Connection refused" (Database)
- Check AWS Security Group allows port 3306
- Check Docker exposes port 3306
- Test: `telnet 54.90.222.165 3306`

### "Access denied" (Database)
- Verify MySQL user has remote access
- Check credentials in config.properties

### "No suitable driver"
- Ensure MySQL connector is in the JAR
- Maven Shade plugin should bundle it automatically

### EXE is too large (>300MB)
- This is normal - includes full Java runtime
- Alternative: Use Launch4j (requires users to install Java)

### Application won't start
- Check if antivirus is blocking the EXE
- Run from command line to see error messages
- Verify config.properties exists in resources

## 📊 Comparison: jpackage vs Launch4j

| Feature | jpackage | Launch4j |
|---------|----------|----------|
| **Bundled Java** | ✅ Yes | ❌ No (users need Java) |
| **File Size** | ~200-300 MB | ~50 MB |
| **User Experience** | Better (no Java install) | Requires Java setup |
| **Official Tool** | ✅ Oracle/OpenJDK | Third-party |
| **Installer Support** | ✅ .msi available | ❌ No |
| **Complexity** | Simple | Moderate |

**Recommendation:** Use **jpackage** for production distribution.

## 🔄 Alternative: Launch4j (Legacy Method)

If you need a smaller EXE and users already have Java:

1. Download Launch4j: https://launch4j.sourceforge.net/
2. Install and open Launch4j
3. Configure:
   - **Output file:** `INVESTI.exe`
   - **Jar:** `target/investi-integration-1.0-SNAPSHOT.jar`
   - **Icon:** (optional) Add your app icon
   - **JRE min version:** 17
4. Click "Build wrapper"

## 📝 Notes

- The Maven Shade plugin already creates a fat JAR with all dependencies
- JavaFX modules are included in the JAR
- Main class is `edu.connexion3a8.InvestiApp`
- Application uses Java 17 features

## 🆘 Need Help?

If you encounter issues:
1. Check Java version: `java -version` (should be 17+)
2. Check Maven: `mvn -version`
3. Check jpackage: `jpackage --version`
4. Review build logs for errors
5. Test JAR first before creating EXE

## Database Connection

The app connects to:
- **Host:** 54.90.222.165
- **Port:** 3306
- **Database:** 3a8
- **User:** investi
- **Password:** investi123

## Security Notes

⚠️ **Important:**
- Exposing MySQL port 3306 to the internet is a security risk
- Consider using:
  - IP whitelist (restrict to specific IPs)
  - VPN/SSH tunnel
  - REST API instead of direct database access

## Troubleshooting

### "Connection refused"
- Check AWS Security Group allows port 3306
- Check Docker exposes port 3306
- Test: `telnet 54.90.222.165 3306`

### "Access denied"
- Verify MySQL user has remote access
- Check credentials in config.properties

### "No suitable driver"
- Ensure MySQL connector is in the JAR
- Maven Shade plugin should bundle it automatically
