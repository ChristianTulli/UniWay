package uniway.persistenza;

import uniway.model.Corso;
import uniway.model.Insegnamento;

import java.sql.*;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

//PRODUCT
public class InsegnamentoDAO {

    private final Connection conn;
    private static final Logger LOGGER = Logger.getLogger(InsegnamentoDAO.class.getName());
    private String semestre="semestre";

    public InsegnamentoDAO(Connection conn) {
        this.conn = conn;
    }

    public void getInsegnamenti(Corso corso, List<Insegnamento> insegnamenti) {
        String nomeCorso = corso.getNomeCorso();
        String nomeAteneo = corso.getAteneo();
        String query = """
                SELECT i.nome, i.anno, i.semestre, i.cfu, i.curriculum
                FROM insegnamenti i
                JOIN corsi c ON i.id_corso = c.id
                JOIN atenei a ON c.idateneo = a.id
                WHERE c.nomecorso = ? AND a.nome = ?
                """;

        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, nomeCorso);
            stmt.setString(2, nomeAteneo);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Insegnamento ins = new Insegnamento();
                    ins.setNome(rs.getString("nome"));
                    ins.setAnno(rs.getInt("anno"));
                    ins.setSemestre(rs.getInt(semestre));
                    ins.setCfu(rs.getInt("cfu"));
                    insegnamenti.add(ins);
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Errore nel recupero degli insegnamenti", e);
        }
    }

    public void getInsegnamentiWithCurriculum(Corso corso, List<Insegnamento> insegnamenti) {
        String query = """
        SELECT i.nome, i.anno, i.semestre, i.cfu, i.curriculum
        FROM insegnamenti i
        JOIN corsi c ON i.id_corso = c.id
        JOIN atenei a ON c.idateneo = a.id
        WHERE c.nomecorso = ? 
          AND a.nome = ?
          AND (
                i.curriculum = ?                              -- match esatto
             OR i.curriculum LIKE CONCAT('%,', ?, ',%')       -- caso in mezzo
             OR i.curriculum LIKE CONCAT(?, ',%')             -- caso all’inizio
             OR i.curriculum LIKE CONCAT('%,', ?)             -- caso alla fine
          )
    """;

        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, corso.getNomeCorso());
            stmt.setString(2, corso.getAteneo());
            stmt.setString(3, corso.getCurriculum());
            stmt.setString(4, corso.getCurriculum());
            stmt.setString(5, corso.getCurriculum());
            stmt.setString(6, corso.getCurriculum());

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Insegnamento ins = new Insegnamento();
                    ins.setNome(rs.getString("nome"));
                    ins.setAnno(rs.getInt("anno"));
                    ins.setSemestre(rs.getInt(semestre));
                    ins.setCfu(rs.getInt("cfu"));
                    insegnamenti.add(ins);
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Errore nel recupero degli insegnamenti con curriculum", e);
        }
    }



    public Insegnamento getInsegnamento(Corso corso, String nomeInsegnamento) {
        String nomeCorso = corso.getNomeCorso();
        String nomeAteneo = corso.getAteneo();
        String query = """
        SELECT i.nome, i.anno, i.semestre, i.cfu
        FROM insegnamenti i
        JOIN corsi c ON i.id_corso = c.id
        JOIN atenei a ON c.idateneo = a.id
        WHERE i.nome = ? AND c.nomecorso = ? AND a.nome = ?
    """;

        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, nomeInsegnamento);
            stmt.setString(2, nomeCorso);
            stmt.setString(3, nomeAteneo);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String nome = rs.getString("nome");
                    int anno = rs.getInt("anno");
                    int sem = rs.getInt(semestre);
                    int cfu = rs.getInt("cfu");

                    // usa il costruttore che hai fornito
                    return new Insegnamento(nome, anno, sem, cfu);
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Errore nel recupero dell'insegnamento", e);
        }

        return null; // nessun insegnamento trovato
    }


}
