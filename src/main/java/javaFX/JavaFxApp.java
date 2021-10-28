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

public class JavaFxApp extends Application {

    public void start(Stage stage) throws Exception {
//        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/chartWindow.fxml"));
//        fxmlLoader.setController(new ChartController());

//        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/graphWindow.fxml"));
//        fxmlLoader.setController(new GraphController());
//        URL location = getClass().getResource("mainComponent.fxml");
//        ResourceBundle resources = ResourceBundle.getBundle("javaFX.MainController");
//        FXMLLoader fxmlLoader = new FXMLLoader(location, null);

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/mainComponent.fxml"));
//        fxmlLoader.setController(new MainController());
        Parent root = fxmlLoader.load();
        Scene scene = new Scene(root);
        stage.setScene(scene);
//        stage.setMaximized(true);
        stage.initStyle(StageStyle.DECORATED);
        stage.setTitle("MoonLight Viewer");
//        ChartController hc = new ChartController();
//        hc.initialize();
        stage.show();
    }
}
