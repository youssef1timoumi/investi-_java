# System Flow Diagram - Quiz-Course Linking

## 📊 Complete System Architecture

```
┌─────────────────────────────────────────────────────────────────┐
│                         MAIN MENU                                │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐          │
│  │   Manage     │  │   Manage     │  │   Manage     │          │
│  │   Courses    │  │   Quizzes    │  │   Badges     │          │
│  └──────────────┘  └──────────────┘  └──────────────┘          │
│                                                                   │
│  ┌──────────────────────────────────────────────────┐           │
│  │      Browse Course Catalog (User View)           │           │
│  └──────────────────────────────────────────────────┘           │
└─────────────────────────────────────────────────────────────────┘
```

## 🔄 Admin Workflow: Creating Linked Content

```
STEP 1: Create a Course
┌─────────────────────────────────────┐
│     Course Management Page          │
│  ┌───────────────────────────────┐  │
│  │ Title: Introduction to Java   │  │
│  │ Category: programming         │  │
│  │ Difficulty: beginner          │  │
│  │ Points: 100                   │  │
│  └───────────────────────────────┘  │
│         [Add Course]                │
└─────────────────────────────────────┘
              ↓
         Course Created
              ↓
┌─────────────────────────────────────┐
│         Database: course            │
│  id: 1                              │
│  title: "Introduction to Java"     │
│  category: "programming"            │
└─────────────────────────────────────┘

STEP 2: Create a Quiz and Link to Course
┌─────────────────────────────────────┐
│      Quiz Management Page           │
│  ┌───────────────────────────────┐  │
│  │ Title: Java Basics Test       │  │
│  │ Category: programming         │  │
│  │ Points: 50                    │  │
│  │ Related Course: ▼             │  │
│  │  ┌─────────────────────────┐  │  │
│  │  │ None (No course)        │  │  │
│  │  │ Introduction to Java ✓  │  │  │
│  │  │ Advanced SQL            │  │  │
│  │  └─────────────────────────┘  │  │
│  └───────────────────────────────┘  │
│         [Add Quiz]                  │
└─────────────────────────────────────┘
              ↓
         Quiz Created & Linked
              ↓
┌─────────────────────────────────────┐
│       Database: quizzes             │
│  id: 1                              │
│  title: "Java Basics Test"         │
│  points_reward: 50                  │
└─────────────────────────────────────┘
              ↓
┌─────────────────────────────────────┐
│    Database: course_quizzes         │
│  course_id: 1                       │
│  quiz_id: 1                         │
│  quiz_order: 1                      │
│  is_required: true                  │
└─────────────────────────────────────┘

STEP 3: Add Questions to Quiz
┌─────────────────────────────────────┐
│   Quiz Management → Edit Quiz       │
│         [Manage Questions]          │
└─────────────────────────────────────┘
              ↓
┌─────────────────────────────────────┐
│   Question Management Dialog        │
│  ┌───────────────────────────────┐  │
│  │ Question: What is a class?    │  │
│  │                               │  │
│  │ Options:                      │  │
│  │ ☑ A blueprint for objects     │  │
│  │ ☐ A function                  │  │
│  │ ☐ A variable                  │  │
│  │         [+ Add Option]        │  │
│  └───────────────────────────────┘  │
│       [Add Question]                │
└─────────────────────────────────────┘
              ↓
┌─────────────────────────────────────┐
│      Database: questions            │
│  id: 1                              │
│  quiz_id: 1                         │
│  question_text: "What is a class?"  │
└─────────────────────────────────────┘
              ↓
┌─────────────────────────────────────┐
│   Database: question_options        │
│  Option 1: "A blueprint..." ✓       │
│  Option 2: "A function"             │
│  Option 3: "A variable"             │
└─────────────────────────────────────┘
```

## 👤 User Workflow: Browsing and Taking Quizzes

```
STEP 1: Browse Course Catalog
┌─────────────────────────────────────────────────────────┐
│              Course Catalog Page                        │
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐    │
│  │ Intro to    │  │ Advanced    │  │ Web Dev     │    │
│  │ Java        │  │ SQL         │  │ Basics      │    │
│  │             │  │             │  │             │    │
│  │ 👍 12  👎 2 │  │ 👍 8   👎 1 │  │ 👍 15  👎 3 │    │
│  │ [View]      │  │ [View]      │  │ [View]      │    │
│  └─────────────┘  └─────────────┘  └─────────────┘    │
└─────────────────────────────────────────────────────────┘
              ↓ (User clicks "View")
              
STEP 2: View Course Details
┌─────────────────────────────────────────────────────────┐
│         Introduction to Java                            │
│  ─────────────────────────────────────────────────────  │
│  Learn Java basics and object-oriented programming      │
│                                                          │
│  📂 Category: programming                               │
│  📊 Difficulty: beginner                                │
│  ⏱️ Duration: 120 minutes                               │
│  ⭐ Reward: 100 points                                   │
│  ─────────────────────────────────────────────────────  │
│  📝 Related Quizzes:                                    │
│  ┌───────────────────────────────────────────────────┐ │
│  │ Java Basics Test                                  │ │
│  │ 10 questions • 50 points                          │ │
│  │                              [Take Quiz]          │ │
│  └───────────────────────────────────────────────────┘ │
│  ┌───────────────────────────────────────────────────┐ │
│  │ Java OOP Quiz                                     │ │
│  │ 15 questions • 75 points                          │ │
│  │                              [Take Quiz]          │ │
│  └───────────────────────────────────────────────────┘ │
└─────────────────────────────────────────────────────────┘
              ↓ (User clicks "Take Quiz")
              
STEP 3: Take Quiz (Coming Soon)
┌─────────────────────────────────────────────────────────┐
│         Java Basics Test                                │
│  ─────────────────────────────────────────────────────  │
│  Question 1 of 10                                       │
│                                                          │
│  What is a class in Java?                               │
│                                                          │
│  ○ A blueprint for creating objects                     │
│  ○ A function                                           │
│  ○ A variable                                           │
│  ○ A data type                                          │
│                                                          │
│  [Next Question]                                        │
└─────────────────────────────────────────────────────────┘
```

