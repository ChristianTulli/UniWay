package uniway.controller;

import uniway.beans.InsegnamentoBean;
import uniway.beans.UtenteBean;
import uniway.model.Insegnamento;
import uniway.persistenza.CorsoDAO;
import uniway.persistenza.InsegnamentoDAO;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class InCercaDettaglioCorsoController {
    private static final Logger LOGGER = Logger.getLogger(InCercaDettaglioCorsoController.class.getName());
    private CorsoDAO corsoDAO;
    private PersistenzaController persistenzaController = PersistenzaController.getInstance();
    private InsegnamentoDAO insegnamentoDAO;
    private List<Insegnamento> insegnamenti=new ArrayList<>();

    public InCercaDettaglioCorsoController(){
        corsoDAO = new CorsoDAO();
        insegnamentoDAO = new InsegnamentoDAO();
    }

    public List<InsegnamentoBean> getInsegnamenti(String corsoCorrente, String ateneoCorrente) {
        // Implementare la logica per ottenere gli insegnamenti
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
        try {
            persistenzaController.getUtenteDAO().aggiungiPreferitiUtente(utenteBean.getUsername(), idCorso);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Errore durante l'inserimento nei preferiti", e);
        }
    }

    public Integer getIdInsegnamento(String NomeInsegnamento, String corso, String ateneo) {
        return insegnamentoDAO.getIdInsegnamento(NomeInsegnamento, corso, ateneo);
    }
}

