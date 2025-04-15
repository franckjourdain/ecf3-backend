package application.poudlard.controller;

import application.poudlard.model.Cours;
import application.poudlard.model.Etudiant;
import application.poudlard.model.Note;
import application.poudlard.service.CoursService;
import application.poudlard.service.EtudiantService;
import application.poudlard.service.NoteService;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/poudlard/etudiant")
//@CrossOrigin(origins = "*")
public class EtudiantController {

    private final EtudiantService etudiantService;
    private final NoteService noteService;

    public EtudiantController(EtudiantService etudiantService, NoteService noteService) {
        this.etudiantService = etudiantService;
        this.noteService = noteService;
    }

    @GetMapping("/all")
    @PreAuthorize("hasAnyRole('PROFESSEUR', 'ADMIN')")
    public List<Etudiant> getAllEtudiants() {
        return etudiantService.getEtudiants();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('PROFESSEUR', 'ADMIN')")
    public Etudiant getEtudiant(@PathVariable int id) {
        return etudiantService.getEtudiant(id);
    }

    @GetMapping("{idEtudiant}/notes")
    @PreAuthorize("hasAnyRole('ETUDIANT', 'PROFESSEUR', 'ADMIN')")
    public List<Note> getNotesByEtudiant(@PathVariable int idEtudiant) {
        return noteService.getNotesByEtudiant(idEtudiant);
    }

    @GetMapping("{idEtudiant}/cours")
    @PreAuthorize("hasAnyRole('ETUDIANT', 'PROFESSEUR', 'ADMIN')")
    public List<Cours> getCoursByEtudiant(@PathVariable int idEtudiant) {
        return etudiantService.getCoursByEtudiant(idEtudiant);
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('PROFESSEUR', 'ADMIN')")
    public Etudiant ajouterEtudiant(@RequestBody Etudiant etudiant) {
        return etudiantService.ajouterEtudiant(etudiant);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('PROFESSEUR', 'ADMIN')")
    public Etudiant modifierEtudiant(@PathVariable int id, @RequestBody Etudiant etudiantModifie) {
        etudiantModifie.setIdEtudiant(id);
        return etudiantService.modifierEtudiant(etudiantModifie);
    }

    @PutMapping("/{idEtudiant}/addCours")
    @PreAuthorize("hasAnyRole('PROFESSEUR', 'ADMIN')")
    public void ajouterCoursAEtudiant(@PathVariable int idEtudiant, @RequestBody List<Integer> idCours) {
        etudiantService.ajouterCoursAEtudiant(idEtudiant, idCours);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMIN')")
    public void supprimerEtudiant(@PathVariable int id) {
        etudiantService.supprimerEtudiant(id);
    }

    @GetMapping("/professeur")
    @PreAuthorize("hasRole('PROFESSEUR')")
    public List<Etudiant> getEtudiantsDesCoursDuProfesseur(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new RuntimeException("Utilisateur non authentifié.");
        }
        String email = authentication.getName();
        return etudiantService.getEtudiantsDesCoursDuProfesseur(email);
    }
}
