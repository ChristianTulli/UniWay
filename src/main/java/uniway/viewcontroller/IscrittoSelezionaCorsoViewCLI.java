package uniway.viewcontroller;

import uniway.beans.UtenteBean;
import uniway.controller.IscrittoSelezionaCorsoController;

import java.io.IOException;
import java.util.List;
import java.util.Scanner;

public class IscrittoSelezionaCorsoViewCLI {

    private final Scanner scanner = new Scanner(System.in);
    private final IscrittoSelezionaCorsoController controller = new IscrittoSelezionaCorsoController();

    public void show(UtenteBean utenteBean) {
        System.out.println("\n=== Seleziona il tuo corso di laurea ===");

        String regione = selezionaDaLista("Regione", controller.getRegioni());
        if (regione == null) return;

        String provincia = selezionaDaLista("Provincia", controller.getProvince(regione));
        if (provincia == null) return;

        String comune = selezionaDaLista("Comune", controller.getComuni(provincia));
        if (comune == null) return;

        String ateneo = selezionaDaLista("Ateneo", controller.getAtenei(comune));
        if (ateneo == null) return;

        String disciplina = selezionaDaLista("Disciplina", controller.getDiscipline(ateneo));
        if (disciplina == null) return;

        String tipologia = selezionaDaLista("Tipologia", controller.getTipologie(disciplina));
        if (tipologia == null) return;

        String classe = selezionaDaLista("Corso", controller.getCorsi(tipologia));
        if (classe == null) return;

        List<String> risultati = controller.getRisultati(classe);
        if (risultati == null || risultati.isEmpty()) {
            System.out.println("⚠ Nessun corso trovato con i filtri selezionati.");
            return;
        }

        String corsoSelezionato = selezionaDaLista("Corso di Laurea", risultati);
        if (corsoSelezionato == null) return;

        List<String> curriculumDisponibili = controller.getCurriculumPerCorso(corsoSelezionato);

        controller.setCorsoUtente(utenteBean, corsoSelezionato);

        if (curriculumDisponibili != null && !curriculumDisponibili.isEmpty()) {
            String curriculum;
            if (curriculumDisponibili.size() == 1) {
                curriculum = curriculumDisponibili.get(0);
                System.out.println("→ Curriculum assegnato automaticamente: " + curriculum);
            } else {
                curriculum = selezionaDaLista("Curriculum", curriculumDisponibili);
                if (curriculum == null) return;
            }
            controller.setCurriculumUtente(utenteBean, curriculum);
        }

        System.out.println("\n✅ Corso selezionato: " + corsoSelezionato);
        if (utenteBean.getCurriculum() != null) {
            System.out.println("📘 Curriculum: " + utenteBean.getCurriculum());
        }

        // Prosegui alla prossima schermata CLI
        new IscrittoInsegnamentiViewCLI().show(utenteBean);
        System.out.println("\n→ Passaggio alla schermata insegnamenti");
    }

    private String selezionaDaLista(String tipo, List<String> opzioni) {
        if (opzioni == null || opzioni.isEmpty()) {
            System.out.println("⚠ Nessuna " + tipo.toLowerCase() + " disponibile.");
            return null;
        }

        System.out.println("\nSeleziona " + tipo + ":");
        for (int i = 0; i < opzioni.size(); i++) {
            System.out.println((i + 1) + ". " + opzioni.get(i));
        }

        System.out.print("Scelta (" + tipo + "): ");
        try {
            int scelta = Integer.parseInt(scanner.nextLine());
            if (scelta >= 1 && scelta <= opzioni.size()) {
                return opzioni.get(scelta - 1);
            }
        } catch (NumberFormatException ignored) {}

        System.out.println("Scelta non valida.");
        return null;
    }
}

