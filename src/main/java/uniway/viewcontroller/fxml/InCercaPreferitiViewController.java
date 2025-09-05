package uniway.viewcontroller.fxml;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.event.ActionEvent;
import javafx.scene.control.*;
import uniway.beans.UtenteBean;
import uniway.controller.InCercaPreferitiController;
import uniway.utils.NavigationManager;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

public class InCercaPreferitiViewController implements Initializable {

    private UtenteBean utenteBean;
    private final InCercaPreferitiController inCercaPreferitiController = new InCercaPreferitiController();
    private static final Logger LOGGER = Logger.getLogger(InCercaPreferitiViewController.class.getName());

    // destinazioni
    private static final String FXML_DETTAGLIO = "/view/InCercaDettaglioCorsoUI.fxml";
    private static final String FXML_RICERCA  = "/view/InCercaTrovaCorsoUI.fxml";
    private static final String FXML_LOGIN    = "/view/LogInUI.fxml";
    private static final String TITOLO_DETTAGLIO = "UniWay - Dettaglio corso";
    private static final String TITOLO_RICERCA   = "UniWay - Trova corso";
    private static final String TITOLO_LOGIN     = "UniWay - Login";

    @FXML private ListView<String> listView;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        listView.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                String selectedItem = listView.getSelectionModel().getSelectedItem();
                if (selectedItem != null) {
                    mostraPopupAzione(selectedItem);
                }
            }
        });
    }

    public void impostaSchermata(UtenteBean utenteBean) {
        this.utenteBean = utenteBean;
        try {
            listView.getItems().setAll(inCercaPreferitiController.getPreferiti(utenteBean.getUsername()));
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Errore nell'impostazione della schermata", e);
        }
    }

    private void mostraPopupAzione(String corso) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Corso preferito");
        alert.setHeaderText("Cosa vuoi fare con il corso selezionato?");
        alert.setContentText(corso);

        ButtonType visualizza = new ButtonType("Visualizza");
        ButtonType rimuovi = new ButtonType("Rimuovi");
        ButtonType annulla = new ButtonType("Annulla", ButtonBar.ButtonData.CANCEL_CLOSE);

        alert.getButtonTypes().setAll(visualizza, rimuovi, annulla);

        alert.showAndWait().ifPresent(risposta -> {
            if (risposta == visualizza) {
                apriDettaglioCorso(corso);
            } else if (risposta == rimuovi) {
                rimuoviCorsoDaiPreferiti(corso);
            }
        });
    }

    private void rimuoviCorsoDaiPreferiti(String corso) {
        try {
            inCercaPreferitiController.rimuoviPreferito(utenteBean.getUsername(), corso);
            listView.getItems().remove(corso);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Errore durante la rimozione del corso dai preferiti", e);
        }
    }

    /** Dettaglio corso (passa UtenteBean + lista corsi simili vuota per ora) */
    private void apriDettaglioCorso(String corso) {
        try {
            var stage = (javafx.stage.Stage) listView.getScene().getWindow();
            List<String> corsiSimili = new ArrayList<>();

            NavigationManager.switchScene(
                    stage,
                    FXML_DETTAGLIO,
                    TITOLO_DETTAGLIO,
                    InCercaDettaglioCorsoViewController.class,
                    c -> c.impostaSchermata(utenteBean, corso, corsiSimili)
            );
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Errore nell'apertura del dettaglio corso", e);
        }
    }

    /** Torna alla ricerca (mantiene l’utente attivo) */
    public void goToRicerca(ActionEvent event) {
        NavigationManager.switchScene(
                event,
                FXML_RICERCA,
                TITOLO_RICERCA,
                InCercaTrovaCorsoViewController.class,
                c -> c.impostaSchermata(utenteBean)
        );
    }

    /** Logout semplice */
    public void logOut(ActionEvent event) {
        NavigationManager.switchScene(event, FXML_LOGIN, TITOLO_LOGIN);
    }
}

