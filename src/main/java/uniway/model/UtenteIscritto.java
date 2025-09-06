package uniway.model;

public class UtenteIscritto extends Utente{
    private Corso corso;
    private String curriculum;

    public UtenteIscritto(String username, String password, boolean iscritto) {
        super(username, password, iscritto);
    }

    public UtenteIscritto(String username, String password,boolean iscritto, Corso corso, String curriculum) {
        super(username, password, iscritto);
        this.corso = corso;
        this.curriculum = curriculum;
    }

    public Corso getCorso() {
        return corso;
    }

    public void setCorso(Corso corso) {
        this.corso = corso;
    }

    public String getCurriculum() {
        return curriculum;
    }
    public void setCurriculum(String curriculum) {
        this.curriculum = curriculum;
    }
}
