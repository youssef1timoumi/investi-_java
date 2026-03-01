# Quick Start Guide - Quiz-Course Linking Feature

## 🎯 What's New?

You can now link quizzes to courses! When users browse the Course Catalog, they'll see all related quizzes for each course.

## ⚡ Quick Setup (2 Steps)

### Step 1: Execute SQL (REQUIRED)

Run the SQL file to create the necessary database tables:

```bash
mysql -u your_username -p 3a8 < database.sql
```

Or manually execute in your MySQL client:
- Open `database.sql`
- Execute the entire file (it has `IF NOT EXISTS` checks, so it's safe to run)

The following tables will be created:
- `course_interactions` - Likes, dislikes, reports
- `course_quizzes` - Links quizzes to courses
- `questions` - Dynamic questions for quizzes
- `question_options` - Dynamic options for each question

### Step 2: Test the Feature

1. **Run the application**
2. **Go to Quiz Management**
3. **Create or edit a quiz**
4. **Select a course** from the "Related Course" dropdown
5. **Save the quiz**
6. **Go to Main Menu** → Click "Browse Course Catalog"
7. **Find your course** → Click "View Course Details"
8. **See your quiz** in the "Related Quizzes" section!

## 📋 Usage Examples

### Example 1: Link a Quiz to a Course

```
1. Quiz Management → Fill in quiz details
2. Related Course: "Introduction to Java (programming)"
3. Add Quiz
✅ Quiz is now linked to the Java course
```

### Example 2: Add Questions to a Quiz

```
1. Quiz Management → Edit a quiz
2. Click "Manage Questions"
3. Enter question text
4. Fill in 2+ options
5. Check the correct answer(s)
6. Click "+" to add more options
7. Click "Add Question"
✅ Question added dynamically
```

### Example 3: Browse Courses as a User

```
1. Main Menu → "Browse Course Catalog"
2. Search/filter courses
3. Click "View Course Details" on any course
4. See all linked quizzes
5. Click "Take Quiz" (coming soon)
6. Like/dislike/report courses
✅ Full user experience
```

## 🔍 Where to Find Things

### For Admins:
- **Quiz Management**: Main Menu → "Manage Quizzes"
- **Course Management**: Main Menu → "Manage Courses"
- **Link Quiz to Course**: Quiz Form → "Related Course" dropdown

### For Users:
- **Browse Courses**: Main Menu → "Browse Course Catalog" (green button)
- **View Course Details**: Click on any course card
- **See Quizzes**: In course details dialog → "Related Quizzes" section

## ✅ Verification Checklist

After executing the SQL, verify:

- [ ] Can create a quiz and select a course
- [ ] Quiz appears in Course Catalog under the selected course
- [ ] Can edit quiz and change the linked course
- [ ] Can add questions with multiple options
- [ ] Can like/dislike courses in the catalog
- [ ] Question count updates automatically

## 🐛 Troubleshooting

### "Cannot find course_quizzes table"
→ Execute the SQL file: `database.sql`

### "Course dropdown is empty"
→ Create some courses first in Course Management

### "Manage Questions button is disabled"
→ You need to select/edit a quiz first

### "No quizzes showing in Course Catalog"
→ Make sure you linked the quiz to a course in Quiz Management

## 📚 Related Documentation

- `QUIZ_COURSE_LINKING_FEATURE.md` - Detailed technical documentation
- `COURSE_CATALOG_IMPLEMENTATION_GUIDE.md` - Course catalog details
- `DYNAMIC_QUESTIONS_FEATURE.md` - Question management details
- `IMPLEMENTATION_STATUS.md` - Complete status overview

## 🎉 You're Ready!

Once you execute the SQL, everything is ready to use. No code changes needed!
