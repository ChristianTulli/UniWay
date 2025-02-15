package uniway.viewcontroller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class DettaglioCorsoController {

    private Scene scene;
    private Stage stage;
    private Parent root;

    @FXML private Label corsoLabel;

    public void setCorsoSelezionato(String corso) {
        corsoLabel.setText("Dettagli del corso: " + corso);
    }

    public void logOut(ActionEvent event) throws IOException {
        root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/view/accesso-registrazione.fxml")));
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
}
