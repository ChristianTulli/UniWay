package uniway.viewcontroller;

import javafx.application.Application;
import javafx.stage.Stage;
import uniway.utils.NavigationManager;

public class FXMLController extends Application {

    @Override
    public void start(Stage stage) {
        NavigationManager.switchScene(stage, "/view/LogInUI.fxml", "UniWay - Login");
    }
}
