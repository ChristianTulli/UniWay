package uniway.viewcontroller.cli;

import uniway.beans.UtenteBean;
import uniway.controller.LogInController;
import uniway.eccezioni.EsciException;
import uniway.eccezioni.TornaAlLoginException;
import uniway.utils.CLIUtils;

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

    // Metodo principale che mostra il menu iniziale
    public void show() {
        while (true) {
            try {
                System.out.println("=== Benvenuto in UniWay (CLI) ===");
                System.out.println("1. Accedi");
                System.out.println("2. Registrati");
                System.out.println("3. Esci");
                System.out.println("Scrivi 'login' per tornare qui da ogni schermata, oppure 'esci' per uscire.");
                String scelta = CLIUtils.leggiInput(scanner, "Scegli un'opzione: ");

                switch (scelta) {
                    case "1" -> accedi();
                    case "2" -> registrati();
                    case "3" -> throw new TornaAlLoginException();
                    default -> System.out.println("Scelta non valida.");
                }
            } catch (TornaAlLoginException e) {
                // Lanciare questa eccezione qui non ha effetto: siamo già al login
                System.out.println("Sei già nel menu di login.");
            } catch (EsciException e) {
                // Uscita globale dal programma
                System.out.println("Chiusura dell'applicazione...");
                return;
            }
        }
    }

    // Metodo per l'autenticazione dell'utente
    private void accedi() {
        System.out.println("--- Login ---");

        // Richiesta credenziali con possibilità di digitare 'login' o 'esci'
        String username = CLIUtils.leggiInput(scanner, "Username: ");
        String password = CLIUtils.leggiInput(scanner, "Password: ");

        try {
            Optional<UtenteBean> utenteOpt = loginController.autenticazione(username, password);
            if (utenteOpt.isPresent()) {
                UtenteBean utente = utenteOpt.get();
                System.out.println("Login effettuato con successo!");

                // Se utente è iscritto con corso già selezionato
                if (utente.getIscritto()) {
                    if (utente.getIdCorso() != null && utente.getCurriculum() != null) {
                        System.out.println("Utente iscritto con corso selezionato");
                        new IscrittoInsegnamentiViewCLI().show(utente);
                    } else {
                        System.out.println("Utente iscritto ma senza corso selezionato");
                        new IscrittoSelezionaCorsoViewCLI().show(utente);
                    }
                } else {
                    // Utente in cerca
                    System.out.println("Utente in cerca");
                    new InCercaTrovaCorsoViewCLI().show(utente);
                }

            } else {
                System.out.println("Errore: username o password non corretti.");
            }
        } catch (IOException e) {
            System.out.println("Errore durante l'autenticazione: " + e.getMessage());
        }
    }

    // Metodo per la registrazione di un nuovo utente
    private void registrati() {
        System.out.println("--- Registrazione ---");

        String username = CLIUtils.leggiInput(scanner, "Scegli uno username: ");
        String password = CLIUtils.leggiInput(scanner, "Scegli una password (min 6 caratteri): ");

        if (password.length() < 6) {
            System.out.println("Errore: la password deve contenere almeno 6 caratteri.");
            return;
        }

        System.out.println("Sei:");
        System.out.println("1. Iscritto");
        System.out.println("2. In cerca");

        String ruolo = CLIUtils.leggiInput(scanner, "Scelta: ");
        boolean iscritto = "1".equals(ruolo);

        UtenteBean utenteBean = new UtenteBean(username, password, iscritto);

        try {
            boolean success = loginController.registrazione(utenteBean);
            if (success) {
                System.out.println("Registrazione completata!");

                if (iscritto) {
                    new IscrittoSelezionaCorsoViewCLI().show(utenteBean);
                    System.out.println("Continua come utente iscritto");
                } else {
                    new InCercaTrovaCorsoViewCLI().show(utenteBean);
                    System.out.println("Continua come utente in cerca");
                }

            } else {
                System.out.println("Errore: username esistente o dati non validi.");
            }
        } catch (IOException e) {
            System.out.println("Errore durante la registrazione: " + e.getMessage());
        }
    }
}

