-- Run this in your MySQL database to add the new collaboration tracking columns --

ALTER TABLE investment 
ADD COLUMN progressPercentage INT DEFAULT 0,
ADD COLUMN latestProgressLog TEXT DEFAULT NULL,
ADD COLUMN paymentMonthsCompleted INT DEFAULT 0;
