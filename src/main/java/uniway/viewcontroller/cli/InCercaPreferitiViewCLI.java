package uniway.viewcontroller.cli;

import uniway.beans.UtenteBean;
import uniway.controller.InCercaPreferitiController;
import uniway.eccezioni.EsciException;
import uniway.eccezioni.TornaAlLoginException;
import uniway.utils.CLIUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class InCercaPreferitiViewCLI {

    private final Scanner scanner = new Scanner(System.in);
    private final InCercaPreferitiController controller = new InCercaPreferitiController();

    // Metodo principale che mostra la lista dei corsi preferiti
    public void show(UtenteBean utenteBean) {
        System.out.println("\n=== I TUOI CORSI PREFERITI ===");

        List<String> preferiti;
        try {
            preferiti = controller.getPreferiti(utenteBean.getUsername());
        } catch (IOException e) {
            System.out.println("Errore nel recupero dei preferiti.");
            return;
        }

        if (preferiti == null || preferiti.isEmpty()) {
            System.out.println("Non hai corsi nei preferiti.");
            return;
        }

        // Stampa i corsi preferiti
        int i = 1;
        for (String corso : preferiti) {
            System.out.println(i++ + ". " + corso);
        }

        // Chiede all'utente cosa fare
        try {
            String input = CLIUtils.leggiInput(scanner,
                    "\nSeleziona un corso per visualizzarlo o rimuoverlo (numero), INVIO per tornare indietro oppure scrivi 'login' o 'esci': ");
            int scelta = Integer.parseInt(input);

            if (scelta < 1 || scelta > preferiti.size()) {
                System.out.println("Scelta non valida.");
                return;
            }

            String corsoSelezionato = preferiti.get(scelta - 1);
            mostraMenuAzione(utenteBean, corsoSelezionato);

        } catch (NumberFormatException e) {
            System.out.println("Input numerico non valido.");
        } catch (TornaAlLoginException | EsciException e) {
            throw e; // Propaga per gestione globale
        }
    }

    // Mostra il menu di azioni per un singolo corso preferito
    private void mostraMenuAzione(UtenteBean utenteBean, String corso) {
        try {
            System.out.println("\nCorso selezionato: " + corso);
            System.out.println("1. Visualizza dettagli");
            System.out.println("2. Rimuovi dai preferiti");
            System.out.println("3. Annulla");

            String scelta = CLIUtils.leggiInput(scanner, "Scelta: ");

            switch (scelta) {
                case "1" -> {
                    new InCercaDettaglioCorsoViewCLI().show(utenteBean, corso, new ArrayList<>());
                }
                case "2" -> {
                    try {
                        controller.rimuoviPreferito(utenteBean.getUsername(), corso);
                        System.out.println("Corso rimosso dai preferiti.");
                    } catch (IOException e) {
                        System.out.println("Errore durante la rimozione.");
                    }
                    show(utenteBean); // Ricarica la lista aggiornata
                }
                case "3" -> show(utenteBean); // Ritorna alla lista senza fare nulla
                default -> {
                    System.out.println("Scelta non valida.");
                    mostraMenuAzione(utenteBean, corso); // Ripete il menu
                }
            }
        } catch (TornaAlLoginException | EsciException e) {
            throw e;
        }
    }
}

