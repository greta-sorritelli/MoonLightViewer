package javaFX;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 * Class for fxml load
 */
public class JavaFxApp extends Application {

    public void start(Stage stage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/mainComponent.fxml"));
        Parent root = fxmlLoader.load();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.getIcons().add(new Image("/images/ML.png"));
//        stage.setMaximized(true);
        stage.initStyle(StageStyle.DECORATED);
        stage.setTitle("MoonLight Viewer");
        stage.show();
    }
}
