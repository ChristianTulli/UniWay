package uniway.persistenza;

import uniway.model.Insegnamento;
import uniway.model.Utente;

import java.io.IOException;
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
        String query = "SELECT valutazione_generale FROM recensioni WHERE id_insegnamento = ? AND nome_utente = ?";

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

    public void setRecesnione(String testo, Integer valutazione, String nomeUtente, Integer idInsegnamento) {
            String query;
            query = "INSERT INTO recensioni (commento, valutazione_generale, nome_utente, id_insegnamento) VALUES (?, ?, ?, ?)";//scrivere query per inserire le recensioni

            try (Connection conn = DriverManager.getConnection(url, username, password);
                 PreparedStatement stmt = conn.prepareStatement(query)){

                stmt.setString(1, testo);
                stmt.setInt(2, valutazione);
                stmt.setString(3, nomeUtente);
                stmt.setInt(4, idInsegnamento);

                int rowsAffected = stmt.executeUpdate(); // Eseguiamo la query

                if (rowsAffected == 0) {
                    throw new IOException("Nessuna recensione è stata inserita nel database.");
                }

            } catch (IOException | SQLException e) {
                e.printStackTrace();
            }
    }
}
