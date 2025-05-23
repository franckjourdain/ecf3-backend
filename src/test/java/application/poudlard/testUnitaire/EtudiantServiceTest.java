package application.poudlard.testUnitaire;

import application.poudlard.dao.CoursDAO;
import application.poudlard.dao.EtudiantDAO;
import application.poudlard.dao.RoleRepository;
import application.poudlard.model.Cours;
import application.poudlard.model.Etudiant;
import application.poudlard.model.Note;
import application.poudlard.model.Role;
import application.poudlard.service.EtudiantService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class EtudiantServiceTest {

    @Mock
    private EtudiantDAO etudiantDao;
    @Mock
    private CoursDAO coursDao;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private RoleRepository roleRepository;

    @InjectMocks
    private EtudiantService etudiantService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        etudiantService = new EtudiantService(etudiantDao, coursDao, passwordEncoder, roleRepository);
    }

    @Test
    void testGetEtudiants() {
        List<Etudiant> liste = List.of(new Etudiant(), new Etudiant());
        when(etudiantDao.findAll()).thenReturn(liste);

        List<Etudiant> result = etudiantService.getEtudiants();
        assertThat(result).hasSize(2);
    }

    @Test
    void testAjouterEtudiant_Success() {
        Etudiant etudiant = new Etudiant();
        etudiant.setIdEtudiant(1L);
        when(etudiantDao.existsById(1L)).thenReturn(false);
        when(etudiantDao.save(etudiant)).thenReturn(etudiant);

        Etudiant result = etudiantService.ajouterEtudiant(etudiant);
        assertThat(result).isEqualTo(etudiant);
    }

    @Test
    void testAjouterEtudiant_AlreadyExists() {
        Etudiant etudiant = new Etudiant();
        etudiant.setIdEtudiant(1L);
        when(etudiantDao.existsById(1L)).thenReturn(true);

        assertThatThrownBy(() -> etudiantService.ajouterEtudiant(etudiant))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Cet étudiant existe déjà !");
    }

    @Test
    void testSupprimerEtudiant_Success() {
        when(etudiantDao.existsById(1L)).thenReturn(true);
        etudiantService.supprimerEtudiant(1L);
        verify(etudiantDao, times(1)).deleteById(1L);
    }

    @Test
    void testSupprimerEtudiant_NotFound() {
        when(etudiantDao.existsById(1L)).thenReturn(false);
        assertThatThrownBy(() -> etudiantService.supprimerEtudiant(1L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Etudiant non trouvé");
    }

    @Test
    void testModifierEtudiant() {
        Etudiant existant = new Etudiant();
        existant.setIdEtudiant(1L);

        Etudiant modifie = new Etudiant();
        modifie.setIdEtudiant(1L);
        modifie.setNom("Dupont");
        modifie.setPrenom("Jean");
        modifie.setNotes(List.of(new Note()));

        when(etudiantDao.findById(1L)).thenReturn(Optional.of(existant));
        when(etudiantDao.save(existant)).thenReturn(existant);

        Etudiant result = etudiantService.modifierEtudiant(modifie);
        assertThat(result.getNom()).isEqualTo("Dupont");
        assertThat(result.getPrenom()).isEqualTo("Jean");
    }

    @Test
    void testGetEtudiantsByCours_Empty() {
        when(etudiantDao.findEtudiantsByCours(1L)).thenReturn(Collections.emptyList());

        assertThatThrownBy(() -> etudiantService.getEtudiantsByCours(1L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Cours introuvable avec l'id");
    }

    @Test
    void testAjouterCoursAEtudiant() {
        Cours cours = new Cours();
        cours.setIdCours(1L);
        cours.setEtudiants(new ArrayList<>());

        Etudiant etudiant = new Etudiant();
        etudiant.setIdEtudiant(1L);
        etudiant.setCours(new ArrayList<>());

        when(etudiantDao.findById(1L)).thenReturn(Optional.of(etudiant));
        when(coursDao.findById(1L)).thenReturn(Optional.of(cours));

        etudiantService.ajouterCoursAEtudiant(1L, List.of(1L));

        assertThat(etudiant.getCours()).contains(cours);
        assertThat(cours.getEtudiants()).contains(etudiant);
    }

    @Test
    void testGetEtudiantsDesCoursDuProfesseur() {
        Etudiant professeur = new Etudiant();
        professeur.setEmail("prof@example.com");

        Cours cours = new Cours();
        cours.setProfesseur(professeur);
        Etudiant etu = new Etudiant();
        cours.setEtudiants(List.of(etu));

        when(etudiantDao.findByEmail("prof@example.com")).thenReturn(Optional.of(professeur));
        when(coursDao.findAll()).thenReturn(List.of(cours));

        List<Etudiant> result = etudiantService.getEtudiantsDesCoursDuProfesseur("prof@example.com");
        assertThat(result).contains(etu);
    }
}
