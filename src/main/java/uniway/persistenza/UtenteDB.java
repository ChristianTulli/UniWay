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

        throw new IOException("Not supported yet.");
    }

    @Override
    public List<Utente> ottieniUtenti() throws IOException {
        String query = "SELECT id, username, password, is_iscritto, id_corso, preferenze FROM Utente";

        List<Utente> utenti = new ArrayList<>();
        try (Connection conn = DriverManager.getConnection(url, username, password);
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                int id = rs.getInt("id");
                String username = rs.getString("username");
                String password = rs.getString("password");
                boolean isIscritto = rs.getBoolean("is_iscritto");
                Integer idCorso = rs.getObject("id_corso", Integer.class); // può essere NULL
                List<String> preferenze = new ArrayList<>();

                if (!isIscritto) {
                    String prefStr = rs.getString("preferenze");
                    if (prefStr != null && !prefStr.isEmpty()) {
                        preferenze = List.of(prefStr.split(",")); // Converte la stringa in lista
                    }
                }

                // Creazione dell'oggetto corretto
                if (isIscritto) {
                    utenti.add(new UtenteIscritto(id, username, password, idCorso));
                } else {
                    utenti.add(new UtenteInCerca(id, username, password, preferenze));
                }
            }
        } catch (SQLException e) {
            throw new IOException("Errore durante il recupero degli utenti", e);
        }
        return utenti;
    }
}