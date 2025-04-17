package application.poudlard.service;

import application.poudlard.dao.CoursDAO;
import application.poudlard.dao.EtudiantDAO;
import application.poudlard.model.Cours;
import application.poudlard.model.Etudiant;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class CoursService {

    private final CoursDAO coursDao;
    private final EtudiantDAO etudiantDao;
    private static final String COURS_INTROUVABLE = "Cours non trouvé";
    private static final String ETUDIANT_INTROUVABLE = "Etudiant not found with id: ";

    public CoursService(CoursDAO coursDao, EtudiantDAO etudiantDAO) {
        this.coursDao = coursDao;
        this.etudiantDao = etudiantDAO;
    }
    public List<Cours> getCoursByProfesseur(String emailProfesseur) {
        Etudiant professeur = etudiantDao.findByEmail(emailProfesseur)
                .orElseThrow(() -> new IllegalArgumentException("Professeur non trouvé avec email : " + emailProfesseur));

        if (!"PROFESSEUR".equals(professeur.getRole().getNom())) {
            throw new IllegalArgumentException("L'utilisateur n'est pas un professeur");
        }

        return coursDao.findAll().stream()
                .filter(cours -> cours.getProfesseur() != null && cours.getProfesseur().getEmail().equals(emailProfesseur))
                .toList();
    }
    private void creerCoursSiAbsent(String intitule, String ref, String emailProfesseur, boolean option) {
        if (!coursDao.existsByRef(ref)) {
            Etudiant professeur = etudiantDao.findByEmail(emailProfesseur)
                    .orElseThrow(() -> new IllegalArgumentException("Professeur non trouvé avec email : " + emailProfesseur));

            if (!"PROFESSEUR".equals(professeur.getRole().getNom())) {
                throw new IllegalArgumentException("L'utilisateur avec email " + emailProfesseur + " n'est pas un professeur");
            }

            Cours cours = new Cours();
            cours.setIntitule(intitule);
            cours.setRef(ref);
            cours.setEstOptionnel(true);
            cours.setProfesseur(professeur);
            coursDao.save(cours);
        }
    }
    public void initialiserCours() {
        // Données initiales déplacées dans DataInitializer
    }


    private void ajouterCoursSiProfExiste(String nom, String code, String emailProf, boolean obligatoire) {
        Optional<Etudiant> profOpt = etudiantDao.findByEmail(emailProf);
        if (profOpt.isPresent()) {
            creerCoursSiAbsent(nom, code, emailProf, obligatoire);
        } else {
            System.out.println("⚠️ Professeur introuvable pour le cours '" + nom + "' (" + emailProf + "). Cours non créé.");
        }
    }

    // Méthodes utilitaires pour réduire le code dupliqué
    private Cours findCoursByIdOrThrow(int id) {
        return coursDao.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(COURS_INTROUVABLE));
    }

    private Etudiant findEtudiantByIdOrThrow(int id) {
        return etudiantDao.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(ETUDIANT_INTROUVABLE + id));
    }

    public Cours getCoursById(int id) {
        return findCoursByIdOrThrow(id);
    }

    public List<Cours> getAllCours() {
        return coursDao.findAll();
    }

    public void createCours(Cours cours) {
        Optional<Cours> optionalCours = coursDao.findById(cours.getIdCours());
        if (optionalCours.isPresent()) {
            throw new IllegalArgumentException("Ce cours existe déjà");
        }
        coursDao.save(cours);
    }

    public Cours updateCours(Cours cours) {
        Cours existingCours = findCoursByIdOrThrow(cours.getIdCours());
        existingCours.setIntitule(cours.getIntitule());
        existingCours.setRef(cours.getRef());
        existingCours.setEtudiants(cours.getEtudiants());
        existingCours.setNotes(cours.getNotes());

        return coursDao.save(existingCours);
    }

    public void supprimerCours(int idCours) {
        Cours cours = findCoursByIdOrThrow(idCours);
        coursDao.delete(cours);
    }

    public List<Etudiant> ajouterEtudiantsAuCours(int idCours, List<Integer> idEtudiants) {
        Cours cours = findCoursByIdOrThrow(idCours);

        for (int idEtudiant : idEtudiants) {
            Etudiant etudiant = findEtudiantByIdOrThrow(idEtudiant);

            if (!cours.getEtudiants().contains(etudiant)) {
                cours.getEtudiants().add(etudiant);


                if (etudiant.getCours() == null) {
                    etudiant.setCours(new ArrayList<>());
                }

                etudiant.getCours().add(cours);
            }
        }

        coursDao.save(cours);
        etudiantDao.saveAll(cours.getEtudiants());

        return new ArrayList<>(cours.getEtudiants());
    }

}
