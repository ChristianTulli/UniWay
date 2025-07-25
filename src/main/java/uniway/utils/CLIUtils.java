package uniway.utils;

import uniway.eccezioni.EsciException;
import uniway.eccezioni.TornaAlLoginException;

import java.util.Scanner;

public class CLIUtils {
    private CLIUtils() {
        throw new IllegalStateException("Utility class");
    }

    public static String leggiInput(Scanner scanner, String prompt) {
        System.out.print(prompt);
        String input = scanner.nextLine().trim();

        // Controllo comandi globali ignorando il case
        switch (input.toLowerCase()) {
            case "login" -> throw new TornaAlLoginException();
            case "esci"  -> throw new EsciException();
            default      -> { return input; }
        }
    }
}
