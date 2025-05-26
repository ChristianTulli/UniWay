package uniway.viewcontroller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import uniway.beans.InsegnamentoBean;
import uniway.beans.UtenteBean;
import uniway.controller.GestoreDettaglioCorso;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;

public class DettaglioCorsoController implements Initializable {

    private Scene scene;
    private Stage stage;
    private Parent root;
    private UtenteBean utenteBean;
    private String corsoCorrente;
    private String nomeCorso;
    private String nomeAteneo;
    private GestoreDettaglioCorso gestoreDettaglioCorso = new GestoreDettaglioCorso();

    public void setUtenteBean(UtenteBean utenteBean) {
        this.utenteBean = utenteBean;
    }

    @FXML
    private Label corsoLabel;

    @FXML
    private Label ateneoLabel;

    @FXML
    private Label erroreLabel;

    @FXML
    private TableView<InsegnamentoBean> tableView;

    @FXML
    private TableColumn<InsegnamentoBean, Integer> cfu;

    @FXML
    private TableColumn<InsegnamentoBean, String> curriculum;

    @FXML
    private TableColumn<InsegnamentoBean, String> insegnamento;

    @FXML
    private TableColumn<InsegnamentoBean, Integer> semestre;

    @FXML
    private TableColumn<InsegnamentoBean, Integer> anno;


    @FXML
    private ListView<String> listaCorsiSimili;

    @FXML
    private Button preferitiButton;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        insegnamento.setCellValueFactory(new PropertyValueFactory<InsegnamentoBean, String>("nome"));
        curriculum.setCellValueFactory(new PropertyValueFactory<InsegnamentoBean, String>("curriculum"));
        cfu.setCellValueFactory(new PropertyValueFactory<InsegnamentoBean, Integer>("cfu"));
        semestre.setCellValueFactory(new PropertyValueFactory<InsegnamentoBean, Integer>("semestre"));
        anno.setCellValueFactory(new PropertyValueFactory<InsegnamentoBean, Integer>("anno"));

    }

    private List<String> corsiSimili;

    public void setCorsoSelezionato(String corso, List<String> corsiSimili) {
        this.corsoCorrente = corso;
        this.corsiSimili = corsiSimili;
        String[] dettagli = corso.split(" - ");

        corsoLabel.setText("Corso: " + dettagli[0]);
        nomeCorso = dettagli[0];
        ateneoLabel.setText("Ateneo: " + dettagli[1]);
        nomeAteneo = dettagli[1];

        // Popola la lista dei corsi simili escludendo il corso selezionato
        listaCorsiSimili.getItems().clear();
        for (String corsoSimile : corsiSimili) {
            if (!corsoSimile.equals(corso)) {
                listaCorsiSimili.getItems().add(corsoSimile);
            }
        }

        // Messaggio di errore se vuota
        erroreLabel.setText(listaCorsiSimili.getItems().isEmpty() ?
                "Nessun corso simile con i filtri precedentemente impostati." : "");

        // popola la tabella insegnamenti
        List<InsegnamentoBean> lista = gestoreDettaglioCorso.getInsegnamenti(nomeCorso, nomeAteneo);
        ObservableList<InsegnamentoBean> listaInsegnamenti = FXCollections.observableArrayList(lista);
        tableView.setItems(listaInsegnamenti);
    }


    @FXML
    public void aggiungiAiPreferiti(ActionEvent event) {
        if (utenteBean != null && nomeCorso != null) {
            gestoreDettaglioCorso.aggiungiAiPreferiti(utenteBean, nomeCorso, nomeAteneo);
            preferitiButton.setDisable(true);
        }
    }


    public void goBack(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/ricerca-home.fxml"));
        Parent newRoot = loader.load();

        // Passa l'UtenteBean al nuovo controller
        RicercaController ricercaController = loader.getController();
        ricercaController.setUtenteBean(utenteBean);  // Mantiene l'utente attivo

        // Cambia schermata
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(newRoot);
        stage.setScene(scene);
        stage.show();
    }


    public void logOut(ActionEvent event) throws IOException {
        root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/view/accesso-registrazione.fxml")));
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
}


