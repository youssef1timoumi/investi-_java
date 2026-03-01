# 🚀 Quick Start - Course Media Upload

## ⚡ 30-Second Setup

The feature is ready to use! No setup needed.

## 📤 Upload Media (Admin)

### Step 1: Go to Course Management
Click "Manage Courses" from Main Menu

### Step 2: Create or Edit a Course
Fill in course details or click "Edit" on existing course

### Step 3: Upload Media
1. Find the "Content URL" field
2. Click the **"📁 Browse"** button
3. Select your video (MP4, AVI, MKV) or PDF file
4. File automatically uploads to `media/courses/` folder
5. Path appears in Content URL field
6. Content Type auto-set (video or pdf)

### Step 4: Save
Click "Add Course" or update existing course

## 👀 View Media (User)

### Step 1: Go to Course Catalog
Click "Browse Course Catalog" from Main Menu

### Step 2: View Course
Click "View Course Details" on any course card

### Step 3: Watch/Read
Scroll to "📺 Course Content" section

**For Videos:**
- Video player appears automatically
- Click ▶ Play to start
- Click ⏸ Pause to pause
- Click ⏹ Stop to stop and reset

**For PDFs:**
- Click "📄 Open PDF" button
- PDF opens in your default viewer

**For External URLs:**
- Click the hyperlink to open in browser

## 📁 Supported Formats

### Videos
- ✅ MP4 (recommended)
- ✅ AVI
- ✅ MKV
- ✅ MOV
- ✅ WMV
- ✅ FLV

### Documents
- ✅ PDF

## 🎯 Examples

### Example 1: Upload Video Tutorial

```
1. Course Management → Create new course
2. Title: "Java Programming Basics"
3. Click "📁 Browse"
4. Select "java_tutorial.mp4"
5. ✅ File uploaded to media/courses/course_1709000000000.mp4
6. ✅ Content Type set to "video"
7. Save course
8. Users can now watch video in Course Catalog!
```

### Example 2: Upload PDF Slides

```
1. Course Management → Edit existing course
2. Click "📁 Browse"
3. Select "database_slides.pdf"
4. ✅ File uploaded to media/courses/course_1709000001000.pdf
5. ✅ Content Type set to "pdf"
6. Save course
7. Users can now open PDF in Course Catalog!
```

### Example 3: Use External URL

```
1. Course Management → Create new course
2. Manually type YouTube URL in Content URL field
3. Example: "https://youtube.com/watch?v=..."
4. Save course
5. Users can click link to open in browser!
```

## 🔍 Where Files Are Stored

```
Project Root
└── media/
    └── courses/
        ├── course_1709000000000.mp4  ← Your uploaded videos
        ├── course_1709000001000.pdf  ← Your uploaded PDFs
        └── README.md                  ← Info about this folder
```

## ⚠️ Important Notes

1. **File Size** - No current limit, but large files take longer to upload
2. **Unique Names** - Files automatically renamed with timestamp
3. **Don't Delete** - Don't manually delete files from media folder
4. **Backup** - Media files not in version control, backup regularly

## 🐛 Troubleshooting

### "Error uploading file"
- Check disk space
- Ensure you have write permissions
- Try a smaller file

### "Video won't play"
- Check file format (MP4 recommended)
- Ensure codec is supported (H.264 + AAC)
- Try converting video to MP4

### "PDF won't open"
- Ensure you have a PDF viewer installed
- Check file isn't corrupted
- Try opening file manually from media/courses/

### "Browse button doesn't work"
- Restart application
- Check FXML file loaded correctly
- Verify handleBrowseFile method exists

## 📚 Full Documentation

For complete details, see:
- `COURSE_MEDIA_UPLOAD_FEATURE.md` - Complete feature documentation
- `media/courses/README.md` - Media folder information

## ✅ Checklist

### Admin Checklist
- [ ] Can click Browse button
- [ ] Can select video file
- [ ] Can select PDF file
- [ ] File uploads successfully
- [ ] Path appears in Content URL field
- [ ] Content Type auto-set
- [ ] Can save course
- [ ] Success message appears

### User Checklist
- [ ] Can view course details
- [ ] Video player appears for videos
- [ ] Can play/pause/stop video
- [ ] PDF button appears for PDFs
- [ ] Can open PDF
- [ ] External URLs are clickable
- [ ] "No media" message for empty courses

## 🎉 You're Ready!

The feature is fully functional. Just click Browse, select your file, and save!

---

**Need Help?** Check `COURSE_MEDIA_UPLOAD_FEATURE.md` for detailed documentation.
