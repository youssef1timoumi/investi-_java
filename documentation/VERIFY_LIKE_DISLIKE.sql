-- ============================================
-- Like/Dislike System - Verification Script
-- ============================================

-- 1. Check if course_interactions table exists
SHOW TABLES LIKE 'course_interactions';

-- 2. Check table structure
DESCRIBE course_interactions;

-- 3. Check current interactions
SELECT 
    'Total Interactions' as check_name,
    COUNT(*) as count 
FROM course_interactions;

-- 4. Check interactions by type
SELECT 
    interaction_type,
    COUNT(*) as count
FROM course_interactions
GROUP BY interaction_type;

-- 5. Check user 1's interactions
SELECT 
    ci.id,
    ci.course_id,
    c.title as course_title,
    ci.interaction_type,
    ci.created_at
FROM course_interactions ci
LEFT JOIN course c ON ci.course_id = c.id
WHERE ci.user_id = 1
ORDER BY ci.created_at DESC;

-- 6. Check courses with most likes
SELECT 
    c.id,
    c.title,
    COUNT(CASE WHEN ci.interaction_type = 'like' THEN 1 END) as likes,
    COUNT(CASE WHEN ci.interaction_type = 'dislike' THEN 1 END) as dislikes
FROM course c
LEFT JOIN course_interactions ci ON c.id = ci.course_id
GROUP BY c.id, c.title
HAVING likes > 0 OR dislikes > 0
ORDER BY likes DESC, dislikes ASC
LIMIT 10;

-- ============================================
-- CREATE TABLE (if not exists)
-- ============================================

-- Run this if table doesn't exist:
/*
CREATE TABLE IF NOT EXISTS course_interactions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    course_id BIGINT NOT NULL,
    interaction_type VARCHAR(50) NOT NULL,
    report_reason VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY unique_user_course_interaction (user_id, course_id, interaction_type),
    INDEX idx_course_id (course_id),
    INDEX idx_user_id (user_id),
    INDEX idx_interaction_type (interaction_type)
);
*/

-- ============================================
-- TEST DATA (optional)
-- ============================================

-- Add some test likes/dislikes:
/*
-- Like course 1
INSERT INTO course_interactions (user_id, course_id, interaction_type) 
VALUES (1, 1, 'like')
ON DUPLICATE KEY UPDATE created_at = NOW();

-- Like course 2
INSERT INTO course_interactions (user_id, course_id, interaction_type) 
VALUES (1, 2, 'like')
ON DUPLICATE KEY UPDATE created_at = NOW();

-- Dislike course 3
INSERT INTO course_interactions (user_id, course_id, interaction_type) 
VALUES (1, 3, 'dislike')
ON DUPLICATE KEY UPDATE created_at = NOW();
*/

-- ============================================
-- CLEANUP (if needed)
-- ============================================

-- Remove all interactions for user 1:
-- DELETE FROM course_interactions WHERE user_id = 1;

-- Remove specific interaction:
-- DELETE FROM course_interactions WHERE user_id = 1 AND course_id = 1 AND interaction_type = 'like';
