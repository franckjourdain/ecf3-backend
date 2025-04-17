package application.poudlard.testUnitaire;

import application.poudlard.dao.CoursDAO;
import application.poudlard.model.Cours;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;


@SpringBootTest
class CoursDAOTest {

    @Autowired
    private CoursDAO coursDAO;

    @Test
    void testInsertCours() {
        Cours cours = new Cours("TestCours", "TEST");
        coursDAO.save(cours);
        Assertions.assertTrue(coursDAO.existsByRef("TEST"));
    }
}