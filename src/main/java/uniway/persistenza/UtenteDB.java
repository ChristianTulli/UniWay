package uniway.persistenza;

import uniway.model.*;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class UtenteDB implements UtenteDAO {
    private static final Logger LOGGER = Logger.getLogger(UtenteDB.class.getName());
    private final Connection conn;
    private String colonnaPreferenze = "preferenze";
    private String queryfrequente = "SELECT preferenze FROM utenti WHERE username = ?";

    public UtenteDB(Connection conn) {
        this.conn = conn;
    }

    @Override
    public Utente trovaDaUsername(String username) {
        String query = "SELECT username, password, iscritto, id_corso, preferenze, curriculum FROM utenti WHERE username = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, username);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String user = rs.getString("username");
                    String pass = rs.getString("password");
                    boolean iscritto = rs.getBoolean("iscritto");
                    Integer idCorso = rs.getInt("id_corso");
                    String curriculum = rs.getString("curriculum");
                    String preferenzeStr = rs.getString("preferenze");
                    List<Integer> preferenze= parsePreferenze(preferenzeStr);
                    Utente utente;
                    if (iscritto) {
                        utente = new UtenteIscritto(user, pass, iscritto, trovaCorsoDaId(idCorso, curriculum));
                    } else {
                        List<Corso> corsiPreferiti = new ArrayList<>();
                        for (Integer idPref : preferenze) {
                            Corso corso = trovaCorsoDaId(idPref, null); // curriculum non serve per i preferiti
                            if (corso != null) {
                                corsiPreferiti.add(corso);
                            }
                        }
                        utente = new UtenteInCerca(user, pass, iscritto, corsiPreferiti);

                    }
                    return utente;
                }

            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "errore durante la ricerca dell'utente");
        }
        return null; // nessun utente trovato
    }

    private List<Integer> parsePreferenze(String prefStr) {
        if (prefStr == null || prefStr.isBlank()) return List.of();
        List<Integer> out = new ArrayList<>();
        for (String token : prefStr.split(",")) {
            try {
                out.add(Integer.parseInt(token.trim()));
            } catch (NumberFormatException ex) {
                LOGGER.log(Level.WARNING, "ID corso non valido nelle preferenze: {0}", token);
            }
        }
        return out;
    }

    private Corso trovaCorsoDaId(Integer idCorso, String curriculum) {
        String query = """
                
                SELECT
                                c.idCorso,
                                        c.regionecorso,
                                        c.sedeprovincia,
                                        c.sedecomune,
                                        a.nome           AS nome_ateneo,
                                c.gruppodisciplinare,
                                        c.durata,
                                        c.nomeclasse,
                                        c.nomecorso
                                FROM corsi AS c
                                JOIN atenei AS a
                                ON a.idAteneo = c.idAteneo
                                WHERE c.idCorso = ?
                """;
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, idCorso);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String regionecorso = rs.getString("regionecorso");
                    String sedeprovincia = rs.getString("sedeprovincia");
                    String sedecomune = rs.getString("sedecomune");
                    String nomeAteneo = rs.getString("nome");
                    String gruppodisciplinare = rs.getString("gruppodisciplinare");
                    String durata = rs.getString("durata");
                    String nomeclasse = rs.getString("nomeclasse");
                    String nomecorso = rs.getString("nomecorso");
                    Corso corso = new Corso(regionecorso, sedeprovincia, sedecomune, nomeAteneo, gruppodisciplinare, durata, nomeclasse, nomecorso);
                    if(curriculum!=null) {
                        List<Insegnamento> insegnamenti = trovaInsegnamentiDaIdCorsoCurriculum(idCorso, curriculum);
                        corso.setCurriculum(curriculum);
                        corso.setInsegnamenti(insegnamenti);
                    }
                    return corso;
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "errore durante la ricerca del corso");
        }

        return null;
    }

    private List<Insegnamento> trovaInsegnamentiDaIdCorsoCurriculum(Integer idCorso, String curriculum){
        String query = """
                SELECT nome, anno, semestre, cfu
        FROM insegnamenti
        WHERE id_corso = ?
        AND (
                curriculum = ?                              -- caso "solo quel curriculum"
        OR curriculum LIKE CONCAT('%,', ?, ',%')       -- caso in mezzo
        OR curriculum LIKE CONCAT(?, ',%')             -- caso all’inizio
        OR curriculum LIKE CONCAT('%,', ?)             -- caso alla fine
  );
        """
        ;
        List<Insegnamento> insegnamenti = new ArrayList<>();

        try(PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, idCorso);
            stmt.setString(2, curriculum);

            try (ResultSet rs = stmt.executeQuery()) {

                while (rs.next()) {
                    String nome = rs.getString("nome");
                    Integer anno = rs.getInt("anno");
                    Integer semestre = rs.getInt("semestre");
                    Integer cfu = rs.getInt("cfu");
                    insegnamenti.add(new Insegnamento(nome, anno, semestre, cfu));
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "errore durante la ricerca dell'insegnamento");
        }
        return insegnamenti;
    }


    @Override
    public void salvaUtente(Utente utente) {
        String query = "INSERT INTO utenti (username, password, iscritto) VALUES (?, ?, ?)";

        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, utente.getUsername());
            stmt.setString(2, utente.getPassword());
            stmt.setBoolean(3, utente.getIscritto());

            if (stmt.executeUpdate() == 0) {
                throw new IOException("Nessun utente è stato inserito nel database.");
            }

        } catch (SQLException | IOException e) {
            LOGGER.log(Level.SEVERE, "errore durante la registrazione dell'utente");
        }
    }

    @Override
    public List<Utente> ottieniUtenti() {
        String query = "SELECT id, username, password, iscritto, id_corso, preferenze, curriculum FROM utenti";
        List<Utente> utenti = new ArrayList<>();

        try (PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                String utenteUsername = rs.getString("username");
                String utentePassword = rs.getString("password");
                boolean iscritto = rs.getBoolean("iscritto");

                if (iscritto) {
                    utenti.add(new UtenteIscritto(utenteUsername, utentePassword, true));
                } else {
                    utenti.add(new UtenteInCerca(utenteUsername, utentePassword, false));
                }
            }

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "errore durante il recuper degli utenti");
        }

        return utenti;
    }

    @Override
    public void aggiungiCorsoUtente(Utente u, Corso c) {
        String usernameUtente=u.getUsername();
        String nomeCorso=c.getNomeCorso();
        String nomeAteneo=c.getAteneo();
        String curriculum=c.getCurriculum();

        String query = """
                UPDATE utenti u
        JOIN corsi c   ON c.nomecorso = ?
        JOIN atenei a  ON a.idAteneo = c.idAteneo AND a.nome = ?
        SET u.id_corso = c.idCorso,
                u.curriculum = ?
        WHERE u.username = ?
        """;

        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, nomeCorso);
            stmt.setString(2, nomeAteneo);
            stmt.setString(3, curriculum);
            stmt.setString(4, usernameUtente);
            stmt.executeUpdate();
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "errore durante l'inserimento del corso");
        }
    }

    @Override
    public Boolean aggiungiPreferitiUtente(String usernameUtente, Integer idCorso) {
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
            LOGGER.log(Level.SEVERE, "errore durante l'aggiornamrnto dei preferiti");
            return false;
        }
    }


    @Override
    public void aggiungiCurriculumUtente(String username, String curriculum) {
        String query = "UPDATE utenti SET curriculum = ? WHERE username = ?";

        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, curriculum);
            stmt.setString(2, username);
            stmt.executeUpdate();
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "errore durante l'aggiornamrnto del curriculum");
        }
    }

    @Override
    public List<Integer> getPreferitiUtente(String username) {
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
            LOGGER.log(Level.SEVERE, "errore durante il recupero dei preferiti");
        }

        return preferiti;
    }

    @Override
    public void rimuoviPreferitoUtente(String username, int idCorso) {
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
            LOGGER.log(Level.SEVERE, "errore durante la rimozione dei preferiti");
        }
    }

}

