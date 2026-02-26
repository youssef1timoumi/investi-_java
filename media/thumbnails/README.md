# Course Thumbnails Folder

## 📁 Purpose

This folder stores all uploaded course thumbnail images.

## 📋 File Naming Convention

Files are automatically named with the following pattern:
```
thumb_<timestamp>.<extension>
```

Examples:
- `thumb_1709000000000.png`
- `thumb_1709000001000.jpg`
- `thumb_1709000002000.jpeg`

## 🎯 Supported Formats

### Images
- PNG (recommended for quality)
- JPG/JPEG (recommended for smaller file size)

## 📐 Recommended Dimensions

- **Aspect Ratio**: 16:9 or 4:3
- **Minimum Size**: 240x140 pixels
- **Recommended Size**: 480x280 pixels or higher
- **Maximum Size**: No limit, but keep under 2MB for performance

## 🎨 Display

Thumbnails are displayed:
- In Course Catalog cards (240x140 pixels)
- Automatically cropped to fit
- Rounded corners for modern look

## 🔒 Important Notes

1. **Do NOT manually delete files** - They are referenced in the database
2. **Do NOT rename files** - The database stores the exact filename
3. **Backup regularly** - These files are not in version control
4. **Optimize images** - Compress before uploading for better performance

## 📊 File Management

### To Clean Up Unused Files

1. Check database for referenced files:
   ```sql
   SELECT thumbnail_url FROM course WHERE thumbnail_url LIKE 'media/thumbnails/%';
   ```

2. Compare with files in this folder
3. Delete only files NOT in the database

### To Move Files

If you need to move the thumbnails folder:
1. Update all `thumbnail_url` values in the database
2. Or keep the relative path structure intact

## 🚀 Usage

Files in this folder are:
- Uploaded via the Course Management form
- Displayed in the Course Catalog cards
- Shown as 240x140 pixel images with rounded corners

## 🎨 Placeholder

If no thumbnail is uploaded, a gradient placeholder with 📚 icon is shown.

## ⚠️ .gitignore

This folder is typically added to `.gitignore` to avoid committing image files to version control.

Add to your `.gitignore`:
```
media/thumbnails/*
!media/thumbnails/README.md
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

This folder is automatically managed by the application. Just use the Browse button in Course Management to upload thumbnails!
