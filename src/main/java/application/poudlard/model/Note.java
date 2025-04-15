package application.poudlard.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Note {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int idNote;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cours_id", nullable = false)
    @JsonBackReference
    private Cours cours;

    private String intitule;

    private double valeur;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "etudiant_id", nullable = false)
    @JsonIgnore
    private Etudiant etudiant;

    public Note(String intitule, double valeur, Cours cours, Etudiant etudiant){
        this.intitule = intitule;
        this.valeur = valeur;
        this.cours = cours;
        this.etudiant = etudiant;
    }

    public Note(String intitule, double valeur){
        this.intitule = intitule;
        this.valeur = valeur;
    }
}
