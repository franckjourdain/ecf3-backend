# Projet ECF3 - Application de gestion des cours, notes et utilisateurs

Ce projet a été réalisé dans le cadre de l'épreuve ECF3 du titre professionnel de Concepteur Développeur d'Applications. Il met en œuvre une architecture moderne (Spring Boot + React) pour gérer les données relatives aux étudiants, professeurs, cours et notes.

## 🛠 Technologies principales
- **Back-end** : Java 17, Spring Boot 3, JPA (Hibernate)
- **Front-end** : React.js, Bootstrap
- **Base de données** : MySQL 8 (port 3307)
- **Tests** : JUnit 5, Mockito, SpringBootTest, MockMvc
- **Qualité de code** : JaCoCo, SonarQube
- **DevOps** : Docker, docker-compose, Jenkins, GitHub

## ⚙️ Lancer le projet avec Docker

```bash
docker-compose up --build
```

### Accès local :
- Frontend : http://localhost:3000
- Backend : http://localhost:8082
- MySQL : port 3307

## 🚀 Lancer manuellement en local

```bash
./mvnw clean install
./mvnw spring-boot:run
```

Frontend dans un second terminal :
```bash
cd front
npm install
npm start
```

## 🔐 Authentification

- Endpoint : `POST /api/auth/login`
- Payload JSON :
```json
{
  "username": "prof@test.com",
  "password": "test123"
}
```
- Retour : JWT à inclure dans les headers `Authorization: Bearer <token>`

## 📊 Structure de l'application

- Rôles utilisateurs : Étudiant, Professeur, Professeur Principal, Administrateur
- Contrôles d'accès via Spring Security et annotations `@PreAuthorize`
- Validation backend (JSR-303) + frontend (React)

## 🔮 Tests

### Unitaires (Services)
```bash
./mvnw test
```
- Mockito pour isoler les dépendances

### Intégration (Controller → BDD)
- Avec `@SpringBootTest`, base MySQL test initialisée par `schema.sql`

### Rapport de couverture :
```
target/site/jacoco/index.html
```

## 🚮 Sécurité

- Authentification via JWT
- Autorisation par rôle
- Validation des entrées : annotations `@Valid`, email unique, etc.
- Mot de passe hashé avec BCrypt
- Protection CSRF intégrée (Spring + React)

## ♻️ CI/CD avec Jenkins

Pipeline Jenkins configuré via `Jenkinsfile` :
- Build Maven
- Tests + JaCoCo
- Analyse SonarQube
- Archivage du .jar

## 📚 Documentation technique

- `schema.sql` : création manuelle de la base
- Dossier `src/test` : tous les tests automatisés
- Rapport SonarQube : qualité et couverture
- `README.md` : guide de prise en main rapide

---

© 2025 - Kévin Boedec | CDA ECF3 | AFPA Brest


