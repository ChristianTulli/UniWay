package uniway.viewcontroller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import uniway.beans.InsegnamentoBean;
import uniway.beans.UtenteBean;
import uniway.controller.IscrittoCommentaInsegnamentoController;
import uniway.controller.IscrittoVisualizzaInsegnamentiController;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

public class IscrittoCommentaInsegnamentoViewController implements Initializable {

    private UtenteBean utenteBean;
    private InsegnamentoBean insegnamentoBean;
    private IscrittoCommentaInsegnamentoController iscrittoCommentaInsegnamentoController= new IscrittoCommentaInsegnamentoController();
    private int valutazioneSelezionata = 0;

    @FXML
    private Label corsoLabel;

    @FXML
    private Label curriculumLabel;

    @FXML
    private Label ateneoLabel;

    @FXML
    private Label insegnamentoLabel;

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

    public void setIscrittoVisualizzaInsegnamentiController(IscrittoVisualizzaInsegnamentiController iscrittoVisualizzaInsegnamentiController){
        iscrittoCommentaInsegnamentoController.setIscrittoVisualizzaInsegnamentiController(iscrittoVisualizzaInsegnamentiController);
    }
    public void impostaSchermata(UtenteBean utenteBean, String nomeCorso, String nomeAteneo, InsegnamentoBean insegnamentoBean, IscrittoVisualizzaInsegnamentiController iscrittoVisualizzaInsegnamentiController){
        setUtenteBean(utenteBean);
        setCorso(nomeCorso,nomeAteneo);
        setInsegnamentoBean(insegnamentoBean);
        setIscrittoVisualizzaInsegnamentiController(iscrittoVisualizzaInsegnamentiController);
    }

    private void aggiornaStelle(int valore) {
        valutazioneSelezionata = valore;
        Image piena = new Image(getClass().getResourceAsStream("/image/stella.png"));
        Image vuota = new Image(getClass().getResourceAsStream("/image/stella-vuota.png"));

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
        iscrittoCommentaInsegnamentoController.salvaRecensione(utenteBean, insegnamentoBean, commentoArea.getText(), valutazioneSelezionata);
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Recensione salvata");
        alert.setHeaderText(null);
        alert.setContentText("La tua recensione è stata registrata con successo!");
        alert.showAndWait();
        goBack();
    }

    public void goBack() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/IscrittoVisualizzaInsegnamentiUI.fxml"));
            Parent newRoot = loader.load();

            IscrittoVisualizzaInsegnamentiViewController controller = loader.getController();
            controller.impostaSchermata(utenteBean);

            Stage stage = (Stage) commentoArea.getScene().getWindow(); // usa un nodo qualsiasi
            Scene scene = new Scene(newRoot);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void logOut (ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/view/LogInUI.fxml")));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
}
