package javaFX;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Class for fxml load
 */
public class JavaFxApp extends Application {

    public void start(Stage stage) throws Exception {
//        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/chartWindow.fxml"));
//        fxmlLoader.setController(new ChartController());

//        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/graphWindow.fxml"));
//        fxmlLoader.setController(new GraphController());

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/mainComponent.fxml"));
        Parent root = fxmlLoader.load();
        Scene scene = new Scene(root);
        stage.setScene(scene);
//        stage.setMaximized(true);
        stage.initStyle(StageStyle.DECORATED);
        stage.setTitle("MoonLight Viewer");
        stage.show();
    }
}
