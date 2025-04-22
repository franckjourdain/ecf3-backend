package application.poudlard.testUnitaire;

import application.poudlard.controller.CoursController;
import application.poudlard.model.Cours;
import application.poudlard.security.JwtAuthFilter;
import application.poudlard.security.JwtUtil;
import application.poudlard.service.CoursService;
import application.poudlard.service.NoteService;
import application.poudlard.service.EtudiantService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CoursController.class)
@AutoConfigureMockMvc(addFilters = false)
class CoursControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CoursService coursService;

    @MockBean
    private NoteService noteService;

    @MockBean
    private EtudiantService etudiantService;

    @MockBean
    private JwtAuthFilter jwtAuthFilter;

    @MockBean
    private JwtUtil jwtUtil;

    @Test
    void testGetAllCours_shouldReturnOk() throws Exception {
        mockMvc.perform(get("/poudlard/cours/all"))
                .andExpect(status().isOk());
    }

    @Test
    void testGetCoursById_shouldReturnCours() throws Exception {
        Cours mockCours = new Cours();
        mockCours.setIdCours(1L);
        mockCours.setIntitule("Défense contre les forces du mal");

        Mockito.when(coursService.getCoursById(1L)).thenReturn(mockCours);

        mockMvc.perform(get("/poudlard/cours/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idCours").value(1))
                .andExpect(jsonPath("$.intitule").value("Défense contre les forces du mal"));
    }

    @Test
    void testAjouterCours_shouldReturnCreated() throws Exception {
        Cours cours = new Cours();
        cours.setIntitule("Potions");

        String coursJson = new ObjectMapper().writeValueAsString(cours);

        mockMvc.perform(post("/poudlard/cours")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(coursJson))
                .andExpect(status().isCreated());
    }

    @Test
    void testModifierCours_shouldReturnUpdatedCours() throws Exception {
        Cours updatedCours = new Cours();
        updatedCours.setIdCours(1L);
        updatedCours.setIntitule("Métamorphose");

        String updatedJson = new ObjectMapper().writeValueAsString(updatedCours);

        Mockito.when(coursService.updateCours(Mockito.any(Cours.class))).thenReturn(updatedCours);

        mockMvc.perform(put("/poudlard/cours/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updatedJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idCours").value(1))
                .andExpect(jsonPath("$.intitule").value("Métamorphose"));
    }
}