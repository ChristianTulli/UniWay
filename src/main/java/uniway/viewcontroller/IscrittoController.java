package uniway.viewcontroller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.stage.Stage;
import uniway.controller.GestioneIscritto;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;

public class IscrittoController implements Initializable {

    private Scene scene;
    private Stage stage;
    private Parent root;
    private final GestioneIscritto gestioneIscritto = new GestioneIscritto();

    public void logOut(ActionEvent event) throws IOException {
        root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/view/accesso-registrazione.fxml")));
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    private ComboBox<String> regione;
    @FXML
    private ComboBox<String> provincia;
    @FXML
    private ComboBox<String> comune;
    @FXML
    private ComboBox<String> ateneo;
    @FXML
    private ComboBox<String> disciplina;
    @FXML
    private ComboBox<String> tipologia;
    @FXML
    private ComboBox<String> classe;
    @FXML
    private Button cerca;
    @FXML
    private ListView<String> listView;
    @FXML
    private Label label;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        regione.getItems().addAll(gestioneIscritto.getRegioni());
        regione.setOnAction(this::handleRegioneSelection);
    }

    @FXML
    public void handleRegioneSelection(ActionEvent event) {
        provincia.getItems().clear();
        provincia.getItems().addAll(gestioneIscritto.getProvince(regione.getValue()));
        provincia.setDisable(false);
        provincia.setOnAction(this::handleProvinciaSelection);
        comune.getItems().clear();
        comune.setDisable(true);
        ateneo.getItems().clear();
        ateneo.setDisable(true);
        disciplina.getItems().clear();
        disciplina.setDisable(true);
        tipologia.getItems().clear();
        tipologia.setDisable(true);
        classe.getItems().clear();
        classe.setDisable(true);
        cerca.setDisable(true);
        listView.getItems().clear();
        label.setText("");


    }

    @FXML
    public void handleProvinciaSelection(ActionEvent event) {
        comune.getItems().clear();
        comune.getItems().addAll(gestioneIscritto.getComuni(provincia.getValue()));
        comune.setDisable(false);
        comune.setOnAction(this::handleComuneSelection);
        ateneo.getItems().clear();
        ateneo.setDisable(true);
        disciplina.getItems().clear();
        disciplina.setDisable(true);
        tipologia.getItems().clear();
        tipologia.setDisable(true);
        classe.getItems().clear();
        classe.setDisable(true);
        cerca.setDisable(true);
        listView.getItems().clear();
        label.setText("");
    }


    @FXML
    public void handleComuneSelection(ActionEvent event) {
        ateneo.getItems().clear();
        ateneo.getItems().addAll(gestioneIscritto.getAtenei(comune.getValue()));
        ateneo.setDisable(false);
        ateneo.setOnAction(this::handleAteneoSelection);
        disciplina.getItems().clear();
        disciplina.setDisable(true);
        tipologia.getItems().clear();
        tipologia.setDisable(true);
        classe.getItems().clear();
        classe.setDisable(true);
        cerca.setDisable(true);
        listView.getItems().clear();
        label.setText("");

    }

    @FXML
    public void handleAteneoSelection(ActionEvent event) {
        disciplina.getItems().clear();
        disciplina.getItems().addAll(gestioneIscritto.getDiscipline(ateneo.getValue()));
        disciplina.setDisable(false);
        disciplina.setOnAction(this::handleDisciplinaSelection);
        tipologia.getItems().clear();
        tipologia.setDisable(true);
        classe.getItems().clear();
        classe.setDisable(true);
        cerca.setDisable(true);
        listView.getItems().clear();
        label.setText("");

    }

    @FXML
    public void handleDisciplinaSelection(ActionEvent event) {
        tipologia.getItems().clear();
        tipologia.getItems().addAll(gestioneIscritto.getTipologie(disciplina.getValue()));
        tipologia.setDisable(false);
        tipologia.setOnAction(this::handleTipologiaSelection);
        classe.getItems().clear();
        classe.setDisable(true);
        cerca.setDisable(true);
        listView.getItems().clear();
        label.setText("");
    }

    @FXML
    public void handleTipologiaSelection(ActionEvent event) {
        classe.getItems().clear();
        classe.getItems().addAll(gestioneIscritto.getCorsi(tipologia.getValue()));
        classe.setDisable(false);
        classe.setOnAction(this::handleCorsoSelection);
        cerca.setDisable(true);
        listView.getItems().clear();
        label.setText("");
    }

    public void handleCorsoSelection(ActionEvent event) {
            cerca.setDisable(false);
            cerca.setOnAction(this::handleCercaSelection);
    }

    public void handleCercaSelection(ActionEvent event) {
        List<String> risultato =  gestioneIscritto.getRisultati(classe.getValue());
        if (risultato != null) {
            listView.getItems().addAll(risultato);
        }else label.setText("Nessun risultato , controlla i filtri");

    }
}