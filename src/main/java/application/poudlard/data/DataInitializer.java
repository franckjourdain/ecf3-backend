package application.poudlard.data;

import application.poudlard.dao.RoleRepository;
import application.poudlard.model.Role;
import application.poudlard.service.CoursService;
import application.poudlard.service.EtudiantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    private final CoursService coursService;
    private final EtudiantService etudiantService;
    private final RoleRepository roleRepository;

    public DataInitializer(CoursService coursService, EtudiantService etudiantService, RoleRepository roleRepository) {
        this.coursService = coursService;
        this.etudiantService = etudiantService;
        this.roleRepository = roleRepository;
    }

    @Override
    public void run(String... args) {
        initialiserRoles();                    // 1. Crée les rôles
        etudiantService.initialiserEtudiants(); // 2. Crée les étudiants avec ces rôles
        coursService.initialiserCours();        // 3. Crée les cours
    }

    private void initialiserRoles() {
        if (roleRepository.count() == 0) {
            roleRepository.save(new Role( "ETUDIANT"));
            roleRepository.save(new Role( "PROFESSEUR"));
            roleRepository.save(new Role( "ADMIN"));
        }
    }
}