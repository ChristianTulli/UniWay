package uniway.viewcontroller.fxml;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.VBox;
import uniway.beans.RecensioneBean;
import uniway.beans.UtenteBean;
import uniway.controller.TrovaCorsoController;
import uniway.patterns.NavigationManagerFacade;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class InCercaRecensioneViewController implements Initializable {

    @FXML private Label corsoLabel;
    @FXML private Label ateneoLabel;
    @FXML private Label insegnamentoLabel;
    @FXML private Label curriculumLabel;
    @FXML private Label mediaLabel;
    @FXML private ListView<RecensioneBean> listViewRecensioni;

    private final TrovaCorsoController trovaCorsoController = new TrovaCorsoController();

    private List<String> corsiSimili;
    private String corso;
    private String ateneo;
    private UtenteBean utente;
    private String nomeInsegnamento;

    // destinazioni
    private static final String FXML_DETTAGLIO  = "/view/InCercaDettaglioCorsoUI.fxml";
    private static final String FXML_PREFERITI  = "/view/InCercaPreferitiUI.fxml";
    private static final String FXML_LOGIN      = "/view/LogInUI.fxml";
    private static final String TITOLO_DETTAGLIO = "UniWay - Dettaglio corso";
    private static final String TITOLO_PREFERITI = "UniWay - Preferiti";
    private static final String TITOLO_LOGIN     = "UniWay - Login";

    public void impostaSchermata(
                                 String nomeCorso,
                                 String nomeAteneo,
                                 String nomeInsegnamento,
                                 String curriculum,
                                 UtenteBean utenteBean,
                                 List<String> corsisimili) {
        this.corso = nomeCorso;
        this.ateneo = nomeAteneo;
        this.utente = utenteBean;
        this.corsiSimili = corsisimili;
        this.nomeInsegnamento = nomeInsegnamento;

        corsoLabel.setText(corso);
        ateneoLabel.setText(ateneo);
        insegnamentoLabel.setText(nomeInsegnamento);
        curriculumLabel.setText(curriculum);

        caricaRecensioni();
    }

    private void caricaRecensioni() {
        List<RecensioneBean> recensioni = trovaCorsoController.getRecensioni(corso, ateneo, nomeInsegnamento);
        double media = trovaCorsoController.getMediaValutazioni(recensioni);

        mediaLabel.setText(String.format("%.1f", media));
        listViewRecensioni.getItems().setAll(recensioni);
        listViewRecensioni.setCellFactory(lv -> new RecensioneListCell());
    }

    public static class RecensioneListCell extends ListCell<RecensioneBean> {
        private final VBox content;
        private final Label nomeUtente = new Label();
        private final Label stelle = new Label();
        private final Label commento = new Label();

        public RecensioneListCell() {
            commento.setWrapText(true);
            commento.setMaxWidth(740);

            // STILI
            nomeUtente.setStyle("-fx-font-weight: bold; -fx-font-size: 20px;");
            stelle.setStyle("-fx-text-fill: purple; -fx-font-weight: bold; -fx-font-size: 15px;");
            commento.setStyle(
                    "-fx-background-color: #ffffff;" +
                            "-fx-font-size: 15px;" +
                            "-fx-border-color: black;" +
                            "-fx-border-radius: 12px;" +
                            "-fx-background-radius: 12px;" +
                            "-fx-padding: 12px;" +
                            "-fx-control-inner-background: #ffffff;"
            );
            content = new VBox(nomeUtente, stelle, commento);
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
        // nessuna init anticipata
    }

    // ===== NAVIGAZIONE con NavigationManagerFacade =====

    @FXML
    public void goBack(ActionEvent event) {
        // Torna al dettaglio corso, passando di nuovo i dati
        NavigationManagerFacade.switchScene(
                event,
                FXML_DETTAGLIO,
                TITOLO_DETTAGLIO,
                InCercaDettaglioCorsoViewController.class,
                c -> c.impostaSchermata(utente, corso + " - " + ateneo, corsiSimili)
        );
    }

    @FXML
    public void goToPreferiti(ActionEvent event) {
        NavigationManagerFacade.switchScene(
                event,
                FXML_PREFERITI,
                TITOLO_PREFERITI,
                InCercaPreferitiViewController.class,
                c -> c.impostaSchermata(utente)
        );
    }

    @FXML
    public void logOut(ActionEvent event) {
        NavigationManagerFacade.switchScene(event, FXML_LOGIN, TITOLO_LOGIN);
    }
}


