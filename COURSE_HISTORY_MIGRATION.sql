-- ============================================
-- COURSE HISTORY FEATURE - Database Migration
-- Tracks user course visits and viewing history
-- ============================================

USE 3a8;

-- Create course_history table
CREATE TABLE IF NOT EXISTS course_history (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    course_id BIGINT NOT NULL,
    visited_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_position INT DEFAULT 0 COMMENT 'Last video position in seconds or page number',
    completion_percentage INT DEFAULT 0 COMMENT 'Percentage of course completed',
    FOREIGN KEY (user_id) REFERENCES personne(id) ON DELETE CASCADE,
    FOREIGN KEY (course_id) REFERENCES course(id) ON DELETE CASCADE,
    INDEX idx_course_history_user (user_id),
    INDEX idx_course_history_course (course_id),
    INDEX idx_course_history_visited (visited_at),
    INDEX idx_course_history_user_course (user_id, course_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4
COMMENT='Tracks user course viewing history and progress';

-- Sample data: Add some history for test user
-- Assuming test user ID is 4 (adjust if different)
INSERT INTO course_history (user_id, course_id, visited_at, completion_percentage) 
SELECT 4, id, NOW() - INTERVAL FLOOR(RAND() * 30) DAY, FLOOR(RAND() * 100)
FROM course 
LIMIT 3;

COMMIT;
