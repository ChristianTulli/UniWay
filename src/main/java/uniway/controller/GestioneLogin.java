
package uniway.controller;

import uniway.beans.UtenteBean;
import uniway.model.Utente;




import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class GestioneLogin {

    //pattern singleton
    private  static GestioneLogin instance;
    private final List<Utente> utenti= new ArrayList<>();

    private GestioneLogin() {}

    public static GestioneLogin getInstance() {
        if(instance == null) {
            instance = new GestioneLogin();
        }
        return instance;
    }


    //per la registrazione controlliamo se i dati inseriti momentaneamente nella classe bean sono accettabili e istanziamo un oggetto user in caso positivo, confermando la registrazione

    public boolean registrazione(UtenteBean utenteBean) {
        if(utenteBean.getUsername().isEmpty() || utenteBean.getPassword().isEmpty()) {
            return false;
        }else if (utenteBean.getPassword().length() < 6) {
            return false;
        } else {
            Optional<Utente> existingUser = utenti.stream()
                    .filter(u -> u.getUsername().equals(utenteBean.getUsername()))
                    .findFirst();

            if (existingUser.isPresent()) {
                return false; // Username già esistente
            } else {
            Utente utente = new Utente(utenteBean.getUsername(), utenteBean.getPassword());
            utenti.add(utente);
            return true;
            }
        }
    }
    public boolean autenticazione(UtenteBean utenteBean) {
        return utenti.stream()
                .anyMatch(utente -> utente.getUsername().equals(utenteBean.getUsername()) && utente.getPassword().equals(utenteBean.getPassword()));

    }
}
