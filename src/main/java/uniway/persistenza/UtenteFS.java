package uniway.persistenza;

import uniway.eccezioni.CorsoGiaPresenteTraIPreferitiException;
import uniway.model.Corso;
import uniway.model.Utente;
import uniway.model.UtenteInCerca;
import uniway.model.UtenteIscritto;

import java.io.*;
import java.util.*;

public class UtenteFS implements UtenteDAO {

    // === Costanti per evitare duplicazioni di literal ===
    private static final String MSG_UTENTE_NULLO = "utente nullo";
    private static final String MSG_CORSO_NULLO  = "corso nullo";
    private static final String MSG_ERR_LETTURA  = "Errore lettura file";
    private static final String MSG_ERR_SCRITTURA= "Errore scrittura file";
    private static final String MSG_ERR_UTENTI   = "Errore lettura file utenti";

    private final String path;

    public UtenteFS(String path) {
        this.path = Objects.requireNonNull(path, "path nullo");
    }

    // ----------------- API -----------------

    @Override
    public void salvaUtente(Utente utente) {
        Objects.requireNonNull(utente, MSG_UTENTE_NULLO);
        // preveniamo duplicati (username unico)
        if (trovaDaUsername(utente.getUsername()) != null) {
            throw new IllegalArgumentException("Username già esistente: " + utente.getUsername());
        }
        appendLine(serializeUserLine(utente));
    }

