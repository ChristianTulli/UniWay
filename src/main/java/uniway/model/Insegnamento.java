package uniway.model;

public class Insegnamento {
    private String nome;
    private Integer anno;
    private Integer semestre;
    private Integer cfu;

    public Insegnamento() {}

    public Insegnamento(String nome, Integer anno, Integer semestre, Integer cfu) {
        this.nome = nome;
        this.anno = anno;
        this.semestre = semestre;
        this.cfu = cfu;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public Integer getAnno() {
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

    public Integer getCfu() {
        return cfu;
    }

    public void setCfu(Integer cfu) {
        this.cfu = cfu;
    }
}
