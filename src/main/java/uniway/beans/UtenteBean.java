
package uniway.beans;


public class UtenteBean {
    private String username;
    private String password;
    private Boolean iscritto;

    public UtenteBean(String username, String password, Boolean tipo) {
        this.username = username;
        this.password = password;
        this.iscritto = tipo;
    }

    public UtenteBean(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {

        return username;
    }

    public String getPassword() {

        return password;
    }
    public Boolean getIscritto() {
        return iscritto;
    }

}