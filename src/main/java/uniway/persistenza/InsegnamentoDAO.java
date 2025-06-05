package uniway.persistenza;

import uniway.model.Insegnamento;

import java.sql.*;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;


public class InsegnamentoDAO {
    private final String url;
    private final String username;
    private final String password;
    private static final Logger LOGGER = Logger.getLogger(InsegnamentoDAO.class.getName());

    public InsegnamentoDAO(String url, String username, String password) {
        this.url = url;
        this.username = username;
        this.password = password;
    }
    public void getInsegnamentiFromDB(String nomeCorso, String nomeAteneo, List<Insegnamento> insegnamenti) {
        String query = "SELECT i.nome, i.anno, i.semestre, i.cfu, i.curriculum " +
                "FROM insegnamenti i " +
                "JOIN corsi c ON i.id_corso = c.id " +
                "JOIN atenei a ON c.idateneo = a.id " +
                "WHERE c.nomecorso = ? AND a.nome = ?";

        try (Connection conn = DriverManager.getConnection(url, username, password);
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, nomeCorso);
            stmt.setString(2, nomeAteneo);

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Insegnamento ins = new Insegnamento();
                ins.setNome(rs.getString("nome"));
                ins.setAnno(rs.getInt("anno"));
                ins.setSemestre(rs.getInt("semestre"));
                ins.setCfu(rs.getInt("cfu"));
                ins.setCurriculum(rs.getString("curriculum"));
                insegnamenti.add(ins);
            }

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Errore nel recupero degli insegnamenti", e);
        }
    }

    public void getInsegnamentiByCurriculum(Integer idCorso, String curriculum, List<Insegnamento> insegnamenti) {
        String query = """
    SELECT id, nome, cfu, semestre, anno
    FROM insegnamenti
    WHERE id_corso = ? AND CONCAT(',', curriculum, ',') LIKE ?
""";

        try (Connection conn = DriverManager.getConnection(url, username, password);
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, idCorso);
            stmt.setString(2, "%," + curriculum + ",%");


            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Insegnamento ins = new Insegnamento();
                ins.setId(rs.getInt("id"));
                ins.setNome(rs.getString("nome"));
                ins.setCfu(rs.getInt("cfu"));
                ins.setSemestre(rs.getInt("semestre"));
                ins.setAnno(rs.getInt("anno"));
                insegnamenti.add(ins);
            }

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Errore nel recupero degli insegnamenti", e);
        }
    }


}
