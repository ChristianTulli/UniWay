package uniway.persistenza;

import uniway.model.Corso;
import uniway.model.Utente;

import java.util.List;

//ABSTRACT PRODUCT
public interface UtenteDAO {
     void salvaUtente(Utente utente);

     List<Utente> ottieniUtenti();

     Utente trovaDaUsername(String username);

     void aggiungiCorsoUtente(Utente utente, Corso corso);

     Boolean aggiungiPreferitiUtente(String username, Integer idPreferiti);

     List <Integer> getPreferitiUtente(String username);

     void aggiungiCurriculumUtente(String username, String curriculum);

     void rimuoviPreferitoUtente(String username, int idCorso);

}
