package application.poudlard.testUnitaire;

import application.poudlard.controller.NoteController;
import application.poudlard.model.Note;
import application.poudlard.security.JwtAuthFilter;
import application.poudlard.security.JwtUtil;
import application.poudlard.service.NoteService;
import application.poudlard.service.EtudiantService;
import application.poudlard.dao.CoursDAO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(NoteController.class)
@AutoConfigureMockMvc(addFilters = false)
public class NoteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private NoteService noteService;

    @MockBean
    private EtudiantService etudiantService;

    @MockBean
    private CoursDAO coursDAO;

    @MockBean
    private JwtAuthFilter jwtAuthFilter;

    @MockBean
    private JwtUtil jwtUtil;

    @Test
    void testGetNoteById_shouldReturnNote() throws Exception {
        Note note = new Note();
        note.setIdNote(1);
        note.setValeur(18.5);

        when(noteService.getNoteById(1)).thenReturn(note);

        mockMvc.perform(get("/classes/notes/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idNote").value(1))
                .andExpect(jsonPath("$.valeur").value(18.5));
    }

    @Test
    void testAjouterNotePourEtudiant_shouldReturnCreatedNote() throws Exception {
        Note note = new Note();
        note.setIdNote(1);
        note.setValeur(16.0);

        when(noteService.ajouterNotePourEtudiantDansCours(eq(1), eq(1), any(Note.class))).thenReturn(note);

        mockMvc.perform(post("/classes/notes/cours/1/etudiants/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                        {
                          "idNote": 1,
                          "valeur": 16.0
                        }
                        """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idNote").value(1))
                .andExpect(jsonPath("$.valeur").value(16.0));
    }

    @Test
    void testModifierNote_shouldReturnUpdatedNote() throws Exception {
        Note updatedNote = new Note();
        updatedNote.setIdNote(1);
        updatedNote.setValeur(19.0);

        when(noteService.modifierNote(any(Note.class))).thenReturn(updatedNote);

        mockMvc.perform(put("/classes/notes/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                        {
                          "idNote": 1,
                          "valeur": 19.0
                        }
                        """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idNote").value(1))
                .andExpect(jsonPath("$.valeur").value(19.0));
    }

    @Test
    void testSupprimerNote_shouldReturnNoContent() throws Exception {
        Note note = new Note();
        note.setIdNote(1);

        when(noteService.getNoteById(1)).thenReturn(note);

        mockMvc.perform(delete("/classes/notes/1"))
                .andExpect(status().isNoContent());
    }
}
