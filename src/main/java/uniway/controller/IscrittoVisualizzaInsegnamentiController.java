package uniway.controller;

import uniway.beans.InsegnamentoBean;
import uniway.model.Insegnamento;
import uniway.persistenza.CorsoDAO;
import uniway.persistenza.InsegnamentoDAO;
import uniway.persistenza.RecensioneDAO;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class IscrittoVisualizzaInsegnamentiController {
    private static final Logger LOGGER = Logger.getLogger(IscrittoVisualizzaInsegnamentiController.class.getName());
    private CorsoDAO corsoDAO;
    private InsegnamentoDAO insegnamentoDAO;
    private RecensioneDAO recensioneDAO;
    private List<Insegnamento> insegnamentiDelCorso = new ArrayList<>();
    private Insegnamento insegnamentoSelezionato;

    public IscrittoVisualizzaInsegnamentiController() throws IllegalArgumentException {
        Properties properties = new Properties();
        try (FileInputStream input = new FileInputStream("src/main/resources/config.properties")) {
            properties.load(input);
            String username= properties.getProperty("db.username");
            String dbUrl = properties.getProperty("db.url");
            String password = properties.getProperty("db.password");

            corsoDAO = new CorsoDAO(dbUrl, username, password);
            insegnamentoDAO = new InsegnamentoDAO(dbUrl, username, password);
            recensioneDAO = new RecensioneDAO(dbUrl, username, password);
        } catch (FileNotFoundException e) {
            throw new IllegalArgumentException("File config.properties non trovato", e);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Errore durante il caricamento della configurazione", e);
        }
    }
    public List<InsegnamentoBean> getInsegnamenti(Integer idCorso, String curriculum, String usernameUtente) {
        List<InsegnamentoBean> insegnamentiBean = new ArrayList<>();
        insegnamentoDAO.getInsegnamentiByCurriculum(idCorso, curriculum, insegnamentiDelCorso);
        //trasmettere risultato passando la lista ottenuta a lista di InsegnamentiBean

        for (Insegnamento ins : insegnamentiDelCorso) {
            InsegnamentoBean bean = new InsegnamentoBean();
            bean.setNome(ins.getNome());
            bean.setAnno(ins.getAnno());
            bean.setSemestre(ins.getSemestre());
            bean.setCfu(ins.getCfu());
            bean.setCurriculum(ins.getCurriculum());
            Integer valutazione = recensioneDAO.getValutazioneUtente(ins.getId(), usernameUtente);
            bean.setValutazione(valutazione);
            insegnamentiBean.add(bean);
        }
        return insegnamentiBean;
    }
    public Insegnamento passaInsegnamento(InsegnamentoBean insegnamentoBean) {
        return insegnamentiDelCorso.stream()
                .filter(ins ->
                        Objects.equals(ins.getNome(), insegnamentoBean.getNome()) &&
                                Objects.equals(ins.getAnno(), insegnamentoBean.getAnno()) &&
                                Objects.equals(ins.getSemestre(), insegnamentoBean.getSemestre()) &&
                                Objects.equals(ins.getCfu(), insegnamentoBean.getCfu()) &&
                                Objects.equals(ins.getCurriculum(), insegnamentoBean.getCurriculum())
                )
                .findFirst()
                .orElse(null); // oppure lancia eccezione se obbligatorio
    }



    public String getCorso(Integer idCorso) {
        return corsoDAO.getNomeByIdCorso(idCorso);
    }

    public String getAteneo(Integer idCorso) {
        return corsoDAO.getAteneoByIdCorso(idCorso);
    }
}
