package uniway.viewcontroller.fxml;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import uniway.beans.UtenteBean;
import uniway.controller.LogInController;
import uniway.eccezioni.UtenteEsistenteException;
import uniway.eccezioni.UtenteNonTrovatoException;
import uniway.utils.NavigationManager;

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

    private boolean registra(UtenteBean utenteBean) {
        try {
            boolean ok = loginController.registrazione(utenteBean); // può lanciare UtenteEsistenteException
            if (ok) {
                errorLabel.setText("Utente registrato con successo");
            } else {
                // qui rientri quando campi vuoti / password < 6 (nel tuo metodo torni false)
                errorLabel.setText("Username o password non valide (min 6 caratteri).");
            }
            return ok;

        } catch (UtenteEsistenteException e) {
            // caso di business: username già in uso
            errorLabel.setText(e.getMessage()); // "esiste già un utente con username: …"
            return false;

        } catch (Exception e) {
            // fallback per errori imprevisti (DAO, I/O, ecc.)
            errorLabel.setText("Errore inatteso durante la registrazione.");
            return false;
        }
    }

    // REGISTRAZIONE come ISCRITTO
    public void onIscrittoButtonClick(ActionEvent event) {
        UtenteBean utenteBean = new UtenteBean(usernameField.getText(), passwordField.getText(), true);
        if (registra(utenteBean)) {
            NavigationManager.switchScene(event, FXML_ISCRITTO, "UniWay - Seleziona corso (Iscritto)");
        }
    }

    // REGISTRAZIONE come IN CERCA
    public void onRicercaButtonClick(ActionEvent event) {
        UtenteBean utenteBean = new UtenteBean(usernameField.getText(), passwordField.getText(), false);
        if (registra(utenteBean)) {
            NavigationManager.switchScene(event, FXML_RICERCA, "UniWay - Trova corso",
                    InCercaTrovaCorsoViewController.class,
                    c -> c.impostaSchermata(utenteBean));
        }
    }

    // LOGIN
    public void logIn(ActionEvent event) {
        // reset messaggio
        errorLabel.setText("");

        String u = usernameField.getText();
        String p = passwordField.getText();

        try {
            UtenteBean utenteBean = loginController.autenticazione(u, p);

            if (utenteBean.getIscritto()) {
                if (utenteBean.getCorso()) {
                    // iscritto con corso già impostato
                    NavigationManager.switchScene(event, FXML_CORSO_ISCRITTO, "UniWay - Insegnamenti");
                } else {
                    // iscritto ma deve selezionare il corso
                    NavigationManager.switchScene(event, FXML_ISCRITTO, "UniWay - Seleziona corso (Iscritto)");
                }
            } else {
                // utente in cerca
                NavigationManager.switchScene(event, FXML_RICERCA, "UniWay - Trova corso",
                        InCercaTrovaCorsoViewController.class,
                        c -> c.impostaSchermata(utenteBean));
            }


        } catch (UtenteNonTrovatoException e) {
            errorLabel.setText(e.getMessage());

        } catch (IllegalArgumentException e) {
            // usata sia per campi vuoti che per password errata
            errorLabel.setText(e.getMessage()); // "inserire username e password" / "Password errata"

        } catch (Exception e) {
            // fallback imprevisti (I/O, DB, ecc.)
            errorLabel.setText("Errore inatteso durante il login.");
        }
    }

}
