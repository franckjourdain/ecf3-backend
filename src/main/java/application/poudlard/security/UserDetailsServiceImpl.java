package application.poudlard.security;

import application.poudlard.dao.EtudiantDAO;
import application.poudlard.model.Etudiant;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final EtudiantDAO etudiantDAO;

    public UserDetailsServiceImpl(EtudiantDAO etudiantDAO) {
        this.etudiantDAO = etudiantDAO;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Etudiant etudiant = etudiantDAO.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Utilisateur non trouvé : " + email));

        return new User(
                etudiant.getEmail(),
                etudiant.getMotDePasse(),
                Collections.singletonList(
                        new SimpleGrantedAuthority("ROLE_" + etudiant.getRole().getNom())
                )
        );
    }
}
