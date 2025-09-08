
package uniway.beans;


public class UtenteBean {
    private String username;
    private String password;
    private boolean iscritto;
    private boolean corsoImpostato;

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

    public String getUsername() {

        return username;
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


}