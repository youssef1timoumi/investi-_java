# Course Media Folder

## 📁 Purpose

This folder stores all uploaded course media files (videos and PDFs).

## 📋 File Naming Convention

Files are automatically named with the following pattern:
```
course_<timestamp>.<extension>
```

Examples:
- `course_1709000000000.mp4`
- `course_1709000001000.pdf`
- `course_1709000002000.avi`

## 🎯 Supported Formats

### Videos
- MP4 (recommended)
- AVI
- MKV
- MOV
- WMV
- FLV

### Documents
- PDF

## 🔒 Important Notes

1. **Do NOT manually delete files** - They are referenced in the database
2. **Do NOT rename files** - The database stores the exact filename
3. **Backup regularly** - These files are not in version control
4. **Check disk space** - Video files can be large

## 📊 File Management

### To Clean Up Unused Files

1. Check database for referenced files:
   ```sql
   SELECT content_url FROM course WHERE content_url LIKE 'media/courses/%';
   ```

2. Compare with files in this folder
3. Delete only files NOT in the database

### To Move Files

If you need to move the media folder:
1. Update all `content_url` values in the database
2. Or keep the relative path structure intact

## 🚀 Usage

Files in this folder are:
- Uploaded via the Course Management form
- Displayed in the Course Catalog
- Played in the embedded video player (for videos)
- Opened in system PDF viewer (for PDFs)

## ⚠️ .gitignore

This folder is typically added to `.gitignore` to avoid committing large media files to version control.

Add to your `.gitignore`:
```
media/courses/*
!media/courses/README.md
```

## 📈 Statistics

To see folder size:
```bash
# Windows PowerShell
Get-ChildItem -Recurse | Measure-Object -Property Length -Sum

# Linux/Mac
du -sh .
```

## 🎉 Enjoy!

This folder is automatically managed by the application. Just use the Browse button in Course Management to upload files!
