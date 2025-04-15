package application.poudlard.dao;

import application.poudlard.model.Etudiant;
import application.poudlard.model.Note;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EtudiantDAO  extends JpaRepository<Etudiant, Integer> {
    boolean existsById(Integer id);

    @Query("SELECT e.notes FROM Etudiant e WHERE e.idEtudiant = :etudiantId")
    List<Note> getNotes(@Param("etudiantId") Integer etudiantId);

    boolean existsByEmail(String email);
    Optional<Etudiant> findByEmail(String email);

    @Query("SELECT e FROM Etudiant e JOIN e.cours c WHERE c.idCours = :idCours")
    List<Etudiant> findEtudiantsByCours(@Param("idCours") int idCours);

}
