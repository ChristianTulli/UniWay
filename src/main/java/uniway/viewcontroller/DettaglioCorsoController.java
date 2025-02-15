package uniway.viewcontroller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class DettaglioCorsoController {

    @FXML private Label corsoLabel;

    public void setCorsoSelezionato(String corso) {
        corsoLabel.setText("Dettagli del corso: " + corso);
    }
}
