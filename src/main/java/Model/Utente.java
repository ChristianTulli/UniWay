package Model;
//possibile usare pattern singleton? per avere una sola istanza di utente attivo per volta
public class Utente {
    private String username;
    private String password;
    private String email;

    public Utente(String username, String password, String email) {
        this.username = username;
        this.password = password;
        this.email = email;
    }
}
