package uniway.controller;

import uniway.beans.UtenteBean;
import uniway.eccezioni.UtenteEsistenteException;
import uniway.eccezioni.UtenteNonTrovatoException;
import uniway.model.Utente;
import uniway.model.UtenteInCerca;
import uniway.model.UtenteIscritto;
import uniway.patterns.SessioneControllerSingleton;
import uniway.persistenza.UtenteDAO;


public class LogInController {

    private UtenteDAO utenteDAO;

    public LogInController() {
        SessioneControllerSingleton sessioneControllerSingleton = SessioneControllerSingleton.getInstance();
        this.utenteDAO = sessioneControllerSingleton.getUtenteDAO();
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
        SessioneControllerSingleton.getInstance().setCurrentUser(utente);
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
                if(utenteIscritto.getCorso()!=null) {
                    SessioneControllerSingleton.getInstance().setCurrentUser(utenteIscritto);//imposa utente attivo nella sessione
                    return new UtenteBean(utenteIscritto.getUsername(), utenteIscritto.getPassword(), true, true);
                }else{
                    SessioneControllerSingleton.getInstance().setCurrentUser(utenteIscritto);//imposa utente attivo nella sessione
                    return new UtenteBean(utente.getUsername(), utente.getPassword(), true, false);
                }
            } else if (utente instanceof UtenteInCerca utenteInCerca) {
                SessioneControllerSingleton.getInstance().setCurrentUser(utenteInCerca);//imposa utente attivo nella sessione
                return new UtenteBean(utenteInCerca.getUsername(), utenteInCerca.getPassword(), false);
            }
        }
        throw new IllegalArgumentException("Password errata");
    }

}
