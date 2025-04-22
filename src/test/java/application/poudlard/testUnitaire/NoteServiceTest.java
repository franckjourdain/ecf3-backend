package application.poudlard.testUnitaire;

import application.poudlard.dao.CoursDAO;
import application.poudlard.dao.EtudiantDAO;
import application.poudlard.dao.NoteDAO;
import application.poudlard.model.Cours;
import application.poudlard.model.Etudiant;
import application.poudlard.model.Note;
import application.poudlard.service.NoteService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class NoteServiceTest {

    private NoteDAO noteDao;
    private CoursDAO coursDao;
    private EtudiantDAO etudiantDao;
    private NoteService noteService;

    @BeforeEach
    void setUp() {
        noteDao = mock(NoteDAO.class);
        coursDao = mock(CoursDAO.class);
        etudiantDao = mock(EtudiantDAO.class);
        noteService = new NoteService(noteDao, coursDao, etudiantDao);
    }

    @Test
    void testAjouterNotePourEtudiantDansCours_OK() {
        // Arrange
        Note note = new Note();
        note.setIntitule("Java");
        note.setValeur(17.5);

        Cours cours = new Cours();
        cours.setIdCours(1L);

        Etudiant etudiant = new Etudiant();
        etudiant.setIdEtudiant(2L);

        when(coursDao.findById(1L)).thenReturn(Optional.of(cours));
        when(etudiantDao.findById(2L)).thenReturn(Optional.of(etudiant));
        when(noteDao.save(any(Note.class))).thenAnswer(i -> i.getArgument(0));

        // Act
        Note resultat = noteService.ajouterNotePourEtudiantDansCours(1L, 2L, note);

        // Assert
        assertEquals("Java", resultat.getIntitule());
        assertEquals(17.5, resultat.getValeur());
        assertEquals(cours, resultat.getCours());
        assertEquals(etudiant, resultat.getEtudiant());
    }

    @Test
    void testAjouterNotePourEtudiantDansCours_EtudiantNonTrouve() {
        when(coursDao.findById(1L)).thenReturn(Optional.of(new Cours()));
        when(etudiantDao.findById(2L)).thenReturn(Optional.empty());

        Note note = new Note();
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                noteService.ajouterNotePourEtudiantDansCours(1L, 2L, note));

        assertEquals("Cet étudiant n'existe pas", exception.getMessage());
    }

    @Test
    void testAjouterNotePourEtudiantDansCours_CoursNonTrouve() {
        when(coursDao.findById(1L)).thenReturn(Optional.empty());

        Note note = new Note();
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                noteService.ajouterNotePourEtudiantDansCours(1L, 2L, note));

        assertEquals("Ce cours n'existe pas", exception.getMessage());
    }

    @Test
    void testModifierNote_NoteExistante() {
        Note ancienne = new Note();
        ancienne.setIdNote(1L);
        ancienne.setValeur(12.0);

        Note modifiee = new Note();
        modifiee.setIdNote(1L);
        modifiee.setValeur(18.0);

        when(noteDao.findById(1L)).thenReturn(Optional.of(ancienne));
        when(noteDao.save(any())).thenAnswer(i -> i.getArgument(0));

        Note result = noteService.modifierNote(modifiee);
        assertEquals(18.0, result.getValeur());
    }

    @Test
    void testModifierNote_NonTrouvee() {
        Note note = new Note();
        note.setIdNote(999L);

        when(noteDao.findById(999L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> noteService.modifierNote(note));
    }

    @Test
    void testGetNoteById_Success() {
        Note note = new Note();
        note.setIdNote(5L);
        when(noteDao.findById(5L)).thenReturn(Optional.of(note));

        Note resultat = noteService.getNoteById(5L);
        assertEquals(5L, resultat.getIdNote());
    }

    @Test
    void testGetNoteById_NonTrouvee() {
        when(noteDao.findById(10L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> noteService.getNoteById(10L));
    }
}
