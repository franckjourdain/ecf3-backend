package application.poudlard.controller;

import application.poudlard.model.Cours;
import application.poudlard.model.Etudiant;
import application.poudlard.model.Note;
import application.poudlard.service.CoursService;
import application.poudlard.service.EtudiantService;
import application.poudlard.service.NoteService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/poudlard/cours")
public class CoursController {

    private final CoursService coursService;
    private final EtudiantService etudiantService;
    private final NoteService noteService;

    public CoursController(CoursService coursService,
                           EtudiantService etudiantService,
                           NoteService noteService) {
        this.coursService = coursService;
        this.etudiantService = etudiantService;
        this.noteService = noteService;
    }

    @GetMapping("/all")
    @PreAuthorize("hasAnyRole('PROFESSEUR','ADMIN')")
    public List<Cours> getAllCours() {
        return coursService.getAllCours();
    }

    @GetMapping("/{idCours}")
    @PreAuthorize("hasAnyRole('PROFESSEUR','ADMIN')")
    public Cours getCoursById(@PathVariable Long idCours) {
        return coursService.getCoursById(idCours);
    }

    @GetMapping("/{idCours}/etudiants")
    @PreAuthorize("hasAnyRole('PROFESSEUR','ADMIN')")
    public List<Etudiant> getEtudiantsByCours(@PathVariable Long idCours) {
        return etudiantService.getEtudiantsByCours(idCours.longValue());
    }

    @GetMapping("/{id}/Etudiants")
    public ResponseEntity<Void> redirectToLowercase(@PathVariable Long id) {
        return ResponseEntity
                .status(HttpStatus.MOVED_PERMANENTLY)
                .header("Location", "/" + id + "/etudiants")
                .build();
    }

    @GetMapping("/{idCours}/notes")
    @PreAuthorize("hasAnyRole('PROFESSEUR','ADMIN')")
    public List<Note> getNotesByCours(@PathVariable Long idCours) {
        return noteService.getNotesByCours(idCours);
    }

    @PostMapping("/{idCours}/{idEtudiant}/notes")
    @PreAuthorize("hasAnyRole('PROFESSEUR','ADMIN')")
    public Note ajouterNoteAEtudiant(@PathVariable Long idCours,
                                     @PathVariable Long idEtudiant,
                                     @RequestBody Note note) {
        return noteService.ajouterNotePourEtudiantDansCours(idCours, idEtudiant, note);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ADMIN')")
    public void ajouterCours(@RequestBody Cours cours) {
        coursService.createCours(cours);
    }

    @PutMapping("/{idCours}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Cours> modifierCours(@PathVariable Long idCours,
                                               @RequestBody Cours cours) {
        cours.setIdCours(idCours);
        Cours updated = coursService.updateCours(cours);
        return ResponseEntity.ok(updated);
    }

    @PutMapping("/{idCours}/addEtudiants")
    @PreAuthorize("hasRole('ADMIN')")
    public List<Etudiant> ajouterEtudiantsAuCours(@PathVariable Long idCours,
                                                  @RequestBody List<Long> idEtudiants) {
        return coursService.ajouterEtudiantsAuCours(idCours, idEtudiants);
    }

    @DeleteMapping("/{idCours}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMIN')")
    public void supprimerCours(@PathVariable Long idCours) {
        coursService.supprimerCours(idCours);
    }

    @GetMapping("/professeur")
    @PreAuthorize("hasRole('PROFESSEUR')")
    public ResponseEntity<?> getCoursDuProfesseurConnecte(Authentication auth) {
        String email = auth.getName();
        try {
            return ResponseEntity.ok(coursService.getCoursByProfesseur(email));
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Erreur serveur : " + e.getMessage());
        }
    }
}
