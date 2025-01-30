package uniway.persistenza;

import uniway.model.Utente;

import java.io.IOException;
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
    public void salvaUtente(Utente utente) throws IOException {
        //vuoto perche' devo implementare il db
        throw new IOException("Not supported yet.");
    }

    @Override
    public List<Utente> ottieniUtenti() throws IOException, RuntimeException {
        //vuoto perche' devo implementare il db
        return new ArrayList<>();


    }
}
