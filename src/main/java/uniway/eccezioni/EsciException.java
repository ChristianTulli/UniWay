package uniway.eccezioni;

public class EsciException extends RuntimeException {
    public EsciException() {
        super("Richiesta di uscita dall'app.");
    }
}
