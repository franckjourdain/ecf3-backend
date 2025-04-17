package application.poudlard.data;

import application.poudlard.dao.CoursDAO;
import application.poudlard.dao.EtudiantDAO;
import application.poudlard.dao.RoleRepository;
import application.poudlard.model.Cours;
import application.poudlard.model.Etudiant;
import application.poudlard.model.Role;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@Profile("!test") // NE S'EXÉCUTE PAS SI LE PROFIL ACTIF EST 'test'
public class DataInitializer implements CommandLineRunner {

    private final EtudiantDAO etudiantDao;
    private final CoursDAO coursDao;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(EtudiantDAO etudiantDao, CoursDAO coursDao, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.etudiantDao = etudiantDao;
        this.coursDao = coursDao;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        initialiserRoles();
        initialiserEtudiantsEtProfs();
        initialiserCours();
    }

    private void initialiserRoles() {
        if (roleRepository.count() == 0) {
            roleRepository.save(new Role("ETUDIANT"));
            roleRepository.save(new Role("PROFESSEUR"));
            roleRepository.save(new Role("ADMIN"));
        }
    }

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

    private void initialiserEtudiantsEtProfs() {
        Role etudiantRole = roleRepository.findByNom("ETUDIANT").orElse(null);
        Role professeurRole = roleRepository.findByNom("PROFESSEUR").orElse(null);
        Role adminRole = roleRepository.findByNom("ADMIN").orElse(null);

        if (etudiantRole == null || professeurRole == null || adminRole == null) {
            System.out.println("️ Un ou plusieurs rôles manquent, annulation de l'initialisation.");
            return;
        }

        // Étudiants
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

        // Professeurs
        creerEtudiantSiAbsent("Dupont", "Jean", "jean.dupont@poudlard.com", "1001", professeurRole);
        creerEtudiantSiAbsent("Micheline", "Jeanne", "jeanne.micheline@poudlard.com", "1002", professeurRole);
        creerEtudiantSiAbsent("Smith", "Mickael", "mickael.smith@poudlard.com", "1003", professeurRole);
        creerEtudiantSiAbsent("Dufour", "Eric", "eric.dufour@poudlard.com", "1004", professeurRole);
        creerEtudiantSiAbsent("Abgrall", "François", "francois.abgrall@poudlard.com", "1005", professeurRole);
        creerEtudiantSiAbsent("Cavarec", "Jam", "jam.cavarec@poudlard.com", "1006", professeurRole);
        creerEtudiantSiAbsent("Tanguy", "René", "rene.tanguy@poudlard.com", "1007", professeurRole);

        // Admin
        creerEtudiantSiAbsent("Admin", "Super", "admin@poudlard.com", "root", adminRole);
    }

    private void creerCoursSiAbsent(String intitule, String ref, String emailProfesseur, boolean option) {
        if (!coursDao.existsByRef(ref)) {
            Etudiant prof = etudiantDao.findByEmail(emailProfesseur).orElse(null);

            if (prof == null || !"PROFESSEUR".equals(prof.getRole().getNom())) {
                System.out.println(" Professeur non trouvé ou invalide pour le cours : " + intitule);
                return;
            }

            Cours cours = new Cours();
            cours.setIntitule(intitule);
            cours.setRef(ref);
            cours.setEstOptionnel(option); // ✅ corrigé ici
            cours.setProfesseur(prof);
            coursDao.save(cours);
        }
    }

    private void initialiserCours() {
        creerCoursSiAbsent("Français", "FR", "jean.dupont@poudlard.com", false);
        creerCoursSiAbsent("Mathematique", "MATH", "jeanne.micheline@poudlard.com", false);
        creerCoursSiAbsent("Anglais", "ANG", "mickael.smith@poudlard.com", false);
        creerCoursSiAbsent("Chimie", "CHIM", "eric.dufour@poudlard.com", false);
        creerCoursSiAbsent("Physique", "PHYS", "francois.abgrall@poudlard.com", false);
        creerCoursSiAbsent("Informatique", "INFO", "jam.cavarec@poudlard.com", false);
        creerCoursSiAbsent("Sport", "SP", "rene.tanguy@poudlard.com", true);
    }
}
