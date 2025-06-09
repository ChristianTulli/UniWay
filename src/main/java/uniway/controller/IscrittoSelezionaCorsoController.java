package uniway.controller;

import uniway.beans.UtenteBean;
import uniway.model.UtenteIscritto;
import uniway.persistenza.CorsoDAO;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class IscrittoSelezionaCorsoController {

    private static final Logger LOGGER = Logger.getLogger(IscrittoSelezionaCorsoController.class.getName());
    private CorsoDAO corsoDAO;
    private String errore = "errore";

    private String comune;
    private String ateneo;
    private String disciplina;
    private String tipologia;
    private String corso;

    private final LogInController loginController = LogInController.getInstance(); // Otteniamo il Singleton


    public IscrittoSelezionaCorsoController() throws IllegalArgumentException {
        Properties properties = new Properties();
        try (FileInputStream input = new FileInputStream("src/main/resources/config.properties")) {
            properties.load(input);
            corsoDAO = new CorsoDAO(properties.getProperty("db.url"), properties.getProperty("db.username"), properties.getProperty("db.password"));

        } catch (FileNotFoundException e) {
            throw new IllegalArgumentException("File config.properties non trovato", e);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, errore, e);
        }
    }

    public void setCorsoUtente(UtenteBean utenteBean, String corsoSelezionato) {
        this.corso = corsoSelezionato;
        Integer idCorso = corsoDAO.getIdCorsoByNome(comune, ateneo, tipologia, corso);
        utenteBean.setIdCorso(idCorso);

        if (loginController.isFullMode()) { // Ora possiamo accedere a isFullMode
            try {
                loginController.getUtenteDAO().aggiungiCorsoUtente(utenteBean.getUsername(), idCorso);
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Errore durante l'inserimento del corso", e);
            }
            loginController.getUtenti().stream()
                    .filter(u -> u instanceof UtenteIscritto && u.getUsername().equals(utenteBean.getUsername()))
                    .map(u -> (UtenteIscritto) u)
                    .findFirst()
                    .ifPresent(u -> u.setIdCorso(idCorso));
        } else {
            loginController.getUtenti().stream()
                    .filter(u -> u instanceof UtenteIscritto && u.getUsername().equals(utenteBean.getUsername()))
                    .map(u -> (UtenteIscritto) u)
                    .findFirst()
                    .ifPresent(u -> u.setIdCorso(idCorso));
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

        if (loginController.isFullMode()) {
            try {
                loginController.getUtenteDAO().aggiungiCurriculumUtente(utente.getUsername(), curriculum);
            } catch (IOException e) {
                LOGGER.log(Level.SEVERE, "Errore durante l'inserimento del curriculum", e);
            }
            loginController.getUtenti().stream()
                    .filter(u -> u instanceof UtenteIscritto && u.getUsername().equals(utente.getUsername()))
                    .map(u -> (UtenteIscritto) u)
                    .findFirst()
                    .ifPresent(u -> u.setCurriculum(curriculum));
        } else {
            loginController.getUtenti().stream()
                    .filter(u -> u instanceof UtenteIscritto && u.getUsername().equals(utente.getUsername()))
                    .map(u -> (UtenteIscritto) u)
                    .findFirst()
                    .ifPresent(u -> u.setCurriculum(curriculum));
        }
    }


}
