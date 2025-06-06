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
import javafx.scene.layout.Region;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import uniway.beans.InsegnamentoBean;
import uniway.beans.UtenteBean;
import uniway.controller.GestioneCommenti;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;

public class CommentiController implements Initializable {
    private UtenteBean utenteBean;
    private String nomeCorso;
    private String nomeAteneo;
    private GestioneCommenti gestioneCommenti= new GestioneCommenti();

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
        insegnamento.setCellValueFactory(new PropertyValueFactory<InsegnamentoBean, String>("nome"));
        cfu.setCellValueFactory(new PropertyValueFactory<InsegnamentoBean, Integer>("cfu"));
        semestre.setCellValueFactory(new PropertyValueFactory<InsegnamentoBean, Integer>("semestre"));
        anno.setCellValueFactory(new PropertyValueFactory<InsegnamentoBean, Integer>("anno"));
        valutazione.setCellValueFactory(new PropertyValueFactory<InsegnamentoBean, Integer>("valutazione"));

        insegnamento.setCellFactory(column -> new TableCell<>() {
            private final Text text = new Text();

            {
                text.wrappingWidthProperty().bind(insegnamento.widthProperty().subtract(10)); // Wrapping dinamico
                setGraphic(text);
            }

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
        });

    }

    public void setUtenteBean(UtenteBean utenteBean) {

        this.utenteBean = utenteBean;

        // popola la tabella insegnamenti
        List<InsegnamentoBean> lista = gestioneCommenti.getInsegnamenti(utenteBean.getIdCorso(), utenteBean.getCurriculum(), utenteBean.getUsername());
        ObservableList<InsegnamentoBean> listaInsegnamenti = FXCollections.observableArrayList(lista);
        tableView.setItems(listaInsegnamenti);

        //imposta nome corso, curriculum, ateneo
        curriculumLabel.setText(utenteBean.getCurriculum());
        corsoLabel.setText(gestioneCommenti.getCorso(utenteBean.getIdCorso()));
        ateneoLabel.setText(gestioneCommenti.getAteneo(utenteBean.getIdCorso()));
    }

    public void logOut (ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/view/accesso-registrazione.fxml")));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
}
