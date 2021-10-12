package javaFX;

import App.CsvUtility.CsvImport;
import com.sun.javafx.charts.Legend;
import javafx.fxml.FXML;
import javafx.geometry.Side;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
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


    @FXML
    private void openExplorer() {
        FileChooser fileChooser = new FileChooser();
        Stage stage = (Stage) anchorID.getScene().getWindow();
        File file = fileChooser.showOpenDialog(stage);
        if (file != null) {
            String path = file.getAbsolutePath();
            List<XYChart.Series<Number, Number>> series = CsvImport.getSeriesFromCsv(path);
            lineChart.getData().addAll(series);
            legendSelect();
            anchorID.getScene().getStylesheets().add("lineChart.css");
        }
    }

    public void initialize() {
        yAxis.setLabel("Values");
        xAxis.setLabel("Time");
        lineChart.setTitle("X,Y,Z values in time");
        lineChart.setLegendSide(Side.LEFT);
        lineChart.getYAxis().lookup(".axis-label").setStyle("-fx-label-padding: -40 0 0 0;");
        zoomable();
    }

    private void zoomable() {
        JFXChartUtil.setupZooming(lineChart);
    }

    //method for selecting value on legend
    private void legendSelect() {
        for (Node n : this.lineChart.getChildrenUnmodifiable()) {
            if (n instanceof Legend) {
                Legend l = (Legend) n;
                for (Legend.LegendItem li : l.getItems()) {
                    for (XYChart.Series<Number, Number> s : this.lineChart.getData()) {
                        if (s.getName().equals(li.getText())) {
                            li.getSymbol().setCursor(Cursor.HAND);
                            li.getSymbol().setOnMouseClicked(me -> {
                                if (me.getButton() == MouseButton.PRIMARY) {
                                    s.getNode().setVisible(!s.getNode().isVisible());
                                    for (XYChart.Data<Number, Number> d : s.getData()) {
                                        if (d.getNode() != null) {
                                            d.getNode().setVisible(s.getNode().isVisible());
                                        }
                                    }
                                }
                            });
                            break;
                        }
                    }
                }
            }
        }
    }
}







