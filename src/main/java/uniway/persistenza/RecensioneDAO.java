package uniway.persistenza;

public interface RecensioneDAO {
    Integer getValutazioneUtente(int idInsegnamento, String usernameUtente);

    void setRecesnione(String testo, Integer valutazione, String nomeUtente, Integer idInsegnamento);


}
