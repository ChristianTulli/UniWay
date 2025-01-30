package uniway.persistenza;

import uniway.model.Utente;

import java.io.IOException;
import java.util.List;

public interface UtenteDAO {
     void salvaUtente(Utente utente) throws IOException;

     List<Utente> ottieniUtenti() throws Exception;
}
