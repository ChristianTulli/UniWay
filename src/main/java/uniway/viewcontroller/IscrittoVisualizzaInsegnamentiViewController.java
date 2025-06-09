package uniway.viewcontroller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import uniway.beans.InsegnamentoBean;
import uniway.beans.UtenteBean;
import uniway.controller.IscrittoVisualizzaInsegnamentiController;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

public class IscrittoVisualizzaInsegnamentiViewController implements Initializable {
    private UtenteBean utenteBean;
    private String nomeCorso;
    private String nomeAteneo;
    private IscrittoVisualizzaInsegnamentiController iscrittoVisualizzaInsegnamentiController = new IscrittoVisualizzaInsegnamentiController();
    private static final Logger LOGGER = Logger.getLogger(IscrittoVisualizzaInsegnamentiViewController.class.getName());

    @FXML
    private Label corsoLabel;

    @FXML
    private Label curriculumLabel;

    @FXML
    private Label ateneoLabel;

    @FXML
    private TableView<InsegnamentoBean> tableView;

    @FXML
    private TableColumn<InsegnamentoBean, Integer> cfu;

    @FXML
    private TableColumn<InsegnamentoBean, Integer> valutazione;

    @FXML
    private TableColumn<InsegnamentoBean, String> insegnamento;

    @FXML
    private TableColumn<InsegnamentoBean, Integer> semestre;

    @FXML
    private TableColumn<InsegnamentoBean, Integer> anno;

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

    private void apriSchermataCommento(InsegnamentoBean insegnamentoBean) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/IscrittoCommentaInsegnamentoUI.fxml"));
                Parent newRoot = loader.load();

                // Ottieni il controller della nuova interfaccia
                IscrittoCommentaInsegnamentoViewController iscrittoCommentaInsegnamentoViewController = loader.getController();
                iscrittoCommentaInsegnamentoViewController.impostaSchermata(utenteBean,
                        iscrittoVisualizzaInsegnamentiController.getCorso(utenteBean.getIdCorso()),
                        iscrittoVisualizzaInsegnamentiController.getAteneo(utenteBean.getIdCorso()),
                        insegnamentoBean,
                        iscrittoVisualizzaInsegnamentiController);


                // Cambia la schermata attuale
                Stage stage = (Stage) tableView.getScene().getWindow();
                Scene scene = new Scene(newRoot);
                stage.setScene(scene);
                stage.show();

            } catch (IOException e) {
                LOGGER.log(Level.SEVERE, "Errore nell'apertura della visualizzazione del corso", e);
            }
    }

    public void impostaSchermata(UtenteBean utenteBean) {

        this.utenteBean = utenteBean;

        // popola la tabella insegnamenti
        List<InsegnamentoBean> lista = iscrittoVisualizzaInsegnamentiController.getInsegnamenti(utenteBean.getIdCorso(), utenteBean.getCurriculum(), utenteBean.getUsername());
        ObservableList<InsegnamentoBean> listaInsegnamenti = FXCollections.observableArrayList(lista);
        tableView.setItems(listaInsegnamenti);

        //imposta nome corso, curriculum, ateneo
        curriculumLabel.setText(utenteBean.getCurriculum());
        corsoLabel.setText(iscrittoVisualizzaInsegnamentiController.getCorso(utenteBean.getIdCorso()));
        ateneoLabel.setText(iscrittoVisualizzaInsegnamentiController.getAteneo(utenteBean.getIdCorso()));
    }

    public void logOut (ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/view/LogInUI.fxml")));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
}
