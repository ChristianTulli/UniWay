package uniway.controller;

import uniway.beans.UtenteBean;
import uniway.model.Utente;
import uniway.model.UtenteInCerca;
import uniway.model.UtenteIscritto;
import uniway.persistenza.UtenteDAO;

import java.io.IOException;
import java.util.Optional;

public class LogInController {

    private UtenteDAO utenteDAO;

    public LogInController() throws IllegalArgumentException {
        PersistenzaController persistenzaController = PersistenzaController.getInstance();
        this.utenteDAO = persistenzaController.getUtenteDAO();
    }

    //per la registrazione controlliamo se i dati inseriti momentaneamente nella classe bean sono accettabili e istanziamo un oggetto user in caso positivo, confermando la registrazione

    public boolean registrazione(UtenteBean utenteBean) throws IOException {
        if (utenteBean.getUsername().isEmpty() || utenteBean.getPassword().isEmpty() || utenteBean.getPassword().length() < 6) {
            return false;
        }
        Optional<Utente> existingUser = utenteDAO.ottieniUtenti().stream().filter(u -> u.getUsername().equals(utenteBean.getUsername())).findFirst();

        if (existingUser.isPresent()) {
            return false; // Username già esistente
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

    public Optional<UtenteBean> autenticazione(String username, String password) throws IOException {
        return utenteDAO.ottieniUtenti().stream().filter(utente -> utente.getUsername().equals(username) && utente.getPassword().equals(password)).findFirst().map(utente -> {
            if (utente instanceof UtenteIscritto utenteIscritto) {
                return new UtenteBean(utenteIscritto.getUsername(), utenteIscritto.getPassword(), true, utenteIscritto.getIdCorso(), utenteIscritto.getCurriculum()
                        //aggiungere attributo curriculum
                );
            } else {
                return new UtenteBean(utente.getUsername(), utente.getPassword(), false);
            }
        });
    }

}
