DROP TABLE IF EXISTS note;
DROP TABLE IF EXISTS etudiant_cours;
DROP TABLE IF EXISTS cours;
DROP TABLE IF EXISTS etudiant;
DROP TABLE IF EXISTS role;

CREATE TABLE role (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  nom VARCHAR(255) NOT NULL
);

CREATE TABLE etudiant (
  id_etudiant BIGINT AUTO_INCREMENT PRIMARY KEY,
  email VARCHAR(255) UNIQUE,
  mot_de_passe VARCHAR(255),
  nom VARCHAR(255),
  prenom VARCHAR(255),
  role_id BIGINT,
  FOREIGN KEY (role_id) REFERENCES role(id)
);

CREATE TABLE cours (
    id_cours INT AUTO_INCREMENT PRIMARY KEY,
    intitule VARCHAR(255),
    ref VARCHAR(255),
    professeur_id_etudiant INT,
    est_optionnel BOOLEAN
);


CREATE TABLE note (
  id_note BIGINT AUTO_INCREMENT PRIMARY KEY,
  valeur FLOAT,
  intitule VARCHAR(255),
  etudiant_id BIGINT,
  cours_id INT,
  FOREIGN KEY (etudiant_id) REFERENCES etudiant(id_etudiant),
  FOREIGN KEY (cours_id) REFERENCES cours(id_cours)
);

CREATE TABLE etudiant_cours (
  cours_id INT,
  etudiant_id BIGINT,
  PRIMARY KEY (cours_id, etudiant_id),
  FOREIGN KEY (cours_id) REFERENCES cours(id_cours),
  FOREIGN KEY (etudiant_id) REFERENCES etudiant(id_etudiant)
);
