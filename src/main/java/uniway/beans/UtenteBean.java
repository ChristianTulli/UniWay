
package uniway.beans;


import uniway.model.Corso;

public class UtenteBean {
    private String username;
    private String password;
    private boolean iscritto;
    private Corso corso;
    private String curriculum;

    public UtenteBean(String username, String password, boolean iscritto) {
        this.username = username;
        this.password = password;
        this.iscritto = iscritto;
    }

    public UtenteBean(String username, String password, boolean iscritto, Corso corso, String curriculum) {
        this.username = username;
        this.password = password;
        this.iscritto = iscritto;
        this.corso = corso;
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

    public void setCorso(Corso corso) {
        this.corso =corso;
    }

    public Corso getCorso() {
        return corso;
    }

    public void setCurriculum(String curriculum) {
        this.curriculum = curriculum;
    }
    public String getCurriculum() {
        return curriculum;
    }

}