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

    // Initialiser des étudiants en BDD au lancement de l'application

    public void initialiserEtudiants() {
        Optional<Role> etudiantRoleOpt = roleRepository.findByNom("ETUDIANT");
        Optional<Role> professeurRoleOpt = roleRepository.findByNom("PROFESSEUR");
        Optional<Role> adminRoleOpt = roleRepository.findByNom("ADMIN");

        // Si au moins un rôle est manquant, on interrompt l'initialisation pour éviter une exception
        if (etudiantRoleOpt.isEmpty() || professeurRoleOpt.isEmpty() || adminRoleOpt.isEmpty()) {
            System.out.println("⚠️ Initialisation annulée : un ou plusieurs rôles sont manquants en base.");
            return;
        }

        Role etudiantRole = etudiantRoleOpt.get();
        Role professeurRole = professeurRoleOpt.get();
        Role adminRole = adminRoleOpt.get();

        // Étudiants avec le rôle "ETUDIANT"
        creerEtudiantSiAbsent("Gueguen", "Yvon", "yvon.gueguen@gmail.com", "0001", etudiantRole);
        creerEtudiantSiAbsent("Cadiou", "Pierre", "pierre.cadiou@gmail.com", "0002", etudiantRole);
        creerEtudiantSiAbsent("Corre", "Jacques", "jacques.corre@gmail.com", "0003", etudiantRole);
        creerEtudiantSiAbsent("Smith", "Paul", "paul.smith@gmail.com", "0004", etudiantRole);
        creerEtudiantSiAbsent("Moulin", "Marie", "marie.moulin@gmail.com", "0005", etudiantRole);
        creerEtudiantSiAbsent("Jacopin", "Helène", "helene.jacopin@gmail.com", "0006", etudiantRole);
        creerEtudiantSiAbsent("Troadec", "Jeanne", "jeanne.troadec@gmail.com", "0007", etudiantRole);
        creerEtudiantSiAbsent("Jacopin", "Vincent", "vincent.jacopin@gmail.com", "0008", etudiantRole);
        creerEtudiantSiAbsent("Jestin", "Pauline", "pauline.jestin@gmail.com", "0009", etudiantRole);
        creerEtudiantSiAbsent("Carnac", "Jennifer", "jennifer.carnac@gmail.com", "0010", etudiantRole);
        creerEtudiantSiAbsent("Mazé", "Sarah", "sarah.mazé@gmail.com", "0011", etudiantRole);
        creerEtudiantSiAbsent("Bacuet", "Matthieu", "matthieu.bacuet@gmail.com", "0012", etudiantRole);

        // Professeurs avec le rôle "PROFESSEUR"
        creerEtudiantSiAbsent("Dupont", "Jean", "jean.dupont@poudlard.com", "1001", professeurRole);
        creerEtudiantSiAbsent("Micheline", "Jeanne", "jeanne.micheline@poudlard.com", "1002", professeurRole);
        creerEtudiantSiAbsent("Smith", "Mickael", "mickael.smith@poudlard.com", "1003", professeurRole);
        creerEtudiantSiAbsent("Dufour", "Eric", "eric.dufour@poudlard.com", "1004", professeurRole);
        creerEtudiantSiAbsent("Abgrall", "François", "francois.abgrall@poudlard.com", "1005", professeurRole);
        creerEtudiantSiAbsent("Cavarec", "Jam", "jam.cavarec@poudlard.com", "1006", professeurRole);
        creerEtudiantSiAbsent("Tanguy", "René", "rene.tanguy@poudlard.com", "1007", professeurRole);

        // Administrateur avec le rôle "ADMIN"
        creerEtudiantSiAbsent("Admin", "Super", "admin@poudlard.com", "root", adminRole);
    }



    public List<Etudiant> getEtudiants() {
        return etudiantDao.findAll();
    }

    public Etudiant getEtudiant(int id) {
        return etudiantDao.findById(id).orElseThrow(() -> new IllegalArgumentException(ETUDIANT_NON_TROUVE));
    }

    public List<Etudiant> getEtudiantsByCours(int idCours) {
        List<Etudiant> etudiants = etudiantDao.findEtudiantsByCours(idCours);
        if (etudiants.isEmpty()) {
            throw new IllegalArgumentException("Cours inexistant ou aucun étudiant trouvé pour le cours avec id: " + idCours);
        }
        return etudiants;
    }

    public List<Cours> getCoursByEtudiant(int idEtudiant) {
        return etudiantDao.findById(idEtudiant)
                .map(Etudiant::getCours)
                .orElseThrow(() -> new IllegalArgumentException(ETUDIANT_NON_TROUVE));
    }

    public Etudiant ajouterEtudiant(Etudiant etudiant) {
        if (!etudiantDao.existsById(etudiant.getIdEtudiant())) {
            return etudiantDao.save(etudiant);
        }
        throw new IllegalArgumentException("Cet étudiant existe déjà !");
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

    public void supprimerEtudiant(int idEtudiant) {
        if (!etudiantDao.existsById(idEtudiant)) {
            throw new IllegalArgumentException(ETUDIANT_NON_TROUVE);
        }
        etudiantDao.deleteById(idEtudiant);
    }

    public List<Note> consulterNotes(int etudiantId) {
        return etudiantDao.getNotes(etudiantId);
    }

    public void ajouterCoursAEtudiant(int idEtudiant, List<Integer> idCoursList) {
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

}
