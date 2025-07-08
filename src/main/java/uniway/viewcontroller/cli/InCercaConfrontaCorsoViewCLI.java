package uniway.viewcontroller.cli;

import uniway.beans.InsegnamentoBean;
import uniway.beans.UtenteBean;
import uniway.controller.InCercaConfrontaCorsoController;
import uniway.utils.CLIUtils;

import java.util.List;
import java.util.Scanner;

public class InCercaConfrontaCorsoViewCLI {

    private final InCercaConfrontaCorsoController controller = new InCercaConfrontaCorsoController();
    private final Scanner scanner = new Scanner(System.in);

    // Mostra il confronto tra due corsi (insegnamenti + azioni)
    public void show(UtenteBean utenteBean, String corsoStr1, String corsoStr2, List<String> corsiSimili) {
        System.out.println("\n=== CONFRONTO TRA CORSI ===");

        // Parsing dei nomi dei corsi e atenei
        String[] split1 = corsoStr1.split(" - ");
        String nomeCorso1 = split1[0];
        String ateneo1 = split1[1];

        String[] split2 = corsoStr2.split(" - ");
        String nomeCorso2 = split2[0];
        String ateneo2 = split2[1];

        System.out.println("\nCorso 1: " + nomeCorso1 + " (" + ateneo1 + ")");
        System.out.println("Corso 2: " + nomeCorso2 + " (" + ateneo2 + ")");

        // Recupero insegnamenti per entrambi i corsi
        List<InsegnamentoBean> insegnamenti1 = controller.getInsegnamenti(nomeCorso1, ateneo1);
        List<InsegnamentoBean> insegnamenti2 = controller.getInsegnamenti(nomeCorso2, ateneo2);

        // Stampa insegnamenti
        System.out.println("\n--- Insegnamenti Corso 1 ---");
        stampaInsegnamenti(insegnamenti1);

        System.out.println("\n--- Insegnamenti Corso 2 ---");
        stampaInsegnamenti(insegnamenti2);

        // Menu azioni preferiti/confronto
        menuPreferiti(utenteBean, nomeCorso1, ateneo1, nomeCorso2, ateneo2, corsoStr1, corsiSimili);
    }

    // Stampa lista insegnamenti
    private void stampaInsegnamenti(List<InsegnamentoBean> lista) {
        if (lista.isEmpty()) {
            System.out.println("Nessun insegnamento disponibile.");
            return;
        }

        int i = 1;
        for (InsegnamentoBean ins : lista) {
            System.out.printf("%d. %s | CFU: %d | Anno: %d | Semestre: %d%n", i++,
                    ins.getNome(), ins.getCfu(), ins.getAnno(), ins.getSemestre());
            System.out.println("   Curriculum: " + ins.getCurriculum());
            System.out.println("--------------------------------------------------");
        }
    }

    // Menu per azioni sui preferiti o ritorno al dettaglio
    private void menuPreferiti(UtenteBean utenteBean, String corso1, String ateneo1,
                               String corso2, String ateneo2, String corsoPrincipale, List<String> corsiSimili) {

        System.out.println("\nAzioni disponibili:");
        System.out.println("1. Aggiungi Corso 1 ai preferiti");
        System.out.println("2. Aggiungi Corso 2 ai preferiti");
        System.out.println("3. Torna al dettaglio corso principale");
        System.out.println("4. Torna alla Ricerca");

        String scelta = CLIUtils.leggiInput(scanner, "Scelta: ");

        switch (scelta) {
            case "1" -> {
                boolean ok = controller.aggiungiAiPreferiti(utenteBean, corso1, ateneo1);
                System.out.println(ok ? "Corso 1 aggiunto ai preferiti." : "Corso 1 già nei preferiti.");
                menuPreferiti(utenteBean, corso1, ateneo1, corso2, ateneo2, corsoPrincipale, corsiSimili);
            }
            case "2" -> {
                boolean ok = controller.aggiungiAiPreferiti(utenteBean, corso2, ateneo2);
                System.out.println(ok ? "Corso 2 aggiunto ai preferiti." : "Corso 2 già nei preferiti.");
                menuPreferiti(utenteBean, corso1, ateneo1, corso2, ateneo2, corsoPrincipale, corsiSimili);
            }
            case "3" -> new InCercaDettaglioCorsoViewCLI().show(utenteBean, corsoPrincipale, corsiSimili);
            case "4" -> System.out.println("Uscita dal confronto.");
            default -> {
                System.out.println("Scelta non valida.");
                menuPreferiti(utenteBean, corso1, ateneo1, corso2, ateneo2, corsoPrincipale, corsiSimili);
            }
        }

    }
}

