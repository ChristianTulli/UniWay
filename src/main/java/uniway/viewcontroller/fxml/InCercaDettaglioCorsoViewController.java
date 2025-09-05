package uniway.viewcontroller.fxml;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.text.Text;
import uniway.beans.InsegnamentoBean;
import uniway.beans.UtenteBean;
import uniway.controller.InCercaDettaglioCorsoController;
import uniway.utils.NavigationManager;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

public class InCercaDettaglioCorsoViewController implements Initializable {

    private UtenteBean utenteBean;
    private String corsoCorrente;
    private String nomeCorso;
    private String nomeAteneo;

    private final InCercaDettaglioCorsoController inCercaDettaglioCorsoController =
            new InCercaDettaglioCorsoController();

    private static final Logger LOGGER =
            Logger.getLogger(InCercaDettaglioCorsoViewController.class.getName());

    // destinazioni
    private static final String FXML_CONFRONTO   = "/view/InCercaConfrontaCorsoUI.fxml";
    private static final String FXML_RECENSIONI  = "/view/InCercaRecensioneUI.fxml";
    private static final String FXML_RICERCA     = "/view/InCercaTrovaCorsoUI.fxml";
    private static final String FXML_PREFERITI   = "/view/InCercaPreferitiUI.fxml";
    private static final String FXML_LOGIN       = "/view/LogInUI.fxml";

    private static final String TITOLO_CONFRONTO  = "UniWay - Confronta corsi";
    private static final String TITOLO_RECENSIONI = "UniWay - Recensioni";
    private static final String TITOLO_RICERCA    = "UniWay - Trova corso";
    private static final String TITOLO_PREFERITI  = "UniWay - Preferiti";
    private static final String TITOLO_LOGIN      = "UniWay - Login";

    @FXML private Label corsoLabel;
    @FXML private Label ateneoLabel;
    @FXML private Label erroreLabel;

    @FXML private TableView<InsegnamentoBean> tableView;
    @FXML private TableColumn<InsegnamentoBean, Integer> cfu;
    @FXML private TableColumn<InsegnamentoBean, String> curriculum;
    @FXML private TableColumn<InsegnamentoBean, String> insegnamento;
    @FXML private TableColumn<InsegnamentoBean, Integer> semestre;
    @FXML private TableColumn<InsegnamentoBean, Integer> anno;

    @FXML private ListView<String> listaCorsiSimili;
    @FXML private Button preferitiButton;