    @Override
    public Utente trovaDaUsername(String username) {
        Objects.requireNonNull(username, "username nullo");
        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] cols = splitCSV(line);
                if (cols.length >= 3 && username.equals(cols[0])) {
                    return parseUser(cols);
                }
            }
        } catch (FileNotFoundException e) {
            // file assente → nessun utente
            return null;
        } catch (IOException e) {
            throw new FilePersistenceException(MSG_ERR_UTENTI, e);
        }
        return null;
    }

    @Override
    public void aggiungiCorsoUtente(UtenteIscritto utente, Corso corso) {
        Objects.requireNonNull(utente, MSG_UTENTE_NULLO);
        Objects.requireNonNull(corso,  MSG_CORSO_NULLO);

        List<String> lines = readAll();
        int idx = -1;

        // Trova indice riga dell'utente iscritto (niente continue/break multipli)
        for (int i = 0; i < lines.size() && idx == -1; i++) {
            String[] cols = splitCSV(lines.get(i));
            if (cols.length >= 3) {
                String u = cols[0];
                boolean iscritto = Boolean.parseBoolean(cols[2]);
                if (u.equals(utente.getUsername()) && iscritto) {
                    idx = i;
                }
            }
        }

        if (idx != -1) {
            String[] cols = splitCSV(lines.get(idx));
            ensureCols(cols, 6);
            cols[3] = safe(corso.getNomeCorso());
            cols[4] = safe(corso.getAteneo());
            // preferiti (col 5) resta com’è
            lines.set(idx, joinCSV(cols));
        } else {
            // se non esiste, creiamo riga coerente con i dati
            String nuova = joinCSV(new String[]{
                    safe(utente.getUsername()),
                    safe(utente.getPassword()),
                    "true",
                    safe(corso.getNomeCorso()),
                    safe(corso.getAteneo()),
                    "" // preferiti vuoto
            });
            lines.add(nuova);
        }

        writeAll(lines);
    }

    @Override
    public void aggiungiPreferitiUtente(UtenteInCerca utente, Corso corso) {
        Objects.requireNonNull(utente, MSG_UTENTE_NULLO);
        Objects.requireNonNull(corso,  MSG_CORSO_NULLO);

        List<String> lines = readAll();
        final String key = buildPrefKey(corso); // "corso|ateneo"

        int idx = -1;
        for (int i = 0; i < lines.size() && idx == -1; i++) {
            String[] cols = splitCSV(lines.get(i));
            if (cols.length >= 3) {
                String u = cols[0];
                boolean iscritto = Boolean.parseBoolean(cols[2]);
                if (u.equals(utente.getUsername()) && !iscritto) {
                    idx = i;
                }
            }
        }

        if (idx != -1) {
            String[] cols = splitCSV(lines.get(idx));
            ensureCols(cols, 6);
            List<String> prefs = parsePreferiti(cols[5]);

            if (prefs.contains(key)) {
                throw new CorsoGiaPresenteTraIPreferitiException("Il corso è già presente tra i preferiti.");
            }

            prefs.add(key);
            cols[5] = serializePreferiti(prefs);
            lines.set(idx, joinCSV(cols));
        } else {
            // se non esiste, creiamo riga “in cerca” con quel preferito
            String nuova = joinCSV(new String[]{
                    safe(utente.getUsername()),
                    safe(utente.getPassword()),
                    "false",
                    "", // corsoNome
                    "", // ateneoNome
                    serializePreferiti(List.of(key))
            });
            lines.add(nuova);
        }

        writeAll(lines);
    }

    @Override
    public void rimuoviPreferitoUtente(UtenteInCerca utente, Corso corso) {
        Objects.requireNonNull(utente, MSG_UTENTE_NULLO);
        Objects.requireNonNull(corso,  MSG_CORSO_NULLO);

        List<String> lines = readAll();
        final String key = buildPrefKey(corso);

        int idx = -1;
        for (int i = 0; i < lines.size() && idx == -1; i++) {
            String[] cols = splitCSV(lines.get(i));
            if (cols.length >= 3) {
                String u = cols[0];
                boolean iscritto = Boolean.parseBoolean(cols[2]);
                if (u.equals(utente.getUsername()) && !iscritto) {
                    idx = i;
                }
            }
        }

        if (idx != -1) {
            String[] cols = splitCSV(lines.get(idx));
            ensureCols(cols, 6);
            List<String> prefs = parsePreferiti(cols[5]);
            boolean removed = prefs.remove(key);
            if (removed) {
                cols[5] = prefs.isEmpty() ? "" : serializePreferiti(prefs);
                lines.set(idx, joinCSV(cols));
                writeAll(lines);
            }
            // se non rimuove nulla, non facciamo nulla (comportamento tollerante)
        }
        // se utente non trovato, no-op
    }

    // ----------------- Helpers di parsing/IO -----------------

    private String[] splitCSV(String line) {
        // manteniamo colonne vuote in coda
        return line.split(",", -1);
    }

    private String joinCSV(String[] cols) {
        return String.join(",", cols);
    }

    private void ensureCols(String[] cols, int min) {
        if (cols.length < min) {
            String[] n = Arrays.copyOf(cols, min);
            for (int i = cols.length; i < min; i++) n[i] = "";
            System.arraycopy(n, 0, cols, 0, n.length);
        }
    }

    private String safe(String s) { return (s == null) ? "" : s; }

    private List<String> readAll() {
        List<String> out = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            String l;
            while ((l = br.readLine()) != null) out.add(l);
        } catch (FileNotFoundException e) {
            // file non esistente → lista vuota
        } catch (IOException e) {
            throw new FilePersistenceException(MSG_ERR_LETTURA, e);
        }
        return out;
    }

    private void writeAll(List<String> lines) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(path))) {
            for (String l : lines) {
                bw.write(l);
                bw.newLine();
            }
        } catch (IOException e) {
            throw new FilePersistenceException(MSG_ERR_SCRITTURA, e);
        }
    }

    private void appendLine(String line) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(path, true))) {
            bw.write(line);
            bw.newLine();
        } catch (IOException e) {
            throw new FilePersistenceException(MSG_ERR_SCRITTURA, e);
        }
    }

    private String serializeUserLine(Utente u) {
        String username = safe(u.getUsername());
        String password = safe(u.getPassword());
        String iscritto = String.valueOf(u.getIscritto());
        String corso = "";
        String ateneo = "";
        String preferiti = "";

        if (u instanceof UtenteIscritto ui && ui.getCorso() != null) {
            corso  = safe(ui.getCorso().getNomeCorso());
            ateneo = safe(ui.getCorso().getAteneo());
        } else if (u instanceof UtenteInCerca ic && ic.getPreferenze() != null) {
            // Stream.toList() → lista non modificata
            List<String> prefs = ic.getPreferenze().stream()
                    .filter(Objects::nonNull)
                    .map(this::buildPrefKey)
                    .toList();
            preferiti = serializePreferiti(prefs);
        }

        return joinCSV(new String[]{ username, password, iscritto, corso, ateneo, preferiti });
    }

    private Utente parseUser(String[] cols) {
        String username = cols[0];
        String password = cols[1];
        boolean iscritto = Boolean.parseBoolean(cols[2]);

        if (iscritto) {
            String corsoNome  = cols.length > 3 ? cols[3] : "";
            String ateneoNome = cols.length > 4 ? cols[4] : "";
            UtenteIscritto ui = new UtenteIscritto(username, password, true);
            if (!corsoNome.isBlank() || !ateneoNome.isBlank()) {
                Corso c = new Corso();
                c.setNomeCorso(corsoNome);
                c.setAteneo(ateneoNome);
                ui.setCorso(c);
            }
            return ui;
        } else {
            String prefStr = cols.length > 5 ? cols[5] : "";
            // Stream.toList() → lista non modificata
            List<Corso> preferiti = parsePreferiti(prefStr).stream()
                    .map(this::parseKeyToCorso)
                    .toList();
            return new UtenteInCerca(username, password, false, preferiti);
        }
    }

    private List<String> parsePreferiti(String pref) {
        if (pref == null || pref.isBlank()) return new ArrayList<>();
        String[] parts = pref.split(";");
        List<String> out = new ArrayList<>();
        for (String p : parts) {
            String t = p.trim();
            if (!t.isEmpty()) out.add(t);
        }
        return out;
    }

    private String serializePreferiti(List<String> prefs) {
        return String.join(";", prefs);
    }

    private String buildPrefKey(Corso c) {
        return safe(c.getNomeCorso()) + "|" + safe(c.getAteneo());
    }

    private Corso parseKeyToCorso(String key) {
        String nome = "";
        String ateneo = "";
        int sep = key.indexOf('|');
        if (sep >= 0) {
            nome = key.substring(0, sep);
            ateneo = key.substring(sep + 1);
        } else {
            // fallback: tutto come nome
            nome = key;
        }
        Corso c = new Corso();
        c.setNomeCorso(nome);
        c.setAteneo(ateneo);
        return c;
    }

    // === Eccezione dedicata alla persistenza su file ===
    static class FilePersistenceException extends RuntimeException {
        FilePersistenceException(String message, Throwable cause) { super(message, cause); }
    }
}


