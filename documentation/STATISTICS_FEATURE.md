# Statistics Feature

## Overview
Added interactive statistics functionality to Quiz, Course, and Badge management pages. Users can select different statistical views from a dropdown menu to analyze their data dynamically.

## Implementation

### 1. UI Changes

**Added Statistics Button to:**
- QuizForm.fxml
- CourseForm.fxml  
- BadgeForm.fxml

**Button Styling:**
- Faded Copper background (#9B7E46)
- Icon: 📊
- Width: 130-140px
- Positioned between form actions and view/back buttons

### 2. Statistics Dialog

**Features:**
- Modal dialog window (650x600px)
- Dropdown selector for different statistics
- Scrollable results container
- Visual progress bars for distribution stats
- Clean, modern design matching app theme

**Design Elements:**
- Lavender Mist background (#F7F0F5)
- White results container with Baltic Blue border
- Large, bold numbers for key metrics
- Progress bars with Baltic Blue accent
- Percentage indicators

### 3. Quiz Statistics

**Available Statistics:**

1. **Total Quizzes**
   - Large number display
   - Total count of all quizzes

2. **Quizzes by Difficulty**
   - Breakdown by: beginner, intermediate, advanced, expert
   - Count, progress bar, and percentage for each

3. **Quizzes by Category**
   - Groups quizzes by category
   - Shows distribution across categories

4. **Quizzes by Status**
   - Active, inactive, draft status breakdown
   - Visual representation of quiz states

5. **Average Points Reward**
   - Calculated average across all quizzes
   - Displayed as decimal number

6. **Total Questions**
   - Sum of all questions across all quizzes
   - Dynamically calculated from database

7. **Average Questions per Quiz**
   - Mean number of questions
   - Helps assess quiz complexity

8. **Quizzes by Points Range**
   - 0-50 points
   - 51-100 points
   - 101-200 points
   - 201+ points
   - Distribution visualization

### 4. Visual Components

**Stat Row Format:**
```
[Label]  [Count]  [Progress Bar]  [Percentage]
```

**Progress Bars:**
- Width: 150px
- Color: Baltic Blue (#456990)
- Shows relative proportion
- Smooth, modern appearance

**Large Metrics:**
- Font size: 32px
- Bold weight
- Baltic Blue color
- Descriptive subtitle

## Technical Implementation

### QuizController Methods

**Main Methods:**
- `handleStatistics()` - Opens statistics dialog
- `openStatisticsDialog()` - Creates and displays dialog
- `displayStatistic(String, VBox)` - Routes to specific stat display

**Stat Display Methods:**
- `displayTotalQuizzes()` - Shows total count
- `displayQuizzesByDifficulty()` - Groups by difficulty
- `displayQuizzesByCategory()` - Groups by category
- `displayQuizzesByStatus()` - Groups by status
- `displayAveragePoints()` - Calculates average points
- `displayTotalQuestions()` - Sums all questions
- `displayAverageQuestions()` - Calculates question average
- `displayQuizzesByPointsRange()` - Groups by point ranges

**Helper Methods:**
- `createStatRow()` - Creates formatted stat row with progress bar

### Data Processing

**Uses Java Streams:**
```java
quizzes.stream()
    .collect(Collectors.groupingBy(Quiz::getDifficultyLevel, Collectors.counting()))
```

**Dynamic Calculations:**
- Real-time question counting from database
- Percentage calculations
- Average computations
- Range filtering

## User Workflow

1. Click "📊 Statistics" button
2. Dialog opens with dropdown selector
3. Select desired statistic from list
4. View results with visual representations
5. Switch between different statistics
6. Close dialog when done

## Benefits

✅ **Data Insights** - Quick overview of quiz distribution  
✅ **Visual Feedback** - Progress bars and percentages  
✅ **Dynamic Selection** - Choose what to analyze  
✅ **Real-time Data** - Always current from database  
✅ **User-Friendly** - Clean, intuitive interface  
✅ **Extensible** - Easy to add new statistics  

## Future Enhancements for Courses & Badges

### Course Statistics (To Implement)
- Total Courses
- Courses by Difficulty
- Courses by Category
- Courses by Status (published, draft, archived)
- Average Reward Points
- Average Duration
- Courses by Language
- Content Type Distribution

### Badge Statistics (To Implement)
- Total Badges
- Badges by Points Required Range
- Most/Least Earned Badges
- Average Points Required
- Badge Distribution
- Badges Earned by Users

## Styling

**Colors:**
- Background: #F7F0F5 (Lavender Mist)
- Primary: #456990 (Baltic Blue)
- Accent: #9B7E46 (Faded Copper)
- Text: #000501 (Black)
- Secondary Text: #6B7280 (Gray)

**Typography:**
- Title: 22px, bold
- Stat Numbers: 32px, bold
- Labels: 14px, semi-bold
- Descriptions: 13px, regular
- Percentages: 12px, regular

## Code Structure

```
QuizController
├── handleStatistics()
├── openStatisticsDialog()
├── displayStatistic()
├── displayTotalQuizzes()
├── displayQuizzesByDifficulty()
├── displayQuizzesByCategory()
├── displayQuizzesByStatus()
├── displayAveragePoints()
├── displayTotalQuestions()
├── displayAverageQuestions()
├── displayQuizzesByPointsRange()
└── createStatRow()
```

## Notes

- Statistics are calculated on-demand (not cached)
- All data comes from live database queries
- Progress bars automatically scale to data
- Empty states handled gracefully
- Error messages displayed if database issues occur
- Dialog is modal (blocks interaction with main window)
- Can be extended to export statistics as CSV/PDF
