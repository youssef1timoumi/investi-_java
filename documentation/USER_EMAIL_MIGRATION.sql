-- Add email and name fields to personne table for email notifications
-- Run this SQL to enable email notifications

-- Add email column to personne table (if not exists)
ALTER TABLE personne 
ADD COLUMN IF NOT EXISTS email VARCHAR(255) UNIQUE;

-- Add index for faster email lookups
CREATE INDEX IF NOT EXISTS idx_personne_email ON personne(email);

-- Example: Update existing user with email
-- UPDATE personne SET email = 'user@example.com' WHERE id = 1;

-- Verify the changes
DESCRIBE personne;

-- Check current data
SELECT id, nom, prenom, email FROM personne;
