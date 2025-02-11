package uniway.controller;

import uniway.persistenza.AteneoDAO;
import uniway.persistenza.CorsoDAO;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class GestioneIscritto {

    private static final Logger LOGGER = Logger.getLogger(GestioneIscritto.class.getName());
    private  AteneoDAO ateneoDAO;
    private CorsoDAO corsoDAO;
    private String errore="errore";

    public GestioneIscritto() throws IllegalArgumentException {
        Properties properties = new Properties();
        try (FileInputStream input=new FileInputStream("src/main/resources/config.properties")) {
            properties.load(input);
            ateneoDAO = new AteneoDAO(properties.getProperty("db.url"), properties.getProperty("db.username"), properties.getProperty("db.password"));
            corsoDAO = new CorsoDAO(properties.getProperty("db.url"), properties.getProperty("db.username"), properties.getProperty("db.password"));

        }catch (FileNotFoundException e){
            throw new IllegalArgumentException("File config.properties non trovato", e);
        }catch (Exception e){
            LOGGER.log(Level.SEVERE, errore, e);
        }
    }

    public List<String> getRegioni() {
        return ateneoDAO.getAllRegioni();
    }

    public List<String> getProvince(String regione) {
        return corsoDAO.getProvinceByRegione(regione);
    }

    public List<String> getComuni(String provincia) {
        return corsoDAO.getComuniByProvincia(provincia);
    }

    public List<String> getAtenei(String comune) {
        return ateneoDAO.getAteneiByComune(comune);
    }

    public List<String> getDiscipline(String ateneo) {
        return corsoDAO.getDisciplineByAteneo(ateneo);
    }

    public List<String> getTipologie(String disciplina) {
        return corsoDAO.getTipologieByDisciplina(disciplina);
    }

    public List<String> getCorsi(String tipologia) {
        return corsoDAO.getCorsiByTipologia(tipologia);
    }

    public List<String> getRisultati(String corsi) {
        return corsoDAO.getRisultatiByCorsi(corsi);
    }
}

