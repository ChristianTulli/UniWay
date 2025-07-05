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

public class InCercaConfrontaCorsoController {
    private static final Logger LOGGER = Logger.getLogger(InCercaConfrontaCorsoController.class.getName());
    private final CorsoDAO corsoDAO = new CorsoDAO();
    private final InsegnamentoDAO insegnamentoDAO = new InsegnamentoDAO();
    private final PersistenzaController persistenzaController = PersistenzaController.getInstance();

    public List<InsegnamentoBean> getInsegnamenti(String nomeCorso, String nomeAteneo) {
        List<Insegnamento> insegnamenti = new ArrayList<>();
        insegnamentoDAO.getInsegnamentiFromDB(nomeCorso, nomeAteneo, insegnamenti);
        List<InsegnamentoBean> beanList = new ArrayList<>();

        for (Insegnamento ins : insegnamenti) {
            InsegnamentoBean bean = new InsegnamentoBean();
            bean.setNome(ins.getNome());
            bean.setAnno(ins.getAnno());
            bean.setSemestre(ins.getSemestre());
            bean.setCfu(ins.getCfu());
            bean.setCurriculum(ins.getCurriculum());
            beanList.add(bean);
        }

        return beanList;
    }

    public void aggiungiAiPreferiti(UtenteBean utenteBean, String nomeCorso, String nomeAteneo) {
        try {
            Integer idCorso = corsoDAO.getIdCorsoByNomeAndAteneo(nomeAteneo, nomeCorso);
            persistenzaController.getUtenteDAO().aggiungiPreferitiUtente(utenteBean.getUsername(), idCorso);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Errore durante l'aggiunta ai preferiti", e);
        }
    }
}
