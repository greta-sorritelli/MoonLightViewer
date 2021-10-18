package javaFX;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class JavaFxApp extends Application {

    public void start(Stage stage) throws Exception {
//        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/chartWindow.fxml"));
//        fxmlLoader.setController(new ChartController());

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/graphWindow.fxml"));
        fxmlLoader.setController(new GraphController());
        Parent root = fxmlLoader.load();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.initStyle(StageStyle.DECORATED);
        stage.setTitle("MoonLight Viewer");
        ChartController hc = new ChartController();
        hc.initialize();
        stage.show();
    }
}
