package uniway;

import javafx.application.Application;
import uniway.viewcontroller.CLIController;
import uniway.viewcontroller.FXMLController;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Main {

    public static void main(String[] args) {
        String uiVersion = loadUIVersion();

        if ("cli".equalsIgnoreCase(uiVersion)) {
            new CLIController().start(); // non dimenticare il .start()
        } else {
            Application.launch(FXMLController.class, args);
        }
    }

    private static String loadUIVersion() {
        Properties props = new Properties();
        try (InputStream input = Main.class.getClassLoader().getResourceAsStream("config.properties")) {
            if (input != null) {
                props.load(input);
                return props.getProperty("ui.version", "fxml");
            }
        } catch (IOException e) {
            System.err.println("Errore durante il caricamento di config.properties: " + e.getMessage());
        }
        return "fxml"; // default fallback
    }
}
