package uniway.viewcontroller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import uniway.beans.RecensioneBean;
import uniway.beans.UtenteBean;
import uniway.controller.InCercaRecensioneController;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;

public class InCercaRecensioneViewController implements Initializable {

    @FXML private Label corsoLabel;
    @FXML private Label ateneoLabel;
    @FXML private Label insegnamentoLabel;
    @FXML private Label curriculumLabel;
    @FXML private Label mediaLabel;
    @FXML private ListView<RecensioneBean> listViewRecensioni;

    private final InCercaRecensioneController controller = new InCercaRecensioneController();
    private int idInsegnamento;

    private List<String> corsiSimili;
    private Stage stage;
    private Scene scene;
    private String corso;
    private String ateneo;
    private UtenteBean utente;

    public void impostaSchermata(Integer idInsegnamento, String nomeCorso, String nomeAteneo, String nomeInsegnamento, String curriculum, UtenteBean utenteBean, List<String> corsisimili) {
        this.idInsegnamento = idInsegnamento;
        this.corso = nomeCorso;
        this.ateneo = nomeAteneo;
        this.utente = utenteBean;
        this.corsiSimili = corsisimili;
        corsoLabel.setText("Corso: " + corso);
        ateneoLabel.setText("Ateneo: " + ateneo);
        insegnamentoLabel.setText("Insegnamento: " + nomeInsegnamento);
        curriculumLabel.setText("Curriculum: " + curriculum);

        caricaRecensioni();
    }

    private void caricaRecensioni() {
        List<RecensioneBean> recensioni = controller.getRecensioni(idInsegnamento);
        double media = controller.getMediaValutazioni(recensioni);

        mediaLabel.setText(String.format("%.1f", media));

        listViewRecensioni.getItems().setAll(recensioni);

        listViewRecensioni.setCellFactory(lv -> new RecensioneListCell());
    }

        public class RecensioneListCell extends ListCell<RecensioneBean> {
            private final VBox content;
            private final Label nomeUtente;
            private final Label stelle;
            private final TextArea commento;

            public RecensioneListCell() {
                nomeUtente = new Label();
                stelle = new Label();
                commento = new TextArea();
                commento.setWrapText(true);
                commento.setEditable(false);
                commento.setStyle("-fx-background-color: transparent; -fx-border-color: transparent;");
                commento.setPrefRowCount(3);
                content = new VBox(nomeUtente, stelle, commento);
                content.setSpacing(5);
            }

            @Override
            protected void updateItem(RecensioneBean item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                } else {
                    nomeUtente.setText(item.getNome());
                    stelle.setText("⭐".repeat(item.getValutazione()) + "  (" + item.getValutazione() + "/5)");
                    commento.setText(item.getCommento());
                    setGraphic(content);
                }
            }
        }


        @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // niente da inizializzare in anticipo
    }

    @FXML
    public void goBack(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/InCercaDettaglioCorsoUI.fxml"));
        Parent root = loader.load();
        InCercaDettaglioCorsoViewController viewController = loader.getController();
        viewController.impostaSchermata(utente, corso + " - " + ateneo, corsiSimili);
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    public void goToPreferiti(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/InCercaPreferitiUI.fxml"));
        Parent root = loader.load();
        InCercaPreferitiViewController viewController = loader.getController();
        viewController.impostaSchermata(utente);
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    public void logOut(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/view/LogInUI.fxml")));
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

}

