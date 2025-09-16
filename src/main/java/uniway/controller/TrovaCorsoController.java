package uniway.controller;

import uniway.beans.InsegnamentoBean;
import uniway.beans.RecensioneBean;
import uniway.model.Corso;
import uniway.model.Insegnamento;
import uniway.model.Recensione;
import uniway.patterns.SessioneControllerSingleton;
import uniway.persistenza.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class TrovaCorsoController {

    private CorsoDAO corsoDAO;
    private AteneoDAO ateneoDAO;
    private InsegnamentoDAO insegnamentoDAO;
    private RecensioneDAO recensioneDAO;

    private String statale;
    private String tipologia;
    private String regione;
    private String provincia;
    private String comune;
    private String durata;
    private String gruppoDisciplina;
    private String classeCorso;

    public TrovaCorsoController() {
        this.corsoDAO=SessioneControllerSingleton.getInstance().getCorsoDAO();
        this.ateneoDAO=SessioneControllerSingleton.getInstance().getAteneoDAO();
        this.insegnamentoDAO = SessioneControllerSingleton.getInstance().getInsegnamentoDAO();
        this.recensioneDAO = SessioneControllerSingleton.getInstance().getRecensioneDAO();
    }
    //FILTRI DEDICATI ALLA RICERCA PER GLI UTENTI IN CERCA DI UNIVERSITA'

    //COLONNA 1: TIPOLOGIA ATENEO
    public List<String> getTipiAteneo() {
        return ateneoDAO.getAllTipiAteneo();
    }

    public List<String> getTipologie(String stataleSelezionato) {
        this.statale = stataleSelezionato;
        return ateneoDAO.getTipologieByStatale(statale);
    }

    public void setTipologia(String tipologiaSelezionata) {
        this.tipologia = tipologiaSelezionata;
    }

    //COLONNA 2: UBICAZIONE
    public List<String> getRegioni() {
        return corsoDAO.getAllRegioni();
    }

    public List<String> getProvince(String regioneSelezionata) {
        this.regione = regioneSelezionata;
        return corsoDAO.getProvinceByRegione(regione);
    }

    public List<String> getComuni(String provinciaSelezionata) {
        this.provincia = provinciaSelezionata;
        return corsoDAO.getComuniByProvincia(provincia);
    }

    public void setComune(String comuneSelezionato) {
        this.comune = comuneSelezionato;
    }

    //COLONNA 3: CARATTERISTICHE CORSO
    public List<String> getDurate() {
        return corsoDAO.getAllDurate();
    }

    public List<String> getDiscipline(String durataSelezionata) {
        this.durata = durataSelezionata;
        return corsoDAO.getDisciplineByDurata(durata);
    }

    public List<String> getClassi(String gruppoSelezionato) {
        this.gruppoDisciplina = gruppoSelezionato;
        return corsoDAO.getClassiByDisciplina(gruppoDisciplina, durata);
    }

    public void setClasseCorso(String classeSelezionata) {
        this.classeCorso = classeSelezionata;
    }

    //CERCA RISULTATI
    public List<String> getRisultati() {
        List<String> filtri = new ArrayList<>();
        filtri.add(statale != null ? statale : "");
        filtri.add(tipologia != null ? tipologia : "");
        filtri.add(regione != null ? regione : "");
        filtri.add(provincia != null ? provincia : "");
        filtri.add(comune != null ? comune : "");
        filtri.add(durata != null ? durata : "");
        filtri.add(gruppoDisciplina != null ? gruppoDisciplina : "");
        filtri.add(classeCorso != null ? classeCorso : "");

        return corsoDAO.getRisultatiRicerca(filtri);
    }

    //MOSTRA GLI INSEGNAMENTI DI UN CORSO SELEZIONATO
    public List<InsegnamentoBean> mostraInsegnamenti(String corsoCorrente, String ateneoCorrente) {
        List<Insegnamento> insegnamenti = new ArrayList<>();
        List<InsegnamentoBean> insegnamentiBean = new ArrayList<>();
        Corso corso = corsoDAO.getCorsoByNomeAndAteneo(corsoCorrente, ateneoCorrente);
        insegnamentoDAO.getInsegnamenti(corso, insegnamenti);
        for (Insegnamento ins : insegnamenti) {
            InsegnamentoBean bean = new InsegnamentoBean();
            bean.setNome(ins.getNome());
            bean.setAnno(ins.getAnno());
            bean.setSemestre(ins.getSemestre());
            bean.setCfu(ins.getCfu());
            insegnamentiBean.add(bean);
        }

        return insegnamentiBean;
    }

    public List<RecensioneBean> getRecensioni(String nomeCorso, String nomeAteneo, String nomeInsegnamento) {
        Objects.requireNonNull(nomeCorso);
        Objects.requireNonNull(nomeAteneo);
        Objects.requireNonNull(nomeInsegnamento);
        Corso corso = corsoDAO.getCorsoByNomeAndAteneo(nomeCorso, nomeAteneo);
        Insegnamento insegnamento = insegnamentoDAO.getInsegnamento(corso,nomeInsegnamento);
        try {
            List<RecensioneBean> recensioniBean= new ArrayList<>();
            List<Recensione> recensioni= recensioneDAO.getRecensioniByInsegnamento(insegnamento);
            for (Recensione rec : recensioni) {
                RecensioneBean bean = new RecensioneBean();
                bean.setNome(rec.getNomeUtente());
                bean.setCommento(rec.getCommento());
                bean.setValutazione(rec.getValutazione());
                recensioniBean.add(bean);
            }
            return recensioniBean;
        } catch (Exception e) {
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