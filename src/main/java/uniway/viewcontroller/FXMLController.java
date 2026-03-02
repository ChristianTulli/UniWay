package uniway.viewcontroller;

import javafx.application.Application;
import javafx.stage.Stage;
import uniway.patterns.NavigationManagerFacade;

public class FXMLController extends Application {

    @Override
    public void start(Stage stage) {
        NavigationManagerFacade.switchScene(stage, "/view/LogInUI.fxml", "UniWay - Login");
    }
}
