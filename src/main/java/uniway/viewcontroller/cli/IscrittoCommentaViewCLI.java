package uniway.viewcontroller.cli;

import uniway.beans.InsegnamentoBean;
import uniway.beans.UtenteBean;
import uniway.controller.IscrittoCommentaController;
import uniway.controller.IscrittoInsegnamentiController;
import uniway.eccezioni.EsciException;
import uniway.eccezioni.TornaAlLoginException;
import uniway.utils.CLIUtils;

import java.util.Scanner;

public class IscrittoCommentaViewCLI {

    private final Scanner scanner = new Scanner(System.in);
    private final IscrittoCommentaController commentaController = new IscrittoCommentaController();

    public void show(UtenteBean utenteBean,
                     String nomeCorso,
                     String nomeAteneo,
                     InsegnamentoBean insegnamentoBean,
                     IscrittoInsegnamentiController iscrittoInsegnamentiController) {

        // Salva il controller per gestire il salvataggio della recensione
        commentaController.setIscrittoVisualizzaInsegnamentiController(iscrittoInsegnamentiController);

        // Intestazione schermata
        System.out.println("\n=== Recensione Insegnamento ===");
        System.out.println("Corso:       " + nomeCorso);
        System.out.println("Curriculum:  " + utenteBean.getCurriculum());
        System.out.println("Ateneo:      " + nomeAteneo);
        System.out.println("Insegnamento:" + insegnamentoBean.getNome());

        // Chiede la valutazione (da 1 a 5)
        int valutazione = chiediValutazione();
        if (valutazione == -1) return;

        // Chiede il commento dell'utente
        String commento = chiediCommento();
        if (commento.isBlank()) {
            System.out.println("Commento non valido.");
            return;
        }

        // Salva la recensione
        commentaController.salvaRecensione(utenteBean, insegnamentoBean, commento, valutazione);

        // Conferma e torna alla schermata precedente
        System.out.println("\nRecensione salvata con successo.");
        System.out.println("Torno alla lista degli insegnamenti...\n");

        new IscrittoInsegnamentiViewCLI().show(utenteBean);
    }

    private int chiediValutazione() {
        System.out.println("\nDifficoltà percepita (1–5 stelle): ");
        System.out.println("[1] ★☆☆☆☆");
        System.out.println("[2] ★★☆☆☆");
        System.out.println("[3] ★★★☆☆");
        System.out.println("[4] ★★★★☆");
        System.out.println("[5] ★★★★★");

        try {
            String input = CLIUtils.leggiInput(scanner, "Valutazione: ");
            int valutazione = Integer.parseInt(input);
            if (valutazione >= 1 && valutazione <= 5) {
                return valutazione;
            }
        } catch (NumberFormatException e) {
            System.out.println("Valutazione non valida.");
        } catch (TornaAlLoginException | EsciException e) {
            throw e; // Propaga l'eccezione
        }

        return -1;
    }

    private String chiediCommento() throws TornaAlLoginException, EsciException{
         return CLIUtils.leggiInput(scanner, "\nLascia un commento sull'insegnamento:\nCommento: ");
    }
}
