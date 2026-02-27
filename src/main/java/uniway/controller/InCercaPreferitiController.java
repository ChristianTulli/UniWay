package uniway.controller;

import uniway.persistenza.CorsoDAO;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class InCercaPreferitiController {

    private final PersistenzaController persistenzaController = PersistenzaController.getInstance();
    private final CorsoDAO corsoDAO = new CorsoDAO();

    public List<String> getPreferiti(String username) throws IOException {
        List<Integer> idPreferiti = persistenzaController.getUtenteDAO().getPreferitiUtente(username);
        List<String> descrizioni = new ArrayList<>();

        for (Integer id : idPreferiti) {
            String nomeCorso = corsoDAO.getNomeByIdCorso(id);
            String nomeAteneo = corsoDAO.getAteneoByIdCorso(id);
            if (nomeCorso != null && nomeAteneo != null) {
                descrizioni.add(nomeCorso + " - " + nomeAteneo);
            }
        }

        return descrizioni;
    }

    public void rimuoviPreferito(String username, String corso) throws IOException {
        int idCorso = corsoDAO.getIdCorsoByNomeAndAteneo(
                corso.split(" - ")[1], // nome ateneo
                corso.split(" - ")[0]  // nome corso
        );
        persistenzaController.getUtenteDAO().rimuoviPreferitoUtente(username, idCorso);
    }

}

