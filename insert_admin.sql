-- Insert admin user
-- Password: admin123 (you should hash this in production)
INSERT INTO users (id, email, password_hash, name, role, avatar_url, bio, points, level, is_active, email_verified)
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
