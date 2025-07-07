package uniway.persistenza;

import uniway.model.Recensione;

import java.util.List;

public interface RecensioneDAO {
    Integer getValutazioneUtente(int idInsegnamento, String usernameUtente);

    void setRecesnione(String testo, Integer valutazione, String nomeUtente, Integer idInsegnamento);

    List<Recensione> getRecensioniByInsegnamento(Integer idInsegnamento);

}
