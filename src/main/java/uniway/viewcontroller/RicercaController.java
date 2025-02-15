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
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import uniway.beans.UtenteBean;
import uniway.controller.GestioneRicerca;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RicercaController implements Initializable {

    private Scene scene;
    private Stage stage;
    private Parent root;
    private final GestioneRicerca gestioneRicerca = new GestioneRicerca();
    private UtenteBean utenteBean;
    private String interfacciaCorso = "/view/dettaglio-corso.fxml";
    private static final Logger LOGGER = Logger.getLogger(RicercaController.class.getName());

    public void setUtenteBean(UtenteBean utenteBean) {
        this.utenteBean = utenteBean;
    }

    // 🔵 COLONNA 1: Tipologia di Ateneo
    @FXML private ComboBox<String> statale;
    @FXML private ComboBox<String> tipologia;

    // 🔵 COLONNA 2: Ubicazione
    @FXML private ComboBox<String> regione;
    @FXML private ComboBox<String> provincia;
    @FXML private ComboBox<String> comune;

    // 🔵 COLONNA 3: Caratteristiche del Corso
    @FXML private ComboBox<String> durata;
    @FXML private ComboBox<String> gruppoDisciplina;
    @FXML private ComboBox<String> classeCorso;

    @FXML private Button cerca;
    @FXML private ListView<String> listView;
    @FXML private Label label;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setupComboBox(statale, gestioneRicerca.getTipiAteneo(), this::handleStataleSelection);
        setupComboBox(regione, gestioneRicerca.getRegioni(), this::handleRegioneSelection);
        setupComboBox(durata, gestioneRicerca.getDurate(), this::handleDurataSelection);
        cerca.setDisable(true); // Disattiva il tasto "Cerca" all'inizio

        // Listener per il doppio click su un elemento della ListView
        listView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                label.setText("Corso selezionato: " + newValue);
                apriDettaglioCorso(newValue);
            }
        });
    }



    private void setupComboBox(ComboBox<String> comboBox, List<String> items, EventHandler<ActionEvent> eventHandler) {
        comboBox.getItems().setAll(items);
        comboBox.setDisable(items.isEmpty());
        comboBox.setOnAction(event -> {
            if (eventHandler != null) eventHandler.handle(event);
        });
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
        boolean durataSelezionata = durata.getValue() != null && !durata.getValue().isEmpty();

        // Il tasto cerca si attiva solo quando tutti e tre i filtri principali sono stati selezionati
        boolean tuttiPrincipaliSelezionati = stataleSelezionato && regioneSelezionata && durataSelezionata;

        cerca.setDisable(!tuttiPrincipaliSelezionati);
    }


    // 🔵 COLONNA 1: TIPOLOGIA ATENEO
    @FXML
    public void handleStataleSelection(ActionEvent event) {
        resetComboBoxes(tipologia);
        setupComboBox(tipologia, gestioneRicerca.getTipologie(statale.getValue()), this::handleTipologiaSelection);
        checkCercaEnabled();
    }

    @FXML
    public void handleTipologiaSelection(ActionEvent event) {
        resetComboBoxes();
        gestioneRicerca.setTipologia(tipologia.getValue());
    }

    // 🔵 COLONNA 2: UBICAZIONE
    @FXML
    public void handleRegioneSelection(ActionEvent event) {
        resetComboBoxes(provincia, comune);
        setupComboBox(provincia, gestioneRicerca.getProvince(regione.getValue()), this::handleProvinciaSelection);
        checkCercaEnabled();
    }

    @FXML
    public void handleProvinciaSelection(ActionEvent event) {
        resetComboBoxes(comune);
        setupComboBox(comune, gestioneRicerca.getComuni(provincia.getValue()), this::handleComuneSelection);
    }

    @FXML
    public void handleComuneSelection(ActionEvent event) {
        resetComboBoxes();
        gestioneRicerca.setComune(comune.getValue());
    }

    // 🔵 COLONNA 3: CARATTERISTICHE CORSO
    @FXML
    public void handleDurataSelection(ActionEvent event) {
        resetComboBoxes(gruppoDisciplina, classeCorso);
        setupComboBox(gruppoDisciplina, gestioneRicerca.getDiscipline(durata.getValue()), this::handleGruppoSelection);
        checkCercaEnabled();
    }

    @FXML
    public void handleGruppoSelection(ActionEvent event) {
        resetComboBoxes(classeCorso);
        setupComboBox(classeCorso, gestioneRicerca.getClassi(gruppoDisciplina.getValue()), this::handleClasseSelection);
    }

    @FXML
    public void handleClasseSelection(ActionEvent event) {
        resetComboBoxes();
        gestioneRicerca.setClasseCorso(classeCorso.getValue());
    }

    // 🔵 CERCA RISULTATI
    @FXML
    public void handleCercaSelection(ActionEvent event) {
        resetComboBoxes();
        List<String> risultato = gestioneRicerca.getRisultati();
        if (risultato != null && !risultato.isEmpty()) {
            listView.getItems().addAll(risultato);
        } else {
            label.setText("Nessun risultato, prova a cambiare i filtri.");
        }
    }

    // 🔹 Metodo per aprire la schermata del dettaglio del corso
    private void apriDettaglioCorso(String corsoSelezionato) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(interfacciaCorso));
            Parent newRoot = loader.load();

            // Ottieni il controller della nuova interfaccia
            DettaglioCorsoController controller = loader.getController();
            controller.setCorsoSelezionato(corsoSelezionato);

            // Cambia la schermata attuale
            stage = (Stage) listView.getScene().getWindow();
            scene = new Scene(newRoot);
            stage.setScene(scene);
            stage.show();

        } catch (IOException e) {
                LOGGER.log(Level.SEVERE, "errore nell'apertura nella visualizzazione del corso", e);
            }
    }


    public void logOut(ActionEvent event) throws IOException {
        root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/view/accesso-registrazione.fxml")));
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
}


