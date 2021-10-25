package javaFX;

import App.ChartUtility.LogarithmicAxis;
import App.CsvUtility.CsvImport;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Side;
import javafx.scene.chart.Axis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Series;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ChartController {

    @FXML
    AnchorPane anchorID;
    @FXML
    NumberAxis yAxis = new NumberAxis();
    @FXML
    NumberAxis xAxis = new NumberAxis();
    @FXML
    LogarithmicAxis xLAxis = new LogarithmicAxis();
    @FXML
    LogarithmicAxis yLAxis = new LogarithmicAxis();
    @FXML
    ListView<CheckBox> list;
    @FXML
    LineChart<Number, Number> lineChartLog = new LineChart<>(xLAxis, yLAxis);
    @FXML
    LineChart<Number, Number> lineChart = new LineChart<>(xAxis, yAxis);
    @FXML
    TableView<Series<Number, Number>> variables;
    @FXML
    TableColumn<Series<Number, Number>, String> nameVColumn;
    @FXML
    TableColumn<Series<Number, Number>, Number> minColumn;
    @FXML
    TableColumn<Series<Number, Number>, Number> maxColumn;
    @FXML
    RadioButton linear = new RadioButton();
    @FXML
    RadioButton logarithmic = new RadioButton();

    @FXML
    private void openExplorer() {
        FileChooser fileChooser = new FileChooser();
        Stage stage = (Stage) anchorID.getScene().getWindow();
        File file = fileChooser.showOpenDialog(stage);
        if (file != null) {
            String path = file.getAbsolutePath();
            List<Series<Number, Number>> linearSeries = CsvImport.getSeriesFromCsv(path);
            List<Series<Number, Number>> logarithmicSeries = CsvImport.getSeriesFromCsv(path);
            lineChart.getData().addAll(linearSeries);
            lineChartLog.getData().addAll(logarithmicSeries);
            linearSelected();
            initLists();
        }
    }

    private void initializeChart(LineChart<Number, Number> l, Axis<Number> x, Axis<Number> y) {
        y.setLabel("Values");
        x.setLabel("Time");
        l.setTitle("X,Y,Z values in time");
        l.setLegendSide(Side.RIGHT);
        l.getYAxis().lookup(".axis-label").setStyle("-fx-label-padding: -40 0 0 0;");
    }

    public void initialize(){
        lineChartLog.setVisible(false);
        initializeChart(lineChart, xAxis, yAxis);
        initializeChart(lineChartLog, xLAxis, yLAxis);
    }

    @FXML
    private void logarithmicSelected() {
        lineChartLog.setAnimated(false);
        lineChart.setVisible(false);
        lineChartLog.setVisible(true);
        linear.setSelected(false);
        logarithmic.requestFocus();
        logarithmic.setSelected(true);
    }

    @FXML
    private void linearSelected() {
        lineChart.setAnimated(false);
        lineChartLog.setVisible(false);
        lineChart.setVisible(true);
        linear.setSelected(true);
        linear.requestFocus();
        logarithmic.setSelected(false);
    }

    private void initLists() {
        initVariablesList();
        showList();
    }

    private void initVariablesList() {
        List<Series<Number, Number>> items = new ArrayList<>();
        lineChart.getData().forEach(e -> {
            if (e != null)
                items.add(e);
        });
        variables.getItems().clear();
        for (Series<Number, Number> m : items) {
            variables.getItems().add(m);
        }
        setMinMaxValueFactory();
    }

    private void showList() {
        if (list != null && !list.getItems().isEmpty())
            list.getItems().clear();
        final ObservableList<CheckBox> variables = FXCollections.observableArrayList();
        for (Series<Number, Number> serie : lineChart.getData()) {
            CheckBox ck = new CheckBox(serie.getName());
            ck.setSelected(true);
            ck.selectedProperty().addListener((observable, oldValue, newValue) -> {
                ck.setSelected(!oldValue);
                changeVisibility(ck.getText());
            });
            variables.add(ck);
        }
        if (!variables.isEmpty())
            list.getItems().addAll(variables);
    }

    private void changeVisibility(String name) {
        changeSingleChartVisibility(name, lineChart);
        changeSingleChartVisibility(name, lineChartLog);
    }

    private void changeSingleChartVisibility(String name, LineChart<Number, Number> lineChart) {
        for (Series<Number, Number> s : lineChart.getData()) {
            if (s.getName().equals(name)) {
                s.getNode().setVisible(!s.getNode().isVisible());
                for (XYChart.Data<Number, Number> d : s.getData()) {
                    if (d.getNode() != null) {
                        d.getNode().setVisible(s.getNode().isVisible());
                    }
                }
            }
        }
    }

    private void setMinMaxValueFactory() {
        nameVColumn.setCellValueFactory(value -> new SimpleObjectProperty<>(value.getValue().getName()));
        minColumn.setCellValueFactory(value -> new SimpleObjectProperty<>(getMinSeries(value.getValue())));
        maxColumn.setCellValueFactory(value -> new SimpleObjectProperty<>(getMaxSeries(value.getValue())));
    }

    public Number getMinSeries(Series<Number, Number> series) {
        return series.getData().stream().mapToDouble(num -> num.getYValue().doubleValue()).min().getAsDouble();
    }

    public Number getMaxSeries(Series<Number, Number> series) {
        return series.getData().stream().mapToDouble(num -> num.getYValue().doubleValue()).max().getAsDouble();
    }
}