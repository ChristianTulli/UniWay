package uniway.model;

public class UtenteIscritto extends Utente{
    private Integer idCorso;

    public UtenteIscritto(String username, String password, boolean iscritto) {
        super(username, password, iscritto);
    }

    public UtenteIscritto(int id, String username, String password,boolean iscritto, Integer idCorso) {
        super(id, username, password, iscritto);
        this.idCorso = idCorso;
    }

    public Integer getIdCorso() {
        return idCorso;
    }

    public void setIdCorso(int idCorso) {
        this.idCorso = idCorso;
    }
}
