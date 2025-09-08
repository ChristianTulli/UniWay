package uniway.model;

import java.util.ArrayList;
import java.util.List;

public class UtenteInCerca extends Utente{
    private List<Corso> preferiti; //da cambiare in lista di corsi

    public UtenteInCerca(String username, String password, boolean iscritto) {
        super(username, password, iscritto);
        this.preferiti =new ArrayList<>();
    }

    public UtenteInCerca(String username, String password, boolean iscritto, List<Corso> preferiti) {
        super(username, password, iscritto);
        this.preferiti = (preferiti != null) ? preferiti : new ArrayList<>();
    }

    public List<Corso> getPreferenze() {
        return preferiti;
    }
    public void setPreferenze(List<Corso> preferiti) {
        this.preferiti = (preferiti != null) ? preferiti : new ArrayList<>();
    }
}
