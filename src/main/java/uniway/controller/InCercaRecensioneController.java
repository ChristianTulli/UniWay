package uniway.controller;

import uniway.beans.RecensioneBean;
import uniway.model.Recensione;
import uniway.persistenza.RecensioneDAO;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class InCercaRecensioneController {
    private static final Logger LOGGER = Logger.getLogger(InCercaRecensioneController.class.getName());
    private final RecensioneDAO recensioneDAO;

    public InCercaRecensioneController() {
        this.recensioneDAO = PersistenzaController.getInstance().getRecensioneDAO();
    }

    public List<RecensioneBean> getRecensioni(int idInsegnamento) {
        try {
            List<RecensioneBean> recensioniBean= new ArrayList<>();
            List<Recensione> recensioni= recensioneDAO.getRecensioniByInsegnamento(idInsegnamento);
            for (Recensione rec : recensioni) {
                RecensioneBean bean = new RecensioneBean();
                bean.setNome(rec.getNomeUtente());
                bean.setCommento(rec.getCommento());
                bean.setValutazione(rec.getValutazione());
                bean.setData(rec.getData());
                recensioniBean.add(bean);
            }
            return recensioniBean;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Errore nel recupero delle recensioni", e);
            return new ArrayList<>();
        }
    }

    public double getMediaValutazioni(List<RecensioneBean> recensioni) {
        if (recensioni.isEmpty()) return 0.0;
        return recensioni.stream()
                .mapToInt(RecensioneBean::getValutazione)
                .average()
                .orElse(0.0);
    }
}
