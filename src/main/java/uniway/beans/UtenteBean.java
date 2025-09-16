
package uniway.beans;


public class UtenteBean {
    private String username;
    private String password;
    private boolean iscritto;
    private boolean corsoImpostato;
    private String curriculum;
    private String nomeCorso;
    private String nomeAteneo;

    public UtenteBean() {}
    public UtenteBean(String username, String password, boolean iscritto) {
        this.username = username;
        this.password = password;
        this.iscritto = iscritto;
    }

    public UtenteBean(String username, String password, boolean iscritto, boolean corsoImpostato) {
        this.username = username;
        this.password = password;
        this.iscritto = iscritto;
        this.corsoImpostato = corsoImpostato;
    }

    public void setUsername(String username) {
        this.username = username;
    }
    public String getUsername() {

        return username;
    }
    public void setPassword(String password) {
        this.password = password;
    }

    public String getPassword() {

        return password;
    }

    public boolean getIscritto() {

        return iscritto;
    }

    public void setCorso(boolean corsoImpostato) {
        this.corsoImpostato =corsoImpostato;
    }

    public boolean getCorso() {
        return corsoImpostato;
    }
    public String getCurriculum() {
        return curriculum;
    }
    public void setCurriculum(String curriculum) {
        this.curriculum = curriculum;
    }
    public String getNomeCorso() {
        return nomeCorso;
    }
    public void setNomeCorso(String nomeCorso) {
        this.nomeCorso = nomeCorso;
    }
    public String getNomeAteneo() {
        return nomeAteneo;
    }
    public void setNomeAteneo(String nomeAteneo) {
        this.nomeAteneo = nomeAteneo;
    }

}