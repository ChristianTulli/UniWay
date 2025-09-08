package uniway.viewcontroller.fxml;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import uniway.beans.UtenteBean;
import uniway.controller.CommentaEValutaInsegnamentoController;
import uniway.utils.NavigationManager;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

public class IscrittoSelezionaCorsoViewController implements Initializable {

    private final CommentaEValutaInsegnamentoController commentaEValutaInsegnamentoController =
            new CommentaEValutaInsegnamentoController();

    private UtenteBean utenteBean;

    // destinazioni
    private static final String FXML_INSEGNAMENTI = "/view/IscrittoInsegnamentiUI.fxml";
    private static final String FXML_LOGIN        = "/view/LogInUI.fxml";

    private static final String TITOLO_INSEGNAMENTI = "UniWay - Insegnamenti (Iscritto)";
    private static final String TITOLO_LOGIN        = "UniWay - Login";

    private static final Logger LOGGER =
            Logger.getLogger(IscrittoSelezionaCorsoViewController.class.getName());

    public void impostaSchermata(UtenteBean utenteBean) {
        this.utenteBean = utenteBean;
    }

    @FXML private ComboBox<String> regione;
    @FXML private ComboBox<String> provincia;
    @FXML private ComboBox<String> comune;
    @FXML private ComboBox<String> ateneo;
    @FXML private ComboBox<String> disciplina;
    @FXML private ComboBox<String> tipologia;
    @FXML private ComboBox<String> classe;
    @FXML private Button cerca;
    @FXML private ListView<String> listView;
    @FXML private Label label;

