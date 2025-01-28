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
import javafx.stage.Stage;
import java.io.IOException;
import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

public class RicercaController implements Initializable {

    private Scene scene;
    private Stage stage;
    private Parent root;

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
    private ComboBox<String> ateneo;
    @FXML
    private ComboBox<String> facolta;
    @FXML
    private ComboBox<String> corso;
    @FXML
    private Button cerca;

    //private final String[] ... implementare i dati provenienti dal database
    private final String[] regioni = {"Lazio", "Lombardia"};

    private final String[] provinciaLazio = {"Roma", "Latina"};
    //private final String[] provinciaLombardia = {"Milano", "Como"};
    private final String[] ateneiRoma = {"TorVergata", "LaSapienza"};
    //private final String[] AteneiMilano = {"Politecnico", "Bocconi"};
    private final String[] facoltaAteneo = {"Ingegneria", "Economia"};
    private final String[] corsiIngegneria = {"Ingegneria informatica", "Ingegneria gestionale", "Ingegneria civile", "Ingegneria medica"};
    //private final String[] ProvinciaEconomia = {"Finanza e Marketing"};

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        regione.getItems().addAll(regioni);
        regione.setOnAction(this::handleRegioneSelection);
    }

    @FXML
    public void handleRegioneSelection(ActionEvent event) {
        String selezione = regione.getValue();
        if ("Lazio".equals(selezione)) {
            provincia.getItems().clear();
            provincia.getItems().addAll(provinciaLazio);
            provincia.setDisable(false);
            provincia.setOnAction(this::handleProvinciaSelection);
        } else {
            provincia.setDisable(true);
            ateneo.setDisable(true);
            facolta.setDisable(true);
            corso.setDisable(true);
            cerca.setDisable(true);
        }
    }

//controllare l'attivazione del tasto cerca

    @FXML
    public void handleProvinciaSelection(ActionEvent event) {
        String selezione = provincia.getValue();

        if ("Roma".equals(selezione)) {
            ateneo.getItems().clear();
            ateneo.getItems().addAll(ateneiRoma);
            ateneo.setDisable(false);
            ateneo.setOnAction(this::handleAteneoSelection);
        } else {
            ateneo.setDisable(true);
            facolta.setDisable(true);
            corso.setDisable(true);
            cerca.setDisable(true);
        }
    }

    @FXML
    public void handleAteneoSelection(ActionEvent event) {
        String selezione = ateneo.getValue();

        if ("TorVergata".equals(selezione)) {
            facolta.getItems().clear();
            facolta.getItems().addAll(facoltaAteneo);
            facolta.setDisable(false);
            facolta.setOnAction(this::handleFacoltaSelection);
        } else {
            facolta.setDisable(true);
            corso.setDisable(true);
            cerca.setDisable(true);
        }
    }

    @FXML
    public void handleFacoltaSelection(ActionEvent event) {
        String selezione = facolta.getValue();
        if ("Ingegneria".equals(selezione)) {
            corso.getItems().clear();
            corso.getItems().addAll(corsiIngegneria);
            corso.setDisable(false);
            corso.setOnAction(this::handleCorsoSelection);

        } else {
            corso.setDisable(true);
            cerca.setDisable(true);
        }


    }

    public void handleCorsoSelection(ActionEvent event) {
        String selezione = corso.getValue();
        if (selezione!=null){
            cerca.setDisable(false);
        }else cerca.setDisable(true);
    }
}