    private List<String> corsiSimili;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        inizializzaColonneTabella();
        configuraListaCorsiSimili();
        configuraRowFactoryTableView();
    }

    private void inizializzaColonneTabella() {
        insegnamento.setCellValueFactory(new PropertyValueFactory<>("nome"));
        curriculum.setCellValueFactory(new PropertyValueFactory<>("curriculum"));
        cfu.setCellValueFactory(new PropertyValueFactory<>("cfu"));
        semestre.setCellValueFactory(new PropertyValueFactory<>("semestre"));
        anno.setCellValueFactory(new PropertyValueFactory<>("anno"));

        setWrappedTextCellFactory(insegnamento);
        setWrappedTextCellFactory(curriculum);
    }

    private void configuraListaCorsiSimili() {
        listaCorsiSimili.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                String corsoSimile = listaCorsiSimili.getSelectionModel().getSelectedItem();
                if (corsoSimile != null) {
                    apriSchermataConfronto(corsoSimile);
                }
            }
        });
    }

    private void apriSchermataConfronto(String corsoSimile) {
        try {
            var stage = (javafx.stage.Stage) listaCorsiSimili.getScene().getWindow();
            NavigationManager.switchScene(
                    stage,
                    FXML_CONFRONTO,
                    TITOLO_CONFRONTO,
                    InCercaConfrontaCorsoViewController.class,
                    c -> c.impostaSchermata(utenteBean, corsoCorrente, corsoSimile, corsiSimili)
            );
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Errore nell'apertura della schermata di confronto", e);
        }
    }

    private void configuraRowFactoryTableView() {
        tableView.setRowFactory(tv -> {
            TableRow<InsegnamentoBean> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && !row.isEmpty()) {
                    InsegnamentoBean insegnamentoSelezionato = row.getItem();
                    apriSchermataRecensioni(insegnamentoSelezionato);
                }
            });
            return row;
        });
    }

    private void apriSchermataRecensioni(InsegnamentoBean insSel) {
        try {
            Integer idInsegnamento = inCercaDettaglioCorsoController.getIdInsegnamento(
                    insSel.getNome(), nomeCorso, nomeAteneo
            );

            var stage = (javafx.stage.Stage) tableView.getScene().getWindow();
            NavigationManager.switchScene(
                    stage,
                    FXML_RECENSIONI,
                    TITOLO_RECENSIONI,
                    InCercaRecensioneViewController.class,
                    c -> c.impostaSchermata(
                            idInsegnamento,
                            nomeCorso,
                            nomeAteneo,
                            insSel.getNome(),
                            insSel.getCurriculum(),
                            utenteBean,
                            corsiSimili
                    )
            );
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Errore nell'apertura della schermata recensioni", e);
        }
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

    public void setUtenteBean(UtenteBean utenteBean) {
        this.utenteBean = utenteBean;
    }

    public void setCorsoSelezionato(String corso, List<String> corsiSimili) {
        this.corsoCorrente = corso;
        this.corsiSimili = corsiSimili;

        String[] dettagli = corso.split(" - ");
        corsoLabel.setText(dettagli[0]);
        nomeCorso = dettagli[0];
        ateneoLabel.setText(dettagli[1]);
        nomeAteneo = dettagli[1];

        listaCorsiSimili.getItems().clear();
        for (String c : corsiSimili) {
            if (!c.equals(corso)) listaCorsiSimili.getItems().add(c);
        }

        erroreLabel.setText(listaCorsiSimili.getItems().isEmpty()
                ? "Effettua una nuova ricerca per vedere corsi simili"
                : "");

        List<InsegnamentoBean> lista =
                inCercaDettaglioCorsoController.getInsegnamenti(nomeCorso, nomeAteneo);
        tableView.setItems(FXCollections.observableArrayList(lista));
    }

    public void impostaSchermata(UtenteBean utenteBean, String corso, List<String> corsiSimili){
        setUtenteBean(utenteBean);
        setCorsoSelezionato(corso, corsiSimili);
    }

    @FXML
    public void aggiungiAiPreferiti(ActionEvent event) {
        if (utenteBean != null && nomeCorso != null) {
            boolean aggiunto = inCercaDettaglioCorsoController.aggiungiAiPreferiti(utenteBean, nomeCorso, nomeAteneo);
            if (aggiunto) {
                preferitiButton.setDisable(true);
                mostraPopup("Aggiunto ai preferiti", "Il corso è stato aggiunto con successo.");
            } else {
                mostraPopup("Corso già nei preferiti", "Hai già aggiunto questo corso ai tuoi preferiti.");
            }
        }
    }

    private void mostraPopup(String titolo, String messaggio) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titolo);
        alert.setHeaderText(null);
        alert.setContentText(messaggio);
        alert.showAndWait();
    }

    // ===== NAVIGAZIONE =====

    public void goBack(ActionEvent event) {
        NavigationManager.switchScene(
                event,
                FXML_RICERCA,
                TITOLO_RICERCA,
                InCercaTrovaCorsoViewController.class,
                c -> c.impostaSchermata(utenteBean)
        );
    }

    public void goToPreferiti(ActionEvent event) {
        NavigationManager.switchScene(
                event,
                FXML_PREFERITI,
                TITOLO_PREFERITI,
                InCercaPreferitiViewController.class,
                c -> c.impostaSchermata(utenteBean)
        );
    }

    public void logOut(ActionEvent event) {
        NavigationManager.switchScene(event, FXML_LOGIN, TITOLO_LOGIN);
    }
}



