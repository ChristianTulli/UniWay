
package uniway.beans;


public class UtenteBean {
    private String username;
    private String password;

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
}