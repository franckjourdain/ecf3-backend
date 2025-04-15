package application.poudlard.service;

import application.poudlard.dao.CoursDAO;
import application.poudlard.dao.EtudiantDAO;
import application.poudlard.dao.NoteDAO;
import application.poudlard.model.Cours;
import application.poudlard.model.Etudiant;
import application.poudlard.model.Note;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class NoteService {

    private final NoteDAO noteDao;
    private final CoursDAO coursDAO;
    private final EtudiantDAO etudiantDao;

    public NoteService(NoteDAO noteDAO, CoursDAO coursDAO, EtudiantDAO etudiantDao) {
        this.noteDao = noteDAO;
        this.coursDAO = coursDAO;
        this.etudiantDao = etudiantDao;
    }

    public List<Note> getNotesByCours(int idCours) {
        return noteDao.findNotesByCours(idCours);
    }

    public List<Note> getNotesByEtudiant(int idEtudiant) {
        return noteDao.findNotesByEtudiant(idEtudiant);
    }

    public List<Note> getNotesByEtudiantAndCours(int etudiantId, int coursId) {
        return noteDao.findNotesByEtudiantAndCours(etudiantId, coursId);
    }

    public void ajouterNote(Note note, Authentication authentication) {
        if (noteDao.existsById(note.getIdNote())) {
            throw new IllegalArgumentException("Cette note existe déjà");
        }

        // 🔍 Récupérer l'étudiant connecté
        String email = authentication.getName();
        Etudiant etudiant = etudiantDao.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Étudiant non trouvé avec l'email : " + email));

        // 🔗 Lier l'étudiant à la note
        note.setEtudiant(etudiant);

        // 💾 Sauvegarder la note
        noteDao.save(note);
    }

    public Note ajouterNotePourEtudiantDansCours(int idCours, int idEtudiant, Note note) {
        Cours cours = validerCours(idCours);
        Etudiant etudiant = validerEtudiant(idEtudiant);
        note.setCours(cours);
        note.setEtudiant(etudiant);
        return noteDao.save(note);
    }

    public Note modifierNote(Note noteModifie) {
        Note note = noteDao.findById(noteModifie.getIdNote())
                .orElseThrow(() -> new IllegalArgumentException("Note non trouvée"));
        copierProprietes(noteModifie, note);
        return noteDao.save(note);
    }

    public void supprimerNote(Note note) {
        noteDao.delete(note);
    }

    private Cours validerCours(int idCours) {
        return coursDAO.findById(idCours)
                .orElseThrow(() -> new IllegalArgumentException("Ce cours n'existe pas"));
    }

    private Etudiant validerEtudiant(int idEtudiant) {
        return etudiantDao.findById(idEtudiant)
                .orElseThrow(() -> new IllegalArgumentException("Cet étudiant n'existe pas"));
    }

    private void copierProprietes(Note source, Note cible) {
        cible.setValeur(source.getValeur());
        cible.setEtudiant(source.getEtudiant());
        cible.setCours(source.getCours());
        cible.setIntitule(source.getIntitule());
    }

    public Note getNoteById(int id) {
        return noteDao.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Note non trouvée avec id : " + id));
    }

    public Note ajouterNote(Note note) {
        return note;
    }
}
