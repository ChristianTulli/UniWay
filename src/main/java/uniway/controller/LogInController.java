package uniway.controller;

import uniway.beans.UtenteBean;
import uniway.eccezioni.UtenteEsistenteException;
import uniway.eccezioni.UtenteNonTrovatoException;
import uniway.model.Utente;
import uniway.model.UtenteInCerca;
import uniway.model.UtenteIscritto;
import uniway.persistenza.UtenteDAO;

import java.io.IOException;
import java.util.Optional;

public class LogInController {

    private UtenteDAO utenteDAO;

    public LogInController() {
        PersistenzaController persistenzaController = PersistenzaController.getInstance();
        this.utenteDAO = persistenzaController.getUtenteDAO();
    }

    //per la registrazione controlliamo se i dati inseriti momentaneamente nella classe bean sono accettabili e istanziamo un oggetto user in caso positivo, confermando la registrazione

    public boolean registrazione(UtenteBean utenteBean) throws UtenteEsistenteException {
        if (utenteBean.getUsername().isEmpty() || utenteBean.getPassword().isEmpty() || utenteBean.getPassword().length() < 6) {
            return false;
        }
        Utente existingUser = utenteDAO.trovaDaUsername(utenteBean.getUsername());

        if (existingUser!=null) {
            throw new UtenteEsistenteException("esiste già un utente con username: " + utenteBean.getUsername()); // Username già esistente
        }
        Utente utente;
        if (utenteBean.getIscritto()) {
            utente = new UtenteIscritto(utenteBean.getUsername(), utenteBean.getPassword(), utenteBean.getIscritto());
        } else {
            utente = new UtenteInCerca(utenteBean.getUsername(), utenteBean.getPassword(), utenteBean.getIscritto());
        }
        utenteDAO.salvaUtente(utente);
        return true;
    }

    public UtenteBean autenticazione(String username, String password) {
        if (username.isEmpty() || password.isEmpty()) {
            throw new IllegalArgumentException("inserire username e password");
        }
        Utente utente = utenteDAO.trovaDaUsername(username);
        if (utente == null) {
            throw new UtenteNonTrovatoException("utente non trovato");
        }
        if (utente.getPassword().equals(password)) {
            if (utente instanceof UtenteIscritto utenteIscritto) {
                return new UtenteBean(utenteIscritto.getUsername(), utenteIscritto.getPassword(), true, utenteIscritto.getCorso(), utenteIscritto.getCurriculum()
                        //aggiungere attributo curriculum
                );
            } else {
                return new UtenteBean(utente.getUsername(), utente.getPassword(), false);
            }
        }
        throw new IllegalArgumentException("Password errata");
    }

}
