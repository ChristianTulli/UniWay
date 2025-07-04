package uniway.persistenza;

import uniway.controller.PersistenzaController;

import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CorsoDAO {
    private final Connection conn;
    private static final Logger LOGGER = Logger.getLogger(CorsoDAO.class.getName());
    private final String eccezione = "problema nella comunicazione col database";
    private final String nomecorso = "nomecorso";

    public CorsoDAO() {
        this.conn = PersistenzaController.getInstance().getConnessione();
    }

    public List<String> getAllRegioni() {
        List<String> regioni = new ArrayList<>();
        String query = "SELECT DISTINCT regionecorso FROM corsi";

        try (PreparedStatement stmt = conn.prepareStatement(query);
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

        try (PreparedStatement stmt = conn.prepareStatement(query);
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

        try (PreparedStatement stmt = conn.prepareStatement(query)) {
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

        try (PreparedStatement stmt = conn.prepareStatement(query)) {
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
        String query = """
            SELECT DISTINCT a.nome 
            FROM atenei a 
            JOIN corsi c ON a.id = c.idAteneo 
            WHERE c.sedecomune = ?
        """;

        try (PreparedStatement stmt = conn.prepareStatement(query)) {
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
        String query = """
            SELECT DISTINCT c.gruppodisciplinare 
            FROM corsi c 
            JOIN atenei a ON c.idateneo = a.id 
            WHERE a.nome = ? AND c.sedecomune = ?
        """;

        try (PreparedStatement stmt = conn.prepareStatement(query)) {
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

        try (PreparedStatement stmt = conn.prepareStatement(query)) {
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
        String query = """
            SELECT DISTINCT c.nomeclasse 
            FROM corsi c 
            JOIN atenei a ON c.idateneo = a.id 
            WHERE c.sedecomune = ? 
            AND a.nome = ? 
            AND c.gruppodisciplinare = ? 
            AND c.durata = ?
        """;

        try (PreparedStatement stmt = conn.prepareStatement(query)) {
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
        String query = """
            SELECT DISTINCT c.nomecorso 
            FROM corsi c 
            JOIN atenei a ON c.idateneo = a.id 
            WHERE c.sedecomune = ? 
            AND a.nome = ? 
            AND c.gruppodisciplinare = ? 
            AND c.durata = ? 
            AND c.nomeclasse = ?
        """;

        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, comune);
            stmt.setString(2, ateneo);
            stmt.setString(3, disciplina);
            stmt.setString(4, tipologia);
            stmt.setString(5, nomeclasse);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                risultati.add(rs.getString(nomecorso));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, eccezione, e);
        }

        return risultati;
    }

    public Integer getIdCorsoByNome(String comune, String ateneo, String tipologia, String nomecorso) {
        String query = """
            SELECT c.id 
            FROM corsi c 
            JOIN atenei a ON c.idateneo = a.id 
            WHERE c.sedecomune = ? 
            AND a.nome = ? 
            AND c.durata = ? 
            AND c.nomecorso = ?
        """;

        try (PreparedStatement stmt = conn.prepareStatement(query)) {
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

        return null;
    }

    public Integer getIdCorsoByNomeAndAteneo(String ateneo, String nomecorso) {
        String query = """
            SELECT c.id 
            FROM corsi c 
            JOIN atenei a ON c.idateneo = a.id 
            WHERE a.nome = ? 
            AND c.nomecorso = ?
        """;

        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, ateneo);
            stmt.setString(2, nomecorso);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt("id");
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, eccezione, e);
        }

        return null;
    }

    public List<String> getDisciplineByDurata(String durata) {
        List<String> discipline = new ArrayList<>();
        String query = "SELECT DISTINCT gruppodisciplinare FROM corsi WHERE durata = ?";

        try (PreparedStatement stmt = conn.prepareStatement(query)) {
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

        try (PreparedStatement stmt = conn.prepareStatement(query)) {
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

    public List<String> getRisultatiRicerca(List<String> filtri) {
        List<String> risultati = new ArrayList<>();
        StringBuilder query = new StringBuilder("""
            SELECT c.nomecorso, a.nome AS nome_ateneo 
            FROM corsi c 
            JOIN atenei a ON c.idateneo = a.id 
        """);

        String[] colonne = {"a.statale", "a.tipologia", "c.regionecorso", "c.sedeprovincia",
                "c.sedecomune", "c.durata", "c.gruppodisciplinare", "c.nomeclasse"};

        List<String> condizioni = new ArrayList<>();
        List<String> parametri = new ArrayList<>();

        for (int i = 0; i < filtri.size(); i++) {
            if (filtri.get(i) != null && !filtri.get(i).isBlank()) {
                condizioni.add(colonne[i] + " = ?");
                parametri.add(filtri.get(i));
            }
        }

        if (!condizioni.isEmpty()) {
            query.append(" WHERE ").append(String.join(" AND ", condizioni));
        }

        try (PreparedStatement stmt = conn.prepareStatement(query.toString())) {
            for (int i = 0; i < parametri.size(); i++) {
                stmt.setString(i + 1, parametri.get(i));
            }

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                String corso = rs.getString("nomecorso");
                String ateneo = rs.getString("nome_ateneo");
                risultati.add(corso + " - " + ateneo);
            }

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Errore durante il recupero dei risultati della ricerca", e);
        }

        return risultati;
    }

    public List<String> getCurriculum(Integer idCorso) {
        List<String> curriculum = new ArrayList<>();
        String query = "SELECT curriculum FROM corsi WHERE id = ?";

        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, idCorso);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String raw = rs.getString("curriculum");
                if (raw != null && !raw.isBlank()) {
                    curriculum = Arrays.stream(raw.split(",\\s*"))
                            .map(String::trim)
                            .toList();
                }
            }

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Errore durante il recupero del curriculum", e);
        }

        return curriculum;
    }

    public String getNomeByIdCorso(Integer idCorso) {
        String query = "SELECT nomecorso FROM corsi WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, idCorso);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getString(nomecorso);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Errore nella query getNomeByIdCorso", e);
        }

        return null;
    }

    public String getAteneoByIdCorso(Integer idCorso) {
        String query = """
            SELECT a.nome 
            FROM atenei a 
            JOIN corsi c ON a.id = c.idateneo 
            WHERE c.id = ?
        """;

        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, idCorso);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getString("nome");
            }

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Errore nel recupero del nome dell'ateneo", e);
        }

        return null;
    }
}

