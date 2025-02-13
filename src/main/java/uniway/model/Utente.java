
package uniway.model;


public class Utente {
    private String username;
    private String password;
    private boolean iscritto;
    private int id;

    public Utente(String username, String password, boolean iscritto) {
        this.username = username;
        this.password = password;
        this.iscritto = iscritto;

    }

    public Utente(int id, String username, String password, boolean iscritto) {
        this.id = id;
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

    public int getId() {
        return id;
    }
}

