package uniway.controller;

import uniway.beans.UtenteBean;
import uniway.model.UtenteIscritto;
import uniway.persistenza.CorsoDAO;
import uniway.persistenza.InsegnamentoDAO;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class GestioneIscritto {

    private static final Logger LOGGER = Logger.getLogger(GestioneIscritto.class.getName());
    private CorsoDAO corsoDAO;
    private String errore = "errore";

    private String regione;
    private String provincia;
    private String comune;
    private String ateneo;
    private String disciplina;
    private String tipologia;
    private String classe;
    private String corso;

    private final GestioneLogin gestioneLogin = GestioneLogin.getInstance(); // Otteniamo il Singleton


    public GestioneIscritto() throws IllegalArgumentException {
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

        if (gestioneLogin.isFullMode()) { // Ora possiamo accedere a isFullMode
            try {
                gestioneLogin.getUtenteDAO().aggiungiCorsoUtente(utenteBean.getUsername(), idCorso);
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Errore durante l'inserimento del corso", e);
            }
        } else {
            gestioneLogin.getUtenti().stream()
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
        this.regione = regioneselezionata;
        return corsoDAO.getProvinceByRegione(regione);
    }

    public List<String> getComuni(String provinciaselezionata) {
        this.provincia = provinciaselezionata;
        return corsoDAO.getComuniByProvincia(provincia);
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
        this.classe = classeselezionata;
        return corsoDAO.getRisultatiByCorsi(comune, ateneo, disciplina, tipologia, classe);
    }

    public List<String> getCurriculumPerCorso(String idCorso) {
        return corsoDAO.getCurriculum(idCorso);
    }

    public void setCurriculumUtente(UtenteBean utente, String curriculum) {
        utente.setCurriculum(curriculum);
    }

}