    private boolean listenerAgganciato = false;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setupComboBox(regione, commentaEValutaInsegnamentoController.getRegioni(), this::handleRegioneSelection);
    }

    private void setupComboBox(ComboBox<String> comboBox, List<String> items, EventHandler<ActionEvent> handler) {
        comboBox.getItems().setAll(items);
        comboBox.setDisable(items.isEmpty());
        comboBox.setOnAction(handler);
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
        resetComboBoxes(provincia, comune, ateneo, disciplina, tipologia, classe);
        setupComboBox(provincia, commentaEValutaInsegnamentoController.getProvince(regione.getValue()), this::handleProvinciaSelection);
    }

    @FXML
    public void handleProvinciaSelection(ActionEvent event) {
        resetComboBoxes(comune, ateneo, disciplina, tipologia, classe);
        setupComboBox(comune, commentaEValutaInsegnamentoController.getComuni(provincia.getValue()), this::handleComuneSelection);
    }

    @FXML
    public void handleComuneSelection(ActionEvent event) {
        resetComboBoxes(ateneo, disciplina, tipologia, classe);
        setupComboBox(ateneo, commentaEValutaInsegnamentoController.getAtenei(comune.getValue()), this::handleAteneoSelection);
    }

    @FXML
    public void handleAteneoSelection(ActionEvent event) {
        resetComboBoxes(disciplina, tipologia, classe);
        setupComboBox(disciplina, commentaEValutaInsegnamentoController.getDiscipline(ateneo.getValue()), this::handleDisciplinaSelection);
    }

    @FXML
    public void handleDisciplinaSelection(ActionEvent event) {
        resetComboBoxes(tipologia, classe);
        setupComboBox(tipologia, commentaEValutaInsegnamentoController.getDurate(disciplina.getValue()), this::handleTipologiaSelection);
    }

    @FXML
    public void handleTipologiaSelection(ActionEvent event) {
        resetComboBoxes(classe);
        setupComboBox(classe, commentaEValutaInsegnamentoController.getCorsi(tipologia.getValue()), this::handleClasseSelection);
    }

    @FXML
    public void handleClasseSelection(ActionEvent event) {
        resetComboBoxes();
        cerca.setDisable(false);
        cerca.setOnAction(this::handleCercaSelection);
    }

    @FXML
    public void handleCercaSelection(ActionEvent event) {
        reimpostaUI();

        List<String> risultati = commentaEValutaInsegnamentoController.getRisultati(classe.getValue());
        popolaRisultatiONotifica(risultati);

        agganciaListenerUnaVolta();
    }

    private void reimpostaUI() {
        listView.getItems().clear();
        label.setText("");
    }

    private void popolaRisultatiONotifica(List<String> risultati) {
        if (risultati != null && !risultati.isEmpty()) {
            listView.getItems().addAll(risultati);
            return;
        }
        label.setText("Nessun risultato, controlla i filtri");
    }

    private void agganciaListenerUnaVolta() {
        if (listenerAgganciato) return;

        listenerAgganciato = true;
        listView.getSelectionModel()
                .selectedItemProperty()
                .addListener((obs, vecchio, selezionato) -> quandoCorsoSelezionato(selezionato));
    }

    private void quandoCorsoSelezionato(String corsoSelezionatoNuovo) {
        if (corsoSelezionatoNuovo == null) return;

        List<String> curricula = commentaEValutaInsegnamentoController.getCurriculumPerCorso(corsoSelezionatoNuovo);

        if (curricula == null || curricula.isEmpty()) {
            impostaSoloCorso(corsoSelezionatoNuovo);
            aggiornaLabelSoloCorso(corsoSelezionatoNuovo);
            vaiAInterfacciaCommenti(); // navigazione centralizzata
            return;
        }

        if (curricula.size() == 1) {
            String unico = curricula.getFirst();
            impostaCorsoECurriculum(corsoSelezionatoNuovo, unico);
            aggiornaLabelCorsoECurriculum(corsoSelezionatoNuovo, unico);
            vaiAInterfacciaCommenti();
            return;
        }

        chiediCurriculumEProcedi(corsoSelezionatoNuovo, curricula);
    }

    private void chiediCurriculumEProcedi(String corso, List<String> curricula) {
        ChoiceDialog<String> dialog = new ChoiceDialog<>(curricula.getFirst(), curricula);
        dialog.setTitle("Seleziona curriculum");
        dialog.setHeaderText("Curriculum disponibili per il corso selezionato:");
        dialog.setContentText("Scegli curriculum:");

        dialog.showAndWait().ifPresent(curr -> {
            impostaCorsoECurriculum(corso, curr);
            aggiornaLabelCorsoECurriculum(corso, curr);
            vaiAInterfacciaCommenti();
        });
    }

    private void impostaSoloCorso(String corso) {
        commentaEValutaInsegnamentoController.setCorsoUtente(utenteBean);
    }

    private void impostaCorsoECurriculum(String corso, String curriculum) {
        commentaEValutaInsegnamentoController.setCorsoUtente(utenteBean);
        commentaEValutaInsegnamentoController.setCurriculum(curriculum);
    }

    private void aggiornaLabelSoloCorso(String corso) {
        label.setText("Corso selezionato: " + corso);
    }

    private void aggiornaLabelCorsoECurriculum(String corso, String curriculum) {
        label.setText("Corso selezionato: " + corso + "\ncurriculum: " + curriculum);
    }

    /** Navigazione verso la schermata degli insegnamenti (passa UtenteBean) */
    private void vaiAInterfacciaCommenti() {
        try {
            // ottieni lo Stage dalla view
            javafx.stage.Stage stage = (javafx.stage.Stage) listView.getScene().getWindow();

            // naviga passando l'UtenteBean al controller successivo
            uniway.utils.NavigationManager.switchScene(
                    stage,
                    FXML_INSEGNAMENTI,
                    TITOLO_INSEGNAMENTI,
                    uniway.viewcontroller.fxml.IscrittoInsegnamentiViewController.class,
                    c -> c.impostaSchermata(utenteBean)
            );
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Errore nel passaggio alla schermata Insegnamenti", e);
        }
    }


    /** Logout → torna al login con NavigationManager */
    @FXML
    public void logOut(ActionEvent event) {
        NavigationManager.switchScene(event, FXML_LOGIN, TITOLO_LOGIN);
    }
}


