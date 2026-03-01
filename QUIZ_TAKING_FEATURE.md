# Quiz Taking Feature

## Overview
Interactive quiz-taking interface with question-by-question navigation, progress tracking, and automatic timer-based submission.

## Features

### 1. Quiz Interface
- **Question Display**: Shows one question at a time with multiple-choice options
- **Progress Bar**: Visual indicator showing quiz completion progress
- **Timer**: Countdown timer that auto-submits when time expires
- **Score Tracking**: Real-time display of answered questions count

### 2. Navigation
- **Previous Button**: Navigate back to previous questions
- **Next Button**: Move forward to next questions
- **Submit Button**: Appears on the last question to submit the quiz

### 3. Timer Functionality
- Displays remaining time in MM:SS format
- Color changes based on remaining time:
  - Normal: Blue (#456990)
  - Warning (< 5 min): Orange (#FFA500)
  - Critical (< 1 min): Red (#DC3545)
- Auto-submits quiz when timer reaches 0:00

### 4. Answer Selection
- Radio buttons for single-choice answers
- Selected answers are highlighted
- Answers persist when navigating between questions
- Visual feedback on hover and selection

### 5. Quiz Submission
- Confirmation dialog before submission
- Warning if not all questions are answered
- Automatic submission when time expires
- Score calculation and results display

### 6. Results Display
- Shows correct answers count
- Displays percentage score
- Indicates pass/fail status
- Shows points earned (if passed)
- Compares score to passing threshold

### 7. Dark Mode Support
- Full dark mode styling for all elements
- Synchronized with global theme manager
- Toggle button in header

## User Flow

1. **Start Quiz**: Click "Take Quiz" button from course content page
2. **View Question**: Read question and available options
3. **Select Answer**: Click radio button to select an answer
4. **Navigate**: Use Previous/Next buttons to move between questions
5. **Monitor Progress**: Check progress bar and answered count
6. **Watch Timer**: Keep track of remaining time
7. **Submit**: Click Submit button on last question or wait for auto-submit
8. **View Results**: See score, pass/fail status, and points earned
9. **Return**: Go back to course content page

## Technical Details

### Files Created
- `src/main/resources/QuizTakingView.fxml` - Quiz UI layout
- `src/main/java/edu/connections3a8/controllers/QuizTakingController.java` - Quiz logic
- `src/main/resources/quizTaking.css` - Quiz styling

### Files Modified
- `src/main/java/edu/connections3a8/controllers/CourseContentController.java` - Added navigation to quiz page

### Key Components

#### QuizTakingController
- `setQuizAndCourse()` - Initialize quiz with data
- `loadQuizData()` - Load questions and options from database
- `startTimer()` - Initialize and start countdown timer
- `displayQuestion()` - Show current question and options
- `handleNext/Previous()` - Navigate between questions
- `handleSubmit()` - Process quiz submission
- `submitQuiz()` - Calculate score and save results
- `autoSubmitQuiz()` - Handle automatic submission on timeout

#### Data Flow
1. Quiz and Course objects passed from CourseContentController
2. Questions loaded from database via GamificationService
3. User answers stored in HashMap (questionId -> optionId)
4. Score calculated by comparing selected options with correct answers
5. Results saved to database and points awarded if passed

## Styling

### Light Mode
- Background: Lavender Mist (#F7F0F5)
- Cards: White with blue borders
- Primary buttons: Gold gradient
- Text: Dark (#000501)

### Dark Mode
- Background: Dark gradient (#12122A, #0A0A18, #100F22)
- Cards: Dark purple (#161630) with muted borders
- Primary buttons: Gold gradient (maintained)
- Text: Light (#F0F2FA, #E8E8E8)

## Database Integration

### Required Tables
- `quizzes` - Quiz metadata (title, time limit, passing score, points)
- `questions` - Quiz questions
- `question_options` - Answer options with correct flag
- `user_quizzes` - User quiz attempts and scores (TODO: implement)
- `point_transactions` - Points awarded for quiz completion

### Service Methods Used
- `getQuestionsByQuizId()` - Fetch all questions for a quiz
- `getQuestionOptions()` - Fetch options for a question
- `addPoints()` - Award points for passing quiz

## Future Enhancements
- Save quiz attempts to `user_quizzes` table
- Show detailed answer review after submission
- Support for multiple question types (true/false, multiple select)
- Quiz history and retry functionality
- Leaderboard for quiz scores
- Question explanations for wrong answers
- Pause/resume functionality
- Bookmark questions for review

## Testing Checklist
- [ ] Quiz loads with all questions and options
- [ ] Timer counts down correctly
- [ ] Timer auto-submits at 0:00
- [ ] Navigation between questions works
- [ ] Answer selection persists across navigation
- [ ] Progress bar updates correctly
- [ ] Submit confirmation dialog appears
- [ ] Score calculation is accurate
- [ ] Points are awarded correctly
- [ ] Dark mode toggle works
- [ ] Back button returns to course content
- [ ] All styling matches design system

## Color Palette
- Primary: #456990 (Baltic Blue)
- Accent: #9B7E46 (Faded Copper)
- Success: #28A745 (Green)
- Warning: #FFA500 (Orange)
- Danger: #DC3545 (Red)
- Dark BG: #0A0A18, #12122A, #161630
- Light Text: #F0F2FA, #E8E8E8
- Muted Text: #8D96A6
