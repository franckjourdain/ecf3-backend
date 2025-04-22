package application.poudlard.testUnitaire;

import application.poudlard.controller.EtudiantController;
import application.poudlard.model.Etudiant;
import application.poudlard.security.JwtAuthFilter;
import application.poudlard.security.JwtUtil;
import application.poudlard.service.EtudiantService;
import application.poudlard.service.NoteService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(EtudiantController.class)
@AutoConfigureMockMvc(addFilters = false)
public class EtudiantControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EtudiantService etudiantService;

    @MockBean
    private NoteService noteService;

    @MockBean
    private JwtAuthFilter jwtAuthFilter;

    @MockBean
    private JwtUtil jwtUtil;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testGetAllEtudiants_shouldReturnOk() throws Exception {
        Mockito.when(etudiantService.getEtudiants()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/poudlard/etudiant/all"))
                .andExpect(status().isOk());
    }

    @Test
    void testAjouterEtudiant_shouldReturnCreatedEtudiant() throws Exception {
        Etudiant mockEtudiant = new Etudiant();
        mockEtudiant.setIdEtudiant(1L); // Changement ici
        mockEtudiant.setNom("Harry Potter");

        Mockito.when(etudiantService.ajouterEtudiant(Mockito.any(Etudiant.class))).thenReturn(mockEtudiant);

        mockMvc.perform(post("/poudlard/etudiant")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(mockEtudiant)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idEtudiant").value(1))
                .andExpect(jsonPath("$.nom").value("Harry Potter"));
    }

    @Test
    void testGetEtudiantById_shouldReturnEtudiant() throws Exception {
        Etudiant mockEtudiant = new Etudiant();
        mockEtudiant.setIdEtudiant(2L); // Changement ici
        mockEtudiant.setNom("Hermione Granger");

        Mockito.when(etudiantService.getEtudiant(2L)).thenReturn(mockEtudiant); // Changement ici

        mockMvc.perform(get("/poudlard/etudiant/2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idEtudiant").value(2))
                .andExpect(jsonPath("$.nom").value("Hermione Granger"));
    }

    @Test
    void testModifierEtudiant_shouldReturnUpdatedEtudiant() throws Exception {
        Etudiant etudiantModifie = new Etudiant();
        etudiantModifie.setIdEtudiant(1L); // Changement ici
        etudiantModifie.setNom("Neville Londubat");

        Mockito.when(etudiantService.modifierEtudiant(Mockito.any(Etudiant.class))).thenReturn(etudiantModifie);

        mockMvc.perform(put("/poudlard/etudiant/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(etudiantModifie)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idEtudiant").value(1))
                .andExpect(jsonPath("$.nom").value("Neville Londubat"));
    }

    @Test
    void testSupprimerEtudiant_shouldReturnNoContent() throws Exception {
        Mockito.doNothing().when(etudiantService).supprimerEtudiant(1L); // Changement ici

        mockMvc.perform(delete("/poudlard/etudiant/1"))
                .andExpect(status().isNoContent());
    }
}
