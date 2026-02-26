# Question Management Feature

## Overview
Added comprehensive question management functionality to the Quiz Management system, allowing users to create, view, and delete multiple-choice questions for each quiz.

## New Components

### 1. Question Entity
**File:** `src/main/java/edu/connections3a8/entities/Question.java`

**Fields:**
- `id` (long) - Unique identifier
- `quizId` (long) - Foreign key to quiz
- `questionText` (String) - The question text
- `option1` (String) - First answer option
- `option2` (String) - Second answer option
- `option3` (String) - Third answer option
- `option4` (String) - Fourth answer option
- `correctAnswer` (int) - Correct answer number (1-4)
- `createdAt` (Timestamp) - Creation timestamp
- `updatedAt` (Timestamp) - Last update timestamp

**Methods:**
- Constructors (default and parameterized)
- Getters and setters for all fields
- `equals()`, `hashCode()`, `toString()` methods

### 2. Database Schema
**File:** `database.sql`

**New Table:** `questions`
```sql
CREATE TABLE IF NOT EXISTS questions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    quiz_id BIGINT NOT NULL,
    question_text TEXT NOT NULL,
    option1 VARCHAR(500) NOT NULL,
    option2 VARCHAR(500) NOT NULL,
    option3 VARCHAR(500) NOT NULL,
    option4 VARCHAR(500) NOT NULL,
    correct_answer INT NOT NULL CHECK (correct_answer BETWEEN 1 AND 4),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (quiz_id) REFERENCES quizzes(id) ON DELETE CASCADE,
    INDEX idx_questions_quiz (quiz_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4
```

**Features:**
- Foreign key constraint to quizzes table with CASCADE delete
- Check constraint ensuring correct_answer is between 1-4
- Index on quiz_id for faster queries
- Automatic timestamps

### 3. Service Layer Methods
**File:** `src/main/java/edu/connections3a8/services/GamificationService.java`

**New Methods:**
- `addQuestion(Question question)` - Create new question
- `updateQuestion(Question question, long id)` - Update existing question
- `deleteQuestion(long id)` - Delete question
- `getQuestionById(long id)` - Retrieve single question
- `getQuestionsByQuizId(long quizId)` - Get all questions for a quiz
- `getQuestionCountByQuizId(long quizId)` - Count questions for a quiz
- `mapResultSetToQuestion(ResultSet rs)` - Map database results to Question entity

### 4. UI Components
**File:** `src/main/resources/QuizForm.fxml`

**Changes:**
- Added "Manage Questions" button (disabled by default)
- Button enables when a quiz is selected for editing

**File:** `src/main/java/edu/connections3a8/controllers/QuizController.java`

**New Features:**
- `manageQuestionsBtn` - FXML button reference
- `handleManageQuestions()` - Opens question management dialog
- `openQuestionManagementDialog(Quiz quiz)` - Creates and displays dialog
- `loadQuestionsIntoDialog(long quizId, VBox container)` - Loads questions into UI
- Added "Questions" button to each quiz card in the list

### 5. Question Management Dialog

**Layout:**
- Title showing quiz name
- Form section with fields:
  - Question text (TextArea)
  - Option 1-4 (TextField)
  - Correct answer (ComboBox)
- Action buttons:
  - "Add Question" - Saves new question
  - "Clear" - Resets form
- Questions list section:
  - Displays all existing questions
  - Shows question number, text, and all options
  - Highlights correct answer in green
  - Delete button for each question

**Styling:**
- Consistent with application color palette
- White form container with Baltic Blue borders
- Lavender Mist background
- Brown Red delete buttons
- Faded Copper "Questions" buttons in quiz cards

### 6. Test Coverage
**File:** `src/test/java/edu/connections3a8/entities/QuestionTest.java`

**Tests:**
- Default constructor
- Parameterized constructor
- All getters and setters
- Equals and hashCode methods
- ToString method
- Correct answer validation
- Complete question setup

**File:** `src/test/java/edu/connections3a8/AllTestsSuite.java`
- Added QuestionTest to test suite

## User Workflow

### Creating Questions
1. Create or edit a quiz in Quiz Management
2. Click "Manage Questions" button (or "Questions" button on quiz card)
3. Fill in question text and all four options
4. Select the correct answer from dropdown
5. Click "Add Question"
6. Question appears in the list below

### Viewing Questions
1. Select a quiz from the list
2. Click "Questions" button on the quiz card
3. All questions display with:
   - Question number and text
   - All four options
   - Correct answer highlighted in green

### Deleting Questions
1. Open question management dialog
2. Find the question to delete
3. Click "Delete" button
4. Confirm deletion in dialog
5. Question removed from list

## Validation

### Form Validation
- All fields required (question text and all 4 options)
- Correct answer must be selected
- Shows error alert if validation fails

### Database Validation
- Correct answer must be between 1-4 (CHECK constraint)
- Quiz must exist (FOREIGN KEY constraint)
- Questions cascade delete when quiz is deleted

## Technical Details

### Database Operations
- Uses PreparedStatement for SQL injection prevention
- Proper exception handling with SQLException
- Automatic timestamp management
- Cascade delete ensures data integrity

### UI Implementation
- JavaFX Stage for modal dialog
- GridPane for form layout
- ScrollPane for questions list
- VBox containers for question cards
- Proper styling with inline CSS

### Integration
- Seamlessly integrated with existing Quiz Management
- Consistent with application design patterns
- Follows same styling as Badges and Courses pages
- Uses same color palette throughout

## Benefits

1. **Complete Quiz Management** - Users can now fully manage quiz content
2. **User-Friendly Interface** - Intuitive dialog-based question management
3. **Data Integrity** - Foreign key constraints and validation
4. **Visual Feedback** - Correct answers highlighted, clear question numbering
5. **Flexible Access** - Manage questions from form or quiz list
6. **Consistent Design** - Matches application styling and patterns

## Future Enhancements

Potential improvements:
- Edit existing questions (currently add/delete only)
- Drag-and-drop question reordering
- Question categories/tags
- Image support for questions
- Multiple correct answers support
- Question difficulty levels
- Question bank/library for reuse
- Import/export questions (CSV, JSON)
- Rich text formatting for questions
- Question preview mode
