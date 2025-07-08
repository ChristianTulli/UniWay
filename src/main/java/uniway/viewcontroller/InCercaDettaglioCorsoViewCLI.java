package uniway.viewcontroller;

import uniway.beans.InsegnamentoBean;
import uniway.beans.UtenteBean;
import uniway.controller.InCercaDettaglioCorsoController;

import java.util.List;
import java.util.Scanner;

public class InCercaDettaglioCorsoViewCLI {

    private final Scanner scanner = new Scanner(System.in);
    private final InCercaDettaglioCorsoController controller = new InCercaDettaglioCorsoController();

    public void show(UtenteBean utenteBean, String corsoCompleto, List<String> corsiSimili) {
        System.out.println("\n=== Dettagli Corso ===");

        String[] dettagli = corsoCompleto.split(" - ");
        if (dettagli.length < 2) {
            System.out.println("⚠ Errore nel nome del corso.");
            return;
        }

        String nomeCorso = dettagli[0];
        String nomeAteneo = dettagli[1];

        System.out.println("Corso:   " + nomeCorso);
        System.out.println("Ateneo:  " + nomeAteneo);

        // Insegnamenti
        List<InsegnamentoBean> listaInsegnamenti = controller.getInsegnamenti(nomeCorso, nomeAteneo);

        if (listaInsegnamenti.isEmpty()) {
            System.out.println("⚠ Nessun insegnamento disponibile per questo corso.");
        } else {
            System.out.println("\nInsegnamenti:");
            int index = 1;
            for (InsegnamentoBean ins : listaInsegnamenti) {
                System.out.printf("%d. %-40s | Anno: %d | Sem: %d | CFU: %d | Curr: %s\n",
                        index++, ins.getNome(), ins.getAnno(), ins.getSemestre(), ins.getCfu(), ins.getCurriculum());
            }

            System.out.print("\n→ Vuoi vedere le recensioni di un insegnamento? Inserisci il numero (o 0 per saltare): ");
            try {
                int scelta = Integer.parseInt(scanner.nextLine());
                if (scelta >= 1 && scelta <= listaInsegnamenti.size()) {
                    InsegnamentoBean selezionato = listaInsegnamenti.get(scelta - 1);
                    Integer idInsegnamento = controller.getIdInsegnamento(selezionato.getNome(), nomeCorso, nomeAteneo);
                    new InCercaRecensioneViewCLI().show(idInsegnamento, nomeCorso, nomeAteneo, selezionato.getNome(), selezionato.getCurriculum(), utenteBean, corsiSimili);
                    return;
                }
            } catch (NumberFormatException ignored) {}
        }

        // Preferiti
        System.out.print("\n→ Aggiungere questo corso ai preferiti? [s/N]: ");
        String risposta = scanner.nextLine().trim().toLowerCase();
        if (risposta.equals("s")) {
            boolean esito = controller.aggiungiAiPreferiti(utenteBean, nomeCorso, nomeAteneo);
            System.out.println(esito
                    ? "✅ Corso aggiunto ai preferiti con successo."
                    : "⚠ Corso già presente nei preferiti.");
        }

        // Corsi simili
        System.out.println("\nCorsi simili:");
        int j = 1;
        for (String simile : corsiSimili) {
            if (!simile.equals(corsoCompleto)) {
                System.out.println(j++ + ". " + simile);
            }
        }

        if (j == 1) {
            System.out.println("⚠ Nessun corso simile disponibile.");
            return;
        }

        System.out.print("\n→ Vuoi confrontare con un corso simile? Inserisci il numero (o 0 per uscire): ");
        try {
            int sceltaSimile = Integer.parseInt(scanner.nextLine());
            List<String> corsiEffettivi = corsiSimili.stream().filter(c -> !c.equals(corsoCompleto)).toList();
            if (sceltaSimile >= 1 && sceltaSimile <= corsiEffettivi.size()) {
                String corsoSimile = corsiEffettivi.get(sceltaSimile - 1);
                new InCercaConfrontaCorsoViewCLI().show(utenteBean, corsoCompleto, corsoSimile, corsiSimili);
            }
        } catch (NumberFormatException ignored) {}
    }
}
