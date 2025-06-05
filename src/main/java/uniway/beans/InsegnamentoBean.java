package uniway.beans;

public class InsegnamentoBean {
    private String nome;
    private Integer anno;
    private Integer semestre;
    private String curriculum;
    private Integer cfu;
    private Integer valutazione;

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public int getAnno() {
        return anno;
    }

    public void setAnno(Integer anno) {
        this.anno = anno;
    }

    public Integer getSemestre() {
        return semestre;
    }

    public void setSemestre(Integer semestre) {
        this.semestre = semestre;
    }

    public int getCfu() {
        return cfu;
    }

    public void setCfu(Integer cfu) {
        this.cfu = cfu;
    }

    public String getCurriculum() {
        return curriculum;
    }

    public void setCurriculum(String curriculum) {
        this.curriculum = curriculum;
    }

    public Integer getValutazione() {
        return valutazione;
    }
    public void setValutazione(Integer valutazione) {
        this.valutazione = valutazione;
    }
}

