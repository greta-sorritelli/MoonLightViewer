package javaFX;

import App.CsvUtility.CsvImport;
import javafx.fxml.FXML;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.chart.Axis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.CheckBox;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.gillius.jfxutils.chart.JFXChartUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class HomeController {

    @FXML
    private AnchorPane anchorID;

    @FXML
    private NumberAxis yAxis = new NumberAxis();
    @FXML
    private NumberAxis xAxis = new NumberAxis();

    @FXML
    private LogarithmicAxis xLAxis = new LogarithmicAxis();
    @FXML
    private LogarithmicAxis yLAxis = new LogarithmicAxis();

    @FXML
    private LineChart<Number, Number> lineChart = new LineChart<>(xAxis, yAxis);
    @FXML
    private LineChart<Number, Number> lineChartLog = new LineChart<>(xLAxis, yLAxis);

    @FXML
    private TableView<Object> checkList;
    @FXML
    private TableColumn<CheckBox, Node> checkColumn;
    @FXML
    private TableColumn<String, Node> nodeColumn;
    @FXML
    private TableView<String> variables;
    @FXML
    private TableColumn<String, Node> nameVColumn;
    @FXML
    private TableColumn<String, Node> minColumn;
    @FXML
    private TableColumn<String, Node> maxColumn;

    @FXML
    private RadioButton linear = new RadioButton();
    @FXML
    private RadioButton logarithmic = new RadioButton();


    private List<XYChart.Series<Number, Number>> series = new ArrayList<>();

    @FXML
    private void openExplorer() {
        FileChooser fileChooser = new FileChooser();
        Stage stage = (Stage) anchorID.getScene().getWindow();
        File file = fileChooser.showOpenDialog(stage);
        if (file != null) {
            String path = file.getAbsolutePath();
            series = CsvImport.getSeriesFromCsv(path);
            linearSelected();
            anchorID.getScene().getStylesheets().add("lineChart.css");
        }
    }

    private void initializeChart(LineChart<Number,Number> l, Axis x, Axis y) {
        y.setLabel("Values");
        x.setLabel("Time");
        l.setTitle("X,Y,Z values in time");
        l.setLegendSide(Side.RIGHT);
        l.getYAxis().lookup(".axis-label").setStyle("-fx-label-padding: -40 0 0 0;");
    }

    public void initialize(){
        linearSelected();
//        zoomable(lineChart);
//        zoomable(lineChartLog);
        initializeChart(lineChart, xAxis, yAxis);
        initializeChart(lineChartLog, xLAxis, yLAxis);
    }

    @FXML
    private void logarithmicSelected() {
        lineChartLog.getData().removeAll(series);
        lineChartLog.getData().addAll(series);
        linear.setSelected(false);
        lineChart.setVisible(false);
        lineChartLog.setVisible(true);
        logarithmic.requestFocus();
        logarithmic.setSelected(true);
    }

    @FXML
    private void linearSelected() {
        lineChart.getData().removeAll(series);
        lineChart.getData().addAll(series);
        linear.setSelected(true);
        lineChartLog.setVisible(false);
        lineChart.setVisible(true);
        linear.requestFocus();
        logarithmic.setSelected(false);
    }

    private void zoomable(LineChart<Number,Number> lineChart) {

        JFXChartUtil.setupZooming(lineChart);
    }

}