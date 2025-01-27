
package uniway.model;

public class Utente {
    private final String username;
    private final String password;

    public Utente(String username, String password) {
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

