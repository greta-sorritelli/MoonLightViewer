package App.javaFX.controllers;

import App.javaFX.axisUtility.LogarithmicAxis;
import App.javaModel.chart.ChartBuilder;
import App.javaModel.chart.SimpleChartBuilder;
import App.javaModel.graph.TimeGraph;
import App.utility.dialogUtility.DialogBuilder;
import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart.Series;
import javafx.scene.control.*;
import org.graphstream.graph.Graph;

import java.util.ArrayList;
import java.util.List;
import java.util.OptionalDouble;

/**
 * Controller for a chart
 */
public class JavaFXChartController {

    @FXML
    NumberAxis yAxis = new NumberAxis();
    @FXML
    NumberAxis xAxis = new NumberAxis();
    @FXML
    NumberAxis xLAxis = new NumberAxis();
    @FXML
    LogarithmicAxis yLAxis = new LogarithmicAxis();
    @FXML
    ListView<CheckBox> list;
    @FXML
    LineChart<Number, Number> lineChartLog = new LineChart<>(xLAxis, yLAxis);
    @FXML
    LineChart<Number, Number> lineChart = new LineChart<>(xAxis, yAxis);
    @FXML
    CategoryAxis categoryBarAxis = new CategoryAxis();
    @FXML
    NumberAxis numberBarAxis = new NumberAxis();
    @FXML
    BarChart<String, Number> barChart = new BarChart<>(categoryBarAxis, numberBarAxis);
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
    Button selectAll;
    @FXML
    Button deselectAll;

    private JavaFXMainController mainController;

    public void injectMainController(JavaFXMainController mainController) {
        this.mainController = mainController;
    }

    /**
     * Create a chart from a {@link TimeGraph} using a {@link ChartBuilder}
     *
     * @param timeGraph a {@link TimeGraph}
     */
    public void createDataFromGraphs(List<TimeGraph> timeGraph) {
        selectLineCharts();
        ChartBuilder cb = new SimpleChartBuilder();
        reset();
        lineChart.getData().addAll(cb.getSeriesFromNodes(timeGraph));
        lineChartLog.getData().addAll(cb.getSeriesFromNodes(timeGraph));
        linearSelected();
        initLists();
    }


    public void createDataFromStaticGraph(Graph staticGraph) {
        selectBarChart();
        disableLists();
        ChartBuilder cb = new SimpleChartBuilder();
        reset();
        barChart.getData().addAll(cb.barChartFromNodes(staticGraph));
    }

    private void disableLists() {
        selectAll.setDisable(true);
        deselectAll.setDisable(true);
        list.setDisable(true);
        variables.setDisable(true);
    }

    private void enableLists() {
        selectAll.setDisable(false);
        deselectAll.setDisable(false);
        list.setDisable(false);
        variables.setDisable(false);
    }

    private void selectBarChart() {
        lineChart.setVisible(false);
        lineChartLog.setVisible(false);
        linear.setVisible(false);
        logarithmic.setVisible(false);
        barChart.setVisible(true);
    }

    private void selectLineCharts() {
        lineChart.setVisible(true);
        lineChartLog.setVisible(false);
        linear.setVisible(true);
        logarithmic.setVisible(true);
        barChart.setVisible(false);
    }

    /**
     * Initializes the two charts
     */
    @FXML
    public void initialize() {
        lineChartLog.setVisible(false);
        lineChartLog.setAnimated(false);
        lineChart.setAnimated(false);
    }

    @FXML
    private void logarithmicSelected() {
        lineChart.setVisible(false);
        lineChartLog.setVisible(true);
        linear.setSelected(false);
        logarithmic.requestFocus();
        logarithmic.setSelected(true);
    }

    @FXML
    private void linearSelected() {
        lineChartLog.setVisible(false);
        lineChart.setVisible(true);
        linear.setSelected(true);
        linear.requestFocus();
        logarithmic.setSelected(false);
    }

    private void initLists() {
        enableLists();
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

    public void reset() {
        this.lineChartLog.getData().clear();
        this.lineChart.getData().clear();
        this.barChart.getData().clear();
        this.list.getItems().clear();
        this.variables.getItems().clear();
    }

    /**
     * Select all series and checkbox
     */
    @FXML
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
     *
     * @param name name of the series
     */
    private void changeVisibility(String name) {
        changeSingleChartVisibility(name, lineChart);
        changeSingleChartVisibility(name, lineChartLog);
    }

    /**
     * Change visibility of a series in a single chart based on its name
     *
     * @param name      name of the series
     * @param lineChart chart
     */
    private void changeSingleChartVisibility(String name, LineChart<Number, Number> lineChart) {
        new Thread(() -> {
            try {
                Thread.sleep(500);
                lineChart.getData().forEach(series -> Platform.runLater(() -> {
                    if (series.getName().equals(name)) {
                        series.getNode().setVisible(!series.getNode().isVisible());
                        series.getData().forEach(data -> data.getNode().setVisible(!data.getNode().isVisible()));
                    }
                }));
//                Thread.sleep(500);
            } catch (InterruptedException e) {
                DialogBuilder d = new DialogBuilder(mainController.getTheme());
                d.error(e.getMessage());
            }
        }).start();


//        new Thread(() -> {
//                for (Series<Number, Number> series : lineChart.getData()) {
//                    Platform.runLater(() -> {
//                        if (series.getName().equals(name)) {
//                            series.getNode().setVisible(!series.getNode().isVisible());
//                            series.getData().forEach(data -> data.getNode().setVisible(series.getNode().isVisible()));
//                        }
//                    });
//                }
//        }).start();
    }

    /**
     * Select only one series in all charts. The series to be displayed must be only one.
     *
     * @param seriesName name of the series
     */
    public void selectOnlyOneSeries(String seriesName) {
        list.getItems().forEach(checkBox -> checkBox.setSelected(checkBox.getText().equals(seriesName)));
    }

    /**
     * Select one series in all charts
     *
     * @param seriesName name of the series
     */
    public void selectOneSeries(String seriesName) {
        list.getItems().forEach(checkBox -> {
            if (checkBox.getText().equals("Node " + seriesName))
                checkBox.setSelected(true);
        });
    }

    /**
     * Deselect all series in all charts
     */
    @FXML
    public void deselectAllSeries() {
        list.getItems().forEach(checkBox -> checkBox.setSelected(false));
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
     *
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
     *
     * @return max value
     */
    public Number getMaxSeries(Series<Number, Number> series) {
        OptionalDouble d = series.getData().stream().mapToDouble(num -> num.getYValue().doubleValue()).max();
        if (d.isPresent())
            return d.getAsDouble();
        return 0;
    }

    @FXML
    private void deselectSeriesList() {
        list.getSelectionModel().clearSelection();
    }

    @FXML
    private void deselectSeriesTable() {
        variables.getSelectionModel().clearSelection();
    }

}