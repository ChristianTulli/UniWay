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
import uniway.beans.UtenteBean;
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
    private UtenteBean utenteBean; // Aggiunto per tenere traccia dell'utente loggato
    private String corsoSelezionato= "Corso selezionato: ";

    public void setUtenteBean(UtenteBean utenteBean) {
        this.utenteBean = utenteBean;
    }


    @FXML
    private ComboBox<String> regione;
    @FXML
    private ComboBox<String> provincia;
    @FXML
    private ComboBox<String> comune;
    @FXML
    private ComboBox<String> ateneo;
    @FXML
    private ComboBox<String>disciplina;
    @FXML
    private ComboBox<String> tipologia;
    @FXML
    private ComboBox<String> classe;
    @FXML
    private Button cerca;
    @FXML
    private ListView<String> listView;
    @FXML
    private Label label;


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
        resetComboBoxes(provincia, comune, ateneo, disciplina, tipologia, classe);
        setupComboBox(provincia, gestioneIscritto.getProvince(regione.getValue()), this::handleProvinciaSelection);

    }

    @FXML
    public void handleProvinciaSelection(ActionEvent event) {
        resetComboBoxes(comune, ateneo, disciplina, tipologia, classe);
        setupComboBox(comune, gestioneIscritto.getComuni(provincia.getValue()), this::handleComuneSelection);

    }

    @FXML
    public void handleComuneSelection(ActionEvent event) {
        resetComboBoxes(ateneo, disciplina, tipologia, classe);
        setupComboBox(ateneo, gestioneIscritto.getAtenei(comune.getValue()), this::handleAteneoSelection);

    }

    @FXML
    public void handleAteneoSelection(ActionEvent event) {
        resetComboBoxes(disciplina, tipologia, classe);
        setupComboBox(disciplina, gestioneIscritto.getDiscipline(ateneo.getValue()), this::handleDisciplinaSelection);

    }

    @FXML
    public void handleDisciplinaSelection(ActionEvent event) {
        resetComboBoxes(tipologia, classe);
        setupComboBox(tipologia, gestioneIscritto.getTipologie(disciplina.getValue()), this::handleTipologiaSelection);

    }

    @FXML
    public void handleTipologiaSelection(ActionEvent event) {
        resetComboBoxes(classe);
        setupComboBox(classe, gestioneIscritto.getCorsi(tipologia.getValue()), this::handleClasseSelection);
    }

    @FXML
    public void handleClasseSelection(ActionEvent event) {
        resetComboBoxes();
        cerca.setDisable(false);
        cerca.setOnAction(this::handleCercaSelection);
    }

    private void caricaInterfacciaCommenti(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/iscritto-commento.fxml"));
        root = loader.load();

        CommentiController controller = loader.getController();
        controller.setUtenteBean(utenteBean); // passa l'utente alla nuova schermata

        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }


    @FXML
    public void handleCercaSelection(ActionEvent event) {
        listView.getItems().clear();
        label.setText("");
        List<String> risultato = gestioneIscritto.getRisultati(classe.getValue());
        if (risultato != null && !risultato.isEmpty()) {
            listView.getItems().addAll(risultato);
        }else {
            label.setText("Nessun risultato, controlla i filtri");
        }
        listView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                List<String> curriculumDisponibili = gestioneIscritto.getCurriculumPerCorso(newValue);
                //logica del curriculum
                if (curriculumDisponibili != null && curriculumDisponibili.size() > 1) {
                    // Scelta curriculum tramite dialog
                    ChoiceDialog<String> dialog = new ChoiceDialog<>(curriculumDisponibili.get(0), curriculumDisponibili);
                    dialog.setTitle("Seleziona curriculum");
                    dialog.setHeaderText("Curriculum disponibili per il corso selezionato:");
                    dialog.setContentText("Scegli curriculum:");

                    dialog.showAndWait().ifPresent(curr -> {
                        gestioneIscritto.setCorsoUtente(utenteBean, newValue); // imposta idCorso
                        gestioneIscritto.setCurriculumUtente(utenteBean, curr); // imposta curriculum
                        label.setText(corsoSelezionato + newValue + "\ncurriculum: " + curr);
                    });
                } else if (curriculumDisponibili != null && curriculumDisponibili.size() == 1) {
                    // Salva direttamente
                    gestioneIscritto.setCorsoUtente(utenteBean, newValue);
                    gestioneIscritto.setCurriculumUtente(utenteBean, curriculumDisponibili.get(0));
                    label.setText(corsoSelezionato + newValue + "\ncurriculum: " + curriculumDisponibili.get(0));
                } else{
                    // Nessun curriculum salva solo il corso
                    gestioneIscritto.setCorsoUtente(utenteBean, newValue);
                    label.setText(corsoSelezionato + newValue);
                }try {
                    caricaInterfacciaCommenti(event);
                }catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }


    public void logOut (ActionEvent event) throws IOException {
        root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/view/accesso-registrazione.fxml")));
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
}
