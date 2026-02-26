# 🖼️ Course Thumbnail Upload Feature

## 🎯 Overview

Administrators can now upload custom thumbnail images for courses. These thumbnails are displayed prominently in the Course Catalog, making courses more visually appealing and easier to identify.

## ✨ Features

### For Administrators

1. **Thumbnail Upload Button** - Browse and select image files from your computer
2. **Supported Formats**:
   - PNG (recommended for quality)
   - JPG/JPEG (recommended for smaller file size)
3. **Automatic Storage** - Files copied to `media/thumbnails/` folder
4. **Unique Naming** - Timestamp-based naming prevents conflicts
5. **Local Path Storage** - Relative path stored in database

### For Users

1. **Visual Course Cards** - Thumbnails displayed at top of each course card
2. **Consistent Size** - All thumbnails displayed as 240x140 pixels
3. **Rounded Corners** - Modern, polished appearance
4. **Fallback Placeholder** - Gradient placeholder with 📚 icon if no thumbnail

## 🚀 How to Use

### Admin: Uploading Course Thumbnail

1. **Go to Course Management**
2. **Create or Edit a Course**
3. **Click the "🖼️ Browse" button** next to Thumbnail URL field
4. **Select your image file** (PNG or JPG)
5. **File is automatically uploaded** to `media/thumbnails/` folder
6. **Path appears in Thumbnail URL field**
7. **Save the course**

### User: Viewing Thumbnails

1. **Go to Course Catalog**
2. **Browse courses**
3. **See thumbnail** at the top of each course card
4. **If no thumbnail**: See gradient placeholder with 📚 icon

## 📁 File Structure

```
Project Root
├── media/
│   ├── courses/          ← Course media (videos/PDFs)
│   └── thumbnails/       ← Course thumbnails (NEW!)
│       ├── README.md
│       ├── thumb_1709000000000.png
│       ├── thumb_1709000001000.jpg
│       └── thumb_1709000002000.jpeg
│
├── src/
│   └── main/
│       ├── java/
│       │   └── edu/connections3a8/controllers/
│       │       ├── CourseController.java
│       │       │   └── handleBrowseThumbnail()  ← Upload logic
│       │       │
│       │       └── CourseCatalogController.java
│       │           ├── createCourseCard()       ← Display logic
│       │           └── addThumbnailPlaceholder() ← Fallback
│       │
│       └── resources/
│           └── CourseForm.fxml
│               └── Browse button (🖼️)          ← UI element
│
└── database (MySQL)
    └── course table
        └── thumbnail_url column
            ├── "media/thumbnails/thumb_xxx.png"
            ├── "media/thumbnails/thumb_xxx.jpg"
            └── "https://example.com/image.jpg"
```

## 🔧 Technical Implementation

### CourseController.java

**New Method: `handleBrowseThumbnail()`**

```java
@FXML
private void handleBrowseThumbnail() {
    // 1. Open file chooser with image filters
    // 2. User selects PNG or JPG file
    // 3. Create media/thumbnails directory
    // 4. Generate unique filename with timestamp
    // 5. Copy file to thumbnails directory
    // 6. Set relative path in thumbnail URL field
    // 7. Show success message
}
```

**Key Features:**
- Extension filters for PNG and JPG only
- Unique filename generation: `thumb_<timestamp>.<ext>`
- Automatic directory creation
- Error handling with user feedback

### CourseCatalogController.java

**Updated Method: `createCourseCard(Course course)`**

```java
private VBox createCourseCard(Course course) {
    // 1. Create card VBox
    // 2. Check if thumbnail URL exists
    // 3. If exists and file found:
    //    - Load image
    //    - Create ImageView (240x140)
    //    - Apply rounded corners
    //    - Add to card
    // 4. If not found:
    //    - Add gradient placeholder
    // 5. Add title, description, metadata
    // 6. Return card
}
```

**New Method: `addThumbnailPlaceholder(VBox card)`**

```java
private void addThumbnailPlaceholder(VBox card) {
    // Creates gradient placeholder with 📚 icon
    // Size: 240x140 pixels
    // Colors: Baltic Blue to Faded Copper gradient
}
```

### CourseForm.fxml

**Updated Thumbnail Field:**

```xml
<HBox spacing="10" alignment="CENTER_LEFT">
    <TextField fx:id="thumbnailField" 
               promptText="https://example.com/image.jpg or local file path" 
               styleClass="modern-text-field" 
               HBox.hgrow="ALWAYS"/>
    <Button text="🖼️ Browse" 
            onAction="#handleBrowseThumbnail" 
            styleClass="btn, btn-secondary"/>
</HBox>
```

