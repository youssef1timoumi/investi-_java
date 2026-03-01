# Course History Feature

## Overview
Tracks user course visits and displays a viewing history, allowing users to see which courses they've recently accessed and their progress.

## Features

### 1. Automatic Visit Tracking
- Every time a user opens a course content page, the visit is logged
- Tracks timestamp of each visit
- Updates existing record if course was visited before (keeps most recent visit)

### 2. History Display
- "📚 My History" button in course catalog header
- Shows up to 20 most recently visited courses
- Displays for each course:
  - Course title
  - Difficulty level
  - Reward points
  - Completion percentage (if tracked)
  - Time since last visit (days/hours/minutes ago)

### 3. Progress Tracking (Optional)
- Stores completion percentage (0-100%)
- Stores last position (for videos or pages)
- Can be updated as user progresses through course

### 4. History Management
- View recent history via dialog
- Clear history option (method available)
- Automatic sorting by most recent visit

## Database Schema

### course_history Table
```sql
CREATE TABLE course_history (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    course_id BIGINT NOT NULL,
    visited_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_position INT DEFAULT 0,
    completion_percentage INT DEFAULT 0,
    FOREIGN KEY (user_id) REFERENCES personne(id) ON DELETE CASCADE,
    FOREIGN KEY (course_id) REFERENCES course(id) ON DELETE CASCADE,
    INDEX idx_course_history_user (user_id),
    INDEX idx_course_history_course (course_id),
    INDEX idx_course_history_visited (visited_at),
    INDEX idx_course_history_user_course (user_id, course_id)
);
```

### Fields
- `id`: Primary key
- `user_id`: Reference to user
- `course_id`: Reference to course
- `visited_at`: Timestamp of last visit
- `last_position`: Last video position (seconds) or page number
- `completion_percentage`: Progress through course (0-100%)

## Implementation Details

### Files Created

#### Entity
- `src/main/java/edu/connections3a8/entities/CourseHistory.java`
  - Represents a course visit record
  - Includes Course object for joined queries

#### Migration
- `COURSE_HISTORY_MIGRATION.sql`
  - Creates course_history table
  - Adds sample data for testing

### Files Modified

#### Service
- `src/main/java/edu/connections3a8/services/CouseService.java`
  - `addCourseVisit()` - Log a course visit
  - `getUserCourseHistory()` - Get user's visit history
  - `updateCourseProgress()` - Update completion percentage
  - `getCourseVisitCount()` - Count visits for a course
  - `clearUserHistory()` - Delete all user history

#### Controllers
- `src/main/java/edu/connections3a8/controllers/CourseContentController.java`
  - Logs visit when `setCourse()` is called
  - Automatic tracking on course page load

- `src/main/java/edu/connections3a8/controllers/CourseCatalogController.java`
  - Added `handleViewHistory()` method
  - Added `styleHistoryDialog()` for dark mode support
  - Shows history in formatted dialog

#### FXML
- `src/main/resources/CourseCatalogView.fxml`
  - Added "📚 My History" button in header

## User Flow

1. **Visit Course**: User clicks "Course Content" on any course
2. **Auto-Log**: Visit is automatically logged to database
3. **View History**: User clicks "📚 My History" button
4. **See List**: Dialog shows recent courses with details
5. **Time Info**: Shows how long ago each course was visited

## Service Methods

### addCourseVisit(userId, courseId)
- Logs a course visit
- Updates timestamp if already visited
- Creates new record if first visit

### getUserCourseHistory(userId, limit)
- Returns list of CourseHistory objects
- Includes full Course details via JOIN
- Sorted by most recent visit
- Limited to specified number of records

### updateCourseProgress(userId, courseId, percentage)
- Updates completion percentage
- Can be called as user progresses through course

### getCourseVisitCount(userId, courseId)
- Returns number of times user visited course
- Useful for analytics

### clearUserHistory(userId)
- Deletes all history for a user
- Can be used for privacy/reset feature

## History Dialog

### Display Format
```
You've visited X course(s) recently:

1. Course Title
   📊 beginner • ⭐ 100 pts • 45% complete
   🕒 2 days ago

2. Another Course
   📊 intermediate • ⭐ 150 pts
   🕒 5 hours ago
```

### Time Display
- Just now (< 1 minute)
- X minute(s) ago
- X hour(s) ago
- X day(s) ago

### Dark Mode Support
- Dialog styled to match app theme
- Dark background and borders
- Light text colors
- Styled buttons

## Future Enhancements

### Progress Tracking
- Track video playback position
- Resume from last position
- Show progress bar in history

### Advanced Features
- Filter history by date range
- Search within history
- Export history to CSV
- History statistics (most visited, time spent)
- Recommendations based on history

### UI Improvements
- Dedicated history page (not just dialog)
- Thumbnail previews in history
- Quick access to continue watching
- History timeline view
- Group by date (Today, Yesterday, This Week)

### Analytics
- Track time spent on each course
- Completion rates
- Learning patterns
- Popular courses

## Testing

### SQL Commands
```sql
-- Run migration
source COURSE_HISTORY_MIGRATION.sql;

-- View user history
SELECT ch.*, c.title 
FROM course_history ch 
JOIN course c ON ch.course_id = c.id 
WHERE ch.user_id = 1 
ORDER BY ch.visited_at DESC;

-- Count visits
SELECT COUNT(*) FROM course_history WHERE user_id = 1;

-- Clear history
DELETE FROM course_history WHERE user_id = 1;
```

### Test Scenarios
1. Visit a course → Check database for new record
2. Visit same course again → Check timestamp updated
3. Visit multiple courses → Check order in history
4. Click "My History" → See all visited courses
5. Empty history → See "No History Yet" message

## Privacy Considerations
- Users can clear their history
- History is user-specific (not shared)
- Can be disabled per user if needed
- Complies with data retention policies

## Performance
- Indexed on user_id and visited_at for fast queries
- Composite index on (user_id, course_id) for updates
- Limit parameter prevents loading too many records
- Efficient JOIN query for history with course details

## Color Palette
- History Button: Gold gradient (#E4C45E, #C8A84E, #9B7E46)
- Dialog Border (Light): #456990 (Baltic Blue)
- Dialog Border (Dark): rgba(70,70,100,0.6)
- Dialog Background (Dark): #161630
- Text (Dark): #F0F2FA
