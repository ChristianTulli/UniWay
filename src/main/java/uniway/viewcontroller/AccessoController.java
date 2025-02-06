
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


public class AccessoController {
    private Scene scene;
    private Stage stage;
    private Parent root;
    private String interfacciaAccesso = "/view/ricerca.fxml";
    private String errore= "Credenziali non valide";


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

    private final GestioneLogin gestioneLogin= GestioneLogin.getInstance(); //non creare una nuova istanza ma usare la stessa, altrimenti creo una nuova lista

    public void onRegisratiButtonClick() {
        registratiButton.setDisable(true);
        iscrittoButton.setVisible(true);
        ricercaButton.setVisible(true);
        iscrittoButton.setDisable(false);
        ricercaButton.setDisable(false);
    }

    public Boolean autentica(UtenteBean utenteBean, ActionEvent event)throws IOException{
        if(gestioneLogin.autenticazione(utenteBean)) {
            root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource(interfacciaAccesso)));
            stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
            return true;
        }else return false;
    }

    public void registra(UtenteBean utenteBean, ActionEvent event) throws IOException {
        if(gestioneLogin.registrazione(utenteBean)){
            errorLabel.setText("Utente registrato con successo");
        }else {
            errorLabel.setText("username o password non valide");
        }
        if(!autentica(utenteBean, event)) {
            errorLabel.setText(errore);
        }
    }


    public void onIscrittoButtonClick(ActionEvent event) throws IOException {
        UtenteBean utenteBean=new UtenteBean(usernameField.getText(), passwordField.getText(), true);
        registra(utenteBean, event);

    }

    public void onRicercaButtonClick(ActionEvent event) throws IOException {
        UtenteBean utenteBean=new UtenteBean(usernameField.getText(), passwordField.getText(), false);
        registra(utenteBean, event);
    }


    public void logIn(ActionEvent event) throws IOException {
        UtenteBean utenteBean=new UtenteBean(usernameField.getText(), passwordField.getText());
        if(!autentica(utenteBean, event)) {
            errorLabel.setText("nessun utente o password corrispondente");
        }
    }
}