package uniway.persistenza;

import uniway.model.Recensione;

import java.util.List;

public interface RecensioneDAO {
    Integer getValutazioneUtente(int idInsegnamento, String usernameUtente);

    void setRecesnione(Recensione recensione);

    List<Recensione> getRecensioniByInsegnamento(Integer idInsegnamento);

}
