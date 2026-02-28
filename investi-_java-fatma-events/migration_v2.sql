-- Migration V2: Add missing columns for geolocation and AI images
ALTER TABLE evenement ADD COLUMN lieu_latitude DOUBLE AFTER lieu;
ALTER TABLE evenement ADD COLUMN lieu_longitude DOUBLE AFTER lieu_latitude;
ALTER TABLE evenement ADD COLUMN image_url VARCHAR(255) AFTER lieu_longitude;
