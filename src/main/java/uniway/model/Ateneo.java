package uniway.model;

public class Ateneo {
    private String nome;
    private String regione;
    private String provincia;
    private String sito;

    public Ateneo(String nome, String regione, String provincia, String sito) {
        this.nome = nome;
        this.regione = regione;
        this.provincia = provincia;
        this.sito = sito;
    }

    public String getNome() {
        return nome;
    }
    public void setNome(String nome) {
        this.nome = nome;
    }
    public String getRegione() {
        return regione;
    }
    public void setRegione(String regione) {
        this.regione = regione;
    }
    public String getProvincia() {
        return provincia;
    }
    public void setProvincia(String provincia) {
        this.provincia = provincia;
    }
    public String getSito() {
        return sito;
    }
    public void setSito(String sito) {
        this.sito = sito;
    }

}
