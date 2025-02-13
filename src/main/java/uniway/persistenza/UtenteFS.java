package uniway.persistenza;

import uniway.model.Utente;
import uniway.model.UtenteIscritto;
import uniway.model.UtenteInCerca;

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
        List<Utente> utenti = ottieniUtenti();
        int nuovoId = utenti.isEmpty() ? 1 : utenti.get(utenti.size() - 1).getId() + 1;

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(path, true))) {
            StringBuilder sb = new StringBuilder();
            sb.append(nuovoId).append(",")
                    .append(utente.getUsername()).append(",")
                    .append(utente.getPassword()).append(",")
                    .append(utente.getIscritto()).append(",");

            if (utente instanceof UtenteIscritto iscritto) {
                sb.append(iscritto.getIdCorso() != null ? iscritto.getIdCorso() : "");
            } else if (utente instanceof UtenteInCerca inCerca) {
                sb.append(",").append(String.join(";", inCerca.getPreferenze()));
            }

            writer.write(sb.toString());
            writer.newLine();
        }
    }

    @Override
    public List<Utente> ottieniUtenti() throws IOException {
        List<Utente> utenti = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(path))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] split = line.split(",");

                if (split.length < 4) continue;

                int id = Integer.parseInt(split[0]);
                String username = split[1];
                String password = split[2];
                boolean iscritto = Boolean.parseBoolean(split[3]);

                if (iscritto) {
                    Integer idCorso = split.length > 4 && !split[4].isEmpty() ? Integer.parseInt(split[4]) : null;
                    utenti.add(new UtenteIscritto(id, username, password, true, idCorso));
                } else {
                    List<String> preferenze = split.length > 5 ? List.of(split[5].split(";")) : new ArrayList<>();
                    utenti.add(new UtenteInCerca(id, username, password, false, preferenze));
                }
            }
        } catch (IOException e) {
            throw new IOException("Errore nella lettura del file utenti", e);
        }
        return utenti;
    }

    @Override
    public void aggiungiCorsoUtente(String username, int idCorso) throws IOException {
        List<String> righe = new ArrayList<>();
        boolean utenteTrovato = false;

        try (BufferedReader reader = new BufferedReader(new FileReader(path))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] split = line.split(",");

                if (split.length >= 4 && split[1].equals(username)) { // Controllo se è l'utente giusto
                    utenteTrovato = true;

                    // Se l'utente è iscritto, aggiorno o aggiungo l'idCorso
                    if (split[3].equals("true")) {
                        StringBuilder sb = new StringBuilder();
                        sb.append(split[0]).append(",")  // ID
                                .append(split[1]).append(",")  // Username
                                .append(split[2]).append(",")  // Password
                                .append(split[3]).append(","); // Iscritto

                        if (split.length == 4) {
                            // L'utente non ha ancora un idCorso, lo aggiungo
                            sb.append(idCorso);
                        } else {
                            // L'utente ha già una colonna idCorso, la aggiorno
                            sb.append(idCorso);
                        }

                        line = sb.toString(); // Converte il contenuto aggiornato in stringa
                    }
                }

                righe.add(line);
            }
        }

        if (!utenteTrovato) {
            throw new IOException("Utente non trovato nel file.");
        }

        // Sovrascrivo il file con le righe aggiornate
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(path))) {
            for (String riga : righe) {
                writer.write(riga);
                writer.newLine();
            }
        }
    }

}
