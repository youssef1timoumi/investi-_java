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
