package uniway.viewcontroller.fxml;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import uniway.beans.InsegnamentoBean;
import uniway.beans.UtenteBean;
import uniway.controller.CommentaEValutaInsegnamentoController;
import uniway.eccezioni.RecensioneNonSalvataException;
import uniway.patterns.NavigationManagerFacade;

import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

public class IscrittoCommentaViewController implements Initializable {

    private InsegnamentoBean insegnamentoBean;
    private UtenteBean utenteBean;
    private final CommentaEValutaInsegnamentoController commentaEValutaInsegnamentoController= new CommentaEValutaInsegnamentoController();
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
        //GUARDA L'INITIALIZE DI ISCRITTOINSEGNAMENTIVIEWCONTROLLER PER IMPOSTARE LE ETICHETTE INVECE DI TUTTI I METODI QUI SOTTO
        utenteBean = commentaEValutaInsegnamentoController.getUtenteBean();
        curriculumLabel.setText(utenteBean.getCurriculum());
        corsoLabel.setText(utenteBean.getNomeCorso());
        ateneoLabel.setText(utenteBean.getNomeAteneo());


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

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Recensione salvata");
        alert.setHeaderText(null);
        try {
            commentaEValutaInsegnamentoController.recensisciInsegnamento(utenteBean, insegnamentoBean, commentoArea.getText(), valutazioneSelezionata);
        }catch (RecensioneNonSalvataException e) {
            alert.setContentText(e.getMessage());
            alert.showAndWait();
            goBack(); // torna alla lista insegnamenti
            return;
        }
        alert.setContentText("La tua recensione è stata registrata con successo!");
        alert.showAndWait();
        goBack(); // torna alla lista insegnamenti
    }

    public void impostaSchermata(InsegnamentoBean insegnamentoBean){
        this.insegnamentoBean = insegnamentoBean;
        insegnamentoLabel.setText(insegnamentoBean.getNome());
    }

    //Torna alla schermata degli insegnamenti passando l'utente
    public void goBack() {
        try {
            var stage = (javafx.stage.Stage) commentoArea.getScene().getWindow();
            NavigationManagerFacade.switchScene(stage, FXML_INSEGNAMENTI, TITOLO_INSEGNAMENTI);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Errore nell'apertura della schermata della lista dei corsi", e);
        }
    }

    public void logOut(ActionEvent event) {
        commentaEValutaInsegnamentoController.logOut();
        NavigationManagerFacade.switchScene(event, FXML_LOGIN, TITOLO_LOGIN);
    }
}

