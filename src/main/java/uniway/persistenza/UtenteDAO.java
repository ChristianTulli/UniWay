package uniway.persistenza;

import uniway.model.Utente;

import java.util.List;

public interface UtenteDAO {
     void salvaUtente(Utente utente) throws Exception;

     List<Utente> ottieniUtenti() throws Exception;
}
