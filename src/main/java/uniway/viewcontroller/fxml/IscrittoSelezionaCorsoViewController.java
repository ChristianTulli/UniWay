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
        listView.getItems().clear();
        label.setText("");

        List<String> risultato = iscrittoSelezionaCorsoController.getRisultati(classe.getValue());
        if (risultato != null && !risultato.isEmpty()) {
            listView.getItems().addAll(risultato);
        } else {
            label.setText("Nessun risultato, controlla i filtri");
        }

        if (!listenerAgganciato) {
            listenerAgganciato = true;
            listView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
                if (newValue != null) {
                    List<String> curriculumDisponibili = iscrittoSelezionaCorsoController.getCurriculumPerCorso(newValue);

                    if (curriculumDisponibili != null && curriculumDisponibili.size() > 1) {
                        ChoiceDialog<String> dialog = new ChoiceDialog<>(curriculumDisponibili.get(0), curriculumDisponibili);
                        dialog.setTitle("Seleziona curriculum");
                        dialog.setHeaderText("Curriculum disponibili per il corso selezionato:");
                        dialog.setContentText("Scegli curriculum:");

                        dialog.showAndWait().ifPresent(curr -> {
                            iscrittoSelezionaCorsoController.setCorsoUtente(utenteBean, newValue);
                            iscrittoSelezionaCorsoController.setCurriculumUtente(utenteBean, curr);
                            label.setText(corsoSelezionato + newValue + "\ncurriculum: " + curr);
                            try { caricaInterfacciaCommenti(); } catch (IOException e) { e.printStackTrace(); }
                        });

                    } else if (curriculumDisponibili != null && curriculumDisponibili.size() == 1) {
                        iscrittoSelezionaCorsoController.setCorsoUtente(utenteBean, newValue);
                        iscrittoSelezionaCorsoController.setCurriculumUtente(utenteBean, curriculumDisponibili.get(0));
                        label.setText(corsoSelezionato + newValue + "\ncurriculum: " + curriculumDisponibili.get(0));
                        try { caricaInterfacciaCommenti(); } catch (IOException e) { e.printStackTrace(); }

                    } else {
                        iscrittoSelezionaCorsoController.setCorsoUtente(utenteBean, newValue);
                        label.setText(corsoSelezionato + newValue);
                        try { caricaInterfacciaCommenti(); } catch (IOException e) { e.printStackTrace(); }
                    }
                }
            });
        }
    }

    public void logOut(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/view/LogInUI.fxml")));
        Stage current = (Stage) ((Node) event.getSource()).getScene().getWindow();
        current.setScene(new Scene(root));
        current.show();
    }
}

