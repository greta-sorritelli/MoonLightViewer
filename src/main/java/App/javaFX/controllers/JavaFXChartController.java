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
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Series;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;

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
    Label attributes;

    private JavaFXMainController mainController;

    private final ChartBuilder cb = new SimpleChartBuilder();


    public void injectMainController(JavaFXMainController mainController) {
        this.mainController = mainController;
    }

    /**
     * Create a chart from a {@link TimeGraph} using a {@link ChartBuilder}
     *
     * @param timeGraph a {@link TimeGraph}
     */
    public void createDataFromGraphs(List<TimeGraph> timeGraph) {
        resetCharts();
        lineChart.getData().addAll(cb.getSeriesFromNodes(timeGraph));
        lineChartLog.getData().addAll(cb.getSeriesFromNodes(timeGraph));
        init();
    }


    /**
     * Adds a toolTip to all nodes of series.
     *
     * @param l lineChart
     */
    private void showToolTip(LineChart<Number, Number> l) {
        for (XYChart.Series<Number, Number> s : l.getData()) {
            for (XYChart.Data<Number, Number> d : s.getData()) {
                Tooltip t = new Tooltip(s.getName());
                t.setShowDelay(Duration.seconds(0));
                Tooltip.install(d.getNode(), t);
            }
        }
    }


    public void createSeriesFromStaticGraph(String line) {
        lineChart.getData().addAll(cb.getSeriesFromStaticGraph(line, cb.getListLinear(), true));
        lineChartLog.getData().addAll(cb.getSeriesFromStaticGraph(line, cb.getListLog(), false));
    }


    public void addDataToSeries(String line) {
        String[] attributes = line.split(",");
        cb.addAttributes(attributes);
        cb.addData(lineChart, attributes);
        cb.addData(lineChartLog, attributes);
    }


    private void init() {
        linearSelected();
        initLists();
        showToolTip(lineChart);
        showToolTip(lineChartLog);
    }

    public void initStatic() {
        linearSelected();
        initLists();
        addListener(lineChart);
        addListener(lineChartLog);
        showToolTip(lineChart);
        showToolTip(lineChartLog);
    }

    private void addListener(LineChart<Number, Number> chart) {
        for (Series<Number, Number> s : chart.getData()) {
            for (XYChart.Data<Number, Number> d : s.getData()) {
                d.getNode().setOnMouseClicked(event -> {
                    int id = Integer.parseInt(s.getName().substring(5));
                    Double time = d.getXValue().doubleValue();
                    String v = d.getYValue().toString();
                    for (ArrayList<String> a : cb.getAttributes())
                        if (Double.valueOf(a.get(0)).equals(time) && (a.get(id * 5 + 5).replaceAll("\\s+", "")).equals(v))
                            attributes.setText("X: " + a.get(id * 5 + 1) + ", Y: " + a.get(id * 5 + 2) + ", direction: " + a.get(id * 5 + 3) + ", speed: " + a.get(id * 5 + 4) + ", v: " + a.get(id * 5 + 5));
                });
            }
        }
    }

    @FXML
    public void clearLabel(MouseEvent event) {
        if (!event.getTarget().getClass().equals(StackPane.class))
            attributes.setText(" ");
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
        this.list.getItems().clear();
        this.variables.getItems().clear();
    }

    public void resetCharts() {
        cb.clearList();
        reset();
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
                Platform.runLater(() -> {
                    lineChart.getData().forEach(series -> {
                        if (series.getName().equals(name)) {
                            series.getNode().setVisible(!series.getNode().isVisible());
                            series.getData().forEach(data -> data.getNode().setVisible(!data.getNode().isVisible()));
                        }
                    });
                });
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