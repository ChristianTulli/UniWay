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
        JOIN insegnamenti i ON r.id_insegnamento = i.id
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
        Integer idInsegnamento = resolveIdInsegnamento(recensione.getInsegnamento());
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
    private Integer resolveIdInsegnamento(Insegnamento ins) {

        final String query = """
        SELECT i.id
                    FROM insegnamenti i
                    WHERE i.nome=? AND i.anno=? AND i.semestre=? AND i.cfu=?
                    LIMIT 1
    """;

        try (PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, ins.getNome());
            ps.setInt(2,    ins.getAnno());
            ps.setInt(3,    ins.getSemestre());
            ps.setInt(4,    ins.getCfu());

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt(1) : null;
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Errore durante la risoluzione id_insegnamento", e);
            return null;
        }
    }


    @Override
    public List<Recensione> getRecensioniByInsegnamento(Insegnamento insegnamento) {
        Objects.requireNonNull(insegnamento, "insegnamento nullo");

        Integer idIns = resolveIdInsegnamento(insegnamento); // vedi sotto
        if (idIns == null) return List.of(); // nessun match

        String sql = """
            SELECT r.commento, r.valutazione_generale, r.nome_utente
            FROM recensioni r
            WHERE r.id_insegnamento = ?
            ORDER BY r.nome_utente
        """;

        List<Recensione> out = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idIns);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Recensione rec = new Recensione(
                            rs.getString("commento"),
                            rs.getInt("valutazione_generale"),
                            rs.getString("nome_utente"),
                            insegnamento // ricollego lo stesso oggetto passato
                    );
                    out.add(rec);
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Errore nel recupero recensioni per insegnamento", e);
        }
        return out;
    }

}

