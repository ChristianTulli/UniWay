
package uniway.beans;


public class UtenteBean {
    private String username;
    private String password;
    private boolean iscritto;
    private Integer idCorso;
    private String curriculum;

    public UtenteBean(String username, String password, boolean iscritto) {
        this.username = username;
        this.password = password;
        this.iscritto = iscritto;
    }

    public UtenteBean(String username, String password, boolean iscritto, Integer idCorso, String curriculum) {
        this.username = username;
        this.password = password;
        this.iscritto = iscritto;
        this.idCorso = idCorso;
        this.curriculum = curriculum;
    }

    public String getUsername() {

        return username;
    }

    public String getPassword() {

        return password;
    }

    public boolean getIscritto() {

        return iscritto;
    }

    public void setIdCorso(Integer idCorso) {
        this.idCorso = idCorso;
    }

    public Integer getIdCorso() {
        return idCorso;
    }

    public void setCurriculum(String curriculum) {
        this.curriculum = curriculum;
    }
    public String getCurriculum() {
        return curriculum;
    }

}