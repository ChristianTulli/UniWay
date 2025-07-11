package uniway.viewcontroller.cli;

import uniway.beans.UtenteBean;
import uniway.controller.InCercaTrovaCorsoController;
import uniway.eccezioni.EsciException;
import uniway.eccezioni.TornaAlLoginException;
import uniway.utils.CLIUtils;

import java.util.List;
import java.util.Scanner;

public class InCercaTrovaCorsoViewCLI {

    private final Scanner scanner = new Scanner(System.in);
    private final InCercaTrovaCorsoController controller = new InCercaTrovaCorsoController();
    private String sceltaNonValida="sceltaNonValida.";

    public void show(UtenteBean utenteBean) {
        while (true) {
            try {
                System.out.println("\n=== Benvenuto nella Ricerca Corsi ===");
                System.out.println("1. Ricerca Corso di Laurea");
                System.out.println("2. Vai alla lista Preferiti");
                System.out.println("3. Esci");
                System.out.println("Scrivi 'login' per tornare al login, oppure 'esci' per uscire.");

                String scelta = CLIUtils.leggiInput(scanner, "Scegli un'opzione: ");

                switch (scelta) {
                    case "1" -> avviaRicerca(utenteBean);
                    case "2" -> new InCercaPreferitiViewCLI().show(utenteBean);
                    case "3" -> throw new TornaAlLoginException();
                    default -> System.out.println(sceltaNonValida);
                }
            } catch (TornaAlLoginException e) {
                // Torna al login
                System.out.println("Torno al login...");
                throw e;
            } catch (EsciException e) {
                // Uscita globale
                System.out.println("Chiusura dell'applicazione...");
                throw e;
            }
        }
    }

    private void avviaRicerca(UtenteBean utenteBean) {
        System.out.println("\n=== Ricerca Corso di Laurea ===");

        try {
            // Colonna 1 – Tipologia Ateneo
            String statale = selezionaDaLista("Tipo di Ateneo (Statale/Privato)", controller.getTipiAteneo());
            if (statale == null) return;

            String tipologia = selezionaDaLista("Tipologia Ateneo", controller.getTipologie(statale));
            if (tipologia == null) return;
            controller.setTipologia(tipologia);

            // Colonna 2 – Ubicazione
            String regione = selezionaDaLista("Regione", controller.getRegioni());
            if (regione == null) return;

            String provincia = selezionaDaLista("Provincia", controller.getProvince(regione));
            if (provincia == null) return;

            String comune = selezionaDaLista("Comune", controller.getComuni(provincia));
            if (comune == null) return;
            controller.setComune(comune);

            // Colonna 3 – Caratteristiche del Corso
            String durata = selezionaDaLista("Durata del Corso", controller.getDurate());
            if (durata == null) return;

            String gruppo = selezionaDaLista("Gruppo Disciplina", controller.getDiscipline(durata));
            if (gruppo == null) return;

            String classe = selezionaDaLista("Classe del Corso", controller.getClassi(gruppo));
            if (classe == null) return;
            controller.setClasseCorso(classe);

            // Esecuzione della ricerca
            System.out.println("\n→ Ricerca dei corsi in corso...");
            List<String> risultati = controller.getRisultati();
            if (risultati == null || risultati.isEmpty()) {
                System.out.println("Nessun corso trovato con i filtri selezionati.");
                return;
            }

            System.out.println("\nCorsi trovati:");
            int index = 1;
            for (String corso : risultati) {
                System.out.println(index++ + ". " + corso);
            }

            String scelta = CLIUtils.leggiInput(scanner, "Seleziona un corso per visualizzare i dettagli (numero): ");
            int sceltaNum = Integer.parseInt(scelta);
            if (sceltaNum < 1 || sceltaNum > risultati.size()) {
                System.out.println(sceltaNonValida);
                return;
            }

            String corsoSelezionato = risultati.get(sceltaNum - 1);
            new InCercaDettaglioCorsoViewCLI().show(utenteBean, corsoSelezionato, risultati);

        } catch (NumberFormatException e) {
            System.out.println("Input non valido.");
        } catch (TornaAlLoginException | EsciException e) {
            throw e; // Propaga l'eccezione al chiamante
        }
    }

    private String selezionaDaLista(String descrizione, List<String> opzioni) {
        if (opzioni == null || opzioni.isEmpty()) {
            System.out.println("Nessuna opzione disponibile per: " + descrizione);
            return null;
        }

        System.out.println("\nSeleziona " + descrizione + ":");
        for (int i = 0; i < opzioni.size(); i++) {
            System.out.println((i + 1) + ". " + opzioni.get(i));
        }

        try {
            String input = CLIUtils.leggiInput(scanner, "Scelta: ");
            int scelta = Integer.parseInt(input);
            if (scelta >= 1 && scelta <= opzioni.size()) {
                return opzioni.get(scelta - 1);
            }
        } catch (NumberFormatException e) {
            System.out.println(sceltaNonValida);
        } catch (TornaAlLoginException | EsciException e) {
            throw e; // Propaga per la gestione globale
        }

        return null;
    }
}


