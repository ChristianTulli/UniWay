package uniway.viewcontroller.fxml;

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
import uniway.beans.UtenteBean;
import uniway.controller.IscrittoSelezionaCorsoController;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;

public class IscrittoSelezionaCorsoViewController implements Initializable {

    private final IscrittoSelezionaCorsoController iscrittoSelezionaCorsoController = new IscrittoSelezionaCorsoController();
    private UtenteBean utenteBean;
    private String corsoSelezionato = "Corso selezionato: ";

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
        setupComboBox(regione, iscrittoSelezionaCorsoController.getRegioni(), this::handleRegioneSelection);
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
        resetComboBoxes(provincia, comune, ateneo, disciplina, tipologia, classe);
        setupComboBox(provincia, iscrittoSelezionaCorsoController.getProvince(regione.getValue()), this::handleProvinciaSelection);
    }

    @FXML
    public void handleProvinciaSelection(ActionEvent event) {
        resetComboBoxes(comune, ateneo, disciplina, tipologia, classe);
        setupComboBox(comune, iscrittoSelezionaCorsoController.getComuni(provincia.getValue()), this::handleComuneSelection);
    }

    @FXML
    public void handleComuneSelection(ActionEvent event) {
        resetComboBoxes(ateneo, disciplina, tipologia, classe);
        setupComboBox(ateneo, iscrittoSelezionaCorsoController.getAtenei(comune.getValue()), this::handleAteneoSelection);
    }

    @FXML
    public void handleAteneoSelection(ActionEvent event) {
        resetComboBoxes(disciplina, tipologia, classe);
        setupComboBox(disciplina, iscrittoSelezionaCorsoController.getDiscipline(ateneo.getValue()), this::handleDisciplinaSelection);
    }

    @FXML
    public void handleDisciplinaSelection(ActionEvent event) {
        resetComboBoxes(tipologia, classe);
        setupComboBox(tipologia, iscrittoSelezionaCorsoController.getTipologie(disciplina.getValue()), this::handleTipologiaSelection);
    }

    @FXML
    public void handleTipologiaSelection(ActionEvent event) {
        resetComboBoxes(classe);
        setupComboBox(classe, iscrittoSelezionaCorsoController.getCorsi(tipologia.getValue()), this::handleClasseSelection);
    }

    @FXML
    public void handleClasseSelection(ActionEvent event) {
        resetComboBoxes();
        cerca.setDisable(false);
        cerca.setOnAction(this::handleCercaSelection);
    }

    private void caricaInterfacciaCommenti() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/IscrittoInsegnamentiUI.fxml"));
        Parent newRoot = loader.load();

        IscrittoInsegnamentiViewController controller = loader.getController();
        controller.impostaSchermata(utenteBean);

        Stage current = (Stage) listView.getScene().getWindow();
        current.setScene(new Scene(newRoot));
        current.show();
    }

    @FXML
    public void handleCercaSelection(ActionEvent event) {
        reimpostaUI();

        List<String> risultati = iscrittoSelezionaCorsoController.getRisultati(classe.getValue());
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

        List<String> curricula = iscrittoSelezionaCorsoController.getCurriculumPerCorso(corsoSelezionatoNuovo);

        if (curricula == null || curricula.isEmpty()) {
            impostaSoloCorso(corsoSelezionatoNuovo);
            aggiornaLabelSoloCorso(corsoSelezionatoNuovo);
            vaiAInterfacciaCommenti();
            return;
        }

        if (curricula.size() == 1) {
            String unico = curricula.get(0);
            impostaCorsoECurriculum(corsoSelezionatoNuovo, unico);
            aggiornaLabelCorsoECurriculum(corsoSelezionatoNuovo, unico);
            vaiAInterfacciaCommenti();
            return;
        }

        chiediCurriculumEProcedi(corsoSelezionatoNuovo, curricula);
    }

    private void chiediCurriculumEProcedi(String corso, List<String> curricula) {
        ChoiceDialog<String> dialog = new ChoiceDialog<>(curricula.get(0), curricula);
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
        iscrittoSelezionaCorsoController.setCorsoUtente(utenteBean, corso);
    }

    private void impostaCorsoECurriculum(String corso, String curriculum) {
        iscrittoSelezionaCorsoController.setCorsoUtente(utenteBean, corso);
        iscrittoSelezionaCorsoController.setCurriculumUtente(utenteBean, curriculum);
    }

    private void aggiornaLabelSoloCorso(String corso) {
        label.setText(corsoSelezionato + corso);
    }

    private void aggiornaLabelCorsoECurriculum(String corso, String curriculum) {
        label.setText(corsoSelezionato + corso + "\ncurriculum: " + curriculum);
    }

    private void vaiAInterfacciaCommenti() {
        try {
            caricaInterfacciaCommenti();
        } catch (IOException e) {
            e.printStackTrace(); // sostituisci con logging se preferisci
        }
    }


    public void logOut(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/view/LogInUI.fxml")));
        Stage current = (Stage) ((Node) event.getSource()).getScene().getWindow();
        current.setScene(new Scene(root));
        current.show();
    }
}

