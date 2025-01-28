package uniway.persistenza;

import uniway.model.Utente;

import java.io.BufferedWriter;
import java.io.FileWriter;

public class UtenteFS implements UtenteDAO {
    private final String path;

    public UtenteFS(String path) {
        this.path = path;
    }

    @Override
    public void salvaUtente(Utente utente) throws Exception {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(path, true))) {
            writer.write(utente.getUsername() + "," + utente.getPassword());
            writer.newLine();
        }
    }

    @Override
    public void autenticazioneUtente(Utente utente) throws Exception {

    }
}
