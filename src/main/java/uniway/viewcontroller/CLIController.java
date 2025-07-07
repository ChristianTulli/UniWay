package uniway.viewcontroller;

public class CLIController {

    public void start() {
        System.out.println("Benvenuto nella versione CLI di UniWay!");
        // Esegui CLI iniziale, ad esempio login:
        new LoginViewControllerCLI().show();
    }
}
