package App.javaModel.chart;

import App.javaModel.graph.TimeGraph;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Series;

import java.util.ArrayList;
import java.util.List;

/**
 * Interface for a builder of charts from a graph
 */
public interface ChartBuilder {

    /**
     * Create series of a chart from nodes in a graph
     * @param timeGraph a {@link TimeGraph}
     * @return a list of series created
     */
    List<Series<Number, Number>> getSeriesFromNodes(List<TimeGraph> timeGraph);

    /**
     * Creates series of a chart from a file of a static graph
     */
    ArrayList<Series<Number, Number>> getSeriesFromStaticGraph(String line, ArrayList<Series<Number, Number>> list, boolean first);

    /**
     * Clears the lists of series
     */
    void clearList();

    /**
     * Adds data to the chart from an array of attributes
     *
     */
    void addLineData(List<Series<Number, Number>> series, String[] attributes);


    ArrayList<ArrayList<String>> getAttributes();

    ArrayList<Series<Number, Number>> getListLinear();

    ArrayList<Series<Number, Number>> getListLog();

    void addAttributes(String[] attributes);
}
