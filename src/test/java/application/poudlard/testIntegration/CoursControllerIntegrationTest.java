package application.poudlard.testIntegration;

import application.poudlard.dao.CoursDAO;
import application.poudlard.dao.EtudiantDAO;
import application.poudlard.dao.NoteDAO;
import application.poudlard.dao.RoleRepository;
import application.poudlard.model.Cours;
import application.poudlard.model.Etudiant;
import application.poudlard.model.Note;
import application.poudlard.model.Role;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;

import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@Sql(scripts = "/data-test.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class CoursControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CoursDAO coursDAO;

    @Autowired
    private EtudiantDAO etudiantDAO;

    @Autowired
    private NoteDAO noteDAO;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        noteDAO.deleteAll();
        coursDAO.deleteAll();
        etudiantDAO.deleteAll();
        roleRepository.deleteAll();

        Role profRole = new Role();
        profRole.setNom("PROFESSEUR");
        roleRepository.save(profRole);

        Etudiant prof = new Etudiant();
        prof.setNom("Professeur");
        prof.setPrenom("Test");
        prof.setEmail("prof@test.com");
        prof.setMotDePasse("test123");
        prof.setRole(profRole);
        etudiantDAO.save(prof);

        Cours cours = new Cours();
        cours.setIntitule("Biologie");
        cours.setRef("BIO");
        cours.setProfesseur(prof);
        coursDAO.save(cours);
    }

    @Test
    void testGetAllCours_shouldReturnCoursList() throws Exception {
        Optional<Etudiant> optionalProf = etudiantDAO.findByEmail("prof@test.com");
        Etudiant prof = optionalProf.orElseGet(() -> {
            Etudiant p = new Etudiant();
            p.setNom("Professeur");
            p.setPrenom("Test");
            p.setEmail("prof@test.com");
            p.setMotDePasse("test123");
            p.setRole(roleRepository.findByNom("PROFESSEUR").orElseThrow());
            return etudiantDAO.save(p);
        });

        Cours cours = new Cours();
        cours.setIntitule("Biologie");
        cours.setRef("BIO");
        cours.setProfesseur(prof);
        cours.setEstOptionnel(false);
        coursDAO.save(cours);

        mockMvc.perform(get("/poudlard/cours/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].intitule").value("Biologie"))
                .andExpect(jsonPath("$[0].ref").value("BIO"));
    }

    @Test
    void testGetCoursById_shouldReturnSpecificCours() throws Exception {
        Etudiant prof = etudiantDAO.findByEmail("prof@test.com").orElseGet(() -> {
            Etudiant p = new Etudiant();
            p.setNom("Professeur");
            p.setPrenom("Test");
            p.setEmail("prof@test.com");
            p.setMotDePasse("test123");
            p.setRole(roleRepository.findByNom("PROFESSEUR").orElseThrow());
            return etudiantDAO.save(p);
        });

        Cours cours = new Cours();
        cours.setIntitule("Astronomie");
        cours.setRef("ASTRO");
        cours.setProfesseur(prof);
        cours.setEstOptionnel(false);
        cours = coursDAO.save(cours);

        mockMvc.perform(get("/poudlard/cours/" + cours.getIdCours()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.intitule").value("Astronomie"))
                .andExpect(jsonPath("$.ref").value("ASTRO"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testAjouterCours_shouldCreateNewCours() throws Exception {
        Etudiant prof = etudiantDAO.findByEmail("prof2@test.com").orElseGet(() -> {
            Etudiant p = new Etudiant();
            p.setNom("Professeur2");
            p.setPrenom("Test2");
            p.setEmail("prof2@test.com");
            p.setMotDePasse("test123");
            p.setRole(roleRepository.findByNom("PROFESSEUR").orElseThrow());
            return etudiantDAO.save(p);
        });

        Cours cours = new Cours();
        cours.setIntitule("Sortilèges");
        cours.setRef("SORT");
        cours.setProfesseur(prof);
        cours.setEstOptionnel(false);

        mockMvc.perform(post("/poudlard/cours")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(cours)))
                .andExpect(status().isCreated());

        List<Cours> coursList = coursDAO.findAll();
        assertThat(coursList).anyMatch(c -> c.getRef().equals("SORT") && c.getIntitule().equals("Sortilèges"));
    }

    @Test
    void testGetCoursById_shouldReturnCours() throws Exception {
        Role roleProf = roleRepository.findByNom("PROFESSEUR").orElseGet(() -> {
            Role r = new Role();
            r.setNom("PROFESSEUR");
            return roleRepository.save(r);
        });

        Etudiant prof = new Etudiant();
        prof.setNom("Snape");
        prof.setPrenom("Severus");
        prof.setEmail("snape@poudlard.com");
        prof.setMotDePasse("potions123");
        prof.setRole(roleProf);
        Etudiant savedProf = etudiantDAO.save(prof);

        Cours cours = new Cours();
        cours.setIntitule("Potions");
        cours.setRef("POT");
        cours.setProfesseur(savedProf);
        cours.setEstOptionnel(false);
        Cours savedCours = coursDAO.save(cours);

        mockMvc.perform(get("/poudlard/cours/" + savedCours.getIdCours()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.intitule").value("Potions"))
                .andExpect(jsonPath("$.ref").value("POT"))
                .andExpect(jsonPath("$.professeur.email").value("snape@poudlard.com"))
                .andExpect(jsonPath("$.estOptionnel").value(false));
    }

    @Test
    void testGetEtudiantsByCours_shouldReturnListOfEtudiants() throws Exception {
        Role roleEtudiant = roleRepository.findByNom("ETUDIANT").orElseGet(() -> {
            Role r = new Role();
            r.setNom("ETUDIANT");
            return roleRepository.save(r);
        });

        Etudiant etu1 = new Etudiant();
        etu1.setNom("Granger");
        etu1.setPrenom("Hermione");
        etu1.setEmail("hermione@poudlard.com");
        etu1.setMotDePasse("livres123");
        etu1.setRole(roleEtudiant);

        Etudiant etu2 = new Etudiant();
        etu2.setNom("Weasley");
        etu2.setPrenom("Ron");
        etu2.setEmail("ron@poudlard.com");
        etu2.setMotDePasse("chess123");
        etu2.setRole(roleEtudiant);

        etu1 = etudiantDAO.save(etu1);
        etu2 = etudiantDAO.save(etu2);

        Role roleProf = roleRepository.findByNom("PROFESSEUR").orElseGet(() -> {
            Role r = new Role();
            r.setNom("PROFESSEUR");
            return roleRepository.save(r);
        });

        Etudiant prof = new Etudiant();
        prof.setNom("McGonagall");
        prof.setPrenom("Minerva");
        prof.setEmail("mcgonagall@poudlard.com");
        prof.setMotDePasse("transfiguration");
        prof.setRole(roleProf);
        prof = etudiantDAO.save(prof);

        Cours cours = new Cours();
        cours.setIntitule("Transfiguration");
        cours.setRef("TRANS");
        cours.setProfesseur(prof);
        cours.setEstOptionnel(false);
        cours.getEtudiants().add(etu1);
        cours.getEtudiants().add(etu2);
        cours = coursDAO.save(cours);

        mockMvc.perform(get("/poudlard/cours/" + cours.getIdCours() + "/etudiants"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].email").value("hermione@poudlard.com"))
                .andExpect(jsonPath("$[1].email").value("ron@poudlard.com"));
    }

    @Test
    @WithMockUser(roles = "PROFESSEUR")
    void testGetNotesByCours_shouldReturnNotesList() throws Exception {
        Role professeurRole = roleRepository.findByNom("PROFESSEUR")
                .orElseGet(() -> roleRepository.save(new Role(null, "PROFESSEUR")));

        Etudiant prof = new Etudiant();
        prof.setNom("Professeur");
        prof.setPrenom("Test");
        prof.setEmail("prof2@test.com");
        prof.setMotDePasse("test123");
        prof.setRole(professeurRole);
        Etudiant savedProf = etudiantDAO.save(prof);

        Cours cours = new Cours();
        cours.setIntitule("Histoire");
        cours.setRef("HIST");
        cours.setProfesseur(savedProf);
        cours.setEstOptionnel(false);
        Cours savedCours = coursDAO.save(cours);

        Etudiant etu = new Etudiant();
        etu.setNom("Eleve");
        etu.setPrenom("Note");
        etu.setEmail("eleve@test.com");
        etu.setMotDePasse("test123");
        etu.setRole(professeurRole);
        Etudiant savedEtu = etudiantDAO.save(etu);

        Note note = new Note();
        note.setValeur(16.0);
        note.setCours(savedCours);
        note.setEtudiant(savedEtu);
        noteDAO.save(note);

        mockMvc.perform(get("/poudlard/cours/" + savedCours.getIdCours() + "/notes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].valeur").value(16.0));
    }

    @Test
    @WithMockUser(roles = "PROFESSEUR")
    void testAjouterNoteAEtudiant_shouldCreateNote() throws Exception {
        Role profRole = roleRepository.findByNom("PROFESSEUR")
                .orElseGet(() -> roleRepository.save(new Role(null, "PROFESSEUR")));

        Etudiant prof = new Etudiant();
        prof.setNom("Prof");
        prof.setPrenom("AjouterNote");
        prof.setEmail("ajouternote@test.com");
        prof.setMotDePasse("test123");
        prof.setRole(profRole);
        Etudiant savedProf = etudiantDAO.save(prof);

        Etudiant etu = new Etudiant();
        etu.setNom("Etudiant");
        etu.setPrenom("Note");
        etu.setEmail("etu.note@test.com");
        etu.setMotDePasse("test123");
        etu.setRole(profRole);
        Etudiant savedEtu = etudiantDAO.save(etu);

        Cours cours = new Cours();
        cours.setIntitule("Philo");
        cours.setRef("PHI");
        cours.setProfesseur(savedProf);
        cours.setEstOptionnel(false);
        Cours savedCours = coursDAO.save(cours);

        String noteJson = """
        {
            "valeur": 17.5
        }
        """;

        mockMvc.perform(post("/poudlard/cours/" + savedCours.getIdCours() + "/" + savedEtu.getIdEtudiant() + "/notes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(noteJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.valeur").value(17.5))
                .andExpect(jsonPath("$.idNote").exists());
    }
}
