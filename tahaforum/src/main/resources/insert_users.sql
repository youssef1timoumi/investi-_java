-- =====================================================
-- Insert 6 users for INVESTI Forum Testing
-- Run this in phpMyAdmin after the forum_schema.sql
-- =====================================================

USE 3a8;

-- First, check if the role ENUM needs to be updated to include 'coach'
-- If your users table has role ENUM('admin', 'investor', 'innovator'), run this first:
-- ALTER TABLE users MODIFY COLUMN role ENUM('admin', 'investor', 'innovator', 'coach') NOT NULL;

-- Insert Users (using existing ENUM values)
-- Note: Seif is a coach/mentor but we use 'admin' role to give him moderation powers
-- Or you can change to 'innovator' if you prefer

INSERT INTO users (id, email, password_hash, name, role, avatar_url, bio, points, level, is_active, email_verified) VALUES
(UUID(), 'mohamed.frihida@investi.tn', '$2a$10$hashedpassword123456789012345678901234567890', 'Mohamed Taha Frihida', 'investor', NULL, 'Investisseur passionné par les startups innovantes en Tunisie. Toujours à la recherche de projets prometteurs.', 500, 3, TRUE, TRUE),
(UUID(), 'youssef.timoumi@investi.tn', '$2a$10$hashedpassword123456789012345678901234567890', 'Youssef Timoumi', 'admin', NULL, 'Administrateur de la plateforme INVESTI. Ici pour vous aider et maintenir une communauté saine.', 1000, 5, TRUE, TRUE),
(UUID(), 'fatma.sassi@investi.tn', '$2a$10$hashedpassword123456789012345678901234567890', 'Fatma Sassi', 'innovator', NULL, 'Innovatrice dans le domaine de la technologie verte. Fondatrice de plusieurs projets écologiques.', 350, 2, TRUE, TRUE),
(UUID(), 'moez.touil@investi.tn', '$2a$10$hashedpassword123456789012345678901234567890', 'Moez Touil', 'innovator', NULL, 'Développeur et entrepreneur. Passionné par l''IA et le machine learning.', 420, 3, TRUE, TRUE),
(UUID(), 'seif.benabdallah@investi.tn', '$2a$10$hashedpassword123456789012345678901234567890', 'Seif Eddine Ben Abdallah', 'admin', NULL, 'Coach et mentor pour startups. Plus de 10 ans d''expérience dans l''accompagnement entrepreneurial.', 800, 4, TRUE, TRUE),
(UUID(), 'dhia.djebbi@investi.tn', '$2a$10$hashedpassword123456789012345678901234567890', 'Dhia Eddine Djebbi', 'investor', NULL, 'Business Angel et investisseur. Spécialisé dans les fintech et e-commerce.', 650, 4, TRUE, TRUE);

-- Verify users were created
SELECT id, name, email, role FROM users ORDER BY name;
