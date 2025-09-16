package uniway.viewcontroller.fxml;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import uniway.beans.UtenteBean;
import uniway.controller.TrovaCorsoController;
import uniway.patterns.NavigationManagerFacade;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

public class InCercaTrovaCorsoViewController implements Initializable {

    private final TrovaCorsoController trovaCorsoController = new TrovaCorsoController();
    private UtenteBean utenteBean;

    private static final Logger LOGGER = Logger.getLogger(InCercaTrovaCorsoViewController.class.getName());

    // destinazioni
    private static final String FXML_DETTAGLIO   = "/view/InCercaDettaglioCorsoUI.fxml";
    private static final String FXML_PREFERITI   = "/view/InCercaPreferitiUI.fxml";
    private static final String FXML_LOGIN       = "/view/LogInUI.fxml";
    private static final String TITOLO_DETTAGLIO = "UniWay - Dettaglio corso";
    private static final String TITOLO_PREFERITI = "UniWay - Preferiti";
    private static final String TITOLO_LOGIN     = "UniWay - Login";

    public void impostaSchermata(UtenteBean utenteBean) {
        this.utenteBean = utenteBean;
    }

    // COLONNA 1: Tipologia di Ateneo
    @FXML private ComboBox<String> statale;
    @FXML private ComboBox<String> tipologia;

    // COLONNA 2: Ubicazione
    @FXML private ComboBox<String> regione;
    @FXML private ComboBox<String> provincia;
    @FXML private ComboBox<String> comune;

    // COLONNA 3: Caratteristiche del Corso
    @FXML private ComboBox<String> durata;
    @FXML private ComboBox<String> gruppoDisciplina;
    @FXML private ComboBox<String> classeCorso;

    @FXML private Button cerca;
    @FXML private ListView<String> listView;
    @FXML private Label label;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setupComboBox(statale, trovaCorsoController.getTipiAteneo(), this::handleStataleSelection);
        setupComboBox(regione, trovaCorsoController.getRegioni(), this::handleRegioneSelection);
        setupComboBox(durata, trovaCorsoController.getDurate(), this::handleDurataSelection);
        cerca.setDisable(true);

        // Selezione dalla lista → apri dettaglio
        listView.getSelectionModel().selectedItemProperty().addListener((obs, oldV, newV) -> {
            if (newV != null) {
                label.setText("Corso selezionato: " + newV);
                apriDettaglioCorso(newV);
            }
        });
    }

    private void setupComboBox(ComboBox<String> comboBox, List<String> items, EventHandler<ActionEvent> eventHandler) {
        comboBox.getItems().setAll(items);
        comboBox.setDisable(items.isEmpty());
        comboBox.setOnAction(event -> { if (eventHandler != null) eventHandler.handle(event); });
    }

    private void resetComboBoxes(ComboBox<?>... comboBoxes) {
        for (ComboBox<?> comboBox : comboBoxes) {
            comboBox.getItems().clear();
            comboBox.setDisable(true);
        }
        listView.getItems().clear();
        label.setText("");
    }

    private void checkCercaEnabled() {
        boolean stataleSelezionato = statale.getValue() != null && !statale.getValue().isEmpty();
        boolean regioneSelezionata = regione.getValue() != null && !regione.getValue().isEmpty();
        boolean durataSelezionata  = durata.getValue()  != null && !durata.getValue().isEmpty();
        cerca.setDisable(!(stataleSelezionato && regioneSelezionata && durataSelezionata));
    }

    // --- FILTRI ---

    @FXML public void handleStataleSelection(ActionEvent event) {
        resetComboBoxes(tipologia);
        setupComboBox(tipologia, trovaCorsoController.getTipologie(statale.getValue()), this::handleTipologiaSelection);
        checkCercaEnabled();
    }

    @FXML public void handleTipologiaSelection(ActionEvent event) {
        resetComboBoxes();
        trovaCorsoController.setTipologia(tipologia.getValue());
    }

    @FXML public void handleRegioneSelection(ActionEvent event) {
        resetComboBoxes(provincia, comune);
        setupComboBox(provincia, trovaCorsoController.getProvince(regione.getValue()), this::handleProvinciaSelection);
        checkCercaEnabled();
    }

    @FXML public void handleProvinciaSelection(ActionEvent event) {
        resetComboBoxes(comune);
        setupComboBox(comune, trovaCorsoController.getComuni(provincia.getValue()), this::handleComuneSelection);
    }

    @FXML public void handleComuneSelection(ActionEvent event) {
        resetComboBoxes();
        trovaCorsoController.setComune(comune.getValue());
    }

    @FXML public void handleDurataSelection(ActionEvent event) {
        resetComboBoxes(gruppoDisciplina, classeCorso);
        setupComboBox(gruppoDisciplina, trovaCorsoController.getDiscipline(durata.getValue()), this::handleGruppoSelection);
        checkCercaEnabled();
    }

    @FXML public void handleGruppoSelection(ActionEvent event) {
        resetComboBoxes(classeCorso);
        setupComboBox(classeCorso, trovaCorsoController.getClassi(gruppoDisciplina.getValue()), this::handleClasseSelection);
    }

    @FXML public void handleClasseSelection(ActionEvent event) {
        resetComboBoxes();
        trovaCorsoController.setClasseCorso(classeCorso.getValue());
    }

    // --- CERCA ---
    @FXML
    public void handleCercaSelection(ActionEvent event) {
        resetComboBoxes();
        List<String> risultato = trovaCorsoController.getRisultati();
        if (risultato != null && !risultato.isEmpty()) {
            listView.getItems().addAll(risultato);
        } else {
            label.setText("Nessun risultato, prova a cambiare i filtri.");
        }
    }

    // --- NAVIGAZIONE ---

    private void apriDettaglioCorso(String corsoSelezionato) {
        try {
            var stage = (javafx.stage.Stage) listView.getScene().getWindow();
            NavigationManagerFacade.switchScene(
                    stage,
                    FXML_DETTAGLIO,
                    TITOLO_DETTAGLIO,
                    InCercaDettaglioCorsoViewController.class,
                    c -> c.impostaSchermata(utenteBean, corsoSelezionato, listView.getItems())
            );
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Errore nell'apertura della visualizzazione del corso", e);
        }
    }

    public void goToPreferiti(ActionEvent event) {
        NavigationManagerFacade.switchScene(
                event,
                FXML_PREFERITI,
                TITOLO_PREFERITI,
                InCercaPreferitiViewController.class,
                c -> c.impostaSchermata(utenteBean)
        );
    }

    public void logOut(ActionEvent event) {
        NavigationManagerFacade.switchScene(event, FXML_LOGIN, TITOLO_LOGIN);
    }
}


