package it.unicam.cs.project.moonlightviewer;

import it.unicam.cs.project.moonlightviewer.javaFX.JavaFxMoonlightViewer;
import javafx.application.Application;

/**
 * Main class. Launch the user interface
 *
 * @author Albanese Clarissa, Sorritelli Greta
 */
public class App {

    public static void main(String[] args) {
        launchGui();
    }

    private static void launchGui() {
        Application.launch(JavaFxMoonlightViewer.class);
    }
}
