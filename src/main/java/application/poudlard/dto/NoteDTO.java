package application.poudlard.dto;

public class NoteDTO {
    private Long idNote;
    private double valeur;
    private String intitule;
    private String coursIntitule;

    public NoteDTO(Long idNote, double valeur, String intitule, String coursIntitule) {
        this.idNote = idNote;
        this.valeur = valeur;
        this.intitule = intitule;
        this.coursIntitule = coursIntitule;
    }

    // Getters & setters (ou lombok si tu veux)
    public Long getIdNote() { return idNote; }
    public double getValeur() { return valeur; }
    public String getIntitule() { return intitule; }
    public String getCoursIntitule() { return coursIntitule; }
}
