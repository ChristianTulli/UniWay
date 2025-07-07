package uniway.persistenza;

import uniway.model.Utente;
import uniway.model.UtenteInCerca;
import uniway.model.UtenteIscritto;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class UtenteDB implements UtenteDAO {
    private String colonnaPreferenze = "preferenze";
    private String queryfrequente = "SELECT preferenze FROM utenti WHERE username = ?";
    private final Connection conn;

    public UtenteDB(Connection conn) {
        this.conn = conn;
    }

    @Override
    public void salvaUtente(Utente utente) throws IOException {
        String query = "INSERT INTO utenti (username, password, iscritto) VALUES (?, ?, ?)";

        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, utente.getUsername());
            stmt.setString(2, utente.getPassword());
            stmt.setBoolean(3, utente.getIscritto());

            if (stmt.executeUpdate() == 0) {
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

        try (PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                int id = rs.getInt("id");
                String utenteUsername = rs.getString("username");
                String utentePassword = rs.getString("password");
                boolean iscritto = rs.getBoolean("iscritto");
                Integer idCorso = rs.getObject("id_corso", Integer.class);
                String curriculum = rs.getString("curriculum");
                List<Integer> preferenze = new ArrayList<>();

                if (!iscritto) {
                    String prefStr = rs.getString(colonnaPreferenze);
                    if (prefStr != null && !prefStr.isEmpty()) {
                        preferenze = Arrays.stream(prefStr.split(","))
                                .map(Integer::parseInt)
                                .toList();
                    }
                }

                if (iscritto) {
                    utenti.add(new UtenteIscritto(id, utenteUsername, utentePassword, true, idCorso, curriculum));
                } else {
                    utenti.add(new UtenteInCerca(id, utenteUsername, utentePassword, false, preferenze));
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

        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, idCorso);
            stmt.setString(2, usernameUtente);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new IOException("Errore durante l'aggiornamento del corso", e);
        }
    }

    @Override
    public Boolean aggiungiPreferitiUtente(String usernameUtente, Integer idCorso) throws IOException {
        String querySelect = queryfrequente;
        String queryUpdate = "UPDATE utenti SET preferenze = ? WHERE username = ?";

        try (
                PreparedStatement stmtSelect = conn.prepareStatement(querySelect);
                PreparedStatement stmtUpdate = conn.prepareStatement(queryUpdate)
        ) {
            stmtSelect.setString(1, usernameUtente);
            ResultSet rs = stmtSelect.executeQuery();

            String nuovaLista = String.valueOf(idCorso);
            if (rs.next()) {
                String preferenzeAttuali = rs.getString(colonnaPreferenze);
                if (preferenzeAttuali != null && !preferenzeAttuali.isBlank()) {
                    List<String> listaPreferiti = new ArrayList<>(List.of(preferenzeAttuali.split(",")));
                    if (listaPreferiti.contains(nuovaLista)) {
                        return false; // già presente
                    }
                    listaPreferiti.add(nuovaLista);
                    nuovaLista = String.join(",", listaPreferiti);
                }
            }

            stmtUpdate.setString(1, nuovaLista);
            stmtUpdate.setString(2, usernameUtente);
            stmtUpdate.executeUpdate();
            return true;
        } catch (SQLException e) {
            throw new IOException("Errore durante l'aggiornamento dei preferiti", e);
        }
    }


    @Override
    public void aggiungiCurriculumUtente(String username, String curriculum) throws IOException {
        String query = "UPDATE utenti SET curriculum = ? WHERE username = ?";

        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, curriculum);
            stmt.setString(2, username);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new IOException("Errore durante l'aggiornamento del curriculum", e);
        }
    }

    @Override
    public List<Integer> getPreferitiUtente(String username) throws IOException {
        List<Integer> preferiti = new ArrayList<>();
        String query = queryfrequente;

        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String preferenzeStr = rs.getString(colonnaPreferenze);
                if (preferenzeStr != null && !preferenzeStr.isBlank()) {
                    for (String id : preferenzeStr.split(",")) {
                        preferiti.add(Integer.parseInt(id.trim()));
                    }
                }
            }
        } catch (SQLException e) {
            throw new IOException("Errore durante il recupero dei preferiti", e);
        }

        return preferiti;
    }

    @Override
    public void rimuoviPreferitoUtente(String username, int idCorso) throws IOException {
        String querySelect = queryfrequente;
        String queryUpdate = "UPDATE utenti SET preferenze = ? WHERE username = ?";

        try (
                PreparedStatement stmtSelect = conn.prepareStatement(querySelect);
                PreparedStatement stmtUpdate = conn.prepareStatement(queryUpdate)
        ) {
            stmtSelect.setString(1, username);
            ResultSet rs = stmtSelect.executeQuery();

            if (rs.next()) {
                String preferenzeStr = rs.getString(colonnaPreferenze);
                if (preferenzeStr == null || preferenzeStr.isBlank()) return;

                List<String> idList = new ArrayList<>(Arrays.asList(preferenzeStr.split(",")));
                boolean removed = idList.removeIf(id -> id.trim().equals(String.valueOf(idCorso)));

                if (removed) {
                    String nuovoValore = String.join(",", idList);
                    stmtUpdate.setString(1, nuovoValore);
                    stmtUpdate.setString(2, username);
                    stmtUpdate.executeUpdate();
                }
            }

        } catch (SQLException e) {
            throw new IOException("Errore durante la rimozione dai preferiti", e);
        }
    }

}

