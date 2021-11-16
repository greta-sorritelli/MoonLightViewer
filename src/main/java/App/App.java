package App;

import App.javaFX.JavaFxApp;
import javafx.application.Application;

/**
 * Main class. Launch the user interface
 */
public class App {

    public static void main(String[] args) {
        launchGui();
    }

    private static void launchGui() {
        Application.launch(JavaFxApp.class);
    }
}
