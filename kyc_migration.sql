-- KYC Migration: add id_image_url column and set is_active default to FALSE
ALTER TABLE users ADD COLUMN id_image_url TEXT AFTER email_verified;
ALTER TABLE users ALTER is_active SET DEFAULT FALSE;
