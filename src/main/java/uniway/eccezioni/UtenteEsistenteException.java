package uniway.eccezioni;

public class UtenteEsistenteException extends RuntimeException {
    public UtenteEsistenteException(String message) {
        super(message);
    }
}
