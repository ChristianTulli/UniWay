
package uniway.beans;


public class UtenteBean {
    private String username;
    private String password;
    private boolean iscritto;

    public UtenteBean(String username, String password, boolean iscritto) {
        this.username = username;
        this.password = password;
        this.iscritto = iscritto;
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
    public boolean getIscritto() {

        return iscritto;
    }
    public void setIscritto(boolean iscritto) {
        this.iscritto = iscritto;
    }

}