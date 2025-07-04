package uniway.controller;

import uniway.persistenza.AteneoDAO;
import uniway.persistenza.CorsoDAO;
import java.util.ArrayList;
import java.util.List;

public class InCercaTrovaCorsoController {

    private CorsoDAO corsoDAO;
    private AteneoDAO ateneoDAO;

    private String statale;
    private String tipologia;
    private String regione;
    private String provincia;
    private String comune;
    private String durata;
    private String gruppoDisciplina;
    private String classeCorso;

    public InCercaTrovaCorsoController() {
            corsoDAO = new CorsoDAO();
            ateneoDAO = new AteneoDAO();
    }

    // 🔵 COLONNA 1: TIPOLOGIA ATENEO
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

    // 🔵 COLONNA 2: UBICAZIONE
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

    // 🔵 COLONNA 3: CARATTERISTICHE CORSO
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

    // 🔵 CERCA RISULTATI
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

}