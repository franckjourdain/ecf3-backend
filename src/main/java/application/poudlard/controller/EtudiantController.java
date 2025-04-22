package application.poudlard.controller;

import application.poudlard.dao.EtudiantDAO;
import application.poudlard.dao.NoteDAO;
import application.poudlard.dto.NoteDTO;
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
import java.util.Optional;

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
    public Etudiant getEtudiant(@PathVariable long id) {
        return etudiantService.getEtudiant(id);
    }

    @GetMapping("/{idEtudiant}/notes")
    @PreAuthorize("hasAnyRole('ETUDIANT', 'PROFESSEUR', 'ADMIN')")
    public List<Note> getNotesByEtudiant(@PathVariable Long idEtudiant) {
        return noteService.getNotesByEtudiant(idEtudiant);
    }

    @GetMapping("/{idEtudiant}/cours")
    @PreAuthorize("hasAnyRole('ETUDIANT', 'PROFESSEUR', 'ADMIN')")
    public List<Cours> getCoursByEtudiant(@PathVariable long idEtudiant) {
        return etudiantService.getCoursByEtudiant(idEtudiant);
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('PROFESSEUR', 'ADMIN')")
    public Etudiant ajouterEtudiant(@RequestBody Etudiant etudiant) {
        return etudiantService.ajouterEtudiant(etudiant);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('PROFESSEUR', 'ADMIN')")
    public Etudiant modifierEtudiant(@PathVariable long id, @RequestBody Etudiant etudiantModifie) {
        etudiantModifie.setIdEtudiant(id);
        return etudiantService.modifierEtudiant(etudiantModifie);
    }

    @PutMapping("/{idEtudiant}/addCours")
    @PreAuthorize("hasAnyRole('PROFESSEUR', 'ADMIN')")
    public void ajouterCoursAEtudiant(@PathVariable long idEtudiant, @RequestBody List<Long> idCours) {
        etudiantService.ajouterCoursAEtudiant(idEtudiant, idCours);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMIN')")
    public void supprimerEtudiant(@PathVariable long id) {
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

    @GetMapping("/me")
    public ResponseEntity<Etudiant> getMonProfil(Authentication authentication) {
        String email = authentication.getName(); // récupère l'email depuis le token JWT
        Optional<Etudiant> etudiant = etudiantService.getByEmail(email);
        return etudiant.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    @GetMapping("/me/notes")
    @PreAuthorize("hasAnyRole('ETUDIANT', 'PROFESSEUR', 'ADMIN')")
    public ResponseEntity<List<NoteDTO>> getMesNotes(Authentication authentication) {
        String email = authentication.getName();
        Optional<Etudiant> etudiantOpt = etudiantService.getByEmail(email);
        if (etudiantOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Etudiant etudiant = etudiantOpt.get();
        List<NoteDTO> notesDTO = etudiant.getNotes().stream()
                .map(note -> new NoteDTO(
                        note.getIdNote(),
                        note.getValeur(),
                        note.getIntitule(),
                        note.getCours() != null ? note.getCours().getIntitule() : "Cours inconnu"
                ))
                .toList();

        return ResponseEntity.ok(notesDTO);
    }
}
