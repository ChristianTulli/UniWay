package uniway.persistenza;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CorsoDAO {
    private final String url;
    private final String username;
    private final String password;

    public CorsoDAO(String url, String username, String password) {
        this.url = url;
        this.username = username;
        this.password = password;
    }

}
