package uniway.persistenza;

import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RecensioneDAO {
    private final String url;
    private final String username;
    private final String password;
    private static final Logger LOGGER = Logger.getLogger(RecensioneDAO.class.getName());
    private String eccezione = "problema nella comunicazione con la tabella recensioni nel databse";

    public RecensioneDAO(String url, String username, String password) {
        this.url = url;
        this.username = username;
        this.password = password;
    }

    public Integer getValutazioneUtente(int idInsegnamento, String usernameUtente) {
        String query = "SELECT valutazione_generale FROM recensioni r " +
                "JOIN utenti u ON r.id_utente = u.id " +
                "WHERE r.id_insegnamento = ? AND u.username = ?";

        try (Connection conn = DriverManager.getConnection(url, username, password);
        PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, idInsegnamento);
            stmt.setString(2, usernameUtente);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt("valutazione_generale");
            }

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, eccezione, e);
        }

        return null; // Nessuna recensione trovata
    }

}
