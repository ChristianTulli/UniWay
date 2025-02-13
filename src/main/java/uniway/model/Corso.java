package uniway.model;

public class Corso {
    private String nomeClasse;
    private String nomeCorso;
    private String disciplina;
    private String durata;
    private String sedeProvincia;
    private String sedeComune;
    private String sedeRegione;

    public Corso(String nomeClasse, String nomeCorso, String disciplina, String durata, String sedeCorso, String sedeRegione, String sedeProvincia, String sedeComune) {
        this.nomeClasse = nomeClasse;
        this.nomeCorso = nomeCorso;
        this.disciplina = disciplina;
        this.durata = durata;
        this.sedeProvincia = sedeProvincia;
        this.sedeComune = sedeComune;
        this.sedeRegione = sedeRegione;
    }

}
