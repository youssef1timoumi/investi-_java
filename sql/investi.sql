USE 3a8;

CREATE TABLE IF NOT EXISTS users (
    id CHAR(36) PRIMARY KEY DEFAULT (UUID()),
    email VARCHAR(255) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    name VARCHAR(255) NOT NULL,
    role ENUM('admin', 'investor', 'innovator', 'mentor', 'user') NOT NULL,
    avatar_url TEXT,
    bio TEXT,
    points INT DEFAULT 0,
    level INT DEFAULT 1,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    last_login TIMESTAMP NULL,
    is_active BOOLEAN DEFAULT FALSE,
    email_verified BOOLEAN DEFAULT FALSE,
    id_image_url TEXT,
    INDEX idx_users_email (email),
    INDEX idx_users_role (role),
    INDEX idx_users_is_active (is_active)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS evenement (
    id_evenement INT PRIMARY KEY AUTO_INCREMENT,
    id_mentor CHAR(36) NOT NULL,
    titre VARCHAR(200) NOT NULL,
    contenu TEXT NOT NULL,
    lieu VARCHAR(200),
    lieu_latitude DOUBLE,
    lieu_longitude DOUBLE,
    image_url VARCHAR(255),
    date_debut DATETIME NOT NULL,
    date_fin DATETIME NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (id_mentor) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT check_dates CHECK (date_fin > date_debut),
    INDEX idx_evenement_mentor (id_mentor),
    INDEX idx_evenement_dates (date_debut, date_fin)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS inscription (
    id_inscription INT PRIMARY KEY AUTO_INCREMENT,
    id_user CHAR(36) NOT NULL,
    id_evenement INT NOT NULL,
    date_inscription DATETIME DEFAULT CURRENT_TIMESTAMP,
    statut ENUM('confirme', 'en_attente', 'annule') DEFAULT 'confirme',
    FOREIGN KEY (id_user) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (id_evenement) REFERENCES evenement(id_evenement) ON DELETE CASCADE,
    UNIQUE KEY unique_inscription (id_user, id_evenement),
    INDEX idx_inscription_user (id_user),
    INDEX idx_inscription_event (id_evenement),
    INDEX idx_inscription_status (statut)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


INSERT INTO users (email, password_hash, name, role, is_active, email_verified) 
VALUES ('youssef1timoumi@hotmail.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'Youssef Timoumi', 'admin', TRUE, TRUE);


-- =====================================================
-- FORUM MODULE TABLES
-- =====================================================

-- Create forum_posts table
CREATE TABLE IF NOT EXISTS forum_posts (
    id CHAR(36) PRIMARY KEY DEFAULT (UUID()),
    user_id CHAR(36) NOT NULL,
    title VARCHAR(500),
    content TEXT,
    category VARCHAR(100),
    upvotes INT DEFAULT 0,
    downvotes INT DEFAULT 0,
    views INT DEFAULT 0,
    is_pinned BOOLEAN DEFAULT FALSE,
    is_deleted BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_forum_posts_user_id (user_id),
    INDEX idx_forum_posts_category (category),
    INDEX idx_forum_posts_created_at (created_at),
    INDEX idx_forum_posts_is_deleted (is_deleted),
    FULLTEXT INDEX idx_forum_posts_search (title, content)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci 
COMMENT='Forum posts with Reddit-style upvote/downvote system';

-- Create forum_post_images table for multiple images per post
CREATE TABLE IF NOT EXISTS forum_post_images (
    id CHAR(36) PRIMARY KEY DEFAULT (UUID()),
    post_id CHAR(36) NOT NULL,
    image_path VARCHAR(500) NOT NULL,
    image_order INT DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (post_id) REFERENCES forum_posts(id) ON DELETE CASCADE,
    INDEX idx_forum_post_images_post_id (post_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci 
COMMENT='Images attached to forum posts';

-- Create forum_comments table
CREATE TABLE IF NOT EXISTS forum_comments (
    id CHAR(36) PRIMARY KEY DEFAULT (UUID()),
    post_id CHAR(36) NOT NULL,
    user_id CHAR(36) NOT NULL,
    parent_comment_id CHAR(36) NULL,
    content TEXT NOT NULL,
    upvotes INT DEFAULT 0,
    downvotes INT DEFAULT 0,
    is_deleted BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (post_id) REFERENCES forum_posts(id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (parent_comment_id) REFERENCES forum_comments(id) ON DELETE CASCADE,
    INDEX idx_forum_comments_post_id (post_id),
    INDEX idx_forum_comments_user_id (user_id),
    INDEX idx_forum_comments_parent_comment_id (parent_comment_id),
    INDEX idx_forum_comments_created_at (created_at),
    INDEX idx_forum_comments_is_deleted (is_deleted)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci 
COMMENT='Comments on forum posts with nested replies and upvote/downvote system';

-- Create forum_post_votes table
CREATE TABLE IF NOT EXISTS forum_post_votes (
    id CHAR(36) PRIMARY KEY DEFAULT (UUID()),
    post_id CHAR(36) NOT NULL,
    user_id CHAR(36) NOT NULL,
    vote_type ENUM('upvote', 'downvote') NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (post_id) REFERENCES forum_posts(id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    UNIQUE KEY unique_post_vote (post_id, user_id),
    INDEX idx_forum_post_votes_post_id (post_id),
    INDEX idx_forum_post_votes_user_id (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci 
COMMENT='Tracks user votes on posts - one vote per user per post';

-- Create forum_comment_votes table
CREATE TABLE IF NOT EXISTS forum_comment_votes (
    id CHAR(36) PRIMARY KEY DEFAULT (UUID()),
    comment_id CHAR(36) NOT NULL,
    user_id CHAR(36) NOT NULL,
    vote_type ENUM('upvote', 'downvote') NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (comment_id) REFERENCES forum_comments(id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    UNIQUE KEY unique_comment_vote (comment_id, user_id),
    INDEX idx_forum_comment_votes_comment_id (comment_id),
    INDEX idx_forum_comment_votes_user_id (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci 
COMMENT='Tracks user votes on comments - one vote per user per comment';

-- Create forum_post_views table to track unique views per user
CREATE TABLE IF NOT EXISTS forum_post_views (
    id CHAR(36) PRIMARY KEY DEFAULT (UUID()),
    post_id CHAR(36) NOT NULL,
    user_id CHAR(36) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (post_id) REFERENCES forum_posts(id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    UNIQUE KEY unique_post_view (post_id, user_id),
    INDEX idx_forum_post_views_post_id (post_id),
    INDEX idx_forum_post_views_user_id (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci 
COMMENT='Tracks unique views per user - one view per user per post';

-- Create forum_bookmarks table for saved/bookmarked posts
CREATE TABLE IF NOT EXISTS forum_bookmarks (
    id CHAR(36) PRIMARY KEY DEFAULT (UUID()),
    post_id CHAR(36) NOT NULL,
    user_id CHAR(36) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (post_id) REFERENCES forum_posts(id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    UNIQUE KEY unique_bookmark (post_id, user_id),
    INDEX idx_forum_bookmarks_user_id (user_id),
    INDEX idx_forum_bookmarks_post_id (post_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci 
COMMENT='Tracks bookmarked/saved posts per user';

-- Create forum_notifications table for @mention notifications
CREATE TABLE IF NOT EXISTS forum_notifications (
    id CHAR(36) PRIMARY KEY DEFAULT (UUID()),
    recipient_user_id CHAR(36) NOT NULL,
    sender_user_id CHAR(36) NOT NULL,
    post_id CHAR(36) NOT NULL,
    comment_id CHAR(36) NULL,
    type VARCHAR(50) NOT NULL DEFAULT 'mention',
    message TEXT NOT NULL,
    is_read BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (recipient_user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (sender_user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (post_id) REFERENCES forum_posts(id) ON DELETE CASCADE,
    INDEX idx_forum_notif_recipient (recipient_user_id),
    INDEX idx_forum_notif_read (recipient_user_id, is_read)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci 
COMMENT='Notifications for @mention tags - notifies users when they are tagged';


-- =====================================================
-- GAMIFICATION SYSTEM TABLES
-- =====================================================

-- Create course table
CREATE TABLE IF NOT EXISTS `course` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `title` VARCHAR(255) NOT NULL,
    `slug` VARCHAR(255) UNIQUE NOT NULL,
    `description` TEXT,
    `content_url` VARCHAR(500),
    `content_type` VARCHAR(50) COMMENT 'video, pdf, youtube, external',
    `difficulty_level` VARCHAR(50) COMMENT 'beginner, intermediate, advanced, expert',
    `category` VARCHAR(100) COMMENT 'programming, database, web, design, business',
    `language` VARCHAR(50) DEFAULT 'en',
    `estimated_duration` INT DEFAULT 0 COMMENT 'Duration in minutes',
    `reward_points` INT DEFAULT 0,
    `minimum_points_required` INT DEFAULT 0 COMMENT 'Points needed to unlock course',
    `status` VARCHAR(50) DEFAULT 'draft' COMMENT 'draft, published, archived',
    `visibility` VARCHAR(50) DEFAULT 'public' COMMENT 'public, private, unlisted',
    `thumbnail_url` VARCHAR(500),
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `published_at` TIMESTAMP NULL,
    INDEX `idx_slug` (`slug`),
    INDEX `idx_status` (`status`),
    INDEX `idx_category` (`category`),
    INDEX `idx_difficulty` (`difficulty_level`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Create course_history table
CREATE TABLE IF NOT EXISTS `course_history` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `user_id` CHAR(36) NOT NULL,
    `course_id` BIGINT NOT NULL,
    `visited_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    `last_position` INT DEFAULT 0 COMMENT 'Last video position or page number',
    `completion_percentage` INT DEFAULT 0,
    FOREIGN KEY (`user_id`) REFERENCES users(`id`) ON DELETE CASCADE,
    FOREIGN KEY (`course_id`) REFERENCES course(`id`) ON DELETE CASCADE,
    INDEX `idx_user_id` (`user_id`),
    INDEX `idx_course_id` (`course_id`),
    INDEX `idx_visited_at` (`visited_at`),
    UNIQUE KEY `unique_user_course_visit` (`user_id`, `course_id`, `visited_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Create course_interactions table
CREATE TABLE IF NOT EXISTS `course_interactions` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `user_id` CHAR(36) NOT NULL,
    `course_id` BIGINT NOT NULL,
    `interaction_type` VARCHAR(50) NOT NULL COMMENT 'like, dislike, report',
    `report_reason` VARCHAR(255),
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (`user_id`) REFERENCES users(`id`) ON DELETE CASCADE,
    FOREIGN KEY (`course_id`) REFERENCES course(`id`) ON DELETE CASCADE,
    UNIQUE KEY `unique_user_course_interaction` (`user_id`, `course_id`, `interaction_type`),
    INDEX `idx_course_id` (`course_id`),
    INDEX `idx_user_id` (`user_id`),
    INDEX `idx_interaction_type` (`interaction_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Create course_reports table
CREATE TABLE IF NOT EXISTS `course_reports` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `course_id` BIGINT NOT NULL,
    `user_id` CHAR(36) NOT NULL,
    `report_reason` VARCHAR(255) NOT NULL,
    `description` TEXT NOT NULL,
    `status` VARCHAR(50) DEFAULT 'pending' COMMENT 'pending, reviewed, resolved, dismissed',
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (`user_id`) REFERENCES users(`id`) ON DELETE CASCADE,
    FOREIGN KEY (`course_id`) REFERENCES course(`id`) ON DELETE CASCADE,
    INDEX `idx_course_id` (`course_id`),
    INDEX `idx_user_id` (`user_id`),
    INDEX `idx_status` (`status`),
    INDEX `idx_created_at` (`created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Create quizzes table
CREATE TABLE IF NOT EXISTS `quizzes` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `title` VARCHAR(255) NOT NULL,
    `description` TEXT,
    `points_reward` INT DEFAULT 0,
    `question_count` INT DEFAULT 0,
    `difficulty_level` VARCHAR(50) COMMENT 'beginner, intermediate, advanced, expert',
    `category` VARCHAR(100),
    `time_limit` INT DEFAULT 0 COMMENT 'Time limit in minutes, 0 = no limit',
    `passing_score` INT DEFAULT 70 COMMENT 'Minimum score to pass (percentage)',
    `status` VARCHAR(50) DEFAULT 'active' COMMENT 'active, inactive, archived',
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX `idx_status` (`status`),
    INDEX `idx_category` (`category`),
    INDEX `idx_difficulty` (`difficulty_level`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Create questions table
CREATE TABLE IF NOT EXISTS `questions` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `quiz_id` BIGINT NOT NULL,
    `question_text` TEXT NOT NULL,
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (`quiz_id`) REFERENCES quizzes(`id`) ON DELETE CASCADE,
    INDEX `idx_quiz_id` (`quiz_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Create question_options table
CREATE TABLE IF NOT EXISTS `question_options` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `question_id` BIGINT NOT NULL,
    `option_text` TEXT NOT NULL,
    `is_correct` BOOLEAN DEFAULT FALSE,
    `option_order` INT DEFAULT 0,
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (`question_id`) REFERENCES questions(`id`) ON DELETE CASCADE,
    INDEX `idx_question_id` (`question_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Create user_quizzes table
CREATE TABLE IF NOT EXISTS `user_quizzes` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `user_id` CHAR(36) NOT NULL,
    `quiz_id` BIGINT NOT NULL,
    `score` INT DEFAULT 0,
    `completed_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    `time_taken` INT DEFAULT 0 COMMENT 'Time taken in seconds',
    `passed` BOOLEAN DEFAULT FALSE,
    FOREIGN KEY (`user_id`) REFERENCES users(`id`) ON DELETE CASCADE,
    FOREIGN KEY (`quiz_id`) REFERENCES quizzes(`id`) ON DELETE CASCADE,
    INDEX `idx_user_id` (`user_id`),
    INDEX `idx_quiz_id` (`quiz_id`),
    INDEX `idx_completed_at` (`completed_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Create quiz_courses mapping table
CREATE TABLE IF NOT EXISTS `quiz_courses` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `quiz_id` BIGINT NOT NULL,
    `course_id` BIGINT NOT NULL,
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (`quiz_id`) REFERENCES quizzes(`id`) ON DELETE CASCADE,
    FOREIGN KEY (`course_id`) REFERENCES course(`id`) ON DELETE CASCADE,
    UNIQUE KEY `unique_quiz_course` (`quiz_id`, `course_id`),
    INDEX `idx_quiz_id` (`quiz_id`),
    INDEX `idx_course_id` (`course_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Create badges table
CREATE TABLE IF NOT EXISTS `badges` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `name` VARCHAR(255) NOT NULL UNIQUE,
    `description` TEXT,
    `points_required` INT NOT NULL,
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX `idx_points_required` (`points_required`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Create user_badges table
CREATE TABLE IF NOT EXISTS `user_badges` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `user_id` CHAR(36) NOT NULL,
    `badge_id` BIGINT NOT NULL,
    `earned_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (`user_id`) REFERENCES users(`id`) ON DELETE CASCADE,
    FOREIGN KEY (`badge_id`) REFERENCES badges(`id`) ON DELETE CASCADE,
    UNIQUE KEY `unique_user_badge` (`user_id`, `badge_id`),
    INDEX `idx_user_id` (`user_id`),
    INDEX `idx_badge_id` (`badge_id`),
    INDEX `idx_earned_at` (`earned_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Create user_points table
CREATE TABLE IF NOT EXISTS `user_points` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `user_id` CHAR(36) NOT NULL UNIQUE,
    `points` INT DEFAULT 0,
    `level` INT DEFAULT 1,
    `total_earned_points` INT DEFAULT 0 COMMENT 'Total points earned (never decreases)',
    `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (`user_id`) REFERENCES users(`id`) ON DELETE CASCADE,
    INDEX `idx_user_id` (`user_id`),
    INDEX `idx_points` (`points`),
    INDEX `idx_level` (`level`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Create point_transactions table
CREATE TABLE IF NOT EXISTS `point_transactions` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `user_id` CHAR(36) NOT NULL,
    `points` INT NOT NULL COMMENT 'Positive for earned, negative for spent',
    `transaction_type` VARCHAR(50) NOT NULL COMMENT 'quiz_completion, course_completion, badge_earned, course_unlock',
    `reference_id` BIGINT COMMENT 'ID of related entity (quiz_id, course_id, badge_id)',
    `reference_type` VARCHAR(50) COMMENT 'quiz, course, badge',
    `description` TEXT,
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (`user_id`) REFERENCES users(`id`) ON DELETE CASCADE,
    INDEX `idx_user_id` (`user_id`),
    INDEX `idx_transaction_type` (`transaction_type`),
    INDEX `idx_created_at` (`created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =====================================================
-- GAMIFICATION SAMPLE DATA
-- =====================================================

-- Insert sample badges
INSERT INTO `badges` (`name`, `description`, `points_required`) VALUES
('Beginner', 'Complete your first quiz', 10),
('Intermediate', 'Earn 50 points', 50),
('Advanced', 'Earn 100 points', 100),
('Expert', 'Earn 200 points', 200),
('Master', 'Earn 500 points', 500),
('Legend', 'Earn 1000 points', 1000)
ON DUPLICATE KEY UPDATE `description` = VALUES(`description`);
