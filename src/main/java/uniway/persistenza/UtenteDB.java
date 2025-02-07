package uniway.persistenza;

import uniway.model.Utente;
import uniway.model.UtenteInCerca;
import uniway.model.UtenteIscritto;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UtenteDB implements UtenteDAO {
    private final String url;
    private final String username;
    private final String password;

    public UtenteDB(String url, String username, String password) {
        this.url = url;
        this.username = username;
        this.password = password;
    }

    @Override
    public void salvaUtente(Utente utente) throws IOException {
        String query;
        query = "INSERT INTO Utenti (username, password, iscritto) VALUES (?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(url, username, password);
             PreparedStatement stmt = conn.prepareStatement(query)){

            stmt.setString(1, utente.getUsername());
            stmt.setString(2, utente.getPassword());
            stmt.setBoolean(3, utente.getIscritto());
        } catch (SQLException e) {
            throw new IOException("Errore durante il la registrazione dell'utente", e);
        }
    }

    @Override
    public List<Utente> ottieniUtenti() throws IOException {
        String query = "SELECT id, username, password, iscritto, id_corso, preferenze FROM utenti";

        List<Utente> utenti = new ArrayList<>();
        try (Connection conn = DriverManager.getConnection(url, username, password);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                int id = rs.getInt("id");
                String utenteUsername = rs.getString("username");
                String utentePassword = rs.getString("password");
                boolean iscritto = rs.getBoolean("iscritto");
                Integer idCorso = rs.getObject("id_corso", Integer.class); // può essere NULL
                List<String> preferenze = new ArrayList<>();

                if (!iscritto) {
                    String prefStr = rs.getString("preferenze");
                    if (prefStr != null && !prefStr.isEmpty()) {
                        preferenze = List.of(prefStr.split(",")); // Converte la stringa in lista
                    }
                }

                // Creazione dell'oggetto corretto
                if (iscritto) {
                    utenti.add(new UtenteIscritto(id, utenteUsername, utentePassword, iscritto, idCorso));
                } else {
                    utenti.add(new UtenteInCerca(id, utenteUsername, utentePassword, iscritto, preferenze));
                }
            }
        } catch (SQLException e) {
            throw new IOException("Errore durante il recupero degli utenti", e);
        }
        return utenti;
    }
}