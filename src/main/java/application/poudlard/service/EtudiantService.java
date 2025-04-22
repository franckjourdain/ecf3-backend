package application.poudlard.service;

import application.poudlard.dao.CoursDAO;
import application.poudlard.dao.EtudiantDAO;
import application.poudlard.dao.RoleRepository;
import application.poudlard.model.Cours;
import application.poudlard.model.Etudiant;
import application.poudlard.model.Note;
import application.poudlard.model.Role;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EtudiantService {

    private static final String ETUDIANT_NON_TROUVE = "Etudiant non trouvé";

    private final EtudiantDAO etudiantDao;
    private final CoursDAO coursDao;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;

    public EtudiantService(EtudiantDAO etudiantDao, CoursDAO coursDao, PasswordEncoder passwordEncoder, RoleRepository roleRepository) {
        this.etudiantDao = etudiantDao;
        this.coursDao = coursDao;
        this.passwordEncoder = passwordEncoder;
        this.roleRepository = roleRepository;
    }
    // Méthode pour créer un étudiant si absent dans la BDD
    private void creerEtudiantSiAbsent(String nom, String prenom, String email, String motDePasse, Role role) {
        if (!etudiantDao.existsByEmail(email)) {
            Etudiant etudiant = new Etudiant();
            etudiant.setNom(nom);
            etudiant.setPrenom(prenom);
            etudiant.setEmail(email);
            etudiant.setMotDePasse(passwordEncoder.encode(motDePasse));
            etudiant.setRole(role);
            etudiantDao.save(etudiant);
        }
    }

    public void initialiserEtudiants() {
        // Méthode obsolète : l'initialisation des étudiants est désormais gérée dans DataInitializer
    }



    public List<Etudiant> getEtudiants() {
        return etudiantDao.findAll();
    }

    public Etudiant getEtudiant(long id) {
        return etudiantDao.findById(id).orElseThrow(() -> new IllegalArgumentException(ETUDIANT_NON_TROUVE));
    }

    public List<Etudiant> getEtudiantsByCours(long idCours) {
        // Vérifie que le cours existe — sinon exception claire
        coursDao.findById(idCours)
                .orElseThrow(() -> new IllegalArgumentException("Cours introuvable avec l'id: " + idCours));

        // Récupère les étudiants, même si la liste est vide (pas une erreur côté front)
        return etudiantDao.findEtudiantsByCours(idCours);
    }

    public List<Cours> getCoursByEtudiant(long idEtudiant) {
        return etudiantDao.findById(idEtudiant)
                .map(Etudiant::getCours)
                .orElseThrow(() -> new IllegalArgumentException(ETUDIANT_NON_TROUVE));
    }

    public Etudiant ajouterEtudiant(Etudiant etudiant) {
        if (etudiant.getIdEtudiant() != null && etudiantDao.existsById(etudiant.getIdEtudiant())) {
            throw new IllegalArgumentException("Cet étudiant existe déjà !");
        }
        return etudiantDao.save(etudiant);
    }

    public Etudiant modifierEtudiant(Etudiant etudiantModifie) {
        Etudiant etudiantExistant = etudiantDao.findById(etudiantModifie.getIdEtudiant())
                .orElseThrow(() -> new IllegalArgumentException("Étudiant non trouvé"));

        etudiantExistant.setNom(etudiantModifie.getNom());
        etudiantExistant.setPrenom(etudiantModifie.getPrenom());
        etudiantExistant.setEmail(etudiantModifie.getEmail());
        etudiantExistant.setMotDePasse(etudiantModifie.getMotDePasse());



        return etudiantDao.save(etudiantExistant);
    }

    public void supprimerEtudiant(long idEtudiant) {
        if (!etudiantDao.existsById(idEtudiant)) {
            throw new IllegalArgumentException(ETUDIANT_NON_TROUVE);
        }
        etudiantDao.deleteById(idEtudiant);
    }

    public List<Note> consulterNotes(long etudiantId) {
        return etudiantDao.getNotes(etudiantId);
    }

    public void ajouterCoursAEtudiant(long idEtudiant, List<Long> idCoursList) {
        Etudiant etudiant = etudiantDao.findById(idEtudiant)
                .orElseThrow(() -> new IllegalArgumentException(ETUDIANT_NON_TROUVE));

        idCoursList.forEach(idCours -> {
            Cours cours = coursDao.findById(idCours)
                    .orElseThrow(() -> new IllegalArgumentException("Cours inexistant, id: " + idCours));
            if (!etudiant.getCours().contains(cours)) {
                etudiant.getCours().add(cours);
                cours.getEtudiants().add(etudiant);
            }
        });

        etudiantDao.save(etudiant);
    }

    public List<Etudiant> getEtudiantsDesCoursDuProfesseur(String emailProfesseur) {
        Etudiant professeur = etudiantDao.findByEmail(emailProfesseur)
                .orElseThrow(() -> new IllegalArgumentException("Professeur non trouvé"));

        List<Cours> coursDuProf = coursDao.findAll().stream()
                .filter(c -> c.getProfesseur() != null && c.getProfesseur().getEmail().equals(emailProfesseur))
                .toList();

        return coursDuProf.stream()
                .flatMap(cours -> cours.getEtudiants().stream())
                .distinct() // pour éviter les doublons si un étudiant est dans plusieurs cours du prof
                .toList();
    }
    public Optional<Etudiant> getByEmail(String email) {
        return etudiantDao.findByEmail(email);
    }

}
