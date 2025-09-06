
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

    public boolean getIscritto() {

        return iscritto;
    }

    public int getId() {
        return id;
    }

    public void setId(int andIncrement) {
        this.id = andIncrement;
    }
}

