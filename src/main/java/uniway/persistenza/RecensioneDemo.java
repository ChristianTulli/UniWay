package uniway.persistenza;

import uniway.eccezioni.RecensioneNonSalvataException;
import uniway.model.Insegnamento;
import uniway.model.Recensione;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

//PRODUCT
//Simula l’ID dell’insegnamento con una chiave deterministica calcolata dai campi dell'oggetto.
public class RecensioneDemo implements RecensioneDAO {

    // idInsegnamentoSynth -> (username -> Recensione)
    private final Map<Integer, Map<String, Recensione>> recensioni = new ConcurrentHashMap<>();

    // Hash deterministico dei campi
    private int synthId(Insegnamento i) {
        String nome       = i.getNome() != null ? i.getNome() : "";
        Integer anno      = i.getAnno() != null ? i.getAnno() : 0;
        Integer semestre  = i.getSemestre() != null ? i.getSemestre() : 0;
        Integer cfu       = i.getCfu() != null ? i.getCfu() : 0;
        return Objects.hash(nome, anno, semestre, cfu);
    }

    @Override
    public Integer getValutazioneUtente(Insegnamento insegnamento, String usernameUtente) {
        int id = synthId(insegnamento);
        Recensione rec = recensioni.getOrDefault(id, Map.of()).get(usernameUtente);
        return rec != null ? rec.getValutazione() : null;
    }

    @Override
    public void setRecensione(Recensione recensione) throws RecensioneNonSalvataException {
        Objects.requireNonNull(recensione, "recensione nulla");
        Objects.requireNonNull(recensione.getInsegnamento(), "insegnamento nullo");
        Objects.requireNonNull(recensione.getNomeUtente(), "nome utente nullo");

        int id = synthId(recensione.getInsegnamento());
        recensioni
                .computeIfAbsent(id, k -> new ConcurrentHashMap<>())
                .put(recensione.getNomeUtente(), recensione);
    }

    @Override
    public List<Recensione> getRecensioniByInsegnamento(Insegnamento insegnamento) {
        if (insegnamento == null) return List.of();
        int id = synthId(insegnamento);
        Map<String, Recensione> m = recensioni.get(id);
        if (m == null || m.isEmpty()) return List.of();
        return new ArrayList<>(m.values());
    }
}
