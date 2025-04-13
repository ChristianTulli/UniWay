package uniway.controller;

import uniway.beans.InsegnamentoBean;
import uniway.beans.UtenteBean;
import uniway.model.Insegnamento;
import uniway.model.UtenteInCerca;
import uniway.persistenza.CorsoDAO;
import uniway.persistenza.InsegnamentoDAO;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class GestoreDettaglioCorso {
    private static final Logger LOGGER = Logger.getLogger(GestoreDettaglioCorso.class.getName());
    private CorsoDAO corsoDAO;
    private String comune;
    private String ateneo;
    private String tipologia;
    private final GestioneLogin gestioneLogin = GestioneLogin.getInstance();
    private InsegnamentoDAO insegnamentoDAO;

    public GestoreDettaglioCorso() throws IllegalArgumentException {
        Properties properties = new Properties();
        try (FileInputStream input = new FileInputStream("src/main/resources/config.properties")) {
            properties.load(input);
            corsoDAO = new CorsoDAO(properties.getProperty("db.url"), properties.getProperty("db.username"), properties.getProperty("db.password"));
            insegnamentoDAO = new InsegnamentoDAO(properties.getProperty("db.url"), properties.getProperty("db.username"), properties.getProperty("db.password"));
        } catch (FileNotFoundException e) {
            throw new IllegalArgumentException("File config.properties non trovato", e);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Errore durante il caricamento della configurazione", e);
        }
    }

    public List<InsegnamentoBean> getInsegnamenti(String corsoCorrente, String ateneoCorrente) {
        // Implementare la logica per ottenere gli insegnamenti
        List<Insegnamento> insegnamenti = new ArrayList<>();
        List<InsegnamentoBean> insegnamentiBean = new ArrayList<>();
        insegnamentoDAO.getInsegnamentiFromDB(corsoCorrente, ateneoCorrente, insegnamenti);
        for (Insegnamento ins : insegnamenti) {
            InsegnamentoBean bean = new InsegnamentoBean();
            bean.setNome(ins.getNome());
            bean.setAnno(ins.getAnno());
            bean.setSemestre(ins.getSemestre());
            bean.setCfu(ins.getCfu());
            bean.setCurriculum(ins.getCurriculum());
            insegnamentiBean.add(bean);
        }

        return insegnamentiBean;
    }

    public void aggiungiAiPreferiti(UtenteBean utenteBean, String corsoSelezionato, String ateneoSelezionato) {
        Integer idCorso = corsoDAO.getIdCorsoByNomeAndAteneo(ateneoSelezionato, corsoSelezionato);

        if (gestioneLogin.isFullMode()) {
            try {
                gestioneLogin.getUtenteDAO().aggiungiPreferitiUtente(utenteBean.getUsername(), idCorso);
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Errore durante l'inserimento nei preferiti", e);
            }
        } else {
            gestioneLogin.getUtenti().stream()
                    .filter(u -> u instanceof UtenteInCerca && u.getUsername().equals(utenteBean.getUsername()))
                    .map(u -> (UtenteInCerca) u)
                    .findFirst()
                    .ifPresent(u -> u.getPreferenze().add(idCorso));
        }
    }
}

