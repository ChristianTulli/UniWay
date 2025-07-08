package uniway.viewcontroller.cli;

import uniway.beans.InsegnamentoBean;
import uniway.beans.UtenteBean;
import uniway.controller.InCercaDettaglioCorsoController;
import uniway.eccezioni.EsciException;
import uniway.eccezioni.TornaAlLoginException;
import uniway.utils.CLIUtils;

import java.util.List;
import java.util.Scanner;

public class InCercaDettaglioCorsoViewCLI {
    private final Scanner scanner = new Scanner(System.in);
    private final InCercaDettaglioCorsoController controller = new InCercaDettaglioCorsoController();

    public void show(UtenteBean utenteBean, String corsoCompleto, List<String> corsiSimili) {
        System.out.println("\n=== Dettagli Corso ===");
        String[] dettagli = corsoCompleto.split(" - ");
        if (dettagli.length < 2) {
            System.out.println("Errore nel nome del corso.");
            return;
        }

        String nomeCorso = dettagli[0];
        String nomeAteneo = dettagli[1];
        System.out.println("Corso:   " + nomeCorso);
        System.out.println("Ateneo:  " + nomeAteneo);

        List<InsegnamentoBean> listaInsegnamenti = controller.getInsegnamenti(nomeCorso, nomeAteneo);
        mostraInsegnamenti(listaInsegnamenti);

        gestisciSceltaInsegnamento(listaInsegnamenti, nomeCorso, nomeAteneo, utenteBean, corsiSimili);
        gestisciAggiuntaPreferiti(nomeCorso, nomeAteneo, utenteBean);
        gestisciCorsiSimili(corsoCompleto, corsiSimili, utenteBean);
    }

    private void mostraInsegnamenti(List<InsegnamentoBean> listaInsegnamenti) {
        if (listaInsegnamenti.isEmpty()) {
            System.out.println("Nessun insegnamento disponibile per questo corso.");
            return;
        }

        System.out.println("\nInsegnamenti:");
        int index = 1;
        for (InsegnamentoBean ins : listaInsegnamenti) {
            System.out.printf("%d. %-40s | Anno: %d | Sem: %d | CFU: %d | Curr: %s\n",
                    index++, ins.getNome(), ins.getAnno(), ins.getSemestre(), ins.getCfu(), ins.getCurriculum());
        }
    }

    private void gestisciSceltaInsegnamento(List<InsegnamentoBean> lista, String nomeCorso, String nomeAteneo,
                                            UtenteBean utenteBean, List<String> corsiSimili) {
        if (lista.isEmpty()) return;

        try {
            String input = CLIUtils.leggiInput(scanner, "\n→ Vuoi vedere le recensioni di un insegnamento? Inserisci il numero (0 per proseguire o 'login' / 'esci'): ");
            int scelta = Integer.parseInt(input);
            if (scelta >= 1 && scelta <= lista.size()) {
                InsegnamentoBean selezionato = lista.get(scelta - 1);
                Integer id = controller.getIdInsegnamento(selezionato.getNome(), nomeCorso, nomeAteneo);
                new InCercaRecensioneViewCLI().show(id, nomeCorso, nomeAteneo,
                        selezionato.getNome(), selezionato.getCurriculum(), utenteBean, corsiSimili);
            }
        } catch (NumberFormatException ignored) {
            System.out.println("Input non valido.");
        }
    }

    private void gestisciAggiuntaPreferiti(String nomeCorso, String nomeAteneo, UtenteBean utenteBean) {
        try {
            String risposta = CLIUtils.leggiInput(scanner, "\n→ Aggiungere questo corso ai preferiti? [s/N]: ").toLowerCase();
            if (risposta.equals("s")) {
                boolean esito = controller.aggiungiAiPreferiti(utenteBean, nomeCorso, nomeAteneo);
                System.out.println(esito
                        ? "Corso aggiunto ai preferiti con successo."
                        : "Corso già presente nei preferiti.");
            }
        } catch (TornaAlLoginException | EsciException e) {
            throw e;
        }
    }

    private void gestisciCorsiSimili(String corsoCompleto, List<String> corsiSimili, UtenteBean utenteBean) {
        List<String> corsiEffettivi = corsiSimili.stream()
                .filter(c -> !c.equals(corsoCompleto))
                .toList();

        System.out.println("\nCorsi simili:");
        if (corsiEffettivi.isEmpty()) {
            System.out.println("Nessun corso simile disponibile.");
            return;
        }

        for (int i = 0; i < corsiEffettivi.size(); i++) {
            System.out.println((i + 1) + ". " + corsiEffettivi.get(i));
        }

        try {
            String input = CLIUtils.leggiInput(scanner, "\n→ Vuoi confrontare con un corso simile? Inserisci il numero (o 'login' / 'esci'): ");
            int scelta = Integer.parseInt(input);
            if (scelta >= 1 && scelta <= corsiEffettivi.size()) {
                String corsoSimile = corsiEffettivi.get(scelta - 1);
                new InCercaConfrontaCorsoViewCLI().show(utenteBean, corsoCompleto, corsoSimile, corsiSimili);
            }
        } catch (NumberFormatException ignored) {
            System.out.println("Input non valido.");
        }
    }
}
