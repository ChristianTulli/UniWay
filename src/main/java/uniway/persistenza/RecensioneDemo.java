package uniway.persistenza;

import uniway.model.Recensione;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class RecensioneDemo implements RecensioneDAO {

    // Mappa: idInsegnamento -> (nomeUtente -> Recensione)
    private final Map<Integer, Map<String, Recensione>> recensioni = new ConcurrentHashMap<>();

    @Override
    public Integer getValutazioneUtente(int idInsegnamento, String usernameUtente) {
        Recensione rec = recensioni
                .getOrDefault(idInsegnamento, Collections.emptyMap())
                .get(usernameUtente);
        return rec != null ? rec.getValutazione() : null;
    }

    @Override
    public void setRecesnione(String testo, Integer valutazione, String nomeUtente, Integer idInsegnamento) {
        Recensione rec = new Recensione();
        rec.setCommento(testo);
        rec.setValutazione(valutazione);
        rec.setNomeUtente(nomeUtente);
        rec.setIdInsegnamento(idInsegnamento);

        recensioni
                .computeIfAbsent(idInsegnamento, k -> new HashMap<>())
                .put(nomeUtente, rec);
    }

    @Override
    public List<Recensione> getRecensioniByInsegnamento(Integer idInsegnamento) {
        return new ArrayList<>(
                recensioni.getOrDefault(idInsegnamento, Collections.emptyMap()).values()
        );
    }
}
