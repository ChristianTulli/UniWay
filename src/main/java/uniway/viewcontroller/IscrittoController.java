package uniway.viewcontroller;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
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

    @FXML private ComboBox<String> regione, provincia, comune, ateneo, disciplina, tipologia, classe;
    @FXML private Button cerca;
    @FXML private ListView<String> listView;
    @FXML private Label label;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setupComboBox(regione, gestioneIscritto.getRegioni(), this::handleRegioneSelection);
    }

    private void setupComboBox(ComboBox<String> comboBox, List<String> items, EventHandler<ActionEvent> eventHandler) {
        comboBox.getItems().setAll(items);
        comboBox.setDisable(items.isEmpty());
        comboBox.setOnAction(eventHandler);
    }

    private void resetComboBoxes(ComboBox<?>... comboBoxes) {
        for (ComboBox<?> comboBox : comboBoxes) {
            comboBox.getItems().clear();
            comboBox.setDisable(true);
        }
        cerca.setDisable(true);
        listView.getItems().clear();
        label.setText("");
    }

    @FXML
    public void handleRegioneSelection(ActionEvent event) {
        setupComboBox(provincia, gestioneIscritto.getProvince(regione.getValue()), this::handleProvinciaSelection);
        resetComboBoxes(comune, ateneo, disciplina, tipologia, classe);
    }

    @FXML
    public void handleProvinciaSelection(ActionEvent event) {
        setupComboBox(comune, gestioneIscritto.getComuni(provincia.getValue()), this::handleComuneSelection);
        resetComboBoxes(ateneo, disciplina, tipologia, classe);
    }

    @FXML
    public void handleComuneSelection(ActionEvent event) {
        setupComboBox(ateneo, gestioneIscritto.getAtenei(comune.getValue()), this::handleAteneoSelection);
        resetComboBoxes(disciplina, tipologia, classe);
    }

    @FXML
    public void handleAteneoSelection(ActionEvent event) {
        setupComboBox(disciplina, gestioneIscritto.getDiscipline(ateneo.getValue()), this::handleDisciplinaSelection);
        resetComboBoxes(tipologia, classe);
    }

    @FXML
    public void handleDisciplinaSelection(ActionEvent event) {
        setupComboBox(tipologia, gestioneIscritto.getTipologie(disciplina.getValue()), this::handleTipologiaSelection);
        resetComboBoxes(classe);
    }

    @FXML
    public void handleTipologiaSelection(ActionEvent event) {
        setupComboBox(classe, gestioneIscritto.getCorsi(tipologia.getValue()), this::handleCorsoSelection);
    }

    @FXML
    public void handleCorsoSelection(ActionEvent event) {
        cerca.setDisable(false);
        cerca.setOnAction(this::handleCercaSelection);
    }

    @FXML
    public void handleCercaSelection(ActionEvent event) {
        List<String> risultato = gestioneIscritto.getRisultati(classe.getValue());
        if (risultato != null && !risultato.isEmpty()) {
            listView.getItems().setAll(risultato);
        } else {
            label.setText("Nessun risultato, controlla i filtri.");
        }
    }

    public void logOut(ActionEvent event) throws IOException {
        root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/view/accesso-registrazione.fxml")));
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
}
