package uniway.persistenza;

import uniway.model.Utente;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public interface UtenteDAO {
     void salvaUtente(Utente utente) throws IOException;

     List<Utente> ottieniUtenti() throws IOException, IllegalArgumentException;

     void aggiungiCorsoUtente(String username, int idCorso) throws IOException;

}
