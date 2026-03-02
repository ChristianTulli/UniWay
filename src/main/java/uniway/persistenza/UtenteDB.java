package uniway.persistenza;

import uniway.eccezioni.CorsoGiaPresenteTraIPreferitiException;
import uniway.model.*;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

//PRODUCT
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
                    Integer idCorso = rs.getObject("id_corso", Integer.class);
                    String curriculum = rs.getString("curriculum");
                    String preferenzeStr = rs.getString(colonnaPreferenze);
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
        if(idCorso==null) return null;
        String query = """
                
                SELECT
                                c.id,
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
                                ON a.id = c.idateneo
                                WHERE c.id = ?
                """;
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, idCorso);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String regionecorso = rs.getString("regionecorso");
                    String sedeprovincia = rs.getString("sedeprovincia");
                    String sedecomune = rs.getString("sedecomune");
                    String nomeAteneo = rs.getString("nome_ateneo");
                    String gruppodisciplinare = rs.getString("gruppodisciplinare");
                    String durata = rs.getString("durata");
                    String nomeclasse = rs.getString("nomeclasse");
                    String nomecorso = rs.getString("nomecorso");
                    Corso corso = new Corso(regionecorso, sedeprovincia, sedecomune, nomeAteneo, gruppodisciplinare, nomeclasse, nomecorso);
                    corso.setDurata(durata);
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
                curriculum = ?                              -- caso sono un curriculum
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
            stmt.setString(3, curriculum);
            stmt.setString(4, curriculum);
            stmt.setString(5, curriculum);

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
    public void aggiungiCorsoUtente(UtenteIscritto u, Corso c) {
        String usernameUtente=u.getUsername();
        String nomeCorso=c.getNomeCorso();
        String nomeAteneo=c.getAteneo();
        String curriculum=c.getCurriculum();

        String query = """
                UPDATE utenti u
        JOIN corsi c   ON c.nomecorso = ?
        JOIN atenei a  ON a.id = c.idateneo AND a.nome = ?
        SET u.id_corso = c.id,
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
    public void aggiungiPreferitiUtente(UtenteInCerca utente, Corso corso) {

        final String username = utente.getUsername();
        final Integer idCorso = resolveIdCorso(corso);

        final String qSelect = queryfrequente;
        final String qUpdate = "UPDATE utenti SET preferenze = ? WHERE username = ?";

        try (PreparedStatement psSel = conn.prepareStatement(qSelect);
             PreparedStatement psUpd = conn.prepareStatement(qUpdate)) {

            psSel.setString(1, username);
            String nuovaLista = String.valueOf(idCorso);

            try (ResultSet rs = psSel.executeQuery()) {
                if (rs.next()) {
                    String preferenzeAttuali = rs.getString(colonnaPreferenze);
                    if (preferenzeAttuali != null && !preferenzeAttuali.isBlank()) {
                        Set<String> set = new LinkedHashSet<>();
                        for (String tok : preferenzeAttuali.split(",")) {
                            String trimmed = tok.trim();
                            if (!trimmed.isEmpty()) set.add(trimmed);
                        }
                        // controllo duplicato
                        if (set.contains(nuovaLista)) {
                            throw new CorsoGiaPresenteTraIPreferitiException(
                                    "Il corso è già presente tra i preferiti.");
                        }
                        set.add(nuovaLista);
                        nuovaLista = String.join(",", set);
                    }
                }
            }

            psUpd.setString(1, nuovaLista);
            psUpd.setString(2, username);
            psUpd.executeUpdate();

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Errore durante l'aggiornamento dei preferiti", e);
        }
    }

    private Integer resolveIdCorso(Corso c) {
        final String nomeCorso  = c.getNomeCorso();
        final String nomeAteneo = c.getAteneo();

        if (nomeCorso == null || nomeCorso.isBlank() || nomeAteneo == null || nomeAteneo.isBlank()) {
            return null;
        }

        final String sql = """
        SELECT c.id
        FROM corsi c
        JOIN atenei a ON a.id = c.idateneo
        WHERE c.nomecorso = ? AND a.nome = ?
        LIMIT 1
    """;

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, nomeCorso);
            ps.setString(2, nomeAteneo);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt(1) : null;
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Errore in resolveIdCorso()", e);
            return null;
        }
    }

    @Override
    public void rimuoviPreferitoUtente(UtenteInCerca utente, Corso corso) {
        final String username = utente.getUsername();
        final Integer idCorso = resolveIdCorso(corso);
        if (idCorso == null) {
            LOGGER.severe("Impossibile risolvere idCorso da nome/ateneo");
            return;
        }

        final String qSelect = queryfrequente;
        final String qUpdate = "UPDATE utenti SET preferenze = ? WHERE username = ?";

        try (PreparedStatement psSel = conn.prepareStatement(qSelect);
             PreparedStatement psUpd = conn.prepareStatement(qUpdate)) {

            psSel.setString(1, username);

            try (ResultSet rs = psSel.executeQuery()) {
                if (!rs.next()) {
                    LOGGER.severe("Utente non trovato durante la rimozione del preferito");
                    return;
                }

                String preferenzeStr = rs.getString("preferenze");
                if (preferenzeStr == null || preferenzeStr.isBlank()) {
                    // Nessun preferito da cui rimuovere
                    return;
                }

                // traduci in lista
                List<String> idList = new ArrayList<>();
                for (String tok : preferenzeStr.split(",")) {
                    String t = tok.trim();
                    if (!t.isEmpty()) idList.add(t);
                }

                boolean removed = idList.removeIf(t -> t.equals(String.valueOf(idCorso)));
                if (!removed) {
                    // Il corso non era nei preferiti, niente da fare
                    return;
                }

                if (idList.isEmpty()) {
                    // Svuota la colonna
                    psUpd.setNull(1, java.sql.Types.VARCHAR);
                } else {
                    psUpd.setString(1, String.join(",", idList));
                }
                psUpd.setString(2, username);
                psUpd.executeUpdate();
            }

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Errore durante la rimozione del corso dai preferiti", e);
        }
    }
}