## 📊 Database Schema

No database changes required! Uses the existing `thumbnail_url` field:

```sql
-- Existing field
thumbnail_url VARCHAR(500)

-- Examples of stored values:
-- Local PNG: "media/thumbnails/thumb_1709000000000.png"
-- Local JPG: "media/thumbnails/thumb_1709000001000.jpg"
-- External URL: "https://example.com/course-image.jpg"
```

## 🎨 UI Components

### Admin View (Course Form)

```
┌─────────────────────────────────────────────────┐
│ Thumbnail URL:                                  │
│ ┌───────────────────────────────┐ ┌──────────┐ │
│ │ media/thumbnails/thumb_xxx.png│ │🖼️ Browse │ │
│ └───────────────────────────────┘ └──────────┘ │
└─────────────────────────────────────────────────┘
```

### User View (Course Catalog - With Thumbnail)

```
┌─────────────────────────────────┐
│ ┌───────────────────────────┐   │
│ │                           │   │
│ │   [Course Thumbnail]      │   │
│ │      240 x 140 px         │   │
│ │                           │   │
│ └───────────────────────────┘   │
│                                 │
│ Introduction to Java            │
│ Learn Java basics and OOP...    │
│                                 │
│ 📊 beginner  ⭐ 100 pts         │
│                                 │
│ 👍 12  👎 2  🚩                 │
│                                 │
│        [View Course]            │
└─────────────────────────────────┘
```

### User View (Course Catalog - Without Thumbnail)

```
┌─────────────────────────────────┐
│ ┌───────────────────────────┐   │
│ │                           │   │
│ │      📚                   │   │
│ │   [Gradient Placeholder]  │   │
│ │                           │   │
│ └───────────────────────────┘   │
│                                 │
│ Database Design                 │
│ Learn database principles...    │
│                                 │
│ 📊 intermediate  ⭐ 150 pts     │
│                                 │
│ 👍 8   👎 1  🚩                 │
│                                 │
│        [View Course]            │
└─────────────────────────────────┘
```

## 📐 Image Specifications

### Recommended Dimensions
- **Aspect Ratio**: 16:9 (ideal) or 4:3
- **Minimum Size**: 240x140 pixels
- **Recommended Size**: 480x280 pixels (2x for retina)
- **Maximum Size**: No hard limit, but keep under 2MB

### Display Size
- **Width**: 240 pixels (fixed)
- **Height**: 140 pixels (fixed)
- **Fit**: Cropped to fill (preserveRatio = false)
- **Corners**: Rounded (16px arc)

### File Size
- **PNG**: Typically 50-500 KB
- **JPG**: Typically 20-200 KB
- **Recommended**: Compress images before uploading

## 🎨 Placeholder Design

When no thumbnail is uploaded:

```
┌─────────────────────────┐
│                         │
│                         │
│          📚            │
│                         │
│                         │
└─────────────────────────┘
```

**Styling:**
- Background: Linear gradient (Baltic Blue → Faded Copper)
- Icon: 📚 (48px font size)
- Size: 240x140 pixels
- Corners: Rounded (8px radius)

## 🔐 Security Considerations

### File Validation
- Extension filtering in FileChooser
- Only PNG and JPG formats allowed
- File size not currently limited (consider adding)

### File Storage
- Files stored in `media/thumbnails/` directory
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

### Display Errors
- Missing thumbnail file
- Corrupted image file
- Invalid file path
- Permission issues

All errors gracefully fall back to placeholder.

## 📈 Performance Considerations

### File Size
- No current limit on file size
- Large images may slow down catalog loading
- Recommend compressing images before upload

### Loading Performance
- Images loaded on-demand when catalog opens
- JavaFX Image caching helps performance
- Consider lazy loading for many courses

### Memory Usage
- Each image loaded into memory
- 20 courses × 200KB = ~4MB memory
- Acceptable for most systems

## 🎯 Use Cases

### Example 1: Upload Course Thumbnail

```
1. Admin creates course "Java Basics"
2. Clicks "🖼️ Browse"
3. Selects "java-logo.png" from desktop
4. File uploaded to media/thumbnails/thumb_1709000000000.png
5. Path set in Thumbnail URL field
6. Saves course
7. User sees Java logo in catalog
```

### Example 2: Update Existing Thumbnail

