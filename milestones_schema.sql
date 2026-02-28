-- SQL Script to initialize the milestone table
-- Run this in your database to support the new Milestone-Driven Progress System

CREATE TABLE IF NOT EXISTS milestone (
    id INT AUTO_INCREMENT PRIMARY KEY,
    collaboration_id INT NOT NULL,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    weight DOUBLE DEFAULT 10.0,
    due_date DATE,
    status VARCHAR(50) DEFAULT 'PENDING', -- PENDING, IN_PROGRESS, COMPLETED
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_milestone_collaboration FOREIGN KEY (collaboration_id) 
        REFERENCES collaboration(id) ON DELETE CASCADE
);

-- Optional: Initial milestones for existing collaborations could be added here
-- But the system allows entrepreneurs to add them dynamically from the UI.
