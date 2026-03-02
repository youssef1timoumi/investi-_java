# 🚀 Recommendation Engine - Setup Steps

## Quick Setup (3 Steps)

### Step 1: Rebuild Your Project ⚙️

**In IntelliJ IDEA:**
1. Click **Build** menu
2. Select **Rebuild Project**
3. Wait for "Build completed successfully"

**Why?** The code has been updated but needs to be recompiled.

---

### Step 2: Verify Database Has Courses 📊

Run this SQL query:

```sql
SELECT COUNT(*) as total, 
       SUM(CASE WHEN status = 'published' THEN 1 ELSE 0 END) as published
FROM course;
```

**Expected Result:**
- `total` > 0 (you have courses)
- `published` > 0 (some courses are published)

**If published = 0**, run this fix:
```sql
UPDATE course SET status = 'published';
```

---

### Step 3: Run and Check 🎯

1. **Start** your application
2. **Navigate** to Course Catalog
3. **Look for** the "✨ Recommended for You" section at the top
4. **Check console** for debug messages

**Console should show:**
```
=== Loading Recommendations ===
User history size: 0
Loading recommendations for new user...
=== Getting recommendations for new user ===
Found course ID: 1 (popularity: 5, likes: 2)
  Added: Introduction to Java
Total recommendations: 6
Found 6 recommendations
✅ Recommendations loaded successfully!
```

---

## What You Should See

### Course Catalog Page

```
┌─────────────────────────────────────────────────────┐
│  📚 Course Catalog                    [Buttons]     │
├─────────────────────────────────────────────────────┤
│  [Search] [Category ▼] [Difficulty ▼] [Clear]      │
├─────────────────────────────────────────────────────┤
│                                                      │
│  ✨ Recommended for You                             │
│  Based on your learning history                     │
│                                                      │
│  ┌────────┐  ┌────────┐  ┌────────┐  ┌────────┐   │
│  │ Course │  │ Course │  │ Course │  │ Course │ → │
│  │   1    │  │   2    │  │   3    │  │   4    │   │
│  └────────┘  └────────┘  └────────┘  └────────┘   │
│                                                      │
├─────────────────────────────────────────────────────┤
│  Showing 12 course(s)                               │
│                                                      │
│  [All Courses Grid Below]                           │
└─────────────────────────────────────────────────────┘
```

### Recommendation Cards

Each card shows:
- 📸 Thumbnail image (or placeholder)
- 📝 Course title
- ⭐ Points + 📊 Difficulty
- 🔘 "View Course" button (golden)

---

## Troubleshooting

### ❌ Section Not Showing

**Check 1: Did you rebuild?**
- Build → Rebuild Project in IDE

**Check 2: Any courses in database?**
```sql
SELECT COUNT(*) FROM course;
```

**Check 3: Console errors?**
- Look for red error messages
- Share them if you need help

### ❌ "No recommendations found"

**Fix: Set courses to published**
```sql
UPDATE course SET status = 'published';
```

Then restart the application.

### ❌ Console shows errors

**Common errors:**

1. **"recommendationsContainer is null"**
   - Rebuild project
   - Restart application

2. **"SQLException: Table doesn't exist"**
   - Check database connection
   - Verify tables: `course`, `course_history`, `course_interactions`

3. **"No courses found"**
   - Add courses to database
   - Or run the sample data script

---

## Testing Checklist

- [ ] Project rebuilt successfully
- [ ] Database has courses (at least 6)
- [ ] Courses have status = 'published'
- [ ] Application started without errors
- [ ] Navigated to Course Catalog
- [ ] Console shows "Loading Recommendations"
- [ ] Console shows "✅ Recommendations loaded successfully"
- [ ] Recommendations section visible on page
- [ ] Can see 6 course cards
- [ ] Can click "View Course" button
- [ ] Navigation works correctly

---

## Success! 🎉

If you see the recommendations section with course cards, you're all set!

**Next Steps:**
- View some courses to build history
- Get personalized recommendations
- Check "Similar Courses" on course content pages

---

## Still Need Help?

Run the database check script:
```bash
documentation/CHECK_RECOMMENDATIONS_DATA.sql
```

Check the troubleshooting guide:
```bash
documentation/RECOMMENDATION_TROUBLESHOOTING.md
```

Or share:
1. Console output
2. Database course count
3. Screenshot of Course Catalog page
