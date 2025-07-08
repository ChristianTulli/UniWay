package uniway.viewcontroller.cli;

import uniway.beans.RecensioneBean;
import uniway.beans.UtenteBean;
import uniway.controller.InCercaRecensioneController;
import uniway.eccezioni.EsciException;
import uniway.eccezioni.TornaAlLoginException;
import uniway.viewcontroller.CLIUtils;

import java.util.List;
import java.util.Scanner;

public class InCercaRecensioneViewCLI {

    private final Scanner scanner = new Scanner(System.in);
    private final InCercaRecensioneController controller = new InCercaRecensioneController();

    public void show(Integer idInsegnamento,
                     String nomeCorso,
                     String nomeAteneo,
                     String nomeInsegnamento,
                     String curriculum,
                     UtenteBean utenteBean,
                     List<String> corsiSimili) {

        // Stampa intestazione e dettagli dell'insegnamento
        System.out.println("\n=== Recensioni Insegnamento ===");
        System.out.println("Corso:         " + nomeCorso);
        System.out.println("Ateneo:        " + nomeAteneo);
        System.out.println("Insegnamento:  " + nomeInsegnamento);
        System.out.println("Curriculum:    " + curriculum);

        // Recupera recensioni e calcola media
        List<RecensioneBean> recensioni = controller.getRecensioni(idInsegnamento);
        double media = controller.getMediaValutazioni(recensioni);
        System.out.printf("Media difficoltà:  %.1f / 5\n", media);

        // Stampa recensioni se presenti
        if (recensioni.isEmpty()) {
            System.out.println("\nNessuna recensione disponibile per questo insegnamento.");
        } else {
            System.out.println("\nRecensioni degli studenti:\n");

            int i = 1;
            for (RecensioneBean r : recensioni) {
                System.out.printf("%d. Utente: %s | ★ %d/5\n", i++, r.getNome(), r.getValutazione());
                System.out.println("   Commento: " + r.getCommento());
                System.out.println("   Data:     " + r.getData());
                System.out.println("--------------------------------------------------");
            }
        }

        // Attendi conferma per tornare alla schermata precedente
        try {
            CLIUtils.leggiInput(scanner, "\nPremi INVIO per tornare alla schermata del corso...");
            new InCercaDettaglioCorsoViewCLI().show(utenteBean, nomeCorso + " - " + nomeAteneo, corsiSimili);
        } catch (TornaAlLoginException | EsciException e) {
            throw e; // Propaga al chiamante
        }
    }
}

