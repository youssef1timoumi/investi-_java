# Course Media Upload Feature

## 🎯 Overview

This feature allows administrators to upload course media files (videos and PDFs) directly from their computer. The files are automatically stored in a dedicated media folder and can be viewed by users in the Course Catalog.

## ✨ Features

### For Administrators

1. **File Upload Button** - Browse and select media files from your computer
2. **Supported Formats**:
   - Videos: MP4, AVI, MKV, MOV, WMV, FLV
   - Documents: PDF
3. **Automatic Storage** - Files copied to `media/courses/` folder
4. **Unique Naming** - Timestamp-based naming prevents conflicts
5. **Auto-Detection** - Content type automatically set based on file extension
6. **Local Path Storage** - Relative path stored in database

### For Users

1. **Video Player** - Built-in video player with controls
   - Play/Pause/Stop buttons
   - Preserves aspect ratio
   - Embedded in course details dialog
2. **PDF Viewer** - Opens PDF in default system viewer
3. **External URLs** - Clickable links for external content
4. **Fallback** - Graceful handling when no media available

## 🚀 How to Use

### Admin: Uploading Course Media

1. **Go to Course Management**
2. **Create or Edit a Course**
3. **Click the "📁 Browse" button** next to Content URL field
4. **Select your media file** (video or PDF)
5. **File is automatically uploaded** to `media/courses/` folder
6. **Content Type is auto-detected** (video or pdf)
7. **Save the course**

### User: Viewing Course Media

1. **Go to Course Catalog**
2. **Click "View Course Details"** on any course
3. **Scroll to "📺 Course Content" section**
4. **For Videos**:
   - Video player appears with the video loaded
   - Click "▶ Play" to start
   - Click "⏸ Pause" to pause
   - Click "⏹ Stop" to stop and reset
5. **For PDFs**:
   - Click "📄 Open PDF" button
   - PDF opens in your default PDF viewer
6. **For External URLs**:
   - Click the hyperlink to open in browser

## 📁 File Structure

```
Project Root
├── media/
│   └── courses/
│       ├── course_1709000000000.mp4
│       ├── course_1709000001000.pdf
│       └── course_1709000002000.mp4
│
├── src/
│   └── main/
│       ├── java/
│       │   └── edu/connections3a8/controllers/
│       │       ├── CourseController.java (upload logic)
│       │       └── CourseCatalogController.java (viewer logic)
│       └── resources/
│           └── CourseForm.fxml (browse button)
```

## 🔧 Technical Implementation

### CourseController.java

**New Method: `handleBrowseFile()`**

```java
@FXML
private void handleBrowseFile() {
    // 1. Open file chooser with filters
    // 2. User selects file
    // 3. Create media/courses directory
    // 4. Generate unique filename with timestamp
    // 5. Copy file to media directory
    // 6. Set relative path in content URL field
    // 7. Auto-detect content type
    // 8. Show success message
}
```

**Key Features:**
- Extension filters for videos and PDFs
- Unique filename generation: `course_<timestamp>.<ext>`
- Automatic directory creation
- Error handling with user feedback
- Auto-detection of content type

### CourseCatalogController.java

**New Method: `createMediaViewer(Course course)`**

```java
private VBox createMediaViewer(Course course) {
    // 1. Check if content URL exists
    // 2. Determine if local file or external URL
    // 3. For local files:
    //    - Videos: Create MediaPlayer with controls
    //    - PDFs: Create "Open PDF" button
    // 4. For external URLs: Create clickable hyperlink
    // 5. Return formatted VBox with media viewer
}
```

**Key Features:**
- JavaFX MediaPlayer for videos
- Play/Pause/Stop controls
- System default PDF viewer integration
- External URL browser integration
- Graceful fallback for missing media

### CourseForm.fxml

**Updated Content URL Field:**

```xml
<HBox spacing="10" alignment="CENTER_LEFT">
    <TextField fx:id="contentUrlField" 
               promptText="https://example.com/course or local file path" 
               styleClass="modern-text-field" 
               HBox.hgrow="ALWAYS"/>
    <Button text="📁 Browse" 
            onAction="#handleBrowseFile" 
            styleClass="btn, btn-secondary"/>
</HBox>
```

## 📊 Database Schema

