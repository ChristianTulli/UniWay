package uniway.viewcontroller;

import javafx.collections.FXCollections;
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
import uniway.controller.InCercaConfrontaCorsoController;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;

public class InCercaConfrontaCorsoViewController implements Initializable {

    private UtenteBean utenteBean;
    private String corso1;
    private String corso2;

    private String ateneo1;
    private String ateneo2;

    private final InCercaConfrontaCorsoController controller = new InCercaConfrontaCorsoController();
    private List<String> corsiSimili;
    private Stage stage;
    private Scene scene;

    @FXML private Label corsoLabel1;
    @FXML private Label ateneoLabel1;
    @FXML private Label corsoLabel2;
    @FXML private Label ateneoLabel2;

    @FXML private Button preferitiButton1;
    @FXML private Button preferitiButton2;

    @FXML private TableView<InsegnamentoBean> tableView1;
    @FXML private TableView<InsegnamentoBean> tableView2;

    @FXML private TableColumn<InsegnamentoBean, String> insegnamento;
    @FXML private TableColumn<InsegnamentoBean, Integer> semestre;
    @FXML private TableColumn<InsegnamentoBean, Integer> anno;
    @FXML private TableColumn<InsegnamentoBean, Integer> cfu;
    @FXML private TableColumn<InsegnamentoBean, String> curriculum;

    @FXML private TableColumn<InsegnamentoBean, String> insegnamento1;
    @FXML private TableColumn<InsegnamentoBean, Integer> semestre1;
    @FXML private TableColumn<InsegnamentoBean, Integer> anno1;
    @FXML private TableColumn<InsegnamentoBean, Integer> cfu1;
    @FXML private TableColumn<InsegnamentoBean, String> curriculum1;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Prima tabella
        insegnamento.setCellValueFactory(new PropertyValueFactory<>("nome"));
        curriculum.setCellValueFactory(new PropertyValueFactory<>("curriculum"));
        cfu.setCellValueFactory(new PropertyValueFactory<>("cfu"));
        semestre.setCellValueFactory(new PropertyValueFactory<>("semestre"));
        anno.setCellValueFactory(new PropertyValueFactory<>("anno"));

        // Seconda tabella
        insegnamento1.setCellValueFactory(new PropertyValueFactory<>("nome"));
        curriculum1.setCellValueFactory(new PropertyValueFactory<>("curriculum"));
        cfu1.setCellValueFactory(new PropertyValueFactory<>("cfu"));
        semestre1.setCellValueFactory(new PropertyValueFactory<>("semestre"));
        anno1.setCellValueFactory(new PropertyValueFactory<>("anno"));

        // Wrapping per entrambe le colonne "insegnamento"
        setWrappedTextCellFactory(insegnamento);
        setWrappedTextCellFactory(insegnamento1);
        setWrappedTextCellFactory(curriculum);
        setWrappedTextCellFactory(curriculum1);

    }

    private void setWrappedTextCellFactory(TableColumn<InsegnamentoBean, String> column) {
        column.setCellFactory(col -> {
            Text text = new Text();
            text.wrappingWidthProperty().bind(col.widthProperty().subtract(10));

            return new TableCell<>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setGraphic(null);
                    } else {
                        text.setText(item);
                        setGraphic(text);
                    }
                }
            };
        });
    }



    public void impostaSchermata(UtenteBean utenteBean, String corsoStr1, String corsoStr2, List<String> corsiSimil) {
        this.utenteBean = utenteBean;
        this.corsiSimili = corsiSimil;

        String[] dettagli1 = corsoStr1.split(" - ");
        corso1 = dettagli1[0];
        ateneo1 = dettagli1[1];

        String[] dettagli2 = corsoStr2.split(" - ");
        corso2 = dettagli2[0];
        ateneo2 = dettagli2[1];

        corsoLabel1.setText(corso1);
        ateneoLabel1.setText(ateneo1);

        corsoLabel2.setText(corso2);
        ateneoLabel2.setText(ateneo2);

        tableView1.setItems(FXCollections.observableArrayList(controller.getInsegnamenti(corso1, ateneo1)));
        tableView2.setItems(FXCollections.observableArrayList(controller.getInsegnamenti(corso2, ateneo2)));
    }

    @FXML
    public void aggiungiAiPreferiti1(ActionEvent event) {
        controller.aggiungiAiPreferiti(utenteBean, corso1, ateneo1);
        preferitiButton1.setDisable(true);
    }

    @FXML
    public void aggiungiAiPreferiti2(ActionEvent event) {
        controller.aggiungiAiPreferiti(utenteBean, corso2, ateneo2);
        preferitiButton2.setDisable(true);
    }

    @FXML
    public void goBack(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/InCercaDettaglioCorsoUI.fxml"));
        Parent root = loader.load();
        InCercaDettaglioCorsoViewController viewController = loader.getController();
        viewController.impostaSchermata(utenteBean, corso1 + " - " + ateneo1, corsiSimili);
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
        viewController.impostaSchermata(utenteBean);
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
