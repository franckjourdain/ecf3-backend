package application.poudlard.dao;

import application.poudlard.model.Cours;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface CoursDAO extends JpaRepository<Cours, Integer> {
    boolean existsByRef(String ref);

}
