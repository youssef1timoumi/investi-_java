Table Evenement
CREATE TABLE evenement (
    id_evenement INT PRIMARY KEY AUTO_INCREMENT,
    id_mentor INT NOT NULL,
    titre VARCHAR(200) NOT NULL,
    contenu TEXT NOT NULL,
    lieu VARCHAR(200),
    date_debut DATETIME NOT NULL,
    date_fin DATETIME NOT NULL,
    
    FOREIGN KEY (id_mentor) REFERENCES user(id_user) ON DELETE CASCADE,
    
    CONSTRAINT check_dates CHECK (date_fin > date_debut)
);

-- Table Inscription
CREATE TABLE inscription (
    id_inscription INT PRIMARY KEY AUTO_INCREMENT,
    id_user INT NOT NULL,
    id_evenement INT NOT NULL,
    date_inscription DATETIME DEFAULT CURRENT_TIMESTAMP,
    statut ENUM('confirme', 'en_attente', 'annule') DEFAULT 'confirme',
    
    FOREIGN KEY (id_user) REFERENCES user(id_user) ON DELETE CASCADE,
    FOREIGN KEY (id_evenement) REFERENCES evenement(id_evenement) ON DELETE CASCADE,
    
    UNIQUE KEY unique_inscription (id_user, id_evenement)
);