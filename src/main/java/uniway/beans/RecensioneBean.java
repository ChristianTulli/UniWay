package uniway.beans;

public class RecensioneBean {
    private String commento;
    private String nome;
    private Integer valutazione;
    private String data;
    private Integer idInsegnamento;

    public RecensioneBean() {

    }
    public RecensioneBean(String commento, String nome, Integer valutazione, String data) {
        this.commento = commento;
        this.nome = nome;
        this.valutazione = valutazione;
        this.data = data;
    }
    public String getCommento() {
        return commento;
    }
    public void setCommento(String commento) {
        this.commento = commento;
    }
    public String getNome() {
        return nome;
    }
    public void setNome(String nome) {
        this.nome = nome;
    }
    public Integer getValutazione() {
        return valutazione;
    }
    public void setValutazione(Integer valutazione) {
        this.valutazione = valutazione;
    }
    public String getData() {
        return data;
    }
    public void setData(String data) {
        this.data = data;
    }
    public Integer getIdInsegnamento() {
        return idInsegnamento;
    }
    public void setIdInsegnamento(Integer idInsegnamento) {
        this.idInsegnamento = idInsegnamento;
    }

}
