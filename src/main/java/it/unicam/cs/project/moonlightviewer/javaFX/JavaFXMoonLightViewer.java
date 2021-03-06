package it.unicam.cs.project.moonlightviewer.javaFX;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.net.URL;
import java.util.Objects;

/**
 * This class is responsible for the principle window of app
 *
 * @author Albanese Clarissa, Sorritelli Greta
 */
public class JavaFXMoonLightViewer extends Application {

    /**
     * Launch the principle stage.
     *
     * @param stage           Stage to open.
     * @throws Exception      Exception.
     */
    public void start(Stage stage) throws Exception {
        ClassLoader classLoader = ClassLoader.getSystemClassLoader();
        URL file = classLoader.getResource("fxml/mainComponent.fxml");
        VBox root = FXMLLoader.load(Objects.requireNonNull(file));
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.getIcons().add(new Image((Objects.requireNonNull(classLoader.getResource("images/ML.png"))).toString()));
        stage.setTitle("MoonLightViewer");
        stage.initStyle(StageStyle.DECORATED);
        stage.show();
    }
}
