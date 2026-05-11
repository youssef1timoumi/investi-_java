# 🚀 Simple EXE Creation (Works with GraalVM)

## Option 1: Launch4j (Recommended - 5 minutes)

### Download Launch4j
1. Go to: https://launch4j.sourceforge.net/
2. Download and install Launch4j
3. Open Launch4j

### Create EXE
1. Click **"Load Config"** → Select `launch4j-config.xml` from this folder
2. Click **"Build wrapper"** (gear icon)
3. Done! You'll get `INVESTI.exe`

**Note:** Users need Java 17+ installed. The EXE is small (~50 MB).

---

## Option 2: Batch Script Wrapper (Instant - No tools needed)

Just distribute the JAR with this batch file:

### Create `INVESTI.bat`:
```batch
@echo off
start javaw -Xmx2G -jar investi-integration-1.0-SNAPSHOT.jar
```

Users double-click `INVESTI.bat` to run the app.

---

## Option 3: Use Regular JDK (Not GraalVM)

If you want jpackage with bundled Java:

1. Download **Oracle JDK 17** or **Temurin JDK 17** (not GraalVM)
2. Install it
3. Run:
```powershell
"C:\Program Files\Java\jdk-17\bin\jpackage.exe" --input target --name INVESTI --main-jar investi-integration-1.0-SNAPSHOT.jar --main-class edu.connexion3a8.InvestiApp --type exe --app-version 1.0 --vendor "Connexion3A8" --win-console
```

This creates a 200-300 MB EXE with bundled Java (users don't need Java installed).

---

## What I Recommend Right Now:

**Use Launch4j** - it's the easiest and works perfectly with GraalVM.

1. Download: https://launch4j.sourceforge.net/
2. Install and open Launch4j
3. Load the `launch4j-config.xml` file I created
4. Click "Build wrapper"
5. Done! ✅

Sleep well bro! 😴
