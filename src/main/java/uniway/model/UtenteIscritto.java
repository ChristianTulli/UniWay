package uniway.model;

public class UtenteIscritto extends Utente{
    int id;
    private int idCorso;

    public UtenteIscritto(String username, String password) {
        super(username, password);
    }

    public UtenteIscritto(int id, String username, String password, int idCorso) {
        super(username, password);
        this.id = id;
        this.idCorso = idCorso;
    }

    public int getIdCorso() {
        return idCorso;
    }
}
