
package uniway.model;


public class Utente {
    private String username;
    private String password;
    private boolean iscritto;

    public Utente(String username, String password, boolean iscritto) {
        this.username = username;
        this.password = password;
        this.iscritto = iscritto;

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
}

