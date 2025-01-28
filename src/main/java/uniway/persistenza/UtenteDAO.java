package uniway.persistenza;

import uniway.model.Utente;

public interface UtenteDAO {
     void salvaUtente(Utente utente) throws Exception;
     void autenticazioneUtente(Utente utente) throws Exception;
}
