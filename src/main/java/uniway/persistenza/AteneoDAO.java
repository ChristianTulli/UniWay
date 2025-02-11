package uniway.persistenza;

import uniway.model.Ateneo;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AteneoDAO {
    private final String url;
    private final String username;
    private final String password;

    public AteneoDAO(String url, String username, String password) {
        this.url = url;
        this.username = username;
        this.password = password;
    }
    public List<String> getAllRegioni() {
        List<String> regioni = new ArrayList<>();
        String query = "SELECT DISTINCT regione FROM atenei";

        try (Connection conn = DriverManager.getConnection(url, username, password);
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                regioni.add(rs.getString("regione"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return regioni;
    }
}
