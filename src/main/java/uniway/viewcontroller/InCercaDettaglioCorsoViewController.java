package uniway.viewcontroller;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import uniway.beans.InsegnamentoBean;
import uniway.beans.UtenteBean;
import uniway.controller.InCercaDettaglioCorsoController;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

public class InCercaDettaglioCorsoViewController implements Initializable {

    private Scene scene;
    private Stage stage;
    private Parent root;
    private UtenteBean utenteBean;
    private String corsoCorrente;
    private String nomeCorso;
    private String nomeAteneo;
    private final InCercaDettaglioCorsoController inCercaDettaglioCorsoController = new InCercaDettaglioCorsoController();
    private static final Logger LOGGER = Logger.getLogger(InCercaDettaglioCorsoViewController.class.getName());

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
        insegnamento.setCellValueFactory(new PropertyValueFactory<>("nome"));
        curriculum.setCellValueFactory(new PropertyValueFactory<>("curriculum"));
        cfu.setCellValueFactory(new PropertyValueFactory<>("cfu"));
        semestre.setCellValueFactory(new PropertyValueFactory<>("semestre"));
        anno.setCellValueFactory(new PropertyValueFactory<>("anno"));

        setWrappedTextCellFactory(insegnamento);
        setWrappedTextCellFactory(curriculum);

        listaCorsiSimili.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                String corsoSimile = listaCorsiSimili.getSelectionModel().getSelectedItem();
                if (corsoSimile != null) {
                    try {
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/InCercaConfrontaCorsoUI.fxml"));
                        root = loader.load();
                        uniway.viewcontroller.InCercaConfrontaCorsoViewController controller = loader.getController();
                        controller.impostaSchermata(utenteBean, corsoCorrente, corsoSimile, corsiSimili);
                        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                        stage.setScene(new Scene(root));
                        stage.show();
                    } catch (IOException e) {
                        LOGGER.log(Level.SEVERE, "Errore nell'apertura della schermata di confronto", e);
                    }
                }
            }
        });

        tableView.setRowFactory(tv -> {
            TableRow<InsegnamentoBean> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && !row.isEmpty()) {
                    InsegnamentoBean insegnamentoSelezionato = row.getItem();
                    try {
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/InCercaRecensioneUI.fxml"));
                        Parent rootRecensioni = loader.load();
                        InCercaRecensioneViewController controller = loader.getController();

                        // Recupera l'id dell'insegnamento dal controller applicativo
                        Integer idInsegnamento = inCercaDettaglioCorsoController.getIdInsegnamento(
                                insegnamentoSelezionato.getNome(),
                                nomeCorso,
                                nomeAteneo
                        );

                        // Imposta i dati nella schermata recensioni
                        controller.impostaSchermata(
                                idInsegnamento,
                                nomeCorso,
                                nomeAteneo,
                                insegnamentoSelezionato.getNome(),
                                insegnamentoSelezionato.getCurriculum(),
                                utenteBean,
                                corsiSimili
                        );

                        Stage stage = (Stage) tableView.getScene().getWindow();
                        stage.setScene(new Scene(rootRecensioni));
                        stage.show();
                    } catch (IOException e) {
                        LOGGER.log(Level.SEVERE, "Errore nell'apertura della schermata recensioni", e);
                    }
                }
            });
            return row;
        });

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

        corsoLabel.setText("Corso: " + dettagli[0]);
        nomeCorso = dettagli[0];
        ateneoLabel.setText("Ateneo: " + dettagli[1]);
        nomeAteneo = dettagli[1];

        listaCorsiSimili.getItems().clear();
        for (String corsoSimile : corsiSimili) {
            if (!corsoSimile.equals(corso)) {
                listaCorsiSimili.getItems().add(corsoSimile);
            }
        }

        erroreLabel.setText(listaCorsiSimili.getItems().isEmpty()
                ? "Effettua una nuova ricerca per vedere corsi simili"
                : "");

        List<InsegnamentoBean> lista = inCercaDettaglioCorsoController.getInsegnamenti(nomeCorso, nomeAteneo);
        tableView.setItems(FXCollections.observableArrayList(lista));
    }

    public void impostaSchermata(UtenteBean utenteBean, String corso, List<String> corsiSimili){
        setUtenteBean(utenteBean);
        setCorsoSelezionato(corso, corsiSimili);
    }

    @FXML
    public void aggiungiAiPreferiti(ActionEvent event) {
        if (utenteBean != null && nomeCorso != null) {
            inCercaDettaglioCorsoController.aggiungiAiPreferiti(utenteBean, nomeCorso, nomeAteneo);
            preferitiButton.setDisable(true);
        }
    }

    public void goBack(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/InCercaTrovaCorsoUI.fxml"));
        Parent newRoot = loader.load();
        InCercaTrovaCorsoViewController controller = loader.getController();
        controller.impostaSchermata(utenteBean);
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(newRoot));
        stage.show();
    }

    public void goToPreferiti(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/InCercaPreferitiUI.fxml"));
        Parent newRoot = loader.load();
        InCercaPreferitiViewController controller = loader.getController();
        controller.impostaSchermata(utenteBean);
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(newRoot));
        stage.show();
    }

    public void logOut(ActionEvent event) throws IOException {
        root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/view/LogInUI.fxml")));
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.show();
    }
}



