package application.poudlard.dao;

import application.poudlard.model.Note;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NoteDAO extends JpaRepository<Note, Integer> {

    @Query("SELECT n FROM Note n WHERE n.cours.idCours = :idCours")
    List<Note> findNotesByCours(@Param("idCours")int idCours);

    @Query("SELECT n FROM Note n WHERE n.etudiant.idEtudiant =:idEtudiant")
    List<Note> findNotesByEtudiant(@Param("idEtudiant") int idEtudiant);

    @Query("SELECT n FROM Note n WHERE n.etudiant.idEtudiant = :etudiantId AND n.cours.idCours = :coursId")
    List<Note> findNotesByEtudiantAndCours(@Param("etudiantId") int etudiantId, @Param("coursId") int coursId);
}
