package uniway.persistenza;

import uniway.eccezioni.RecensioneNonSalvataException;
import uniway.model.Insegnamento;
import uniway.model.Recensione;

import java.util.List;

//ABSTRACT PRODUCT
public interface RecensioneDAO {
    Integer getValutazioneUtente(Insegnamento insegnamento, String usernameUtente);

    void setRecensione(Recensione recensione) throws RecensioneNonSalvataException;

    List<Recensione> getRecensioniByInsegnamento(Insegnamento insegnamento);

}
