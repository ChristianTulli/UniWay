package uniway.viewcontroller;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class FXMLController extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/view/LogInUI.fxml"));
        Scene scene = new Scene(root, 1600, 900);
        stage.setScene(scene);
        stage.setTitle("UniWay");
        stage.setResizable(false);
        stage.centerOnScreen();
        stage.show();
    }
}
