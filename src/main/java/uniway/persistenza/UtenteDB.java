package uniway.persistenza;

import uniway.model.Utente;
import uniway.model.UtenteInCerca;
import uniway.model.UtenteIscritto;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

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
        query = "INSERT INTO utenti (username, password, iscritto) VALUES (?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(url, username, password);
             PreparedStatement stmt = conn.prepareStatement(query)){

            stmt.setString(1, utente.getUsername());
            stmt.setString(2, utente.getPassword());
            stmt.setBoolean(3, utente.getIscritto());

            int rowsAffected = stmt.executeUpdate(); // Eseguiamo la query

            if (rowsAffected == 0) {
                throw new IOException("Nessun utente è stato inserito nel database.");
            }

        } catch (SQLException e) {
            throw new IOException("Errore durante la registrazione dell'utente", e);
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
                List<Integer> preferenze = new ArrayList<>();

                if (!iscritto) {
                    String prefStr = rs.getString("preferenze");
                    if (prefStr != null && !prefStr.isEmpty()) {
                        preferenze = Arrays.stream(prefStr.split(","))
                                .map(Integer::parseInt) // Converte ogni elemento in Integer
                                .collect(Collectors.toList()); // Converte in lista mutabile
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

    @Override
    public void aggiungiCorsoUtente(String usernameUtente, int idCorso) throws IOException {
        String query = "UPDATE utenti SET id_corso = ? WHERE username = ?";

        try (Connection conn = DriverManager.getConnection(url, username, password);
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setObject(1, idCorso, Types.INTEGER);
            stmt.setString(2, usernameUtente);
            stmt.executeUpdate();
        }catch (SQLException e) {
            throw new IOException("Errore durante il recupero degli utenti", e);
        }

    }

    @Override
    public void aggiungiPreferitiUtente(String usernameUtente, int idCorso) throws IOException {
        String querySelect = "SELECT preferenze FROM utenti WHERE username = ?";
        String queryUpdate = "UPDATE utenti SET preferenze = ? WHERE username = ?";

        try (Connection conn = DriverManager.getConnection(url, username, password);
             PreparedStatement stmtSelect = conn.prepareStatement(querySelect);
             PreparedStatement stmtUpdate = conn.prepareStatement(queryUpdate)) {

            stmtSelect.setString(1, usernameUtente);
            ResultSet rs = stmtSelect.executeQuery();

            String nuovaLista = String.valueOf(idCorso);
            if (rs.next() && rs.getString("preferenze") != null) {
                String preferenzeAttuali = rs.getString("preferenze");
                List<String> listaPreferiti = new ArrayList<>(List.of(preferenzeAttuali.split(",")));

                if (!listaPreferiti.contains(nuovaLista)) {
                    listaPreferiti.add(nuovaLista);
                }
                nuovaLista = String.join(",", listaPreferiti);
            }

            stmtUpdate.setString(1, nuovaLista);
            stmtUpdate.setString(2, usernameUtente);
            stmtUpdate.executeUpdate();
        } catch (SQLException e) {
            throw new IOException("Errore durante l'aggiornamento dei preferiti", e);
        }
    }

}
