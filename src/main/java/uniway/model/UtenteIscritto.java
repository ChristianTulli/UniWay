package uniway.model;

public class UtenteIscritto extends Utente{
    private Corso corso;

    public UtenteIscritto(String username, String password, boolean iscritto) {
        super(username, password, iscritto);
    }

    public UtenteIscritto(String username, String password,boolean iscritto, Corso corso) {
        super(username, password, iscritto);
        this.corso = corso;
    }

    public Corso getCorso() {
        return corso;
    }

    public void setCorso(Corso corso) {
        this.corso = corso;
    }

}
