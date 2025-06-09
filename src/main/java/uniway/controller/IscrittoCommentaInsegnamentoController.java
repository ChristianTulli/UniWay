package uniway.controller;

import uniway.beans.InsegnamentoBean;
import uniway.beans.UtenteBean;
import uniway.model.Insegnamento;
import uniway.model.Utente;
import uniway.model.UtenteIscritto;
import uniway.persistenza.RecensioneDAO;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class IscrittoCommentaInsegnamentoController {

    private static final Logger LOGGER = Logger.getLogger(IscrittoCommentaInsegnamentoController.class.getName());
    private RecensioneDAO recensioneDAO;
    private IscrittoVisualizzaInsegnamentiController iscrittoVisualizzaInsegnamentiController;

    public IscrittoCommentaInsegnamentoController() throws IllegalArgumentException {
        Properties properties = new Properties();
        try (FileInputStream input = new FileInputStream("src/main/resources/config.properties")) {
            properties.load(input);
            String username= properties.getProperty("db.username");
            String dbUrl = properties.getProperty("db.url");
            String password = properties.getProperty("db.password");

            recensioneDAO = new RecensioneDAO(dbUrl, username, password);
        } catch (FileNotFoundException e) {
            throw new IllegalArgumentException("File config.properties non trovato", e);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Errore durante il caricamento della configurazione", e);
        }

    }

    public void setIscrittoVisualizzaInsegnamentiController(IscrittoVisualizzaInsegnamentiController iscrittoVisualizzaInsegnamentiController) {
        this.iscrittoVisualizzaInsegnamentiController = iscrittoVisualizzaInsegnamentiController;
    }

    public void salvaRecensione(UtenteBean utenteBean, InsegnamentoBean insegnamentoBean, String testo, Integer valutazione){
        Insegnamento insegnamento = iscrittoVisualizzaInsegnamentiController.passaInsegnamento(insegnamentoBean);
        recensioneDAO.setRecesnione(testo, valutazione, utenteBean.getUsername(), insegnamento.getId());

    }
}
