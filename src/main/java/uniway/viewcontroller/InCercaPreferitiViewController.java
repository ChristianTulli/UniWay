package uniway.viewcontroller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.stage.Stage;
import uniway.beans.UtenteBean;
import uniway.controller.InCercaPreferitiController;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

public class InCercaPreferitiViewController implements Initializable {
    private UtenteBean utenteBean;
    private InCercaPreferitiController inCercaPreferitiController=new InCercaPreferitiController();
    private static final Logger LOGGER = Logger.getLogger(InCercaPreferitiViewController.class.getName());

    @FXML
    private ListView<String> listView;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        // Listener per il doppio click su un elemento della ListView
        listView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                try {
                    apriDettaglioCorso(newValue);
                } catch (IOException e) {
                    LOGGER.log(Level.SEVERE, "Errore nell'apertura della visualizzazione del corso", e);
                }
            }
        });
    }
    public void impostaSchermata(UtenteBean utenteBean){
        this.utenteBean=utenteBean;
        try {
            listView.getItems().addAll(inCercaPreferitiController.getPreferiti(utenteBean.getUsername()));
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Errore nell'impostazione della schermata", e);
        }
    }

    public void apriDettaglioCorso(String corso) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/InCercaDettaglioCorsoUI.fxml"));
        Parent newRoot = loader.load();
        List<String> corsiSimili=new ArrayList<>();
        InCercaDettaglioCorsoViewController controller = loader.getController();
        controller.impostaSchermata(utenteBean, corso, corsiSimili);

        Stage stage = (Stage) listView.getScene().getWindow(); // usa un nodo qualsiasi
        Scene scene = new Scene(newRoot);
        stage.setScene(scene);
        stage.show();
    }

    public void goToRicerca(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/InCercaTrovaCorsoUI.fxml"));
        Parent newRoot = loader.load();

        // Passa l'UtenteBean al nuovo controller
        InCercaTrovaCorsoViewController inCercaTrovaCorsoViewController = loader.getController();
        inCercaTrovaCorsoViewController.impostaSchermata(utenteBean);  // Mantiene l'utente attivo

        // Cambia schermata
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Scene scene = new Scene(newRoot);
        stage.setScene(scene);
        stage.show();
    }

    public void logOut (ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/view/LogInUI.fxml")));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
}
