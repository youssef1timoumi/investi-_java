-- Create database
CREATE DATABASE IF NOT EXISTS 3a8;
USE 3a8;

-- Table: personne
CREATE TABLE IF NOT EXISTS personne (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nom VARCHAR(100) NOT NULL,
    prenom VARCHAR(100) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Table: course
CREATE TABLE IF NOT EXISTS course (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    slug VARCHAR(255) NOT NULL UNIQUE,
    description TEXT,
    content_url VARCHAR(500),
    content_type VARCHAR(50),
    difficulty_level VARCHAR(50),
    category VARCHAR(100),
    language VARCHAR(50),
    estimated_duration INT DEFAULT 0,
    reward_points INT DEFAULT 0,
    status VARCHAR(50),
    visibility VARCHAR(50),
    thumbnail_url VARCHAR(500),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    published_at TIMESTAMP NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================
-- GAMIFICATION TABLES
-- ============================================

-- Table: badges
CREATE TABLE IF NOT EXISTS badges (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    points_required INT DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_badges_points (points_required)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4
COMMENT='Available badges that users can earn';

-- Table: user_badges
CREATE TABLE IF NOT EXISTS user_badges (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    badge_id BIGINT NOT NULL,
    earned_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES personne(id) ON DELETE CASCADE,
    FOREIGN KEY (badge_id) REFERENCES badges(id) ON DELETE CASCADE,
    UNIQUE KEY unique_user_badge (user_id, badge_id),
    INDEX idx_user_badges_user (user_id),
    INDEX idx_user_badges_earned (earned_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4
COMMENT='Tracks which badges users have earned';

-- Table: quizzes
CREATE TABLE IF NOT EXISTS quizzes (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    points_reward INT DEFAULT 0,
    question_count INT DEFAULT 0,
    difficulty_level VARCHAR(50),
    category VARCHAR(100),
    time_limit INT DEFAULT 0,
    passing_score INT DEFAULT 70,
    status VARCHAR(50) DEFAULT 'active',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_quizzes_category (category),
    INDEX idx_quizzes_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4
COMMENT='Available quizzes for users to complete';

-- Table: user_quizzes
CREATE TABLE IF NOT EXISTS user_quizzes (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    quiz_id BIGINT NOT NULL,
    score INT DEFAULT 0,
    completed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    time_taken INT DEFAULT 0,
    passed BOOLEAN DEFAULT FALSE,
    FOREIGN KEY (user_id) REFERENCES personne(id) ON DELETE CASCADE,
    FOREIGN KEY (quiz_id) REFERENCES quizzes(id) ON DELETE CASCADE,
    INDEX idx_user_quizzes_user (user_id),
    INDEX idx_user_quizzes_quiz (quiz_id),
    INDEX idx_user_quizzes_completed (completed_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4
COMMENT='Tracks user quiz completions and scores';

-- Table: user_points
CREATE TABLE IF NOT EXISTS user_points (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    points INT DEFAULT 0,
    level INT DEFAULT 1,
    total_earned_points INT DEFAULT 0,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES personne(id) ON DELETE CASCADE,
    UNIQUE KEY unique_user_points (user_id),
    INDEX idx_user_points_level (level),
    INDEX idx_user_points_points (points)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4
COMMENT='Tracks user points and levels';

-- Table: point_transactions
CREATE TABLE IF NOT EXISTS point_transactions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    points INT NOT NULL,
    transaction_type VARCHAR(50) NOT NULL,
    reference_id BIGINT,
    reference_type VARCHAR(50),
    description TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES personne(id) ON DELETE CASCADE,
    INDEX idx_point_transactions_user (user_id),
    INDEX idx_point_transactions_type (transaction_type),
    INDEX idx_point_transactions_created (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4
COMMENT='Logs all point earning and spending activities';

-- Sample data for personne table
INSERT INTO personne (nom, prenom) VALUES
('Doe', 'John'),
('Smith', 'Jane'),
('Johnson', 'Bob');

-- Sample data for course table
INSERT INTO course (title, slug, description, content_url, content_type, difficulty_level, category, language, estimated_duration, reward_points, status, visibility) VALUES
('Introduction to Java', 'intro-java', 'Learn Java basics', 'https://example.com/java', 'video', 'beginner', 'programming', 'en', 120, 100, 'published', 'public'),
('Advanced SQL', 'advanced-sql', 'Master SQL queries', 'https://example.com/sql', 'video', 'advanced', 'database', 'en', 180, 200, 'published', 'public'),
('Web Development', 'web-dev', 'Build modern websites', 'https://example.com/web', 'video', 'intermediate', 'web', 'en', 240, 150, 'draft', 'private');

-- Sample data for badges
INSERT INTO badges (name, description, points_required) VALUES
('First Steps', 'Complete your first course', 0),
('Knowledge Seeker', 'Complete 5 courses', 500),
('Quiz Master', 'Complete 10 quizzes', 1000),
('Rising Star', 'Reach level 3', 250),
('Expert Learner', 'Reach level 5', 1000);

-- Table: questions
CREATE TABLE IF NOT EXISTS questions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    quiz_id BIGINT NOT NULL,
    question_text TEXT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (quiz_id) REFERENCES quizzes(id) ON DELETE CASCADE,
    INDEX idx_questions_quiz (quiz_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4
COMMENT='Questions for each quiz';

-- Table: question_options
CREATE TABLE IF NOT EXISTS question_options (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    question_id BIGINT NOT NULL,
    option_text VARCHAR(500) NOT NULL,
    is_correct BOOLEAN DEFAULT FALSE,
    option_order INT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (question_id) REFERENCES questions(id) ON DELETE CASCADE,
    INDEX idx_options_question (question_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4
COMMENT='Answer options for each question';

-- Sample data for quizzes
INSERT INTO quizzes (title, description, points_reward, question_count, difficulty_level, category, time_limit, status) VALUES
('Java Basics Quiz', 'Test your Java fundamentals', 50, 10, 'beginner', 'programming', 600, 'active'),
('SQL Advanced Quiz', 'Master SQL queries', 100, 15, 'advanced', 'database', 900, 'active'),
('Web Development Quiz', 'HTML, CSS, JavaScript basics', 75, 12, 'intermediate', 'web', 720, 'active');

-- Initialize points for existing users
INSERT INTO user_points (user_id, points, level, total_earned_points)
SELECT id, 0, 1, 0 FROM personne;

-- Table: course_interactions
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
    INDEX idx_course_interactions_user (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4
COMMENT='Tracks user interactions with courses (likes, dislikes, reports)';

-- Table: course_quizzes (links courses to quizzes)
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4
COMMENT='Links quizzes to courses';
