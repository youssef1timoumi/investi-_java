# SQL Setup Instructions

## 🎯 Goal

Create the 4 database tables required for the Quiz-Course Linking feature.

## ⚡ Quick Method (Recommended)

### Option 1: Execute Entire File

```bash
mysql -u your_username -p 3a8 < database.sql
```

This is the easiest method. The file contains `IF NOT EXISTS` checks, so it's safe to run multiple times.

### Option 2: Using MySQL Workbench

1. Open MySQL Workbench
2. Connect to your database
3. File → Open SQL Script
4. Select `database.sql`
5. Click Execute (⚡ icon)

### Option 3: Using phpMyAdmin

1. Open phpMyAdmin
2. Select database `3a8`
3. Click "Import" tab
4. Choose file: `database.sql`
5. Click "Go"

## 📋 Manual Method (If Needed)

If you prefer to execute only the new tables, copy and paste these commands:

### Step 1: Connect to Database

```bash
mysql -u your_username -p
```

Enter your password, then:

```sql
USE 3a8;
```

### Step 2: Create Tables

```sql
-- Table 1: Course Interactions (likes, dislikes, reports)
CREATE TABLE IF NOT EXISTS course_interactions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    course_id BIGINT NOT NULL,
    interaction_type ENUM('like', 'dislike', 'report') NOT NULL,
    report_reason TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES personne(id) ON DELETE CASCADE,
    FOREIGN KEY (course_id) REFERENCES course(id) ON DELETE CASCADE,
    UNIQUE KEY unique_user_course_interaction (user_id, course_id, interaction_type),
    INDEX idx_course_interactions_course (course_id),
    INDEX idx_course_interactions_user (user_id),
    INDEX idx_course_interactions_type (interaction_type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Table 2: Course-Quiz Linking
CREATE TABLE IF NOT EXISTS course_quizzes (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    course_id BIGINT NOT NULL,
    quiz_id BIGINT NOT NULL,
    quiz_order INT DEFAULT 1,
    is_required BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (course_id) REFERENCES course(id) ON DELETE CASCADE,
    FOREIGN KEY (quiz_id) REFERENCES quizzes(id) ON DELETE CASCADE,
    UNIQUE KEY unique_course_quiz (course_id, quiz_id),
    INDEX idx_course_quizzes_course (course_id),
    INDEX idx_course_quizzes_quiz (quiz_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Table 3: Questions (dynamic)
CREATE TABLE IF NOT EXISTS questions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    quiz_id BIGINT NOT NULL,
    question_text TEXT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (quiz_id) REFERENCES quizzes(id) ON DELETE CASCADE,
    INDEX idx_questions_quiz (quiz_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Table 4: Question Options (dynamic)
CREATE TABLE IF NOT EXISTS question_options (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    question_id BIGINT NOT NULL,
    option_text VARCHAR(500) NOT NULL,
    is_correct BOOLEAN DEFAULT FALSE,
    option_order INT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (question_id) REFERENCES questions(id) ON DELETE CASCADE,
    INDEX idx_options_question (question_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
```

### Step 3: Verify Tables Created

```sql
-- Check if tables exist
SHOW TABLES LIKE 'course_%';
SHOW TABLES LIKE 'question%';

-- Should show:
-- course_interactions
-- course_quizzes
-- questions
-- question_options
```

### Step 4: Verify Table Structure

```sql
-- Check course_quizzes structure
DESCRIBE course_quizzes;

-- Check questions structure
DESCRIBE questions;

-- Check question_options structure
DESCRIBE question_options;

-- Check course_interactions structure
DESCRIBE course_interactions;
```

## ✅ Verification

After executing the SQL, verify everything is set up correctly:

### Check Table Count

```sql
SELECT COUNT(*) as table_count 
FROM information_schema.tables 
WHERE table_schema = '3a8' 
AND table_name IN ('course_interactions', 'course_quizzes', 'questions', 'question_options');
```

Expected result: `table_count = 4`

### Check Foreign Keys

