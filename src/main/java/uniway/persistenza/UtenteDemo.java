package uniway.persistenza;

import uniway.model.Utente;
import uniway.model.UtenteInCerca;
import uniway.model.UtenteIscritto;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class UtenteDemo implements UtenteDAO {

    private final List<Utente> utenti = new ArrayList<>();
    private final AtomicInteger nextId = new AtomicInteger(1);

    @Override
    public void salvaUtente(Utente utente) throws IOException {
        utente.setId(nextId.getAndIncrement());
        utenti.add(utente);
    }

    @Override
    public List<Utente> ottieniUtenti() {
        return new ArrayList<>(utenti); // copia difensiva
    }

    @Override
    public void aggiungiCorsoUtente(String username, Integer idCorso) {
        for (Utente u : utenti) {
            if (u.getUsername().equals(username) && u instanceof UtenteIscritto iscritto) {
                iscritto.setIdCorso(idCorso);
            }
        }
    }

    @Override
    public Boolean aggiungiPreferitiUtente(String usernameUtente, Integer idCorso) {
        for (Utente u : utenti) {
            if (u.getUsername().equals(usernameUtente) && u instanceof UtenteInCerca inCerca) {
                List<Integer> preferenze = inCerca.getPreferenze();
                if (!preferenze.contains(idCorso)) {
                    preferenze.add(idCorso);
                    return true;  // Aggiunto con successo
                } else {
                    return false; // Già presente
                }
            }
        }
        return false; // Utente non trovato o non compatibile
    }


    @Override
    public List<Integer> getPreferitiUtente(String username) {
        for (Utente u : utenti) {
            if (u.getUsername().equals(username) && u instanceof UtenteInCerca inCerca) {
                return new ArrayList<>(inCerca.getPreferenze());
            }
        }
        return new ArrayList<>();
    }

    @Override
    public void aggiungiCurriculumUtente(String username, String curriculum) {
        for (Utente u : utenti) {
            if (u.getUsername().equals(username) && u instanceof UtenteIscritto iscritto) {
                iscritto.setCurriculum(curriculum);
            }
        }
    }

    @Override
    public void rimuoviPreferitoUtente(String username, int idCorso) {
        for (Utente utente : utenti) {
            if (utente.getUsername().equals(username) && utente instanceof UtenteInCerca inCerca) {
                List<Integer> preferenze = inCerca.getPreferenze();
                if (preferenze.contains(idCorso)) {
                    preferenze.remove((Integer) idCorso);// rimuove l'id specifico
                }
            }
        }
    }

}

