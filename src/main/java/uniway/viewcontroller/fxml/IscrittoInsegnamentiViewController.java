package uniway.viewcontroller.fxml;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.text.Text;
import uniway.beans.InsegnamentoBean;
import uniway.beans.UtenteBean;
import uniway.controller.IscrittoInsegnamentiController;
import uniway.utils.NavigationManager;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

public class IscrittoInsegnamentiViewController implements Initializable {
    private UtenteBean utenteBean;
    private final IscrittoInsegnamentiController iscrittoInsegnamentiController =
            new IscrittoInsegnamentiController();

    private static final Logger LOGGER =
            Logger.getLogger(IscrittoInsegnamentiViewController.class.getName());

    // destinazioni
    private static final String FXML_COMMENTA = "/view/IscrittoCommentaUI.fxml";
    private static final String FXML_LOGIN    = "/view/LogInUI.fxml";
    private static final String TITOLO_COMMENTA = "UniWay - Commenta Insegnamento";
    private static final String TITOLO_LOGIN    = "UniWay - Login";

    @FXML private Label corsoLabel;
    @FXML private Label curriculumLabel;
    @FXML private Label ateneoLabel;

    @FXML private TableView<InsegnamentoBean> tableView;
    @FXML private TableColumn<InsegnamentoBean, Integer> cfu;
    @FXML private TableColumn<InsegnamentoBean, Integer> valutazione;
    @FXML private TableColumn<InsegnamentoBean, String> insegnamento;
    @FXML private TableColumn<InsegnamentoBean, Integer> semestre;
    @FXML private TableColumn<InsegnamentoBean, Integer> anno;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        configuraColonne();
        configuraInsegnamentoCellFactory();
        configuraRowFactory();
    }

    private void configuraColonne() {
        insegnamento.setCellValueFactory(new PropertyValueFactory<>("nome"));
        cfu.setCellValueFactory(new PropertyValueFactory<>("cfu"));
        semestre.setCellValueFactory(new PropertyValueFactory<>("semestre"));
        anno.setCellValueFactory(new PropertyValueFactory<>("anno"));
        valutazione.setCellValueFactory(new PropertyValueFactory<>("valutazione"));
    }

    private void configuraInsegnamentoCellFactory() {
        insegnamento.setCellFactory(column -> new TableCell<>() {
            private Text text;
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                } else {
                    if (text == null) {
                        text = new Text();
                        text.wrappingWidthProperty().bind(insegnamento.widthProperty().subtract(10));
                    }
                    text.setText(item);
                    setGraphic(text);
                }
            }
        });
    }

    private void configuraRowFactory() {
        tableView.setRowFactory(tv -> {
            TableRow<InsegnamentoBean> row = new TableRow<>();
            row.itemProperty().addListener((obs, oldItem, newItem) -> {
                boolean giaRecensito = newItem != null && newItem.getValutazione() != null;
                row.setStyle(giaRecensito ? "-fx-background-color: #dddddd;" : "");
            });

            row.setOnMouseClicked(event -> {
                InsegnamentoBean selected = row.getItem();
                if (event.getClickCount() == 2 && selected != null) {
                    if (selected.getValutazione() == null) {
                        apriSchermataCommento(selected);
                    } else {
                        mostraAlertRecensionePresente();
                    }
                }
            });
            return row;
        });
    }

    private void mostraAlertRecensionePresente() {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Già recensito");
        alert.setHeaderText(null);
        alert.setContentText("Hai già recensito questo insegnamento.");
        alert.showAndWait();
    }

    /** Naviga alla schermata di commento passando tutti i dati necessari */
    private void apriSchermataCommento(InsegnamentoBean insBean) {
        try {
            var stage = (javafx.stage.Stage) tableView.getScene().getWindow();
            NavigationManager.switchScene(
                    stage,
                    FXML_COMMENTA,
                    TITOLO_COMMENTA,
                    IscrittoCommentaViewController.class,
                    c -> c.impostaSchermata(
                            utenteBean,
                            iscrittoInsegnamentiController.getCorso(utenteBean.getIdCorso()),
                            iscrittoInsegnamentiController.getAteneo(utenteBean.getIdCorso()),
                            insBean,
                            iscrittoInsegnamentiController
                    )
            );
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Errore nell'apertura della visualizzazione del corso", e);
        }
    }

    /** Invocata dalla schermata precedente per inizializzare la view con l’utente */
    public void impostaSchermata(UtenteBean utenteBean) {
        this.utenteBean = utenteBean;

        // popola la tabella insegnamenti
        List<InsegnamentoBean> lista = iscrittoInsegnamentiController.getInsegnamenti(
                utenteBean.getIdCorso(), utenteBean.getCurriculum(), utenteBean.getUsername());
        ObservableList<InsegnamentoBean> listaInsegnamenti = FXCollections.observableArrayList(lista);
        tableView.setItems(listaInsegnamenti);

        // etichette corso/curriculum/ateneo
        curriculumLabel.setText(utenteBean.getCurriculum());
        corsoLabel.setText(iscrittoInsegnamentiController.getCorso(utenteBean.getIdCorso()));
        ateneoLabel.setText(iscrittoInsegnamentiController.getAteneo(utenteBean.getIdCorso()));
    }

    /** Logout semplice via NavigationManager */
    public void logOut(ActionEvent event) {
        NavigationManager.switchScene(event, FXML_LOGIN, TITOLO_LOGIN);
    }
}

