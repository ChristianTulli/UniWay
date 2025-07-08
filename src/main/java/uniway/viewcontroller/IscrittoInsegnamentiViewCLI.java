package uniway.viewcontroller;

import uniway.beans.InsegnamentoBean;
import uniway.beans.UtenteBean;
import uniway.controller.IscrittoInsegnamentiController;

import java.util.List;
import java.util.Scanner;

public class IscrittoInsegnamentiViewCLI {

    private final Scanner scanner = new Scanner(System.in);
    private final IscrittoInsegnamentiController controller = new IscrittoInsegnamentiController();

    public void show(UtenteBean utenteBean) {
        System.out.println("\n=== Insegnamenti del Corso ===");

        // Stampa dettagli corso
        String nomeCorso = controller.getCorso(utenteBean.getIdCorso());
        String nomeAteneo = controller.getAteneo(utenteBean.getIdCorso());

        System.out.println("Corso:     " + nomeCorso);
        System.out.println("Curriculum:" + utenteBean.getCurriculum());
        System.out.println("Ateneo:    " + nomeAteneo);
        System.out.println("--------------------------------------");

        // Ottiene e mostra insegnamenti
        List<InsegnamentoBean> insegnamenti = controller.getInsegnamenti(
                utenteBean.getIdCorso(),
                utenteBean.getCurriculum(),
                utenteBean.getUsername()
        );

        if (insegnamenti == null || insegnamenti.isEmpty()) {
            System.out.println("⚠ Nessun insegnamento disponibile.");
            return;
        }

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

        System.out.println("\nSeleziona un insegnamento per lasciare una recensione (numero): ");
        try {
            int scelta = Integer.parseInt(scanner.nextLine());
            if (scelta < 1 || scelta > insegnamenti.size()) {
                System.out.println("Scelta non valida.");
                return;
            }

            InsegnamentoBean selezionato = insegnamenti.get(scelta - 1);

            if (selezionato.getValutazione() != null) {
                System.out.println("⚠ Hai già recensito questo insegnamento.");
                return;
            }

            // Passa al commento
            new IscrittoCommentaViewCLI().show(
                    utenteBean,
                    controller.getCorso(utenteBean.getIdCorso()),
                    controller.getAteneo(utenteBean.getIdCorso()),
                    selezionato,
                    controller
            );

        } catch (NumberFormatException e) {
            System.out.println("Input non valido.");
        }
    }
}

