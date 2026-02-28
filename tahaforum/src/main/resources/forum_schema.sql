-- Forum Management SQL Schema for INVESTI Platform
-- Run this script in phpMyAdmin to create the forum tables

-- Make sure you're using the correct database
USE 3a8;

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

-- Insert some sample data for testing (optional)
-- Make sure you have at least one user in the users table first

-- Sample post (uncomment and modify user_id to test)
-- INSERT INTO forum_posts (user_id, title, content, category) 
-- VALUES ('your-user-id-here', 'Welcome to INVESTI Forum', 'This is the first post in our community forum!', 'Announcements');


-- =====================================================
-- Insert 6 users for INVESTI Forum Testing
-- =====================================================

-- Insert Users
INSERT INTO users (id, email, password_hash, name, role, avatar_url, bio, points, level, is_active, email_verified) VALUES
(UUID(), 'mohamed.frihida@investi.tn', '$2a$10$hashedpassword123456789012345678901234567890', 'Mohamed Taha Frihida', 'investor', NULL, 'Investisseur passionné par les startups innovantes en Tunisie.', 500, 3, TRUE, TRUE),
(UUID(), 'youssef.timoumi@investi.tn', '$2a$10$hashedpassword123456789012345678901234567890', 'Youssef Timoumi', 'admin', NULL, 'Administrateur de la plateforme INVESTI.', 1000, 5, TRUE, TRUE),
(UUID(), 'fatma.sassi@investi.tn', '$2a$10$hashedpassword123456789012345678901234567890', 'Fatma Sassi', 'innovator', NULL, 'Innovatrice dans le domaine de la technologie verte.', 350, 2, TRUE, TRUE),
(UUID(), 'moez.touil@investi.tn', '$2a$10$hashedpassword123456789012345678901234567890', 'Moez Touil', 'innovator', NULL, 'Développeur et entrepreneur passionné par l''IA.', 420, 3, TRUE, TRUE),
(UUID(), 'seif.benabdallah@investi.tn', '$2a$10$hashedpassword123456789012345678901234567890', 'Seif Eddine Ben Abdallah', 'admin', NULL, 'Coach et mentor pour startups.', 800, 4, TRUE, TRUE),
(UUID(), 'dhia.djebbi@investi.tn', '$2a$10$hashedpassword123456789012345678901234567890', 'Dhia Eddine Djebbi', 'investor', NULL, 'Business Angel spécialisé fintech et e-commerce.', 650, 4, TRUE, TRUE);

-- Verify users were created
SELECT id, name, email, role FROM users ORDER BY name;
