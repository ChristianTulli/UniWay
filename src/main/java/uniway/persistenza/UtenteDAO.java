package uniway.persistenza;

import uniway.model.Utente;

import java.util.List;

public interface UtenteDAO {
     void salvaUtente(Utente utente);

     List<Utente> ottieniUtenti();

     Utente trovaDaUsername(String username);

     void aggiungiCorsoUtente(String username, Integer idCorso);

     Boolean aggiungiPreferitiUtente(String username, Integer idPreferiti);

     List <Integer> getPreferitiUtente(String username);

     void aggiungiCurriculumUtente(String username, String curriculum);

     void rimuoviPreferitoUtente(String username, int idCorso);

}
