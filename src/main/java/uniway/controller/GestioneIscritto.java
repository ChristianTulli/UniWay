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

    private String regione;
    private String provincia;
    private String comune;
    private String ateneo;
    private String disciplina;
    private String tipologia;
    private String classe;


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
}
