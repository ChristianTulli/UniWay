package uniway.controller;

import uniway.beans.InsegnamentoBean;
import uniway.model.Corso;
import uniway.model.Insegnamento;
import uniway.model.UtenteInCerca;
import uniway.patterns.SessioneControllerSingleton;
import uniway.persistenza.CorsoDAO;
import uniway.persistenza.InsegnamentoDAO;
import uniway.persistenza.UtenteDAO;

import java.util.ArrayList;
import java.util.List;

public class ConfrontaCorsoController {
    private final CorsoDAO corsoDAO;
    private final InsegnamentoDAO insegnamentoDAO;
    private final UtenteDAO utenteDAO;

    public ConfrontaCorsoController() {
        this.insegnamentoDAO = SessioneControllerSingleton.getInstance().getInsegnamentoDAO();
        this.corsoDAO = SessioneControllerSingleton.getInstance().getCorsoDAO();
        this.utenteDAO = SessioneControllerSingleton.getInstance().getUtenteDAO();
    }

    public List<InsegnamentoBean> getInsegnamenti(String nomeCorso, String nomeAteneo) {
        List<Insegnamento> insegnamenti = new ArrayList<>();
        Corso corso = corsoDAO.getCorsoByNomeAndAteneo(nomeCorso, nomeAteneo);
        insegnamentoDAO.getInsegnamenti(corso, insegnamenti);
        List<InsegnamentoBean> beanList = new ArrayList<>();

        for (Insegnamento ins : insegnamenti) {
            InsegnamentoBean bean = new InsegnamentoBean();
            bean.setNome(ins.getNome());
            bean.setAnno(ins.getAnno());
            bean.setSemestre(ins.getSemestre());
            bean.setCfu(ins.getCfu());
            beanList.add(bean);
        }

        return beanList;
    }

    public void aggiungiAiPreferiti(String nomeCorso, String nomeAteneo) {
        UtenteInCerca uc = (UtenteInCerca) SessioneControllerSingleton.getInstance().getCurrentUser();
            Corso corso = corsoDAO.getCorsoByNomeAndAteneo(nomeCorso, nomeAteneo);
            utenteDAO.aggiungiPreferitiUtente(uc, corso);
            uc.aggiungiPreferito(corso);

    }
}

