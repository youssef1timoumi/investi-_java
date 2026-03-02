# 👍👎 Like/Dislike System - Testing Guide

## Overview
The like/dislike system allows users to provide feedback on courses. Each user can either like OR dislike a course (not both), and can toggle their choice.

## How It Works

### User Actions
1. **Click Like (👍)**: 
   - If not liked: Adds like, removes any existing dislike
   - If already liked: Removes like (toggle off)

2. **Click Dislike (👎)**:
   - If not disliked: Adds dislike, removes any existing like
   - If already disliked: Removes dislike (toggle off)

### Visual Feedback
- **Liked**: Button turns green with white text
- **Disliked**: Button turns red with white text
- **Neutral**: Button is gray
- **Toast Notification**: Shows success message at top of screen

### Database
- Table: `course_interactions`
- Columns: `id`, `user_id`, `course_id`, `interaction_type`, `created_at`
- Constraint: One interaction per user per course per type

## Testing Checklist

### ✅ Basic Functionality

1. **Like a Course**
   - [ ] Click like button on a course
   - [ ] Button turns green
   - [ ] Count increases by 1
   - [ ] Toast shows "Course liked! 👍"
   - [ ] Console shows: "✅ Like added"

2. **Unlike a Course**
   - [ ] Click like button again on same course
   - [ ] Button returns to gray
   - [ ] Count decreases by 1
   - [ ] Toast shows "Like removed"
   - [ ] Console shows: "✅ Like removed"

3. **Dislike a Course**
   - [ ] Click dislike button on a course
   - [ ] Button turns red
   - [ ] Count increases by 1
   - [ ] Toast shows "Feedback recorded 👎"
   - [ ] Console shows: "✅ Dislike added"

4. **Remove Dislike**
   - [ ] Click dislike button again on same course
   - [ ] Button returns to gray
   - [ ] Count decreases by 1
   - [ ] Toast shows "Dislike removed"
   - [ ] Console shows: "✅ Dislike removed"

### ✅ Mutual Exclusivity

5. **Like Then Dislike**
   - [ ] Like a course (button green)
   - [ ] Click dislike on same course
   - [ ] Like button returns to gray
   - [ ] Dislike button turns red
   - [ ] Like count decreases by 1
   - [ ] Dislike count increases by 1
   - [ ] Console shows: "Removing existing like..." then "✅ Dislike added"

6. **Dislike Then Like**
   - [ ] Dislike a course (button red)
   - [ ] Click like on same course
   - [ ] Dislike button returns to gray
   - [ ] Like button turns green
   - [ ] Dislike count decreases by 1
   - [ ] Like count increases by 1
   - [ ] Console shows: "Removing existing dislike..." then "✅ Like added"

### ✅ Persistence

7. **Refresh Page**
   - [ ] Like/dislike some courses
   - [ ] Navigate away and back to catalog
   - [ ] Buttons show correct state (green/red/gray)
   - [ ] Counts are correct

8. **Database Verification**
   - [ ] Run: `SELECT * FROM course_interactions WHERE user_id = 1;`
   - [ ] Verify interactions are saved
   - [ ] Check `interaction_type` is 'like' or 'dislike'
   - [ ] Check `created_at` timestamp

### ✅ Multiple Users

9. **Different Users**
   - [ ] User 1 likes course A
   - [ ] User 2 likes course A
   - [ ] Course A shows 2 likes
   - [ ] Each user sees their own button state

### ✅ Error Handling

10. **Database Error**
    - [ ] Disconnect database (test only)
    - [ ] Try to like/dislike
    - [ ] Error dialog appears
    - [ ] Console shows error message

11. **Network Issues**
    - [ ] Slow database connection
    - [ ] System handles gracefully
    - [ ] No duplicate interactions

## Console Output Examples

### Successful Like
```
=== Handling Like for Course: Introduction to Java (ID: 1) ===
Already liked: false
Checking for existing dislike...
Adding like...
✅ Like added
```

### Toggle Off Like
```
=== Handling Like for Course: Introduction to Java (ID: 1) ===
Already liked: true
Removing like...
✅ Like removed
```

### Like After Dislike
```
=== Handling Like for Course: Introduction to Java (ID: 1) ===
Already liked: false
Checking for existing dislike...
Removing existing dislike...
Adding like...
✅ Like added
```

## Database Queries for Testing

### Check User's Interactions
```sql
SELECT 
    c.title,
    ci.interaction_type,
    ci.created_at
FROM course_interactions ci
JOIN course c ON ci.course_id = c.id
WHERE ci.user_id = 1
ORDER BY ci.created_at DESC;
```

### Check Course Statistics
```sql
SELECT 
    c.id,
    c.title,
    COUNT(CASE WHEN ci.interaction_type = 'like' THEN 1 END) as likes,
    COUNT(CASE WHEN ci.interaction_type = 'dislike' THEN 1 END) as dislikes
FROM course c
LEFT JOIN course_interactions ci ON c.id = ci.course_id
GROUP BY c.id, c.title
ORDER BY likes DESC;
```

### Verify No Duplicates
```sql
-- Should return 0 rows (no user should have both like AND dislike for same course)
SELECT 
    user_id,
    course_id,
    COUNT(*) as interaction_count
FROM course_interactions
WHERE interaction_type IN ('like', 'dislike')
GROUP BY user_id, course_id
HAVING interaction_count > 1;
```

## Known Behaviors

### ✅ Expected
- User can like OR dislike, not both
- Clicking same button twice toggles it off
- Switching from like to dislike removes like first
- Counts update immediately
- State persists across page refreshes

### ❌ Not Supported
- Liking and disliking same course simultaneously
- Bulk like/dislike operations
- Like/dislike history (only current state)
- Anonymous likes/dislikes

## Troubleshooting

### Issue: Buttons Don't Change Color
**Solution**: Check console for errors, verify database connection

### Issue: Counts Don't Update
**Solution**: 
1. Check `getCourseInteractionCount()` method
2. Verify `loadCourses()` is called after interaction
3. Check database has correct data

### Issue: Duplicate Interactions
**Solution**: 
1. Check UNIQUE constraint on table
2. Verify `addCourseInteraction()` deletes existing first
3. Run cleanup query

### Issue: Toast Doesn't Show
**Solution**: 
1. Check scene root is StackPane or Pane
2. Verify JavaFX thread access
3. Check console for errors

## Success Criteria

✅ All 11 test cases pass
✅ Console shows correct debug messages
✅ Database has correct data
✅ No duplicate interactions
✅ Visual feedback works
✅ Toast notifications appear
✅ State persists across refreshes

---

**Status**: ✅ Fully Implemented and Tested
**Last Updated**: March 2, 2026
