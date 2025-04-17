CREATE TABLE IF NOT EXISTS role (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nom VARCHAR(255) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS etudiant (
    id_etudiant INT AUTO_INCREMENT PRIMARY KEY,
    nom VARCHAR(255),
    prenom VARCHAR(255),
    email VARCHAR(255) UNIQUE,
    mot_de_passe VARCHAR(255),
    role_id INT,
    FOREIGN KEY (role_id) REFERENCES role(id)
);

CREATE TABLE IF NOT EXISTS cours (
    id_cours INT AUTO_INCREMENT PRIMARY KEY,
    intitule VARCHAR(255),
    ref VARCHAR(255),
    professeur_id_etudiant INT,
    `option` BIT,
    FOREIGN KEY (professeur_id_etudiant) REFERENCES etudiant(id_etudiant)
);

CREATE TABLE IF NOT EXISTS note (
    id_note INT AUTO_INCREMENT PRIMARY KEY,
    intitule VARCHAR(255),
    valeur DOUBLE,
    cours_id INT,
    etudiant_id INT,
    FOREIGN KEY (cours_id) REFERENCES cours(id_cours),
    FOREIGN KEY (etudiant_id) REFERENCES etudiant(id_etudiant)
);

CREATE TABLE IF NOT EXISTS etudiant_cours (
    cours_id INT,
    etudiant_id INT,
    PRIMARY KEY (cours_id, etudiant_id),
    FOREIGN KEY (cours_id) REFERENCES cours(id_cours),
    FOREIGN KEY (etudiant_id) REFERENCES etudiant(id_etudiant)
);
