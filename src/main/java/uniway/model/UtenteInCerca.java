package uniway.model;

import java.util.List;

public class UtenteInCerca extends Utente{
    private List<Integer> preferiti; //da cambiare in lista di corsi

    public UtenteInCerca(String username, String password, boolean iscritto) {
        super(username, password, iscritto);
    }

    public UtenteInCerca(int id, String username, String password, boolean iscritto, List<Integer> preferiti) {
        super(id, username, password, iscritto);
        this.preferiti = preferiti;
    }

    public List<Integer> getPreferenze() {
        return preferiti;
    }
}
