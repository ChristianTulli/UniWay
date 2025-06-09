package uniway.model;

public class Recensione {
    private Integer id;
    private String commento;
    private Integer valutazione;
    private String nomeUtente;
    private Integer idInsegnamento;

    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
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
    public Integer getIdInsegnamento() {
        return idInsegnamento;
    }
    public void setIdInsegnamento(Integer idInsegnamento) {
        this.idInsegnamento = idInsegnamento;
    }
}
