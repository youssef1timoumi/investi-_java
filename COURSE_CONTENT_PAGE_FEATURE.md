# 📺 Course Content Page Feature

## 🎯 Overview

The Course Catalog now has two separate buttons for each course:
1. **View Details** - Opens a dialog with course information
2. **Course Content** - Opens a dedicated page with media player, PDF viewer, and related quizzes

## ✨ Features

### 1. Course Catalog Cards - Two Buttons

Each course card now displays:
- **View Details** button (Faded Copper color) - Quick overview in dialog
- **Course Content** button (Baltic Blue color) - Full content page

### 2. View Details Dialog

Shows course information in a popup dialog:
- Course title and description
- Category, difficulty, duration, reward points
- Language, status, content type
- Button to open Course Content page
- Close button

### 3. Course Content Page

A dedicated full-page view with:

#### Course Header
- Course title
- Info badges (difficulty, points, duration, category)
- Back to Catalog button

#### Media Section
- **Video Player** (for MP4, AVI, MKV, MOV, WMV, FLV)
  - Embedded video player with controls
  - Play, Pause, Stop buttons
  - Rewind 10s and Forward 10s buttons
  - Full-width display (800px)

- **PDF Viewer** (for PDF files)
  - Embedded PDF viewer using WebView
  - 600px height display
  - "Open in External Viewer" button
  - Fallback to external viewer if embedding fails

- **External URLs**
  - Clickable hyperlink to open in browser

#### Related Quizzes Section
- List of all quizzes linked to the course
- Quiz cards with icon, title, details
- "Take Quiz" button for each quiz
- Quiz count badge

## 🎨 UI Design

### Course Catalog Card Layout

```
┌─────────────────────────────────┐
│ ┌───────────────────────────┐   │
│ │   [Course Thumbnail]      │   │
│ └───────────────────────────┘   │
│                                 │
│ Course Title                    │
│ Description...                  │
│                                 │
│ 📊 beginner  ⭐ 100 pts         │
│                                 │
│ 👍 12  👎 2  🚩                 │
│                                 │
│ ┌─────────────┐ ┌────────────┐ │
│ │View Details │ │Course      │ │
│ │             │ │Content     │ │
│ └─────────────┘ └────────────┘ │
└─────────────────────────────────┘
```

### Course Content Page Layout

```
┌──────────────────────────────────────────┐
│ ← Back to Catalog    Course Title        │
├──────────────────────────────────────────┤
│ 📊 beginner  ⭐ 100 pts  ⏱️ 60 min      │
├──────────────────────────────────────────┤
│                                          │
│ 📺 Course Media                          │
│ ┌────────────────────────────────────┐  │
│ │                                    │  │
│ │      [Video Player / PDF]          │  │
│ │                                    │  │
│ │  ▶ Play  ⏸ Pause  ⏹ Stop         │  │
│ │  ⏪ Rewind  ⏩ Forward             │  │
│ └────────────────────────────────────┘  │
│                                          │
├──────────────────────────────────────────┤
│                                          │
│ 📝 Related Quizzes (3)                   │
│                                          │
│ ┌────────────────────────────────────┐  │
│ │ 📝  Quiz Title                     │  │
│ │     10 questions • 50 pts          │  │
│ │                    [Take Quiz]     │  │
│ └────────────────────────────────────┘  │
│                                          │
└──────────────────────────────────────────┘
```

## 🚀 How to Use

### For Users

#### Viewing Course Details
1. Go to Course Catalog
2. Find a course
3. Click **"View Details"** button
4. See course information in dialog
5. Click "View Course Content" or "Close"

#### Accessing Course Content
1. Go to Course Catalog
2. Find a course
3. Click **"Course Content"** button
4. View media (video/PDF)
5. Browse related quizzes
6. Take quizzes
7. Click "Back to Catalog" when done

#### Watching Videos
1. Open Course Content page
2. Video player loads automatically
3. Click **▶ Play** to start
4. Use **⏸ Pause** to pause
5. Use **⏹ Stop** to stop and reset
6. Use **⏪ Rewind 10s** to go back
7. Use **⏩ Forward 10s** to skip ahead

#### Reading PDFs
1. Open Course Content page
2. PDF displays in embedded viewer (600px height)
3. Scroll within the PDF viewer
4. Click **"Open in External Viewer"** to open in default PDF app
5. If embedding fails, only external button shows

## 📁 File Structure

```
Project Root
├── src/
│   └── main/
│       ├── java/
│       │   └── edu/connections3a8/controllers/
│       │       ├── CourseCatalogController.java
│       │       │   ├── createCourseCard()        ← Two buttons
│       │       │   ├── openCourseDetails()       ← Details dialog
│       │       │   └── openCourseContent()       ← Navigate to content page
│       │       │
│       │       └── CourseContentController.java  ← NEW!
│       │           ├── setCourse()               ← Receive course data
│       │           ├── loadCourseContent()       ← Load all content
│       │           ├── loadMedia()               ← Video/PDF/URL
│       │           ├── loadVideoPlayer()         ← Video player
│       │           ├── loadPDFViewer()           ← PDF viewer
│       │           ├── loadRelatedQuizzes()      ← Quiz list
│       │           ├── createQuizCard()          ← Quiz card UI
│       │           └── handleBack()              ← Back to catalog
│       │
│       └── resources/
│           ├── CourseCatalogView.fxml            ← Updated cards
│           ├── courseCatalog.css
│           ├── CourseContentView.fxml            ← NEW!
│           └── courseContent.css                 ← NEW!
```

