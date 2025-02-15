package uniway.persistenza;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AteneoDAO {
    private final String url;
    private final String username;
    private final String password;
    private static final Logger LOGGER = Logger.getLogger(AteneoDAO.class.getName());
    private String eccezione = "problema nella comunicazione col databse";

    public AteneoDAO(String url, String username, String password) {
        this.url = url;
        this.username = username;
        this.password = password;
    }

    public List<String> getAllTipiAteneo() {
        List<String> statale = new ArrayList<>();
        String query = "SELECT DISTINCT statale FROM atenei";

        try (Connection conn = DriverManager.getConnection(url, username, password);
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                statale.add(rs.getString("statale"));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, eccezione, e);
        }

        return statale;
    }

    public List<String> getTipologieByStatale(String statale) {
        List<String> tipologie = new ArrayList<>();
        String query = "SELECT DISTINCT tipologia FROM atenei WHERE statale = ?";

        try (Connection conn = DriverManager.getConnection(url, username, password);
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, statale);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                tipologie.add(rs.getString("tipologia"));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, eccezione, e);
        }

        return tipologie;
    }



}
