package uniway.viewcontroller;

import uniway.beans.RecensioneBean;
import uniway.beans.UtenteBean;
import uniway.controller.InCercaRecensioneController;

import java.util.List;
import java.util.Scanner;

public class InCercaRecensioneViewCLI {

    private final Scanner scanner = new Scanner(System.in);
    private final InCercaRecensioneController controller = new InCercaRecensioneController();

    public void show(Integer idInsegnamento, String nomeCorso, String nomeAteneo,
                     String nomeInsegnamento, String curriculum, UtenteBean utenteBean,
                     List<String> corsiSimili) {

        System.out.println("\n=== Recensioni Insegnamento ===");
        System.out.println("Corso:         " + nomeCorso);
        System.out.println("Ateneo:        " + nomeAteneo);
        System.out.println("Insegnamento:  " + nomeInsegnamento);
        System.out.println("Curriculum:    " + curriculum);

        List<RecensioneBean> recensioni = controller.getRecensioni(idInsegnamento);
        double media = controller.getMediaValutazioni(recensioni);
        System.out.printf("Media difficoltà:  %.1f / 5\n", media);

        if (recensioni.isEmpty()) {
            System.out.println("\n⚠ Nessuna recensione disponibile per questo insegnamento.");
        } else {
            System.out.println("\n📋 Recensioni degli studenti:\n");

            int i = 1;
            for (RecensioneBean r : recensioni) {
                System.out.printf("%d. Utente: %s | ⭐ %d/5\n", i++, r.getNome(), r.getValutazione());
                System.out.println("   Commento: " + r.getCommento());
                System.out.println("   Data:     " + r.getData());
                System.out.println("--------------------------------------------------");
            }
        }

        System.out.print("\nPremi INVIO per tornare alla schermata del corso...");
        scanner.nextLine();

        // Torna al dettaglio corso
        new InCercaDettaglioCorsoViewCLI().show(utenteBean, nomeCorso + " - " + nomeAteneo, corsiSimili);
    }
}
