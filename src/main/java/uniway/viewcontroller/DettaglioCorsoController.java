package uniway.viewcontroller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.stage.Stage;
import uniway.beans.UtenteBean;
import uniway.controller.GestoreDettaglioCorso;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

public class DettaglioCorsoController {

    private Scene scene;
    private Stage stage;
    private Parent root;
    private UtenteBean utenteBean;
    private String corsoCorrente;
    private String nomeCorso;
    private String nomeAteneo;

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
    private ListView<String> listaInsegnamentiCorso;

    @FXML
    private ListView<String> listaCorsiSimili;

    @FXML
    private Button preferitiButton;

    private List<String> corsiSimili;

    public void setCorsoSelezionato(String corso, List<String> corsiSimili) {
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

        // Mostra il messaggio di errore se non ci sono altri corsi simili
        if (listaCorsiSimili.getItems().isEmpty()) {
            erroreLabel.setText("Nessun corso simile con i filtri precedentemente impostati.");
        } else {
            erroreLabel.setText("");
        }

        // Popola la lista degli insegnamenti usando GestoreDettaglioCorso
        List<String> insegnamenti = GestoreDettaglioCorso.getInsegnamenti(nomeCorso);
        listaInsegnamentiCorso.getItems().setAll(insegnamenti);
    }

    @FXML
    public void aggiungiAiPreferiti(ActionEvent event) {
        if (utenteBean != null && nomeCorso != null) {
            GestoreDettaglioCorso gestore = new GestoreDettaglioCorso(); // Creazione dell'istanza
            gestore.aggiungiAiPreferiti(utenteBean, nomeCorso, nomeAteneo);
            preferitiButton.setDisable(true);
        }
    }


    public void goBack(ActionEvent event) throws IOException {
        root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/view/ricerca-home.fxml")));
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
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


