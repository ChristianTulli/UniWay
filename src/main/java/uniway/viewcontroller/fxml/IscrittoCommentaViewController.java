package uniway.viewcontroller.fxml;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import uniway.beans.InsegnamentoBean;
import uniway.beans.UtenteBean;
import uniway.controller.IscrittoCommentaController;
import uniway.utils.NavigationManager;

import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

public class IscrittoCommentaViewController implements Initializable {

    private UtenteBean utenteBean;
    private InsegnamentoBean insegnamentoBean;
    private final IscrittoCommentaController iscrittoCommentaController = new IscrittoCommentaController();
    private int valutazioneSelezionata = 0;
    private static final Logger LOGGER = Logger.getLogger(IscrittoCommentaViewController.class.getName());

    private static final String FXML_INSEGNAMENTI = "/view/IscrittoInsegnamentiUI.fxml";
    private static final String FXML_LOGIN         = "/view/LogInUI.fxml";
    private static final String TITOLO_INSEGNAMENTI = "UniWay - Insegnamenti (Iscritto)";
    private static final String TITOLO_LOGIN        = "UniWay - Login";

    @FXML private Label corsoLabel;
    @FXML private Label curriculumLabel;
    @FXML private Label ateneoLabel;
    @FXML private Label insegnamentoLabel;

    @FXML private TextArea commentoArea;
    @FXML private Button salvaButton;

    @FXML private ImageView star1;
    @FXML private ImageView star2;
    @FXML private ImageView star3;
    @FXML private ImageView star4;
    @FXML private ImageView star5;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        star1.setOnMouseClicked(e -> aggiornaStelle(1));
        star2.setOnMouseClicked(e -> aggiornaStelle(2));
        star3.setOnMouseClicked(e -> aggiornaStelle(3));
        star4.setOnMouseClicked(e -> aggiornaStelle(4));
        star5.setOnMouseClicked(e -> aggiornaStelle(5));
        commentoArea.textProperty().addListener((obs, oldText, newText) -> aggiornaStatoPulsante());
    }

    public void setUtenteBean(UtenteBean utenteBean) {
        this.utenteBean = utenteBean;
        curriculumLabel.setText(utenteBean.getCurriculum());
    }

    public void setCorso(String nomeCorso, String nomeAteneo){
        corsoLabel.setText(nomeCorso);
        ateneoLabel.setText(nomeAteneo);
    }

    public void setInsegnamentoBean(InsegnamentoBean insegnamentoBean) {
        this.insegnamentoBean = insegnamentoBean;
        insegnamentoLabel.setText(insegnamentoBean.getNome());
    }

    public void setIscrittoVisualizzaInsegnamentiController(IscrittoInsegnamentiController iscrittoInsegnamentiController){
        iscrittoCommentaController.setIscrittoVisualizzaInsegnamentiController(iscrittoInsegnamentiController);
    }

    public void impostaSchermata(UtenteBean utenteBean, String nomeCorso, String nomeAteneo,
                                 InsegnamentoBean insegnamentoBean,
                                 IscrittoInsegnamentiController iscrittoInsegnamentiController){
        setUtenteBean(utenteBean);
        setCorso(nomeCorso,nomeAteneo);
        setInsegnamentoBean(insegnamentoBean);
        setIscrittoVisualizzaInsegnamentiController(iscrittoInsegnamentiController);
    }

    private void aggiornaStelle(int valore) {
        valutazioneSelezionata = valore;
        Image piena = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/image/stella.png")));
        Image vuota = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/image/stella-vuota.png")));

        star1.setImage(valore >= 1 ? piena : vuota);
        star2.setImage(valore >= 2 ? piena : vuota);
        star3.setImage(valore >= 3 ? piena : vuota);
        star4.setImage(valore >= 4 ? piena : vuota);
        star5.setImage(valore >= 5 ? piena : vuota);

        aggiornaStatoPulsante();
    }

    private void aggiornaStatoPulsante() {
        boolean commentoNonVuoto = commentoArea.getText() != null && !commentoArea.getText().trim().isEmpty();
        salvaButton.setDisable(!(valutazioneSelezionata > 0 && commentoNonVuoto));
    }

    @FXML
    private void salvaRecensione(ActionEvent event) {
        iscrittoCommentaController.salvaRecensione(utenteBean, insegnamentoBean, commentoArea.getText(), valutazioneSelezionata);

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Recensione salvata");
        alert.setHeaderText(null);
        alert.setContentText("La tua recensione è stata registrata con successo!");
        alert.showAndWait();

        goBack(); // torna alla lista insegnamenti
    }

    /** Torna alla schermata degli insegnamenti passando l'utente */
    public void goBack() {
        try {
            var stage = (javafx.stage.Stage) commentoArea.getScene().getWindow();
            NavigationManager.switchScene(
                    stage,
                    FXML_INSEGNAMENTI,
                    TITOLO_INSEGNAMENTI,
                    IscrittoInsegnamentiViewController.class,
                    c -> c.impostaSchermata(utenteBean)
            );
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Errore nell'apertura della schermata della lista dei corsi", e);
        }
    }

    public void logOut(ActionEvent event) {
        NavigationManager.switchScene(event, FXML_LOGIN, TITOLO_LOGIN);
    }
}

