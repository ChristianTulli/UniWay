package uniway.controller;

import uniway.beans.InsegnamentoBean;
import uniway.model.Insegnamento;
import uniway.persistenza.CorsoDAO;
import uniway.persistenza.InsegnamentoDAO;
import uniway.persistenza.RecensioneDAO;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class IscrittoInsegnamentiController {
    private CorsoDAO corsoDAO;
    private InsegnamentoDAO insegnamentoDAO;
    private RecensioneDAO recensioneDAO;
    private List<Insegnamento> insegnamentiDelCorso = new ArrayList<>();

    public IscrittoInsegnamentiController(){
            corsoDAO = new CorsoDAO();
            insegnamentoDAO = new InsegnamentoDAO();
            recensioneDAO = PersistenzaController.getInstance().getRecensioneDAO();
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
