package uniway.utils;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class NavigationManager {

    private NavigationManager() {}

    private static final Logger LOGGER =
            Logger.getLogger(NavigationManager.class.getName());
    private static final String ERR = "Errore nel cambio scena. FXML: ";
    private static final double WIDTH = 1600;
    private static final double HEIGHT = 900;

    /* ====== SCENE (Stage già noto) ====== */
    public static void switchScene(Stage stage, String fxmlPath, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(NavigationManager.class.getResource(fxmlPath));
            Parent root = loader.load();
            stage.setScene(new Scene(root, WIDTH, HEIGHT));
            stage.setTitle(title);
            stage.setResizable(false);
            stage.centerOnScreen();
            stage.show();
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, ERR, e);
        }
    }

    /* SCENE (Stage noto) + passaggio dati al controller */
    public static <C> void switchScene(Stage stage, String fxmlPath, String title,
                                       Class<C> controllerType, Consumer<C> initializer) {
        try {
            FXMLLoader loader = new FXMLLoader(NavigationManager.class.getResource(fxmlPath));
            Parent root = loader.load();

            if (controllerType != null && initializer != null) {
                Object ctrl = loader.getController();
                if (!controllerType.isInstance(ctrl)) {
                    throw new IllegalStateException("Controller inatteso: " + ctrl.getClass().getName());
                }
                initializer.accept(controllerType.cast(ctrl));
            }

            stage.setScene(new Scene(root, WIDTH, HEIGHT));
            stage.setTitle(title);
            stage.setResizable(false);
            stage.centerOnScreen();
            stage.show();
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, ERR, e);
        }
    }

    /* ====== SCENE (da ActionEvent) ====== */
    public static void switchScene(ActionEvent event, String fxmlPath, String title) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        switchScene(stage, fxmlPath, title);
    }

    /* SCENE (da ActionEvent) + passaggio dati */
    public static <C> void switchScene(ActionEvent event, String fxmlPath, String title,
                                       Class<C> controllerType, Consumer<C> initializer) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        switchScene(stage, fxmlPath, title, controllerType, initializer);
    }

    /* ====== PANE (sostituisce il contenuto di #contentArea) ====== */
    public static void switchPane(String fxmlPath, Node anyNodeInsideScene) {
        switchPane(fxmlPath, anyNodeInsideScene, null, null);
    }

    public static <C> void switchPane(String fxmlPath, Node anyNodeInsideScene,
                                      Class<C> controllerType, Consumer<C> initializer) {
        try {
            FXMLLoader loader = new FXMLLoader(NavigationManager.class.getResource(fxmlPath));
            Parent newContent = loader.load();

            if (controllerType != null && initializer != null) {
                Object ctrl = loader.getController();
                if (!controllerType.isInstance(ctrl)) {
                    throw new IllegalStateException("Controller inatteso: " + ctrl.getClass().getName());
                }
                initializer.accept(controllerType.cast(ctrl));
            }

            AnchorPane contentArea = (AnchorPane) anyNodeInsideScene.getScene().lookup("#contentArea");
            if (contentArea == null) {
                throw new IllegalStateException("Nessun nodo con fx:id='contentArea' nella scena corrente");
            }

            contentArea.getChildren().setAll(newContent);
            AnchorPane.setTopAnchor(newContent, 0.0);
            AnchorPane.setBottomAnchor(newContent, 0.0);
            AnchorPane.setLeftAnchor(newContent, 0.0);
            AnchorPane.setRightAnchor(newContent, 0.0);

        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, ERR, e);
        }
    }
}

