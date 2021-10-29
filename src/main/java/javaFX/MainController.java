package javaFX;

import javafx.fxml.FXML;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

public class MainController {

//    private static MainController mainController = new MainController();
//
//    private MainController() {
//    }
//
//    public static MainController getInstance() {
//        return mainController;
//    }


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

    public ChartController getChartComponentController() {
        return chartComponentController;
    }

    public GraphController getGraphComponentController() {
        return graphComponentController;
    }

    public VBox getVbox() {
        return this.vbox;
    }

    @FXML
    public void initialize() {
        this.chartComponentController.injectMainController(this);
        this.graphComponentController.injectMainController(this, chartComponentController);
    }

    @FXML
    private void openCsvExplorer() {
//        chartComponentController.openCsvExplorer();
        graphComponentController.openCSVExplorer();
    }

    @FXML
    private void openTraExplorer() {
        graphComponentController.openTraExplorer();
    }
}
