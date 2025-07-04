package uniway.controller;

import uniway.beans.UtenteBean;
import uniway.persistenza.CorsoDAO;

import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class IscrittoSelezionaCorsoController {

    private static final Logger LOGGER = Logger.getLogger(IscrittoSelezionaCorsoController.class.getName());
    private final PersistenzaController persistenzaController = PersistenzaController.getInstance(); // Otteniamo il Singleton
    private CorsoDAO corsoDAO;
    private String comune;
    private String ateneo;
    private String disciplina;
    private String tipologia;
    private String corso;


    public IscrittoSelezionaCorsoController() throws IllegalArgumentException {
        corsoDAO = new CorsoDAO();
    }

    public void setCorsoUtente(UtenteBean utenteBean, String corsoSelezionato) {
        this.corso = corsoSelezionato;
        Integer idCorso = corsoDAO.getIdCorsoByNome(comune, ateneo, tipologia, corso);
        utenteBean.setIdCorso(idCorso);
        try {
            persistenzaController.getUtenteDAO().aggiungiCorsoUtente(utenteBean.getUsername(), idCorso);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Errore durante l'inserimento del corso", e);
        }
    }


    public List<String> getRegioni() {
        return corsoDAO.getAllRegioni();
    }

    public List<String> getProvince(String regioneselezionata) {
        return corsoDAO.getProvinceByRegione(regioneselezionata);
    }

    public List<String> getComuni(String provinciaselezionata) {
        return corsoDAO.getComuniByProvincia(provinciaselezionata);
    }

    public List<String> getAtenei(String comuneselezionata) {
        this.comune = comuneselezionata;
        return corsoDAO.getAteneiByComune(comune);
    }

    public List<String> getDiscipline(String ateneoselezionata) {
        this.ateneo = ateneoselezionata;
        return corsoDAO.getDisciplineByAteneo(comune, ateneo);
    }

    public List<String> getTipologie(String disciplinaselezionata) {
        this.disciplina = disciplinaselezionata;
        return corsoDAO.getTipologieByDisciplina(disciplina);
    }

    public List<String> getCorsi(String tipologiaselezionata) {
        this.tipologia = tipologiaselezionata;
        return corsoDAO.getCorsiByTipologia(comune, ateneo, disciplina, tipologia);
    }

    public List<String> getRisultati(String classeselezionata) {
        return corsoDAO.getRisultatiByCorsi(comune, ateneo, disciplina, tipologia, classeselezionata);
    }

    public List<String> getCurriculumPerCorso(String corsoSelezionato) {
        this.corso = corsoSelezionato;
        Integer idCorso = corsoDAO.getIdCorsoByNome(comune, ateneo, tipologia, corso);
        return corsoDAO.getCurriculum(idCorso);
    }

    public void setCurriculumUtente(UtenteBean utente, String curriculum) {
        utente.setCurriculum(curriculum);
        try {
            persistenzaController.getUtenteDAO().aggiungiCurriculumUtente(utente.getUsername(), curriculum);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Errore durante l'inserimento del curriculum", e);
        }
    }


}
