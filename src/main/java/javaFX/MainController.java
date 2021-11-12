package javaFX;

import javaFX.GraphControllers.GraphController;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Menu;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

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

    @FXML
    HBox bar;

    private String theme = "css/lightTheme.css";

    public ChartController getChartComponentController() {
        return chartComponentController;
    }

    public GraphController getGraphComponentController() {
        return graphComponentController;
    }

    public String getTheme() {
        return this.theme;
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
        this.theme = "css/darkTheme.css";
        this.vbox.getScene().getStylesheets().add(theme);
        if (this.graphComponentController.getCurrentGraph() != null && this.graphComponentController.getCurrentGraph().hasAttribute("ui.stylesheet")) {
            this.graphComponentController.getCurrentGraph().removeAttribute("ui.stylesheet");
            this.graphComponentController.getCurrentGraph().setAttribute("ui.stylesheet", "url('file://src/main/resources/css/graphDarkTheme.css')");
        }
        graphComponentController.setTheme("url('file://src/main/resources/css/graphDarkTheme.css')");
    }

    /**
     * Load light theme to the window.
     */
    @FXML
    private void loadLightTheme() {
        this.theme = "css/lightTheme.css";
        this.vbox.getScene().getStylesheets().clear();
        if (this.graphComponentController.getCurrentGraph() != null && this.graphComponentController.getCurrentGraph().hasAttribute("ui.stylesheet")) {
            this.graphComponentController.getCurrentGraph().removeAttribute("ui.stylesheet");
            this.graphComponentController.getCurrentGraph().setAttribute("ui.stylesheet", "url('file://src/main/resources/css/graphLightTheme.css')");
        }
        graphComponentController.setTheme("url('file://src/main/resources/graphLightTheme.css')");
    }


    @FXML
    private void close() {
        Platform.exit();
    }

    @FXML
    private void maximize() {
        Stage stage = (Stage) vbox.getScene().getWindow();
        if (stage.isMaximized())
            stage.setMaximized(false);
        else
            stage.setMaximized(true);
    }

    @FXML
    private void minimize() {
        Stage stage = (Stage) vbox.getScene().getWindow();
        stage.setIconified(true);
    }

    double x, y;

    @FXML
    private void dragged(MouseEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setX(event.getScreenX() -x);
        stage.setY(event.getScreenY() -y);
    }

    @FXML
    private void pressed(MouseEvent event) {
        x = event.getSceneX();
        y = event.getSceneY();
    }
}
