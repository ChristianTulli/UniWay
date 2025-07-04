package uniway.persistenza;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class RecensioneDemo implements RecensioneDAO {

    private final Map<Integer, Map<String, Integer>> valutazioni = new ConcurrentHashMap<>();

    @Override
    public Integer getValutazioneUtente(int idInsegnamento, String usernameUtente) {
        return valutazioni.getOrDefault(idInsegnamento, Collections.emptyMap()).get(usernameUtente);
    }

    @Override
    public void setRecesnione(String testo, Integer valutazione, String nomeUtente, Integer idInsegnamento) {
        valutazioni
                .computeIfAbsent(idInsegnamento, k -> new HashMap<>())
                .put(nomeUtente, valutazione);
    }
}
