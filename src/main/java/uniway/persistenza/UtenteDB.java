package uniway.persistenza;

import uniway.model.Utente;
import uniway.model.UtenteInCerca;
import uniway.model.UtenteIscritto;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;

public class UtenteDB implements UtenteDAO {
    private final String url;
    private final String username;
    private final String password;
    private String colonnaPreferenze= "preferenze";

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
        String query = "SELECT id, username, password, iscritto, id_corso, preferenze, curriculum FROM utenti";

        List<Utente> utenti = new ArrayList<>();
        try (Connection conn = DriverManager.getConnection(url, username, password);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                int id = rs.getInt("id");
                String utenteUsername = rs.getString("username");
                String utentePassword = rs.getString("password");
                boolean iscritto = rs.getBoolean("iscritto");
                Integer idCorso = rs.getObject("id_corso", Integer.class);// può essere NULL
                String curriculum = rs.getString("curriculum");
                List<Integer> preferenze = new ArrayList<>();

                if (!iscritto) {
                    String prefStr = rs.getString("preferenze");
                    if (prefStr != null && !prefStr.isEmpty()) {
                        preferenze = new ArrayList<>(
                                Arrays.stream(prefStr.split(","))
                                        .map(Integer::parseInt)
                                        .toList()
                        );
                    }
                }

                // Creazione dell'oggetto corretto
                if (iscritto) {
                    utenti.add(new UtenteIscritto(id, utenteUsername, utentePassword, iscritto, idCorso, curriculum));
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
    public void aggiungiCorsoUtente(String usernameUtente, Integer idCorso) throws IOException {
        String query = "UPDATE utenti SET id_corso = ? WHERE username = ?";

        try (Connection conn = DriverManager.getConnection(url, username, password);
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, idCorso);
            stmt.setString(2, usernameUtente);
            stmt.executeUpdate();
        }catch (SQLException e) {
            throw new IOException("Errore durante la selezione del corso", e);
        }
    }

    @Override
    public void aggiungiPreferitiUtente(String usernameUtente, Integer idCorso) throws IOException {
        String querySelect = "SELECT preferenze FROM utenti WHERE username = ?";
        String queryUpdate = "UPDATE utenti SET preferenze = ? WHERE username = ?";

        try (Connection conn = DriverManager.getConnection(url, username, password);
             PreparedStatement stmtSelect = conn.prepareStatement(querySelect);
             PreparedStatement stmtUpdate = conn.prepareStatement(queryUpdate)) {

            stmtSelect.setString(1, usernameUtente);
            ResultSet rs = stmtSelect.executeQuery();

            String nuovaLista = String.valueOf(idCorso);
            if (rs.next() && rs.getString(colonnaPreferenze) != null) {
                String preferenzeAttuali = rs.getString(colonnaPreferenze);
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

    @Override
    public void aggiungiCurriculumUtente(String user, String curriculum) throws IOException {
        String query = "UPDATE utenti SET curriculum = ? WHERE username = ?";

        try (Connection conn = DriverManager.getConnection(url, username, password);
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, curriculum);
            stmt.setString(2, user);
            stmt.executeUpdate();
        }catch (SQLException e) {
            throw new IOException("Errore durante la selezione del curriculum", e);
        }
    }

    @Override
    public List<String> getPreferitiUtente(String username) throws IOException {
        String queryPreferenze = "SELECT preferenze FROM utenti WHERE username = ?";
        String queryCorso = """
    SELECT c.nomecorso, a.nome 
    FROM corsi c
    JOIN atenei a ON c.idateneo = a.id
    WHERE c.id = ?
""";


        List<String> preferiti = new ArrayList<>();

        try (Connection conn = DriverManager.getConnection(url, this.username, password);
             PreparedStatement stmtPref = conn.prepareStatement(queryPreferenze)) {

            stmtPref.setString(1, username);
            ResultSet rsPref = stmtPref.executeQuery();

            if (rsPref.next()) {
                String preferenzeStr = rsPref.getString("preferenze");
                if (preferenzeStr != null && !preferenzeStr.isBlank()) {
                    String[] idCorsi = preferenzeStr.split(",");

                    try (PreparedStatement stmtCorso = conn.prepareStatement(queryCorso)) {
                        for (String idStr : idCorsi) {
                            int id = Integer.parseInt(idStr.trim());
                            stmtCorso.setInt(1, id);
                            try (ResultSet rsCorso = stmtCorso.executeQuery()) {
                                if (rsCorso.next()) {
                                    String nomeCorso = rsCorso.getString("nomecorso");
                                    String nomeAteneo = rsCorso.getString("nome");
                                    preferiti.add(nomeCorso + " - " + nomeAteneo);

                                }
                            }
                        }
                    }
                }
            }

        } catch (SQLException e) {
            throw new IOException("Errore durante il recupero dei preferiti", e);
        }

        return preferiti;
    }


}
