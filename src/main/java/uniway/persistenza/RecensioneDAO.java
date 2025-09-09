package uniway.persistenza;

import uniway.model.Insegnamento;
import uniway.model.Recensione;

import java.util.List;

public interface RecensioneDAO {
    Integer getValutazioneUtente(Insegnamento insegnamento, String usernameUtente);

    void setRecesnione(Recensione recensione);

    List<Recensione> getRecensioniByInsegnamento(Integer idInsegnamento);

}
