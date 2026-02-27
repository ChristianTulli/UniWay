package uniway.persistenza;

import uniway.model.Utente;

import java.io.IOException;

import java.util.List;

public interface UtenteDAO {
     void salvaUtente(Utente utente) throws IOException;

     List<Utente> ottieniUtenti() throws IOException, IllegalArgumentException;

     void aggiungiCorsoUtente(String username, Integer idCorso) throws IOException;

     Boolean aggiungiPreferitiUtente(String username, Integer idPreferiti) throws IOException;

     List <Integer> getPreferitiUtente(String username) throws IOException;

     void aggiungiCurriculumUtente(String username, String curriculum) throws IOException;

     void rimuoviPreferitoUtente(String username, int idCorso) throws IOException;

}
