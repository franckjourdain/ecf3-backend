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

    public CoursService(CoursDAO coursDao, EtudiantDAO etudiantDao) {
        this.coursDao = coursDao;
        this.etudiantDao = etudiantDao;
    }

    public List<Cours> getCoursByProfesseur(String emailProfesseur) {
        Etudiant professeur = etudiantDao.findByEmail(emailProfesseur)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Professeur non trouvé avec email : " + emailProfesseur));

        if (professeur.getRole() == null || !"PROFESSEUR".equals(professeur.getRole().getNom())) {
            throw new IllegalArgumentException("L'utilisateur n'est pas un professeur");
        }

        return coursDao.findAll().stream()
                .filter(c -> c.getProfesseur() != null
                        && emailProfesseur.equals(c.getProfesseur().getEmail()))
                .toList();
    }

    private Cours findCoursByIdOrThrow(Long id) {
        return coursDao.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(COURS_INTROUVABLE));
    }

    private Etudiant findEtudiantByIdOrThrow(Long id) {
        return etudiantDao.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(ETUDIANT_INTROUVABLE + id));
    }

    public Cours getCoursById(Long id) {
        return findCoursByIdOrThrow(id);
    }

    public List<Cours> getAllCours() {
        return coursDao.findAll();
    }

    public void createCours(Cours cours) {
        Optional<Cours> existing = coursDao.findById(cours.getIdCours());
        if (existing.isPresent()) {
            throw new IllegalArgumentException("Ce cours existe déjà");
        }
        coursDao.save(cours);
    }

    public Cours updateCours(Cours cours) {
        Cours existing = findCoursByIdOrThrow(cours.getIdCours());
        existing.setIntitule(cours.getIntitule());
        existing.setRef(cours.getRef());
        existing.setEstOptionnel(cours.isEstOptionnel());
        existing.setProfesseur(cours.getProfesseur());
        existing.setEtudiants(cours.getEtudiants());
        existing.setNotes(cours.getNotes());
        return coursDao.save(existing);
    }

    public void supprimerCours(Long id) {
        Cours cours = findCoursByIdOrThrow(id);
        coursDao.delete(cours);
    }

    public List<Etudiant> ajouterEtudiantsAuCours(Long idCours, List<Long> idEtudiants) {
        Cours cours = findCoursByIdOrThrow(idCours);
        for (Long idEtudiant : idEtudiants) {
            Etudiant etu = findEtudiantByIdOrThrow(idEtudiant);
            if (!cours.getEtudiants().contains(etu)) {
                cours.getEtudiants().add(etu);
                etu.getCours().add(cours);
            }
        }
        coursDao.save(cours);
        etudiantDao.saveAll(cours.getEtudiants());
        return new ArrayList<>(cours.getEtudiants());
    }
}
