package uniway.model;

import java.util.List;

public class UtenteInCerca extends Utente{
    int id;
    private List<String> preferiti; //da cambiare in lista di corsi

    public UtenteInCerca(String username, String password, Boolean iscritto) {
        super(username, password, iscritto);
    }

    public UtenteInCerca(int id, String username, String password, Boolean iscritto, List<String> preferiti) {
        super(username, password, iscritto);
        this.id = id;
        this.preferiti = preferiti;
    }

    public List<String> getPreferiti() {
        return preferiti;
    }
}
