package uniway.viewcontroller;

import javafx.scene.control.*;
import uniway.beans.UtenteBean;
import uniway.controller.GestioneLogin;


import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;
import java.util.Optional;


public class AccessoController {
    private Scene scene;
    private Stage stage;
    private Parent root;
    private String interfacciaIscritto = "/view/iscritto-selezione.fxml";
    private String interfacciaRicerca = "/view/ricerca-home.fxml";


    @FXML
    private Label errorLabel;

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Button registratiButton;

    @FXML
    private Button iscrittoButton;

    @FXML
    private Button ricercaButton;

    private final GestioneLogin gestioneLogin = GestioneLogin.getInstance(); //non creare una nuova istanza ma usare la stessa, altrimenti creo una nuova lista

    public void onRegisratiButtonClick() {
        registratiButton.setDisable(true);
        iscrittoButton.setVisible(true);
        ricercaButton.setVisible(true);
        iscrittoButton.setDisable(false);
        ricercaButton.setDisable(false);
    }

    boolean registra(UtenteBean utenteBean) {
        if (gestioneLogin.registrazione(utenteBean)) {
            errorLabel.setText("Utente registrato con successo");
            return true;
        } else {
            errorLabel.setText("username o password non valide");
            return false;
        }
    }

    private void caricaInterfaccia(ActionEvent event, String percorsoFXML) throws IOException {
        root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource(percorsoFXML)));
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }


    public void onIscrittoButtonClick(ActionEvent event) throws IOException {
        UtenteBean utenteBean = new UtenteBean(usernameField.getText(), passwordField.getText(), true);
        if (registra(utenteBean)) {
            caricaInterfaccia(event, interfacciaIscritto);
        }
    }

    public void onRicercaButtonClick(ActionEvent event) throws IOException {
        UtenteBean utenteBean = new UtenteBean(usernameField.getText(), passwordField.getText(), false);
        if (registra(utenteBean)) {
            caricaInterfaccia(event, interfacciaRicerca);
        }
    }

    public void logIn(ActionEvent event) throws IOException {
        Optional<UtenteBean> utenteOpt = gestioneLogin.autenticazione(usernameField.getText(), passwordField.getText());

        if (utenteOpt.isPresent()) {
            UtenteBean utenteBean = utenteOpt.get();

            if (utenteBean.getIscritto()) {
                caricaInterfaccia(event, interfacciaIscritto);
            } else {
                caricaInterfaccia(event, interfacciaRicerca);
            }
        } else {
            errorLabel.setText("Nessun utente o password corrispondente");
        }
    }
} 