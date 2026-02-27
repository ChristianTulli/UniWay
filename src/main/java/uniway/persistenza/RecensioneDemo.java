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
    public void setRecesnione(Recensione recensione) {
        recensioni
                .computeIfAbsent(recensione.getIdInsegnamento(), k -> new HashMap<>())
                .put(recensione.getNomeUtente(), recensione);
    }

    @Override
    public List<Recensione> getRecensioniByInsegnamento(Integer idInsegnamento) {
        return new ArrayList<>(
                recensioni.getOrDefault(idInsegnamento, Collections.emptyMap()).values()
        );
    }
}
