package uniway.viewcontroller;

import uniway.beans.InsegnamentoBean;
import uniway.beans.UtenteBean;
import uniway.controller.IscrittoCommentaController;
import uniway.controller.IscrittoInsegnamentiController;

import java.util.Scanner;

public class IscrittoCommentaViewCLI {

    private final Scanner scanner = new Scanner(System.in);
    private final IscrittoCommentaController commentaController = new IscrittoCommentaController();

    public void show(UtenteBean utenteBean,
                     String nomeCorso,
                     String nomeAteneo,
                     InsegnamentoBean insegnamentoBean,
                     IscrittoInsegnamentiController iscrittoInsegnamentiController) {

        commentaController.setIscrittoVisualizzaInsegnamentiController(iscrittoInsegnamentiController);

        System.out.println("\n=== Recensione Insegnamento ===");
        System.out.println("Corso:       " + nomeCorso);
        System.out.println("Curriculum:  " + utenteBean.getCurriculum());
        System.out.println("Ateneo:      " + nomeAteneo);
        System.out.println("Insegnamento:" + insegnamentoBean.getNome());

        int valutazione = chiediValutazione();
        if (valutazione == -1) return;

        String commento = chiediCommento();
        if (commento.isBlank()) {
            System.out.println("⚠ Commento non valido.");
            return;
        }

        commentaController.salvaRecensione(utenteBean, insegnamentoBean, commento, valutazione);

        System.out.println("\n✅ Recensione salvata con successo!");
        System.out.println("→ Torno alla lista degli insegnamenti...\n");

        // Torna alla schermata CLI precedente
        new IscrittoInsegnamentiViewCLI().show(utenteBean);
    }

    private int chiediValutazione() {
        System.out.println("\nDifficoltà percepita (1–5 stelle): ");
        System.out.println("[1] ★☆☆☆☆");
        System.out.println("[2] ★★☆☆☆");
        System.out.println("[3] ★★★☆☆");
        System.out.println("[4] ★★★★☆");
        System.out.println("[5] ★★★★★");

        System.out.print("Valutazione: ");
        try {
            int valutazione = Integer.parseInt(scanner.nextLine());
            if (valutazione >= 1 && valutazione <= 5) {
                return valutazione;
            }
        } catch (NumberFormatException ignored) {}

        System.out.println("⚠ Valutazione non valida.");
        return -1;
    }

    private String chiediCommento() {
        System.out.println("\nLascia un commento sull'insegnamento:");
        return scanner.nextLine().trim();
    }
}
