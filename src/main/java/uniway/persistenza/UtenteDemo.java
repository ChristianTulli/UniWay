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
                return;
            }
        }
    }

    @Override
    public void aggiungiPreferitiUtente(String username, Integer idPreferiti) {
        for (Utente u : utenti) {
            if (u.getUsername().equals(username) && u instanceof UtenteInCerca inCerca) {
                List<Integer> preferenze = inCerca.getPreferenze();
                if (!preferenze.contains(idPreferiti)) {
                    preferenze.add(idPreferiti);
                }
                return;
            }
        }
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
                return;
            }
        }
    }
}