## 🔧 Technical Implementation

### CourseCatalogController.java

**Updated: `createCourseCard()`**
```java
// Two buttons instead of one
Button viewDetailsBtn = new Button("View Details");
Button courseContentBtn = new Button("Course Content");

viewDetailsBtn.setOnAction(e -> openCourseDetails(course));
courseContentBtn.setOnAction(e -> openCourseContent(course));
```

**Updated: `openCourseDetails()`**
```java
// Simplified dialog - only shows course info
// No media viewer, no quizzes
// Has button to open Course Content page
```

**New: `openCourseContent()`**
```java
// Navigate to CourseContentView.fxml
// Pass course object to controller
// Change window title
```

### CourseContentController.java (NEW)

**Key Methods:**

1. **`setCourse(Course course)`**
   - Receives course from catalog
   - Triggers content loading

2. **`loadMedia()`**
   - Detects file type (video/PDF/URL)
   - Calls appropriate loader

3. **`loadVideoPlayer(File videoFile)`**
   - Creates MediaPlayer and MediaView
   - Adds control buttons
   - 800px width, preserves ratio

4. **`loadPDFViewer(File pdfFile)`**
   - Uses WebView to embed PDF
   - 600px height display
   - Fallback to external viewer button

5. **`loadRelatedQuizzes()`**
   - Fetches quiz IDs from database
   - Creates quiz cards
   - Shows count badge

6. **`handleBack()`**
   - Stops media player if running
   - Disposes media player
   - Returns to catalog

## 🎨 Styling

### Color Scheme

- **View Details Button**: #9B7E46 (Faded Copper)
- **Course Content Button**: #456990 (Baltic Blue)
- **Play Button**: #28A745 (Green)
- **Pause Button**: #FFA500 (Orange)
- **Stop Button**: #DC3545 (Red)
- **Quiz Cards**: #9B7E46 border (Faded Copper)

### Button Sizes

- **Catalog Buttons**: 130px width, 10-20px padding
- **Media Controls**: Standard padding, auto width
- **Take Quiz**: Auto width, 8-16px padding

## 📊 Media Support

### Video Formats
- MP4 (recommended)
- AVI
- MKV
- MOV
- WMV
- FLV

### Document Formats
- PDF (embedded viewer + external option)

### External Content
- Any URL (opens in browser)

## 🎮 Video Player Controls

### Available Controls
1. **▶ Play** - Start/resume playback
2. **⏸ Pause** - Pause playback
3. **⏹ Stop** - Stop and reset to beginning
4. **⏪ Rewind 10s** - Jump back 10 seconds
5. **⏩ Forward 10s** - Jump forward 10 seconds

### Player Features
- Automatic sizing (800px width)
- Aspect ratio preservation
- Black background container
- Rounded corners (8px)

## 📄 PDF Viewer Features

### Embedded Viewer
- WebView-based display
- 600px height
- 800px width
- Scrollable content
- White background

### External Viewer Option
- "Open in External Viewer" button
- Opens in system default PDF app
- Fallback if embedding fails

### Fallback Behavior
If WebView fails to load PDF:
- Shows file name
- Shows "Open PDF" button only
- Opens in external viewer

## 🎯 Quiz Integration

### Quiz Card Display
- Quiz icon (📝)
- Quiz title
- Question count
- Points reward
- Difficulty level
- "Take Quiz" button

### Quiz Actions
- Click "Take Quiz" to open quiz
- Currently shows placeholder dialog
- Ready for quiz-taking interface integration

## 🔐 Security & Performance

### Media Player Management
- MediaPlayer stopped on page exit
- MediaPlayer disposed to free resources
- Prevents memory leaks

### PDF Handling
- WebView sandboxed environment
- Local file access only
- No remote PDF loading (security)

### Error Handling
- Try-catch for media loading
- Fallback for PDF viewer
- User-friendly error messages
- Graceful degradation

## 🐛 Error Handling

### Video Loading Errors
- Shows error message
- Displays file name
- Logs to console

### PDF Loading Errors
- Falls back to external viewer button
- Shows file name
- No crash or blank screen

### Missing Media
- Shows "No media available" message
- Doesn't break page layout

### Quiz Loading Errors
- Shows error message
- Doesn't prevent page display

## 📈 Performance Considerations

### Video Player
- Lazy loading (only when page opens)
- Automatic cleanup on exit
- Memory management via dispose()

### PDF Viewer
- WebView overhead (~50MB)
- Consider external viewer for large PDFs
- Embedded viewer limited to reasonable file sizes

### Page Navigation
- Fast transition between pages
- No reload of catalog data
- Smooth back navigation

