package uniway.viewcontroller.cli;

import uniway.beans.InsegnamentoBean;
import uniway.beans.UtenteBean;
import uniway.controller.IscrittoInsegnamentiController;
import uniway.eccezioni.EsciException;
import uniway.eccezioni.TornaAlLoginException;
import uniway.utils.CLIUtils;

import java.util.List;
import java.util.Scanner;

public class IscrittoInsegnamentiViewCLI {

    private final Scanner scanner = new Scanner(System.in);
    private final IscrittoInsegnamentiController controller = new IscrittoInsegnamentiController();

    public void show(UtenteBean utenteBean) {
        System.out.println("\n=== Insegnamenti del Corso ===");

        // Mostra i dati principali del corso selezionato
        String nomeCorso = controller.getCorso(utenteBean.getIdCorso());
        String nomeAteneo = controller.getAteneo(utenteBean.getIdCorso());

        System.out.println("Corso:     " + nomeCorso);
        System.out.println("Curriculum:" + utenteBean.getCurriculum());
        System.out.println("Ateneo:    " + nomeAteneo);
        System.out.println("--------------------------------------");

        // Recupera gli insegnamenti associati all'utente
        List<InsegnamentoBean> insegnamenti = controller.getInsegnamenti(
                utenteBean.getIdCorso(),
                utenteBean.getCurriculum(),
                utenteBean.getUsername()
        );

        // Se non ci sono insegnamenti, avvisa e termina
        if (insegnamenti == null || insegnamenti.isEmpty()) {
            System.out.println("Nessun insegnamento disponibile.");
            return;
        }

        // Stampa elenco numerato di insegnamenti
        int index = 1;
        for (InsegnamentoBean bean : insegnamenti) {
            System.out.printf("%d. %-40s | Anno: %d | Sem: %d | CFU: %d | Valutazione: %s\n",
                    index++,
                    bean.getNome(),
                    bean.getAnno(),
                    bean.getSemestre(),
                    bean.getCfu(),
                    (bean.getValutazione() != null ? bean.getValutazione() + "/5" : "non ancora")
            );
        }

        // Chiede all'utente quale insegnamento recensire
        System.out.println("\nSeleziona un insegnamento per lasciare una recensione.");
        System.out.println("Digita il numero corrispondente oppure 'login' o 'esci':");

        try {
            String input = CLIUtils.leggiInput(scanner, "Scelta: ");
            int scelta = Integer.parseInt(input);

            if (scelta < 1 || scelta > insegnamenti.size()) {
                System.out.println("Scelta non valida.");
                return;
            }

            InsegnamentoBean selezionato = insegnamenti.get(scelta - 1);

            if (selezionato.getValutazione() != null) {
                System.out.println("Hai già recensito questo insegnamento.");
                return;
            }

            // Passa alla schermata di inserimento recensione
            new IscrittoCommentaViewCLI().show(
                    utenteBean,
                    nomeCorso,
                    nomeAteneo,
                    selezionato,
                    controller
            );

        } catch (NumberFormatException e) {
            System.out.println("Input non valido. Inserisci un numero.");
        } catch (TornaAlLoginException | EsciException e) {
            // Propaga l'eccezione ai livelli superiori (LoginViewControllerCLI la gestirà)
            throw e;
        }
    }
}

