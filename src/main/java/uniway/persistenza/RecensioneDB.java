package uniway.persistenza;


import uniway.eccezioni.RecensioneNonSalvataException;
import uniway.model.Insegnamento;
import uniway.model.Recensione;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

//PRODUCT
public class RecensioneDB implements RecensioneDAO {
    private static final Logger LOGGER = Logger.getLogger(RecensioneDB.class.getName());
    private final Connection conn;

    public RecensioneDB(Connection conn) {
        this.conn = conn;
    }

    @Override
    public Integer getValutazioneUtente(Insegnamento insegnamento, String usernameUtente) {
        String query = """
        SELECT r.valutazione_generale
        FROM recensioni r
        JOIN insegnamenti i ON r.id_insegnamento = i.id_insegnamento
        WHERE i.nome = ? AND i.anno = ? AND i.semestre = ? AND i.cfu = ? AND r.nome_utente = ?
    """;

        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, insegnamento.getNome());
            stmt.setInt(2, insegnamento.getAnno());
            stmt.setInt(3, insegnamento.getSemestre());
            stmt.setInt(4, insegnamento.getCfu());
            stmt.setString(5, usernameUtente);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("valutazione_generale");
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Errore nel recupero valutazione", e);
        }

        return null;
    }

    @Override
    public void setRecensione(Recensione recensione) throws RecensioneNonSalvataException {
        Objects.requireNonNull(recensione, "recensione nulla");
        Objects.requireNonNull(recensione.getInsegnamento(), "insegnamento nullo");
        Objects.requireNonNull(recensione.getNomeUtente(), "nome utente nullo");

        //trovo id dell'insegnamento
        Integer idInsegnamento = resolveIdInsegnamento(recensione);
        if (idInsegnamento == null) {
            throw new RecensioneNonSalvataException("Insegnamento non trovato per l'utente selezionato");
        }

        // salvo la recensione
        final String insert = """
        INSERT INTO recensioni (commento, valutazione_generale, nome_utente, id_insegnamento)
        VALUES (?, ?, ?, ?)
    """;

        try (PreparedStatement ps = conn.prepareStatement(insert)) {
            ps.setString(1, recensione.getCommento());
            ps.setInt(2, recensione.getValutazione());
            ps.setString(3, recensione.getNomeUtente());
            ps.setInt(4, idInsegnamento);

            int rows = ps.executeUpdate();
            if (rows != 1) {
                throw new RecensioneNonSalvataException("La recensione non è stata salvata");
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Errore durante l'inserimento recensione", e);
            throw new RecensioneNonSalvataException("Errore DB durante il salvataggio");
        }
    }

    //trova l'id_insegnamento
    private Integer resolveIdInsegnamento(Recensione r) {
        Insegnamento ins = r.getInsegnamento();
        String curriculum = ins.getCurriculum();

        // Query con curriculum presente
        final String withCurr = """
        SELECT i.id_insegnamento
        FROM insegnamenti i
        JOIN utenti u ON u.id_corso = i.id_corso
        WHERE u.username = ?
          AND i.nome     = ?
          AND i.anno     = ?
          AND i.semestre = ?
          AND i.cfu      = ?
          AND ( i.curriculum = ?
             OR i.curriculum LIKE CONCAT('%,', ?, ',%')
             OR i.curriculum LIKE CONCAT(?, ',%')
             OR i.curriculum LIKE CONCAT('%,', ?) )
        LIMIT 1
    """;

        // Variante senza curriculum
        final String noCurr = """
        SELECT i.id_insegnamento
        FROM insegnamenti i
        JOIN utenti u ON u.id_corso = i.id_corso
        WHERE u.username = ?
          AND i.nome     = ?
          AND i.anno     = ?
          AND i.semestre = ?
          AND i.cfu      = ?
        LIMIT 1
    """;

        try (PreparedStatement ps = conn.prepareStatement(
                (curriculum != null && !curriculum.isBlank()) ? withCurr : noCurr)) {

            int k = 1;
            ps.setString(k++, r.getNomeUtente());
            ps.setString(k++, ins.getNome());
            ps.setInt(k++,    ins.getAnno());
            ps.setInt(k++,    ins.getSemestre());
            ps.setInt(k++,    ins.getCfu());

            if (curriculum != null && !curriculum.isBlank()) {
                ps.setString(k++, curriculum);
                ps.setString(k++, curriculum);
                ps.setString(k++, curriculum);
                ps.setString(k++, curriculum);
            }

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt(1) : null;
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Errore durante la risoluzione id_insegnamento", e);
            return null;
        }
    }


    @Override
    public List<Recensione> getRecensioniByInsegnamento(Integer idInsegnamento) {
        List<Recensione> recensioni = new ArrayList<>();
        String query = "SELECT nome_utente, commento, valutazione_generale FROM recensioni WHERE id_insegnamento = ?";

        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, idInsegnamento);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Recensione recensione = new Recensione();
                recensione.setNomeUtente(rs.getString("nome_utente"));
                recensione.setCommento(rs.getString("commento"));
                recensione.setValutazione(rs.getInt("valutazione_generale"));
                recensioni.add(recensione);
            }

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Errore nel recupero delle recensioni", e);
        }

        return recensioni;
    }

}

