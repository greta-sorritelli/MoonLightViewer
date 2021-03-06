package it.unicam.cs.project.moonlightviewer.javaModel.chart;

import it.unicam.cs.project.moonlightviewer.javaModel.graph.TimeGraph;
import javafx.scene.chart.XYChart;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Interface that defines a builder of charts from a graph
 *
 * @author Albanese Clarissa, Sorritelli Greta
 */
public interface ChartBuilder {

    ArrayList<XYChart.Series<Number, Number>> createSeriesForConstantChart(File file) throws IOException;

    /**
     * Create series of a chart from nodes in a graph
     * @param timeGraph a {@link TimeGraph}
     * @return a list of series created
     */
    List<XYChart.Series<Number, Number>> getSeriesFromNodes(List<TimeGraph> timeGraph);

    /**
     * Creates series of a chart from a file of a static graph
     */
    ArrayList<XYChart.Series<Number, Number>> getSeriesFromStaticGraph(String line, ArrayList<XYChart.Series<Number, Number>> list, boolean first);

    /**
     * Clears the lists of series
     */
    void clearList();

    /**
     * Adds data to the chart from an array of attributes
     *
     */
    void addLineData(List<XYChart.Series<Number, Number>> series, String[] attributes);

    ArrayList<ArrayList<String>> getAttributes();

    ArrayList<XYChart.Series<Number, Number>> getListLinear();

    ArrayList<XYChart.Series<Number, Number>> getListLog();

    void addAttributes(String[] attributes);
}
