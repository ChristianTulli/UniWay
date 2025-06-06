
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
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/view/accesso-registrazione.fxml"));
        Scene scene = new Scene(root, 1600, 900);
        stage.setScene(scene);
        stage.setTitle("UniWay");
        stage.setResizable(false);
        stage.centerOnScreen();
        stage.show();
    }


    public static void main(String[] args) {
        launch();
    }
}