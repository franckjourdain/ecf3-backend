package application.poudlard.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "cours")
@Getter
@Setter
@NoArgsConstructor
public class Cours {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_cours")
    private int idCours;
    private String intitule;
    private String ref;
    @ManyToOne
    private Etudiant professeur;

    @Column(name = "est_optionnel")
    private boolean estOptionnel;


    @OneToMany(mappedBy = "cours")
    @JsonIgnore
    private List<Note> notes= new ArrayList<>();

    @ManyToMany
    @JoinTable(
            name = "etudiant_cours",
            joinColumns = @JoinColumn(name = "cours_id"),
            inverseJoinColumns = @JoinColumn(name = "etudiant_id")
    )
    @JsonIgnore
    private List<Etudiant> etudiants= new ArrayList<>();

    public Cours(String intitule, String ref) {
        this.intitule = intitule;
        this.ref = ref;
    }

    public Cours(int coursId, String intitule, String ref) {
        this.idCours = coursId;
        this.intitule = intitule;
        this.ref = ref;
    }
}
