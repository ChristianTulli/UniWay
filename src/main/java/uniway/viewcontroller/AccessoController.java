
package uniway.viewcontroller;

import uniway.beans.UtenteBean;
import uniway.controller.GestioneLogin;


import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;


public class AccessoController {
    private Scene scene;
    private Stage stage;
    private Parent root;


    @FXML
    private Label errorLabel;

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    private final GestioneLogin gestioneLogin= GestioneLogin.getInstance(); //non creare una nuova istanza ma usare la stessa, altrimenti creo una nuova lista

    public void onRegisratiButtonClick() {
        UtenteBean utenteBean=new UtenteBean(usernameField.getText(), passwordField.getText());
        if(gestioneLogin.registrazione(utenteBean)){
            errorLabel.setText("Utente registrato con successo");
        }else {
            errorLabel.setText("username o password non valide");
        }
    }

    public void logIn(ActionEvent event) throws IOException {
        UtenteBean utenteBean=new UtenteBean(usernameField.getText(), passwordField.getText());
        if(gestioneLogin.autenticazione(utenteBean)) {
            root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/view/ricerca.fxml")));
            stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        }else {
            errorLabel.setText("Credenziali non valide");
        }

    }
}