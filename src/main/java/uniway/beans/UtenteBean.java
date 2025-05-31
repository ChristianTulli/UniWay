
package uniway.beans;


public class UtenteBean {
    private String username;
    private String password;
    private boolean iscritto;
    private Integer idCorso;

    public UtenteBean(String username, String password, boolean iscritto) {
        this.username = username;
        this.password = password;
        this.iscritto = iscritto;
    }

    public UtenteBean(String username, String password, boolean iscritto, Integer idCorso) {
        this.username = username;
        this.password = password;
        this.iscritto = iscritto;
        this.idCorso = idCorso;
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

}