## 🗄️ Database Relationships

```
┌──────────────┐
│   personne   │
│  (users)     │
└──────┬───────┘
       │
       ├─────────────────────────────────────┐
       │                                     │
       ↓                                     ↓
┌──────────────────┐              ┌──────────────────┐
│ course_          │              │  user_points     │
│ interactions     │              │  user_badges     │
│ (likes/dislikes) │              │  user_quizzes    │
└────────┬─────────┘              └──────────────────┘
         │
         ↓
┌──────────────┐         ┌──────────────────┐
│   course     │←────────│ course_quizzes   │
│              │         │  (linking table) │
└──────────────┘         └────────┬─────────┘
                                  │
                                  ↓
                         ┌──────────────┐
                         │   quizzes    │
                         └──────┬───────┘
                                │
                                ↓
                         ┌──────────────┐
                         │  questions   │
                         └──────┬───────┘
                                │
                                ↓
                         ┌──────────────────┐
                         │ question_options │
                         └──────────────────┘
```

## 🔄 Data Flow: Quiz Creation to User View

```
1. ADMIN CREATES COURSE
   ↓
   course table
   
2. ADMIN CREATES QUIZ + SELECTS COURSE
   ↓
   quizzes table
   ↓
   course_quizzes table (link created)
   
3. ADMIN ADDS QUESTIONS
   ↓
   questions table
   ↓
   question_options table
   
4. USER BROWSES CATALOG
   ↓
   CourseCatalogController.loadCourses()
   ↓
   Displays all courses
   
5. USER CLICKS "VIEW COURSE DETAILS"
   ↓
   courseService.getQuizIdsForCourse(courseId)
   ↓
   Queries course_quizzes table
   ↓
   Gets linked quiz IDs
   ↓
   gamificationService.getQuizById(quizId)
   ↓
   Displays quiz details
   
6. USER CLICKS "TAKE QUIZ"
   ↓
   (Coming soon: Quiz-taking interface)
```

## 🎨 UI Component Hierarchy

```
MainMenuController
├── CourseController (Admin)
│   ├── Course Form
│   ├── Course List
│   └── Statistics Dialog
│
├── QuizController (Admin)
│   ├── Quiz Form
│   │   └── Course ComboBox ← Links quiz to course
│   ├── Quiz List
│   ├── Question Management Dialog
│   │   ├── Question Form
│   │   ├── Options (dynamic)
│   │   └── Questions List
│   └── Statistics Dialog
│
├── BadgeController (Admin)
│   ├── Badge Form
│   ├── Badge List
│   └── Statistics Dialog
│
└── CourseCatalogController (User)
    ├── Search/Filter Controls
    ├── Course Grid (3 columns)
    │   └── Course Cards
    │       ├── Like/Dislike/Report buttons
    │       └── View Details button
    └── Course Details Dialog
        ├── Course Info
        └── Related Quizzes List
            └── Take Quiz buttons
```

## 🔐 Key Features Summary

```
✅ Course Management
   - Create/Edit/Delete courses
   - Set difficulty, category, points
   - Statistics view

✅ Quiz Management
   - Create/Edit/Delete quizzes
   - Link to courses (optional)
   - Dynamic question management
   - Statistics view

✅ Question Management
   - Unlimited questions per quiz
   - 2+ options per question (dynamic)
   - Mark correct answers
   - Delete questions

✅ Course Catalog (User View)
   - Browse all courses
   - Search and filter
   - Like/Dislike/Report
   - View course details
   - See related quizzes

✅ Database Integration
   - Proper foreign keys
   - Cascade deletes
   - Indexed queries
   - Transaction support
```

## 🚀 Next Steps for Users

1. Execute `database.sql` to create tables
2. Create some courses in Course Management
3. Create quizzes and link them to courses
4. Add questions to quizzes
5. Browse the Course Catalog as a user
6. View course details and see linked quizzes

Everything is ready to go! 🎉
