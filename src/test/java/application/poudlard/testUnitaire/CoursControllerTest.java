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
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import org.springframework.http.MediaType;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(CoursController.class)
@AutoConfigureMockMvc(addFilters = false) // désactive les filtres de sécurité Spring Security
class CoursControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CoursService coursService;

    @MockBean
    private NoteService noteService;

    @MockBean
    private EtudiantService etudiantService;

    // 👇 Ajout pour que Spring ne plante pas à cause de JwtAuthFilter ou JwtUtil
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
        mockCours.setIdCours(1);
        mockCours.setIntitule("Défense contre les forces du mal");

        Mockito.when(coursService.getCoursById(1)).thenReturn(mockCours);

        mockMvc.perform(get("/poudlard/cours/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idCours").value(1))
                .andExpect(jsonPath("$.intitule").value("Défense contre les forces du mal"));
    }
    @Test
    void testAjouterCours_shouldReturnCreated() throws Exception {
        Cours cours = new Cours();
        cours.setIntitule("Potions");

        ObjectMapper objectMapper = new ObjectMapper();
        String coursJson = objectMapper.writeValueAsString(cours);

        mockMvc.perform(post("/poudlard/cours")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(coursJson))
                .andExpect(status().isCreated());
    }
    @Test
    void testModifierCours_shouldReturnUpdatedCours() throws Exception {
        Cours updatedCours = new Cours();
        updatedCours.setIdCours(1);
        updatedCours.setIntitule("Métamorphose");

        ObjectMapper objectMapper = new ObjectMapper();
        String updatedJson = objectMapper.writeValueAsString(updatedCours);

        Mockito.when(coursService.updateCours(Mockito.any())).thenReturn(updatedCours);

        mockMvc.perform(put("/poudlard/cours/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updatedJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idCours").value(1))
                .andExpect(jsonPath("$.intitule").value("Métamorphose"));
    }
}
