package application.poudlard.model;

import jakarta.persistence.*;

@Entity
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(unique = true, nullable = false)
    private String nom;

    // 🔧 Constructeur sans argument : requis par JPA
    public Role() {}

    // 🔧 Constructeur pratique avec le nom seul
    public Role(String nom) {
        this.nom = nom;
    }

    // 🔧 Constructeur complet
    public Role(Integer id, String nom) {
        this.id = id;
        this.nom = nom;
    }

    // Getters et Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }
}
