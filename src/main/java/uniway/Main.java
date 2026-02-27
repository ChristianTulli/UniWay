package uniway;

import javafx.application.Application;
import uniway.viewcontroller.CLIController;
import uniway.viewcontroller.FXMLController;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Main {
    private static final Logger LOGGER = Logger.getLogger(Main.class.getName());
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
            LOGGER.log(Level.SEVERE, "errore nell'avvio della UI", e);
        }
        return "fxml"; // default fallback
    }
}
