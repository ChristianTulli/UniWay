package uniway.persistenza;

import uniway.model.Utente;

import java.util.ArrayList;
import java.util.List;

public class UtenteDB implements UtenteDAO {
    private final String url;
    private final String username;
    private final String password;

    public UtenteDB(String url, String username, String password) {
        this.url = url;
        this.username = username;
        this.password = password;
    }

    @Override
    public void salvaUtente(Utente utente) throws Exception {
        //vuoto perche' devo implementare il db
    }

    @Override
    public List<Utente> ottieniUtenti() throws Exception {
        return new ArrayList<>();
    }
}
