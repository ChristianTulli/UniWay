package uniway.viewcontroller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import uniway.beans.UtenteBean;
import uniway.controller.InCercaPreferitiController;

import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class InCercaPreferitiViewController implements Initializable {

    private UtenteBean utenteBean;
    private final InCercaPreferitiController inCercaPreferitiController = new InCercaPreferitiController();
    private static final Logger LOGGER = Logger.getLogger(InCercaPreferitiViewController.class.getName());

    @FXML
    private ListView<String> listView;

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
            listView.getItems().addAll(inCercaPreferitiController.getPreferiti(utenteBean.getUsername()));
        } catch (IOException e) {
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
                try {
                    apriDettaglioCorso(corso);
                } catch (IOException e) {
                    LOGGER.log(Level.SEVERE, "Errore nell'apertura del dettaglio", e);
                }
            } else if (risposta == rimuovi) {
                rimuoviCorsoDaiPreferiti(corso);
            }
        });
    }

    private void rimuoviCorsoDaiPreferiti(String corso) {
        try {
            inCercaPreferitiController.rimuoviPreferito(utenteBean.getUsername(), corso);
            listView.getItems().remove(corso);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Errore durante la rimozione del corso dai preferiti", e);
        }
    }

    private void apriDettaglioCorso(String corso) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/InCercaDettaglioCorsoUI.fxml"));
        Parent newRoot = loader.load();
        List<String> corsiSimili = new ArrayList<>();

        InCercaDettaglioCorsoViewController controller = loader.getController();
        controller.impostaSchermata(utenteBean, corso, corsiSimili);

        Stage stage = (Stage) listView.getScene().getWindow();
        stage.setScene(new Scene(newRoot));
        stage.show();
    }

    public void goToRicerca(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/InCercaTrovaCorsoUI.fxml"));
        Parent newRoot = loader.load();

        InCercaTrovaCorsoViewController inCercaTrovaCorsoViewController = loader.getController();
        inCercaTrovaCorsoViewController.impostaSchermata(utenteBean);

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(newRoot));
        stage.show();
    }

    public void logOut(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/view/LogInUI.fxml")));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.show();
    }
}

