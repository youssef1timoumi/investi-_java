# Testing Checklist - Quiz-Course Linking Feature

## 📋 Pre-Testing Setup

### ✅ Step 1: Execute SQL
- [ ] Open MySQL client or command line
- [ ] Execute: `mysql -u your_username -p 3a8 < database.sql`
- [ ] Verify no errors in output
- [ ] Confirm tables created:
  ```sql
  SHOW TABLES LIKE 'course_%';
  SHOW TABLES LIKE 'question%';
  ```
  Expected output:
  - course_interactions
  - course_quizzes
  - questions
  - question_options

### ✅ Step 2: Start Application
- [ ] Run the JavaFX application
- [ ] Verify Main Menu loads without errors
- [ ] Check console for any SQL errors

## 🧪 Test Suite 1: Course Management

### Test 1.1: Create a Course
- [ ] Click "Manage Courses" from Main Menu
- [ ] Fill in course details:
  - Title: "Introduction to Java"
  - Category: "programming"
  - Difficulty: "beginner"
  - Points: 100
  - Duration: 120
- [ ] Click "Add Course"
- [ ] ✅ Verify success message appears
- [ ] ✅ Verify course appears in course list

### Test 1.2: Create Multiple Courses
- [ ] Create "Advanced SQL" (database, advanced, 200 points)
- [ ] Create "Web Development" (web, intermediate, 150 points)
- [ ] ✅ Verify all courses appear in list

### Test 1.3: Edit a Course
- [ ] Click "Edit" on "Introduction to Java"
- [ ] Change points to 120
- [ ] Click "Add Course" (updates)
- [ ] ✅ Verify changes saved

### Test 1.4: Course Statistics
- [ ] Click "Statistics" button
- [ ] Select "Total Courses"
- [ ] ✅ Verify count is correct (3)
- [ ] Select "Courses by Difficulty"
- [ ] ✅ Verify distribution shows correctly

## 🧪 Test Suite 2: Quiz Management

### Test 2.1: Create Quiz WITHOUT Course Link
- [ ] Click "Manage Quizzes" from Main Menu
- [ ] Fill in quiz details:
  - Title: "General Knowledge Quiz"
  - Category: "general"
  - Points: 25
  - Difficulty: "beginner"
- [ ] Leave "Related Course" as "None (No course)"
- [ ] Click "Add Quiz"
- [ ] ✅ Verify quiz created successfully
- [ ] ✅ Verify quiz appears in list

### Test 2.2: Create Quiz WITH Course Link
- [ ] Fill in quiz details:
  - Title: "Java Basics Test"
  - Category: "programming"
  - Points: 50
  - Difficulty: "beginner"
- [ ] Select "Related Course": "Introduction to Java (programming)"
- [ ] Click "Add Quiz"
- [ ] ✅ Verify quiz created successfully
- [ ] ✅ Verify quiz appears in list

### Test 2.3: Create More Linked Quizzes
- [ ] Create "Java OOP Quiz" linked to "Introduction to Java"
- [ ] Create "SQL Basics Quiz" linked to "Advanced SQL"
- [ ] ✅ Verify all quizzes created and linked

### Test 2.4: Edit Quiz Course Link
- [ ] Click "Edit" on "Java Basics Test"
- [ ] ✅ Verify "Related Course" shows "Introduction to Java (programming)"
- [ ] Change to "None (No course)"
- [ ] Click "Add Quiz"
- [ ] ✅ Verify quiz updated
- [ ] Edit again and link back to "Introduction to Java"
- [ ] ✅ Verify link restored

### Test 2.5: Quiz Statistics
- [ ] Click "Statistics" button
- [ ] Select "Total Quizzes"
- [ ] ✅ Verify count is correct
- [ ] Select "Quizzes by Difficulty"
- [ ] ✅ Verify distribution correct
- [ ] Select "Average Points Reward"
- [ ] ✅ Verify calculation correct

## 🧪 Test Suite 3: Question Management

### Test 3.1: Add Questions to Quiz
- [ ] Edit "Java Basics Test"
- [ ] Click "Manage Questions" button
- [ ] ✅ Verify dialog opens with title "Manage Questions - Java Basics Test"

### Test 3.2: Create Question with 2 Options
- [ ] Enter question: "What is Java?"
- [ ] Option 1: "A programming language" (check as correct)
- [ ] Option 2: "A coffee brand"
- [ ] Click "Add Question"
- [ ] ✅ Verify success message
- [ ] ✅ Verify question appears in list below
- [ ] ✅ Verify correct answer is highlighted in green

### Test 3.3: Create Question with 4+ Options
- [ ] Enter question: "Which are valid Java keywords?"
- [ ] Option 1: "class" (check as correct)
- [ ] Option 2: "interface" (check as correct)
- [ ] Click "+" to add more options
- [ ] Option 3: "function"
- [ ] Click "+" again
- [ ] Option 4: "method"
- [ ] Click "Add Question"
- [ ] ✅ Verify question saved with 4 options
- [ ] ✅ Verify multiple correct answers shown in green

