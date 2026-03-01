# Locked Courses Feature

## Overview
Courses can now be locked based on minimum points requirements. Users must earn enough points before they can access certain courses, creating a progression system.

## Features

### 1. Minimum Points Requirement
- Each course can have a `minimum_points_required` field
- Default value is 0 (unlocked for everyone)
- Courses with points > 0 are locked until user earns enough points

### 2. Visual Indicators
- **Locked Badge**: Red badge showing "🔒 Requires X points"
- **Reduced Opacity**: Locked courses appear at 60% opacity
- **Red Border**: Locked courses have a red border instead of blue
- **Grayscale Effect**: Locked courses appear slightly desaturated
- **Disabled Buttons**: Action buttons are replaced with "🔒 Locked" button

### 3. Course Catalog Display
- All courses are visible (both locked and unlocked)
- Locked courses clearly indicate points requirement
- Users can see what they're working towards
- Interaction buttons (like/dislike) still work on locked courses

### 4. Course Form Updates
- New field: "Min Points Required"
- Accepts numeric values (0 or positive integers)
- 0 means course is unlocked for everyone
- Validation ensures only positive numbers

## Database Changes

### Migration SQL
Run `LOCKED_COURSES_MIGRATION.sql` to:
1. Add `minimum_points_required` column to `course` table
2. Add index for better query performance
3. Set existing courses to 0 (unlocked)
4. Create test user with 150 points

### Schema Update
```sql
ALTER TABLE course 
ADD COLUMN minimum_points_required INT DEFAULT 0 
COMMENT 'Minimum points required to unlock this course';

ALTER TABLE course 
ADD INDEX idx_course_min_points (minimum_points_required);
```

## Testing

### Test User
The migration creates a test user:
- Name: Test User
- ID: (auto-generated)
- Points: 150
- Level: 2

### Test Scenarios

1. **Create Unlocked Course**
   - Set minimum points to 0
   - All users can access

2. **Create Locked Course (100 points)**
   - Set minimum points to 100
   - Test user (150 points) can access
   - Users with < 100 points cannot access

3. **Create Locked Course (200 points)**
   - Set minimum points to 200
   - Test user (150 points) cannot access
   - Appears locked in catalog

4. **Earn Points**
   - Complete quizzes to earn points
   - Watch courses unlock as points increase

## User Flow

1. **View Catalog**: User sees all courses, some locked
2. **Check Requirements**: Locked courses show points needed
3. **Earn Points**: Complete quizzes, courses to earn points
4. **Unlock Courses**: Courses automatically unlock when points threshold met
5. **Access Content**: Unlocked courses can be viewed normally

## Implementation Details

### Files Modified

#### Database
- `LOCKED_COURSES_MIGRATION.sql` - Migration script

#### Entity
- `src/main/java/edu/connections3a8/entities/Course.java`
  - Added `minimumPointsRequired` field
  - Added getter/setter methods

#### Service
- `src/main/java/edu/connections3a8/services/CouseService.java`
  - Updated `addCourse()` to include minimum points
  - Updated `addDraftCourse()` to include minimum points
  - Updated `updateCourse()` to include minimum points
  - Updated `mapResultSetToCourse()` to read minimum points

#### Controllers
- `src/main/java/edu/connections3a8/controllers/CourseController.java`
  - Added `minimumPointsField` TextField
  - Updated form validation
  - Updated course creation/editing logic
  - Updated form clear method

- `src/main/java/edu/connections3a8/controllers/CourseCatalogController.java`
  - Added user points checking
  - Updated `createCourseCard()` to show locked state
  - Added lock badge visual indicator
  - Modified card styling for locked courses
  - Disabled action buttons for locked courses

#### FXML
- `src/main/resources/CourseForm.fxml`
  - Added minimum points field (row 10)
  - Updated subsequent row indices

### Key Logic

#### Checking if Course is Locked
```java
int userPoints = gamificationService.getUserPoints(currentUserId).getPoints();
boolean isLocked = course.getMinimumPointsRequired() > userPoints;
```

#### Visual Styling
- Locked courses: 60% opacity, red border, grayscale filter
- Unlocked courses: 100% opacity, blue border, normal colors

#### Button States
- Locked: Single disabled "🔒 Locked" button
- Unlocked: "View Details" and "Course Content" buttons

## Progression System

### Example Course Ladder
1. **Beginner Courses** (0 points) - Free for all
2. **Intermediate Courses** (100 points) - Complete 2-3 beginner courses
3. **Advanced Courses** (250 points) - Complete several intermediate courses
4. **Expert Courses** (500 points) - Master level content

### Points Sources
- Quiz completion: 50-100 points
- Course completion: 100-200 points
- Badges earned: Varies
- Achievements: Bonus points

## Future Enhancements
- Course prerequisites (must complete Course A before Course B)
- Badge requirements (need specific badge to unlock)
- Level requirements (must be level X)
- Time-based unlocks (available after X days)
- Group unlocks (unlock all courses in category)
- Purchase with points (spend points to unlock early)

## Color Palette
- Locked Border: #A62639 (Brown Red) / #DC3545 (Red)
- Locked Badge: #DC3545 (Red) in light mode, #A62639 in dark mode
- Disabled Button: #6B7280 (Gray)
- Normal Border: #456990 (Baltic Blue)

## Notes
- Current user ID is hardcoded to 1 for testing
- In production, get user ID from session/authentication
- Points are checked in real-time when loading catalog
- Courses automatically unlock when user earns enough points
- No notification system yet for unlocks (future enhancement)
