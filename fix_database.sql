-- Fix the role column to include 'user' role
USE 3a8;

ALTER TABLE users MODIFY COLUMN role ENUM('admin', 'investor', 'innovator', 'user') NOT NULL;

-- Insert admin user if not exists
INSERT IGNORE INTO users (id, email, password_hash, name, role, avatar_url, bio, points, level, is_active, email_verified)
VALUES (
    UUID(),
    'admin@investi.com',
    'admin123',
    'Administrator',
    'admin',
    NULL,
    'Platform Administrator',
    0,
    1,
    TRUE,
    TRUE
);

-- Show all users
SELECT email, name, role FROM users;