### Test 3.4: Add Multiple Questions
- [ ] Add 3 more questions with various option counts
- [ ] ✅ Verify all questions appear in list
- [ ] ✅ Verify question numbering (Q1, Q2, Q3, etc.)

### Test 3.5: Delete a Question
- [ ] Click "Delete" on any question
- [ ] ✅ Verify confirmation dialog appears
- [ ] Click "OK"
- [ ] ✅ Verify question removed from list
- [ ] ✅ Verify question numbers updated

### Test 3.6: Validation Tests
- [ ] Try to add question with empty text
- [ ] ✅ Verify error: "Please enter question text!"
- [ ] Enter question but leave options empty
- [ ] ✅ Verify error: "Please fill in all options!"
- [ ] Fill options but don't mark any as correct
- [ ] ✅ Verify error: "Please mark at least one option as correct!"

### Test 3.7: Dynamic Question Count
- [ ] Close question dialog
- [ ] ✅ Verify quiz list shows updated question count
- [ ] Edit quiz again
- [ ] ✅ Verify status label shows question count

## 🧪 Test Suite 4: Course Catalog (User View)

### Test 4.1: Access Course Catalog
- [ ] Go to Main Menu
- [ ] Click "Browse Course Catalog" (green button)
- [ ] ✅ Verify catalog page loads
- [ ] ✅ Verify all courses displayed in grid

### Test 4.2: Search Functionality
- [ ] Enter "Java" in search box
- [ ] ✅ Verify only Java-related courses shown
- [ ] Clear search
- [ ] ✅ Verify all courses shown again

### Test 4.3: Filter by Category
- [ ] Select "programming" from category filter
- [ ] ✅ Verify only programming courses shown
- [ ] Select "All Categories"
- [ ] ✅ Verify all courses shown

### Test 4.4: Filter by Difficulty
- [ ] Select "beginner" from difficulty filter
- [ ] ✅ Verify only beginner courses shown
- [ ] Select "All Difficulties"
- [ ] ✅ Verify all courses shown

### Test 4.5: Combined Filters
- [ ] Search "Java" + Category "programming" + Difficulty "beginner"
- [ ] ✅ Verify correct courses shown
- [ ] Click "Clear Filters"
- [ ] ✅ Verify all courses shown

### Test 4.6: Like a Course
- [ ] Click 👍 on "Introduction to Java"
- [ ] ✅ Verify like count increases
- [ ] ✅ Verify button highlights (green background)
- [ ] Click 👍 again
- [ ] ✅ Verify like removed (count decreases)

### Test 4.7: Dislike a Course
- [ ] Click 👎 on "Advanced SQL"
- [ ] ✅ Verify dislike count increases
- [ ] ✅ Verify button highlights (red background)
- [ ] Click 👎 again
- [ ] ✅ Verify dislike removed

### Test 4.8: Like/Dislike Toggle
- [ ] Like a course
- [ ] Then dislike the same course
- [ ] ✅ Verify like removed and dislike added
- [ ] ✅ Verify counts updated correctly

### Test 4.9: Report a Course
- [ ] Click 🚩 on any course
- [ ] ✅ Verify report dialog appears
- [ ] Enter reason: "Outdated content"
- [ ] Click "OK"
- [ ] ✅ Verify success message
- [ ] ✅ Verify "Thank you for your feedback" dialog

## 🧪 Test Suite 5: Course Details & Linked Quizzes

### Test 5.1: View Course Details
- [ ] Click "View Course Details" on "Introduction to Java"
- [ ] ✅ Verify dialog opens with course title
- [ ] ✅ Verify course description shown
- [ ] ✅ Verify course metadata (category, difficulty, duration, points, language, status)

### Test 5.2: View Linked Quizzes
- [ ] Scroll to "Related Quizzes" section
- [ ] ✅ Verify "Java Basics Test" appears
- [ ] ✅ Verify "Java OOP Quiz" appears
- [ ] ✅ Verify quiz details shown (question count, points)
- [ ] ✅ Verify "Take Quiz" button present

### Test 5.3: Course with No Quizzes
- [ ] View details of "Web Development" (no linked quizzes)
- [ ] ✅ Verify "No quizzes available for this course yet." message

### Test 5.4: Take Quiz Button
- [ ] Click "Take Quiz" on any quiz
- [ ] ✅ Verify placeholder dialog appears
- [ ] ✅ Verify message: "Quiz-taking interface coming soon!"

### Test 5.5: Multiple Quizzes Display
- [ ] View course with 2+ linked quizzes
- [ ] ✅ Verify all quizzes listed
- [ ] ✅ Verify each has its own "Take Quiz" button
- [ ] ✅ Verify quizzes ordered correctly

## 🧪 Test Suite 6: Integration Tests

### Test 6.1: End-to-End Flow
- [ ] Create new course "Python Basics"
- [ ] Create new quiz "Python Quiz" linked to "Python Basics"
- [ ] Add 5 questions to the quiz
- [ ] Go to Course Catalog
- [ ] Search for "Python"
- [ ] View course details
- [ ] ✅ Verify quiz appears with 5 questions
- [ ] Like the course
- [ ] ✅ Verify like count = 1

