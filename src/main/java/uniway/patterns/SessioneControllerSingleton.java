package uniway.patterns;

import uniway.model.Utente;
import uniway.persistenza.*;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SessioneControllerSingleton {
    private static final Logger LOGGER = Logger.getLogger(SessioneControllerSingleton.class.getName());
    private static SessioneControllerSingleton instance = null;
    private final Properties properties = new Properties();
    private Connection connessione;
    private String isFullMode;
    private UtenteDAO utenteDAO;
    private RecensioneDAO recensioneDAO;
    private AteneoDAO ateneoDAO;
    private CorsoDAO corsoDAO;
    private InsegnamentoDAO insegnamentoDAO;
    private String errore = "errore";
    private Utente utenteCorrente;

    private SessioneControllerSingleton() throws IllegalArgumentException {
        try (FileInputStream input = new FileInputStream("src/main/resources/config.properties")) {
            properties.load(input);
            String mode = properties.getProperty("persistence.mode");
            isFullMode = properties.getProperty("running.mode");

            String dbUrl = properties.getProperty("db.url");
            String dbUsername = properties.getProperty("db.username");
            String dbPassword = properties.getProperty("db.password");
            connessione = DriverManager.getConnection(dbUrl, dbUsername, dbPassword);

            if ("full".equals(isFullMode)) {
                if ("file".equals(mode)) {
                    utenteDAO = new UtenteFS(properties.getProperty("file.path"));

                } else if ("db".equals(mode)) {
                    utenteDAO = new UtenteDB(connessione);
                }

                recensioneDAO = new RecensioneDB(connessione);

            } else if ("demo".equals(isFullMode)) {
                utenteDAO = new UtenteDemo();
                recensioneDAO = new RecensioneDemo();
                // aggiungere funzionalità
            }
        } catch (FileNotFoundException e) {
            throw new IllegalArgumentException("File config.properties non trovato", e);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, errore, e);
        }
        ateneoDAO= new AteneoDAO();
        corsoDAO= new CorsoDAO();
        insegnamentoDAO= new InsegnamentoDAO();
    }

    public static synchronized SessioneControllerSingleton getInstance() {
        if (instance == null) {
            instance = new SessioneControllerSingleton();
        }
        return instance;
    }

    public UtenteDAO getUtenteDAO() {
        return utenteDAO;
    }

    public RecensioneDAO getRecensioneDAO() {
        return recensioneDAO;
    }

    public AteneoDAO getAteneoDAO() {
        return ateneoDAO;
    }
    public CorsoDAO getCorsoDAO() {
        return corsoDAO;
    }
    public InsegnamentoDAO getInsegnamentoDAO() {
        return insegnamentoDAO;
    }

    public Connection getConnessione() {
        return connessione;
    }

    public Utente getCurrentUser() {
        return utenteCorrente;
    }
    public void setCurrentUser(Utente utenteCorrente) {
        this.utenteCorrente = utenteCorrente;
    }
    public void logout(){
        this.utenteCorrente = null;
    }
}

