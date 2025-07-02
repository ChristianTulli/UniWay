package uniway.controller;

import uniway.persistenza.UtenteDAO;
import uniway.persistenza.UtenteDB;
import uniway.persistenza.UtenteFS;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PersistenzaController {
    private static final Logger LOGGER = Logger.getLogger(PersistenzaController.class.getName());
    private static PersistenzaController instance;
    //private DemoDAO demoDAO;
    Properties properties = new Properties();
    private Connection connessione;
    private String isFullMode;
    private UtenteDAO utenteDAO;
    private String dbUrl;
    private String dbUsername;
    private String dbPassword;
    private String errore = "errore";

    private PersistenzaController() throws IllegalArgumentException {
        try (FileInputStream input = new FileInputStream("src/main/resources/config.properties")) {
            properties.load(input);
            String mode = properties.getProperty("persistence.mode");
            isFullMode = properties.getProperty("running.mode");

            if ("full".equals(isFullMode)) {
                dbUrl = properties.getProperty("db.url");
                dbUsername = properties.getProperty("db.username");
                dbPassword = properties.getProperty("db.password");
                this.connessione = DriverManager.getConnection(dbUrl, dbUsername, dbPassword);
                if ("file".equals(mode)) {
                    utenteDAO = new UtenteFS(properties.getProperty("file.path"));
                } else if ("db".equals(mode)) {
                    utenteDAO = new UtenteDB(connessione);
                }

            } else if ("demo".equals(isFullMode)) {
                //this.utenteDAO = new DemoDAO();
                // aggiungere funzionalità
            }
        } catch (FileNotFoundException e) {
            throw new IllegalArgumentException("File config.properties non trovato", e);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, errore, e);
        }
    }

    public static PersistenzaController getInstance() {
        try {
            if (instance == null) {
                synchronized (PersistenzaController.class) {  // Blocco sincronizzato
                    instance = new PersistenzaController();
                }
            }
        } catch (IllegalArgumentException e) {
            LOGGER.log(Level.SEVERE, "errore", e);
        }
        return instance;
    }

    public UtenteDAO getUtenteDAO() {
        return utenteDAO;
    }

    public Connection getConnessione() {
        return connessione;
    }
}