No database changes required! The feature uses the existing `content_url` field in the `course` table:

```sql
-- Existing field
content_url VARCHAR(500)

-- Examples of stored values:
-- Local video: "media/courses/course_1709000000000.mp4"
-- Local PDF: "media/courses/course_1709000001000.pdf"
-- External URL: "https://youtube.com/watch?v=..."
```

## 🎨 UI Components

### Admin View (Course Form)

```
┌─────────────────────────────────────────────────┐
│ Content URL:                                    │
│ ┌───────────────────────────────┐ ┌──────────┐ │
│ │ media/courses/course_xxx.mp4  │ │📁 Browse │ │
│ └───────────────────────────────┘ └──────────┘ │
└─────────────────────────────────────────────────┘
```

### User View (Course Details - Video)

```
┌─────────────────────────────────────────────────┐
│ 📺 Course Content:                              │
│ ┌───────────────────────────────────────────┐   │
│ │                                           │   │
│ │          [Video Player Area]              │   │
│ │                                           │   │
│ ├───────────────────────────────────────────┤   │
│ │  ▶ Play    ⏸ Pause    ⏹ Stop            │   │
│ └───────────────────────────────────────────┘   │
└─────────────────────────────────────────────────┘
```

### User View (Course Details - PDF)

```
┌─────────────────────────────────────────────────┐
│ 📺 Course Content:                              │
│ ┌───────────────────────────────────────────┐   │
│ │ PDF Document: course_xxx.pdf              │   │
│ │                                           │   │
│ │        ┌──────────────────┐               │   │
│ │        │  📄 Open PDF     │               │   │
│ │        └──────────────────┘               │   │
│ └───────────────────────────────────────────┘   │
└─────────────────────────────────────────────────┘
```

## 🔐 Security Considerations

### File Validation
- Extension filtering in FileChooser
- Only allowed extensions: video and PDF formats
- File size not currently limited (consider adding)

### File Storage
- Files stored in `media/courses/` directory
- Unique timestamp-based naming prevents overwrites
- Relative paths stored in database (portable)

### Access Control
- Admin-only upload functionality
- Public viewing in Course Catalog
- No authentication currently (TODO: add user roles)

## 🐛 Error Handling

### Upload Errors
- Directory creation failure
- File copy failure
- Invalid file format
- Disk space issues

All errors show user-friendly messages via `showError()` method.

### Playback Errors
- Missing media file
- Unsupported codec
- Corrupted file
- Permission issues

All errors show alert dialogs with error details.

## 📈 Performance Considerations

### File Size
- No current limit on file size
- Large videos may take time to upload
- Consider adding progress indicator for large files

### Video Playback
- JavaFX MediaPlayer handles buffering
- Supports common codecs (H.264, AAC)
- May not support all video formats

### Memory Usage
- MediaPlayer loads video into memory
- Consider streaming for large files
- Stop player when dialog closes to free memory

## 🎯 Use Cases

### Example 1: Upload Video Tutorial

```
1. Admin creates course "Java Basics"
2. Clicks "📁 Browse"
3. Selects "java_tutorial.mp4" from desktop
4. File uploaded to media/courses/course_1709000000000.mp4
5. Content Type auto-set to "video"
6. Saves course
7. User views course in catalog
8. Video plays in embedded player
```

### Example 2: Upload PDF Slides

```
1. Admin creates course "Database Design"
2. Clicks "📁 Browse"
3. Selects "db_slides.pdf" from documents
4. File uploaded to media/courses/course_1709000001000.pdf
5. Content Type auto-set to "pdf"
6. Saves course
7. User views course in catalog
8. Clicks "Open PDF" button
9. PDF opens in Adobe Reader
```

### Example 3: External YouTube Video

```
1. Admin creates course "Web Development"
2. Manually enters YouTube URL in Content URL field
3. Saves course (no file upload needed)
4. User views course in catalog
5. Clicks hyperlink to open in browser
```

## 🔄 Workflow Diagram

