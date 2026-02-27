package uniway.model;

public class Corso {
    private String nomeClasse;
    private String nomeCorso;
    private String disciplina;
    private String durata;
    private String sedeProvincia;
    private String sedeComune;
    private String sedeRegione;

    public Corso(String nomeClasse, String nomeCorso, String disciplina, String durata, String sedeProvincia, String sedeComune, String sedeRegione) {
        this.nomeClasse = nomeClasse;
        this.nomeCorso = nomeCorso;
        this.disciplina = disciplina;
        this.durata = durata;
        this.sedeProvincia = sedeProvincia;
        this.sedeComune = sedeComune;
        this.sedeRegione = sedeRegione;
    }
    public String getNomeClasse() {
        return nomeClasse;
    }
    public void setNomeClasse(String nomeClasse) {
        this.nomeClasse = nomeClasse;
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
    public String getSedeProvincia() {
        return sedeProvincia;
    }
    public void setSedeProvincia(String sedeProvincia) {
        this.sedeProvincia = sedeProvincia;
    }
    public String getSedeComune() {
        return sedeComune;
    }
    public void setSedeComune(String sedeComune) {
        this.sedeComune = sedeComune;
    }
    public String getSedeRegione() {
        return sedeRegione;
    }
    public void setSedeRegione(String sedeRegione) {
        this.sedeRegione = sedeRegione;
    }


}
