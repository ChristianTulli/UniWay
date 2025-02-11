package uniway.persistenza;

import uniway.model.Ateneo;

public class AteneoDAO {
    private final String url;
    private final String username;
    private final String password;

    public AteneoDAO(String url, String username, String password) {
        this.url = url;
        this.username = username;
        this.password = password;
    }

}
