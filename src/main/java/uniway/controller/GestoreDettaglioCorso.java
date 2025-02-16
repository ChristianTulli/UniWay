package uniway.controller;

import uniway.beans.UtenteBean;
import uniway.model.UtenteInCerca;
import uniway.persistenza.CorsoDAO;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class GestoreDettaglioCorso {
    private static final Logger LOGGER = Logger.getLogger(GestoreDettaglioCorso.class.getName());
    private CorsoDAO corsoDAO;
    private String comune;
    private String ateneo;
    private String tipologia;
    private final GestioneLogin gestioneLogin = GestioneLogin.getInstance();

    public GestoreDettaglioCorso() throws IllegalArgumentException {
        Properties properties = new Properties();
        try (FileInputStream input = new FileInputStream("src/main/resources/config.properties")) {
            properties.load(input);
            corsoDAO = new CorsoDAO(properties.getProperty("db.url"), properties.getProperty("db.username"), properties.getProperty("db.password"));
        } catch (FileNotFoundException e) {
            throw new IllegalArgumentException("File config.properties non trovato", e);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Errore durante il caricamento della configurazione", e);
        }
    }

    public static List<String> getInsegnamenti(String corsoCorrente) {
        // Implementare la logica per ottenere gli insegnamenti
        List<String> insegnamenti = new ArrayList<>();
        return insegnamenti;
    }

    public void aggiungiAiPreferiti(UtenteBean utenteBean, String corsoSelezionato, String ateneoSelezionato) {
        Integer idCorso = corsoDAO.getIdCorsoByNomeAndAteneo(ateneoSelezionato, corsoSelezionato);

        if (gestioneLogin.isFullMode()) {
            try {
                gestioneLogin.getUtenteDAO().aggiungiPreferitiUtente(utenteBean.getUsername(), idCorso);
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Errore durante l'inserimento nei preferiti", e);
            }
        } else {
            gestioneLogin.getUtenti().stream()
                    .filter(u -> u instanceof UtenteInCerca && u.getUsername().equals(utenteBean.getUsername()))
                    .map(u -> (UtenteInCerca) u)
                    .findFirst()
                    .ifPresent(u -> u.getPreferenze().add(idCorso));
        }
    }
}

