# Quiz-Course Linking Feature

## Overview
Added functionality to link quizzes to courses when creating or editing quizzes. This allows quizzes to be associated with specific courses and appear in the Course Catalog.

## Changes Made

### 1. QuizForm.fxml
**Added:**
- New ComboBox field "Related Course" between Category and Time Limit
- Allows selecting a course to link the quiz to
- Shows "None (No course)" option for standalone quizzes

### 2. QuizController.java

**New Fields:**
- `courseCombo` - ComboBox for course selection
- `courseService` - Service to access course data
- `courseNameToIdMap` - Maps display names to course IDs

**New Methods:**
- `loadCourses()` - Loads all courses into the combo box
- `updateCourseLink(quizId)` - Links/unlinks quiz to selected course
- `getCoursesForQuiz(quizId)` - Gets all courses linked to a quiz

**Updated Methods:**
- `initialize()` - Now loads courses into combo box
- `handleAddQuiz()` - Links quiz to course after creation/update
- `handleEditQuiz()` - Shows currently linked course when editing
- `handleClearForm()` - Clears course selection

### 3. Database Integration

Uses existing `course_quizzes` table:
```sql
CREATE TABLE course_quizzes (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    course_id BIGINT NOT NULL,
    quiz_id BIGINT NOT NULL,
    quiz_order INT DEFAULT 1,
    is_required BOOLEAN DEFAULT TRUE,
    FOREIGN KEY (course_id) REFERENCES course(id) ON DELETE CASCADE,
    FOREIGN KEY (quiz_id) REFERENCES quizzes(id) ON DELETE CASCADE,
    UNIQUE KEY unique_course_quiz (course_id, quiz_id)
);
```

## User Workflow

### Creating a New Quiz with Course Link

1. Open Quiz Management
2. Fill in quiz details (title, description, etc.)
3. Select a course from "Related Course" dropdown
   - Shows format: "Course Title (category)"
   - Or select "None (No course)" for standalone quiz
4. Click "Add Quiz"
5. Quiz is created and automatically linked to selected course

### Editing Quiz Course Link

1. Click "Edit" on an existing quiz
2. Form loads with current course selection shown
3. Change course selection if needed
4. Click "Add Quiz" (updates quiz)
5. Course link is updated automatically

### Unlinking Quiz from Course

1. Edit the quiz
2. Select "None (No course)" from dropdown
3. Click "Add Quiz"
4. Quiz is unlinked from all courses

## Features

✅ **Course Selection** - Dropdown shows all available courses
✅ **Display Format** - Shows "Title (category)" for easy identification
✅ **Optional Linking** - Can create quizzes without course link
✅ **Auto-Update** - Links update automatically on save
✅ **Edit Support** - Shows current link when editing
✅ **Multiple Quizzes** - One course can have multiple quizzes
✅ **Quiz Ordering** - Quizzes automatically ordered within course
✅ **Cascade Delete** - Quiz-course links deleted if quiz or course deleted

## Integration with Course Catalog

When users browse the Course Catalog:
1. Click on a course
2. See "Related Quizzes" section
3. All linked quizzes appear with:
   - Quiz title
   - Question count
   - Points reward
   - "Take Quiz" button

## Technical Details

### Course Loading
- Loads all courses on form initialization
- Creates display name: "Title (category)"
- Maps display names to course IDs for lookup

### Link Management
- Removes existing links before adding new one
- Prevents duplicate links (enforced by database)
- Sets quiz order based on existing quiz count
- Marks all quizzes as required by default

### Data Flow
```
Quiz Form → Select Course → Save Quiz → 
Update course_quizzes table → 
Course Catalog displays linked quizzes
```

## Benefits

1. **Organization** - Quizzes organized by course
2. **Discovery** - Users find quizzes through courses
3. **Context** - Quizzes have clear learning context
4. **Flexibility** - Can link/unlink anytime
5. **User Experience** - Seamless integration with catalog

## Future Enhancements

Potential improvements:
- Set quiz as optional vs required
- Reorder quizzes within a course
- Link one quiz to multiple courses
- Show quiz prerequisites
- Track quiz completion per course
- Course progress tracking
- Quiz recommendations based on course

## Example Usage

**Creating Programming Quiz:**
1. Title: "Java Basics Test"
2. Category: "programming"
3. Related Course: "Introduction to Java (programming)"
4. Save → Quiz linked to Java course

**Result in Course Catalog:**
- User views "Introduction to Java" course
- Sees "Java Basics Test" in Related Quizzes
- Can take quiz directly from course page

## Notes

- Course selection is optional
- One quiz can only be linked to one course at a time
- Changing course link removes previous link
- Quiz order is automatic (based on creation order)
- All linked quizzes marked as required by default
- Database constraints prevent orphaned links
