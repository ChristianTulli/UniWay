package uniway.viewcontroller.fxml;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import uniway.beans.UtenteBean;
import uniway.controller.LogInController;
import uniway.utils.NavigationManager;

import java.io.IOException;
import java.util.Optional;

public class LogInViewController {

    private static final String FXML_ISCRITTO = "/view/IscrittoSelezionaCorsoUI.fxml";
    private static final String FXML_RICERCA  = "/view/InCercaTrovaCorsoUI.fxml";
    private static final String FXML_CORSO_ISCRITTO = "/view/IscrittoInsegnamentiUI.fxml";

    @FXML private Label errorLabel;
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Button registratiButton;
    @FXML private Button iscrittoButton;
    @FXML private Button ricercaButton;

    private final LogInController loginController = new LogInController();

    public void onRegisratiButtonClick() {
        registratiButton.setDisable(true);
        iscrittoButton.setVisible(true);
        ricercaButton.setVisible(true);
        iscrittoButton.setDisable(false);
        ricercaButton.setDisable(false);
    }

    private boolean registra(UtenteBean utenteBean) throws IOException {
        if (loginController.registrazione(utenteBean)) {
            errorLabel.setText("Utente registrato con successo");
            return true;
        } else {
            errorLabel.setText("Username o password non valide");
            return false;
        }
    }

    // REGISTRAZIONE come ISCRITTO
    public void onIscrittoButtonClick(ActionEvent event) throws IOException {
        UtenteBean utenteBean = new UtenteBean(usernameField.getText(), passwordField.getText(), true);
        if (registra(utenteBean)) {
            NavigationManager.switchScene(event, FXML_ISCRITTO, "UniWay - Seleziona corso (Iscritto)",
                    IscrittoSelezionaCorsoViewController.class,
                    c -> c.impostaSchermata(utenteBean));
        }
    }

    // REGISTRAZIONE come IN CERCA
    public void onRicercaButtonClick(ActionEvent event) throws IOException {
        UtenteBean utenteBean = new UtenteBean(usernameField.getText(), passwordField.getText(), false);
        if (registra(utenteBean)) {
            NavigationManager.switchScene(event, FXML_RICERCA, "UniWay - Trova corso",
                    InCercaTrovaCorsoViewController.class,
                    c -> c.impostaSchermata(utenteBean));
        }
    }

    // LOGIN
    public void logIn(ActionEvent event) throws IOException {
        Optional<UtenteBean> utenteOpt = loginController.autenticazione(usernameField.getText(), passwordField.getText());

        if (utenteOpt.isEmpty()) {
            errorLabel.setText("Nessun utente o password corrispondente");
            return;
        }

        UtenteBean utenteBean = utenteOpt.get();

        if (utenteBean.getIscritto()) {
            // Ha già corso/curriculum ⇒ vai alla schermata insegnamenti
            if (utenteBean.getIdCorso() != null && utenteBean.getCurriculum() != null) {
                NavigationManager.switchScene(event, FXML_CORSO_ISCRITTO, "UniWay - Insegnamenti",
                        IscrittoInsegnamentiViewController.class,
                        c -> c.impostaSchermata(utenteBean));
            } else {
                // Deve selezionare il corso
                NavigationManager.switchScene(event, FXML_ISCRITTO, "UniWay - Seleziona corso (Iscritto)",
                        IscrittoSelezionaCorsoViewController.class,
                        c -> c.impostaSchermata(utenteBean));
            }
        } else {
            // Utente in cerca
            NavigationManager.switchScene(event, FXML_RICERCA, "UniWay - Trova corso",
                    InCercaTrovaCorsoViewController.class,
                    c -> c.impostaSchermata(utenteBean));
        }
    }
}
