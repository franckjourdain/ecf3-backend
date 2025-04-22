package application.poudlard.testUnitaire;

import application.poudlard.dao.CoursDAO;
import application.poudlard.dao.EtudiantDAO;
import application.poudlard.model.Cours;
import application.poudlard.model.Etudiant;
import application.poudlard.model.Role;
import application.poudlard.service.CoursService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class CoursServiceTest {

    @Mock
    private CoursDAO coursDao;
    @Mock
    private EtudiantDAO etudiantDao;

    @InjectMocks
    private CoursService coursService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        coursService = new CoursService(coursDao, etudiantDao);
    }

    @Test
    void testGetAllCours() {
        List<Cours> coursList = List.of(new Cours(), new Cours());
        when(coursDao.findAll()).thenReturn(coursList);

        List<Cours> result = coursService.getAllCours();

        assertThat(result).hasSize(2);
    }

    @Test
    void testGetCoursById_Success() {
        Cours cours = new Cours();
        cours.setIdCours(1L);
        when(coursDao.findById(1L)).thenReturn(Optional.of(cours));

        Cours result = coursService.getCoursById(1L);

        assertThat(result).isEqualTo(cours);
    }

    @Test
    void testGetCoursById_NotFound() {
        when(coursDao.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> coursService.getCoursById(1L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Cours non trouvé");
    }

    @Test
    void testCreateCours_Success() {
        Cours cours = new Cours();
        cours.setIdCours(1L);
        when(coursDao.findById(1L)).thenReturn(Optional.empty());

        coursService.createCours(cours);

        verify(coursDao, times(1)).save(cours);
    }

    @Test
    void testCreateCours_AlreadyExists() {
        Cours cours = new Cours();
        cours.setIdCours(1L);
        when(coursDao.findById(1L)).thenReturn(Optional.of(cours));

        assertThatThrownBy(() -> coursService.createCours(cours))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Ce cours existe déjà");
    }

    @Test
    void testSupprimerCours() {
        Cours cours = new Cours();
        when(coursDao.findById(1L)).thenReturn(Optional.of(cours));

        coursService.supprimerCours(1L);

        verify(coursDao).delete(cours);
    }

    @Test
    void testAjouterEtudiantsAuCours() {
        Cours cours = new Cours();
        cours.setEtudiants(new ArrayList<>());
        cours.setIdCours(1L);

        Etudiant etu = new Etudiant();
        etu.setIdEtudiant(2L);
        etu.setCours(new ArrayList<>());

        when(coursDao.findById(1L)).thenReturn(Optional.of(cours));
        when(etudiantDao.findById(2L)).thenReturn(Optional.of(etu));

        List<Etudiant> result = coursService.ajouterEtudiantsAuCours(1L, List.of(2L));

        assertThat(result).contains(etu);
        assertThat(etu.getCours()).contains(cours);
    }

    @Test
    void testGetCoursByProfesseur_Success() {
        Etudiant prof = new Etudiant();
        prof.setEmail("prof@test.com");
        Role role = new Role();
        role.setNom("PROFESSEUR");
        prof.setRole(role);

        Cours cours = new Cours();
        cours.setProfesseur(prof);

        when(etudiantDao.findByEmail("prof@test.com")).thenReturn(Optional.of(prof));
        when(coursDao.findAll()).thenReturn(List.of(cours));

        List<Cours> result = coursService.getCoursByProfesseur("prof@test.com");

        assertThat(result).contains(cours);
    }

    @Test
    void testGetCoursByProfesseur_NonProfesseur() {
        Etudiant etudiant = new Etudiant();
        etudiant.setEmail("user@test.com");
        Role role = new Role();
        role.setNom("ETUDIANT");
        etudiant.setRole(role);

        when(etudiantDao.findByEmail("user@test.com")).thenReturn(Optional.of(etudiant));

        assertThatThrownBy(() -> coursService.getCoursByProfesseur("user@test.com"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("L'utilisateur n'est pas un professeur");
    }
}