### Test 6.2: Update Flow
- [ ] Edit "Python Quiz"
- [ ] Change linked course to "None"
- [ ] Save
- [ ] Go to Course Catalog
- [ ] View "Python Basics" details
- [ ] ✅ Verify quiz no longer appears
- [ ] Go back to Quiz Management
- [ ] Edit "Python Quiz" again
- [ ] Link back to "Python Basics"
- [ ] ✅ Verify quiz reappears in catalog

### Test 6.3: Delete Flow
- [ ] Delete a quiz that's linked to a course
- [ ] ✅ Verify quiz removed from quiz list
- [ ] Go to Course Catalog
- [ ] View the course details
- [ ] ✅ Verify quiz no longer appears in related quizzes

### Test 6.4: Question Count Updates
- [ ] Edit a quiz
- [ ] Note current question count
- [ ] Add 2 more questions
- [ ] Close dialog
- [ ] ✅ Verify quiz list shows updated count
- [ ] Go to Course Catalog
- [ ] View course details
- [ ] ✅ Verify quiz shows updated question count

## 🧪 Test Suite 7: UI/UX Tests

### Test 7.1: Theme Toggle
- [ ] Go to Quiz Management
- [ ] Click "🌙 Dark" button
- [ ] ✅ Verify dark mode applied
- [ ] Click "☀️ Light" button
- [ ] ✅ Verify light mode applied

### Test 7.2: Auto Mode
- [ ] Click "⏰ Auto" button
- [ ] ✅ Verify button shows "⏰ Auto ✓"
- [ ] ✅ Verify theme matches time of day (dark if 6PM-6AM)

### Test 7.3: Responsive Layout
- [ ] Resize window to different sizes
- [ ] ✅ Verify course grid adjusts (3 columns)
- [ ] ✅ Verify forms remain readable
- [ ] ✅ Verify scrolling works properly

### Test 7.4: Status Messages
- [ ] Perform various actions (add, edit, delete)
- [ ] ✅ Verify success messages appear in green
- [ ] ✅ Verify error messages appear in red
- [ ] ✅ Verify messages are clear and helpful

### Test 7.5: Animations
- [ ] Watch for smooth transitions
- [ ] ✅ Verify buttons have hover effects
- [ ] ✅ Verify dialogs open smoothly
- [ ] ✅ Verify lists update without flicker

## 🧪 Test Suite 8: Error Handling

### Test 8.1: Database Connection
- [ ] Stop MySQL server
- [ ] Try to load courses
- [ ] ✅ Verify error message displayed
- [ ] Start MySQL server
- [ ] Refresh
- [ ] ✅ Verify data loads correctly

### Test 8.2: Invalid Data
- [ ] Try to create quiz with negative points
- [ ] ✅ Verify validation error
- [ ] Try to create quiz with passing score > 100
- [ ] ✅ Verify validation error

### Test 8.3: Empty States
- [ ] View course with no quizzes
- [ ] ✅ Verify friendly message shown
- [ ] View quiz with no questions
- [ ] ✅ Verify "Add your first question!" message

## 📊 Final Verification

### Database Integrity
- [ ] Check course_quizzes table:
  ```sql
  SELECT * FROM course_quizzes;
  ```
- [ ] ✅ Verify links exist for created quizzes
- [ ] Check questions table:
  ```sql
  SELECT COUNT(*) FROM questions;
  ```
- [ ] ✅ Verify question count matches UI
- [ ] Check question_options table:
  ```sql
  SELECT * FROM question_options WHERE question_id = 1;
  ```
- [ ] ✅ Verify options saved correctly

### Feature Completeness
- [ ] ✅ Course CRUD operations work
- [ ] ✅ Quiz CRUD operations work
- [ ] ✅ Quiz-course linking works
- [ ] ✅ Question management works
- [ ] ✅ Course catalog displays correctly
- [ ] ✅ Course interactions work (like/dislike/report)
- [ ] ✅ Statistics display correctly
- [ ] ✅ Search and filters work
- [ ] ✅ Theme toggle works

### Performance
- [ ] ✅ Pages load quickly (< 2 seconds)
- [ ] ✅ Search is responsive (< 500ms)
- [ ] ✅ No lag when scrolling lists
- [ ] ✅ Dialogs open instantly

## ✅ Sign-Off

### All Tests Passed?
- [ ] All test suites completed
- [ ] No critical errors found
- [ ] All features working as expected
- [ ] UI is responsive and smooth
- [ ] Database integrity verified

### Ready for Production?
- [ ] SQL executed successfully
- [ ] All features tested
- [ ] Documentation reviewed
- [ ] User workflows verified

## 🎉 Congratulations!

If all tests pass, your Quiz-Course Linking feature is fully functional and ready to use!

### Next Steps:
1. Train users on new features
2. Monitor for any issues
3. Gather user feedback
4. Plan future enhancements

### Support:
- Check documentation files for detailed information
- Review troubleshooting section in README
- Test edge cases as they arise
