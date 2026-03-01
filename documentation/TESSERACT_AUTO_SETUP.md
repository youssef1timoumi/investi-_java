# Automatic Tesseract Setup for Windows

## What This Does

The setup script will automatically:
- ✅ Find your Tesseract installation
- ✅ Add Tesseract to system PATH
- ✅ Set TESSDATA_PREFIX environment variable
- ✅ Verify language data files
- ✅ Test the installation

## Prerequisites

**You must install Tesseract first!**

1. Download: https://github.com/UB-Mannheim/tesseract/wiki
2. Run: `tesseract-ocr-w64-setup-5.3.3.20231005.exe`
3. Install to default location: `C:\Program Files\Tesseract-OCR`
4. Complete the installation

## Automatic Setup (Easy Way)

### Step 1: Run the Setup Script

**Double-click:** `SETUP_TESSERACT.bat`

This will:
1. Request Administrator privileges (click "Yes")
2. Find Tesseract installation
3. Add to PATH automatically
4. Set environment variables
5. Verify everything works

### Step 2: Restart

After the script completes:
1. Restart your computer (or at least restart IntelliJ)
2. Open your project
3. Right-click `pom.xml` → Maven → Reload project

### Step 3: Test

1. Run your application
2. Open a course with PDF
3. Click "📖 Extract & Read Text"
4. Check console for: "Using tessdata from: C:\Program Files\Tesseract-OCR\tessdata"

## Manual Setup (If Script Doesn't Work)

### Add to PATH Manually

1. Press `Windows + R`
2. Type: `sysdm.cpl`
3. Press Enter
4. Click "Environment Variables"
5. Under "System variables", find "Path"
6. Click "Edit"
7. Click "New"
8. Add: `C:\Program Files\Tesseract-OCR`
9. Click "OK" on all windows

### Set TESSDATA_PREFIX

1. In "Environment Variables" window
2. Under "System variables", click "New"
3. Variable name: `TESSDATA_PREFIX`
4. Variable value: `C:\Program Files\Tesseract-OCR\tessdata`
5. Click "OK"

### Restart Computer

Restart your computer for changes to take effect.

## Verify Installation

Open Command Prompt and run:

```bash
tesseract --version
```

You should see:
```
tesseract 5.3.3
 leptonica-1.83.1
  ...
```

If you see this, setup is complete!

## Troubleshooting

### "Tesseract not found" after running script

**Solution**: Restart your computer, then try again

### Script says "Tesseract not found"

**Solution**: Install Tesseract first from the link above

### "Access Denied" error

**Solution**: Right-click `SETUP_TESSERACT.bat` → "Run as Administrator"

### Script doesn't run

**Solution**: 
1. Right-click `setup-tesseract.ps1`
2. Select "Run with PowerShell"
3. If blocked, run: `Set-ExecutionPolicy -ExecutionPolicy RemoteSigned -Scope CurrentUser`

## What the Script Does (Technical)

```powershell
# 1. Check for Administrator privileges
# 2. Search for Tesseract in common locations
# 3. Get current system PATH
# 4. Add Tesseract to PATH if not already there
# 5. Set TESSDATA_PREFIX environment variable
# 6. Verify tessdata files exist
# 7. Test tesseract command
```

## Files Created

- `SETUP_TESSERACT.bat` - Double-click this to run setup
- `setup-tesseract.ps1` - PowerShell script that does the work
- `TESSERACT_AUTO_SETUP.md` - This instruction file

## After Setup

Once setup is complete, your Java application will automatically:
1. Find Tesseract installation
2. Use AI OCR for scanned PDFs
3. Extract text with high accuracy
4. Display "Extraction Method: AI OCR (Tesseract)"

No additional configuration needed in your code!

## Summary

**Quick Steps:**
1. ✅ Install Tesseract (download and run installer)
2. ✅ Double-click `SETUP_TESSERACT.bat`
3. ✅ Click "Yes" when prompted for admin
4. ✅ Restart computer
5. ✅ Test in your app

**That's it!** The script handles everything else automatically.
