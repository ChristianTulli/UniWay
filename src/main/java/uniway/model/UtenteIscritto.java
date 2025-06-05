package uniway.model;

public class UtenteIscritto extends Utente{
    private Integer idCorso;
    private String curriculum;

    public UtenteIscritto(String username, String password, boolean iscritto) {
        super(username, password, iscritto);
    }

    public UtenteIscritto(int id, String username, String password,boolean iscritto, Integer idCorso, String curriculum) {
        super(id, username, password, iscritto);
        this.idCorso = idCorso;
        this.curriculum = curriculum;
    }

    public Integer getIdCorso() {
        return idCorso;
    }

    public void setIdCorso(int idCorso) {
        this.idCorso = idCorso;
    }

    public String getCurriculum() {
        return curriculum;
    }
    public void setCurriculum(String curriculum) {
        this.curriculum = curriculum;
    }
}
