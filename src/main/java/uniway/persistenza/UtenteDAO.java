package uniway.persistenza;

import uniway.model.Utente;

import java.io.IOException;

import java.util.List;

public interface UtenteDAO {
     void salvaUtente(Utente utente) throws IOException;

     List<Utente> ottieniUtenti() throws IOException, IllegalArgumentException;

     void aggiungiCorsoUtente(String username, Integer idCorso) throws IOException;

     void aggiungiPreferitiUtente(String username, Integer idPreferiti) throws IOException;

     List <String > getPreferitiUtente(String username) throws IOException;

     void aggiungiCurriculumUtente(String username, String curriculum) throws IOException;

}
