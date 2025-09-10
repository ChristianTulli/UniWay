package uniway.controller;

import uniway.beans.InsegnamentoBean;
import uniway.beans.UtenteBean;
import uniway.eccezioni.RecensioneNonSalvataException;
import uniway.eccezioni.UtenteNonTrovatoException;
import uniway.model.*;
import uniway.patterns.SessioneControllerSingleton;
import uniway.persistenza.CorsoDAO;
import uniway.persistenza.RecensioneDAO;
import uniway.persistenza.UtenteDAO;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class CommentaEValutaInsegnamentoController {

    private final CorsoDAO corsoDAO;
    private final UtenteDAO utenteDAO;
    private final RecensioneDAO recensioneDAO;
    private String regione;
    private String provincia;
    private String comune;
    private String ateneo;
    private String disciplina;
    private String durata;
    private String classe;
    private String corso;
    private String curriculum=null;


    public CommentaEValutaInsegnamentoController() {
        SessioneControllerSingleton sessioneControllerSingleton = SessioneControllerSingleton.getInstance();
        corsoDAO= sessioneControllerSingleton.getCorsoDAO();
        utenteDAO= sessioneControllerSingleton.getUtenteDAO();
        recensioneDAO= sessioneControllerSingleton.getRecensioneDAO();
    }

    public UtenteBean getUtenteBean(){
        UtenteBean ub= new UtenteBean();
        ub.setCurriculum(curriculum);
        ub.setNomeAteneo(ateneo);
        ub.setNomeCorso(corso);
        return ub;
    }

    public List<InsegnamentoBean> getInsegnamentiBean() {
        Utente u = SessioneControllerSingleton.getInstance().getCurrentUser();
        if (!(u instanceof UtenteIscritto ui)) {
            throw new IllegalStateException("L'utente corrente non è un iscritto");
        }

        Corso c = ui.getCorso();
        if (c == null || c.getInsegnamenti() == null) {
            return List.of(); // nessun insegnamento
        }

        List<InsegnamentoBean> beans = new ArrayList<>();
        for (Insegnamento ins : c.getInsegnamenti()) {
            InsegnamentoBean bean = new InsegnamentoBean();
            bean.setNome(ins.getNome());
            bean.setAnno(ins.getAnno());
            bean.setSemestre(ins.getSemestre());
            bean.setCurriculum(ins.getCurriculum());
            bean.setCfu(ins.getCfu());

            // recupero valutazione dal DAO
            Integer valutazione = recensioneDAO.getValutazioneUtente(ins, ui.getUsername());
            bean.setValutazione(valutazione);

            beans.add(bean);
        }
        return beans;
    }


    //sistema di filtri dinamico per la selezione del corso e curriculum di appartenenza:

    public List<String> getRegioni() {
        return corsoDAO.getAllRegioni();
    }

    public List<String> getProvince(String regioneselezionata) {
        Objects.requireNonNull(regioneselezionata);
        this.regione=regioneselezionata;
        return corsoDAO.getProvinceByRegione(regione);
    }

    public List<String> getComuni(String provinciaselezionata) {
        Objects.requireNonNull(provinciaselezionata);
        this.provincia=provinciaselezionata;
        return corsoDAO.getComuniByProvincia(provincia);
    }

    public List<String> getAtenei(String comuneselezionato) {
        Objects.requireNonNull(comuneselezionato);
        this.comune = comuneselezionato;
        return corsoDAO.getAteneiByComune(comune);
    }

    public List<String> getDiscipline(String ateneoselezionato) {
        Objects.requireNonNull(ateneoselezionato);
        this.ateneo = ateneoselezionato;
        return corsoDAO.getDisciplineByAteneo(comune, ateneo);
    }

    public List<String> getDurate(String disciplinaselezionata) {
        Objects.requireNonNull(disciplinaselezionata);
        this.disciplina = disciplinaselezionata;
        return corsoDAO.getDurateByDisciplina(disciplina);
    }

    public List<String> getCorsi(String durataselezionata) {
        Objects.requireNonNull(durataselezionata);
        this.durata = durataselezionata;
        return corsoDAO.getCorsiByTipologia(comune, ateneo, disciplina, durata);
    }

    public List<String> getRisultati(String classeselezionata) {
        Objects.requireNonNull(classeselezionata);
        this.classe=classeselezionata;
        return corsoDAO.getRisultatiByCorsi(comune, ateneo, disciplina, durata, classe);
    }

    public List<String> getCurriculumPerCorso(String corsoSelezionato) {
        Objects.requireNonNull(corsoSelezionato);
        this.corso = corsoSelezionato;
        return corsoDAO.getCurriculum(corso, ateneo);
    }

    public void setCurriculum(String curriculumSelezionato) {
        this.curriculum = curriculumSelezionato;
    }

    public void setCorsoUtente() {
        Corso corsoUtente = new Corso(regione, provincia, comune, ateneo, disciplina, durata, classe, corso);
        corsoUtente.setCurriculum(curriculum);
        SessioneControllerSingleton p = SessioneControllerSingleton.getInstance();
        Utente u = p.getCurrentUser();
        if (u==null){
            throw new UtenteNonTrovatoException("Nessun utente in sessione");
        }
        if(u instanceof UtenteIscritto ui) {
            ui.setCorso(corsoUtente);
            utenteDAO.aggiungiCorsoUtente(ui, corsoUtente);
            p.setCurrentUser(ui);
            return;
        }
        throw new IllegalStateException("L'utente corrente non è iscritto");
    }

    public void recensisciInsegnamento(UtenteBean utenteBean, InsegnamentoBean insegnamentoBean, String testo, Integer valutazione) {
        Insegnamento insegnamento = new Insegnamento(insegnamentoBean.getNome(), insegnamentoBean.getAnno(), insegnamentoBean.getSemestre(), insegnamentoBean.getCurriculum(), insegnamentoBean.getCfu());
        Recensione recensione=new Recensione(testo, valutazione, utenteBean.getUsername(), insegnamento);
        try{
            recensioneDAO.setRecensione(recensione);
        }catch (RecensioneNonSalvataException e){
            throw new RecensioneNonSalvataException("Recensione non salvata");
        }
    }

    public void logOut(){
        SessioneControllerSingleton p = SessioneControllerSingleton.getInstance();
        p.logout();
    }


}
