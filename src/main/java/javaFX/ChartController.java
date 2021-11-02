package javaFX;

import App.ChartUtility.ChartBuilder;
import App.ChartUtility.SimpleChartBuilder;
import App.GraphUtility.TimeGraph;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Side;
import javafx.scene.chart.Axis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart.Series;
import javafx.scene.control.*;

import java.util.ArrayList;
import java.util.List;
import java.util.OptionalDouble;

/**
 * Controller for a chart
 */
public class ChartController {


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

    public void injectMainController(MainController mainController) {
    }


//    public void readDataFromCsv() {
//        FileChooser fileChooser = new FileChooser();
//        Stage stage = (Stage) mainController.getVbox().getScene().getWindow();
//        File file = fileChooser.showOpenDialog(stage);
//        if (file != null) {
//            String path = file.getAbsolutePath();
//            List<Series<Number, Number>> linearSeries = CsvImport.getSeriesFromCsv(path);
//            List<Series<Number, Number>> logarithmicSeries = CsvImport.getSeriesFromCsv(path);
//            lineChart.getData().addAll(linearSeries);
//            lineChartLog.getData().addAll(logarithmicSeries);
//    }
//}


    /**
     * Create a chart from a {@link TimeGraph} using a {@link ChartBuilder}
     * @param timeGraph a {@link TimeGraph}
     */
    public void createDataFromGraphs(List<TimeGraph> timeGraph) {
        ChartBuilder cb = new SimpleChartBuilder();
        List<Series<Number, Number>> linearSeries = cb.getSeriesFromNodes(timeGraph);
        List<Series<Number, Number>> logarithmicSeries = cb.getSeriesFromNodes(timeGraph);
        lineChart.getData().addAll(linearSeries);
        lineChartLog.getData().addAll(logarithmicSeries);
        linearSelected();
        initLists();
    }

    private void initializeChart(LineChart<Number, Number> l, Axis<Number> x) {
        x.setLabel("Time");
        l.setLegendSide(Side.RIGHT);
    }

    /**
     * Initializes the two charts
     */
    public void initialize() {
        lineChartLog.setVisible(false);
        initializeChart(lineChart, xAxis);
        initializeChart(lineChartLog, xLAxis);
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

    /**
     * Initialize the table about series info, as min and max value
     */
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

    /**
     * Select all series and checkbox
     */
    public void selectAllSeries() {
        list.getItems().forEach(checkBox -> checkBox.setSelected(true));
    }

    /**
     * Initialize all checkbox in a list and their listener
     */
    private void showList() {
        if (list != null && !list.getItems().isEmpty())
            list.getItems().clear();
        final ObservableList<CheckBox> variables = FXCollections.observableArrayList();
        for (Series<Number, Number> series : lineChart.getData()) {
            CheckBox ck = new CheckBox(series.getName());
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

    /**
     * Change visibility of a series in all charts based on its name
     * @param name name of the series
     */
    private void changeVisibility(String name) {
        changeSingleChartVisibility(name, lineChart);
        changeSingleChartVisibility(name, lineChartLog);
    }

    /**
     * Change visibility of a series in a single chart based on its name
     * @param name name of the series
     * @param lineChart chart
     */
    private void changeSingleChartVisibility(String name, LineChart<Number, Number> lineChart) {
        lineChart.getData().forEach(series -> {
            if (series.getName().equals(name)) {
                series.getNode().setVisible(!series.getNode().isVisible());
                series.getData().forEach(data -> data.getNode().setVisible(series.getNode().isVisible()));
            }
        });
    }

    /**
     * Select only one series in all charts
     * @param seriesName name of the series
     */
    public void selectOnlyOneSeries(String seriesName) {
        list.getItems().forEach(checkBox -> checkBox.setSelected(checkBox.getText().equals(seriesName)));
    }

    /**
     * Get min and max of the value of a series and put them in the table
     */
    private void setMinMaxValueFactory() {
        nameVColumn.setCellValueFactory(value -> new SimpleObjectProperty<>(value.getValue().getName()));
        minColumn.setCellValueFactory(value -> new SimpleObjectProperty<>(getMinSeries(value.getValue())));
        maxColumn.setCellValueFactory(value -> new SimpleObjectProperty<>(getMaxSeries(value.getValue())));
    }

    /**
     * Return the min value of a series
     * @return min value
     */
    public Number getMinSeries(Series<Number, Number> series) {
        OptionalDouble d = series.getData().stream().mapToDouble(num -> num.getYValue().doubleValue()).min();
        if (d.isPresent())
            return d.getAsDouble();
        return 0;
    }

    /**
     * Return the max value of a series
     * @return max value
     */
    public Number getMaxSeries(Series<Number, Number> series) {
        OptionalDouble d = series.getData().stream().mapToDouble(num -> num.getYValue().doubleValue()).max();
        if (d.isPresent())
            return d.getAsDouble();
        return 0;
    }
}