package uniway.persistenza;

import uniway.model.Utente;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class UtenteFS implements UtenteDAO {
    private final String path;

    public UtenteFS(String path) {
        this.path = path;
    }

    @Override
    public void salvaUtente(Utente utente) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(path, true))) {
            writer.write(utente.getUsername() + "," + utente.getPassword());
            writer.newLine();
        } catch (IOException e) {
            throw new IOException(e);
        }
    }

    @Override
    public List<Utente> ottieniUtenti() throws IOException, IllegalArgumentException {
        List<Utente> utenti = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(path))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] split = line.split(",");
                utenti.add(new Utente(split[0], split[1]));
            }
        }catch (IOException e) {
            throw new IOException("File non trovato: " + e);
        } catch (RuntimeException e) {
            throw new IllegalArgumentException("Errore di lettura: " + e);
        }
        return utenti;
    }
}
