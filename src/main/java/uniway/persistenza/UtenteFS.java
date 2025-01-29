package uniway.persistenza;

import uniway.model.Utente;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

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
    public List<Utente> ottieniUtenti() throws Exception {
        List<Utente> utenti = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(path))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] split = line.split(",");
                utenti.add(new Utente(split[0], split[1]));
            }
        }
        return utenti;
    }
}
