package uniway.persistenza;

import uniway.controller.PersistenzaController;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AteneoDAO {
    private static final Logger LOGGER = Logger.getLogger(AteneoDAO.class.getName());
    private final Connection conn;
    private String eccezione = "problema nella comunicazione col database";

    public AteneoDAO() {
        this.conn = PersistenzaController.getInstance().getConnessione();
    }

    public List<String> getAllTipiAteneo() {
        List<String> statali = new ArrayList<>();
        String query = "SELECT DISTINCT statale FROM atenei";

        try (PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                statali.add(rs.getString("statale"));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, eccezione, e);
        }

        return statali;
    }

    public List<String> getTipologieByStatale(String statale) {
        List<String> tipologie = new ArrayList<>();
        String query = "SELECT DISTINCT tipologia FROM atenei WHERE statale = ?";

        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, statale);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    tipologie.add(rs.getString("tipologia"));
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, eccezione, e);
        }

        return tipologie;
    }
}

