package application.poudlard.testUnitaire;

import application.poudlard.controller.AuthController;
import application.poudlard.dao.EtudiantDAO;
import application.poudlard.dao.RoleRepository;
import application.poudlard.model.Etudiant;
import application.poudlard.model.Role;
import application.poudlard.security.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class AuthControllerTest {

    private EtudiantDAO etudiantDAO;
    private RoleRepository roleRepository;
    private PasswordEncoder passwordEncoder;
    private JwtUtil jwtUtil;
    private AuthController authController;

    @BeforeEach
    void setUp() {
        etudiantDAO = mock(EtudiantDAO.class);
        roleRepository = mock(RoleRepository.class);
        passwordEncoder = mock(PasswordEncoder.class);
        jwtUtil = mock(JwtUtil.class);

        authController = new AuthController(etudiantDAO, roleRepository, passwordEncoder, jwtUtil);
    }

    @Test
    void testRegister_Success() {
        Etudiant user = new Etudiant();
        user.setEmail("test@test.com");
        user.setMotDePasse("mdp");

        Role role = new Role();
        role.setNom("ETUDIANT");

        when(etudiantDAO.existsByEmail("test@test.com")).thenReturn(false);
        when(roleRepository.findByNom("ETUDIANT")).thenReturn(Optional.of(role));
        when(passwordEncoder.encode("mdp")).thenReturn("encoded_mdp");
        when(etudiantDAO.save(Mockito.any())).thenReturn(user);

        ResponseEntity<?> response = authController.register(user);

        assertThat(response.getStatusCodeValue()).isEqualTo(200);
        verify(etudiantDAO).save(Mockito.any());
    }

    @Test
    void testLogin_Success() {
        Map<String, String> login = new HashMap<>();
        login.put("email", "test@test.com");
        login.put("motDePasse", "mdp");

        Etudiant user = new Etudiant();
        user.setEmail("test@test.com");
        user.setMotDePasse("encoded_mdp");
        Role role = new Role();
        role.setNom("ETUDIANT");
        user.setRole(role);

        when(etudiantDAO.findByEmail("test@test.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("mdp", "encoded_mdp")).thenReturn(true);
        when(jwtUtil.generateToken("test@test.com", "ETUDIANT")).thenReturn("fake.jwt.token");

        ResponseEntity<?> response = authController.login(login);

        assertThat(response.getStatusCodeValue()).isEqualTo(200);
        assertThat(((Map<?, ?>) response.getBody()).get("token")).isEqualTo("fake.jwt.token");
    }

    @Test
    void testLogin_BadCredentials() {
        Map<String, String> login = new HashMap<>();
        login.put("email", "wrong@test.com");
        login.put("motDePasse", "wrongpass");

        when(etudiantDAO.findByEmail("wrong@test.com")).thenReturn(Optional.empty());

        ResponseEntity<?> response = authController.login(login);

        assertThat(response.getStatusCodeValue()).isEqualTo(401);
    }
}
