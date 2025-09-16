package uniway.controller;

import uniway.eccezioni.CorsoGiaPresenteTraIPreferitiException;
import uniway.model.Corso;
import uniway.model.UtenteInCerca;
import uniway.patterns.SessioneControllerSingleton;
import uniway.persistenza.CorsoDAO;
import uniway.persistenza.UtenteDAO;

import java.util.ArrayList;
import java.util.List;

public class GestisciCorsiDiLaureaPreferitiController {
    private final CorsoDAO corsoDAO;
    private final UtenteDAO utenteDAO;

    public GestisciCorsiDiLaureaPreferitiController() {
        this.corsoDAO = SessioneControllerSingleton.getInstance().getCorsoDAO();
        this.utenteDAO = SessioneControllerSingleton.getInstance().getUtenteDAO();
    }

    public List<String> getPreferiti() {
        UtenteInCerca uc = (UtenteInCerca) SessioneControllerSingleton.getInstance().getCurrentUser();
        List<String> descrizioni = new ArrayList<>();

        for (Corso corso : uc.getPreferenze()) {
            String nomeCorso = corso.getNomeCorso();
            String nomeAteneo = corso.getAteneo();
            if (nomeCorso != null && nomeAteneo != null) {
                descrizioni.add(nomeCorso + " - " + nomeAteneo);
            }
        }
        return descrizioni;
    }

    public void aggiungiAiPreferiti(String corsoCorrente, String ateneoCorrente) throws CorsoGiaPresenteTraIPreferitiException {
        UtenteInCerca uc = (UtenteInCerca) SessioneControllerSingleton.getInstance().getCurrentUser();
        Corso c = corsoDAO.getCorsoByNomeAndAteneo(corsoCorrente, ateneoCorrente);
        utenteDAO.aggiungiPreferitiUtente(uc, c);
        uc.aggiungiPreferito(c);
    }

    public void rimuoviPreferito(String corso) {
        UtenteInCerca uc = (UtenteInCerca) SessioneControllerSingleton.getInstance().getCurrentUser();
        Corso c = corsoDAO.getCorsoByNomeAndAteneo(
                corso.split(" - ")[0],  // nome corso
                corso.split(" - ")[1] // nome ateneo
        );
        utenteDAO.rimuoviPreferitoUtente(uc, c);
        uc.rimuoviPreferito(c);
    }
}

