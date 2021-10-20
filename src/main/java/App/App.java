package App;

import javaFX.JavaFxApp;
import javafx.application.Application;

public class App {
    public String getGreeting() {
        return "Hello world.";
    }

    public static void main(String[] args) {
        launchGui();
    }

    private static void launchGui() {
        Application.launch(JavaFxApp.class);
    }
}
