package application.poudlard.testIntegration;

import application.poudlard.dao.CoursDAO;
import application.poudlard.dao.EtudiantDAO;
import application.poudlard.dao.NoteDAO;
import application.poudlard.dao.RoleRepository;
import application.poudlard.model.Etudiant;
import application.poudlard.model.Role;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@Sql(scripts = "/data-test.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class AuthControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private EtudiantDAO etudiantDAO;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private CoursDAO coursDAO;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private NoteDAO noteRepository;

    @Autowired
    private CoursDAO coursRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        noteRepository.deleteAll();      // Supprimer les notes d'abord
        coursRepository.deleteAll();     // Ensuite les cours
        etudiantDAO.deleteAll();         // Puis les étudiants
        roleRepository.deleteAll();      // Enfin les rôles

        // Recréer le rôle "ETUDIANT" pour les tests
        if (roleRepository.findByNom("ETUDIANT").isEmpty()) {
            Role role = new Role();
            role.setNom("ETUDIANT");
            roleRepository.save(role);
        }
    }

    @Test
    void testRegister_shouldCreateEtudiant() throws Exception {
        Etudiant etudiant = new Etudiant();
        etudiant.setNom("Harry");
        etudiant.setPrenom("Potter");
        etudiant.setEmail("harry@poudlard.fr");
        etudiant.setMotDePasse("expelliarmus");

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(etudiant)))
                .andExpect(status().isOk())
                .andExpect(content().string("Inscription réussie pour : harry@poudlard.fr"));

        Optional<Etudiant> saved = etudiantDAO.findByEmail("harry@poudlard.fr");
        assertThat(saved).isPresent();
        assertThat(saved.get().getNom()).isEqualTo("Harry");
        assertThat(saved.get().getRole().getNom()).isEqualTo("ETUDIANT");
    }
    @Test
    void testLogin_shouldReturnTokenAndRole() throws Exception {
        // Vérifie si le rôle existe déjà avant de l'insérer
        Role role = roleRepository.findByNom("ETUDIANT")
                .orElseGet(() -> roleRepository.save(new Role(null, "ETUDIANT")));

        Etudiant etudiant = new Etudiant();
        etudiant.setNom("Test");
        etudiant.setPrenom("Login");
        etudiant.setEmail("login@poudlard.fr");
        etudiant.setMotDePasse(passwordEncoder.encode("monmotdepasse"));
        etudiant.setRole(role);
        etudiantDAO.save(etudiant);

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                {
                  "email": "login@poudlard.fr",
                  "motDePasse": "monmotdepasse"
                }
            """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists())
                .andExpect(jsonPath("$.role").value("ETUDIANT"));
    }
    @Test
    void testLogin_shouldReturn401WithInvalidCredentials() throws Exception {
        // Prérequis : créer un utilisateur en base
        Etudiant etudiant = new Etudiant();
        etudiant.setNom("Hermione");
        etudiant.setPrenom("Granger");
        etudiant.setEmail("hermione@poudlard.fr");
        etudiant.setMotDePasse(new BCryptPasswordEncoder().encode("leviosa"));

        Role roleEtudiant = roleRepository.findByNom("ETUDIANT").orElseThrow();
        etudiant.setRole(roleEtudiant);
        etudiantDAO.save(etudiant);

        // Tentative de connexion avec un mauvais mot de passe
        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                        {
                          "email": "hermione@poudlard.fr",
                          "motDePasse": "mauvaismotdepasse"
                        }
                    """))
                .andExpect(status().isUnauthorized()) // 401 attendu
                .andExpect(content().string("Identifiants invalides"));
    }

    // Test de non-regression, test le comportement de l'inscription d'un étudiant via /auth/register
    // Lorsqu'un étudiant est créé sans role explicite, il reçoit automatiquement le role "ETUDIANT"
    @Test
    void testRegisterSansRoleAttribue_shouldAssignEtudiantRole() throws Exception {
        Etudiant etudiant = new Etudiant();
        etudiant.setNom("Regression");
        etudiant.setPrenom("Testeur");
        etudiant.setEmail("regression@test.com");
        etudiant.setMotDePasse("test123");

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(etudiant)))
                .andExpect(status().isOk());

        Optional<Etudiant> saved = etudiantDAO.findByEmail("regression@test.com");
        assertThat(saved).isPresent();
        assertThat(saved.get().getRole()).isNotNull();
        assertThat(saved.get().getRole().getNom()).isEqualTo("ETUDIANT");
    }

}
