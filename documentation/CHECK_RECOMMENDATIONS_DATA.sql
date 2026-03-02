-- ============================================
-- Recommendation Engine - Database Check
-- ============================================
-- Run these queries to verify your data is ready for recommendations

-- 1. Check total courses
SELECT 'Total Courses' as check_name, COUNT(*) as count FROM course;

-- 2. Check published courses (recommendations need these!)
SELECT 'Published Courses' as check_name, COUNT(*) as count 
FROM course WHERE status = 'published';

-- 3. Check course status distribution
SELECT 'Course Status Distribution' as check_name, status, COUNT(*) as count 
FROM course 
GROUP BY status;

-- 4. Check if courses have required fields
SELECT 'Courses with Thumbnails' as check_name, COUNT(*) as count 
FROM course WHERE thumbnail_url IS NOT NULL AND thumbnail_url != '';

-- 5. Check course history (for personalized recommendations)
SELECT 'Total Course History' as check_name, COUNT(*) as count FROM course_history;

-- 6. Check user 1's history (current user)
SELECT 'User 1 History' as check_name, COUNT(*) as count 
FROM course_history WHERE user_id = 1;

-- 7. Check course interactions (likes/dislikes)
SELECT 'Course Interactions' as check_name, interaction_type, COUNT(*) as count 
FROM course_interactions 
GROUP BY interaction_type;

-- 8. Sample courses (first 5)
SELECT 'Sample Courses' as check_name, id, title, status, category, difficulty_level, reward_points
FROM course 
LIMIT 5;

-- ============================================
-- FIXES (if needed)
-- ============================================

-- If you have courses but status is NULL or empty, run this:
-- UPDATE course SET status = 'published' WHERE status IS NULL OR status = '';

-- If you want to test with specific courses, mark them as published:
-- UPDATE course SET status = 'published' WHERE id IN (1, 2, 3, 4, 5, 6);

-- To create some test course history for user 1:
-- INSERT INTO course_history (user_id, course_id, visited_at) 
-- SELECT 1, id, NOW() FROM course LIMIT 3;

-- To add some test likes:
-- INSERT INTO course_interactions (user_id, course_id, interaction_type, created_at)
-- SELECT 1, id, 'like', NOW() FROM course LIMIT 2;
