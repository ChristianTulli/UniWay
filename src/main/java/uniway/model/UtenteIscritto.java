package uniway.model;

public class UtenteIscritto extends Utente{
    int id;
    private Integer idCorso;

    public UtenteIscritto(String username, String password, boolean iscritto) {
        super(username, password, iscritto);
    }

    public UtenteIscritto(int id, String username, String password,boolean iscritto, Integer idCorso) {
        super(username, password, iscritto);
        this.id = id;
        this.idCorso = idCorso;
    }

    public int getIdCorso() {
        return idCorso;
    }
}
