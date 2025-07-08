package uniway.viewcontroller;

import uniway.beans.UtenteBean;
import uniway.controller.LogInController;

import java.io.IOException;
import java.util.Optional;
import java.util.Scanner;

public class LoginViewControllerCLI {

    private final LogInController loginController;
    private final Scanner scanner;

    public LoginViewControllerCLI() {
        this.scanner = new Scanner(System.in);
        this.loginController = new LogInController();
    }

    public void show() {
        System.out.println("=== Benvenuto in UniWay (CLI) ===");
        System.out.println("1. Accedi");
        System.out.println("2. Registrati");
        System.out.print("Scegli un'opzione: ");
        String scelta = scanner.nextLine();

        switch (scelta) {
            case "1" -> accedi();
            case "2" -> registrati();
            default -> System.out.println("Scelta non valida.");
        }
    }

    private void accedi() {
        System.out.println("--- Login ---");
        String username = chiedi("Username");
        String password = chiedi("Password");

        try {
            Optional<UtenteBean> utenteOpt = loginController.autenticazione(username, password);
            if (utenteOpt.isPresent()) {
                UtenteBean utente = utenteOpt.get();
                System.out.println("Login effettuato con successo!");

                if (utente.getIscritto()) {
                    if (utente.getIdCorso() != null && utente.getCurriculum() != null) {
                        System.out.println("→ Utente iscritto con corso selezionato");
                        new IscrittoInsegnamentiViewCLI().show(utente);
                    } else {
                        System.out.println("→ Utente iscritto ma senza corso selezionato");
                        new IscrittoSelezionaCorsoViewCLI().show(utente);
                    }
                } else {
                    System.out.println("→ Utente in cerca");
                    new InCercaTrovaCorsoViewCLI().show(utente);
                }

            } else {
                System.out.println("Errore: username o password non corretti.");
            }
        } catch (IOException e) {
            System.out.println("Errore durante l'autenticazione: " + e.getMessage());
        }
    }

    private void registrati() {
        System.out.println("--- Registrazione ---");

        String username = chiedi("Scegli uno username");
        String password = chiedi("Scegli una password (min 6 caratteri)");

        if (password.length() < 6) {
            System.out.println("Errore: la password deve contenere almeno 6 caratteri.");
            return;
        }

        System.out.println("Sei:");
        System.out.println("1. Iscritto");
        System.out.println("2. In cerca");
        String ruolo = scanner.nextLine();
        boolean iscritto = "1".equals(ruolo);

        UtenteBean utenteBean = new UtenteBean(username, password, iscritto);

        try {
            boolean success = loginController.registrazione(utenteBean);
            if (success) {
                System.out.println("Registrazione completata!");
                if (iscritto) {
                    new IscrittoSelezionaCorsoViewCLI().show(utenteBean);
                    System.out.println("→ Continua come utente iscritto");
                } else {
                    new InCercaTrovaCorsoViewCLI().show(utenteBean);
                    System.out.println("→ Continua come utente in cerca");
                }
            } else {
                System.out.println("Errore: username esistente o dati non validi.");
            }
        } catch (IOException e) {
            System.out.println("Errore durante la registrazione: " + e.getMessage());
        }
    }

    private String chiedi(String campo) {
        System.out.print(campo + ": ");
        return scanner.nextLine();
    }
}
