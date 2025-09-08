package uniway.model;

import java.util.List;

public class Corso {
    private String regione;
    private String provincia;
    private String comune;
    private String ateneo;
    private String disciplina;
    private String durata;
    private String classe;
    private String nomeCorso;
    private String curriculum;
    private List<Insegnamento> insegnamenti;


    public Corso(String regione, String provincia, String comune, String ateneo, String disciplina, String durata, String classe, String nomeCorso) {
        this.regione = regione;
        this.provincia = provincia;
        this.comune = comune;
        this.ateneo = ateneo;
        this.disciplina = disciplina;
        this.durata = durata;
        this.classe = classe;
        this.nomeCorso = nomeCorso;
    }


    public String getClasse() {
        return classe;
    }
    public void setClasse(String classe) {
        this.classe = classe;
    }
    public String getNomeCorso() {
        return nomeCorso;
    }
    public void setNomeCorso(String nomeCorso) {
        this.nomeCorso = nomeCorso;
    }
    public String getDisciplina() {
        return disciplina;
    }
    public void setDisciplina(String disciplina) {
        this.disciplina = disciplina;
    }
    public String getDurata() {
        return durata;
    }
    public void setDurata(String durata) {
        this.durata = durata;
    }
    public String getProvincia() {
        return provincia;
    }
    public void setProvincia(String provincia) {
        this.provincia = provincia;
    }
    public String getComune() {
        return comune;
    }
    public void setComune(String comune) {
        this.comune = comune;
    }
    public String getRegione() {
        return regione;
    }
    public void setRegione(String regione) {
        this.regione = regione;
    }
    public String getCurriculum() {
        return curriculum;
    }
    public void setCurriculum(String curriculum) {
        this.curriculum = curriculum;
    }
    public String getAteneo() {
        return ateneo;
    }
    public void setAteneo(String ateneo) {
        this.ateneo = ateneo;
    }
    public List<Insegnamento> getInsegnamenti() {
        return insegnamenti;
    }
    public void setInsegnamenti(List<Insegnamento> insegnamenti) {
        this.insegnamenti = insegnamenti;
    }



}
