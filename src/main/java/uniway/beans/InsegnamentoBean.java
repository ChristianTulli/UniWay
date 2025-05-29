package uniway.beans;

public class InsegnamentoBean {
    private String nome;
    private Integer anno;
    private Integer semestre;
    private String curriculum;
    private Integer cfu;

    public void setNome(String nome) {
        this.nome = nome;
    }


    public void setAnno(Integer anno) {
        this.anno = anno;
    }


    public void setSemestre(Integer semestre) {
        this.semestre = semestre;
    }


    public void setCurriculum(String curriculum) {
        this.curriculum = curriculum;
    }

    public void setCfu(Integer cfu) {
        this.cfu = cfu;
    }

    public String getNome() {
        return nome;
    }

    public int getAnno() {
        return anno;
    }

    public Integer getSemestre() {
        return semestre;
    }

    public int getCfu() {
        return cfu;
    }

    public String getCurriculum() {
        return curriculum;
    }
}

