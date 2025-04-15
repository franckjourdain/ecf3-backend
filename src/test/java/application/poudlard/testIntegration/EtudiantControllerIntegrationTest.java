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
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class EtudiantControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private EtudiantDAO etudiantDAO;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CoursDAO coursDAO;

    @Autowired
    private NoteDAO noteDAO;

    @BeforeEach
    void setUp() {
        // Supprimer les dépendances dans le bon ordre pour éviter les violations de contrainte
        noteDAO.deleteAll();           // Supprimer les notes
        coursDAO.deleteAll();          // Supprimer les cours (liés aux professeurs)
        etudiantDAO.deleteAll();       // Supprimer les étudiants (professeurs inclus)
        roleRepository.deleteAll();    // Supprimer les rôles

        // Recréer le rôle "ETUDIANT" pour les tests
        Role etudiantRole = new Role();
        etudiantRole.setNom("ETUDIANT");
        roleRepository.save(etudiantRole);

        // Recréer le rôle "PROFESSEUR" pour éviter les erreurs si utilisé dans d'autres tests
        Role profRole = new Role();
        profRole.setNom("PROFESSEUR");
        roleRepository.save(profRole);

        // Créer deux étudiants
        Etudiant e1 = new Etudiant();
        e1.setNom("Potter");
        e1.setPrenom("Harry");
        e1.setEmail("harry@test.com");
        e1.setMotDePasse("1234");
        e1.setRole(etudiantRole);

        Etudiant e2 = new Etudiant();
        e2.setNom("Granger");
        e2.setPrenom("Hermione");
        e2.setEmail("hermione@test.com");
        e2.setMotDePasse("abcd");
        e2.setRole(etudiantRole);

        etudiantDAO.saveAll(List.of(e1, e2));
    }

    @Test
    void testGetAllEtudiants_shouldReturnList() throws Exception {
        mockMvc.perform(get("/poudlard/etudiant/all")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nom").exists())
                .andExpect(jsonPath("$[1].email").value("hermione@test.com"));
    }
    @Test
    void testGetEtudiantById_shouldReturnEtudiant() throws Exception {
        Etudiant etudiant = etudiantDAO.findByEmail("harry@test.com").orElseThrow();

        mockMvc.perform(get("/poudlard/etudiant/" + etudiant.getIdEtudiant()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nom").value("Potter"))
                .andExpect(jsonPath("$.prenom").value("Harry"))
                .andExpect(jsonPath("$.email").value("harry@test.com"));
    }
    @Test
    void testGetNotesByEtudiant_shouldReturnNotes() throws Exception {
        // Récupérer un étudiant
        Etudiant etudiant = etudiantDAO.findByEmail("harry@test.com").orElseThrow();

        // Créer un cours (professeur requis)
        Role profRole = roleRepository.findByNom("PROFESSEUR").orElseThrow();
        Etudiant professeur = new Etudiant();
        professeur.setNom("Professeur");
        professeur.setPrenom("Test");
        professeur.setEmail("profnote@test.com");
        professeur.setMotDePasse("pass123");
        professeur.setRole(profRole);
        etudiantDAO.save(professeur);

        Cours cours = new Cours();
        cours.setIntitule("Test Notes");
        cours.setRef("TNOTE");
        cours.setProfesseur(professeur);
        coursDAO.save(cours);

        // Créer une note pour cet étudiant
        Note note = new Note();
        note.setValeur(14.5);
        note.setEtudiant(etudiant);
        note.setCours(cours);
        noteDAO.save(note);

        mockMvc.perform(get("/poudlard/etudiant/" + etudiant.getIdEtudiant() + "/notes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].valeur").value(14.5));
    }
    @Test
    void testGetCoursByEtudiant_shouldReturnCoursList() throws Exception {
        // Créer un professeur
        Role profRole = roleRepository.findByNom("PROFESSEUR").orElseThrow();
        Etudiant professeur = new Etudiant();
        professeur.setNom("Professeur");
        professeur.setPrenom("Cours");
        professeur.setEmail("coursprof@test.com");
        professeur.setMotDePasse("test123");
        professeur.setRole(profRole);
        etudiantDAO.save(professeur);

        // Créer un étudiant
        Role etuRole = roleRepository.findByNom("ETUDIANT").orElseThrow();
        Etudiant etudiant = new Etudiant();
        etudiant.setNom("Etudiant");
        etudiant.setPrenom("Cours");
        etudiant.setEmail("etucours@test.com");
        etudiant.setMotDePasse("test123");
        etudiant.setRole(etuRole);
        etudiantDAO.save(etudiant);

        // Créer un cours
        Cours cours = new Cours();
        cours.setIntitule("Science");
        cours.setRef("SCI");
        cours.setProfesseur(professeur);
        cours.getEtudiants().add(etudiant);
        coursDAO.save(cours);

        // Lier le cours à l'étudiant (inverse)
        etudiant.getCours().add(cours);
        etudiantDAO.save(etudiant);

        mockMvc.perform(get("/poudlard/etudiant/" + etudiant.getIdEtudiant() + "/cours"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].intitule").value("Science"))
                .andExpect(jsonPath("$[0].ref").value("SCI"));
    }
    @Test
    void testAjouterEtudiant_shouldCreateEtudiant() throws Exception {
        Role roleEtudiant = roleRepository.findByNom("ETUDIANT").orElseThrow();

        Etudiant etudiant = new Etudiant();
        etudiant.setNom("Jean");
        etudiant.setPrenom("Valjean");
        etudiant.setEmail("jean.valjean@test.com");
        etudiant.setMotDePasse("123456");
        etudiant.setRole(roleEtudiant);

        mockMvc.perform(post("/poudlard/etudiant")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(etudiant)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nom").value("Jean"))
                .andExpect(jsonPath("$.email").value("jean.valjean@test.com"));
    }
    @Test
    void testModifierEtudiant_shouldUpdateEtudiant() throws Exception {
        Role roleEtudiant = roleRepository.findByNom("ETUDIANT").orElseThrow();

        Etudiant etudiant = new Etudiant();
        etudiant.setNom("Modif");
        etudiant.setPrenom("Avant");
        etudiant.setEmail("modif@test.com");
        etudiant.setMotDePasse("abc123");
        etudiant.setRole(roleEtudiant);
        etudiantDAO.save(etudiant);

        // Modifications
        etudiant.setNom("Modif");
        etudiant.setPrenom("Apres");

        mockMvc.perform(put("/poudlard/etudiant/" + etudiant.getIdEtudiant())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(etudiant)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.prenom").value("Apres"));
    }

    @Test
    void testSupprimerEtudiant_shouldReturnNoContent() throws Exception {
        Role roleEtudiant = roleRepository.findByNom("ETUDIANT").orElseThrow();

        Etudiant etudiant = new Etudiant();
        etudiant.setNom("Delete");
        etudiant.setPrenom("Moi");
        etudiant.setEmail("delete@test.com");
        etudiant.setMotDePasse("delete123");
        etudiant.setRole(roleEtudiant);
        etudiantDAO.save(etudiant);

        mockMvc.perform(delete("/poudlard/etudiant/" + etudiant.getIdEtudiant()))
                .andExpect(status().isNoContent());
    }

}
