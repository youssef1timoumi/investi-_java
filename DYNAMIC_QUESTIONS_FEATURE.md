# Dynamic Question Options Feature

## Overview
Redesigned the question management system to support dynamic number of answer options. Admins can now add as many options as needed using a "+" button, with a minimum of 2 options required.

## Key Changes

### 1. Database Schema - Two Tables Approach

**questions table** - Stores question text only
```sql
CREATE TABLE IF NOT EXISTS questions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    quiz_id BIGINT NOT NULL,
    question_text TEXT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (quiz_id) REFERENCES quizzes(id) ON DELETE CASCADE,
    INDEX idx_questions_quiz (quiz_id)
);
```

**question_options table** - Stores dynamic options
```sql
CREATE TABLE IF NOT EXISTS question_options (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    question_id BIGINT NOT NULL,
    option_text VARCHAR(500) NOT NULL,
    is_correct BOOLEAN DEFAULT FALSE,
    option_order INT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (question_id) REFERENCES questions(id) ON DELETE CASCADE,
    INDEX idx_options_question (question_id)
);
```

### 2. New Entity: QuestionOption

**File:** `src/main/java/edu/connections3a8/entities/QuestionOption.java`

**Fields:**
- `id` - Unique identifier
- `questionId` - Foreign key to question
- `optionText` - The answer option text
- `isCorrect` - Boolean flag for correct answer
- `optionOrder` - Display order (1, 2, 3, ...)
- `createdAt` - Creation timestamp

### 3. Updated Question Entity

**File:** `src/main/java/edu/connections3a8/entities/Question.java`

**Changes:**
- Removed fixed option fields (option1-4, correctAnswer)
- Added `List<QuestionOption> options` field
- Added `addOption(QuestionOption option)` method
- Simplified constructor to only require quizId and questionText

### 4. Updated Service Methods

**File:** `src/main/java/edu/connections3a8/services/GamificationService.java`

**New/Updated Methods:**
- `addQuestion(Question question)` - Returns generated question ID
- `addQuestionOption(QuestionOption option)` - Add single option
- `deleteQuestionOption(long id)` - Delete single option
- `getQuestionOptions(long questionId)` - Get all options for a question
- `mapResultSetToQuestionOption(ResultSet rs)` - Map options from database

**Key Features:**
- Questions and options saved separately
- CASCADE delete ensures options are removed with question
- Options loaded automatically when fetching questions

### 5. Dynamic UI with "+" Button

**File:** `src/main/java/edu/connections3a8/controllers/QuizController.java`

**UI Features:**

**Option Row Components:**
- Checkbox to mark as correct answer
- TextField for option text
- Remove button (−) to delete option

**Add Option Button:**
- "+" button styled in Faded Copper (#9B7E46)
- Adds new option row dynamically
- Updates option numbers automatically

**Validation Rules:**
- Minimum 2 options required
- Remove button disabled when only 2 options remain
- At least one option must be marked as correct
- All option fields must be filled

**Visual Design:**
- Options container with Lavender Mist background
- Checkboxes for marking correct answers
- Remove buttons in Brown Red (#A62639)
- Tooltips for better UX

## User Workflow

### Adding Questions with Dynamic Options

1. Click "Manage Questions" or "Questions" button
2. Enter question text
3. Fill in the 2 default options
4. Check the correct answer(s)
5. Click "+ Add Option" to add more options
6. Fill in additional options and mark correct ones
7. Click "Add Question" to save

### Managing Options

- **Add Option:** Click "+ Add Option" button
- **Remove Option:** Click "−" button next to option (minimum 2 required)
- **Mark Correct:** Check the checkbox next to correct option(s)
- **Reorder:** Options automatically numbered 1, 2, 3, ...

### Viewing Questions

- Questions display with all their options
- Correct answers highlighted in green
- Dynamic number of options shown
- Clean, organized layout

## Technical Implementation

### Database Relationships

```
quizzes (1) ----< (N) questions (1) ----< (N) question_options
```

- One quiz has many questions
- One question has many options
- CASCADE delete maintains referential integrity

### Option Management

**Adding Options:**
1. Create question, get generated ID
2. Loop through option rows
3. Create QuestionOption for each
4. Set questionId, text, isCorrect, order
5. Save to database

**Loading Options:**
1. Fetch question from database
2. Query options by questionId
3. Order by option_order
4. Attach to question object

### UI State Management

**Dynamic Row Creation:**
- Runnable function creates option rows
- List tracks all option rows
- Remove button updates list and UI
- Option numbers recalculated on changes

**Validation:**
- Check all fields filled
- Verify at least one correct answer
- Ensure minimum 2 options
- Show appropriate error messages

## Benefits

1. **Flexibility** - Support any number of options (2+)
2. **User Control** - Admin decides how many options needed
3. **Better UX** - Visual feedback with checkboxes
4. **Data Integrity** - Proper database normalization
5. **Scalability** - Easy to add features like option images
6. **Clean Code** - Separation of concerns (questions vs options)

## Migration from Old System

If you have existing questions with fixed 4 options:

```sql
-- Migrate old questions to new structure
INSERT INTO questions (quiz_id, question_text, created_at, updated_at)
SELECT quiz_id, question_text, created_at, updated_at
FROM old_questions;

-- Migrate options
INSERT INTO question_options (question_id, option_text, is_correct, option_order)
SELECT id, option1, (correct_answer = 1), 1 FROM old_questions
UNION ALL
SELECT id, option2, (correct_answer = 2), 2 FROM old_questions
UNION ALL
SELECT id, option3, (correct_answer = 3), 3 FROM old_questions
UNION ALL
SELECT id, option4, (correct_answer = 4), 4 FROM old_questions;
```

## Future Enhancements

Potential improvements:
- Drag-and-drop to reorder options
- Rich text editor for options
- Image support for options
- Multiple correct answers (checkbox mode)
- Option explanations/feedback
- Import options from CSV
- Option templates/presets
- Randomize option order for quiz takers
