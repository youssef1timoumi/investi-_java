# Design Update Summary

## Applied to Badges Page ✓
- Modern CSS with dark mode support
- Search functionality (by name and points)
- Sort functionality (Name A-Z, Z-A, Points Low-High, High-Low)
- Theme toggle button (Light/Dark)
- Auto mode button (switches based on time: 6PM-6AM = dark, 6AM-6PM = light)
- Scrollable page with taller badge list (700px)

## To Apply to Courses and Quizzes Pages

### CSS Files Created:
- `src/main/resources/coursesForm.css` ✓
- `src/main/resources/quizzesForm.css` (copy of coursesForm.css)

### Changes Needed for CourseForm.FXML:
1. Wrap in ScrollPane for scrollability
2. Add theme toggle buttons in header
3. Add search field and sort ComboBox
4. Apply CSS classes to all elements
5. Increase list container height to 700px
6. Link to coursesForm.css

### Changes Needed for QuizForm.FXML:
1. Same as CourseForm.FXML
2. Link to quizzesForm.css

### Changes Needed for CourseController.java:
1. Add search and sort fields
2. Add theme toggle buttons
3. Add allCourses list cache
4. Implement applyFiltersAndSort() method
5. Implement handleThemeToggle() method
6. Implement handleAutoMode() method
7. Implement handleClearSearch() method
8. Add search listener (real-time filtering)
9. Add sort listener
10. Update createCourseItem() to use CSS classes
11. Add theme state variables (isDarkMode, isAutoMode, rootPane)

### Changes Needed for QuizController.java:
1. Same as CourseController.java but for quizzes

### Search Functionality:
- Courses: Search by title, category, or difficulty
- Quizzes: Search by title, category, difficulty, or points

### Sort Options:
- Courses: Title (A-Z), Title (Z-A), Points (Low-High), Points (High-Low), Duration (Short-Long), Duration (Long-Short)
- Quizzes: Title (A-Z), Title (Z-A), Points (Low-High), Points (High-Low), Questions (Few-Many), Questions (Many-Few)
