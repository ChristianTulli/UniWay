package uniway.viewcontroller.cli;

import uniway.beans.UtenteBean;
import uniway.controller.IscrittoSelezionaCorsoController;
import uniway.eccezioni.EsciException;
import uniway.eccezioni.TornaAlLoginException;
import uniway.utils.CLIUtils;

import java.util.List;
import java.util.Scanner;

public class IscrittoSelezionaCorsoViewCLI {

    private final Scanner scanner = new Scanner(System.in);
    private final IscrittoSelezionaCorsoController controller = new IscrittoSelezionaCorsoController();

    // Mostra la schermata CLI per la selezione del corso da parte dell'utente iscritto.
    public void show(UtenteBean utenteBean) throws TornaAlLoginException, EsciException {
        System.out.println("\n=== Seleziona il tuo corso di laurea ===");

        String regione = filtra("Regione", controller.getRegioni());
        if (regione == null) return;

        String provincia = filtra("Provincia", controller.getProvince(regione));
        if (provincia == null) return;

        String comune = filtra("Comune", controller.getComuni(provincia));
        if (comune == null) return;

        String ateneo = filtra("Ateneo", controller.getAtenei(comune));
        if (ateneo == null) return;

        String disciplina = filtra("Disciplina", controller.getDiscipline(ateneo));
        if (disciplina == null) return;

        String tipologia = filtra("Tipologia", controller.getTipologie(disciplina));
        if (tipologia == null) return;

        String classe = filtra("Corso", controller.getCorsi(tipologia));
        if (classe == null) return;

        List<String> risultati = controller.getRisultati(classe);
        if (risultati == null || risultati.isEmpty()) {
            System.out.println("Nessun corso trovato con i filtri selezionati.");
            return;
        }

        String corsoSelezionato = filtra("Corso di Laurea", risultati);
        if (corsoSelezionato == null) return;

        controller.setCorsoUtente(utenteBean, corsoSelezionato);

        gestisciCurriculum(corsoSelezionato, utenteBean);

        System.out.println("\nCorso selezionato: " + corsoSelezionato);
        if (utenteBean.getCurriculum() != null) {
            System.out.println("Curriculum: " + utenteBean.getCurriculum());
        }

        new IscrittoInsegnamentiViewCLI().show(utenteBean);
    }

    // Riduce duplicazione nella selezione di un'opzione
    private String filtra(String etichetta, List<String> opzioni) throws TornaAlLoginException, EsciException {
        return selezionaDaLista(etichetta, opzioni);
    }

    // Gestisce logica selezione curriculum separatamente
    private void gestisciCurriculum(String corsoSelezionato, UtenteBean utenteBean) throws TornaAlLoginException, EsciException {
        List<String> curriculumDisponibili = controller.getCurriculumPerCorso(corsoSelezionato);
        if (curriculumDisponibili == null || curriculumDisponibili.isEmpty()) return;

        String curriculum;
        if (curriculumDisponibili.size() == 1) {
            curriculum = curriculumDisponibili.get(0);
            System.out.println("Curriculum assegnato automaticamente: " + curriculum);
        } else {
            curriculum = selezionaDaLista("Curriculum", curriculumDisponibili);
            if (curriculum == null) return;
        }

        controller.setCurriculumUtente(utenteBean, curriculum);
    }

    //Metodo ausiliario per selezionare un elemento da una lista
    private String selezionaDaLista(String tipo, List<String> opzioni) throws TornaAlLoginException, EsciException {
        if (opzioni == null || opzioni.isEmpty()) {
            System.out.println("Nessuna " + tipo.toLowerCase() + " disponibile.");
            return null;
        }

        System.out.println("\nSeleziona " + tipo + ":");
        for (int i = 0; i < opzioni.size(); i++) {
            System.out.println((i + 1) + ". " + opzioni.get(i));
        }

        String input = CLIUtils.leggiInput(scanner, "Scelta (" + tipo + "): ");
        try {
            int scelta = Integer.parseInt(input);
            if (scelta >= 1 && scelta <= opzioni.size()) {
                return opzioni.get(scelta - 1);
            }
        } catch (NumberFormatException ignored) {
            // La validazione viene gestita sotto
        }

        System.out.println("Scelta non valida.");
        return null;
    }
}


