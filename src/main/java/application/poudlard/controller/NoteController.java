package application.poudlard.controller;

import application.poudlard.dao.CoursDAO;
import application.poudlard.dao.EtudiantDAO;
import application.poudlard.model.Cours;
import application.poudlard.model.Etudiant;
import application.poudlard.model.Note;
import application.poudlard.service.EtudiantService;
import application.poudlard.service.NoteService;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/classes/notes")
//@CrossOrigin(origins = "*")
public class NoteController {

    private final NoteService noteService;
    private final EtudiantService etudiantService;
    private final CoursDAO coursDAO;

    public NoteController(NoteService noteService, EtudiantService etudiantService, CoursDAO coursDAO) {
        this.noteService = noteService;
        this.etudiantService = etudiantService;
        this.coursDAO = coursDAO;
    }

    // 🔍 Obtenir toutes les notes d’un étudiant
    @GetMapping("/etudiant/{idEtudiant}")
    @PreAuthorize("hasAnyRole('PROFESSEUR', 'ADMIN')")
    public List<Note> getNotesByEtudiant(@PathVariable Long idEtudiant) {
        return noteService.getNotesByEtudiant(idEtudiant);
    }

    // ➕ Ajouter une note pour un étudiant dans un cours
    @PostMapping("/cours/{idCours}/etudiants/{idEtudiant}")
    @PreAuthorize("hasAnyRole('PROFESSEUR', 'ADMIN')")
    public Note ajouterNotePourEtudiant(@PathVariable Long idCours,
                                        @PathVariable Long idEtudiant,
                                        @RequestBody Note note) {
        return noteService.ajouterNotePourEtudiantDansCours(idCours, idEtudiant, note);
    }

    // ✏️ Modifier une note existante
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('PROFESSEUR', 'ADMIN')")
    public Note modifierNote(@PathVariable Long id, @RequestBody Note note) {
        note.setIdNote(id);
        return noteService.modifierNote(note);
    }

    // ❌ Supprimer une note
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMIN')")
    public void supprimerNote(@PathVariable Long id) {
        Note note = noteService.getNoteById(id);
        noteService.supprimerNote(note);
    }

    // 🔍 Obtenir toutes les notes d’un cours
    @GetMapping("/cours/{idCours}")
    @PreAuthorize("hasAnyRole('PROFESSEUR', 'ADMIN')")
    public List<Note> getNotesByCours(@PathVariable Long idCours) {
        return noteService.getNotesByCours(idCours);
    }

    // 🔍 Obtenir les notes d’un étudiant pour un cours
    @GetMapping("/etudiant/{idEtudiant}/cours/{idCours}")
    @PreAuthorize("hasAnyRole('PROFESSEUR', 'ADMIN')")
    public List<Note> getNotesByEtudiantAndCours(@PathVariable Long idEtudiant, @PathVariable Long idCours) {
        return noteService.getNotesByEtudiantAndCours(idEtudiant, idCours);
    }

    // 🔍 Obtenir une note par ID
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('PROFESSEUR', 'ADMIN')")
    public Note getNoteById(@PathVariable Long id) {
        return noteService.getNoteById(id);
    }
}
