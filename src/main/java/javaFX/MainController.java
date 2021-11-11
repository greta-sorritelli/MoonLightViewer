package javaFX;

import javaFX.GraphControllers.GraphController;
import javafx.fxml.FXML;
import javafx.scene.control.Menu;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

/**
 * Main controller of the application. It has other controllers nested in it.
 */
public class MainController {

    @FXML
    AnchorPane chartComponent;
    @FXML
    ChartController chartComponentController;
    @FXML
    AnchorPane graphComponent;
    @FXML
    GraphController graphComponentController;

    @FXML
    VBox vbox;
    @FXML
    Menu menuCSV;

    public ChartController getChartComponentController() {
        return chartComponentController;
    }

    public GraphController getGraphComponentController() {
        return graphComponentController;
    }

    public VBox getVbox() {
        return this.vbox;
    }

    /**
     * Gets all info and controllers from the others fxml files included and inject this {@link MainController} in its nested controllers
     */
    @FXML
    public void initialize() {
        this.chartComponentController.injectMainController(this);
        this.graphComponentController.injectMainController(this, chartComponentController);
    }

    /**
     * Open the explorer to choose a .csv file
     */
    @FXML
    private void openCsvExplorer() {
        graphComponentController.openCSVExplorer();
    }

    /**
     * Open the explorer to choose a .tra file
     */
    @FXML
    private void openTraExplorer() {
        graphComponentController.openTraExplorer();
        menuCSV.setDisable(false);
    }

    /**
     * Load dark theme to the window.
     */
    @FXML
    private void loadDarkTheme() {
        this.vbox.getScene().getStylesheets().add("dark-theme.css");
        if (this.graphComponentController.getCurrentGraph() != null && this.graphComponentController.getCurrentGraph().hasAttribute("ui.stylesheet")) {
            this.graphComponentController.getCurrentGraph().removeAttribute("ui.stylesheet");
            this.graphComponentController.getCurrentGraph().setAttribute("ui.stylesheet", "url('file://src/main/resources/graphDarkTheme.css')");
        }
        graphComponentController.setTheme("url('file://src/main/resources/graphDarkTheme.css')");
    }

    /**
     * Load light theme to the window.
     */
    @FXML
    private void loadLightTheme() {
        this.vbox.getScene().getStylesheets().clear();
        if (this.graphComponentController.getCurrentGraph() != null && this.graphComponentController.getCurrentGraph().hasAttribute("ui.stylesheet")) {
            this.graphComponentController.getCurrentGraph().removeAttribute("ui.stylesheet");
            this.graphComponentController.getCurrentGraph().setAttribute("ui.stylesheet", "url('file://src/main/resources/graphLightTheme.css')");
        }
        graphComponentController.setTheme("url('file://src/main/resources/graphLightTheme.css')");
    }
}
