package uniway.persistenza;

import uniway.controller.PersistenzaController;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RecensioneDAO {
    private static final Logger LOGGER = Logger.getLogger(RecensioneDAO.class.getName());
    private static final String ECCEZIONE = "problema nella comunicazione con la tabella recensioni nel database";
    private final Connection conn;

    public RecensioneDAO() {
        this.conn = PersistenzaController.getInstance().getConnessione();
    }

    public Integer getValutazioneUtente(int idInsegnamento, String usernameUtente) {
        String query = "SELECT valutazione_generale FROM recensioni WHERE id_insegnamento = ? AND nome_utente = ?";

        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, idInsegnamento);
            stmt.setString(2, usernameUtente);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("valutazione_generale");
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, ECCEZIONE, e);
        }

        return null; // Nessuna recensione trovata
    }

    public void setRecesnione(String testo, Integer valutazione, String nomeUtente, Integer idInsegnamento) {
        String query = "INSERT INTO recensioni (commento, valutazione_generale, nome_utente, id_insegnamento) VALUES (?, ?, ?, ?)";

        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, testo);
            stmt.setInt(2, valutazione);
            stmt.setString(3, nomeUtente);
            stmt.setInt(4, idInsegnamento);

            int rowsAffected = stmt.executeUpdate();

            if (rowsAffected == 0) {
                throw new IOException("Nessuna recensione è stata inserita nel database.");
            }
        } catch (IOException | SQLException e) {
            LOGGER.log(Level.SEVERE, ECCEZIONE, e);
        }
    }
}

