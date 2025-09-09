package uniway.controller;

import uniway.beans.InsegnamentoBean;
import uniway.beans.UtenteBean;
import uniway.eccezioni.UtenteNonTrovatoException;
import uniway.model.Corso;
import uniway.model.Utente;
import uniway.model.UtenteIscritto;
import uniway.persistenza.CorsoDAO;
import uniway.persistenza.InsegnamentoDAO;
import uniway.persistenza.RecensioneDAO;
import uniway.persistenza.UtenteDAO;

import java.util.List;
import java.util.Objects;
import java.util.logging.Logger;

public class CommentaEValutaInsegnamentoController {

    private static final Logger LOGGER = Logger.getLogger(CommentaEValutaInsegnamentoController.class.getName());
    private final CorsoDAO corsoDAO;
    private final InsegnamentoDAO insegnamentoDAO;
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
    private Corso corsoUtente;


    public CommentaEValutaInsegnamentoController() {
        PersistenzaController persistenzaController = PersistenzaController.getInstance();
        corsoDAO=persistenzaController.getCorsoDAO();
        insegnamentoDAO=persistenzaController.getInsegnamentoDAO();
        utenteDAO=persistenzaController.getUtenteDAO();
        recensioneDAO=persistenzaController.getRecensioneDAO();
    }

    public UtenteBean getUtenteBean(){
        //scrivere logica
    }

    public List<InsegnamentoBean> getInsegnamentiBean(){
        //scrivere logica
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
        this.corsoUtente = new Corso(regione, provincia, comune, ateneo, disciplina, durata, classe, corso);
        this.corsoUtente.setCurriculum(curriculum);
        PersistenzaController p = PersistenzaController.getInstance();
        Utente u = p.getCurrentUser();
        if (u==null){
            throw new UtenteNonTrovatoException("Nessun utente in sessione");
        }
        if(u instanceof UtenteIscritto ui) {
            ui.setCorso(corsoUtente);
            utenteDAO.aggiungiCorsoUtente(ui, corsoUtente);
            p.setCurrentUser(ui);
        }
        throw new IllegalStateException("L'utente corrente non è iscritto");
    }

    public void logOut(){
        PersistenzaController p = PersistenzaController.getInstance();
        p.logout();
    }


}