## 🎯 Use Cases

### Example 1: Watch Course Video

```
1. User opens Course Catalog
2. Clicks "Course Content" on Java course
3. Course Content page opens
4. Video player loads automatically
5. User clicks "▶ Play"
6. Watches video
7. Uses "⏪ Rewind 10s" to review section
8. Clicks "Back to Catalog" when done
```

### Example 2: Read Course PDF

```
1. User opens Course Catalog
2. Clicks "Course Content" on Database course
3. Course Content page opens
4. PDF viewer displays document
5. User scrolls through PDF
6. Clicks "Open in External Viewer"
7. PDF opens in Adobe Reader
8. Returns to browser
9. Clicks "Back to Catalog"
```

### Example 3: Take Related Quiz

```
1. User opens Course Content page
2. Scrolls to "Related Quizzes" section
3. Sees 3 quizzes listed
4. Clicks "Take Quiz" on first quiz
5. Quiz interface opens (placeholder)
6. Completes quiz
7. Returns to Course Content page
8. Takes another quiz
```

### Example 4: Quick Details Check

```
1. User opens Course Catalog
2. Clicks "View Details" on course
3. Dialog opens with course info
4. Reviews difficulty, duration, points
5. Decides to take course
6. Clicks "View Course Content"
7. Dialog closes, content page opens
```

## 🔄 Navigation Flow

```
Course Catalog
    │
    ├─ Click "View Details"
    │   └─ Details Dialog
    │       ├─ Click "View Course Content" → Course Content Page
    │       └─ Click "Close" → Back to Catalog
    │
    └─ Click "Course Content"
        └─ Course Content Page
            ├─ Watch video / Read PDF
            ├─ Take quizzes
            └─ Click "Back to Catalog" → Course Catalog
```

## 🚀 Future Enhancements

### Short Term
1. Progress tracking (video position)
2. Bookmark feature
3. Notes/annotations
4. Download PDF option
5. Fullscreen video mode

### Long Term
1. Interactive video quizzes
2. Video chapters/timestamps
3. Subtitle support
4. Playback speed control
5. Picture-in-picture mode
6. PDF annotation tools
7. Course completion tracking
8. Certificate generation

## 📝 Testing Checklist

### Course Catalog
- [ ] Two buttons appear on each card
- [ ] "View Details" button works
- [ ] "Course Content" button works
- [ ] Button colors correct (Copper/Blue)
- [ ] Button hover effects work

### Details Dialog
- [ ] Opens when clicking "View Details"
- [ ] Shows all course information
- [ ] "View Course Content" button works
- [ ] "Close" button works
- [ ] Dialog is scrollable

### Course Content Page
- [ ] Opens when clicking "Course Content"
- [ ] Course title displays correctly
- [ ] Info badges show correct data
- [ ] Back button returns to catalog

### Video Player
- [ ] Video loads and displays
- [ ] Play button starts video
- [ ] Pause button pauses video
- [ ] Stop button stops and resets
- [ ] Rewind button goes back 10s
- [ ] Forward button skips ahead 10s
- [ ] Video size is correct (800px)

### PDF Viewer
- [ ] PDF displays in embedded viewer
- [ ] PDF is scrollable
- [ ] "Open in External Viewer" works
- [ ] Fallback button appears if embed fails
- [ ] External viewer opens PDF correctly

### Quizzes Section
- [ ] Quiz count displays correctly
- [ ] Quiz cards appear
- [ ] Quiz details are correct
- [ ] "Take Quiz" button works
- [ ] Empty state shows if no quizzes

### Navigation
- [ ] Back button returns to catalog
- [ ] Media player stops on exit
- [ ] No memory leaks
- [ ] Smooth transitions

## 🎉 Benefits

### For Users
- ✅ Clear separation: details vs content
- ✅ Dedicated content viewing page
- ✅ Embedded video player with controls
- ✅ Embedded PDF viewer
- ✅ All related quizzes in one place
- ✅ Better learning experience

### For System
- ✅ Better code organization
- ✅ Reusable content page
- ✅ Proper media player cleanup
- ✅ Scalable architecture
- ✅ Easy to extend

## 📚 Related Documentation

- `COURSE_CATALOG_IMPLEMENTATION_GUIDE.md` - Catalog overview
- `COURSE_MEDIA_UPLOAD_FEATURE.md` - Media upload
- `THUMBNAIL_UPLOAD_FEATURE.md` - Thumbnail upload
- `QUIZ_COURSE_LINKING_FEATURE.md` - Quiz linking

## ✅ Status

- ✅ Two buttons in catalog cards
- ✅ View Details dialog implemented
- ✅ Course Content page created
- ✅ Video player with controls
- ✅ PDF viewer (embedded + external)
- ✅ Related quizzes display
- ✅ Navigation working
- ✅ Media player cleanup
- ✅ Error handling
- ✅ Styling complete

## 🎊 Ready to Use!

The Course Content Page feature is fully implemented and ready for testing. Users can now view course details in a dialog or access the full content page with media player and quizzes!
