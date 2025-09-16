package uniway.persistenza;

import uniway.model.Corso;
import uniway.model.Utente;
import uniway.model.UtenteInCerca;
import uniway.model.UtenteIscritto;


//ABSTRACT PRODUCT
public interface UtenteDAO {
     void salvaUtente(Utente utente);

     Utente trovaDaUsername(String username);

     void aggiungiCorsoUtente(UtenteIscritto utente, Corso corso);

     void aggiungiPreferitiUtente(UtenteInCerca utente, Corso corso);

     void rimuoviPreferitoUtente(UtenteInCerca utente, Corso corso);

}