```sql
SELECT 
    TABLE_NAME,
    CONSTRAINT_NAME,
    REFERENCED_TABLE_NAME
FROM information_schema.KEY_COLUMN_USAGE
WHERE TABLE_SCHEMA = '3a8'
AND TABLE_NAME IN ('course_interactions', 'course_quizzes', 'questions', 'question_options')
AND REFERENCED_TABLE_NAME IS NOT NULL;
```

Should show foreign key relationships.

### Check Indexes

```sql
SELECT 
    TABLE_NAME,
    INDEX_NAME,
    COLUMN_NAME
FROM information_schema.STATISTICS
WHERE TABLE_SCHEMA = '3a8'
AND TABLE_NAME IN ('course_interactions', 'course_quizzes', 'questions', 'question_options')
ORDER BY TABLE_NAME, INDEX_NAME;
```

Should show all indexes created.

## 🐛 Troubleshooting

### Error: "Table 'personne' doesn't exist"

The `personne` table should already exist. If not, execute the full `database.sql` file which creates all tables.

### Error: "Table 'course' doesn't exist"

The `course` table should already exist. Execute the full `database.sql` file.

### Error: "Table 'quizzes' doesn't exist"

The `quizzes` table should already exist. Execute the full `database.sql` file.

### Error: "Cannot add foreign key constraint"

This means the referenced tables don't exist. Solution:
1. Execute the full `database.sql` file
2. This will create all tables in the correct order

### Error: "Access denied"

Your MySQL user doesn't have CREATE TABLE permissions. Solutions:
1. Use root user: `mysql -u root -p`
2. Or grant permissions: `GRANT CREATE ON 3a8.* TO 'your_username'@'localhost';`

### Error: "Database '3a8' doesn't exist"

Create the database first:
```sql
CREATE DATABASE IF NOT EXISTS 3a8;
USE 3a8;
```

Then execute the table creation commands.

## 📊 What Each Table Does

### course_interactions
Tracks user interactions with courses:
- **Likes**: User likes a course
- **Dislikes**: User dislikes a course
- **Reports**: User reports inappropriate content

### course_quizzes
Links quizzes to courses:
- **Many-to-many relationship**: One course can have many quizzes
- **Quiz order**: Controls display order
- **Required flag**: Marks quiz as required or optional

### questions
Stores quiz questions:
- **Dynamic**: No fixed count
- **Text-based**: Supports long questions
- **Linked to quiz**: Each question belongs to one quiz

### question_options
Stores answer options:
- **Dynamic**: 2+ options per question
- **Correct flag**: Marks which options are correct
- **Ordered**: Controls display order

## 🔐 Security Notes

- All tables use **InnoDB** engine for transaction support
- **Foreign keys** ensure referential integrity
- **Cascade deletes** prevent orphaned records
- **Indexes** optimize query performance
- **Unique constraints** prevent duplicate data
- **ENUM types** restrict values to valid options

## 📈 Performance Optimization

The tables include indexes on:
- Foreign key columns (automatic)
- Frequently queried columns
- Join columns

This ensures fast queries even with large datasets.

## 🎯 Next Steps

After executing the SQL:

1. ✅ Verify tables created (see Verification section above)
2. ✅ Run your JavaFX application
3. ✅ Test the features (see TESTING_CHECKLIST.md)
4. ✅ Enjoy your new feature!

## 📞 Need Help?

- Check the Troubleshooting section above
- Review the full `database.sql` file
- Ensure MySQL is running
- Verify database name is correct (`3a8`)
- Check user permissions

## 🎉 Success!

Once you see all 4 tables created without errors, you're ready to use the Quiz-Course Linking feature!

---

**Quick Command Reference:**

```bash
# Execute SQL file
mysql -u your_username -p 3a8 < database.sql

# Verify tables
mysql -u your_username -p 3a8 -e "SHOW TABLES LIKE 'course_%'; SHOW TABLES LIKE 'question%';"

# Check table structure
mysql -u your_username -p 3a8 -e "DESCRIBE course_quizzes;"
```

---

**Status**: Ready to execute! 🚀
