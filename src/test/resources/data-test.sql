DELETE FROM note;
DELETE FROM etudiant_cours;
DELETE FROM cours;
DELETE FROM etudiant;
DELETE FROM role;

-- Réinsertion des rôles
INSERT INTO role (nom) VALUES ('ETUDIANT');
INSERT INTO role (nom) VALUES ('PROFESSEUR');
INSERT INTO role (nom) VALUES ('ADMIN');

-- Insertion d’un étudiant avec un rôle fiable via sous-requête
INSERT INTO etudiant (nom, prenom, email, mot_de_passe, role_id)
VALUES (
    'Test',
    'User',
    'test.user@test.com',
    'password',
    (SELECT id FROM role WHERE nom = 'ETUDIANT')
);
