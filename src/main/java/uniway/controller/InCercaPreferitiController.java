package uniway.controller;

import java.io.IOException;
import java.util.List;

public class InCercaPreferitiController {
    private final PersistenzaController persistenzaController = PersistenzaController.getInstance();

    public List<String> getPreferiti(String username) throws IOException {
        return persistenzaController.getUtenteDAO().getPreferitiUtente(username);
    }
}
