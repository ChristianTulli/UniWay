package uniway.controller;

import java.io.IOException;
import java.util.List;

public class InCercaPreferitiController {
    private final LogInController loginController = LogInController.getInstance();

    public List<String> getPreferiti(String username) throws IOException {
        return loginController.getUtenteDAO().getPreferitiUtente(username);
    }
}
