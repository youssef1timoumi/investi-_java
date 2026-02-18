-- MySQL/MariaDB Simple Forum Schema for INVESTI Platform

CREATE TABLE forum_posts (
    id CHAR(36) PRIMARY KEY DEFAULT (UUID()),
    user_id CHAR(36) NOT NULL,
    parent_post_id CHAR(36) NULL,
    title VARCHAR(500),
    content TEXT NOT NULL,
    category VARCHAR(100),
    tags JSON,
    likes INT DEFAULT 0,
    views INT DEFAULT 0,
    is_pinned BOOLEAN DEFAULT FALSE,
    is_deleted BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (parent_post_id) REFERENCES forum_posts(id) ON DELETE CASCADE,
    INDEX idx_forum_posts_user_id (user_id),
    INDEX idx_forum_posts_parent_post_id (parent_post_id),
    INDEX idx_forum_posts_category (category),
    INDEX idx_forum_posts_created_at (created_at),
    INDEX idx_forum_posts_is_deleted (is_deleted),
    FULLTEXT INDEX idx_forum_posts_search (title, content)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
COMMENT='Forum posts and comments - parent_post_id NULL means main post, otherwise its a comment';
