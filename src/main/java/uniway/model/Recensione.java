package uniway.model;

public class Recensione {
    private String commento;
    private Integer valutazione;
    private String nomeUtente;
    private Insegnamento insegnamento;

    public Recensione(){}

    public Recensione(String testo, Integer valutazione, String username, Insegnamento insegnamento) {
        this.commento = testo;
        this.valutazione = valutazione;
        this.nomeUtente = username;
        this.insegnamento = insegnamento;
    }

    public String getCommento() {
        return commento;
    }
    public void setCommento(String commento) {
        this.commento = commento;
    }
    public Integer getValutazione() {
        return valutazione;
    }
    public void setValutazione(Integer valutazione) {
        this.valutazione = valutazione;
    }
    public String getNomeUtente() {
        return nomeUtente;
    }
    public void setNomeUtente(String nomeUtente) {
        this.nomeUtente = nomeUtente;
    }
    public Insegnamento getInsegnamento() {
        return insegnamento;
    }
    public void setInsegnamento(Insegnamento insegnamento) {
        this.insegnamento = insegnamento;
    }
}
