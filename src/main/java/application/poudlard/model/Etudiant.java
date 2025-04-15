package application.poudlard.model;

import application.poudlard.model.Role;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Etudiant {

   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int idEtudiant;
    private String nom;
    private String prenom;
    @Column(unique = true)
    private String email;
    private String motDePasse; //sera haché
    @ManyToOne
    @JoinColumn(name = "role_id")
    private Role role;


    @OneToMany(mappedBy = "etudiant",cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<Note> notes = new ArrayList<>();
    @ManyToMany(mappedBy ="etudiants")
    @JsonIgnore
    private List<Cours> cours = new ArrayList<>();




 public Etudiant(String nom, String prenom, String email, String motDePasse, Role role) {
  this.nom = nom;
  this.prenom = prenom;
  this.email = email;
  this.motDePasse = motDePasse;
  this.role = role;
 }
}
