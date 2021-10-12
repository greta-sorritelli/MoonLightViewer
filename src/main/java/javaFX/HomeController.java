package javaFX;

import App.Utility.CsvImport;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.geometry.Point2D;
import javafx.geometry.Side;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.Rectangle;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.gillius.jfxutils.chart.ChartZoomManager;
import org.gillius.jfxutils.chart.JFXChartUtil;

import java.io.File;
import java.util.List;

public class HomeController {

    @FXML
    private AnchorPane anchorID;
    @FXML
    private NumberAxis yAxis = new NumberAxis();
    @FXML
    private NumberAxis xAxis = new NumberAxis();
    @FXML
    private LineChart<Number, Number> lineChart = new LineChart<>(xAxis, yAxis);

//    final double SCALE_DELTA = 1.1;

    @FXML
    private void openExplorer() {
        FileChooser fileChooser = new FileChooser();
        Stage stage = (Stage) anchorID.getScene().getWindow();
        File file = fileChooser.showOpenDialog(stage);
        if (file != null) {
            String path = file.getAbsolutePath();
            List<XYChart.Series<Number, Number>> series = CsvImport.getSeriesFromCsv(path);
            lineChart.setVisible(true);
            lineChart.getData().addAll(series);
            anchorID.getScene().getStylesheets().add("lineChart.css");
        }
    }

    public void initialize() {
        lineChart.setVisible(false);
        yAxis.setLabel("Values");
        xAxis.setLabel("Time");
        lineChart.setTitle("X,Y,Z values in time");
        lineChart.setLegendSide(Side.RIGHT);
        lineChart.getYAxis().lookup(".axis-label").setStyle("-fx-label-padding: -40 0 0 0;");
        zoomable();
    }

    private void zoomable() {

//        ChartZoomManager zoomManager = new ChartZoomManager();
        JFXChartUtil.setupZooming(lineChart);
    }




}