```
1. Admin edits course "Database Design"
2. Clicks "🖼️ Browse"
3. Selects new image "db-diagram.jpg"
4. File uploaded to media/thumbnails/thumb_1709000001000.jpg
5. Old path replaced with new path
6. Saves course
7. User sees new thumbnail in catalog
```

### Example 3: External URL Thumbnail

```
1. Admin creates course "Web Development"
2. Manually enters image URL in Thumbnail URL field
3. Example: "https://example.com/web-dev-thumb.jpg"
4. Saves course (no file upload needed)
5. User sees external image in catalog
```

## 🔄 Workflow Diagram

```
ADMIN WORKFLOW:
┌─────────────┐
│ Course Form │
└──────┬──────┘
       │
       ├─ Click "🖼️ Browse" button
       │
       ↓
┌──────────────┐
│ File Chooser │
└──────┬───────┘
       │
       ├─ Select PNG/JPG
       │
       ↓
┌────────────────┐
│ Upload Handler │
└──────┬─────────┘
       │
       ├─ Create media/thumbnails/ directory
       ├─ Generate unique filename
       ├─ Copy file
       ├─ Set thumbnail URL field
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
       ├─ Load courses
       │
       ↓
┌──────────────────┐
│ For each course: │
└──────┬───────────┘
       │
       ├─ Check thumbnail_url
       │
       ↓
┌──────────────────┐
│ If file exists:  │
│ Load & display   │
│                  │
│ If not:          │
│ Show placeholder │
└──────────────────┘
```

## 🚀 Future Enhancements

### Short Term
1. Add file size limit (e.g., 2MB)
2. Show image preview before upload
3. Crop/resize tool in upload dialog
4. Drag-and-drop upload
5. Multiple thumbnail sizes

### Long Term
1. Cloud storage integration (AWS S3)
2. Automatic image optimization
3. CDN integration for faster loading
4. Image editing tools (crop, filter, text)
5. AI-generated thumbnails
6. Video thumbnail extraction
7. Thumbnail templates

## 📝 Testing Checklist

### Admin Tests

- [ ] Click Browse button opens file chooser
- [ ] Can select PNG file
- [ ] Can select JPG file
- [ ] File uploads to media/thumbnails/ folder
- [ ] Thumbnail URL field populated with path
- [ ] Can save course with uploaded thumbnail
- [ ] Can edit course and change thumbnail
- [ ] Error shown for invalid file types
- [ ] Success message shown on upload

### User Tests

- [ ] Thumbnail appears in course card
- [ ] Thumbnail is 240x140 pixels
- [ ] Thumbnail has rounded corners
- [ ] Placeholder shown for courses without thumbnail
- [ ] Placeholder has gradient background
- [ ] Placeholder shows 📚 icon
- [ ] External URL thumbnails work
- [ ] Missing files show placeholder

### Edge Cases

- [ ] Very large image files (>5MB)
- [ ] Corrupted image files
- [ ] Missing thumbnail files
- [ ] Special characters in filenames
- [ ] Disk space full
- [ ] Permission denied errors
- [ ] Multiple rapid uploads

## 🎉 Benefits

### For Administrators
- ✅ Easy thumbnail upload with browse button
- ✅ No manual file management needed
- ✅ Automatic organization in thumbnails folder
- ✅ Support for PNG and JPG formats
- ✅ Clear success/error feedback

### For Users
- ✅ Visual course identification
- ✅ Professional appearance
- ✅ Consistent card layout
- ✅ Faster course browsing
- ✅ Better user experience

### For System
- ✅ Organized file structure
- ✅ Unique filenames prevent conflicts
- ✅ Relative paths for portability
- ✅ No database schema changes
- ✅ Backward compatible with URLs

## 📚 Related Documentation

- `COURSE_MEDIA_UPLOAD_FEATURE.md` - Course media upload
- `SCROLLABLE_CATALOG_UPDATE.md` - Scrollable catalog
- `COURSE_CATALOG_IMPLEMENTATION_GUIDE.md` - Catalog details

## ✅ Status

- ✅ Thumbnail upload functionality implemented
- ✅ Image display in course cards
- ✅ Placeholder for missing thumbnails
- ✅ External URL support
- ✅ Error handling
- ✅ UI integration
- ✅ Documentation complete

## 🎊 Ready to Use!

The course thumbnail upload feature is fully implemented and ready to use. Just create or edit a course, click the Browse button, and select your thumbnail image!
