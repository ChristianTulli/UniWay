
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

    //pattern singleton
    private static GestioneLogin instance;
    private final List<Utente> utenti= new ArrayList<>();
    private UtenteDAO utenteDAO;
    private boolean isFullMode;
    private static final Logger LOGGER = Logger.getLogger(GestioneLogin.class.getName());
    private String errore="errore";


    private GestioneLogin() throws IllegalArgumentException {
        Properties properties = new Properties();
        try (FileInputStream input=new FileInputStream("src/main/resources/config.properties")) {
            properties.load(input);

            isFullMode="full".equals(properties.getProperty("running.mode"));

            if(isFullMode) {
                String mode = properties.getProperty("persistence.mode");
                if ("file".equals(mode)) {
                    utenteDAO = new UtenteFS(properties.getProperty("file.path"));
                } else if ("db".equalsIgnoreCase(mode)) {
                    utenteDAO = new UtenteDB(properties.getProperty("db.url"), properties.getProperty("db.username"), properties.getProperty("db.password"));
                } else {
                    throw new IllegalArgumentException("modalita' di persistenza non valida: " + mode);
                }

                //carica gli utenti salvati sulla lista
                utenti.addAll(utenteDAO.ottieniUtenti());
            }
        }catch (FileNotFoundException e){
            throw new IllegalArgumentException("File config.properties non trovato", e);
        }catch (Exception e){
            LOGGER.log(Level.SEVERE, errore, e);
        }
    }

    static {
        try {
            instance = new GestioneLogin();
        }catch (IllegalArgumentException e){
            LOGGER.log(Level.SEVERE, "errore", e);
        }
    }

    public static GestioneLogin getInstance() {
        try {
            if (instance == null) {
                synchronized (GestioneLogin.class) {  // Blocco sincronizzato
                    if (instance == null) {  // Controllo doppio per evitare più istanze
                        instance = new GestioneLogin();
                    }
                }
            }
        }catch (IllegalArgumentException e){
            LOGGER.log(Level.SEVERE, "errore", e);
        }
        return instance;
    }


    //per la registrazione controlliamo se i dati inseriti momentaneamente nella classe bean sono accettabili e istanziamo un oggetto user in caso positivo, confermando la registrazione

    public boolean registrazione(UtenteBean utenteBean) {
        if(utenteBean.getUsername().isEmpty() || utenteBean.getPassword().isEmpty() || utenteBean.getPassword().length() < 6) {
            return false;
        }
            Optional<Utente> existingUser = utenti.stream()
                    .filter(u -> u.getUsername().equals(utenteBean.getUsername()))
                    .findFirst();

            if (existingUser.isPresent()) {
                return false; // Username già esistente
            }
            Utente utente;
            if(utenteBean.getIscritto()) {
                utente = new UtenteIscritto(utenteBean.getUsername(), utenteBean.getPassword(),utenteBean.getIscritto());
            }else{
                utente = new UtenteInCerca(utenteBean.getUsername(), utenteBean.getPassword(), utenteBean.getIscritto());
            }
        utenti.add(utente);

        //se siamo in modalita' full salviamo nel file/db
            if(isFullMode) {
                try{
                    utenteDAO.salvaUtente(utente);
                } catch (Exception e){
                    LOGGER.log(Level.SEVERE, errore, e);
                }
            }

            return true;
            }

    public boolean autenticazione(UtenteBean utenteBean) {
        return utenti.stream()
                .anyMatch(utente -> utente.getUsername().equals(utenteBean.getUsername()) && utente.getPassword().equals(utenteBean.getPassword()));

    }
}
