package uniway.controller;

import uniway.beans.UtenteBean;
import uniway.model.UtenteInCerca;
import uniway.model.UtenteIscritto;
import uniway.model.Utente;
import uniway.persistenza.UtenteDAO;
import uniway.persistenza.UtenteDB;
import uniway.persistenza.UtenteFS;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class GestioneLogin {

    // Singleton thread-safe con volatile
    private static volatile GestioneLogin instance;
    private final List<Utente> utenti = new ArrayList<>();
    private UtenteDAO utenteDAO;
    private boolean isFullMode;
    private static final Logger LOGGER = Logger.getLogger(GestioneLogin.class.getName());

    private GestioneLogin() {
        try {
            Properties properties = new Properties();
            try (FileInputStream input = new FileInputStream("src/main/resources/config.properties")) {
                properties.load(input);

                isFullMode = "full".equals(properties.getProperty("running.mode"));

                String mode = properties.getProperty("persistence.mode");
                if ("file".equals(mode)) {
                    utenteDAO = new UtenteFS(properties.getProperty("file.path"));
                } else if ("db".equalsIgnoreCase(mode)) {
                    utenteDAO = new UtenteDB(properties.getProperty("db.url"), properties.getProperty("db.username"), properties.getProperty("db.password"));
                } else {
                    throw new IllegalArgumentException("Modalità di persistenza non valida: " + mode);
                }

                // Carica gli utenti solo se in modalità Full
                if (isFullMode) {
                    utenti.addAll(utenteDAO.ottieniUtenti());
                }

            }
        } catch (FileNotFoundException e) {
            LOGGER.log(Level.SEVERE, "File config.properties non trovato", e);
            throw new RuntimeException("Errore critico: impossibile avviare GestioneLogin", e);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Errore durante l'inizializzazione", e);
            throw new RuntimeException("Errore critico: impossibile avviare GestioneLogin", e);
        }
    }

    public static GestioneLogin getInstance() {
        if (instance == null) {
            synchronized (GestioneLogin.class) {  // Blocco sincronizzato
                if (instance == null) { // Doppio controllo per evitare più istanze
                    instance = new GestioneLogin();
                }
            }
        }
        return instance;
    }

    public boolean registrazione(UtenteBean utenteBean) {
        if (utenteBean.getUsername().isEmpty() || utenteBean.getPassword().isEmpty() || utenteBean.getPassword().length() < 6) {
            return false;
        }

        if (utenti.stream().anyMatch(u -> u.getUsername().equals(utenteBean.getUsername()))) {
            return false; // Username già esistente
        }

        Utente utente = utenteBean.getIscritto()
                ? new UtenteIscritto(utenteBean.getUsername(), utenteBean.getPassword(), true)
                : new UtenteInCerca(utenteBean.getUsername(), utenteBean.getPassword(), false);

        utenti.add(utente);

        if (isFullMode) {
            try {
                utenteDAO.salvaUtente(utente);
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Errore durante la registrazione", e);
                return false;
            }
        }

        return true;
    }

    public Optional<UtenteBean> autenticazione(String username, String password) {
        return utenti.stream()
                .filter(utente -> utente.getUsername().equals(username) && utente.getPassword().equals(password))
                .findFirst()
                .map(utente -> new UtenteBean(
                        utente.getUsername(),
                        utente.getPassword(),
                        utente instanceof UtenteIscritto
                ));
    }

    public boolean isFullMode() {
        return isFullMode;
    }

    public UtenteDAO getUtenteDAO() {
        return utenteDAO;
    }

    public List<Utente> getUtenti() {
        return utenti;
    }
}
