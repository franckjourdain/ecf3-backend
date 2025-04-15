package application.poudlard.controller;

import application.poudlard.dao.EtudiantDAO;
import application.poudlard.dao.RoleRepository;
import application.poudlard.model.Etudiant;
import application.poudlard.model.Role;
import application.poudlard.security.JwtUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
//@CrossOrigin(origins = "*") // Pour éviter les soucis CORS si jamais tu testes avec un front
public class AuthController {

    private final EtudiantDAO etudiantDAO;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthController(
            EtudiantDAO etudiantDAO,
            RoleRepository roleRepository,
            PasswordEncoder passwordEncoder,
            JwtUtil jwtUtil
    ) {
        this.etudiantDAO = etudiantDAO;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Etudiant utilisateur) {
        if (utilisateur.getEmail() == null || utilisateur.getMotDePasse() == null) {
            return ResponseEntity.badRequest().body("Email ou mot de passe manquant.");
        }

        if (etudiantDAO.existsByEmail(utilisateur.getEmail())) {
            return ResponseEntity.badRequest().body("Cet email est déjà utilisé.");
        }

        // Si aucun rôle n'est fourni, on assigne le rôle "ETUDIANT" par défaut
        if (utilisateur.getRole() == null) {
            Optional<Role> roleEtudiant = roleRepository.findByNom("ETUDIANT");
            if (roleEtudiant.isEmpty()) {
                return ResponseEntity.badRequest().body("Le rôle ETUDIANT est introuvable.");
            }
            utilisateur.setRole(roleEtudiant.get());
        }

        utilisateur.setMotDePasse(passwordEncoder.encode(utilisateur.getMotDePasse()));
        Etudiant savedUser = etudiantDAO.save(utilisateur);
        return ResponseEntity.ok("Inscription réussie pour : " + savedUser.getEmail());
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> loginData) {
        String email = loginData.get("email");
        String motDePasse = loginData.get("motDePasse");

        if (email == null || motDePasse == null) {
            return ResponseEntity.badRequest().body("Email ou mot de passe manquant.");
        }

        Optional<Etudiant> optional = etudiantDAO.findByEmail(email);
        if (optional.isEmpty() || !passwordEncoder.matches(motDePasse, optional.get().getMotDePasse())) {
            return ResponseEntity.status(401).body("Identifiants invalides");
        }

        Etudiant user = optional.get();
        String token = jwtUtil.generateToken(user.getEmail(), user.getRole().getNom());

        Map<String, String> response = new HashMap<>();
        response.put("token", token);
        response.put("role", user.getRole().getNom());

        return ResponseEntity.ok(response);
    }
}
