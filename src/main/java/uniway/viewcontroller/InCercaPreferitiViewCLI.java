package uniway.viewcontroller;

import uniway.beans.UtenteBean;
import uniway.controller.InCercaPreferitiController;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class InCercaPreferitiViewCLI {

    private final Scanner scanner = new Scanner(System.in);
    private final InCercaPreferitiController controller = new InCercaPreferitiController();

    public void show(UtenteBean utenteBean) {
        System.out.println("\n=== I TUOI CORSI PREFERITI ===");

        List<String> preferiti;
        try {
            preferiti = controller.getPreferiti(utenteBean.getUsername());
        } catch (IOException e) {
            System.out.println("❌ Errore nel recupero dei preferiti.");
            return;
        }

        if (preferiti == null || preferiti.isEmpty()) {
            System.out.println("⚠ Non hai corsi nei preferiti.");
            return;
        }

        int i = 1;
        for (String corso : preferiti) {
            System.out.println(i++ + ". " + corso);
        }

        System.out.print("\nSeleziona un corso per visualizzarlo o rimuoverlo (numero), oppure 0 per tornare: ");
        try {
            int scelta = Integer.parseInt(scanner.nextLine());

            if (scelta == 0) {
                new InCercaTrovaCorsoViewCLI().show(utenteBean);
                return;
            }

            if (scelta < 1 || scelta > preferiti.size()) {
                System.out.println("Scelta non valida.");
                return;
            }

            String corsoSelezionato = preferiti.get(scelta - 1);
            mostraMenuAzione(utenteBean, corsoSelezionato, preferiti);

        } catch (NumberFormatException e) {
            System.out.println("⚠ Input non valido.");
        }
    }

    private void mostraMenuAzione(UtenteBean utenteBean, String corso, List<String> listaCompleta) {
        System.out.println("\nCorso selezionato: " + corso);
        System.out.println("1. Visualizza dettagli");
        System.out.println("2. Rimuovi dai preferiti");
        System.out.println("3. Annulla");

        System.out.print("Scelta: ");
        String scelta = scanner.nextLine();

        switch (scelta) {
            case "1" -> new InCercaDettaglioCorsoViewCLI().show(utenteBean, corso, new ArrayList<>());
            case "2" -> {
                try {
                    controller.rimuoviPreferito(utenteBean.getUsername(), corso);
                    System.out.println("✅ Corso rimosso dai preferiti.");
                } catch (IOException e) {
                    System.out.println("❌ Errore durante la rimozione.");
                }
                show(utenteBean); // Ricarica la lista aggiornata
            }
            case "3" -> show(utenteBean); // Ritorna alla lista senza fare nulla
            default -> {
                System.out.println("Scelta non valida.");
                mostraMenuAzione(utenteBean, corso, listaCompleta);
            }
        }
    }
}
