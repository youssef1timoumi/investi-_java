# 🔧 Setup Java & Maven in Windows PATH

## 🚀 Quick Setup (Automated)

### Option 1: PowerShell Script (Recommended)

1. **Right-click PowerShell** → **Run as Administrator**
2. Navigate to project:
   ```powershell
   cd "C:\Users\Reiner\Desktop\integration finale\investi-_java"
   ```
3. Run the setup script:
   ```powershell
   .\setup-environment.ps1
   ```
4. **Close and reopen** all terminals
5. Verify:
   ```powershell
   java -version
   mvn -version
   jpackage --version
   ```

---

## 🛠️ Manual Setup (If script doesn't work)

### Step 1: Open Environment Variables

1. Press **Windows Key** + type "environment"
2. Click **"Edit the system environment variables"**
3. Click **"Environment Variables..."** button at bottom

### Step 2: Set JAVA_HOME

1. Under **"System variables"** section (bottom half)
2. Click **"New..."**
3. Variable name: `JAVA_HOME`
4. Variable value: `C:\graalvm\graalvm-community-openjdk-17.0.9+9.1`
5. Click **OK**

### Step 3: Update PATH

1. Still in **"System variables"** section
2. Find and select **"Path"**
3. Click **"Edit..."**
4. Click **"New"** and add: `C:\graalvm\graalvm-community-openjdk-17.0.9+9.1\bin`
5. Click **"New"** again and add: `C:\xampp\apache\maven\bin`
6. Click **OK** on all windows

### Step 4: Verify

1. **Close ALL terminals/PowerShell windows**
2. Open a **new PowerShell**
3. Test:
   ```powershell
   java -version
   mvn -version
   jpackage --version
   ```

All three should work now! ✅

---

## 🎯 Your Paths

Based on your system:
- **Java Home:** `C:\graalvm\graalvm-community-openjdk-17.0.9+9.1`
- **Java Bin:** `C:\graalvm\graalvm-community-openjdk-17.0.9+9.1\bin`
- **Maven Bin:** `C:\xampp\apache\maven\bin`

---

## ❓ Troubleshooting

### "Access Denied" when running script
- Run PowerShell as **Administrator**

### "Execution Policy" error
```powershell
Set-ExecutionPolicy -ExecutionPolicy RemoteSigned -Scope CurrentUser
```

### Commands still not found after setup
- Make sure you **closed and reopened** the terminal
- Check if paths are correct in Environment Variables
- Restart your computer if still not working

### Verify PATH was added
```powershell
$env:Path -split ';' | Select-String "graalvm"
$env:Path -split ';' | Select-String "maven"
```

Should show your Java and Maven paths.

---

## ✅ After Setup Complete

You can now run from any terminal:
```powershell
# Build the app
mvn clean package

# Create EXE
jpackage --input target --name INVESTI --main-jar investi-integration-1.0-SNAPSHOT.jar --main-class edu.connexion3a8.InvestiApp --type exe --win-console

# Or use the automated script
.\build-exe.bat
```