```
ADMIN WORKFLOW:
┌─────────────┐
│ Course Form │
└──────┬──────┘
       │
       ├─ Click "Browse" button
       │
       ↓
┌──────────────┐
│ File Chooser │
└──────┬───────┘
       │
       ├─ Select video/PDF
       │
       ↓
┌────────────────┐
│ Upload Handler │
└──────┬─────────┘
       │
       ├─ Create media/courses/ directory
       ├─ Generate unique filename
       ├─ Copy file
       ├─ Set content URL field
       ├─ Auto-detect content type
       │
       ↓
┌──────────────┐
│ Save Course  │
└──────────────┘

USER WORKFLOW:
┌────────────────┐
│ Course Catalog │
└──────┬─────────┘
       │
       ├─ Click "View Course Details"
       │
       ↓
┌──────────────────┐
│ Course Details   │
│ Dialog           │
└──────┬───────────┘
       │
       ├─ Scroll to "Course Content"
       │
       ↓
┌──────────────────┐
│ Media Viewer     │
└──────┬───────────┘
       │
       ├─ If video: Play/Pause/Stop
       ├─ If PDF: Open in viewer
       └─ If URL: Open in browser
```

## 🚀 Future Enhancements

### Potential Improvements

1. **File Size Limit** - Add maximum file size validation
2. **Progress Indicator** - Show upload progress for large files
3. **Thumbnail Generation** - Auto-generate video thumbnails
4. **Multiple Files** - Support multiple media files per course
5. **Cloud Storage** - Upload to AWS S3 or similar
6. **Streaming** - Stream videos instead of loading fully
7. **Subtitles** - Support for video subtitles/captions
8. **Quality Selection** - Multiple video quality options
9. **Download Option** - Allow users to download media
10. **Preview** - Preview media before uploading

### Advanced Features

- **Video Transcoding** - Convert videos to web-friendly formats
- **CDN Integration** - Serve media from CDN for better performance
- **Analytics** - Track video watch time and completion
- **Bookmarks** - Allow users to bookmark video positions
- **Playlists** - Create playlists of multiple videos
- **Interactive Elements** - Add quizzes within videos

## 📝 Testing Checklist

### Admin Tests

- [ ] Click Browse button opens file chooser
- [ ] Can select MP4 video file
- [ ] Can select PDF file
- [ ] File uploads to media/courses/ folder
- [ ] Content URL field populated with path
- [ ] Content Type auto-set correctly
- [ ] Can save course with uploaded media
- [ ] Can edit course and change media
- [ ] Error shown for invalid file types
- [ ] Success message shown on upload

### User Tests

- [ ] Video player appears for video courses
- [ ] Play button starts video
- [ ] Pause button pauses video
- [ ] Stop button stops and resets video
- [ ] PDF button opens PDF in viewer
- [ ] External URL link opens in browser
- [ ] "No media" message for courses without media
- [ ] Video maintains aspect ratio
- [ ] Controls are responsive
- [ ] Dialog closes properly

### Edge Cases

- [ ] Very large video files (>100MB)
- [ ] Corrupted video files
- [ ] Missing media files
- [ ] Special characters in filenames
- [ ] Disk space full
- [ ] Permission denied errors
- [ ] Unsupported video codecs
- [ ] Multiple rapid uploads

## 🎉 Benefits

### For Administrators
- ✅ Easy file upload with browse button
- ✅ No manual file management needed
- ✅ Automatic organization in media folder
- ✅ Support for multiple formats
- ✅ Clear success/error feedback

### For Users
- ✅ Embedded video player
- ✅ No external tools needed for videos
- ✅ Simple PDF viewing
- ✅ Consistent experience
- ✅ Fast local file access

### For System
- ✅ Organized file structure
- ✅ Unique filenames prevent conflicts
- ✅ Relative paths for portability
- ✅ No database schema changes
- ✅ Backward compatible with URLs

## 📚 Related Documentation

- `COURSE_CATALOG_IMPLEMENTATION_GUIDE.md` - Course catalog details
- `README_QUIZ_COURSE_LINKING.md` - Quiz-course linking
- `IMPLEMENTATION_STATUS.md` - Overall status

## ✅ Status

- ✅ File upload functionality implemented
- ✅ Media viewer implemented
- ✅ Video player with controls
- ✅ PDF viewer integration
- ✅ External URL support
- ✅ Error handling
- ✅ UI integration
- ✅ Documentation complete

## 🎊 Ready to Use!

The course media upload feature is fully implemented and ready to use. Just create or edit a course, click the Browse button, and select your media file!
