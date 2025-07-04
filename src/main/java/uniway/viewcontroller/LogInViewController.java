package uniway.viewcontroller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import uniway.beans.UtenteBean;
import uniway.controller.LogInController;

import java.io.IOException;
import java.util.Optional;


public class LogInViewController {
    private String interfacciaIscritto = "/view/IscrittoSelezionaCorsoUI.fxml";
    private String interfacciaRicerca = "/view/InCercaTrovaCorsoUI.fxml";
    private String interfacciaCorsoIscritto = "/view/IscrittoInsegnamentiUI.fxml";// fare interfaccia per iscritto con corso selezionato, per poter commentare gli insegnamenti


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

    private LogInController loginController = new LogInController();

    //fa apparire i tasti in cerca e iscritto se ho cliccato su registrati
    public void onRegisratiButtonClick() {
        registratiButton.setDisable(true);
        iscrittoButton.setVisible(true);
        ricercaButton.setVisible(true);
        iscrittoButton.setDisable(false);
        ricercaButton.setDisable(false);
    }


    boolean registra(UtenteBean utenteBean) throws IOException {
        if (loginController.registrazione(utenteBean)) {
            errorLabel.setText("Utente registrato con successo");
            return true;
        } else {
            errorLabel.setText("username o password non valide");
            return false;
        }
    }

    private void caricaInterfaccia(ActionEvent event, String percorsoFXML, UtenteBean utenteBean) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(percorsoFXML));
        Parent newRoot = loader.load();

        // Ottieni il controller della nuova interfaccia
        Object controller = loader.getController();

        // Passa l'utente al nuovo controller, verificando il tipo
        if (controller instanceof IscrittoSelezionaCorsoViewController iscrittoSelezionaCorsoViewController) {
            iscrittoSelezionaCorsoViewController.impostaSchermata(utenteBean);
        } else if (controller instanceof InCercaTrovaCorsoViewController inCercaTrovaCorsoViewController) {
            inCercaTrovaCorsoViewController.impostaSchermata(utenteBean);
        } else if (controller instanceof IscrittoInsegnamentiViewController iscrittoInsegnamentiViewController) {
            iscrittoInsegnamentiViewController.impostaSchermata(utenteBean);
        }
        // Mostra la nuova schermata
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(newRoot));
        stage.show();
    }

    //mi sto registrando come Iscritto
    public void onIscrittoButtonClick(ActionEvent event) throws IOException {
        UtenteBean utenteBean = new UtenteBean(usernameField.getText(), passwordField.getText(), true);
        if (registra(utenteBean)) {
            caricaInterfaccia(event, interfacciaIscritto, utenteBean);
        }
    }

    //mi sto registrando come In Cerca
    public void onRicercaButtonClick(ActionEvent event) throws IOException {
        UtenteBean utenteBean = new UtenteBean(usernameField.getText(), passwordField.getText(), false);
        if (registra(utenteBean)) {
            caricaInterfaccia(event, interfacciaRicerca, utenteBean);
        }
    }

    //sto facendo LogIn
    public void logIn(ActionEvent event) throws IOException {
        Optional<UtenteBean> utenteOpt = loginController.autenticazione(usernameField.getText(), passwordField.getText());

        if (utenteOpt.isPresent()) {
            UtenteBean utenteBean = utenteOpt.get();

            if (utenteBean.getIscritto()) {
                if (utenteBean.getIdCorso() != null && utenteBean.getCurriculum() != null) {
                    caricaInterfaccia(event, interfacciaCorsoIscritto, utenteBean);
                } else {
                    caricaInterfaccia(event, interfacciaIscritto, utenteBean);
                }
            } else {
                caricaInterfaccia(event, interfacciaRicerca, utenteBean);
            }
        } else {
            errorLabel.setText("Nessun utente o password corrispondente");
        }
    }
} 