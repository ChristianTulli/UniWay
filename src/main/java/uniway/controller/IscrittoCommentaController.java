package uniway.controller;

import uniway.beans.InsegnamentoBean;
import uniway.beans.UtenteBean;
import uniway.model.Insegnamento;
import uniway.model.Recensione;
import uniway.persistenza.RecensioneDAO;

public class IscrittoCommentaController {

    private RecensioneDAO recensioneDAO;
    private CommentaEValutaInsegnamentoController commentaEValutaInsegnamentoController;

    public IscrittoCommentaController() {
            recensioneDAO = PersistenzaController.getInstance().getRecensioneDAO();
    }

    public void setIscrittoVisualizzaInsegnamentiController(CommentaEValutaInsegnamentoController commentaEValutaInsegnamentoController) {
        this.commentaEValutaInsegnamentoController = commentaEValutaInsegnamentoController;
    }

    public void salvaRecensione(UtenteBean utenteBean, InsegnamentoBean insegnamentoBean, String testo, Integer valutazione){
        Insegnamento insegnamento = commentaEValutaInsegnamentoController.passaInsegnamento(insegnamentoBean);
        Recensione recensione= new Recensione(testo, valutazione, utenteBean.getUsername(), insegnamento.getId());
        recensioneDAO.setRecesnione(recensione);

    }
}
