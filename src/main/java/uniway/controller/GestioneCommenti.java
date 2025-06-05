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
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class GestioneCommenti {
    private static final Logger LOGGER = Logger.getLogger(GestioneCommenti.class.getName());
    private CorsoDAO corsoDAO;
    private InsegnamentoDAO insegnamentoDAO;
    private RecensioneDAO recensioneDAO;

    public GestioneCommenti() throws IllegalArgumentException {
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
        List<Insegnamento> insegnamenti = new ArrayList<>();
        List<InsegnamentoBean> insegnamentiBean = new ArrayList<>();
        insegnamentoDAO.getInsegnamentiByCurriculum(idCorso, curriculum, insegnamenti);
        //trasmettere risultato passando la lista ottenuta a lista di InsegnamentiBean

        for (Insegnamento ins : insegnamenti) {
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

    public String getCorso(Integer idCorso) {
        return corsoDAO.getNomeByIdCorso(idCorso); //fare DAO
    }

    public String getAteneo(Integer idCorso) {
        return corsoDAO.getAteneoByIdCorso(idCorso); //fare DAO
    }
}
