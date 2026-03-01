-- Course Reports Table
-- Run this SQL to add course reporting functionality

CREATE TABLE IF NOT EXISTS course_reports (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    course_id BIGINT NOT NULL,
    user_id INT NOT NULL,
    report_reason VARCHAR(100) NOT NULL,
    description TEXT NOT NULL,
    status VARCHAR(20) DEFAULT 'pending',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (course_id) REFERENCES courses(id) ON DELETE CASCADE,
    INDEX idx_course_id (course_id),
    INDEX idx_user_id (user_id),
    INDEX idx_status (status),
    INDEX idx_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Sample report reasons (optional - for reference)
-- 'Inappropriate Content'
-- 'Misleading Information'
-- 'Copyright Violation'
-- 'Spam or Scam'
-- 'Technical Issues'
-- 'Other'
