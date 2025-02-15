package uniway.persistenza;


import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CorsoDAO {
    private final String url;
    private final String username;
    private final String password;
    private static final Logger LOGGER = Logger.getLogger(CorsoDAO.class.getName());
    private String eccezione = "problema nella comunicazione col databse";

    public CorsoDAO(String url, String username, String password) {
        this.url = url;
        this.username = username;
        this.password = password;
    }

    public List<String> getAllRegioni() {
        List<String> regioni = new ArrayList<>();
        String query = "SELECT DISTINCT regionecorso FROM corsi";

        try (Connection conn = DriverManager.getConnection(url, username, password);
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                regioni.add(rs.getString("regionecorso"));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, eccezione, e);
        }

        return regioni;
    }

    public List<String> getAllDurate() {
        List<String> durate = new ArrayList<>();
        String query = "SELECT DISTINCT durata FROM corsi";

        try (Connection conn = DriverManager.getConnection(url, username, password);
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                durate.add(rs.getString("durata"));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, eccezione, e);
        }

        return durate;
    }

    public List<String> getProvinceByRegione(String regione) {
        List<String> province = new ArrayList<>();
        String query = "SELECT DISTINCT sedeprovincia FROM corsi WHERE regionecorso = ?";

        try (Connection conn = DriverManager.getConnection(url, username, password);
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, regione);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                province.add(rs.getString("sedeprovincia"));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, eccezione, e);
        }

        return province;
    }

    public List<String> getComuniByProvincia(String provincia) {
        List<String> comuni = new ArrayList<>();
        String query = "SELECT DISTINCT sedecomune FROM corsi WHERE sedeprovincia = ?";

        try (Connection conn = DriverManager.getConnection(url, username, password);
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, provincia);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                comuni.add(rs.getString("sedecomune"));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, eccezione, e);
        }

        return comuni;
    }

    public List<String> getAteneiByComune(String comune) {
        List<String> atenei = new ArrayList<>();
        String query = "SELECT DISTINCT a.nome FROM atenei a " +
                "JOIN corsi c ON a.id = c.idAteneo " +
                "WHERE c.sedecomune = ?";

        try (Connection conn = DriverManager.getConnection(url, username, password);
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, comune);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                atenei.add(rs.getString("nome"));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, eccezione, e);
        }

        return atenei;
    }

    public List<String> getDisciplineByAteneo(String comune, String ateneo) {
        List<String> discipline = new ArrayList<>();
        String query = "SELECT DISTINCT c.gruppodisciplinare " +
                "FROM corsi c " +
                "JOIN atenei a ON c.idateneo = a.id " +
                "WHERE a.nome = ? AND c.sedecomune = ?";

        try (Connection conn = DriverManager.getConnection(url, username, password);
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, ateneo);
            stmt.setString(2, comune);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                discipline.add(rs.getString("gruppodisciplinare"));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, eccezione, e);
        }

        return discipline;
    }

    public List<String> getTipologieByDisciplina(String disciplina) {
        List<String> tipologie = new ArrayList<>();
        String query = "SELECT DISTINCT durata FROM corsi WHERE gruppodisciplinare = ?";

        try (Connection conn = DriverManager.getConnection(url, username, password);
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, disciplina);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                tipologie.add(rs.getString("durata"));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, eccezione, e);
        }

        return tipologie;
    }

    public List<String> getCorsiByTipologia(String comune, String ateneo, String disciplina, String tipologia) {
        List<String> corsi = new ArrayList<>();
        String query = "SELECT DISTINCT c.nomeclasse " +
                "FROM corsi c " +
                "JOIN atenei a ON c.idateneo = a.id " +
                "WHERE c.sedecomune = ? " +
                "AND a.nome = ? " +
                "AND c.gruppodisciplinare = ? " +
                "AND c.durata = ?";

        try (Connection conn = DriverManager.getConnection(url, username, password);
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, comune);
            stmt.setString(2, ateneo);
            stmt.setString(3, disciplina);
            stmt.setString(4, tipologia);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                corsi.add(rs.getString("nomeclasse"));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, eccezione, e);
        }

        return corsi;
    }

    public List<String> getRisultatiByCorsi(String comune, String ateneo, String disciplina, String tipologia, String nomeclasse) {
        List<String> risultati = new ArrayList<>();
        String query = "SELECT DISTINCT c.nomecorso " +
                "FROM corsi c " +
                "JOIN atenei a ON c.idateneo = a.id " +
                "WHERE c.sedecomune = ? " +
                "AND a.nome = ? " +
                "AND c.gruppodisciplinare = ? " +
                "AND c.durata = ? " +
                "AND c.nomeclasse = ?";

        try (Connection conn = DriverManager.getConnection(url, username, password);
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, comune);
            stmt.setString(2, ateneo);
            stmt.setString(3, disciplina);
            stmt.setString(4, tipologia);
            stmt.setString(5, nomeclasse);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                risultati.add(rs.getString("nomecorso"));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, eccezione, e);
        }

        return risultati;
    }

    public Integer getIdCorsoByNome(String comune, String ateneo, String tipologia, String nomecorso) {
        String query = "SELECT c.id " +
                "FROM corsi c " +
                "JOIN atenei a ON c.idateneo = a.id " +
                "WHERE c.sedecomune = ? " +
                "AND a.nome = ?" +
                "AND c.durata = ?" +
                "AND c.nomecorso = ?";

        try (Connection conn = DriverManager.getConnection(url, username, password);
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, comune);
            stmt.setString(2, ateneo);
            stmt.setString(3, tipologia);
            stmt.setString(4, nomecorso);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt("id");
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, eccezione, e);
        }
        return null; // Restituisce null se il corso non è trovato
    }

    public List<String> getDisciplineByDurata(String durata) {
        List<String> discipline = new ArrayList<>();
        String query = "SELECT DISTINCT gruppodisciplinare FROM corsi WHERE durata = ?";

        try (Connection conn = DriverManager.getConnection(url, username, password);
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, durata);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                discipline.add(rs.getString("gruppodisciplinare"));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, eccezione, e);
        }

        return discipline;
    }

    public List<String> getClassiByDisciplina(String disciplina, String durata) {
        List<String> classi = new ArrayList<>();
        String query = "SELECT DISTINCT nomeclasse FROM corsi WHERE gruppodisciplinare = ? AND durata = ?";

        try (Connection conn = DriverManager.getConnection(url, username, password);
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, disciplina);
            stmt.setString(2, durata);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                classi.add(rs.getString("nomeclasse"));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, eccezione, e);
        }

        return classi;
    }

    public List<String> getRisultatiRicerca(String statale, String tipologia, String regione, String provincia,
                                            String comune, String durata, String gruppoDisciplina, String classeCorso) {
        List<String> risultati = new ArrayList<>();
        StringBuilder query = new StringBuilder(
                "SELECT c.nomecorso, a.nome AS nome_ateneo FROM corsi c " +
                        "JOIN atenei a ON c.idateneo = a.id "
        );
        List<String> condizioni = new ArrayList<>();
        List<Object> parametri = new ArrayList<>();

        // Aggiunta condizioni dinamiche solo se i filtri sono impostati
        if (statale != null && !statale.isEmpty()) {
            condizioni.add("a.statale = ?");
            parametri.add(statale);
        }
        if (tipologia != null && !tipologia.isEmpty()) {
            condizioni.add("a.tipologia = ?");
            parametri.add(tipologia);
        }
        if (regione != null && !regione.isEmpty()) {
            condizioni.add("c.regionecorso = ?");
            parametri.add(regione);
        }
        if (provincia != null && !provincia.isEmpty()) {
            condizioni.add("c.sedeprovincia = ?");
            parametri.add(provincia);
        }
        if (comune != null && !comune.isEmpty()) {
            condizioni.add("c.sedecomune = ?");
            parametri.add(comune);
        }
        if (durata != null && !durata.isEmpty()) {
            condizioni.add("c.durata = ?");
            parametri.add(durata);
        }
        if (gruppoDisciplina != null && !gruppoDisciplina.isEmpty()) {
            condizioni.add("c.gruppodisciplinare = ?");
            parametri.add(gruppoDisciplina);
        }
        if (classeCorso != null && !classeCorso.isEmpty()) {
            condizioni.add("c.nomeclasse = ?");
            parametri.add(classeCorso);
        }

        // Costruzione della query con WHERE dinamico
        if (!condizioni.isEmpty()) {
            query.append(" WHERE ").append(String.join(" AND ", condizioni));
        }

        try (Connection conn = DriverManager.getConnection(url, username, password);
             PreparedStatement stmt = conn.prepareStatement(query.toString())) {

            // Assegnazione dei parametri
            for (int i = 0; i < parametri.size(); i++) {
                stmt.setObject(i + 1, parametri.get(i));
            }

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                String nomeCorso = rs.getString("nomecorso");
                String nomeAteneo = rs.getString("nome_ateneo");
                risultati.add(nomeCorso + " - " + nomeAteneo);
            }

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Errore durante il recupero dei risultati della ricerca", e);
        }

        return risultati;
    }




}

