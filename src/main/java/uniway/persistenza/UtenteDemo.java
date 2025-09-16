package uniway.persistenza;

import uniway.eccezioni.CorsoGiaPresenteTraIPreferitiException;
import uniway.model.Corso;
import uniway.model.Utente;
import uniway.model.UtenteInCerca;
import uniway.model.UtenteIscritto;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

public class UtenteDemo implements UtenteDAO {

    private final List<Utente> utenti = new CopyOnWriteArrayList<>();
    private String utentenullo="utente nullo";
    private String corsonullo="corso nullo";

    @Override
    public void salvaUtente(Utente utente) {
        Objects.requireNonNull(utente, utentenullo);
        if (trovaDaUsername(utente.getUsername()) != null) {
            throw new IllegalArgumentException("Username già esistente: " + utente.getUsername());
        }
        utenti.add(utente);
    }

    @Override
    public Utente trovaDaUsername(String username) {
        for (Utente u : utenti) {
            if (u.getUsername().equals(username)) return u;
        }
        return null;
    }

    @Override
    public void aggiungiCorsoUtente(UtenteIscritto utente, Corso corso) {
        Objects.requireNonNull(utente, utentenullo);
        Objects.requireNonNull(corso, corsonullo);
        Utente u = trovaDaUsername(utente.getUsername());
        if (u instanceof UtenteIscritto ui) {
            ui.setCorso(corso);
        }
    }

    @Override
    public void aggiungiPreferitiUtente(UtenteInCerca utente, Corso corso) {
        Objects.requireNonNull(utente, utentenullo);
        Objects.requireNonNull(corso, corsonullo);

        Utente u = trovaDaUsername(utente.getUsername());
        if (!(u instanceof UtenteInCerca ic)) return;

        List<Corso> prefs = ic.getPreferenze();
        boolean exists = prefs.stream().anyMatch(p ->
                Objects.equals(p.getNomeCorso(), corso.getNomeCorso()) &&
                        Objects.equals(p.getAteneo(),corso.getAteneo()));
        if (exists) {
            throw new CorsoGiaPresenteTraIPreferitiException("Il corso è già presente tra i preferiti.");
        }
        prefs.add(corso);
    }

    @Override
    public void rimuoviPreferitoUtente(UtenteInCerca utente, Corso corso) {
        Objects.requireNonNull(utente, utentenullo);
        Objects.requireNonNull(corso, corsonullo);

        Utente u = trovaDaUsername(utente.getUsername());
        if (!(u instanceof UtenteInCerca ic)) return;

        ic.getPreferenze().removeIf(p ->
                Objects.equals(p.getNomeCorso(), corso.getNomeCorso()) &&
                        Objects.equals(p.getAteneo(),corso.getAteneo()));
    }

}


