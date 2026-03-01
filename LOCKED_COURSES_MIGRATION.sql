-- ============================================
-- LOCKED COURSES FEATURE - Database Migration
-- Adds minimum points requirement for courses
-- ============================================

USE 3a8;

-- Add minimum_points_required column to course table
ALTER TABLE course 
ADD COLUMN minimum_points_required INT DEFAULT 0 
COMMENT 'Minimum points required to unlock this course';

-- Add index for better query performance
ALTER TABLE course 
ADD INDEX idx_course_min_points (minimum_points_required);

-- Update existing courses to have 0 minimum points (unlocked by default)
UPDATE course SET minimum_points_required = 0 WHERE minimum_points_required IS NULL;

-- Sample data: Set some courses as locked
-- UPDATE course SET minimum_points_required = 100 WHERE slug = 'advanced-sql';
-- UPDATE course SET minimum_points_required = 250 WHERE slug = 'web-dev';

-- Create a test user with specific points for testing
INSERT INTO personne (nom, prenom) VALUES ('Test', 'User');

-- Get the test user ID and initialize their points
SET @test_user_id = LAST_INSERT_ID();

INSERT INTO user_points (user_id, points, level, total_earned_points)
VALUES (@test_user_id, 150, 2, 150);

-- Display test user info
SELECT 
    p.id,
    p.nom,
    p.prenom,
    up.points,
    up.level
FROM personne p
JOIN user_points up ON p.id = up.user_id
WHERE p.id = @test_user_id;

COMMIT;
