
package uniway;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class Main extends Application {
    //avviammo la schermata iniziale dell'applicazione
    @Override
    public void start(Stage stage) throws IOException {
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/view/accesso-registrazione.fxml")));
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("UniWay");
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}