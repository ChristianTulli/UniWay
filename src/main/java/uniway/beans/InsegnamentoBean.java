package uniway.beans;

public class InsegnamentoBean {
    private String nome;
    private Integer anno;
    private Integer semestre;
    private String curriculum;
    private Integer cfu;

    public InsegnamentoBean() {
    }

    public InsegnamentoBean(String nome, Integer anno, Integer semestre, String curriculum, Integer cfu) {
        this.nome = nome;
        this.anno = anno;
        this.semestre = semestre;
        this.curriculum = curriculum;
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

    public String getCurriculum() {
        return curriculum;
    }

    public void setCurriculum(String curriculum) {
        this.curriculum = curriculum;
    }

    public Integer getCfu() {
        return cfu;
    }

    public void setCfu(Integer cfu) {
        this.cfu = cfu;
    }
}

